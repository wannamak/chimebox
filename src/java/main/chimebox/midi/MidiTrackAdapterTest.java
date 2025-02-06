package chimebox.midi;

import org.junit.Test;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MidiTrackAdapterTest {
  @Test
  public void testConvertDatabaseMidiTrack() throws Exception {
    Sequence sequence = MidiSystem.getSequence(new File("music/westminster.mid"));
    MidiTrackAdapter adapter = new MidiTrackAdapter(375000, 1024);
    List<MidiFile.MidiNote> notes = adapter.convertTrackMelodyToMidiNotes(
        sequence.getTracks()[1]);
    assertEquals(4, notes.size());
    assertEquals(76, notes.get(0).note);
    assertEquals(381, notes.get(0).durationMs);
    assertEquals(74, notes.get(1).note);
    assertEquals(371, notes.get(1).durationMs);
    assertEquals(72, notes.get(2).note);
    assertEquals(371, notes.get(2).durationMs);
    assertEquals(67, notes.get(3).note);
    assertEquals(370, notes.get(3).durationMs);
  }

  @Test
  public void testConvertTraditionalMidiTrack() throws Exception {
    Sequence sequence = MidiSystem.getSequence(new File("hymns/genevan.mid"));
    MidiTrackAdapter adapter = new MidiTrackAdapter(500000, 192);
    List<MidiFile.MidiNote> notes = adapter.convertTrackMelodyToMidiNotes(
        sequence.getTracks()[1]);
    assertEquals(61, notes.size());
    assertEquals(65, notes.get(0).note);
    assertEquals(875, notes.get(0).durationMs);
    assertEquals(67, notes.get(1).note);
    assertEquals(416, notes.get(1).durationMs);
    assertEquals(69, notes.get(2).note);
    assertEquals(875, notes.get(2).durationMs);
    assertEquals(67, notes.get(3).note);
    assertEquals(416, notes.get(3).durationMs);
    assertEquals(64, notes.get(59).note);
    assertEquals(416, notes.get(59).durationMs);
    assertEquals(65, notes.get(60).note);
    assertEquals(1875, notes.get(60).durationMs);
  }
}
