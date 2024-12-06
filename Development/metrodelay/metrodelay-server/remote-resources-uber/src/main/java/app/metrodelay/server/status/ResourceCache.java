/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.metrodelay.server.status;

import app.metrodelay.server.remoteresources.Resource;
import app.metrodelay.server.scheduler.GetUrlResourceJob;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mvolejnik
 */
public class ResourceCache {
    
    private static final int CACHE_SIZE_INIT = 100;

    private Map<URL, Resource> cache = new HashMap<>(CACHE_SIZE_INIT);
    
    private static final Logger l = LogManager.getLogger(ResourceCache.class);

    synchronized public ResourceCache resource(URL url, Resource resource) {
        Objects.nonNull(url);
        Objects.nonNull(resource);
        if (cache.get(url) == null
                || !resource.fingerprint().equals(cache.get(url).fingerprint())
                && !resource.digest().equals(cache.get(url).digest())) {
            cache.put(url, resource);
            l.debug("Resource updated '{}'", url);
        } else {
            l.debug("Not updating resource '{}'", url);
        }
        return this;
    }

    public Optional<Resource> resource(URL url) {
        return Optional.ofNullable(cache.get(url));
    }

}
