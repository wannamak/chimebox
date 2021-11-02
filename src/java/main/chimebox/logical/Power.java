package chimebox.logical;

import java.io.IOException;

public class Power {
  private static final int POWER_RELAY = 0;

  private final Relay powerRelay;

  public Power(Relays relays) {
    this.powerRelay = relays.get(POWER_RELAY);
  }

  public synchronized void on() {
    powerRelay.close();
  }

  public synchronized void off() {
    powerRelay.open();
  }

  public synchronized boolean isOn() throws IOException {
    return powerRelay.isClosed();
  }

  @Override
  public String toString() {
    return "Power using " + powerRelay.toString();
  }

  @Override
  public int hashCode() {
    return this.hashCode() + powerRelay.hashCode();
  }

  @Override
  public boolean equals(Object that) {
    if (!(that instanceof Power)) {
      return false;
    }
    return ((Power) that).powerRelay == powerRelay;
  }
}
