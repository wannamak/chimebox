package chimebox.logical;

import java.util.Objects;

public class Volume {
  private static final int PIANO_VOLUME_RELAY = 1;
  private static final int FORTE_VOLUME_RELAY = 2;

  private final Relay pianoVolumeRelay;
  private final Relay forteVolumeRelay;

  public Volume(Relays relays) {
    this.pianoVolumeRelay = relays.get(PIANO_VOLUME_RELAY);
    this.forteVolumeRelay = relays.get(FORTE_VOLUME_RELAY);
  }

  public synchronized void setDefault() {
    forteVolumeRelay.open();
    pianoVolumeRelay.open();
  }

  public synchronized void setPiano() {
    forteVolumeRelay.open();
    pianoVolumeRelay.close();
  }

  public synchronized void setForte() {
    pianoVolumeRelay.open();
    forteVolumeRelay.close();
  }

  @Override
  public String toString() {
    return "Volume using " + pianoVolumeRelay.toString() + " and " + forteVolumeRelay.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(pianoVolumeRelay, forteVolumeRelay);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Volume)) {
      return false;
    }
    Volume that = (Volume) o;
    return Objects.equals(this.pianoVolumeRelay, that.pianoVolumeRelay)
        && Objects.equals(this.forteVolumeRelay, that.forteVolumeRelay);
  }
}
