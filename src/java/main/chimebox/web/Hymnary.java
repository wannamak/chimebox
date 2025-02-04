package chimebox.web;

import chimebox.Chimebox;
import com.google.common.base.CharMatcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Hymnary {
  private final Logger logger = Logger.getLogger(Hymnary.class.getName());
  private final SecureRandom random = new SecureRandom();
  private static final String TUNE_LIST_URL = "https://hymnary.org/browse/popular/tunes";
  private static final String BASE_URL = "https://hymnary.org";
  private static final String USER_AGENT =
      "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36";
  private final Map<String, String> urlToName = new HashMap<>();
  private final String saveDirectory;

  public static void main(String[] args) throws IOException, SQLException {
    new Hymnary(args).download();
  }

  public Hymnary(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Specify the save directory as the first argument");
      System.exit(-1);
    }
    saveDirectory = args[0];
    logger.info("Saving to " + saveDirectory);
    initialize();
  }

  private void initialize() throws IOException {
    Document doc = Jsoup
        .connect(TUNE_LIST_URL)
        .userAgent(USER_AGENT)
        .get();
    Elements anchors = doc.select("div.popular_two_columns ul li a");
    for (Element anchor : anchors) {
      String url = anchor.attr("href");
      String tuneName = anchor.text();
      urlToName.put(url, tuneName);
    }
  }

  private void download() throws SQLException, IOException {
    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:hymnary.db");
         Statement statement = connection.createStatement()) {
      statement.executeUpdate("create table midifile (" +
          "id integer primary key," +
          "tune_name string," +
          "url string," +
          "file_name string" +
          ")");
      for (String tuneUrl : urlToName.keySet()) {
       List<String> candidateUrls = getMidiFileCandidateUrls(tuneUrl);
       for (String candidateUrl : candidateUrls) {
         String absoluteUrl = BASE_URL + candidateUrl;
         String tuneName = urlToName.get(tuneUrl);
         String fileName = sanitizeTuneName(tuneName);
         try {
           downloadFile(absoluteUrl, saveDirectory + "/" + fileName);
           PreparedStatement preparedStatement =
               connection.prepareStatement("INSERT INTO midifile " +
                   "(id, tune_name, url, file_name) VALUES (?, ?, ?, ?)");
           preparedStatement.setString(2, tuneName);
           preparedStatement.setString(3, candidateUrl);
           preparedStatement.setString(4, fileName);
           preparedStatement.executeUpdate();
           break;
         } catch (IOException ioe) {
           logger.info("Unable to download " + absoluteUrl);
         }
       }
      }
    }
  }

  private String sanitizeTuneName(String str) {
    CharMatcher matcher = CharMatcher.javaLetter();
    str = str.toLowerCase();
    return matcher.retainFrom(str);
  }

  private List<String> getMidiFileCandidateUrls(String tuneUrl) throws IOException {
    Document doc = Jsoup
        .connect(BASE_URL + tuneUrl)
        .userAgent(USER_AGENT)
        .get();
    Elements medias = doc.select("div#authority_media_audio");
    if (medias.isEmpty()) {
      logger.info("No media");
      return List.of();
    }
    Element media = medias.first();
    Elements anchors = media.select("ul li a");
    List<String> result = new ArrayList<>();
    for (Element anchor : anchors) {
      if (anchor.text().contains("MIDI")) {
        String candidate = anchor.attr("href");
        if (candidate != null) {
          result.add(candidate);
        }
      }
    }
    return result;
  }

  private void downloadFile(String url, String savePath) throws IOException {
    try (InputStream in = new URL(url).openStream();
         FileOutputStream out = new FileOutputStream(savePath)) {
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
      }
    }
  }
}
