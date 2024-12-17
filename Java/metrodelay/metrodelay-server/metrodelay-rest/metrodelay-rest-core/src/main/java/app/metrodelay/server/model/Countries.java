package app.metrodelay.server.model;

public class Countries extends AbstractIdentificationList<Country> {

	public static final Countries PROTOTYPE;
	
	private static final String NAME = "countries" ;

	static {
		PROTOTYPE = new Countries();
		PROTOTYPE.add(new Country("CZ", "Czech Republic", "Česká republika"));
		PROTOTYPE.add(new Country("DE", "Germany", "Deutschland"));
		PROTOTYPE.add(new Country("JP", "Japan", "日本国"));
		PROTOTYPE.add(new Country("UK", "United Kingdom"));
	}
	
	@Override
	public String getName() {
		return NAME;
	}
}
