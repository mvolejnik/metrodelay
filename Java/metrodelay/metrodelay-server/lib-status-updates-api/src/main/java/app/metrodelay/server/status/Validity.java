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
  
  final boolean valid;

  private Validity(boolean status) {
    this.valid = status;
  }

  public boolean valid() {
    return valid;
  }  
  
  public static Validity of(Instant start){
    return of(start, null);
  }
  
  public static Validity of(Instant start, Instant end){
    if (end != null && end.isBefore(Instant.now())){
      return PAST;
    }
    return switch (start){
      case null -> ONGOING;
      case Instant s when Instant.now().isBefore(start) -> FUTURE;
      default -> ONGOING;
    };
  }
}
