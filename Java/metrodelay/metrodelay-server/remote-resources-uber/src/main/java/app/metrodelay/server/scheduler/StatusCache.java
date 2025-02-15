/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package app.metrodelay.server.scheduler;

///

import app.metrodelay.server.status.StatusUpdate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.ehcache.Cache;
import org.ehcache.spi.loaderwriter.BulkCacheWritingException;
import org.ehcache.spi.loaderwriter.CacheLoadingException;
import org.ehcache.spi.loaderwriter.CacheWritingException;

/// @author mvolejnik
///
public class StatusCache {
  
  private final Cache<UUID, StatusUpdate> statusCache;
  
  private static StatusCache instance;

  private StatusCache(Cache<UUID, StatusUpdate> statusCache) {
    this.statusCache = statusCache;
  }
  
  public static void init(Cache<UUID, StatusUpdate> statusCache){
    if (instance != null){
      throw new IllegalStateException("Status cache already initialized.");
    }
    instance = new StatusCache(statusCache);
  }
  
  public static StatusCache get(){
    if (instance == null){
      throw new IllegalStateException("Status cache not initialized.");
    }
    return instance;    
  }

  public StatusUpdate get(UUID k) throws CacheLoadingException {
    return statusCache.get(k);
  }

  public void put(UUID k, StatusUpdate v) throws CacheWritingException {
    statusCache.put(k, v);
  }

  public void putAll(Map<? extends UUID, ? extends StatusUpdate> map) throws BulkCacheWritingException {
    statusCache.putAll(map);
  }

  public List<StatusUpdate> list(Predicate<StatusUpdate> predicate) {
    var sci = statusCache.iterator();
    return Stream.generate(() -> null)
            .takeWhile(i -> sci.hasNext())
            .map(i -> sci.next())
            .filter(Objects::nonNull)
            .map(entry -> entry.getValue())
            .toList();
  }

}
