/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package app.metrodelay.server.scheduler;

import app.metrodelay.server.Registry;
import app.metrodelay.server.notification.impl.HttpClientNotifier;
import app.metrodelay.server.status.StatusUpdate;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/// Status update notification job. Sends notification about ongoing status updates.
public class NotifyJob implements Job{
  
  private static final String STORAGE_PATH = "/api";
  private static final Logger l = LogManager.getLogger(NotifyJob.class);

  @Override
  public void execute(JobExecutionContext context) {
    l.info("job [{}] started", context.getJobDetail().getKey());
    var statusUpdates = StatusCache.get().list(su -> su.detail().valid(Duration.ofDays(1)));
    l.debug(statusUpdates);
    statusUpdates.forEach(this::notifyServices);
    l.info(
      "Notification sent for {} updates, operator(s): {}",
      statusUpdates.size(),
      statusUpdates.stream()
        .map(StatusUpdate::operatorId)
        .distinct()
        .collect(Collectors.joining(","))
    );
  }

  protected void notifyServices(StatusUpdate statusUpdate) {
    var baseUrl = Registry.serviceRegistry().get(URI.create("urn:metrodelay.app:service:statusupdate:1.0"));
    if (baseUrl.isPresent()) {
      try {
        var serviceUri = new URI(baseUrl.get() + STORAGE_PATH);
        new HttpClientNotifier().send(serviceUri, statusUpdate);
      } catch (URISyntaxException ex) {
        l.error("unable to send {} notification to {}", baseUrl, ex);
      }
    }
  }

}
