package chimebox.manual;

import chimebox.midi.DebugPlayer;
import chimebox.midi.MidiFile;
import chimebox.midi.MidiFileDatabase;
import chimebox.midi.MidiFileSelector;
import chimebox.midi.MidiPlayer;

public class ManualPlayer {
  public static void main(String[] args) throws Exception {
    new ManualPlayer().run(Integer.parseInt(args[0]));
  }

  public void run(int fileIndex) throws Exception {
    MidiFileDatabase database = new MidiFileDatabase();
    MidiFileSelector selector = new MidiFileSelector(database);
    MidiFile file = selector.select(fileIndex);
    System.out.println("Tracks: " + file.getTrackSize());

    DebugPlayer playerImpl = new DebugPlayer();
    MidiPlayer engine = new MidiPlayer(file, playerImpl);

    if (true) {
      engine.play(5);
      return;
    }
    for (int track = 0; track < file.getTrackSize(); track++) {
      System.out.println("TRACK " + track + "----------------------");
      engine.play(track);
      System.out.println();
    }
  }
}
