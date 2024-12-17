package app.metrodelay.server.model;

public class City extends AbstractIdentification {

	private static final long serialVersionUID = 7945273030682082852L;

	public City(String code, String name, String localName) {
		super(code, name, localName);
	}
	
	public City(String code, String name) {
    super(code, name);
  }
	
	@Override
	public String toString() {
		return "City [toString()=" + super.toString() + "]";
	}

}
