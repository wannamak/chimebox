package chimebox;

import chimebox.hourly.ChimeSchedulerThread;
import chimebox.logical.ClochesStop;
import chimebox.logical.HourlyChimeSwitch;
import chimebox.logical.Notes;
import chimebox.logical.Power;
import chimebox.logical.Relays;
import chimebox.logical.Volume;
import chimebox.midi.MidiFileDatabase;
import chimebox.midi.MidiReceiver;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;
import java.io.IOException;
import java.util.logging.Logger;

public class Chimebox {
  private final Logger logger = Logger.getLogger(Chimebox.class.getName());
  private final String inputDeviceNameSubstring;
  private final ClochesStop clochesStop;
  private final Power power;
  private final Volume volume;
  private final Notes notes;
  private final HourlyChimeSwitch hourlyChimeSwitch;
  private final MidiFileDatabase database;

  public static void main(String[] args) throws Exception {
    System.loadLibrary("chimebox");
    new Chimebox("Uno").run();
  }

  public void listMidiDevices() {
    logger.finer("MIDI Device List:");
    for (MidiDevice.Info device : MidiSystem.getMidiDeviceInfo()) {
      logger.finer(device.getName());
    }
  }

  public Chimebox(String arg) throws IOException {
    this.inputDeviceNameSubstring = arg;
    Relays relays = new Relays();
    relays.initialize();
    this.power = new Power(relays);
    this.volume = new Volume(relays);
    this.clochesStop = new ClochesStop(power, volume);
    this.notes = new Notes(relays);
    this.hourlyChimeSwitch = new HourlyChimeSwitch();
    hourlyChimeSwitch.initialize();
    this.database = new MidiFileDatabase();
  }

  public void run() throws Exception {
    ChimeSchedulerThread scheduler = new ChimeSchedulerThread(
        database, hourlyChimeSwitch, volume, power, notes, clochesStop);
    scheduler.start();

    listMidiDevices();
    try (MidiDevice inputDevice = selectMidiDevice(inputDeviceNameSubstring)) {
      logger.info("Input MIDI device [" + inputDevice.getDeviceInfo().getName() + "]");
      inputDevice.open();

      Transmitter transmitter = inputDevice.getTransmitter();
      transmitter.setReceiver(new MidiReceiver(clochesStop, notes));

      scheduler.join();  // wait forever
    }
  }

  public MidiDevice selectMidiDevice(String substring)
      throws MidiUnavailableException {
    int colon = substring.lastIndexOf(":");
    int deviceIndexOffset = 0;
    if (colon > -1) {
      deviceIndexOffset = Integer.parseInt(substring.substring(colon + 1));
      substring = substring.substring(0, colon);
    }
    int currentIndexOffset = 0;
    for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
      if (info.getName().contains(substring)) {
        MidiDevice midiDevice = MidiSystem.getMidiDevice(info);
        if (midiDevice.getMaxTransmitters() == 0) {
          logger.finer("--> skip device " + info.getName() + " as no transmitters are allowed");
          continue;
        }
        if (currentIndexOffset == deviceIndexOffset) {
          logger.finer("--> Selected " + info);
          return midiDevice;
        } else {
          logger.finer("--> Skipped due to offset " + info);
          currentIndexOffset++;
        }
      } else {
        logger.finer("--> No substring [" + substring + "] match " + info.getName());
      }
    }
    throw new IllegalStateException("No device matching [" + substring + "]");
  }
}
