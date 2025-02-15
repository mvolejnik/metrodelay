package app.metrodelay.server.status;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter interface.
 */
public interface OperatorStatusUpdates {
  List<StatusUpdate> statusUpdates(InputStream contentInputStream) throws StatusUpdateException;
  /// 
  /// @param contentInputStream
  /// @param uuid
  /// @return
  /// @throws StatusUpdateException 
  ///
  Optional<StatusUpdate> statusUpdate(InputStream contentInputStream, UUID uuid, URI baseUri) throws StatusUpdateException;
  
}
