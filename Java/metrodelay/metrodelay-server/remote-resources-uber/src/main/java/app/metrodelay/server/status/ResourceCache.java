/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.metrodelay.server.status;

import app.metrodelay.server.scheduler.CachedItem;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mvolejnik
 */
public class ResourceCache<T> {
    
    private static final int CACHE_SIZE_INIT = 100;

    private Map<URL, CachedItem<T>> cache = new HashMap<>(CACHE_SIZE_INIT);
    
    private static final Logger l = LogManager.getLogger(ResourceCache.class);

    synchronized public ResourceCache resource(URL url, CachedItem<T> item) {
        Objects.nonNull(url);
        Objects.nonNull(item);
        if (cache.get(url) == null
                || !Objects.equals(item.fingerprint(), cache.get(url).fingerprint())
                && !Objects.equals(item.digest(), cache.get(url).digest())) {
            cache.put(url, item);
            l.info("resource updated '{}', fingerprint '{}', digest '{}'", url, item.fingerprint(), item.digest().map(d -> Hex.encodeHexString(d)));
        } else {
            l.debug("not updating resource '{}', fingerprint '{}', digest '{}'", url, item.digest().map(d -> Hex.encodeHexString(d)));
        }
        return this;
    }

    public Optional<CachedItem<T>> resource(URL url) {
        return Optional.ofNullable(cache.get(url));
    }

}
