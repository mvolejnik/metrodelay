package app.metrodelay.server.json;

import jakarta.json.bind.adapter.JsonbAdapter;

public class EnumAdapter implements JsonbAdapter<Enum<?>, String> {

  @Override
  public String adaptToJson(Enum<?> e) {
    return e == null ? null : e.name();
  }

  @Override
  public Enum<?> adaptFromJson(String s) {
    throw new UnsupportedOperationException("Define @JsonbCreator static method instead");
  }
}