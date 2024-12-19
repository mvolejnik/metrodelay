/*
 * Status update notifier
 */
package app.metrodelay.server.notification;

import app.metrodelay.server.status.StatusUpdate;
import java.net.URI;
import java.util.List;

 /**
 *
 * @author mvolejnik
 */
public interface StatusUpdateNotifier {

    void send(URI uri, List<StatusUpdate> statusUpdates);

}