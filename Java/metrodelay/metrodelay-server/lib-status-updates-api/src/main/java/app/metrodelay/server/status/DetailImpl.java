package app.metrodelay.server.status;

///

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/// @author mvolejnik
///
public class DetailImpl implements Detail{
  
  String title;
  Collection<String> lines;
  Instant start;
  Instant end;
  Validity validity;

  public DetailImpl(String title) {
    this.title = title;
    this.validity = Validity.UNKNWON;
  }
  
  public DetailImpl(String title, Collection<String> lines, Instant start, Validity validity) {
    this.title = title;
    this.lines = lines;
    this.start = start;
    this.validity = validity;
  }
  
  public DetailImpl(String title, Collection<String> lines, Instant start, Instant end, Validity validity) {
    this.title = title;
    this.lines = lines;
    this.start = start;
    this.end = end;
    this.validity = validity;
  }

  @Override
  public String title() {
    return title;
  }
  
  @Override
  public Collection<String> lines() {
    return lines;
  }

  @Override
  public Instant start() {
    return start;
  }

  @Override
  public Optional<Instant> end() {
    return Optional.ofNullable(end);
  }

  public DetailImpl end(Instant end) {
    this.end = end;
    return this;
  }

  public DetailImpl validity(Validity validity) {
    this.validity = validity;
    return this;
  }

  public Validity validity() {
    return validity;
  }
  
  public DetailImpl update(String title, Collection<String> lines, Instant start, Validity validity){
    this.title = title;
    this.lines = lines;
    this.start = start;
    this.validity = validity;
    return this;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + Objects.hashCode(this.title);
    hash = 29 * hash + Objects.hashCode(this.lines);
    hash = 29 * hash + Objects.hashCode(this.start);
    hash = 29 * hash + Objects.hashCode(this.end);
    hash = 29 * hash + Objects.hashCode(this.validity);
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
    final DetailImpl other = (DetailImpl) obj;
    if (!Objects.equals(this.title, other.title)) {
      return false;
    }
    if (!Objects.equals(this.lines, other.lines)) {
      return false;
    }
    if (!Objects.equals(this.start, other.start)) {
      return false;
    }
    if (!Objects.equals(this.end, other.end)) {
      return false;
    }
    return this.validity == other.validity;
  }

  @Override
  public String toString() {
    return "DetailImpl{" + "title=" + title + ", lines=" + lines + ", start=" + start + ", end=" + end + ", validity=" + validity + '}';
  }
  
  
}
