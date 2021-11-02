package chimebox.logical;

public class ClochesStop {
  private final Power power;
  private final Volume volume;

  private boolean isDrawn = false;

  public ClochesStop(Power power, Volume volume) {
    this.power = power;
    this.volume = volume;
  }

  public synchronized void on() {
    if (!isDrawn) {
      power.on();
      volume.setDefault();
    }
    isDrawn = true;
  }

  public synchronized void off() {
    if (isDrawn) {
      power.off();
    }
    isDrawn = false;
  }

  public synchronized boolean isDrawn() {
    return isDrawn;
  }

  @Override
  public String toString() {
    return "Cloches is " + (isDrawn ? "on" : "off");
  }

  @Override
  public int hashCode() {
    return Boolean.valueOf(isDrawn).hashCode();
  }

  @Override
  public boolean equals(Object that) {
    if (!(that instanceof ClochesStop)) {
      return false;
    }
    return ((ClochesStop) that).isDrawn == isDrawn;
  }
}
