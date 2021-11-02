package chimebox.midi;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MidiFileSelector {
  private final Logger logger = Logger.getLogger(MidiFileSelector.class.getName());

  private final Random random = new Random();
  private final MidiReader reader = new MidiReader();

  List<File> chimeFiles = new ArrayList<>();

  public MidiFileSelector() {
    chimeFiles.add(new File("./music/westminster.mid"));
    chimeFiles.add(new File("./music/whittington.mid"));
    chimeFiles.add(new File("./music/soissons.mid"));
    chimeFiles.add(new File("./music/st-michaels.mid"));
  }

  public MidiFile select() {
    while (!chimeFiles.isEmpty()) {
      int index = random.nextInt(chimeFiles.size());
      File chimeFile = chimeFiles.get(index);
      if (!chimeFile.exists()) {
        logger.warning("File not found: " + chimeFile.getAbsolutePath());
        continue;
      }
      try {
        logger.info("Selected " + chimeFile.getAbsolutePath());
        return reader.read(chimeFile);
      } catch (IOException | InvalidMidiDataException e) {
        logger.log(Level.WARNING, "Error reading " + chimeFile.getAbsolutePath(), e);
      }
    }
    logger.warning("No files remaining");
    return null;
  }

  public MidiFile select(int index) throws Exception {
    File chimeFile = chimeFiles.get(index);
    if (!chimeFile.exists()) {
      logger.warning("File not found: " + chimeFile.getAbsolutePath());
      return null;
    }
    return reader.read(chimeFile);
  }
}
