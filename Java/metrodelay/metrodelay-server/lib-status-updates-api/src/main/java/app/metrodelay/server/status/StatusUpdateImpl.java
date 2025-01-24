package app.metrodelay.server.status;

import java.net.URL;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * 
 * @author mvolejnik
 *
 */
public record StatusUpdateImpl (
        UUID uuid,
        String title,
        String description,
        String type,
        Collection<String> lines,
        URL link) implements StatusUpdate
  {
  
  public StatusUpdateImpl{
    Objects.requireNonNull(uuid, "UUID is mandatory");
    Objects.requireNonNull(link, "link is mandatory");
  }

  public StatusUpdateImpl(UUID uuid, String title, URL link) {
    this(uuid, title, null, null, Set.of(), link);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(uuid, title, description, type, link, lines);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StatusUpdateImpl other = (StatusUpdateImpl) obj;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (link == null) {
      if (other.link != null)
        return false;
    } else if (!link.equals(other.link))
      return false;
    if (lines == null) {
      if (other.lines != null)
        return false;
    } else if (!lines.equals(other.lines))
      return false;
    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (uuid == null) {
      if (other.uuid != null)
        return false;
    } else if (!uuid.equals(other.uuid))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "StatusUpdateImpl [uuid=" + uuid + ", title=" + title + ", description=" + description + ", type=" + type
        + ", lines=" + lines + ", link=" + link + "]";
  }

}
