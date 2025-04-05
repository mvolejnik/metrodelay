package app.metrodelay.server.status;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;

/// @author mvolejnik
///
/// 
public record StatusUpdateImpl(
        String operatorId,
        UUID uuid,
        URI link,
        Detail detail) implements StatusUpdate, Serializable {

  private static final long serialVersionUID = 1L;

  /*public StatusUpdateImpl{
    Objects.requireNonNull(uuid, "UUID is mandatory");
    Objects.requireNonNull(link, "link is mandatory");
  }*/

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 29 * hash + Objects.hashCode(this.operatorId);
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
    if (!Objects.equals(this.operatorId, other.operatorId)) {
      return false;
    }
    if (!Objects.equals(this.uuid, other.uuid)) {
      return false;
    }
    if (!Objects.equals(this.link, other.link)) {
      return false;
    }
    return Objects.equals(this.detail, other.detail);
  }

  @Override
  public String toString() {
    return "StatusUpdateImpl{" + "operatorId=" + operatorId + ", uuid=" + uuid + ", link=" + link + ", detail=" + detail + '}';
  }

}
