package app.metrodelay.server.api.rs;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.StreamingOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.metrodelay.server.model.Cities;
import app.metrodelay.server.model.Lines;
import app.metrodelay.server.api.rs.json.JsonIdentifiables;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;

@Path("/transport")
public class Transport {
	
	private static final Logger l = LogManager.getLogger(Transport.class);
	
	JsonIdentifiables identifialbes = new JsonIdentifiables();

	@GET
	@Path("/cities")
	@Produces("application/json;charset=utf-8")
	//@Timed(name=METRICS_TRANSPORT_CITIES)
	public Response cities() {
		l.debug("cities::");
		try {
			StreamingOutput stream = output -> {
        l.debug("write::");
        try {
          identifialbes.identifiables(output, Cities.PROTOTYPE);//TODO
        } catch (Exception e) {
          l.error("write:: Unspecific Error occured!", e);
          throw new WebApplicationException(e);
        }
      };
			return Response.ok(stream).build();
		} catch (Exception e){
			l.error("Unspecific Error occured!", e);
			return Response.serverError().status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GET
	@Path("/cities/{city}/lines")
	@Produces("application/json;charset=utf-8")
	public Response lines(@PathParam("city") String city) {
		l.debug("lines:: City Code [%s]", city);
		try {
			StreamingOutput stream = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					l.debug("write::");
					try {
						identifialbes.identifiables(output, Lines.PROTOTYPE);//TODO
					} catch (Exception e) {
						l.error("write:: Unspecific Error occured!", e);
						throw new WebApplicationException(e);
					}
				}
			};
			return Response.ok(stream).build();
		} catch (Exception e){
			l.error("lines:: Unspecific Error occured!", e);
			return Response.serverError().status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

  @PUT
	@Path("/cities/{city}")
        @Consumes("application/json;charset=utf-8")
        public Response update(@PathParam("city") String city) {
            l.debug("lines:: City Code [%s]", city);
            //TODO
            return Response.ok().status(Status.CREATED).build();
        }
	
}
