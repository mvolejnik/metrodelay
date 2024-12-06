/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.ptd.server.registry;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import jakarta.json.Json;
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
    
    
    public String toJson(String action){
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
    
    /*
    private void init(String json){
        l.debug("init::");
        var parser = Json.createParser(new ByteArrayInputStream(json.getBytes()));
        while (parser.hasNext()) {
            var event = parser.next();
            switch (event) {
                case START_ARRAY:
                case END_ARRAY:
                case START_OBJECT:
                case END_OBJECT:
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_TRUE:
                    l.debug("json event: '{}'", event.toString());
                    break;
                case KEY_NAME:
                    l.debug(json);t(event.toString() + " " + parser.getString() + " - ");
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                    System.out.println(event.toString() + " " + parser.getString());
                    break;
            }
        }
    }
    */
    
}
