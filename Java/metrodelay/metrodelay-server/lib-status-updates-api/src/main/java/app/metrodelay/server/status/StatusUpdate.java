package app.metrodelay.server.status;

import java.net.URI;
import java.util.UUID;

public interface StatusUpdate {

  UUID uuid();

  URI link();
  
  Detail detail();

}
