package app.metrodelay.server.model;

public class Country extends AbstractIdentification{

	private static final long serialVersionUID = 730044445369836655L;

	public Country(String code, String name, String localName) {
		super(code, name, localName);
	}
	
	public Country(String code, String name) {
    super(code, name);
  }
	
	@Override
	public String toString() {
		return "City [toString()=" + super.toString() + "]";
	}
	
}
