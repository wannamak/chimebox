package chimebox.physical;

import chimebox.logical.Relay;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls relays via /sys/class/gpio, which is deprecated and has been removed from newer kernels.
 */
public class LegacyGPIORelayImpl extends LegacyGPIOController implements Relay {
  private static final Logger logger = Logger.getLogger(LegacyGPIORelayImpl.class.getName());

  public LegacyGPIORelayImpl(int logicalPin) {
    super(logicalPin, Direction.OUT);
  }

  @Override
  public void close() {
    try {
      set(Value.LOW);
    } catch (IOException ioe) {
      logger.log(Level.WARNING, ioe.getMessage(), ioe);
    }
  }

  @Override
  public void open() {
    try {
      set(Value.HIGH);
    } catch (IOException ioe) {
      logger.log(Level.WARNING, ioe.getMessage(), ioe);
    }
  }

  @Override
  public boolean isClosed() throws IOException {
    return get().equals(Value.LOW);
  }
}
