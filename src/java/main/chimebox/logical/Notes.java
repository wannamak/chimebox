package chimebox.logical;

import chimebox.Chimebox;

import java.util.logging.Logger;

/**
 * Notes maps a note index of 1 through 21 onto the proper relays.
 */
public class Notes {
  private final Logger logger = Logger.getLogger(Notes.class.getName());

  private final Relays relays;

  private static final int MIN_NOTE = 1;
  private static final int MAX_NOTE = 21;
  private static final int RELAY_INDEX_OFFSET = 2;

  public Notes(Relays relays) {
    this.relays = relays;
  }

  public void on(int noteIndex) {
    if (noteIndex < MIN_NOTE || noteIndex > MAX_NOTE) {
      logger.finest("Ignoring out of range note index " + noteIndex);
      return;
    }
    relays.get(noteIndex + RELAY_INDEX_OFFSET).close();
  }

  public void off(int noteIndex) {
    if (noteIndex < MIN_NOTE || noteIndex > MAX_NOTE) {
      logger.finest("Ignoring out of range note index " + noteIndex);
      return;
    }
    relays.get(noteIndex + RELAY_INDEX_OFFSET).open();
  }

  @Override
  public String toString() {
    return "Notes using " + relays.toString();
  }

  @Override
  public int hashCode() {
    return relays.hashCode();
  }

  @Override
  public boolean equals(Object that) {
    if (!(that instanceof Notes)) {
      return false;
    }
    return ((Notes) that).relays == relays;
  }
}
