/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.metrodelay.server.scheduler;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author mvolejnik
 */
public class CachedItemKey implements Serializable{
  
  static final long serialVersionUID = 1L;
  
  private String operatorId;
  
  private UUID uuid;

  public CachedItemKey(String operatorId, UUID uuid) {
    this.operatorId = operatorId;
    this.uuid = uuid;
  }

  public String operatorId() {
    return operatorId;
  }

  public UUID uuid() {
    return uuid;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 79 * hash + Objects.hashCode(this.operatorId);
    hash = 79 * hash + Objects.hashCode(this.uuid);
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
    final CachedItemKey other = (CachedItemKey) obj;
    if (!Objects.equals(this.operatorId, other.operatorId)) {
      return false;
    }
    return Objects.equals(this.uuid, other.uuid);
  }
  
}
