/*
 * Status update notifier
 */
package app.metrodelay.server.notification;

import app.metrodelay.server.status.StatusUpdate;
import java.net.URI;

 /**
 *
 * @author mvolejnik
 */
public interface StatusUpdateNotifier {

    void send(URI uri, StatusUpdate statusUpdate);

}