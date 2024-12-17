package app.metrodelay.server.remoteresources;

import java.io.InputStream;
import java.util.Optional;

public interface Resource {

  Optional<InputStream> content();
  
  Optional<String> fingerprint();
  
  Optional<byte[]> digest();

}