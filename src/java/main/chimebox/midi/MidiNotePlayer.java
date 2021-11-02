package chimebox.midi;

import chimebox.logical.Notes;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MidiNotePlayer implements PlayerInterface {
  private final Logger logger = Logger.getLogger(MidiNotePlayer.class.getName());

  private final MidiNoteAdaptor adaptor = new MidiNoteAdaptor();
  private final Notes notes;

  public MidiNotePlayer(Notes notes) {
    this.notes = notes;
  }

  @Override
  public void sleep(long durationMillis) {
    try {
      Thread.sleep(durationMillis);
    } catch (InterruptedException ioe) {
      logger.log(Level.WARNING, "", ioe);
    }
  }

  @Override
  public void noteOn(int midiNote) {
    int chimeNote = adaptor.toChimesNote(midiNote);
    notes.on(chimeNote);
  }

  @Override
  public void noteOff(int midiNote) {
    int chimeNote = adaptor.toChimesNote(midiNote);
    notes.off(chimeNote);
  }
}
