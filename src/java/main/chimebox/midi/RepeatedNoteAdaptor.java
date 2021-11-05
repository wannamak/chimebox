package chimebox.midi;

/**
 * Articulates repeated notes.  Reduces repeated note duration by amount of preceding silence added.
 */
public class RepeatedNoteAdaptor implements PlayerInterface {
  private static final int MIN_DELAY_BETWEEN_REPEATED_NOTES_MS = 150;
  private static final int MIN_NOTE_LENGTH_MS = 150;

  private int silenceDurationMs = 0;
  private int lastNoteOff = -1;
  private final PlayerInterface wrapped;
  private boolean silence = true;

  public RepeatedNoteAdaptor(PlayerInterface wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public void sleep(long durationMillis) {
    wrapped.sleep(durationMillis);
    if (silence) {
      silenceDurationMs += durationMillis;
    }
  }

  @Override
  public void noteOn(int midiNote) {
    if (lastNoteOff == midiNote) {
      if (silenceDurationMs < MIN_DELAY_BETWEEN_REPEATED_NOTES_MS) {
        int repeatedNoteReductionMs = MIN_DELAY_BETWEEN_REPEATED_NOTES_MS - silenceDurationMs;
        wrapped.sleep(repeatedNoteReductionMs);
      }
    }
    wrapped.noteOn(midiNote);
    silence = false;
    silenceDurationMs = 0;
  }

  @Override
  public void noteOff(int midiNote) {
    wrapped.noteOff(midiNote);
    lastNoteOff = midiNote;
    silence = true;
  }
}
