package chimebox.physical;

import chimebox.logical.Relay;

import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Controls relays via /dev/gpiochipN, which is the 'new' (and only) way to control GPIO
 * in Ubuntu 24 (kernel 6.8.0-1018).  See LegacyGPIOController for older kernels.
 */
public class GPIORelayImpl extends GPIOController implements Relay {
  private static final Logger logger = Logger.getLogger(GPIORelayImpl.class.getName());

  private boolean isClosed = false;

  public GPIORelayImpl(Path devicePath, int logicalPin) {
    super(devicePath, logicalPin, Direction.OUT);
  }

  @Override
  public void close() {
    set(Value.ACTIVE);
    isClosed = true;
  }

  @Override
  public void open() {
    set(Value.INACTIVE);
    isClosed = false;
  }

  @Override
  public boolean isClosed() {
    return isClosed;
  }
}
