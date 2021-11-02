package chimebox.manual;

import chimebox.logical.HourlyChimeSwitch;
import chimebox.logical.Relay;
import chimebox.logical.Relays;

import java.io.IOException;

public class ManualTester {

  public static void main(String[] args) throws Exception {
    new ManualTester().run();
  }

  public void run() throws IOException {
    System.loadLibrary("chimebox");
    Relays relays = new Relays();
    HourlyChimeSwitch hourlyChimeSwitch = new HourlyChimeSwitch();
    relays.initialize();
    hourlyChimeSwitch.initialize();
    while (true) {
      try {
        System.out.printf("Relay 0 - %d or -1 for switch, or sxx for strike: ", relays.length() - 1);
        String line = System.console().readLine().trim();
        boolean strike = false;
        if (line.startsWith("s")) {
          strike = true;
          line = line.substring(1);
        }
        int index = Integer.parseInt(line);
        System.out.printf("Read [%s]=[%d]\n", line, index);
        if (index == -1) {
          System.out.printf("Switch status = %s\n", hourlyChimeSwitch.isClosed() ? "on" : "off");
          continue;
        }

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
