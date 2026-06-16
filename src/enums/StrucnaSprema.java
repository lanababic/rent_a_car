package enums;

public enum StrucnaSprema {
	SSS(1.0),
	VSS(1.2),
	MASTER(1.4);
	
	private final double koeficijent;
	
	private StrucnaSprema(double koeficijent) {
		this.koeficijent= koeficijent;
	}
	
	public double getKoefijent() {
		return koeficijent;
	}

}
