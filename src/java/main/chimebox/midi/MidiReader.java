package chimebox.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class MidiReader {
  private final Logger logger = Logger.getLogger(MidiReader.class.getName());

  private static final int DEFAULT_USEC_PER_QUARTER = 500000;

  public MidiFile read(File file) throws IOException, InvalidMidiDataException {
    Sequence sequence = MidiSystem.getSequence(file);
    if (sequence.getDivisionType() == Sequence.PPQ) {
      //System.out.printf("pulse-per-quarter: resolution=%d\n", sequence.getResolution());
    } else {
      //System.out.printf("SMTPE: resolution=%d\n", sequence.getResolution());
    }
    int usecPerQuarter = DEFAULT_USEC_PER_QUARTER;
    for (int j = 0; j < sequence.getTracks()[0].size(); j++) {
      byte[] msg = sequence.getTracks()[0].get(j).getMessage().getMessage();
      if (msg.length > 3 && msg[0] == (byte) 0xff) {
        if (msg[1] == 0x51 && msg[2] == 0x03) {
          // Tempo change
          usecPerQuarter = (msg[3] & 0xff) << 16 | (msg[4] & 0xff) << 8 | (msg[5] & 0xff);
        }
      }
    }
    return new MidiFile(file, usecPerQuarter, sequence.getResolution(), sequence.getTracks());
  }
}
