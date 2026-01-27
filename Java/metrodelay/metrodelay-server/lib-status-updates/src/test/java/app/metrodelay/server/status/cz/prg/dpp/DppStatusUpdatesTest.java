package app.metrodelay.server.status.cz.prg.dpp;


import app.metrodelay.server.status.Restriction;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import app.metrodelay.server.status.StatusUpdate;
import app.metrodelay.server.status.StatusUpdateException;
import app.metrodelay.server.status.UuidGenerator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

public class DppStatusUpdatesTest {

	private static final String RSS_DPP_SIMPLE = "src/test/resources/cz.dpp/simple.xml";
	private static final String RSS_DPP_EMPTY = "src/test/resources/cz.dpp/empty.xml";
	private static final String RSS_DPP_GUID_MISSING = "src/test/resources/cz.dpp/guid-missing.xml";
	private static final String RSS_DPP_GUID_EMPTY = "src/test/resources/cz.dpp/guid-empty.xml";
  private static final String HTML_DPP_SIMPLE = "src/test/resources/cz.dpp/26104.html";
  private static final String HTML_DPP_LINES = "src/test/resources/cz.dpp/26130.html";
  
	@Test
	@Tag("fast")
	@DisplayName("Parsing RSS - link")
	public void testStatusUpdatesLink() throws StatusUpdateException, IOException, URISyntaxException {
		DppStatusUpdates dppFactory = new DppStatusUpdates();
		List<StatusUpdate> updates = dppFactory.statusUpdates(FileUtils.openInputStream(new File(RSS_DPP_SIMPLE)));
		StatusUpdate su = updates.get(0);
		assertEquals(new URI("https://www.dpp.cz/omezeni-a-mimoradne-udalosti/detail/26079"), su.link());
	}
	
	@Test
  @Tag("fast")
  @DisplayName("Empty link.")
  @Disabled
	public void testStatusUpdatesEmptyLink() throws StatusUpdateException, IOException {
		DppStatusUpdates dppFactory = new DppStatusUpdates();
		var ex = Assertions.assertThrows(NullPointerException.class, () -> dppFactory.statusUpdates(FileUtils.openInputStream(new File(RSS_DPP_EMPTY))));
    assertEquals("link is mandatory", ex.getMessage());
	}
			
	@Test
  @Tag("fast")
  @DisplayName("Missing GUID")
	public void testStatusUpdatesMissingGuid() throws StatusUpdateException, IOException {
		DppStatusUpdates dppFactory = new DppStatusUpdates();
		List<StatusUpdate> updates = dppFactory.statusUpdates(FileUtils.openInputStream(new File(RSS_DPP_GUID_MISSING)));
		StatusUpdate su = updates.get(0);
		assertNotNull(su.uuid(), "Unexpected UUID for missing GUID.");
    assertEquals(UUID.fromString("f2efc90b-7e32-6639-f0ca-9bee1c002cc6"), su.uuid());
	}
  
  @Test
  @Tag("fast")
  @DisplayName("Empty GUID")
	public void testStatusUpdatesEmptyGuid() throws StatusUpdateException, IOException {
		DppStatusUpdates dppFactory = new DppStatusUpdates();
		List<StatusUpdate> updates = dppFactory.statusUpdates(FileUtils.openInputStream(new File(RSS_DPP_GUID_EMPTY)));
		StatusUpdate su = updates.get(0);
		assertNotNull(su.uuid(), "Unexpected UUID for empty GUID.");
    assertEquals(UUID.fromString("f2efc90b-7e32-6639-f0ca-9bee1c002cc6"), su.uuid());
	}
  
  @Test
  @Tag("fast")
  @DisplayName("Simple HTML")
  public void testStatusUpdates() throws Exception {
    var dpp = new DppStatusUpdates();
    var statusUpdate = dpp.statusUpdate(FileUtils.openInputStream(new File(HTML_DPP_SIMPLE)), UuidGenerator.generate("26079"), new URI("https://dummy.url/"));
    assertNotNull(statusUpdate);
  }
  
  @Test
  @Tag("fast")
  @DisplayName("HTML - lines")
  public void testStatusUpdatesLines() throws Exception {
    var dpp = new DppStatusUpdates();
    var statusUpdate = dpp.statusUpdate(FileUtils.openInputStream(new File(HTML_DPP_LINES)), UuidGenerator.generate("26130"), new URI("https://dummy.url/"));
    assertNotNull(statusUpdate);
    assertNotNull(statusUpdate.get().detail());
    assertNotNull(statusUpdate.get().detail().lines());
    assertEquals(6, statusUpdate.get().detail().lines().size());
    assertEquals(List.of(Restriction.DELAY, Restriction.OPERATION_SUSPENDED), statusUpdate.get().detail().types());
    assertEquals(List.of("22", "26", "175", "177", "181", "182"), statusUpdate.get().detail().lines());
  }
	
}
