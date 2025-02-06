package chimebox.midi;

import javax.sound.midi.Track;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Originally we used tracks for the different hourly chimes.
 * Now we use traditional midi files and automagically extract phrases (different impl).
 */
public class ChimeTrackMidiFile implements MidiFile {
  private final File file;
  private final int usecPerQuarter;
  private final int ticksPerBeat;
  private final Track[] tracks;
  private final Map<ChimePhrase, List<MidiNote>> phrases;

  private static final Map<ChimePhrase, Integer> CHIME_PHRASE_TO_TRACK_INDEX = Map.of(
      ChimePhrase.QUARTER, 1,
      ChimePhrase.HALF, 2,
      ChimePhrase.THREE_QUARTERS, 3,
      ChimePhrase.HOUR, 4,
      ChimePhrase.CHIME, 5);

  public ChimeTrackMidiFile(File file, int usecPerQuarter, int ticksPerBeat, Track[] tracks) {
    this.file = file;
    this.usecPerQuarter = usecPerQuarter;
    this.ticksPerBeat = ticksPerBeat;
    this.tracks = tracks;
    MidiTrackAdapter adapter = new MidiTrackAdapter(usecPerQuarter, ticksPerBeat);
    this.phrases = new HashMap<>();
    for (ChimePhrase chimePhrase : ChimePhrase.values()) {
      int trackIndex = CHIME_PHRASE_TO_TRACK_INDEX.get(chimePhrase);
      if (tracks.length > trackIndex) {
        phrases.put(chimePhrase, adapter.convertTrackMelodyToMidiNotes(tracks[trackIndex]));
      }
    }
  }

  @Override
  public File getFile() {
    return file;
  }

  @Override
  public int getTrackSize() {
    return tracks.length;
  }

  @Override
  public Track getTrack(int trackIndex) {
    return tracks[trackIndex];
  }

  @Override
  public List<MidiNote> getMusicalPhrase(ChimePhrase chimePhrase) {
    return phrases.get(chimePhrase);
  }

  @Override
  public int hashCode() {
    return file.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ChimeTrackMidiFile)) {
      return false;
    }
    return ((MidiFile) o).getFile().equals(file);
  }

  @Override
  public String toString() {
    return file.toString();
  }
}
