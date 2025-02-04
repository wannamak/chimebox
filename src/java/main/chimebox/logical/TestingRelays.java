package chimebox.logical;

import java.io.IOException;

public class TestingRelays extends Relays {
  private static final int NUM_RELAYS = 24;
  @Override
  public void initialize() throws IOException {
    relays = new Relay[NUM_RELAYS];
    for (int i = 0; i < NUM_RELAYS; i++) {
      relays[i] = new Relay() {
        @Override
        public void close() {
        }

        @Override
        public void open() {
        }

        @Override
        public boolean isClosed() throws IOException {
          return false;
        }

        @Override
        public void initialize() throws IOException {
        }
      };
    }
  }
}
