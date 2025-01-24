/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package app.metrodelay.server.status;

///

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/// @author mvolejnik
///
public class DetailImpl implements Detail{
  
  Collection<String> lines;
  Instant start;
  Instant end;
  Validity validity;

  public DetailImpl(Collection<String> lines, Instant start, Validity validity) {
    this.lines = lines;
    this.start = start;
    this.validity = validity;
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

  public void end(Instant end) {
    this.end = end;
  }

  public void validity(Validity validity) {
    this.validity = validity;
  }

  public Validity validity() {
    return validity;
  }
  
  public void update(Collection<String> lines, Instant start, Validity validity){
    this.lines = lines;
    this.start = start;
    this.validity = validity;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 47 * hash + Objects.hashCode(this.lines);
    hash = 47 * hash + Objects.hashCode(this.start);
    hash = 47 * hash + Objects.hashCode(this.end);
    hash = 47 * hash + Objects.hashCode(this.validity);
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
    return true;
  }

  @Override
  public String toString() {
    return "DetailImpl{" + "lines=" + lines + ", start=" + start + ", end=" + end + ", validity=" + validity + '}';
  }
}
