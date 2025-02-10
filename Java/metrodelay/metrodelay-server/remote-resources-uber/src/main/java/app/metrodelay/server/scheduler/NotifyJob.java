/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package app.metrodelay.server.scheduler;

///

import app.metrodelay.server.Registry;
import app.metrodelay.server.notification.impl.HttpClientNotifier;
import app.metrodelay.server.status.StatusUpdate;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/// @author mvolejnik
///
public class NotifyJob implements Job{
  
  private static final String STORAGE_PATH = "/api";
  private static final Logger l = LogManager.getLogger(NotifyJob.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    l.info("job [{}] started", context.getJobDetail().getKey());
    var statusUpdates = StatusCache.get().list(su -> {
      var d = su.detail();
      return d.valid() || d.end().map(e -> e.isAfter(Instant.now().minus(1, ChronoUnit.DAYS))).orElse(Boolean.FALSE);
    });
    l.info(statusUpdates);
    statusUpdates.stream().forEach(this::notifyServices);
  }

  protected void notifyServices(StatusUpdate statusUpdate) {
    var baseUrl = Registry.serviceRegistry().get(URI.create("urn:metrodelay.app:service:statusupdate:1.0"));
    if (baseUrl.isPresent()) {
      try {
        var serviceUri = new URI(baseUrl.get().toString() + STORAGE_PATH);
        new HttpClientNotifier().send(serviceUri, statusUpdate);
      } catch (URISyntaxException ex) {
        l.error("unable to send {} notification to {}", baseUrl, ex);
      }
    }
  }

}
