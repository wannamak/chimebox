package chimebox.physical;

import chimebox.logical.Relay;

import java.io.IOException;

public class MCP23017RelayImpl implements Relay {
  private final MCP23017Controller controller;
  private final int pin;

  public MCP23017RelayImpl(MCP23017Controller controller, int pin) {
    this.controller = controller;
    this.pin = pin;
  }

  @Override
  public void close() {
    controller.set(pin, MCP23017Controller.Value.LOW);
  }

  @Override
  public void open() {
    controller.set(pin, MCP23017Controller.Value.HIGH);
  }

  @Override
  public boolean isClosed() throws IOException {
    return controller.get(pin) == MCP23017Controller.Value.LOW;
  }

  @Override
  public void initialize() throws IOException {

  }
}
