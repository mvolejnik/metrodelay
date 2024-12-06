package app.ptd.server.registry;

import java.net.URI;

/**
 *
 * @author mvolejnik
 */
public interface ServiceRegistryClient {
    
    public void register();
    
    public void unregister();
    
    public URI getServiceUri();
    
}
