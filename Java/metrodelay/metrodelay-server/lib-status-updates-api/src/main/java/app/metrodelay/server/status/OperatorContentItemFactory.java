package app.metrodelay.server.status;

// 

import java.util.function.Consumer;

@FunctionalInterface
public interface OperatorContentItemFactory {
  void contentItem(Consumer<StatusUpdate> update);
}
