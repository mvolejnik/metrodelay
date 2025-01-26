package app.metrodelay.server.status;

import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

public interface StatusUpdate extends Serializable{

  UUID uuid();

  URI link();
  
  Detail detail();

}
