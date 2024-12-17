package app.metrodelay.server.remoteresources;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceImpl implements Resource, Closeable {

    private static final String MESSAGE_DIGEST = "SHA-256";
    
    private final Optional<InputStream> content;

    private Optional<String> fingerprint;
    
    private byte[] digest;
    
    private boolean read;
    
    private static final Logger l = LogManager.getLogger(ResourceImpl.class);
    

    public ResourceImpl(InputStream content) throws RemoteResourceException {
        this(content, null);
    }

    public ResourceImpl(InputStream content, String fingerprint) throws RemoteResourceException {
        this.fingerprint = Optional.ofNullable(fingerprint);
        try {
            this.content = initContent(content);
        } catch (NoSuchAlgorithmException | IOException ex) {
            l.error("Unable to instantiate", ex);
            throw new RemoteResourceException("Unable to instantiate", ex);
        }
    }

    @Override
    public synchronized Optional<InputStream> content() {
        if (read){
            throw new IllegalStateException("Resource already read.");
        } else {
            read = true;
            return content;
        }
    }

    public Optional<String> fingerprint() {
        return fingerprint;
    }

    public Resource fingerprint(String fingerprint) {
        this.fingerprint = Optional.ofNullable(fingerprint);
        return this;
    }
    
    @Override
    public void close() throws IOException {
        if (content.isPresent()) {
            content.get().close();
        }
    }

    @Override
    public Optional<byte[]> digest() {
        return Optional.ofNullable(this.digest);
    }
    
    private Optional<InputStream> initContent(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        if (Objects.isNull(inputStream)){
            return Optional.empty();
        } else {
            var digestIs = new DigestInputStream(inputStream, MessageDigest.getInstance(MESSAGE_DIGEST));
            var is = IOUtils.toBufferedInputStream(digestIs);
            this.digest = digestIs.getMessageDigest().digest();
            l.debug("InputStream digest computed '{}'", Base64.encodeBase64String(this.digest));
            return Optional.of(is);
        }
    }

}
