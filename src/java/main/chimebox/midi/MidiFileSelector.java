package chimebox.midi;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

public class MidiFileSelector {
  private final Logger logger = Logger.getLogger(MidiFileSelector.class.getName());

  private final Random random = new Random();
  private final MidiReader reader = new MidiReader();
  private final MidiFileDatabase database;

  public MidiFileSelector(MidiFileDatabase database) {
    this.database = database;
  }

  public int getRandomInt(int maxPlusOne) {
    return random.nextInt(maxPlusOne);
  }

  public MidiFile select() throws IOException, InvalidMidiDataException {
    int index = random.nextInt(database.getFileListSize());
    File chimeFile = database.getFile(index);
    logger.finer("Selected " + chimeFile.getAbsolutePath());
    return reader.read(chimeFile);
  }

  public MidiFile select(int fileIndex) throws Exception {
    File chimeFile = database.getFile(fileIndex);
    if (!chimeFile.exists()) {
      logger.warning("File not found: " + chimeFile.getAbsolutePath());
      return null;
    }
    return reader.read(chimeFile);
  }
}
