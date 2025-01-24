package app.metrodelay.server.status;

import java.io.InputStream;
import java.util.List;

@FunctionalInterface
public interface OperatorContentFactory {
  List<StatusUpdate> statusUpdates(InputStream contentInputStream) throws StatusUpdateException;
}
