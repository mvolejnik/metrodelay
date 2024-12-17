/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.metrodelay.server.remoteresources;

import app.metrodelay.server.remoteresources.Resource;
import app.metrodelay.server.remoteresources.ResourceImpl;
import app.metrodelay.server.remoteresources.RemoteResourceException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author mvolejnik
 */
public class ResourceImplTest {
    
    private static final String CONTENT = "test";
    private static final String FINGERPRINT = "fingerprint";
    private static final String SHA_256 = "SHA-256";
    /**
     * Test of content method, of class ResourceImpl.
     */
    
    @Test
    void testContent() throws RemoteResourceException, IOException, NoSuchAlgorithmException {
        Resource r = new ResourceImpl(IOUtils.toInputStream(CONTENT, Charset.forName("UTF-8")));
        var content = r.content();
        assertNotNull(content, "Content should not be null");
        assertEquals(CONTENT, IOUtils.toString(content.get(), Charset.forName("UTF-8")));
    }
    
     @Test
    void testContentNoFingerprint() throws RemoteResourceException, IOException, NoSuchAlgorithmException {
        Resource r = new ResourceImpl(IOUtils.toInputStream(CONTENT, Charset.forName("UTF-8")));
        assertNotNull(r.content(), "Content should not be null");
        assertTrue(r.fingerprint().isEmpty(), "Fingerprint should be empty");
    }
    
    @Test
    void testContentWithFingerprint() throws RemoteResourceException, IOException, NoSuchAlgorithmException {
        Resource r = new ResourceImpl(IOUtils.toInputStream(CONTENT, Charset.forName("UTF-8")), FINGERPRINT);
        assertTrue(r.fingerprint().isPresent(), "Fingerprint should not be empty");
        assertEquals(FINGERPRINT, r.fingerprint().get(), "Fingerprints should equal");
    }
    
     @Test
    void testContentDigest() throws RemoteResourceException, IOException, NoSuchAlgorithmException {
        Resource r = new ResourceImpl(IOUtils.toInputStream(CONTENT, Charset.forName("UTF-8")));
        assertNotNull(r.content(), "Content should not be null");
        assertTrue(r.digest().isPresent(), "Digest should not be null");
        assertArrayEquals(MessageDigest.getInstance(SHA_256).digest(CONTENT.getBytes(Charset.forName("UTF-8"))), r.digest().get(), "Unexpected digest value");
    }

}
