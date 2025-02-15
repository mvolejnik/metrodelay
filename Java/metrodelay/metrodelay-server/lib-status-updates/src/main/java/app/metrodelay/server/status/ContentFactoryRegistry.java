package app.metrodelay.server.status;

import app.metrodelay.server.status.cz.prg.dpp.DppStatusUpdates;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// Operators content factory registry
public class ContentFactoryRegistry {
  
  private static Map<String, Class<? extends OperatorStatusUpdates>> REGISTRY = Map.of(
          DppStatusUpdates.OPERATOR_ID, DppStatusUpdates.class
  );
  
  private static final Logger l = LogManager.getLogger(ContentFactoryRegistry.class);
  
  // Returns operator content factory
  public static OperatorStatusUpdates get(String operatorId){
    var clazz = Optional.ofNullable(REGISTRY.get(operatorId)).orElseThrow(() -> new NoSuchElementException(String.format("No value present for '%s'", operatorId)));
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (NoSuchMethodException|SecurityException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex) {
      throw new IllegalStateException(String.format("ContentFactory does not exist for '%s'", operatorId), ex);
    }
  }
  
}
