package chimebox.midi;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import java.util.logging.Logger;

public class MidiPlayer {
  private final Logger logger = Logger.getLogger(MidiPlayer.class.getName());

  private long lastDeltaTime;
  private final MidiFile file;
  private final PlayerInterface playerInterface;

  public MidiPlayer(MidiFile file, PlayerInterface playerInterface) {
    this.file = file;
    this.playerInterface = playerInterface;
  }

  public void play(int trackIndex) {
    lastDeltaTime = 0;
    logger.info(String.format("Playing file %s track %d.  usecPerQuarter=%d pulsePerQuarter=%d",
        file.getFile().getName(),
        trackIndex, file.getUsecPerQuarter(), file.getPulsePerQuarter()));
    int numEvents = file.getTrack(trackIndex).size();
    for (int eventIndex = 0; eventIndex < numEvents; eventIndex++) {
      boolean isLastEvent = eventIndex == numEvents - 1;
      processEvent(file.getTrack(trackIndex).get(eventIndex), isLastEvent);
    }
    logger.info(String.format("Playing track %d completed.", trackIndex));
  }

  private void processEvent(MidiEvent event, boolean isLastEvent) {
    long deltaTime = event.getTick();
    long period = deltaTime - lastDeltaTime;
    lastDeltaTime = deltaTime;

    int msPerQuarter = file.getUsecPerQuarter() / 1000;
    long msForPeriod = period * msPerQuarter / file.getPulsePerQuarter();

    if (!isLastEvent) {
      logger.finest(String.format(" msperq=%d, so sleeping %d ms", msPerQuarter, msForPeriod));
      playerInterface.sleep(msForPeriod);
    }

    if (event.getMessage() instanceof ShortMessage) {
      ShortMessage shortMessage = (ShortMessage) event.getMessage();
      switch (shortMessage.getCommand()) {
        case ShortMessage.NOTE_ON -> playerInterface.noteOn(shortMessage.getData1());
        case ShortMessage.NOTE_OFF -> playerInterface.noteOff(shortMessage.getData1());
      }
    }
  }
}
