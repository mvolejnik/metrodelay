/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package app.metrodelay.server.status;

///

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/// @author mvolejnik
///
public interface Detail extends Serializable{
  
  String title();
  
  List<String> lines();

  Instant start();
  
  Optional<Instant> end();
  
  Validity validity();  
  
  boolean valid();
  
  boolean valid(Duration gracePeriod);
  
  List<Restriction> types();
  
}
