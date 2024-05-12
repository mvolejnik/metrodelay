package app.ptd.server.api.rs;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/status")
public class StatusUpdates {
	
	private static final Logger l = LogManager.getLogger(StatusUpdates.class);
	
	@GET
	@Path("/countries/{country}/cities/{city}/operators/{operator}/lines")
	@Produces("application/json;charset=utf-8")
	public void status(){
		l.debug("status::");
		
	}
	
}
