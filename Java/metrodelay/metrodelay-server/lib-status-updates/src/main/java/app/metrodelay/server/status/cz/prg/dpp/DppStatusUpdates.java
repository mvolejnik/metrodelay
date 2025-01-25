package app.metrodelay.server.status.cz.prg.dpp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.xml.bind.JAXBElement;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.metrodelay.server.status.StatusUpdate;
import app.metrodelay.server.status.StatusUpdateException;
import app.metrodelay.server.status.StatusUpdateImpl;
import app.metrodelay.server.status.UuidGenerator;
import app.metrodelay.libs.rss.Rss;
import app.metrodelay.libs.rss.RssException;
import app.metrodelay.libs.rss.impl.Rss20Impl;
import app.metrodelay.libs.rss.jaxb.rss20.Guid;
import app.metrodelay.libs.rss.jaxb.rss20.RssItem;
import app.metrodelay.server.status.DetailImpl;
import java.util.Objects;
import app.metrodelay.server.status.OperatorStatusUpdates;
import java.net.URI;
import java.net.URISyntaxException;

public class DppStatusUpdates implements OperatorStatusUpdates{

  private static final Logger l = LogManager.getLogger(DppStatusUpdates.class);

  public List<StatusUpdate> statusUpdates(InputStream rssInputStream) throws StatusUpdateException {
    l.info("statusUpdates::");
    Rss rss;
    List<RssItem> items;
    List<StatusUpdate> statusUpdates = new ArrayList<>();
    try {
      rss = new Rss20Impl(rssInputStream);
    } catch (RssException e) {
      l.error("statusUpdates::");
      throw new StatusUpdateException("Unable to parse RSS!", e);
    }

    items = rss.getRss().getChannel().getItem();
    for (RssItem item : items) {
      statusUpdates.add(parseStatusUpdate(item));
    }
    return statusUpdates;
  }

  private StatusUpdate parseStatusUpdate(RssItem rss) throws StatusUpdateException {
    String title = null;
    String type = null;
    URI link = null;
    UUID uuid = null;
    List<Object> attrs = rss.getTitleOrDescriptionOrLink();
    for (Object attr : attrs) {
      if (attr instanceof JAXBElement<?>) {
        JAXBElement e = (JAXBElement) attr;
        Class clazz = ((JAXBElement) attr).getDeclaredType();
        l.trace("Attribute of type [{}].", clazz);
        String name = ((JAXBElement) attr).getName().getNamespaceURI() + ":"
            + ((JAXBElement) attr).getName().getLocalPart();
        Object value = ((JAXBElement) attr).getValue();
        if (value != null) {
          switch (name) {
          case ":guid" -> {
            l.debug("Processing GUID");
            Guid guid = (Guid) value;
            l.debug("GUID [{}]", guid.getValue());
            if (!guid.getValue().isEmpty() && guid.getValue().length() > 0) {
              uuid = UuidGenerator.generate(guid.getValue());
            } else {
              l.warn("Item's GUID is empty.");
            }
            }
          case ":title" -> {
            l.debug("Processing title");
            title = value.toString().trim();
            if (StringUtils.isEmpty(title)) {
              l.info("Item's title is empty.");
            }
            }
          case ":link" -> {
            String linkValue = value.toString();
            if (StringUtils.isNotEmpty(linkValue)) {
              try {
                link = new URI(linkValue);
              } catch (URISyntaxException e1) {
                l.warn("Unable to parse link [{}]", linkValue);
              }
            }
            }
          default -> l.debug("unable to parse [{}]", name);
          }
        }
      }
    }
    if (uuid == null) {
      l.warn("GUID is null, generating spare UUID");
      uuid = UuidGenerator.generate(Objects.toString(title));
      l.info("Generated UUID [{}]", uuid.toString());
    }
    var update = new StatusUpdateImpl(uuid, link, new DetailImpl(title));
    l.debug("StatusUpdate [{}]", update);
    return update;
  }

}

