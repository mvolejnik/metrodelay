package app.metrodelay.server.scheduler;

import app.metrodelay.server.remoteresources.ResourceImpl;
import java.io.Serializable;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CachedItem<T>  implements Serializable{
  
  static final long serialVersionUID = 1L;

  private T content;

  private String fingerprint;

  private byte[] digest;

  private static final Logger l = LogManager.getLogger(ResourceImpl.class);

  public CachedItem(T content, String fingerprint, byte[] digest) {
    this.content = content;
    this.fingerprint = fingerprint;
    this.digest = digest;
  }

  public T content() {
    return content;
  }

  public Optional<String> fingerprint(){
    return Optional.ofNullable(fingerprint);
  }

  public Optional<byte[]> digest(){
    return Optional.ofNullable(digest);
  }
}
