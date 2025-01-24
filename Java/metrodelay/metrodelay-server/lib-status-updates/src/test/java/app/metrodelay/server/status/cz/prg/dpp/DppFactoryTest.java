package app.metrodelay.server.status.cz.prg.dpp;


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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.jupiter.api.Assertions;

public class DppFactoryTest {

	private static final String RSS_DPP_SIMPLE = "src/test/resources/cz.dpp/simple.xml";
	private static final String RSS_DPP_EMPTY = "src/test/resources/cz.dpp/empty.xml";
	private static final String RSS_DPP_GUID_MISSING = "src/test/resources/cz.dpp/guid-missing.xml";
	private static final String RSS_DPP_GUID_EMPTY = "src/test/resources/cz.dpp/guid-empty.xml";
	
	@Test
	@Tag("fast")
	@DisplayName("Parsing RSS - title.")
	public void testStatusUpdatesSimpleTitle() throws StatusUpdateException, IOException {
		DppStatusUpdates dppFactory = new DppStatusUpdates();
		List<StatusUpdate> updates = dppFactory.statusUpdates(FileUtils.openInputStream(new File(RSS_DPP_SIMPLE)));
		StatusUpdate su = updates.get(0);
		assertEquals("Právnická fakulta - Čechův most, resp. Nemocnice Na Františku (oba směry)", su.title(), "Unexpected RSS item title!");
	}
	
	@Test
	@Tag("fast")
	@DisplayName("Parsing RSS - description.")
	public void testStatusUpdatesSimpleDescription() throws StatusUpdateException, IOException {
		DppStatusUpdates dppFactory = new DppStatusUpdates();
		List<StatusUpdate> updates = dppFactory.statusUpdates(FileUtils.openInputStream(new File(RSS_DPP_SIMPLE)));
		StatusUpdate su = updates.get(0);
		assertEquals("", su.description(), "Unexpected RSS item description!");
	}
  
	@Test
	@Tag("fast")
	@DisplayName("Parsing RSS - link")
	public void testStatusUpdatesLink() throws StatusUpdateException, IOException, URISyntaxException {
		DppStatusUpdates dppFactory = new DppStatusUpdates();
		List<StatusUpdate> updates = dppFactory.statusUpdates(FileUtils.openInputStream(new File(RSS_DPP_SIMPLE)));
		StatusUpdate su = updates.get(0);
		assertEquals(new URI("https://www.dpp.cz/omezeni-a-mimoradne-udalosti/detail/26079").toURL(), su.link());
	}
	
	@Test
  @Tag("fast")
  @DisplayName("Empty title.")
	public void testStatusUpdatesEmptyTitle() throws StatusUpdateException, IOException {
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
	}
  
  @Test
  @Tag("fast")
  @DisplayName("Empty GUID")
	public void testStatusUpdatesEmptyGuid() throws StatusUpdateException, IOException {
		DppStatusUpdates dppFactory = new DppStatusUpdates();
		List<StatusUpdate> updates = dppFactory.statusUpdates(FileUtils.openInputStream(new File(RSS_DPP_GUID_EMPTY)));
		StatusUpdate su = updates.get(0);
		assertNotNull(su.uuid(), "Unexpected UUID for empty GUID.");
	}
	
}
