/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.metrodelay.server.remoteresources.service;

import app.metrodelay.server.remoteresources.RemoteResourceException;
import app.metrodelay.server.remoteresources.Resource;
import app.metrodelay.server.remoteresources.http.HttpResource;
import java.net.URL;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mvolejnik
 */
public class ResourceServiceImpl implements ResourceService {

    private static final Logger l = LogManager.getLogger(ResourceServiceImpl.class);

    @Override
    public Optional<Resource> resource(URL url) throws RemoteResourceException {
        try {
            return new HttpResource().content(url);
        } catch (RemoteResourceException e) {
            l.warn("Unable to get resource.", e);
            throw new RemoteResourceException(String.format("Unable to get resource '%s'", url), e);
        }
    }

}
