package app.ptd.server.api.rs;

import static app.ptd.server.management.Metrics.MetricsNames.*;

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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import app.ptd.server.management.Metrics;
import app.ptd.server.model.Cities;
import app.ptd.server.model.Lines;
import app.ptd.server.api.rs.json.JsonIdentifiables;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;

@Path("/transport")
public class Transport {
	
	private static final Timer T_CITIES = Metrics.REGISTRY.timer(MetricRegistry.name(Transport.class, METRICS_TRANSPORT_CITIES.getName()));
	private static final Timer T_LINES = Metrics.REGISTRY.timer(MetricRegistry.name(Transport.class, METRICS_TRANSPORT_LINES.getName()));

	private static final Logger l = LogManager.getLogger(Transport.class);
	
	JsonIdentifiables identifialbes = new JsonIdentifiables();

	@GET
	@Path("/cities")
	@Produces("application/json;charset=utf-8")
	//@Timed(name=METRICS_TRANSPORT_CITIES)
	public Response cities() {
		l.debug("cities::");
		try {
			StreamingOutput stream = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					l.debug("write::");
					final Timer.Context tc = T_CITIES.time();
					try {
						identifialbes.identifiables(output, Cities.PROTOTYPE);//TODO
					} catch (Exception e) {
						l.error("write:: Unspecific Error occured!", e);
						throw new WebApplicationException(e);
					} finally {
						tc.stop();
					}
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
					final Timer.Context tc = T_LINES.time();
					try {
						identifialbes.identifiables(output, Lines.PROTOTYPE);//TODO
					} catch (Exception e) {
						l.error("write:: Unspecific Error occured!", e);
						throw new WebApplicationException(e);
					} finally {
						tc.stop();
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
