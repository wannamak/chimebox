package chimebox.manual;

import chimebox.logical.HourlyChimeSwitch;
import chimebox.logical.RaspberryRelays;
import chimebox.logical.Relay;
import chimebox.logical.Relays;
import chimebox.physical.GPIOChipInfoProvider;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.nio.file.Path;

import static chimebox.physical.GPIOChipInfoProvider.DEFAULT_RASPBERRY_PI_DEVICE_LABEL;

public class ManualTester {

  public static void main(String[] args) throws Exception {
    new ManualTester().run();
  }

  public void run() throws IOException {
    System.loadLibrary("chimebox");
    GPIOChipInfoProvider gpioManager = new GPIOChipInfoProvider();
    Path gpioDevicePath = gpioManager.getDevicePathForLabel(DEFAULT_RASPBERRY_PI_DEVICE_LABEL);
    Preconditions.checkNotNull(
        gpioDevicePath, "No device for label " + DEFAULT_RASPBERRY_PI_DEVICE_LABEL);
    Relays relays = new RaspberryRelays(gpioDevicePath);
    HourlyChimeSwitch hourlyChimeSwitch = new HourlyChimeSwitch(gpioDevicePath);
    relays.initialize();
    hourlyChimeSwitch.initialize();
    while (true) {
      try {
        System.out.printf("0-%d to toggle relays; s0-s%d for 1/2 second strike; " +
            "h to read on/off switch\n",
            relays.length() - 1, relays.length() - 1);
        String line = System.console().readLine().trim();
        if (line.equals("h")) {
          System.out.printf("Switch status = %s\n", hourlyChimeSwitch.isClosed() ? "on" : "off");
          continue;
        }
        boolean strike = false;
        if (line.startsWith("s")) {
          strike = true;
          line = line.substring(1);
        }
        int index = Integer.parseInt(line);
        Relay targetRelay = relays.getRelays()[index];

        if (strike) {
          System.out.println("striking relay " + index);
          targetRelay.close();
          try {
            Thread.sleep(500);
          } catch (InterruptedException ie) {
          }
          targetRelay.open();
        } else {
          if (targetRelay.isClosed()) {
            System.out.println("opening relay " + index);
            targetRelay.open();
          } else {
            System.out.println("closing relay " + index);
            targetRelay.close();
          }
        }

        for (int i = 0; i < relays.length(); i++) {
          Relay relay = relays.get(i);
          System.out.printf("  rel%d=%s%s%s", i, i == index ? "**" : "",
              relay.isClosed() ? "on " : "off", i == index ? "**" : "");
          if ((i % 8) == 0) {
            System.out.println();
          }
        }
        System.out.println();

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
