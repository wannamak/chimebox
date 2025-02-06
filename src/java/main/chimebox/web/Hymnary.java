package chimebox.web;

import chimebox.Chimebox;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
      statement.executeUpdate("create table if not exists midifile (" +
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
           String fileFullPath = saveDirectory + "/" + fileName + ".mid";
           File localMidiFile = new File(fileFullPath);
           boolean corruptExistingFile = false;
           if (localMidiFile.exists()) {
             byte[] content = Files.readAllBytes(localMidiFile.toPath());
             if (isMidiFile(content)) {
               logger.info("Already have " + tuneName + ", skipping.");
               break;
             }
             logger.info(fileFullPath + " appears corrupt, redownloading");
             corruptExistingFile = true;
           }
           byte[] content = downloadFile(absoluteUrl);
           if (!isMidiFile(content)) {
             content = submitForm(absoluteUrl, content);
           }
           Files.write(localMidiFile.toPath(), content);
           if (!corruptExistingFile) {
             PreparedStatement preparedStatement =
                 connection.prepareStatement("INSERT INTO midifile " +
                     "(id, tune_name, url, file_name) VALUES (?, ?, ?, ?)");
             preparedStatement.setString(2, tuneName);
             preparedStatement.setString(3, candidateUrl);
             preparedStatement.setString(4, fileName);
             preparedStatement.executeUpdate();
           }
           break;
         } catch (IOException ioe) {
           logger.info("Unable to download " + absoluteUrl);
         }
       }
      }
    }
  }

  private byte[] submitForm(String url, byte[] content) throws IOException {
    Document doc = Jsoup.parse(new String(content));
    Map<String, String> dataParameters = new TreeMap<>();
    for (Element input : doc.select("input")) {
      if (input.attr("type").equals("hidden")) {
        dataParameters.put(input.attr("name"), input.attr("value"));
      }
    }
    byte[] newContent = Jsoup
        .connect(url)
        .data(dataParameters)
        .userAgent(USER_AGENT)
        .method(org.jsoup.Connection.Method.POST)
        .execute()
        .bodyAsBytes();
    Preconditions.checkState(isMidiFile(content));
    return newContent;
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

  private byte[] downloadFile(String url) throws IOException {
    try (InputStream in = new URL(url).openStream();
         ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
      }
      return out.toByteArray();
    }
  }

  private boolean isMidiFile(byte[] content) {
    return content[0] == 0x4d && content[1] == 0x54
        && content[2] == 0x68 && content[3] == 0x64;
  }
}
