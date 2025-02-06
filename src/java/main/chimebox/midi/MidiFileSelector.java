package chimebox.midi;

import chimebox.Proto;

import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import com.google.common.collect.ImmutableList;

import static chimebox.midi.MidiFileDatabase.*;

public class MidiFileSelector {
  private final Logger logger = Logger.getLogger(MidiFileSelector.class.getName());

  private final Random random = new Random();
  private final MidiReader reader = new MidiReader();
  private final MidiFileDatabase database;
  private final Proto.Config config;

  private final List<Integer> ordinaryIndexes = ImmutableList.of(
      WESTMINSTER,
      WHITTINGTON,
      SOISSONS,
      ST_MICHAELS,
      BEECH_SPRING
  );

  public MidiFileSelector(MidiFileDatabase database, Proto.Config config) {
    this.database = database;
    this.config = config;
  }

  public int getRandomInt(int maxPlusOne) {
    return random.nextInt(maxPlusOne);
  }

  private Proto.SpecialDay isSpecialDay(LocalDate today) {
    for (Proto.SpecialDay specialDay : config.getSpecialDayList()) {
      if (today.getMonth().getValue() == specialDay.getLocalDate().getMonth()
          && today.getDayOfMonth() == specialDay.getLocalDate().getDayOfMonth()) {
        return specialDay;
      }
    }
    return null;
  }

  public boolean isSpecialDay() {
    return isSpecialDay(LocalDate.now()) != null;
  }

  public MidiFile selectDatabaseFile() throws IOException, InvalidMidiDataException {
    LocalDate today = LocalDate.now();
    File chimeFile;
    Proto.SpecialDay specialDay = isSpecialDay(today);
    if (specialDay != null) {
      chimeFile = database.getFile(specialDay.getMidiFileDatabaseId());
    } else {
      int index = random.nextInt(ordinaryIndexes.size());
      chimeFile = database.getFile(ordinaryIndexes.get(index));
    }
    logger.finer("Selected " + chimeFile.getAbsolutePath());
    return reader.readDatabaseFile(chimeFile);
  }

  public MidiFile selectDatabaseFile(int fileIndex) throws Exception {
    File chimeFile = database.getFile(fileIndex);
    if (!chimeFile.exists()) {
      logger.warning("File not found: " + chimeFile.getAbsolutePath());
      return null;
    }
    return reader.readDatabaseFile(chimeFile);
  }
}
