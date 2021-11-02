package chimebox.midi;

public interface PlayerInterface {
  void sleep(long durationMillis);

  void noteOn(int midiNote);

  void noteOff(int midiNote);
}
