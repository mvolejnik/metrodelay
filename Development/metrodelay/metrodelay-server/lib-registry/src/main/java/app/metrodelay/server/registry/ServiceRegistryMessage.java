/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.metrodelay.server.registry;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import jakarta.json.Json;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mvolejnik
 */
public class ServiceRegistryMessage {

  private final URI uri;
  private final URL url;
  private static final String JSON_PROPERTY_SERVICE = "srv";
  private static final String JSON_PROPERTY_URL = "url";
  private static final Logger l = LogManager.getLogger(ServiceRegistryMessage.class);

  public ServiceRegistryMessage(URI uri, URL url) {
    this.uri = uri;
    this.url = url;
  }

  public URI uri() {
    return uri;
  }

  public URL url() {
    return url;
  }

  public String toJson(String action) {
    l.debug("toJson::");
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    Json.createGenerator(os).writeStartObject()
            .writeStartObject(action)
            .write(JSON_PROPERTY_SERVICE, uri.toString())
            .write(JSON_PROPERTY_URL, url.toString())
            .writeEnd()
            .writeEnd()
            .close();
    return os.toString(StandardCharsets.UTF_8);
  }

  public static ServiceRegistryMessage fromJson(String action, String json) throws URISyntaxException, MalformedURLException {
    var reader = Json.createReader(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
    var top = reader.read().asJsonObject();
    var register = top.getJsonObject(action);
    var service = new URI(register.getString(JSON_PROPERTY_SERVICE));
    var url = new URI(register.getString(JSON_PROPERTY_URL)).toURL();
    return new ServiceRegistryMessage(service, url);
  }

}
