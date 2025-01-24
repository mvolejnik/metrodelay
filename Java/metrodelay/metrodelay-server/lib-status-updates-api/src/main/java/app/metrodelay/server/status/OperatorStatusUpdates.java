package app.metrodelay.server.status;

import java.io.InputStream;
import java.util.List;

/**
 * Adapter interface.
 */
@FunctionalInterface
public interface OperatorStatusUpdates {
  List<StatusUpdate> statusUpdates(InputStream contentInputStream) throws StatusUpdateException;
  
}
