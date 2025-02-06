package chimebox.manual;

import chimebox.Proto;
import chimebox.logical.ClochesStop;
import chimebox.logical.HourlyChimeSwitch;
import chimebox.logical.Notes;
import chimebox.logical.Power;
import chimebox.logical.RaspberryRelays;
import chimebox.logical.Relays;
import chimebox.logical.Volume;
import chimebox.midi.ChimePhrase;
import chimebox.midi.ChimeTrackMidiFile;
import chimebox.midi.MidiFile;
import chimebox.midi.MidiFileDatabase;
import chimebox.midi.MidiFileSelector;
import chimebox.midi.MidiNotePlayer;
import chimebox.midi.MidiPlayer;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

// ./scripts/run.sh chimebox.manual.ManualMidiPlayer 0 0 0
public class ManualMidiPlayer {
  private final Logger logger = Logger.getLogger(ManualMidiPlayer.class.getName());

  private final ClochesStop clochesStop;
  private final Power power;
  private final Volume volume;
  private final Notes notes;
  private final HourlyChimeSwitch hourlyChimeSwitch;
  private final MidiFileDatabase database;
  private final MidiFileSelector fileSelector;

  public static void main(String args[]) throws Exception {
    if (args.length != 3) {
      System.err.println("args file_index chime_phrase transposition");
      System.exit(-1);
    }
    System.loadLibrary("chimebox");
    new ManualMidiPlayer().run(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
  }

  public ManualMidiPlayer() throws IOException {
    this.database = new MidiFileDatabase();
    Relays relays = new RaspberryRelays(); // new TestingRelays();
    relays.initialize();
    this.power = new Power(relays);
    this.volume = new Volume(relays);
    this.clochesStop = new ClochesStop(power, volume);
    this.notes = new Notes(relays);
    this.hourlyChimeSwitch = new HourlyChimeSwitch();
    this.fileSelector = new MidiFileSelector(database, Proto.Config.getDefaultInstance());
  }

  public void run(int fileIndex, int chimePhraseIndex, int transposition) throws Exception {
    MidiFile midiFile = fileSelector.selectDatabaseFile(fileIndex);
    List<Integer> possibleTranspositions = database.getPossibleTranspositions(midiFile.getFile());
    logger.info("Possible transpositions: " + possibleTranspositions);
    MidiPlayer tunePlayer = new MidiPlayer(midiFile,
        new MidiNotePlayer(notes, transposition));
    logger.info(String.format(
        "Playing file index %d phrase %d transposition %d\n", fileIndex, chimePhraseIndex, transposition));
    power.on();
    volume.setForte();
    tunePlayer.play(ChimePhrase.values()[chimePhraseIndex]);
    power.off();
    logger.info("Play complete.");
  }
}
