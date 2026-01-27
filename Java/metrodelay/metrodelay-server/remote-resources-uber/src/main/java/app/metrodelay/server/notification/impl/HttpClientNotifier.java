/*
 * HTTP Client Status Update Notifier base on java HttpClient implementation.
 */
package app.metrodelay.server.notification.impl;

import app.metrodelay.server.notification.StatusUpdateNotifier;
import app.metrodelay.server.status.StatusUpdate;
import jakarta.json.bind.JsonbBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// REST API notifier.
public class HttpClientNotifier implements StatusUpdateNotifier {

  private static final Logger l = LogManager.getLogger(HttpClientNotifier.class);

  @Override
  public void send(URI uri, StatusUpdate statusUpdate) {
    HttpRequest request;
    URI requestUri;
    try (var jsonb = JsonbBuilder.create();
      var client = HttpClient.newHttpClient()) {
      var json = jsonb.toJson(statusUpdate);
      requestUri = URI.create(uri.toString().concat("/updates/%s".formatted(statusUpdate.uuid())));
      request = HttpRequest.newBuilder()
        .uri(requestUri)
        .header("Content-Type", "application/json")
        .PUT(HttpRequest.BodyPublishers.ofString(json))
        .timeout(Duration.ofMillis(500))
        .build();
      var responseHandler = client.send(request, HttpResponse.BodyHandlers.ofString());
      var statusCode = responseHandler.statusCode();
      if (statusCode < 200 || statusCode >= 300) {
        l.error("sent notification http response status code '{}' for '{}', content: '{}'", statusCode, requestUri, json);
        if (l.isTraceEnabled()) {
          responseHandler.headers().map().entrySet().forEach(l::trace);
        }
      } else {
        l.debug("Notification sent '{}}' ('{}}')", statusUpdate.uuid(), statusUpdate.operatorId());
      }
    } catch (IOException | InterruptedException ex) {
      l.error("notification sending failed", ex);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
