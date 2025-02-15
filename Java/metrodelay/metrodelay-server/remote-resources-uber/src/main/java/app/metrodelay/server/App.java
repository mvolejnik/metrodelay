package app.metrodelay.server;

import app.metrodelay.server.registry.ServiceRegistryImpl;
import app.metrodelay.server.scheduler.CachedItem;
import app.metrodelay.server.scheduler.CachedItemKey;
import app.metrodelay.server.scheduler.GetUrlResourceJob;
import app.metrodelay.server.scheduler.QuartzInit;
import app.metrodelay.server.scheduler.StatusCache;
import app.metrodelay.server.status.StatusUpdate;
import java.io.File;
import java.time.Duration;
import java.util.UUID;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

///
/// Metrodelay remote resource application
///
public class App {

  private static final String JOB_INTERVAL = "interval";
  private static final String JOB_INTERVAL_DELAY = "intervaldelay";
  private static final String JOB_INTERVAL_RANDOM = "intervalrandom";
  private static final String OPERATORS = "operators";
  private static final String REGISTRY_MULTICAST_IP = "multicastip";
  private static final String REGISTRY_MULGTICAST_PORT = "multicastport";
  private static final String SERVICE_STATUS_UPDATE = "serviceStatusUpdate";
  private static final String OPERATORS_DEFAULT = """
                                                  cz.prg.dpp=https://www.dpp.cz/rss/cz/mimoradne-udalosti.xml;""";
                                                  //cz.prg.pid=https://pid.cz/feed/rss-mimoradnosti/""";

  private static Options options() {
    var options = new Options();
    options.addOption("i", JOB_INTERVAL, true, "scheduler job interval");
    options.addOption("d", JOB_INTERVAL_DELAY, true, "scheduler job interval delay");
    options.addOption("r", JOB_INTERVAL_RANDOM, true, "scheduler job interval random");
    options.addOption("ma", REGISTRY_MULTICAST_IP, true, "registry service ip address");
    options.addOption("mp", REGISTRY_MULGTICAST_PORT, true, "registry serivce multicast port");
    options.addOption("s", SERVICE_STATUS_UPDATE, true, "status update service URN");
    options.addOption("o", OPERATORS, true, "operators map as operatorid=url");
    return options;
  }

  public static void main(String[] args) throws Exception {
    CommandLine line = new DefaultParser().parse(options(), args);
    var serviceRegistry = new ServiceRegistryImpl(
            line.getOptionValue(REGISTRY_MULTICAST_IP, ServiceRegistryImpl.MULTICAST_ADDRESS),
            Integer.parseInt(line.getOptionValue(REGISTRY_MULGTICAST_PORT, String.valueOf(ServiceRegistryImpl.MULTICAST_PORT))));
    serviceRegistry.init();
    Registry.serviceRegistry(serviceRegistry);
    try (
            var cache = cacheManager();
            var scheduler = new QuartzInit(
                    Duration.parse(line.getOptionValue(JOB_INTERVAL_DELAY, "PT2S")),
                    Duration.parse(line.getOptionValue(JOB_INTERVAL, "PT1M")),
                    Duration.parse(line.getOptionValue(JOB_INTERVAL_RANDOM, "PT1M")),
                    line.getOptionValue(OPERATORS, OPERATORS_DEFAULT))) {
      cache.init();
      GetUrlResourceJob.initCache(
              cache.getCache("operator", String.class, String.class),
              cache.getCache("resource", CachedItemKey.class, CachedItem.class));
      StatusCache.init(cache.getCache("status", UUID.class, StatusUpdate.class));
      while (true) {
        Thread.sleep(100);
      }
    }
  }

  private static CacheManager cacheManager() {
    return CacheManagerBuilder.newCacheManagerBuilder()
            .with(CacheManagerBuilder.persistence(String.format("%s%s%s%s%s%s", System.getProperty("java.io.tmpdir"), File.separator, "metrodelay" , File.separator,  "remote-resources", File.separator, "cache")))
            .withCache("operator",
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
                            .heap(200, EntryUnit.ENTRIES)))
            .withCache("resource",
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(CachedItemKey.class, CachedItem.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
                            .heap(200, EntryUnit.ENTRIES)
                            .disk(128, MemoryUnit.MB)))
            .withCache("status",
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(UUID.class, StatusUpdate.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
                            .heap(10_000, EntryUnit.ENTRIES)))
            .build();
  }

}
