package app.metrodelay.server.status;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 
 * @author mvolejnik
 *
 */
public record StatusUpdateImpl (
        UUID uuid,
        URI link,
        Detail detail) implements StatusUpdate
  {
  
  public StatusUpdateImpl{
    Objects.requireNonNull(uuid, "UUID is mandatory");
    Objects.requireNonNull(link, "link is mandatory");
  }
  
  public StatusUpdateImpl(UUID uuid, String title, URI link, Instant start, List lines) {
    this(uuid, link, new DetailImpl(title, lines, start, Validity.of(start)));
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 29 * hash + Objects.hashCode(this.uuid);
    hash = 29 * hash + Objects.hashCode(this.link);
    hash = 29 * hash + Objects.hashCode(this.detail);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final StatusUpdateImpl other = (StatusUpdateImpl) obj;
    return true;
  }

  @Override
  public String toString() {
    return "StatusUpdateImpl{" + "uuid=" + uuid + ", link=" + link + ", detail=" + detail + '}';
  }

}
