package app.metrodelay.server.model;

public class Lines extends AbstractIdentificationList<Line> {

	public static final Lines PROTOTYPE;
	
	private static final String NAME = "lines" ;

	static {
		PROTOTYPE = new Lines();
		//PROTOTYPE.add(new Line("", ""));
		PROTOTYPE.add(new Line("A", "Metro A"));
		PROTOTYPE.add(new Line("B", "Metro B"));
		PROTOTYPE.add(new Line("C", "Metro C"));
		
		PROTOTYPE.add(new Line("1", "Tram 1"));
		PROTOTYPE.add(new Line("2", "Tram 2"));
		PROTOTYPE.add(new Line("3", "Tram 3"));
		
		PROTOTYPE.add(new Line("100", "Bus 100"));
		PROTOTYPE.add(new Line("101", "Bus 101"));
		PROTOTYPE.add(new Line("102", "Bus 102"));
		
		PROTOTYPE.add(new Line("Circle", "Circle Line"));
		PROTOTYPE.add(new Line("District", "Circle Line"));
		PROTOTYPE.add(new Line("Hammersmith & City", "Hammersmith & City Line"));
		PROTOTYPE.add(new Line("DLR", "DLR"));
		
		PROTOTYPE.add(new Line("U1", "U-Bahn Uhlandstraße – Warschauer Straße"));
		PROTOTYPE.add(new Line("U2", "U-Bahn Pankow – Ruhleben"));
		PROTOTYPE.add(new Line("U3", "U-Bahn Nollendorfplatz – Krumme Lanke"));
		
		PROTOTYPE.add(new Line("Line 3", "Ginza Line", "銀座線"));
		PROTOTYPE.add(new Line("Line 4", "Marunouchi Line", "丸ノ内線"));

	}
	
	@Override
	public String getName() {
		return NAME;
	}
}
