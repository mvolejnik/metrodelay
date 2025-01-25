/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package app.metrodelay.server.status.cz.prg.dpp;

import java.io.File;
import java.net.URI;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author mvolejnik
 */
public class DppStatusUpdateTest {
  
  private static final String HTML_DPP_SIMPLE = "src/test/resources/cz.dpp/26104.html";
  

  @Test
  public void testStatusUpdates() throws Exception {
    var dpp = new DppStatusUpdate();
    var statusUpdate = dpp.statusUpdate(FileUtils.openInputStream(new File(HTML_DPP_SIMPLE)), "26079", new URI("https://dummy.url/"));
    assertNotNull(statusUpdate);
  }
  
}
