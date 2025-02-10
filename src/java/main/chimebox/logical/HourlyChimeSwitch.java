package chimebox.logical;

import chimebox.physical.GPIOController;

import java.nio.file.Path;
import java.util.logging.Logger;

public class HourlyChimeSwitch extends GPIOController {
  private final Logger logger = Logger.getLogger(HourlyChimeSwitch.class.getName());

  private static final int HOURLY_CHIME_SWITCH_LOGICAL_PIN = 19;

  public HourlyChimeSwitch(Path gpioDevicePath) {
    super(gpioDevicePath, HOURLY_CHIME_SWITCH_LOGICAL_PIN, Direction.IN);
  }

  public boolean isClosed() {
    return get() == Value.ACTIVE;
  }

  @Override
  public String toString() {
    return "Hourly chime switch is " + (isClosed() ? "on" : "off");
  }
}
