package app.metrodelay.server.storage;

import app.metrodelay.server.management.RegistryInit;
import app.metrodelay.server.status.StatusUpdate;
import app.metrodelay.server.storage.rs.Status;
import java.io.File;
import java.net.InetAddress;
import java.util.Locale;
import java.util.UUID;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.event.EventType;
import org.glassfish.jersey.servlet.ServletContainer;

///
/// Metrodelay Storage Application
///
public class App {

    private static final String ARG_PORT = "port";
    private static final String ARG_HOST = "host";
    private static final String REGISTRY_MULTICAST_IP = "multicastip";
    private static final String REGISTRY_MULGTICAST_PORT = "multicastport";

    private static final String DEFAULT_PORT = "8001";
    private static final String DEFAULT_MULTICAST_IP = "233.146.53.48";
    private static final String DEFAULT_MULTICAST_PORT = "6839";

    private static final Logger l = LogManager.getLogger(App.class);

    public static org.eclipse.jetty.server.Server server(int port) {
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(port);
        return server;
    }

    private static Options options() {
        var options = new Options();
        options.addOption("p", ARG_PORT, true, "server port");
        options.addOption("h", ARG_HOST, true, "server port");
        options.addOption("ma", REGISTRY_MULTICAST_IP, true, "registry service ip address");
        options.addOption("mp", REGISTRY_MULGTICAST_PORT, true, "registry serivce multicast port");
        return options;
    }

    private static ContextHandler restHandler(String multicastAddress, String multicastPort, String hostname) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        ServletHandler handler = new ServletHandler();
        context.setHandler(handler);
        context.setWelcomeFiles(new String[]{"index.jsp"});
        ServletHolder servletHolder = handler.addServletWithMapping(ServletContainer.class, "/api/*");
        servletHolder.setInitOrder(1);
        servletHolder.setInitParameter("jersey.config.server.provider.packages", "app.metrodelay.server.storage.rs");
        servletHolder.setInitParameter("jersey.config.server.tracing.type", "ALL");
        servletHolder.setInitParameter("jersey.config.server.tracing.threshold", "TRACE");
        servletHolder.setInitParameter("jersey.config.server.logging.logger.level", "DEBUG");
        context.setInitParameter(RegistryInit.REGISTRY_MULTICAST_IP, multicastAddress);
        context.setInitParameter(RegistryInit.REGISTRY_MULTICAST_PORT, multicastPort);
        context.setInitParameter(RegistryInit.REGISTRY_STATUS_UPDATE_SERVICE_URI, "urn:metrodelay.app:service:statusupdate:1.0");
        context.setInitParameter(RegistryInit.REGISTRY_STATUS_UPDATE_SERVICE_HOST, hostname);
        context.setInitParameter(RegistryInit.CONTEXT_PARAM_INTERVAL, "PT1M");
        context.addEventListener(new RegistryInit());
        return context;
    }

    private static CacheManager cacheManager() {
        return CacheManagerBuilder.newCacheManagerBuilder()
          .with(CacheManagerBuilder.persistence(String.format("%s%s%s%s%s%s", System.getProperty("java.io.tmpdir"), File.separator, "metrodelay", File.separator, "storage", File.separator, "cache")))
          .withCache("status",
            CacheConfigurationBuilder.newCacheConfigurationBuilder(UUID.class, StatusUpdate.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
              .heap(10_000, EntryUnit.ENTRIES)
              /*.disk(128, MemoryUnit.MB)*/)
              .withService(
                  CacheEventListenerConfigurationBuilder.newEventListenerConfiguration(
                      cacheEvent -> l.info("Cache entry {} '{}'", cacheEvent.getType().name().toLowerCase(Locale.ROOT), cacheEvent.getKey()),
                      EventType.CREATED, EventType.UPDATED, EventType.EVICTED, EventType.EXPIRED)
                    .unordered()
                    .asynchronous()
              )
          )
          .build();
    }

    void main(String[] args) throws Exception {
        CommandLine line = new DefaultParser().parse(options(), args);
        var cacheManager = cacheManager();
        cacheManager.init();
        Status.initCache(
          cacheManager.getCache("status", UUID.class, StatusUpdate.class)
        );
        org.eclipse.jetty.server.Server server = server(Integer.parseInt(line.getOptionValue(ARG_PORT, DEFAULT_PORT)));
        server.setHandler(restHandler(
          line.getOptionValue(REGISTRY_MULTICAST_IP, DEFAULT_MULTICAST_IP),
          line.getOptionValue(REGISTRY_MULGTICAST_PORT, DEFAULT_MULTICAST_PORT),
          line.getOptionValue(ARG_HOST, InetAddress.getLocalHost().getHostName())
        ));
        server.start();
        server.join();
    }
}
