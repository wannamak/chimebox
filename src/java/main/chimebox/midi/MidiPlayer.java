package chimebox.midi;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import java.util.logging.Logger;

public class MidiPlayer {
  private final Logger logger = Logger.getLogger(MidiPlayer.class.getName());

  private static final int SILENCE_BETWEEN_CHIMES_MS = 1000;

  private int numNotes;
  private long lastDeltaTime;
  private final MidiFile file;
  private final PlayerInterface playerInterface;

  public MidiPlayer(MidiFile file, PlayerInterface playerInterface) {
    this.file = file;
    this.playerInterface = playerInterface;
  }

  public void play(int trackIndex) {
    play(trackIndex, false);
  }

  public void play(int trackIndex, boolean singleNote) {
    lastDeltaTime = 0;
    numNotes = 0;
    logger.info(String.format("Playing track %d.  usecPerQuarter=%d pulsePerQuarter=%d",
        trackIndex, file.getUsecPerQuarter(), file.getPulsePerQuarter()));
    for (int eventIndex = 0; eventIndex < file.getTrack(trackIndex).size(); eventIndex++) {
      processEvent(file.getTrack(trackIndex).get(eventIndex), singleNote);
    }
    logger.info(String.format("Playing track %d completed.", trackIndex));
  }

  private void processEvent(MidiEvent event, boolean singleNote) {
    long deltaTime = event.getTick();
    long period = deltaTime - lastDeltaTime;
    lastDeltaTime = deltaTime;

    int msPerQuarter = file.getUsecPerQuarter() / 1000;
    long msForPeriod = period * msPerQuarter / file.getPulsePerQuarter();

    if (numNotes == 0 || !singleNote) {
      logger.finest(String.format(" msperq=%d, so sleeping %d ms", msPerQuarter, msForPeriod));
      playerInterface.sleep(msForPeriod);
    }

    if (event.getMessage() instanceof ShortMessage) {
      ShortMessage shortMessage = (ShortMessage) event.getMessage();
      switch (shortMessage.getCommand()) {
        case ShortMessage.NOTE_ON -> playerInterface.noteOn(shortMessage.getData1());
        case ShortMessage.NOTE_OFF -> {
          numNotes++;
          playerInterface.noteOff(shortMessage.getData1());
        }
      }
    }
  }
}
