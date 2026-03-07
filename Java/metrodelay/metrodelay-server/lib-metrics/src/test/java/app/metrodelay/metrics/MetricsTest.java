package app.metrodelay.metrics;

import org.junit.jupiter.api.Test;
import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

class MetricsTest {

  @Test
  void metrics() throws Exception {
    try(
      var metrics = new Metrics().setup("junit").startEmbeddedServer(19003, 5);
      var httpClient = HttpClient.newHttpClient();
    ) {
      var response = httpClient.send(java.net.http.HttpRequest.newBuilder()
        .uri(new java.net.URI("http://localhost:%s/metrics".formatted(metrics.port())))
        .GET()
        .build(), java.net.http.HttpResponse.BodyHandlers.ofString());
      assertNotNull(response);
      assertAll(
        () -> assertNotNull(response.body()),
        () -> assertTrue(response.body().contains("application=\"junit\"")),
        () -> assertTrue(response.body().contains("jvm_memory_used_bytes"))
      );
    }
  }

}