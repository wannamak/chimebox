package chimebox.midi;

import com.google.common.base.Preconditions;

import javax.sound.midi.Track;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraditionalMidiFile implements MidiFile {
  private final File file;
  private final int usecPerQuarter;
  private final int ticksPerBeat;
  private final Track[] tracks;
  private final Map<ChimePhrase, List<MidiNote>> phrases;

  public TraditionalMidiFile(File file, int usecPerQuarter, int ticksPerBeat, Track[] tracks) {
    this.file = file;
    this.usecPerQuarter = usecPerQuarter;
    this.ticksPerBeat = ticksPerBeat;
    this.tracks = tracks;
    MidiTrackAdapter adapter = new MidiTrackAdapter(usecPerQuarter, ticksPerBeat);
    List<MidiNote> notes = adapter.convertTrackMelodyToMidiNotes(tracks[1]);
    this.phrases = new HashMap<>();
    splitNotesToPhrases(notes);
  }

  private static final int MINIMUM_NUMBER_OF_SPLITS = 2;

  private void splitNotesToPhrases(List<MidiNote> notes) {
    // Builds a map of all durations to frequency.
    Map<Integer, Integer> durationToFrequency = new HashMap<>();
    for (MidiNote note : notes) {
      if (!durationToFrequency.containsKey(note.durationMs)) {
        durationToFrequency.put(note.durationMs, 0);
      }
      durationToFrequency.put(note.durationMs,
          durationToFrequency.get(note.durationMs) + 1);
    }

    // Orders the durations.
    List<Integer> durations = new ArrayList<>(durationToFrequency.keySet());
    Collections.sort(durations);

    // Figures out how many phrases we will have if we take a duration as the
    // split point.  Chooses the first duration with a minimum number of splits.
    int numSplits = 0;
    int currentDurationThreshold = 0;
    for (int i = durations.size() - 1; i >= 0; i--) {
      currentDurationThreshold = durations.get(i);
      numSplits += durationToFrequency.get(currentDurationThreshold) + 1;
      if (numSplits >= MINIMUM_NUMBER_OF_SPLITS) {
        break;
      }
    }
    Preconditions.checkState(currentDurationThreshold > 0);

    // We just want the first and final phrase.
    phrases.put(ChimePhrase.HALF, notes.subList(0,
        indexOfDurationGreaterEqual(notes, currentDurationThreshold) + 1));
    phrases.put(ChimePhrase.HOUR, notes);
  }

  private int indexOfDurationGreaterEqual(List<MidiNote> notes, int duration) {
    for (int i = 0; i < notes.size(); i++) {
      if (notes.get(i).durationMs >= duration) {
        return i;
      }
    }
    return -1;
  }

  private int reverseIndexOfDurationGreaterEqual(List<MidiNote> notes, int duration) {
    // -3 to not count the last few notes.
    for (int i = notes.size() - 3; i >= 0; i--) {
      if (notes.get(i).durationMs >= duration) {
        return i;
      }
    }
    return -1;
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
  public String toString() {
    return String.format("%s ticks_per_beat=%d usec_per_quarter=%d",
        file.getAbsolutePath(), ticksPerBeat, usecPerQuarter);
  }
}
