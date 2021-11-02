package chimebox.midi;

public class MidiNoteAdaptor {
  private static final int CLOCHES_MIDI_NOTE_OFFSET = -56;  // MIN_CLOCHES_MIDI_NOTE is note 1

  public int toChimesNote(int midiNote) {
    return midiNote + CLOCHES_MIDI_NOTE_OFFSET;
  }
}
