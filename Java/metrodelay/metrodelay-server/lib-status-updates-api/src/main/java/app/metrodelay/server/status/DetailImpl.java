package app.metrodelay.server.status;

///

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/// @author mvolejnik
///
public class DetailImpl implements Detail{
  
  private static final long serialVersionUID = 1L;
  
  String title;
  List<String> lines;
  Instant start;
  Instant end;
  Validity validity;
  List<Restriction> types;

  public DetailImpl(String title) {
    this.title = title;
    this.validity = Validity.UNKNWON;
  }
  
  public DetailImpl(String title, List<String> lines, Instant start, Validity validity) {
    this.title = title;
    this.lines = lines;
    this.start = start;
    this.validity = validity;
  }
  
  public DetailImpl(String title, List<String> lines, List<Restriction> types, Instant start, Instant end, Validity validity) {
    this.title = title;
    this.lines = lines;
    this.start = start;
    this.end = end;
    this.validity = validity;
    this.types = types;
  }

  @Override
  public String title() {
    return title;
  }
  
  @Override
  public List<String> lines() {
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

  @Override
  public boolean valid() {
    return validity.valid()
            || validity == Validity.FUTURE && Instant.now().isAfter(start)
            || validity == Validity.UNKNWON;
  }

  @Override
  public List<Restriction> types() {
    return types;
  }

  public Detail type(List<Restriction> types) {
    this.types = types;
    return this;
  }

  public DetailImpl update(String title, List<String> lines, Instant start, Validity validity, List<Restriction> types){
    this.title = title;
    this.lines = lines;
    this.start = start;
    this.validity = validity;
    this.types = types;
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
    hash = 29 * hash + Objects.hashCode(this.types);
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
    if (!Objects.equals(this.types, other.types)) {
      return false;
    }
    return this.validity == other.validity;
  }

  @Override
  public String toString() {
    return "DetailImpl{" + "title=" + title + ", lines=" + lines + ", start=" + start + ", end=" + end + ", validity=" + validity + ", types=" + types + '}';
  }
  
  
}
