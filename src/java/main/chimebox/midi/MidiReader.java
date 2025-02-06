package chimebox.midi;

import com.google.common.base.Preconditions;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class MidiReader {
  private final Logger logger = Logger.getLogger(MidiReader.class.getName());

  // 120 bpm
  private static final int DEFAULT_USEC_PER_QUARTER = 500000;

  public MidiFile readDatabaseFile(File file) throws IOException, InvalidMidiDataException {
    // Typically, midi track 0 only has tempo.
    Sequence sequence = MidiSystem.getSequence(file);
    Preconditions.checkState(sequence.getDivisionType() == Sequence.PPQ);
    return new ChimeTrackMidiFile(file, getUsecPerQuarter(sequence), sequence.getResolution(), sequence.getTracks());
  }

  public MidiFile readMidiFile(File file) throws InvalidMidiDataException, IOException {
    Sequence sequence = MidiSystem.getSequence(file);
    return new TraditionalMidiFile(file, getUsecPerQuarter(sequence), sequence.getResolution(), sequence.getTracks());
  }

  private int getUsecPerQuarter(Sequence sequence) {
    for (int midiEventIndex = 0; midiEventIndex < sequence.getTracks()[0].size(); midiEventIndex++) {
      MidiEvent midiEvent = sequence.getTracks()[0].get(midiEventIndex);
      MidiMessage message = midiEvent.getMessage();
      if (message instanceof MetaMessage metaMessage) {
        byte[] data = metaMessage.getData();
        switch (metaMessage.getType()) {
          case 0x51: // Tempo
            int usecPerQuarter = (data[0] & 0xff) << 16 | (data[1] & 0xff) << 8 | (data[2] & 0xff);
            logger.info(String.format("Using track 0 tempo of %d usec/quarter", usecPerQuarter));
            return usecPerQuarter;
        }
      }
    }
    return DEFAULT_USEC_PER_QUARTER;
  }
}
