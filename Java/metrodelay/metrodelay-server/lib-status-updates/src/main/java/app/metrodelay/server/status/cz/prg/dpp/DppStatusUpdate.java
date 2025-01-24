/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package app.metrodelay.server.status.cz.prg.dpp;

///

import app.metrodelay.server.status.OperatorStatusUpdate;
import app.metrodelay.server.status.StatusUpdate;
import app.metrodelay.server.status.StatusUpdateException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

/// @author mvolejnik
///
public class DppStatusUpdate implements OperatorStatusUpdate{
  
  private static final String UNTIL_FUTHER_NOTICE = "until further notice";
  

  @Override
  public Optional<StatusUpdate> statusUpdate(InputStream contentInputStream, String guid, String url) throws StatusUpdateException {
    try {
      var doc = Jsoup.parse(contentInputStream, StandardCharsets.UTF_8.name(), url);
      var root = doc.selectXpath("/html/body/div[1]/div[2]");
      var e = root.select("h1");      
      if (e == null || e.textNodes() == null || e.textNodes().isEmpty()){
        return Optional.empty();
      }
      var title = e.textNodes().getLast();
      var trafficContent = root.select("div.Traffic-content");
      var times = trafficContent.select("time[datetime]");
      if (times == null){
        return Optional.empty();
      }
      var startValue = times.get(0).attributes().get("datetime");      
      System.out.println(startValue);
      if (times.size() > 1){
        var endValue = StringUtils.trim(times.get(1).attributes().get("datetime"));
        if (UNTIL_FUTHER_NOTICE.equals(endValue)){
          
        } else {
          endValue.replaceAll("\\s+","");
        }
        
      }
    } catch (IOException ex) {
      throw new StatusUpdateException(ex);
    }
    
    return null;
  }
    

}
