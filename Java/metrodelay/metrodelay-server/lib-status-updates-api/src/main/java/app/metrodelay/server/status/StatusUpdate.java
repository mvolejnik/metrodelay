package app.metrodelay.server.status;

import java.net.URL;
import java.util.Collection;
import java.util.UUID;

public interface StatusUpdate {

  public UUID uuid();

  public String title();

  public String description();

  public String type();

  public Collection<String> lines();

  public URL link();

}
