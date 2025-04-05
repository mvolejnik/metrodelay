package app.metrodelay.server.storage.rs;

import app.metrodelay.server.status.StatusUpdate;
import app.metrodelay.server.status.StatusUpdateImpl;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;

/**
 *
 * @author mvolejnik
 */
@Path("/")
public class Status {
  
  private static final Logger l = LogManager.getLogger(Status.class);
  
  private static Cache<UUID, StatusUpdate> statusUpdates; 
	
	@PUT
  @Consumes(MediaType.APPLICATION_JSON)
	@Path("/updates/{guid}")
	public void status( @PathParam("guid") String guid, InputStream statusUpdate){
		l.debug("status:: {}", guid);
    Jsonb jsonb = JsonbBuilder.create();
    var update = jsonb.fromJson(statusUpdate, StatusUpdateImpl.class);
    l.debug("status:: {}", update);
    statusUpdates.put(UUID.fromString(guid), update);
	}
  
  public static void initCache(Cache<UUID, StatusUpdate> statusUpdates){
    Status.statusUpdates = statusUpdates;
  }
  
}
