package app.metrodelay.server.status;

///

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotEmpty;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/// @author mvolejnik
///
public record DetailImpl(
  @JsonbProperty("title")
  String title,
  @NotEmpty
  @JsonbProperty("lines")
  List<String> lines,
  @NotEmpty
  @JsonbProperty("types")
  List<Restriction> types,
  @JsonbProperty("start")
  Instant start,
  @JsonbProperty("end")
  Optional<Instant> end,
  @JsonbProperty("validity")
  Validity validity
) implements Detail {

  private static final long serialVersionUID = 1L;

  @JsonbCreator
  public DetailImpl {
  }

  public DetailImpl(String title) {
    this(title, List.of(), List.of(), null, Optional.empty(), Validity.UNKNOWN);
  }

  public DetailImpl(String title, List<String> lines, List<Restriction> types, Instant start, Instant end, Validity validity) {
    this(title, lines, types, start, Optional.ofNullable(end), validity);
  }

  @Override
  public boolean valid() {
    return validity.valid()
      || validity == Validity.FUTURE && Instant.now().isAfter(start)
      || validity == Validity.UNKNOWN;
  }

  @Override
  public boolean valid(Duration gracePeriod) {
    return valid()
      || end().map(e -> e.isAfter(Instant.now().minus(gracePeriod))).orElse(Boolean.FALSE);
  }

  public String getTitle() {
    return title;
  }

  public List<String> getLines() {
    return lines;
  }

  public List<Restriction> getTypes() {
    return types;
  }

  public Instant getStart() {
    return start;
  }

  public Optional<Instant> getEnd() {
    return end;
  }

  public Validity getValidity() {
    return validity;
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
