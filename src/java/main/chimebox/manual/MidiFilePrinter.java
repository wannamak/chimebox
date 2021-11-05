package chimebox.manual;

import chimebox.midi.MidiFile;
import chimebox.midi.MidiFileDatabase;
import chimebox.midi.MidiFileSelector;
import chimebox.midi.MidiPlayer;
import chimebox.midi.PlayerInterface;
import chimebox.midi.RepeatedNoteAdaptor;

// ./scripts/run.sh chimebox.manual.MidiFilePrinter 0
public class MidiFilePrinter implements PlayerInterface {
  private int trackLengthMs = 0;

  public static void main(String[] args) throws Exception {
    new MidiFilePrinter().run(Integer.parseInt(args[0]));
  }

  public void run(int fileIndex) throws Exception {
    MidiFileDatabase database = new MidiFileDatabase();
    MidiFileSelector selector = new MidiFileSelector(database);
    MidiFile file = selector.select(fileIndex);
    System.out.println("Tracks: " + file.getTrackSize());

    printTracks(file, this);
    System.out.println();
    System.out.println();
    printTracks(file, new RepeatedNoteAdaptor(this));
  }

  private void printTracks(MidiFile file, PlayerInterface playerImpl) {
    MidiPlayer engine = new MidiPlayer(file, playerImpl);
    for (int track = 1; track < file.getTrackSize(); track++) {
      trackLengthMs = 0;
      System.out.println("TRACK " + track + "----------------------");
      engine.play(track);
      System.out.printf("  total len: %dms\n", trackLengthMs);
    }
  }

  @Override
  public void sleep(long durationMillis) {
    if (durationMillis > 0) {
      System.out.printf("[%d] ", durationMillis);
      trackLengthMs += durationMillis;
    }
  }

  @Override
  public void noteOn(int midiNote) {
    System.out.print(getNote(midiNote) + " ");
  }

  @Override
  public void noteOff(int midiNote) {
    System.out.print("!" + getNote(midiNote) + " ");
  }

  private String getNote(int note) {
    int tmp = note - 21;
    int octave = (tmp / 12) + 1;
    tmp = tmp % 12;
    return switch (tmp) {
      case 0 -> "A" + octave;
      case 1 -> "A#" + octave;
      case 2 -> "B" + octave;
      case 3 -> "C" + octave;
      case 4 -> "C#" + octave;
      case 5 -> "D" + octave;
      case 6 -> "D#" + octave;
      case 7 -> "E" + octave;
      case 8 -> "F" + octave;
      case 9 -> "F#" + octave;
      case 10 -> "G" + octave;
      case 11 -> "G#" + octave;
      default -> "?" + octave;
    };
  }
}
