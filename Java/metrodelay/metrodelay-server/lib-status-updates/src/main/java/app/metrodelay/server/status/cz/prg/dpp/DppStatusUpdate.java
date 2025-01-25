/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package app.metrodelay.server.status.cz.prg.dpp;

///

import app.metrodelay.server.status.DetailImpl;
import app.metrodelay.server.status.OperatorStatusUpdate;
import app.metrodelay.server.status.StatusUpdate;
import app.metrodelay.server.status.StatusUpdateException;
import app.metrodelay.server.status.StatusUpdateImpl;
import app.metrodelay.server.status.UuidGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;

/// @author mvolejnik
///
public class DppStatusUpdate implements OperatorStatusUpdate{
  
  private static final String UNTIL_FUTHER_NOTICE = "until further notice";
  private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy[,]HH:ss");
  private static final Logger l = LogManager.getLogger(DppStatusUpdate.class);

  @Override
  public Optional<StatusUpdate> statusUpdate(InputStream contentInputStream, String guid, URI uri) throws StatusUpdateException {
    try {
      var doc = Jsoup.parse(contentInputStream, StandardCharsets.UTF_8.name(), uri.toString());
      var root = doc.selectXpath("/html/body/div[1]/div[2]");
      var e = root.select("h1");      
      if (e == null || e.textNodes() == null || e.textNodes().isEmpty()){
        return Optional.empty();
      }
      var title = e.textNodes().getLast().text();
      var trafficContent = root.select("div.Traffic-content");
      var times = trafficContent.select("time[datetime]");
      if (times == null){
        return Optional.empty();
      }
      var startValue = times.get(0).attributes().get("datetime");      
      l.debug("start '{}'", startValue);
      var start = dateTime(startValue);
      Instant end = null;
      if (times.size() > 1){
        var endValue = StringUtils.trim(times.get(1).attributes().get("datetime"));
        if (!UNTIL_FUTHER_NOTICE.equals(endValue)){
          end = dateTime(endValue);
        }
      }
      return Optional.of(new StatusUpdateImpl(UuidGenerator.generate(guid), uri, new DetailImpl(title, null, start, null)));
    } catch (IOException ex) {
      throw new StatusUpdateException(ex);
    }
  }
  
  public Instant dateTime(String dateTime){
    try {
      return DATETIME_FORMATTER.parse(dateTime.replaceAll("\\s+",""), Instant::from);
    } catch (DateTimeParseException dtpe){
      l.warn("unable to parse date '{}'", dateTime);
      return null;
    }
  }
    

}
