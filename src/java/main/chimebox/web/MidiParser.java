package chimebox.web;

import com.google.common.base.Preconditions;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MidiParser {
  public void parseTrack(Track track) {
    for (int i = 0; i < track.size(); i++) {
      MidiEvent event = track.get(i);
      MidiMessage message = event.getMessage();
      byte[] raw = message.getMessage();
      boolean isStatus = ((raw[0] >> 7) & 1) == 1;
      Preconditions.checkState(isStatus);
      int action = (raw[0] >> 4) & 7;
      int channel = (raw[0]) & 7;
//      if (action != 0 && action != 1) {
//        continue;
//      }
      List<Integer> ints = new ArrayList<>();
      for (int j = 1; j < raw.length; j++) {
        ints.add((int) raw[j]);
      }
      if (action == 0) {
        System.out.printf("%9d off: %s\n", event.getTick(), ints);
      } else if (action == 1) {
        System.out.printf("%9d on : %s\n", event.getTick(), ints);
      }
    }
  }
}
