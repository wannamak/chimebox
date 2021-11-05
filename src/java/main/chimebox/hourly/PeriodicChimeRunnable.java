package chimebox.hourly;

import chimebox.logical.ClochesStop;
import chimebox.logical.HourlyChimeSwitch;
import chimebox.logical.Notes;
import chimebox.logical.Power;
import chimebox.logical.Volume;
import chimebox.midi.LowestMidiNotePlayer;
import chimebox.midi.MidiFile;
import chimebox.midi.MidiFileDatabase;
import chimebox.midi.MidiFileSelector;
import chimebox.midi.MidiNotePlayer;
import chimebox.midi.MidiPlayer;
import chimebox.midi.RepeatedNoteAdaptor;

import javax.sound.midi.InvalidMidiDataException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeriodicChimeRunnable implements Runnable {
  private final Logger logger = Logger.getLogger(PeriodicChimeRunnable.class.getName());
  private MidiFile currentFile;
  private MidiPlayer tunePlayer;
  private MidiPlayer chimePlayer;

  private static final int SILENCE_PRIOR_TO_HOUR_CHIMES_MS = 1000;
  private static final int SILENCE_BETWEEN_HOUR_CHIMES_MS = 1200;

  // TODO: move to config
  private static final LocalTime START_TIME = LocalTime.of(8, 1);
  private static final LocalTime END_TIME = LocalTime.of(20, 0);

  private final MidiFileDatabase database;
  private final MidiFileSelector midiFileSelector;
  private final HourlyChimeSwitch hourlyChimeSwitch;
  private final Volume volume;
  private final Power power;
  private final ClochesStop clochesStop;
  private final Notes notes;


  public PeriodicChimeRunnable(MidiFileDatabase database, HourlyChimeSwitch hourlyChimeSwitch,
      Power power, Volume volume,
      Notes notes, ClochesStop clochesStop) {
    this.database = database;
    this.midiFileSelector = new MidiFileSelector(database);
    this.hourlyChimeSwitch = hourlyChimeSwitch;
    this.power = power;
    this.volume = volume;
    this.clochesStop = clochesStop;
    this.notes = notes;
  }

  @Override
  public void run() {
    logger.info("PCR triggered at " + LocalTime.now());
    try {
      runInternal();
    } catch (IOException | InvalidMidiDataException e) {
      logger.log(Level.WARNING, e.getMessage(), e);
    }
  }

  private void runInternal() throws IOException, InvalidMidiDataException {
    LocalTime time = LocalTime.now();

    if (!hourlyChimeSwitch.isClosed()) {
      logger.info("Not chiming due to hourly chime switch off");
      return;
    }

    if (clochesStop.isDrawn()) {
      logger.info("Not chiming due to cloches stop being drawn");
      return;
    }

    if (time.isAfter(END_TIME)) {
      logger.info("Not chiming due to after end time");
      return;
    }

    if (time.isBefore(START_TIME)) {
      logger.info("Not chiming due to before start time");
      return;
    }

    if (currentFile == null || time.getMinute() == 15) {
      currentFile = midiFileSelector.select();
      if (currentFile == null) {
        logger.info("Not chiming due to no chime files available");
        return;
      }

      List<Integer> possibleTranspositions = database.getPossibleTranspositions(currentFile.getFile());
      int transposition = midiFileSelector.getRandomInt(possibleTranspositions.size());
      logger.info("Transposition: " + transposition);

      tunePlayer = new MidiPlayer(currentFile, new RepeatedNoteAdaptor(new MidiNotePlayer(notes, transposition)));
      chimePlayer = new MidiPlayer(currentFile, new LowestMidiNotePlayer(notes, transposition));
    }

    int track = getTrackFromMinuteOfHour(time.getMinute());
    if (track == -1) {
      logger.info("Not chiming due to unexpected minute of hour");
      return;
    }

    logger.finer("Power on");
    power.on();

    volume.setPiano();

    tunePlayer.play(track);

    if (track == MidiFile.HOUR_TRACK) {
      uncheckedThreadSleepMs(SILENCE_PRIOR_TO_HOUR_CHIMES_MS);

      int numRepeats = time.getHour();
      if (numRepeats > 12) {
        numRepeats -= 12;
      }

      volume.setForte();
      for (int i = 0; i < numRepeats; i++) {
        if (!hourlyChimeSwitch.isClosed()) {
          logger.info("Not hourly chiming due to hourly chime switch off");
          power.off();
          return;
        }
        if (i > 0) {
          uncheckedThreadSleepMs(SILENCE_BETWEEN_HOUR_CHIMES_MS);
        }
        chimePlayer.play(MidiFile.CHIME_TRACK);
      }
    }

    logger.finer("Power off");
    power.off();
  }

  private void uncheckedThreadSleepMs(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException ie) {
      logger.log(Level.WARNING, ie.getMessage(), ie);
    }
  }

  private int getTrackFromMinuteOfHour(int minuteOfHour) {
    switch (minuteOfHour) {
      case 0:
        return MidiFile.HOUR_TRACK;
      case 15:
        return MidiFile.QUARTER_TRACK;
      case 30:
        return MidiFile.HALF_TRACK;
      case 45:
        return MidiFile.THREE_QUARTERS_TRACK;
      default:
        logger.warning("Unrecognized minute of hour " + minuteOfHour);
        return -1;
    }
  }
}
