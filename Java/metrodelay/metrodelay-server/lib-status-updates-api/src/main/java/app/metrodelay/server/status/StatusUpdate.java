package app.metrodelay.server.status;

import java.net.URI;
import java.util.UUID;

public interface StatusUpdate {

  public UUID uuid();

  public String title();

  public URI link();

}
