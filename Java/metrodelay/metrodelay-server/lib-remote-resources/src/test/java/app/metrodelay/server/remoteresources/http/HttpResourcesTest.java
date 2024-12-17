package app.metrodelay.server.remoteresources.http;

import app.metrodelay.server.remoteresources.http.HttpResource;
import app.metrodelay.server.remoteresources.RemoteResourceException;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import java.io.InputStream;
import java.net.URL;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@WireMockTest(httpPort = 8040)
public class HttpResourcesTest {

  private static final String MOCK_NOT_MODIFIED = "NOT_MODIFY";
  private static final String NOT_EXISTING_RESOURCE = "/not-existing-resource";
  private static final ZonedDateTime TIME_LAST_DOWNLOADED = ZonedDateTime.parse("2018-01-01T12:00:00.00Z");
  private static final ZonedDateTime TIME_RESOURCE_NOT_UPDATED = ZonedDateTime.parse("2018-01-01T10:00:00.00Z");
  private static final String SCHEME = "http";
  private static final String HOSTNAME = "localhost";
  private static final String URL_BASE = SCHEME + "://" + HOSTNAME + ":%s";
  private static final String URL_PATH_SIMPLE = "/test/simple.json";

  @Test
  public void testRemoteResource(WireMockRuntimeInfo wireMock) throws Exception {
    stubFor(
        get(URL_PATH_SIMPLE)
            .willReturn(
                ok().withBody("""
                                        {"test": "test"}""")));
    try (
        HttpResource httpResource = new HttpResource();
        InputStream is = httpResource.content(new URL(URL_BASE.formatted(wireMock.getHttpPort()) + URL_PATH_SIMPLE)).get().content().get()) {
      byte[] b = new byte[17];
      is.read(b);
      assertEquals("""
                   {"test": "test"}""", new String(b).substring(0, 16), "Unexpected Remote Resource Content.");
    }
  }

  @Test
  void testNotModifyETag(WireMockRuntimeInfo wireMock) throws Exception {
    stubFor(get(URL_PATH_SIMPLE).willReturn(aResponse().withStatus(304)));
    try (HttpResource httpResource = new HttpResource();) {
      assertTrue(httpResource.content(new URL(URL_BASE.formatted(wireMock.getHttpPort()) + URL_PATH_SIMPLE), MOCK_NOT_MODIFIED, null).isEmpty(), "Unmodified resource shouldn't be returned.");
    }
  }

  @Test
  void testNotModifyTime(WireMockRuntimeInfo wireMock) throws Exception {
    stubFor(get(URL_PATH_SIMPLE).willReturn(aResponse().withStatus(304)));
    try (HttpResource httpResource = new HttpResource();) {
      DateTimeFormatter.RFC_1123_DATE_TIME.format(TIME_LAST_DOWNLOADED);
      assertTrue(httpResource.content(new URL(URL_BASE.formatted(wireMock.getHttpPort()) + URL_PATH_SIMPLE), null, TIME_LAST_DOWNLOADED).isEmpty(), "Unmodified resource shouldn't be returned.");
    }
  }

  @Test
  void testNotExistingResource(WireMockRuntimeInfo wireMock) throws Exception {
    stubFor(get(URL_PATH_SIMPLE).willReturn(WireMock.notFound()));
    try (HttpResource httpResource = new HttpResource();) {
      assertThrows(RemoteResourceException.class, () -> httpResource.content(new URL(URL_BASE.formatted(wireMock.getHttpPort()) + NOT_EXISTING_RESOURCE)));
    }
  }

}
