package chimebox.midi;

import chimebox.logical.ClochesStop;
import chimebox.logical.Notes;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.HexFormat;
import java.util.logging.Logger;

public class MidiReceiver implements Receiver {
  private static final int CLOCHES_CHANNEL = 9;
  private static final int CLOCHES_STOP_MIDI_NOTE = 0;

  private static final int MIN_CLOCHES_MIDI_NOTE = 57;  // A below middle C
  private static final int MAX_CLOCHES_MIDI_NOTE = 77;

  // TODO: move to config
  private static final boolean PRINT_MESSAGES = false;

  private final ClochesStop cloches;
  private final Notes notes;
  private final MidiNoteAdapter adaptor = new MidiNoteAdapter();
  private final MidiMessageLogger messageLogger = new MidiMessageLogger();

  public MidiReceiver(ClochesStop cloches, Notes notes) {
    this.cloches = cloches;
    this.notes = notes;
  }

  private final Logger logger = Logger.getLogger(MidiReceiver.class.getName());

  /** This is actually called when a message is received.  Poor API naming. */
  @Override
  public void send(MidiMessage rawMessage, long l) {
    logger.finest("Received midi message: " + HexFormat.of().formatHex(rawMessage.getMessage()));
    if (!(rawMessage instanceof ShortMessage)) {
      logger.info("Ignoring meta or sysex message");
      return;
    }
    ShortMessage message = (ShortMessage) rawMessage;
    if (PRINT_MESSAGES) {
      messageLogger.log(message);
    }
    if (message.getChannel() != CLOCHES_CHANNEL) {
      logger.finest("Ignoring message on channel " + message.getChannel());
      return;
    }
    if (message.getCommand() == ShortMessage.NOTE_ON) {
      int keyNumber = message.getData1();

      if (keyNumber == CLOCHES_STOP_MIDI_NOTE) {
        logger.info("Cloches stop on");
        cloches.on();
        return;
      }

      if (keyNumber < MIN_CLOCHES_MIDI_NOTE || keyNumber > MAX_CLOCHES_MIDI_NOTE) {
        logger.info("Ignoring out of bounds cloches midi note " + keyNumber);
        return;
      }

      int chimesNote = adaptor.toChimesNote(keyNumber);
      notes.on(chimesNote);
    } else if (message.getCommand() == ShortMessage.NOTE_OFF) {
      int keyNumber = message.getData1();

      if (keyNumber == CLOCHES_STOP_MIDI_NOTE) {
        logger.info("Cloches stop off");
        cloches.off();
        return;
      }

      if (keyNumber < MIN_CLOCHES_MIDI_NOTE || keyNumber > MAX_CLOCHES_MIDI_NOTE) {
        logger.info("Ignoring out of bounds cloches midi note " + keyNumber);
        return;
      }

      int chimesNote = adaptor.toChimesNote(keyNumber);
      notes.off(chimesNote);
    }
  }

  @Override
  public void close() {

  }
}
