/*
 * HTTP Client Status Update Notifier base on java HttpClient implementation.
 */
package app.ptd.server.notification.impl;

import app.ptd.server.notification.StatusUpdateNotification;
import app.ptd.server.notification.StatusUpdateNotifier;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mvolejnik
 */
public class HttpClientNotifier implements StatusUpdateNotifier {

    private static final Logger l = LogManager.getLogger(HttpClientNotifier.class);

    @Override
    public void send(StatusUpdateNotification notification) {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/ptd/updates")) //TODO
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofInputStream(() -> notification.content()))
                .build();
        client.sendAsync(request,HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode)
                .thenAccept(s -> {l.info("send:: Status update notification status code [{}]", s);});
    }
}
