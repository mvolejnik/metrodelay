package app.metrodelay.server.model;

public class Line extends AbstractIdentification {
	
	private static final long serialVersionUID = 4596810718689919743L;

	public Line(String code, String name, String localName) {
		super(code, name, localName);
	}
	
	public Line(String code, String name) {
    super(code, name);
  }

	@Override
	public String toString() {
		return "Line [toString()=" + super.toString() + "]";
	}
}
