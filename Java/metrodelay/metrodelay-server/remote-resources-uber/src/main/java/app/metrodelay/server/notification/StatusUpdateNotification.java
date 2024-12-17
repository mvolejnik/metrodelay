/*
 * Status update notification
 */
package app.metrodelay.server.notification;

import java.io.InputStream;

 /**
 *
 * @author mvolejnik
 */
public interface StatusUpdateNotification {

    public InputStream content();

}
