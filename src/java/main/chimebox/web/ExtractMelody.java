package chimebox.web;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

public class ExtractMelody {
  private final Logger logger = Logger.getLogger(ExtractMelody.class.getName());

  private final String saveDirectory;

  public static void main(String[] args) throws SQLException, IOException, InvalidMidiDataException {
    new ExtractMelody(args).extract();
  }

  public ExtractMelody(String[] args) {
    if (args.length != 1) {
      System.err.println("Specify the save directory as the first argument");
      System.exit(-1);
    }
    saveDirectory = args[0];
  }

  public void extract() throws SQLException, IOException, InvalidMidiDataException {
    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:hymnary.db");
         Statement statement = connection.createStatement()) {
      ResultSet rs = statement.executeQuery("select tune_name, url, file_name from midifile where file_name = 'genevan'");
      while (rs.next()) {
        String tuneName = rs.getString(1);
        String url = rs.getString(2);
        String fileName = rs.getString(3);
        File file = new File(saveDirectory + "/" + fileName + ".mid");
        logger.info("Reading " + tuneName + " from " + fileName);
        Sequence sequence = MidiSystem.getSequence(file);
        Track[] tracks = sequence.getTracks();
        for (int i = 0; i < tracks.length; i++) {
          System.out.println("-----> track " + i);
          new MidiParser().parseTrack(tracks[i]);
        }
      }
    }
  }
}
