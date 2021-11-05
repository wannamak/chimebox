package chimebox.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
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

  private void processEvent(MidiEvent event) {
    byte[] msg = event.getMessage().getMessage();
    long deltaTime = event.getTick();
    System.out.print(" @" + event.getTick() + ": ");
    if (event.getMessage() instanceof ShortMessage) {
      ShortMessage shortMessage = (ShortMessage) event.getMessage();
      switch (shortMessage.getCommand()) {
        case 0xc0: System.out.println(" [program change]"); return;
        case 0xb0: System.out.println(" [control change]"); return;
        case 0x90: System.out.printf(" [ on] %d\n", shortMessage.getData1()); return;
        case 0x80: System.out.printf(" [off] %d\n", shortMessage.getData1()); return;
      }
      System.out.printf("cmd:%02x d1:%02x d2:%02x\n", shortMessage.getCommand(),
          shortMessage.getData1(), shortMessage.getData2());
    } else if (msg.length > 3 && msg[0] == (byte) 0xff) {
      parseMetaEvent(msg);
    } else {
      System.out.printf("%d bytes: %02x", msg.length, msg[0]);
      if (msg.length > 1) {
        System.out.printf("%02x", msg[1]);
        if (msg.length > 2) {
          System.out.printf("%02x", msg[2]);
        }
      }
      System.out.println();
    }
  }

  private void parseMetaEvent(byte[] msg) {
    if (msg[1] == 0x51 && msg[2] == 0x03) {
      // Tempo change
      System.out.print(" [tempo] ");
      int tempo = (msg[3] & 0xff) << 16 | (msg[4] & 0xff) << 8 | (msg[5] & 0xff);
      for (int k = 3; k < msg.length; k++) {
        System.out.printf(" %02x", msg[k]);
      }
      System.out.printf(" usec per quarter=%d so bpm=%.1f\n", tempo, 60000000.0 / tempo);
    } else if (msg[1] == 0x59 && msg[2] == 0x02) {
      // Key signature
      System.out.printf(" [key] %d\n", msg[3]);
    } else {
      System.out.printf("%d bytes: %02x", msg.length, msg[0]);
      if (msg.length > 1) {
        System.out.printf("%02x", msg[1]);
        if (msg.length > 2) {
          System.out.printf("%02x", msg[2]);
        }
      }
      System.out.println();
    }
  }
}
