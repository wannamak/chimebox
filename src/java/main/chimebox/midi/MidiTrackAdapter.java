package chimebox.midi;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MidiTrackAdapter {
  private final int ticksPerBeat;
  private final int msPerQuarter;

  public MidiTrackAdapter(int usecPerQuarter, int ticksPerBeat) {
    this.ticksPerBeat = ticksPerBeat;
    this.msPerQuarter = usecPerQuarter / 1000;
  }

  static class CoalescedMidiEvent {
    ListMultimap<Integer, Integer> noteToCommand;
    CoalescedMidiEvent() {
      this.noteToCommand = ArrayListMultimap.create();
    }

    Optional<Integer> getHighestSoundingNoteOn() {
      return noteToCommand.entries().stream()
          .filter(entry -> entry.getValue() == ShortMessage.NOTE_ON)
          .map(Map.Entry::getKey)
          .max(Integer::compareTo);
    }
  }

  /**
   * Converts any Midi track to a list of Midi melody notes and durations.
   * (Takes the 'top' midi note from the track).
   */
  public List<MidiFile.MidiNote> convertTrackMelodyToMidiNotes(Track track) {
    List<MidiFile.MidiNote> result = new ArrayList<>();
    Map<Long, CoalescedMidiEvent> events = coalesceMidiEvents(track);
    List<Long> ticks = new ArrayList<>(events.keySet());
    Collections.sort(ticks);
    int soundingNote = -1;
    long startTick = -1;
    for (int tickIndex = 0; tickIndex < ticks.size(); tickIndex++) {
      long tick = ticks.get(tickIndex);
      CoalescedMidiEvent event = events.get(tick);
      // Off and on again may occur in the same tick, so process off first.
      // Ask me how I know.
      if (soundingNote != -1
          && event.noteToCommand.containsKey(soundingNote)
          && event.noteToCommand.get(soundingNote).contains(ShortMessage.NOTE_OFF)) {
        long tickThisNote = tick - startTick;
        long durationMs = tickThisNote * msPerQuarter / ticksPerBeat;
        result.add(new MidiFile.MidiNote(soundingNote, (int) durationMs));
        soundingNote = -1;
      }
      if (soundingNote == -1) {
        Optional<Integer> highestNote = event.getHighestSoundingNoteOn();
        if (highestNote.isPresent()) {
          soundingNote = highestNote.get();
          startTick = tick;
        }
      }
    }
    return result;
  }

  private Map<Long, CoalescedMidiEvent> coalesceMidiEvents(Track track) {
    Map<Long, CoalescedMidiEvent> result = new HashMap<>();
    for (int eventIndex = 0; eventIndex < track.size(); eventIndex++) {
      MidiEvent event = track.get(eventIndex);
      int command = -1;
      int note = -1;
      if (event.getMessage() instanceof ShortMessage shortMessage) {
        if (shortMessage.getCommand() == ShortMessage.NOTE_OFF ||
            (shortMessage.getCommand() == ShortMessage.NOTE_ON
                && shortMessage.getData2() == 0 /* velocity */)) {
          command = ShortMessage.NOTE_OFF;
          note = shortMessage.getData1();
        } else if (shortMessage.getCommand() == ShortMessage.NOTE_ON
            && shortMessage.getData2() > 0) {
          command = ShortMessage.NOTE_ON;
          note = shortMessage.getData1();
        }
      }
      if (command != -1) {
        long tick = event.getTick();
        if (!result.containsKey(tick)) {
          result.put(tick, new CoalescedMidiEvent());
        }
        result.get(tick).noteToCommand.put(note, command);
      }
    }
    return result;
  }
}
