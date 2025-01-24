/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.metrodelay.server.status;

import app.metrodelay.server.remoteresources.RemoteResourceException;
import app.metrodelay.server.remoteresources.Resource;
import app.metrodelay.server.remoteresources.ResourceImpl;
import app.metrodelay.server.scheduler.CachedItem;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.io.IOUtils;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author mvolejnik
 */
public class ResourceCacheTest {
    
    private static final String ETAG_1 = "ETAG_1" ;
    private static final String ETAG_2 = "ETAG_2" ;
    private static final URL URL_1;
    private static final Resource RESOURCE_WITH_ETAG;
    static {
        try {
            RESOURCE_WITH_ETAG = new ResourceImpl(IOUtils.toInputStream("Resource with etag", Charset.forName("UTF-8")), ETAG_1);
        } catch (RemoteResourceException ex) {
            throw new RuntimeException("Unable to init.", ex);
        }
        try {
            URL_1 = new URL("http://test.url/");
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Unable to init.", ex);
        }
    }
    
    /*
    @Test
    void testResourceCache() throws RemoteResourceException {
        var cache = new ResourceCache<InputStream>();
        var ci_t1 = new CachedItem(IOUtils.toInputStream("content", Charset.forName("UTF-8")), ETAG_1, null);
        cache.resource(URL_1, ci_t1);
        assertTrue(cache.resource(URL_1).isPresent(), "Cache should not be null");
    }
    
    @Test
    void testResourceEtagUpdateChanged() throws RemoteResourceException, NoSuchAlgorithmException {
        var cache = new ResourceCache<InputStream>();
        var ci_t1 = new CachedItem(IOUtils.toInputStream("content", Charset.forName("UTF-8")), ETAG_1, MessageDigest.getInstance("SHA-256").digest("content".getBytes(StandardCharsets.UTF_8)));
        var ci_t2 = new CachedItem(IOUtils.toInputStream("content updated", Charset.forName("UTF-8")), ETAG_2, MessageDigest.getInstance("SHA-256").digest("content updated".getBytes(StandardCharsets.UTF_8)));
        cache.resource(URL_1, ci_t1);
        cache.resource(URL_1, ci_t2);
        assertEquals(ci_t2.fingerprint(), cache.resource(URL_1).get().fingerprint(), "Fingerprint should equals to the newer resource.");
    }
    
    @Test
    void testResourceEtagDoNotUpdateUnchanged() throws RemoteResourceException, IOException {
        var cache = new ResourceCache<InputStream>();
        var content = "content";
        var ci_t1 = new CachedItem(IOUtils.toInputStream(content, Charset.forName("UTF-8")), ETAG_1, null);
        var ci_t2 = new CachedItem(null, ETAG_1, null);
        cache.resource(URL_1, ci_t1);
        cache.resource(URL_1, ci_t2);
        assertEquals(ci_t1.fingerprint(), cache.resource(URL_1).get().fingerprint(), "Fingerpring should equals to the resource.");
        //assertEquals(content, IOUtils.toString(cache.resource(URL_1).get().content() Charset.forName("UTF-8")), "Fingerprint should equals to the resource.");
    } */
    
}
