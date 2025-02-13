/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package app.metrodelay.server.status;

/// @author mvolejnik
///
public enum Restriction {
  DELAY,
  ROUTE_CHANGE,
  STOP_CHANGE,
  STOP_CLOSER,
  OPERATION_SUSPENDED,
  UNDISPATCHED,
  REPLACEMENT_SERVICE,
  UNKNOWN;
}
