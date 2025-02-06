package chimebox.midi;

import chimebox.logical.Notes;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MidiNotePlayer implements MidiPlayerInterface {
  private final Logger logger = Logger.getLogger(MidiNotePlayer.class.getName());

  private final MidiNoteAdapter adaptor = new MidiNoteAdapter();
  private final Notes notes;
  private final int transposition;

  public MidiNotePlayer(Notes notes, int transposition) {
    this.notes = notes;
    this.transposition = transposition;
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
    int chimeNote = getChimeNote(midiNote);
    notes.on(chimeNote);
  }

  @Override
  public void noteOff(int midiNote) {
    int chimeNote = getChimeNote(midiNote);
    notes.off(chimeNote);
  }

  public int getChimeNote(int midiNote) {
    return adaptor.toChimesNote(midiNote) + transposition;
  }
}
