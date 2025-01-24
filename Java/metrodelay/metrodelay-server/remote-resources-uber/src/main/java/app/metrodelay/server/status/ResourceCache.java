/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.metrodelay.server.status;

import app.metrodelay.server.scheduler.CachedItem;
import app.metrodelay.server.scheduler.CachedItemKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mvolejnik
 */
public class ResourceCache<T> {
    
    private static final int CACHE_SIZE_INIT = 100;

    private Map<CachedItemKey, CachedItem<T>> cache = new HashMap<>(CACHE_SIZE_INIT);
    
    private static final Logger l = LogManager.getLogger(ResourceCache.class);

    synchronized public ResourceCache resource(CachedItemKey key, CachedItem<T> item) {
        Objects.nonNull(key);
        Objects.nonNull(item);
        if (cache.get(key) == null
                || !Objects.equals(item.fingerprint(), cache.get(key).fingerprint())
                && !Objects.equals(item.digest(), cache.get(key).digest())) {
            cache.put(key, item);
            l.info("resource updated '{}', fingerprint '{}', digest '{}'", key, item.fingerprint(), item.digest().map(d -> Hex.encodeHexString(d)));
        } else {
            l.debug("not updating resource '{}', fingerprint '{}', digest '{}'", key, item.digest().map(d -> Hex.encodeHexString(d)));
        }
        return this;
    }

    public Optional<CachedItem<T>> resource(String operatorId, UUID uuid) {
        return Optional.ofNullable(cache.get(new CachedItemKey(operatorId, uuid)));
    }

}
