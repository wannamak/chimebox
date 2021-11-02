package chimebox.midi;

import java.util.logging.Logger;

public class DebugPlayer implements PlayerInterface {
  private final Logger logger = Logger.getLogger(DebugPlayer.class.getName());

  @Override
  public void sleep(long durationMillis) {
  }

  @Override
  public void noteOn(int midiNote) {
    System.out.print(getNote(midiNote) + " ");
  }

  @Override
  public void noteOff(int midiNote) {

  }

  private String getNote(int note) {
    int tmp = note - 21;
    tmp = tmp % 12;
    switch (tmp) {
      case 0: return "A";
      case 1: return "A#";
      case 2: return "B";
      case 3: return "C";
      case 4: return "C#";
      case 5: return "D";
      case 6: return "D#";
      case 7: return "E";
      case 8: return "F";
      case 9: return "F#";
      case 10: return "G";
      case 11: return "G#";
      default: return "?";
    }
  }
}
