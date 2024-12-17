package app.metrodelay.server.model;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;

@SuppressWarnings(value = "serial")
public abstract class AbstractIdentification implements Serializable, Identifiable {

  @NotNull
  protected final String code;
  @NotNull
  protected final String name;
  @NotNull
  protected final String localName;

  public AbstractIdentification(String code, String name, String localName) {
    super();
    this.code = code;
    this.name = name;
    this.localName = localName;
  }

  public AbstractIdentification(String code, String name) {
    super();
    this.code = code;
    this.name = name;
    this.localName = null;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getLocalName() {
    return localName;
  }

  @Override
  public String toString() {
    return "AbstractIdentification [code=" + code + ", name=" + name + ", localName=" + localName + "]";
  }

  @Override
  public int compareTo(Identifiable o) {
    int result = this.code.compareTo(o.getCode());
    if (result == 0) {
      result = this.name.compareTo(o.getName());
    }
    if (result == 0) {
      result = this.localName.compareTo(o.getLocalName());
    }
    return result;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((code == null) ? 0 : code.hashCode());
    result = prime * result + ((localName == null) ? 0 : localName.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractIdentification other = (AbstractIdentification) obj;
    if (code == null) {
      if (other.code != null)
        return false;
    } else if (!code.equals(other.code))
      return false;
    if (localName == null) {
      if (other.localName != null)
        return false;
    } else if (!localName.equals(other.localName))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}