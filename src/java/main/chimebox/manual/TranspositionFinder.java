package chimebox.manual;

import chimebox.Proto;
import chimebox.midi.MidiFile;
import chimebox.midi.MidiFileDatabase;
import chimebox.midi.MidiFileSelector;
import chimebox.midi.MidiNoteAdaptor;
import chimebox.midi.MidiPlayer;
import chimebox.midi.PlayerInterface;
import com.google.common.collect.ImmutableSet;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

// ./scripts/run.sh chimebox.manual.TranspositionFinder 0
public class TranspositionFinder implements PlayerInterface {
  private final Logger logger = Logger.getLogger(TranspositionFinder.class.getName());

  public static void main(String[] args) throws Exception {
    new TranspositionFinder().run(Integer.parseInt(args[0]));
  }

  private final Set<Integer> BAD_CHIME_NOTES = ImmutableSet.of(1, 2, 9, 21);
  private final Map<Integer, Integer> transpositionToBadNotes = new TreeMap<>();
  private final MidiNoteAdaptor adaptor = new MidiNoteAdaptor();
  private int transpositionBadNotes;
  private int trackBadNotes;
  private int currentTransposition;
  private boolean outOutBoundNote;

  public void run(int selectorFileIndex) throws Exception {
    MidiFileDatabase database = new MidiFileDatabase();
    MidiFileSelector selector = new MidiFileSelector(database, Proto.Config.getDefaultInstance());
    MidiFile file = selector.select(selectorFileIndex);

    MidiPlayer engine = new MidiPlayer(file, this);

    currentTransposition = 0;
    while (transpose(engine)) {
      transpositionToBadNotes.put(currentTransposition, transpositionBadNotes);
      currentTransposition--;
    }
    currentTransposition = 1;
    while (transpose(engine)) {
      transpositionToBadNotes.put(currentTransposition, transpositionBadNotes);
      currentTransposition++;
    }

    System.out.println(transpositionToBadNotes);
    transpositionToBadNotes.entrySet().removeIf(entry -> entry.getValue() != 0);
    System.out.println(transpositionToBadNotes);
  }

  public boolean transpose(MidiPlayer engine) {
    transpositionBadNotes = 0;
    logger.info("Transposition " + currentTransposition);
    // Don't include chime track as all the notes work on the chime volume level.
    for (int track = 0; track < 4; track++) {
      trackBadNotes = 0;
      outOutBoundNote = false;
      engine.play(track);
      if (outOutBoundNote) {
        return false;
      }
      transpositionBadNotes = Math.max(trackBadNotes, transpositionBadNotes);
    }
    return true;
  }

  @Override
  public void sleep(long durationMillis) {
  }

  @Override
  public void noteOn(int midiNote) {
    int chimeNote = adaptor.toChimesNote(midiNote) + currentTransposition;
    if (chimeNote < 1 || chimeNote > 21) {
      outOutBoundNote = true;
      return;
    }
    if (BAD_CHIME_NOTES.contains(chimeNote)) {
      trackBadNotes++;
    }
  }

  @Override
  public void noteOff(int midiNote) {

  }
}
