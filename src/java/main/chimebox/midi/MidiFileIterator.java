package chimebox.midi;

import chimebox.hourly.PeriodicChimeRunnable;
import com.google.common.base.Preconditions;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MidiFileIterator implements Iterator<MidiFile> {
  private final Logger logger = Logger.getLogger(MidiFileIterator.class.getName());

  private final List<File> files = new ArrayList<>();
  private final SecureRandom secureRandom = new SecureRandom();
  private final MidiReader midiReader = new MidiReader();

  public MidiFileIterator(String directoryName) {
    File directory = new File(directoryName);
    Preconditions.checkState(directory.isDirectory());
    Preconditions.checkState(directory.exists());
    for (String filename : directory.list()) {
      files.add(new File(directory, filename));
    }
    Collections.shuffle(files, secureRandom);
  }

  @Override
  public boolean hasNext() {
    return !files.isEmpty();
  }

  @Override
  public MidiFile next() {
    File file = files.remove(0);
    try {
      return midiReader.readMidiFile(file);
    } catch (InvalidMidiDataException e) {
      Preconditions.checkState(file != null);
      logger.log(Level.INFO, "Error reading " + file.getAbsolutePath(), e);
    } catch (IOException e) {
      logger.log(Level.INFO, "Error reading " + file.getAbsolutePath(), e);
    }
    return !hasNext() ? null : next();
  }
}
