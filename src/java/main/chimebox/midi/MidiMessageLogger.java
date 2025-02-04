package chimebox.midi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.sound.midi.ShortMessage;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

@SuppressWarnings("SpellCheckingInspection")
class MidiMessageLogger {

  private final static int LISTEN_CHANNEL = 0;

  private final Logger logger = Logger.getLogger(MidiReceiver.class.getName());

  private static final Joiner COMMA_JOINER = Joiner.on(',');
  private static final Joiner NEWLINE_JOINER = Joiner.on('\n');
  
  private final List<Integer> onNoteQueue = new ArrayList<>();
  private final List<Integer> offNoteQueue = new ArrayList<>();

  MidiMessageLogger() {
    consumerThread.start();
  }

  private final Thread consumerThread = new Thread() {
    @Override
    public void run() {
      while (true) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          logger.info("sleep exception");
        }
        //TimeUnit.SECONDS.sleep(1)
        List<String> friendly = new ArrayList<>();
        synchronized (this) {
          if (!onNoteQueue.isEmpty()) {
            friendly.add("ON : " + humanize(onNoteQueue));
            onNoteQueue.clear();
          }
          if (!offNoteQueue.isEmpty()) {
            friendly.add("OFF: " + humanize(offNoteQueue));
            offNoteQueue.clear();
          }
        }
        if (!friendly.isEmpty()) {
          logger.info("\n" + NEWLINE_JOINER.join(friendly));
        }
      }
    }
  };

  private String humanize(List<Integer> notes) {
    Map<String, List<String>> result = new HashMap<>();
    for (int note : notes) {
      if (METZ.containsKey(note)) {
        DivisionStop divisionStop = METZ.get(note);
        if (!result.containsKey(divisionStop.division)) {
          result.put(divisionStop.division, new ArrayList<>());
        }
        List<String> stopList = result.get(divisionStop.division);
        stopList.add(divisionStop.stop);
      }
    }
    List<String> outputRows = new ArrayList<>();
    for (String division : result.keySet()) {
      outputRows.add(division + ": " + COMMA_JOINER.join(result.get(division)));
    }
    return NEWLINE_JOINER.join(outputRows);
  }

  static class DivisionStop {
    DivisionStop(String division, String stop) {
      this.division = division;
      this.stop = stop;
    }
    public final String division;
    public final String stop;
  }
  
  private final Map<Integer, DivisionStop> METZ = ImmutableMap.<Integer, DivisionStop>builder()
      .put(0, new DivisionStop("Pedal", "32 Principal"))
      .put(1, new DivisionStop("Pedal", "16 Soubasse"))
      .put(2, new DivisionStop("Pedal", "16 Contre-Basse"))
      .put(3, new DivisionStop("Pedal", "8 Octave-Basse"))
      .put(4, new DivisionStop("Pedal", "4 Flute Champetre"))
      .put(5, new DivisionStop("Pedal", "32 Contra Bombarde"))
      .put(6, new DivisionStop("Pedal", "16 Bombarde"))
      .put(7, new DivisionStop("Pedal", "8 Trompette"))
      .put(8, new DivisionStop("Recit", "16 Quintaton"))
      .put(9, new DivisionStop("Recit", "8 Viole de Gambe"))
      .put(10, new DivisionStop("Recit", "8 Voix Celeste"))
      .put(11, new DivisionStop("Recit", "Cornet V"))
      .put(12, new DivisionStop("Recit", "Plein Jeu IV"))
      .put(13, new DivisionStop("Recit", "16 Bassoon"))
      .put(14, new DivisionStop("Recit", "8 Trompette"))
      .put(15, new DivisionStop("Recit", "4 Clairon Harmon"))
      .put(16, new DivisionStop("Recit", "8 Flute Traversiere"))
      .put(17, new DivisionStop("Recit", "8 Diapason"))
      .put(18, new DivisionStop("Recit", "4 Flute Octaviante"))
      .put(19, new DivisionStop("Recit", "2 Octavin"))
      .put(20, new DivisionStop("Recit", "8 Hautbois"))
      .put(21, new DivisionStop("Recit", "8 Voix Humane"))
      .put(22, new DivisionStop("Recit", "Tremolo"))
      .put(23, new DivisionStop("SAM", "GO-Ped"))
      .put(24, new DivisionStop("SAM", "Pos-Ped"))
      .put(25, new DivisionStop("SAM", "Rec-Ped"))
      .put(26, new DivisionStop("SAM", "Unison GO"))
      .put(27, new DivisionStop("SAM", "Sub GO"))
      .put(28, new DivisionStop("SAM", "Rec-GO"))
      .put(29, new DivisionStop("SAM", "SubRec-GO"))
      .put(30, new DivisionStop("SAM", "Pos-GO"))
      .put(31, new DivisionStop("SAM", "Rec-Pos"))
      .put(32, new DivisionStop("SAM", "Unison Rec"))
      .put(33, new DivisionStop("SAM", "Super Rec"))
      .put(34, new DivisionStop("SAM", "Combs"))
      .put(35, new DivisionStop("Grand-Orgue", "Cloches"))
      .put(36, new DivisionStop("Grand-Orgue", "8 Montre"))
      .put(37, new DivisionStop("Grand-Orgue", "4 Prestant"))
      .put(38, new DivisionStop("Grand-Orgue", "2 Doublette"))
      .put(39, new DivisionStop("Grand-Orgue", "8 Trompette"))
      .put(40, new DivisionStop("Grand-Orgue", "8 Gambe"))
      .put(41, new DivisionStop("Grand-Orgue", "8 Flute Harmon"))
      .put(42, new DivisionStop("Grand-Orgue", "2 2/3 Quinte"))
      .put(43, new DivisionStop("Grand-Orgue", "16 Bombarde"))
      .put(44, new DivisionStop("Grand-Orgue", "16 Bourdon"))
      .put(45, new DivisionStop("Grand-Orgue", "8 Bourdon"))
      .put(46, new DivisionStop("Grand-Orgue", "4 Flute Douce"))
      .put(47, new DivisionStop("Grand-Orgue", "Plein Jeu III"))
      .put(48, new DivisionStop("Grand-Orgue", "4 Clairon"))
      .put(49, new DivisionStop("Positif", "8 Salicional"))
      .put(50, new DivisionStop("Positif", "4 Flute Douce"))
      .put(51, new DivisionStop("Positif", "1 3/5 Tierce"))
      .put(52, new DivisionStop("Positif", "16 Bassoon"))
      .put(53, new DivisionStop("Positif", "8 Unda Maris"))
      .put(54, new DivisionStop("Positif", "8 Principal"))
      .put(55, new DivisionStop("Positif", "2 2/3 Nazard"))
      .put(56, new DivisionStop("Positif", "1 Piccolo"))
      .put(57, new DivisionStop("Positif", "8 Basson"))
      .put(58, new DivisionStop("Positif", "8 Cor de Nuit"))
      .put(59, new DivisionStop("Positif", "4 Prestant"))
      .put(60, new DivisionStop("Positif", "2 Doublette"))
      .put(61, new DivisionStop("Positif", "8 Trompette"))
      .build();

public void log(ShortMessage msg) {
    if (msg.getChannel() == LISTEN_CHANNEL) {
      if (msg.getCommand() == ShortMessage.NOTE_ON) {
        synchronized (this) {
          onNoteQueue.add(msg.getData1());  // keyNumber
        }
        logger.info("ON note " + msg.getData1());
      } else if (msg.getCommand() == ShortMessage.NOTE_OFF) {
        synchronized (this) {
          offNoteQueue.add(msg.getData1());  // keyNumber
        }
        logger.info("OFF note " + msg.getData1());
      }
    } else {
      logger.info("Wrong channel " + msg.getChannel() + ", expected " + LISTEN_CHANNEL);
    }
  }
}