package app.metrodelay.server.storage.rs;

import app.metrodelay.server.status.StatusUpdate;
import app.metrodelay.server.status.StatusUpdateImpl;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;

///  Status storage API to register status alert or check existing one.
@Path("/")
public class Status {

  private static final Logger l = LogManager.getLogger(Status.class);

  private static Cache<UUID, StatusUpdate> statusUpdates;

  private static final Jsonb jsonb = JsonbBuilder.create();
  private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private static final Validator validator = factory.getValidator();

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/updates/{guid}")
  public void status(@PathParam("guid") String guid, InputStream statusUpdate) {
    l.debug("updates '{}'", guid);
    l.trace("updates '{}'", statusUpdate);
    var update = jsonb.fromJson(statusUpdate, StatusUpdateImpl.class);
    Set<ConstraintViolation<StatusUpdateImpl>> violations = validator.validate(update);
    if (!violations.isEmpty()) {
      String message = violations.stream()
        .map(v -> v.getPropertyPath() + " " + v.getMessage())
        .collect(Collectors.joining(", "));
      l.info("Bad request for '{}'", guid);
      throw new BadRequestException("Validation failed: " + message);
    }

    UUID uuid;
    try {
      uuid = UUID.fromString(guid);
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException("Invalid UUID '%s'".formatted(guid));
    }
    var cached = statusUpdates.get(uuid);
    if (cached == null || !cached.equals(update)) {
      l.info("New or updated status update received '{}': '{}'", uuid, update);
      statusUpdates.put(uuid, update);
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/updates/{guid}")
  public String status(@PathParam("guid") String guid) {
    l.debug("status:: {}", guid);
    return jsonb.toJson(statusUpdates.get(UUID.fromString(guid)));
  }

  public static void initCache(Cache<UUID, StatusUpdate> statusUpdates) {
    Status.statusUpdates = statusUpdates;
  }

}
