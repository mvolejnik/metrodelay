package app.metrodelay.server.scheduler;

import app.metrodelay.server.status.StatusUpdateException;
import app.metrodelay.server.remoteresources.http.HttpResource;
import app.metrodelay.server.remoteresources.RemoteResourceException;
import app.metrodelay.server.remoteresources.service.ResourceService;
import app.metrodelay.server.remoteresources.service.ResourceServiceImpl;
import app.metrodelay.server.status.ContentFactoryRegistry;
import app.metrodelay.server.status.StatusUpdate;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class GetUrlResourceJob implements Job {

  private static final int NOTIFICATION_POOL_SIZE = 5;
  private static final ExecutorService NOTIFICATION_EXECUTOR = Executors.newFixedThreadPool(NOTIFICATION_POOL_SIZE);
  static final String DATA_OPERATOR = "opr";
  static final String DATA_URL = "url";
  private static final Logger l = LogManager.getLogger(GetUrlResourceJob.class);
  private static Cache<String, String> operatorCache;
  private static Cache<CachedItemKey, CachedItem> resourceCache;
  private final ResourceService resourceService;

  public GetUrlResourceJob() {
    this.resourceService = new ResourceServiceImpl();
  }  
  

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    l.info("job [{}] started", context.getJobDetail().getKey());
    var operatorId = context.getJobDetail().getJobDataMap().getString(DATA_OPERATOR);
    var urlParam = context.getJobDetail().getJobDataMap().getString(DATA_URL);
    var contentFactory = ContentFactoryRegistry.get(operatorId);
    l.debug("resource url '{}'", urlParam);
    try {
      var uri = URI.create(urlParam);
      var cachedFingerpint = operatorCache.get(operatorId);
      l.debug("operator '{}' resource '{}' cached fingerpring '{}'", operatorId, uri, cachedFingerpint);
      l.debug("getting resource '{}'", uri);
      var resource = resourceService.resource(uri.toURL(), cachedFingerpint);
      l.debug("resource has content '{}'", resource.isPresent());
      resource.ifPresent(r -> {
        try {
          for (StatusUpdate su : contentFactory.statusUpdates(r.content().get())){
            try {
              var cached = StatusCache.get().get(su.uuid());
              if (cached == null || cached.detail().valid()){
                l.debug("getting resource '{}'", su.link());
                var detailResource = new HttpResource().content(su.link().toURL(), null, null);
                l.debug("detail resource has content '{}'", detailResource.isPresent());
                var refreshed = contentFactory.statusUpdate(detailResource.get().content().get(), su.uuid(), su.link());
                if (refreshed.isPresent() && !Objects.equals(cached, refreshed.get())){
                  var updated = refreshed.get();
                  l.info("[{}] status update '{}'", updated.detail().valid() ? "✗" : "✓", updated);
                  StatusCache.get().put(updated.uuid(), updated);
                }
              }
            } catch (RemoteResourceException|MalformedURLException ex) {
              l.warn("unable to fetch resource '{}'", su.link(), ex);
            }
          }
        } catch (StatusUpdateException ex) {
          l.error("unable to process '{}' content", operatorId);
        }
       });
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
  
  public static void initCache(
          Cache<String, String> operatorCache,
          Cache<CachedItemKey, CachedItem> resourceCache){
    GetUrlResourceJob.operatorCache = operatorCache;
    GetUrlResourceJob.resourceCache = resourceCache;
  }

}
