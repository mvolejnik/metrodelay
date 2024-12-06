package app.metrodelay.server.model;

public class Operator extends AbstractIdentification {
	
	private static final long serialVersionUID = 4596810718689919743L;

	public Operator(String code, String name, String localName) {
    super(code, name, localName);
  }
	
	public Operator(String code, String name) {
		super(code, name);
	}

	@Override
	public String toString() {
		return "Operator [toString()=" + super.toString() + "]";
	}
}