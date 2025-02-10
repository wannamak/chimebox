package chimebox.physical;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls GPIO via /sys/class/gpio, which is deprecated and has been removed from newer kernels.
 */
public class LegacyGPIOController {
  private static final Logger logger = Logger.getLogger(LegacyGPIOController.class.getName());

  private static final String PREFIX = "/sys/class/gpio";
  private static final Path EXPORT_DIR = new File(PREFIX, "export").toPath();
  private final Path DIRECTION_FILE;
  private final Path VALUE_FILE;

  public enum Direction {
    IN,
    OUT
  }

  public enum Value {
    HIGH,
    LOW
  }

  private final int logicalPin;
  private final Direction direction;

  public LegacyGPIOController(int logicalPin, Direction direction) {
    this.logicalPin = logicalPin;
    this.direction = direction;
    File gpioDir = new File(PREFIX, String.format("gpio%d", logicalPin));
    this.DIRECTION_FILE = new File(gpioDir, "direction").toPath();
    this.VALUE_FILE = new File(gpioDir, "value").toPath();
  }

  public synchronized void initialize() throws IOException {
    if (!Files.exists(DIRECTION_FILE)) {
      logger.fine("Writing pin to " + EXPORT_DIR);
      if (!repeatedlyAttemptWrite(EXPORT_DIR, Integer.toString(logicalPin))) {
        logger.severe("Unable to export pin");
      }
    }
    logger.fine("Writing direction to " + DIRECTION_FILE);
    // https://www.kernel.org/doc/Documentation/gpio/sysfs.txt
    // says we can write high for out
    String directionFileContent = direction == Direction.OUT ? "high" : "in";
    if (!repeatedlyAttemptWrite(DIRECTION_FILE, directionFileContent)) {
      logger.severe("Unable to write direction file for logical pin " + logicalPin);
    }
  }

  private boolean repeatedlyAttemptWrite(Path path, String content) throws IOException {
    int retryCount = 0;
    while (retryCount < 20) {
      try {
        Files.writeString(path, content);
        return true;
      } catch (AccessDeniedException ade) {
        retryCount++;
        try {
          Thread.sleep(50);
        } catch (InterruptedException ie) {
          logger.log(Level.WARNING, "", ie);
        }
      }
    }
    return false;
  }

  public synchronized void set(Value value) throws IOException {
    Preconditions.checkState(direction == Direction.OUT);
    logger.finest("Setting pin by writing " + VALUE_FILE);
    Files.writeString(VALUE_FILE, value.equals(Value.LOW) ? "0" : "1");
  }

  public synchronized Value get() throws IOException {
    logger.finest("Getting pin by reading " + VALUE_FILE);
    String currentValue = Files.readString(VALUE_FILE);
    return currentValue.trim().equals("0") ? Value.LOW : Value.HIGH;
  }

  @Override
  public String toString() {
    return String.format("GPIO pin %d direction %s", logicalPin,
        direction == Direction.OUT ? "out" : "in");
  }

  @Override
  public int hashCode() {
    return Objects.hash(logicalPin, direction);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof LegacyGPIOController that)) {
      return false;
    }
    return Objects.equals(this.direction, that.direction)
        && Objects.equals(this.logicalPin, that.logicalPin);
  }
}
