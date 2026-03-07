package app.metrodelay.metrics;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.logging.Log4j2Metrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/// Metrics class that sets up a PrometheusMeterRegistry
/// and starts an embedded HTTP server to expose the metrics at /metrics endpoint.
public class Metrics implements AutoCloseable{

  public static PrometheusMeterRegistry registry;

  private HttpServer server;

  private List<AutoCloseable> autoCloseables = new ArrayList<>();

  private static final Logger l = LogManager.getLogger(Metrics.class);

  /// Sets up the PrometheusMeterRegistry with common tags and binds JVM and system metrics.
  public synchronized Metrics setup(String application, MeterBinder... customMeterBinders) {
    if (registry != null) {
      throw new IllegalStateException("Metrics registry already initialized");
    }
    registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    registry.config().commonTags("application", application);
    new JvmMemoryMetrics().bindTo(registry);
    var jvmGcMetrics = new JvmGcMetrics();
    jvmGcMetrics.bindTo(registry);
    new JvmThreadMetrics().bindTo(registry);
    new ClassLoaderMetrics().bindTo(registry);
    new ProcessorMetrics().bindTo(registry);
    var log4jMetrics = new Log4j2Metrics();
    log4jMetrics.bindTo(registry);
    autoCloseables.add(jvmGcMetrics);
    autoCloseables.add(log4jMetrics);
    Stream.of(customMeterBinders).forEach(mb -> mb.bindTo(registry));
    Stream.of(customMeterBinders)
      .filter(AutoCloseable.class::isInstance)
      .map(AutoCloseable.class::cast)
      .forEach(autoCloseables::add);
    return this;
  }

  ///  Starts an embedded HTTP server on the specified port to expose the metrics.
  /// If the port is unavailable, it will try the next port in the range.
  public Metrics startEmbeddedServer(int port, int portRangeSize) throws Exception {
    return startEmbeddedServer(Executors.newSingleThreadExecutor(), port, portRangeSize);
  }

  ///  Starts an embedded HTTP server with the provided executor on the specified port to expose the metrics.
  public synchronized Metrics startEmbeddedServer(Executor executor, int port, int portRangeSize) throws Exception {
    if (server != null) {
      throw new IllegalStateException("Server already started on port " + server.getAddress().getPort());
    }
    for(int p = port; p < port + portRangeSize; p++){
      try {
        var server = HttpServer.create(new InetSocketAddress(p), 0);
        server.setExecutor(executor);
        server.createContext("/metrics", httpExchange -> {
          byte[] metrics = Metrics.registry.scrape().getBytes(StandardCharsets.UTF_8);
          httpExchange.sendResponseHeaders(200, metrics.length);
          httpExchange.getResponseBody().write(metrics);
          httpExchange.close();
        });
        server.start();
        this.server = server;
        break;
      } catch (Exception e) {
        if(p == port + portRangeSize - 1){
          throw new Exception("No free port found in range " + port + "-" + (port + portRangeSize - 1));
        }
      }
    }
    return this;
  }

  /// Returns the port on which the embedded HTTP server is bound to.
  public int port(){
    return server.getAddress().getPort();
  }

  @Override
  public void close() throws Exception {
    try{
      if(server != null){
        server.stop(0);
      }
    } catch (Exception e) {
      l.warn("Failed to stop metrics server", e);
    }
    for(var c : autoCloseables){
      try {
        c.close();
      } catch (Exception e) {
        l.warn("Failed to close autoCloseable", e);
      }
    }
  }
}
