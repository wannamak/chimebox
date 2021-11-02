package chimebox.midi;

import javax.sound.midi.Track;

public class MidiFile {
  private final int usecPerQuarter;
  private final int pulsePerQuarter;
  private final Track[] tracks;

  public static final int QUARTER_TRACK = 1;
  public static final int HALF_TRACK = 2;
  public static final int THREE_QUARTERS_TRACK = 3;
  public static final int HOUR_TRACK = 4;
  public static final int CHIME_TRACK = 5;

  public MidiFile(int usecPerQuarter, int pulsePerQuarter, Track[] tracks) {
    this.usecPerQuarter = usecPerQuarter;
    this.pulsePerQuarter = pulsePerQuarter;
    this.tracks = tracks;
  }

  public int getUsecPerQuarter() {
    return usecPerQuarter;
  }

  public int getPulsePerQuarter() {
    return pulsePerQuarter;
  }

  public Track getTrack(int i) {
    return tracks[i];
  }

  public int getTrackSize() {
    return tracks.length;
  }
}
