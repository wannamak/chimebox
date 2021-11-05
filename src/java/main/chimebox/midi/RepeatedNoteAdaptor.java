package chimebox.midi;

import com.google.common.base.Preconditions;

/**
 * Articulates repeated notes.  Reduces repeated note duration by amount of preceding silence added.
 */
public class RepeatedNoteAdaptor implements PlayerInterface {
  private static final int MIN_DELAY_BETWEEN_REPEATED_NOTES_MS = 50;
  private static final int MIN_NOTE_LENGTH_MS = 20;

  private int repeatedNoteReductionMs = 0;
  private int silenceDurationMs = 0;
  private int lastNoteOff = -1;
  private final PlayerInterface wrapped;
  private boolean silence = true;
  private boolean playingRepeatedNote = false;

  public RepeatedNoteAdaptor(PlayerInterface wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public void sleep(long durationMillis) {
    if (playingRepeatedNote) {
      Preconditions.checkState(!silence);
      durationMillis -= Math.min(durationMillis, repeatedNoteReductionMs);
      Preconditions.checkState(durationMillis >= 0);
      durationMillis = Math.max(MIN_NOTE_LENGTH_MS, durationMillis);
    }
    wrapped.sleep(durationMillis);
    if (silence) {
      silenceDurationMs += durationMillis;
    } else if (playingRepeatedNote) {
      repeatedNoteReductionMs = 0;
    }
  }

  @Override
  public void noteOn(int midiNote) {
    if (lastNoteOff == midiNote) {
      if (silenceDurationMs < MIN_DELAY_BETWEEN_REPEATED_NOTES_MS) {
        repeatedNoteReductionMs = MIN_DELAY_BETWEEN_REPEATED_NOTES_MS - silenceDurationMs;
        wrapped.sleep(repeatedNoteReductionMs);
        playingRepeatedNote = true;
      }
    } else {
      playingRepeatedNote = false;
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
    playingRepeatedNote = false;
    repeatedNoteReductionMs = 0;
  }
}
