package chimebox.physical;

import java.util.logging.Logger;

public class SystemManagementBus {
  private final Logger logger = Logger.getLogger(SystemManagementBus.class.getName());
  private int fd;

  public synchronized void initialize(int deviceId) {
    fd = initializeFileDescriptor("/dev/i2c-1", deviceId);
    if (fd < 0) {
      logger.warning("Unable to initialize bus for device " + deviceId);
    }
  }

  public synchronized int readByte(int register) {
    if (fd < 0) {
      logger.warning("Uninitialized read of register " + register);
      return 0;
    }
    return readByte(fd, register);
  }

  public synchronized void writeByte(int register, int value) {
    if (fd < 0) {
      logger.warning("Uninitialize write of register " + register);
      return;
    }
    if (writeByte(fd, register, value) < 0) {
      logger.warning("Error writing register " + register);
    }
  }

  private native int readByte(int fd, int register);
  private native int writeByte(int fd, int register, int value);
  private native int initializeFileDescriptor(String devicePath, int deviceId);
}
