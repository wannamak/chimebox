package chimebox.logical;

import chimebox.Chimebox;
import chimebox.physical.GPIOController;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HourlyChimeSwitch extends GPIOController {
  private final Logger logger = Logger.getLogger(HourlyChimeSwitch.class.getName());

  private static final int HOURLY_CHIME_SWITCH_LOGICAL_PIN = 19;

  public HourlyChimeSwitch() {
    super(HOURLY_CHIME_SWITCH_LOGICAL_PIN, Direction.IN);
  }

  public boolean isClosed() throws IOException {
    return get() == Value.HIGH;
  }

  @Override
  public String toString() {
    try {
      return "Hourly chime switch is " + (isClosed() ? "on" : "off");
    } catch (IOException e) {
      logger.log(Level.WARNING, "", e);
      return super.toString();
    }
  }
}
