/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package app.metrodelay.server.status;

import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

/**
 * Adapter interface to produce status update details.
 * 
 */
public interface OperatorStatusUpdate {
  /// 
  /// @param contentInputStream
  /// @param guid
  /// @return
  /// @throws StatusUpdateException 
  ///
  Optional<StatusUpdate> statusUpdate(InputStream contentInputStream, String guid, URI baseUri) throws StatusUpdateException;
}
