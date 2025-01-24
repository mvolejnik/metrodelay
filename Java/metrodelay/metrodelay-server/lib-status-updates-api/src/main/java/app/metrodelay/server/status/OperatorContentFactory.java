/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.metrodelay.server.status;

import app.metrodelay.server.status.StatusUpdate;
import app.metrodelay.server.status.StatusUpdateException;
import java.io.InputStream;
import java.util.List;

@FunctionalInterface
public interface OperatorFactory {
  List<StatusUpdate> statusUpdates(InputStream contentInputStream) throws StatusUpdateException;
}
