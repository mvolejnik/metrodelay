/*
 * HTTP Client Status Update Notifier base on java HttpClient implementation.
 */
package app.metrodelay.server.notification.impl;

import app.metrodelay.server.notification.StatusUpdateNotifier;
import app.metrodelay.server.status.StatusUpdate;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mvolejnik
 */
public class HttpClientNotifier implements StatusUpdateNotifier {

  private static final Logger l = LogManager.getLogger(HttpClientNotifier.class);

  @Override
  public void send(URI uri, List<StatusUpdate> statusUpdates) {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request;
    Jsonb jsonb = JsonbBuilder.create();
    for (var statusUpdate : statusUpdates) {
      var requestUri = URI.create(uri.toString().concat("/updates/%s".formatted(statusUpdate.uuid())));
      request = HttpRequest.newBuilder()
              .uri(requestUri)
              .header("Content-Type", "application/json")
              .PUT(HttpRequest.BodyPublishers.ofString(jsonb.toJson(statusUpdate)))
              .build();
      try {
        var responseHandler = client.send(request, HttpResponse.BodyHandlers.ofString());
        var statusCode = responseHandler.statusCode();
        if (statusCode < 200 || statusCode >= 300) {
          l.error("sent notification http response status code '{}' for '{}'", statusCode, requestUri);
          if (l.isTraceEnabled()) {
            responseHandler.headers().map().entrySet().stream().forEach(l::trace);
          }
        }
      } catch (IOException | InterruptedException ex) {
        l.error("notification sending failed", ex);
      }
    }
  }
}
