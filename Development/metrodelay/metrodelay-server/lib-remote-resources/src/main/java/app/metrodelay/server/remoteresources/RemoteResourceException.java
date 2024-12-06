package app.metrodelay.server.remoteresources;

public class RemoteResourceException extends Exception {

  public RemoteResourceException() {
  }

  public RemoteResourceException(String message) {
    super(message);
  }

  public RemoteResourceException(Throwable cause) {
    super(cause);
  }

  public RemoteResourceException(String message, Throwable cause) {
    super(message, cause);
  }

  public RemoteResourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
