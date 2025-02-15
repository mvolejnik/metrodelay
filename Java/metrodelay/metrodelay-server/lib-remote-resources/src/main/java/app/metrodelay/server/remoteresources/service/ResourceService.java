package app.metrodelay.server.remoteresources.service;

import app.metrodelay.server.remoteresources.RemoteResourceException;
import java.net.URL;
import java.util.Optional;
import app.metrodelay.server.remoteresources.Resource;

///  Remote resource downloading service.
public interface ResourceService {
    
    Optional<Resource> resource(URL url, String etag) throws RemoteResourceException;
    
}
