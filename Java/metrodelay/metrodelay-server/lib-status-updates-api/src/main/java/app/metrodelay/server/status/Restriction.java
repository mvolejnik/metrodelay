package app.metrodelay.server.status;

/// @author mvolejnik
///
public enum Restriction {
  DELAY,
  ROUTE_CHANGE,
  STOP_CHANGE,
  STOP_CLOSER,
  OPERATION_SUSPENDED,
  OPERATION_HALTED,
  UNDISPATCHED,
  REPLACEMENT_SERVICE,
  STATION_CLOSED,
  UNKNOWN;

}
