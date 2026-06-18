package model;
import enums.*;
import java.time.LocalDate;

public class Zaposleni extends Osoba {
	private StrucnaSprema sprema;
	private int staz;
	private double osnovnaPlata;

	public Zaposleni(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String email,
			String korisnickoIme, String lozinka, StrucnaSprema sprema, int staz, double osnovnaPlata) {
		super(ime, prezime, pol, datumRodj, telefon, email, korisnickoIme, lozinka);
		this.sprema = sprema;
		this.staz = staz;
		this.osnovnaPlata = osnovnaPlata;
	}

	@Override
	public String toString() {
		return "Zaposleni [sprema=" + sprema + ", staz=" + staz + ", osnovnaPlata=" + osnovnaPlata + "]";
	}

	public StrucnaSprema getSprema() {
		return sprema;
	}

	public void setSprema(StrucnaSprema sprema) {
		this.sprema = sprema;
	}

	public int getStaz() {
		return staz;
	}

	public void setStaz(int staz) {
		this.staz = staz;
	}

	public double getOsnovnaPlata() {
		return osnovnaPlata;
	}

	public void setOsnovnaPlata(int osnovnaPlata) {
		this.osnovnaPlata = osnovnaPlata;
	}
	
}
