/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.ptd.server.registry;

import java.net.URI;
import java.net.URL;
import java.util.Optional;

/**
 *
 * @author mvolejnik
 */
public interface ServiceRegistry {

    public Optional<URL> get(URI serviceUri);

}
