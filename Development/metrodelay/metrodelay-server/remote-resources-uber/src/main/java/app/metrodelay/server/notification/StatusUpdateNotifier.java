/*
 * Status update notifier
 */
package app.metrodelay.server.notification;
 /**
 *
 * @author mvolejnik
 */
public interface StatusUpdateNotifier {

    void send(StatusUpdateNotification notification);

}