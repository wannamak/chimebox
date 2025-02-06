package chimebox.midi;

import java.util.List;
import java.util.logging.Logger;

public class MidiPlayer {
  private final Logger logger = Logger.getLogger(MidiPlayer.class.getName());

  private final MidiFile file;
  private final MidiPlayerInterface playerInterface;

  public MidiPlayer(MidiFile file, MidiPlayerInterface playerInterface) {
    this.file = file;
    this.playerInterface = playerInterface;
  }

  public void play(ChimePhrase chimePhrase) {
    List<MidiFile.MidiNote> notes = file.getMusicalPhrase(chimePhrase);
    for (int noteIndex = 0; noteIndex < notes.size(); noteIndex++) {
      boolean isLastNote = noteIndex == notes.size() - 1;
      processNote(
          notes.get(noteIndex),
          isLastNote ? null : notes.get(noteIndex + 1));
    }
  }

  private void processNote(
      MidiFile.MidiNote note,
      MidiFile.MidiNote nextNote) {
    int durationMs = note.durationMs;
    boolean isRepeatedNote = false;

    if (nextNote != null && note != null && nextNote.note == note.note) {
      // This note will be repeated, so give it half value (and sleep for half after off)
      durationMs /= 2;
      isRepeatedNote = true;
    }

    if (note != null) {
      playerInterface.noteOn(note.note);
      playerInterface.sleep(durationMs);
      playerInterface.noteOff(note.note);
      if (isRepeatedNote) {
        playerInterface.sleep(durationMs);
      }
    }
  }
}
