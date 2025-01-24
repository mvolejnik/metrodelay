/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package app.metrodelay.server.status;

///

import java.time.Instant;

/// @author mvolejnik
///
public enum Validity {
  FUTURE(false),
  ONGOING(true),
  PAST(false),
  UNKNWON(false);
  
  final boolean status;

  private Validity(boolean status) {
    this.status = status;
  }

  public boolean status() {
    return status;
  }  
  
  public static Validity of(Instant start){
    return switch (start){
      case null -> ONGOING;
      case Instant s when Instant.now().isBefore(start) -> FUTURE;
      default -> ONGOING;
    };
  }
}
