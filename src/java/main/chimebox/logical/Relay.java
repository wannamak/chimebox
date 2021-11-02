package chimebox.logical;

import java.io.IOException;

public interface Relay {
  void close();

  void open();

  boolean isClosed() throws IOException;

  void initialize() throws IOException;
}
