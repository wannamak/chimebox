package chimebox.manual;

import chimebox.midi.ChimePhrase;
import chimebox.midi.MidiFile;
import chimebox.midi.MidiPlayer;
import chimebox.midi.MidiPlayerInterface;
import chimebox.midi.MidiReader;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.io.File;
import java.io.IOException;

public class LocalMidiPlayer implements MidiPlayerInterface {
  public static void main(String[] args) throws Exception {
    new LocalMidiPlayer(args[0], args[1]).run();
  }

  private final MidiFile file;
  private final ChimePhrase chimePhrase;
  private final MidiChannel[] channels;

  private static final int VOLUME = 60;

  public LocalMidiPlayer(String filename, String chimePhraseIndexStr)
      throws InvalidMidiDataException, IOException, MidiUnavailableException {
    File inputFile = new File(filename);
    System.out.println("Reading " + inputFile.getAbsolutePath());
    this.file = new MidiReader().readMidiFile(inputFile);
    //this.file = new MidiReader().readDatabaseFile(inputFile);
    System.out.println("Loaded " + file);

    int chimePhraseIndex = Integer.parseInt(chimePhraseIndexStr);
    this.chimePhrase = ChimePhrase.values()[chimePhraseIndex];
    Synthesizer synth = MidiSystem.getSynthesizer();
    synth.open();
    channels = synth.getChannels();
  }

  public void run() {
    MidiPlayer player = new MidiPlayer(file, this);
    System.out.println("Playing " + chimePhrase);
    System.out.println(file.getMusicalPhrase(chimePhrase));
    player.play(chimePhrase);
  }

  @Override
  public void sleep(long durationMillis) {
    try {
      Thread.sleep(durationMillis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void noteOn(int midiNote) {
    channels[0].noteOn(midiNote, VOLUME);
  }

  @Override
  public void noteOff(int midiNote) {
    channels[0].noteOff(midiNote);
  }
}
