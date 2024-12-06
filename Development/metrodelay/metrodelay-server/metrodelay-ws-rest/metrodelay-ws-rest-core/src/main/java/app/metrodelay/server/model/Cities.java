package app.metrodelay.server.model;

public class Cities extends AbstractIdentificationList<City> {

	public static final Cities PROTOTYPE;
	
	private static final String NAME = "cities" ;
	
	static {
		PROTOTYPE = new Cities();
		PROTOTYPE.add(new City("BER", "Berlin"));
		PROTOTYPE.add(new City("LON", "London"));
		PROTOTYPE.add(new City("PLZ", "Pilsen", "Plzeň"));
		PROTOTYPE.add(new City("PRG", "Prague", "Praha"));
		PROTOTYPE.add(new City("TYO", "Tokyo", "東京都"));
	}

	@Override
	public String getName() {
		return NAME;
	}

}
