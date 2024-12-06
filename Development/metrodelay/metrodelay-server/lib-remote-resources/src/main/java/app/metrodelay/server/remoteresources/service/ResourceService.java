/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.metrodelay.server.remoteresources.service;

import app.metrodelay.server.remoteresources.RemoteResourceException;
import java.net.URL;
import java.util.Optional;
import app.metrodelay.server.remoteresources.Resource;

/**
 *
 */
public interface ResourceService {
    
    public Optional<Resource> resource(URL url) throws RemoteResourceException;
    
}
