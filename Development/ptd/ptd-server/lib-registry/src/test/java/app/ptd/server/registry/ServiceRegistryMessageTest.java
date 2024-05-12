/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.ptd.server.registry;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.stream.JsonParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author mvolejnik
 */
public class ServiceRegistryMessageTest {

    private static final String ACTION = "test";
    private static final URI URN;
    private static URL MSG_URL;
    private static final String MULTICAST_ADDRESS = "233.146.53.49";

    static {
        try {
            URN = new URI("urn:service:test");
            MSG_URL = new URL("https://localhost:8443/test/");
        } catch (URISyntaxException | MalformedURLException ex) {
            throw new IllegalArgumentException();
        }
    }


    /**
     * Test of toJson method, of class ServiceRegistryMessage.
     */
    @Test
    public void testToJson() {
        System.out.println("testToJson");
        String expectedJson = "{\"test\":{\"srv\":\"" + URN + "\",\"url\":\"" + MSG_URL + "\"}}";
        assertEquals(expectedJson, new ServiceRegistryMessage(URN, MSG_URL).toJson(ACTION), "Produced JSON is incorrect.");
    }
    
    @Test
    public void testMessageValidity() throws URISyntaxException, MalformedURLException {
        System.out.println("testMessageValidity");
        try {
            JsonParser p = Json.createParser(new ByteArrayInputStream(new ServiceRegistryMessage(URN, MSG_URL).toJson(ACTION).getBytes()));
            while (p.hasNext()) {
                p.next();
            }
        } catch (JsonException e) {
            fail("Produced JSON not proper json object.");
        }
    }
    
}
