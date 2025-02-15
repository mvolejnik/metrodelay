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
import app.metrodelay.server.status.Restriction;
import app.metrodelay.server.status.Validity;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;

///
/// DPP (CZ, PRG) Status updates processes raw remote resources.
///
public class DppStatusUpdates implements OperatorStatusUpdates {

  public static final String OPERATOR_ID = "cz.prg.dpp";
  private static final String UNTIL_FUTHER_NOTICE_EN = "until further notice";
  private static final String UNTIL_FUTHER_NOTICE_CZ = "do odvolání";
  private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy[,]HH:mm").withZone(ZoneId.of("Europe/Prague"));
  private static final Logger l = LogManager.getLogger(DppStatusUpdates.class);

  /// Processes raw remote resource (RSS)
  /// @param rssInputStream RSS input stream
  /// @return list of [StatusUpdate]
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
        var name = ((JAXBElement) attr).getName().getNamespaceURI() + ":" + ((JAXBElement) attr).getName().getLocalPart();
        var value = ((JAXBElement) attr).getValue();
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
            default ->
              l.debug("skips [{}]", name);
          }
        }
      }
    }
    if (uuid == null) {
      l.warn("GUID is null, generating spare UUID");
      uuid = UuidGenerator.generate(Objects.toString(title));
      l.info("Generated UUID [{}]", uuid.toString());
    }
    var update = new StatusUpdateImpl(OPERATOR_ID, uuid, link, new DetailImpl(title));
    l.debug("StatusUpdate [{}]", update);
    return update;
  }

  @Override
  public Optional<StatusUpdate> statusUpdate(InputStream contentInputStream, UUID uuid, URI uri) throws StatusUpdateException {
    try {
      var doc = Jsoup.parse(contentInputStream, StandardCharsets.UTF_8.name(), uri.toString());
      var root = doc.selectXpath("/html/body/div[1]/div[2]");
      var heading = root.select("h1");
      if (heading == null || heading.textNodes() == null || heading.textNodes().isEmpty()) {
        l.warn("unable to select xpath - h1");
        return Optional.empty();
      }
      var title = heading.textNodes().getLast().text();
      l.debug("title '{}'", title);
      var trafficContent = root.select("div.Traffic-content");
      var times = trafficContent.select("time[datetime]");
      if (times == null) {
        l.warn("unable to select xpath - time");
        return Optional.empty();
      }
      var startValue = times.get(0).attributes().get("datetime");
      l.debug("start '{}'", startValue);
      var start = dateTime(startValue);
      Instant end = null;
      if (times.size() > 1) {
        var endValue = StringUtils.trim(times.get(1).attributes().get("datetime"));
        l.debug("end '{}'", endValue);
        if (!UNTIL_FUTHER_NOTICE_CZ.equals(endValue) && !UNTIL_FUTHER_NOTICE_EN.equals(endValue)) {
          end = dateTime(endValue);
        }
      }
      List<Restriction> types = List.of();
      var typeContainer = trafficContent.select("> div.Traffic-content > p");
      var typeHeading = typeContainer.select("strong");
      if (typeHeading.hasText() && typeHeading.text().contains("Typ události")) {
        var typesText = StringUtils.trimToNull(typeContainer.textNodes().getLast().text());
        if (typesText != null) {
          l.debug("types '{}'", typesText);
          types = Stream.of(typesText.split(","))
                  .map(StringUtils::trimToNull)
                  .filter(Objects::nonNull)
                  .map(this::restriction)
                  .distinct()
                  .toList();
        }
      }
      var lines = trafficContent.select("span.Traffic-lineName")
              .eachText()
              .stream()
              .filter(Objects::nonNull)
              .flatMap(t -> Stream.of(t.split("[,\\.\\s]")))
              .filter(StringUtils::isNotBlank)
              .map(String::trim)
              .sorted(linesComparator())
              .toList();
      var statusUpdate = new StatusUpdateImpl(OPERATOR_ID, uuid, uri, new DetailImpl(title, lines, types, start, end, Validity.of(start, end)));
      l.debug("status update '{}'", statusUpdate);
      return Optional.of(statusUpdate);
    } catch (IOException ex) {
      throw new StatusUpdateException(ex);
    }
  }

  public Instant dateTime(String dateTime) {
    try {
      return DATETIME_FORMATTER.parse(dateTime.replaceAll("\\s+", ""), Instant::from);
    } catch (DateTimeParseException dtpe) {
      l.warn("unable to parse date '{}'", dateTime);
      return null;
    }
  }

  private Comparator<String> linesComparator() {
    return (line1, line2) -> {
      if (line1 == null) {
        return line2 == null ? 0 : -1;
      }
      if (line2 == null) {
        return 1;
      }
      if (line1.equals(line2)) {
        return 0;
      }
      var metro1 = line1.matches("[a-zA-Z]+");
      var metro2 = line2.matches("[a-zA-Z]+");
      if (metro1 && metro2) {
        return line1.compareToIgnoreCase(line2);
      } else if (metro1) {
        return 1;
      } else if (metro2) {
        return -1;
      }
      var x1 = line1.matches("[xX]([0-9]+)");
      var x2 = line2.matches("[xX]([0-9]+)");
      if (x1 && x2) {
        return line1.compareToIgnoreCase(line2);
      } else if (x1) {
        return 1;
      } else if (x2) {
        return -1;
      }
      if (NumberUtils.isParsable(line1) && NumberUtils.isParsable(line2)) {
        return Integer.compare(Integer.parseInt(line1), Integer.parseInt(line2));
      } else {
        return line1.compareToIgnoreCase(line2);
      }
    };
  }

  private Restriction restriction(String restriction) {
    return switch (StringUtils.trimToNull(restriction)) {
      case "Zpoždění spoje", "Zpoždění spojů" -> Restriction.DELAY;
      case "Neodjetí spoje" -> Restriction.UNDISPATCHED;
      case "Odklon" -> Restriction.ROUTE_CHANGE;
      case "Zrušení zastávky" -> Restriction.STOP_CLOSER;
      case "Provoz omezen" -> Restriction.OPERATION_SUSPENDED;
      case "Provoz zastaven" -> Restriction.OPERATION_HALTED;
      case "Náhradní doprava" -> Restriction.REPLACEMENT_SERVICE;
      case "Uzavření stanice" -> Restriction.STATION_CLOSED;
      case null, default -> Restriction.UNKNOWN;
    };
  }

}
