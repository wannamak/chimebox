package chimebox.logical;

import chimebox.physical.GPIORelayImpl;
import chimebox.physical.MCP23017Controller;
import chimebox.physical.MCP23017RelayImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

public class Relays {
  private static final Logger logger = Logger.getLogger(Relays.class.getName());

  private Relay[] relays;

  public void initialize() throws IOException {
    MCP23017Controller controller = new MCP23017Controller();
    controller.initialize();
    relays = new Relay[] {
        new GPIORelayImpl(12),
        new GPIORelayImpl(16),
        new GPIORelayImpl(20),
        new GPIORelayImpl(21),
        new GPIORelayImpl(23),
        new GPIORelayImpl(24),
        new GPIORelayImpl(25),
        new GPIORelayImpl(26),
        new MCP23017RelayImpl(controller, 7),
        new MCP23017RelayImpl(controller, 6),
        new MCP23017RelayImpl(controller, 5),
        new MCP23017RelayImpl(controller, 4),
        new MCP23017RelayImpl(controller, 3),
        new MCP23017RelayImpl(controller, 2),
        new MCP23017RelayImpl(controller, 1),
        new MCP23017RelayImpl(controller, 0),
        new MCP23017RelayImpl(controller, 15),
        new MCP23017RelayImpl(controller, 14),
        new MCP23017RelayImpl(controller, 13),
        new MCP23017RelayImpl(controller, 12),
        new MCP23017RelayImpl(controller, 11),
        new MCP23017RelayImpl(controller, 10),
        new MCP23017RelayImpl(controller, 9),
        new MCP23017RelayImpl(controller, 8),
    };
    for (int i = 0; i < relays.length; ++i) {
      logger.info(String.format("Initializing relay %d", i));
      relays[i].initialize();
    }
  }

  public Relay[] getRelays() {
    return relays;
  }

  public int length() {
    return relays.length;
  }

  public Relay get(int index) {
    return relays[index];
  }

  @Override
  public String toString() {
    return "Relays";
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(relays);
  }

  @Override
  public boolean equals(Object that) {
    if (!(that instanceof Relays)) {
      return false;
    }
    return Arrays.equals(((Relays) that).relays, relays);
  }
}
