package app.metrodelay.server.scheduler;

import app.metrodelay.server.status.StatusUpdateException;
import app.metrodelay.server.Registry;
import app.metrodelay.server.notification.impl.HttpClientNotifier;
import app.metrodelay.server.remoteresources.http.HttpResource;
import app.metrodelay.server.remoteresources.RemoteResourceException;
import app.metrodelay.server.status.ContentFactoryRegistry;
import app.metrodelay.server.status.StatusUpdate;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class GetUrlResourceJob implements Job {

  private static final Map<String, String> OPERATOR_CACHE = new HashMap<>();
  private static final int NOTIFICATION_POOL_SIZE = 5;
  private static final ExecutorService NOTIFICATION_EXECUTOR = Executors.newFixedThreadPool(NOTIFICATION_POOL_SIZE);
  static final String DATA_OPERATOR = "opr";
  static final String DATA_URL = "url";
  private static final Logger l = LogManager.getLogger(GetUrlResourceJob.class);
  private static final String STORAGE_PATH = "/api/countries/%s/cities/%s/operators/%s";
  private static Cache cache;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    l.info("job [{}] started", context.getJobDetail().getKey());
    var operatorId = context.getJobDetail().getJobDataMap().getString(DATA_OPERATOR);
    var urlParam = context.getJobDetail().getJobDataMap().getString(DATA_URL);
    var contentFactory = ContentFactoryRegistry.get(operatorId);
    l.debug("resource url '{}'", urlParam);
    try {
      var uri = URI.create(urlParam);
      var cachedFingerpint = OPERATOR_CACHE.get(operatorId);
      l.debug("operator '{}' resource '{}' cached fingerpring '{}'", operatorId, uri, cachedFingerpint);
      l.debug("getting resource '{}'", uri);
      var resource = new HttpResource().content(uri.toURL(), cachedFingerpint, null);
      l.debug("resource has content '{}'", resource.isPresent());
      resource.ifPresent(r -> {
        try {
          var statusUpdates = contentFactory.statusUpdates(r.content().get());
          //RESOURCE_CACHE.resource(, new CachedItem(statusUpdates, r.fingerprint().orElse(null), r.digest().orElse(null)));
        } catch (StatusUpdateException ex) {
          l.error("unable to process '{}' content", operatorId);
        }        
       });
      /* cached = RESOURCE_CACHE.resource(url);
      if (cached.isPresent()) {
        notifyServices(operatorId, cached.get().content());
      } */
    } catch (MalformedURLException | RemoteResourceException e) {
      l.error("Incorrect URL to download resource '{}'", urlParam);
      l.info("job [{}] finished ✗", context.getJobDetail().getKey());
      throw new JobExecutionException(String.format("Unable to download resource '%s'", urlParam), e);
    } catch (Exception e) {
      l.info("job [{}] finished ✗", context.getJobDetail().getKey());
      l.error("Exception has occured", e);
      throw e;
    }
    l.info("execute:: job [{}] finished ✓", context.getJobDetail().getKey());
  }

  protected void notifyServices(String operatorId, List<StatusUpdate> statusUpdates) {
    var baseUrl = Registry.serviceRegistry().get(URI.create("urn:metrodelay.app:service:statusupdate:1.0"));
    if (baseUrl.isPresent()) {
      try {
        var operatorPathParts = operatorId.split("\\.", 3);
        var serviceUri = new URI(baseUrl.get().toString() + STORAGE_PATH.formatted(operatorPathParts[0], operatorPathParts[1], operatorPathParts[2]));
        new HttpClientNotifier().send(serviceUri, statusUpdates);
      } catch (URISyntaxException ex) {
        l.error("unable to send {} notification to {}", operatorId, baseUrl, ex);
      }
    }
  }
  
  public static void initCache(Cache cache){
    GetUrlResourceJob.cache = cache;
  }

}
