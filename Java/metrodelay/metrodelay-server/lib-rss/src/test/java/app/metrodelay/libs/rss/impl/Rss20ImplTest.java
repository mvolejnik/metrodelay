package app.metrodelay.libs.rss.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import app.metrodelay.libs.rss.RssException;
import app.metrodelay.libs.rss.impl.Rss20Impl;
import app.metrodelay.libs.rss.jaxb.rss20.Rss;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Rss20ImplTest {
	
	private static String RSS_CZ_DPP_1 = "src/test/resources/dpp.cz/1.xml" ;

	@Test
	@Tag("fast")
	@DisplayName("Test RSS parsing.")
	public void testGetRss() throws IOException, RssException {
		InputStream rssIs = FileUtils.openInputStream(new File(RSS_CZ_DPP_1));
		app.metrodelay.libs.rss.Rss rssReader = new Rss20Impl(rssIs);
		Rss rss = rssReader.getRss();
		int itemsSize = rss.getChannel().getItem().size();
		assertEquals(423, itemsSize, "Unexpected no. of items (title, link, description)!");
	}

}
