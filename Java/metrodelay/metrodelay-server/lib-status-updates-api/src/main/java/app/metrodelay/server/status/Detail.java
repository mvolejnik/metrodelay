/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package app.metrodelay.server.status;

///

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

/// @author mvolejnik
///
public interface Detail {
  
  String title();
  
  Collection<String> lines();

  Instant start();
  
  Optional<Instant> end();
  
  Validity validity();  
  
}
