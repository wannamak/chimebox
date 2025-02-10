package chimebox.physical;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.stream.Stream;

/**
 * Provides a way to figure out which /dev/gpiochipN the program should use.
 * The label is set in the configuration.
 * Some manual tools use the default label below.
 */
public class GPIOChipInfoProvider {
  public static final String DEFAULT_RASPBERRY_PI_DEVICE_LABEL = "pinctrl-bcm2711";

  public static class GPIOChipInfo {
    public final String name;
    public final String label;
    public final int numLines;

    public GPIOChipInfo(String name, String label, int numLines) {
      this.name = name;
      this.label = label;
      this.numLines = numLines;
    }

    @Override
    public String toString() {
      return String.format("name:[%s] label:[%s] numLines:[%d]", name, label, numLines);
    }
  }

  /**
   * Given a label, iterates all /dev/gpiochip devices trying to find the one which matches.
   * @return the matching device or null
   */
  public Path getDevicePathForLabel(String targetLabel) throws IOException {
    Path devDirectory = new File("/dev").toPath();
    final PathMatcher filter = devDirectory.getFileSystem().getPathMatcher("glob:**/gpiochip*");
    try (Stream<Path> stream = Files.list(devDirectory)) {
      for (Path path : stream.filter(filter::matches).toList()) {
        GPIOChipInfo info = getGPIOChipInfo(path);
        if (info != null && info.label.equals(targetLabel)) {
          return path;
        }
      }
    }
    return null;
  }

  public GPIOChipInfo getGPIOChipInfo(Path devicePath) {
    return getGPIOChipInfoInternal(devicePath.toString());
  }

  native private GPIOChipInfo getGPIOChipInfoInternal(String devicePath);
}
