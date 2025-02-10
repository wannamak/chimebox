package chimebox.physical;

import com.google.common.base.Preconditions;

import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Controls GPIO via /dev/gpiochipN, which is the 'new' (and only) way to control GPIO
 * in Ubuntu 24 (kernel 6.8.0-1018).  See LegacyGPIOController for older kernels.
 */
public class GPIOController {
  private static final Logger logger = Logger.getLogger(GPIOController.class.getName());

  public enum Value {
    ACTIVE,
    INACTIVE
  }

  public enum Direction {
    IN,
    OUT
  }

  private final Path devicePath;
  private final int logicalPin;
  private final Direction direction;
  private long context;

  public GPIOController(Path devicePath, int logicalPin, Direction direction) {
    this.devicePath = devicePath;
    this.logicalPin = logicalPin;
    this.direction = direction;
  }

  public synchronized void initialize() {
    if (direction == Direction.OUT) {
      context = initializeOutput(devicePath.toString(), logicalPin, true);
    } else {
      Preconditions.checkState(direction == Direction.IN);
      context = initializeInput(devicePath.toString(), logicalPin);
    }
    Preconditions.checkState(context != 0);
  }

  /**
   * Returns an opaque context for the GPIO pin.
   */
  private native long initializeOutput(String devicePath, int pin, boolean isActiveLow);
  private native long initializeInput(String devicePath, int pin);

  public synchronized void set(Value value) {
    Preconditions.checkState(direction == Direction.OUT);
    int result = setInternal(context, value.equals(Value.ACTIVE));
    Preconditions.checkState(result == 0);
  }

  /**
   * Returns -1 on error, else 0.
   */
  private native int setInternal(long context, boolean value);

  public synchronized Value get() {
    boolean result = getInternal(context);
    return result ? Value.ACTIVE : Value.INACTIVE;
  }

  private native boolean getInternal(long context);

  @Override
  public String toString() {
    return String.format("GPIO pin %d:%s %s", logicalPin, direction.toString(), devicePath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(logicalPin, direction, devicePath);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof GPIOController that)) {
      return false;
    }
    return Objects.equals(this.logicalPin, that.logicalPin)
        && Objects.equals(this.direction, that.direction)
        && Objects.equals(this.devicePath, that.devicePath);
  }
}
