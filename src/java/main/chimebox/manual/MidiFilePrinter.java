package chimebox.manual;

import chimebox.Proto;
import chimebox.midi.ChimePhrase;
import chimebox.midi.MidiFile;
import chimebox.midi.MidiFileDatabase;
import chimebox.midi.MidiFileSelector;
import chimebox.midi.MidiReader;
import chimebox.midi.MidiTrackAdapter;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import java.io.File;

// ./scripts/run.sh chimebox.manual.MidiFilePrinter 0
public class MidiFilePrinter {
  public static void main(String[] args) throws Exception {
    new MidiFilePrinter().run(args[0]);
  }

  public void run(String fileOrIndex) throws Exception {
    MidiFile midiFile;
    try {
      int fileIndex = Integer.parseInt(fileOrIndex);
      System.out.println("Printing database file index " + fileIndex);
      MidiFileDatabase database = new MidiFileDatabase();
      MidiFileSelector selector = new MidiFileSelector(database, Proto.Config.getDefaultInstance());
      midiFile = selector.selectDatabaseFile(fileIndex);
    } catch (NumberFormatException nfe) {
      File file = new File(fileOrIndex);
      System.out.println("Printing midi file " + file.getAbsolutePath());
      midiFile = new MidiReader().readMidiFile(file);
    }

    System.out.println("Tracks: " + midiFile.getTrackSize());
    printTracks(midiFile);

    System.out.println("HOUR Phrase: ");
    System.out.println(midiFile.getMusicalPhrase(ChimePhrase.HOUR));
  }

  private void printTracks(MidiFile file) {
    for (int trackIndex = 1; trackIndex < file.getTrackSize(); trackIndex++) {
      System.out.println("TRACK " + trackIndex + "----------------------");
      Track track = file.getTrack(trackIndex);
      printTrack(track);
    }
  }

  private void printTrack(Track track) {
    long lastTick = 0;
    for (int eventIndex = 0; eventIndex < track.size(); eventIndex++) {
      MidiEvent event = track.get(eventIndex);
      if (event.getMessage() instanceof ShortMessage shortMessage) {
        long tick = event.getTick();
        int data = shortMessage.getData1();
        long durationTicks = tick - lastTick;
        if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
          System.out.printf("%9d ON : %s (%d) (dur:%d)\n",
              tick, getNoteString(data), data, durationTicks);
        } else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
          System.out.printf("%9d OFF: %s (%d) (dur:%d)\n",
              tick, getNoteString(data), data, durationTicks);
        } else if (shortMessage.getCommand() == ShortMessage.PROGRAM_CHANGE) {
          System.out.printf("%9d PROGRAM CHANGE: %d\n",
              tick, data);
        } else {
          System.out.printf("Message command: %d\n", shortMessage.getCommand());
        }
        if (tick != lastTick) {
          lastTick = tick;
        }
      } else if (event.getMessage() instanceof MetaMessage metaMessage) {
        System.out.printf("Meta message: %2d len_data:%d\n",
            metaMessage.getType(), metaMessage.getData().length);
      } else if (event.getMessage() instanceof SysexMessage sysexMessage) {
        System.out.printf("Sysex message: len:%d len_data:%d\n",
            sysexMessage.getMessage().length,
            sysexMessage.getData().length);
      }
    }
  }

  private String getNoteString(int note) {
    int tmp = note - 21;
    int octave = (tmp / 12) + 1;
    tmp = tmp % 12;
    return switch (tmp) {
      case 0 -> "A" + octave;
      case 1 -> "A#" + octave;
      case 2 -> "B" + octave;
      case 3 -> "C" + octave;
      case 4 -> "C#" + octave;
      case 5 -> "D" + octave;
      case 6 -> "D#" + octave;
      case 7 -> "E" + octave;
      case 8 -> "F" + octave;
      case 9 -> "F#" + octave;
      case 10 -> "G" + octave;
      case 11 -> "G#" + octave;
      default -> "?" + octave;
    };
  }
}
