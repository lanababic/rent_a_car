package model;
import enums.*;
import java.time.LocalDate;

public class Zaposleni extends Osoba {
	private StrucnaSprema sprema;
	private int staz;
	private int osnovnaPlata;

	public Zaposleni(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String adresa,
			String korisnickoIme, String lozinka, StrucnaSprema sprema, int staz, int osnovnaPlata) {
		super(ime, prezime, pol, datumRodj, telefon, adresa, korisnickoIme, lozinka);
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

	public int getOsnovnaPlata() {
		return osnovnaPlata;
	}

	public void setOsnovnaPlata(int osnovnaPlata) {
		this.osnovnaPlata = osnovnaPlata;
	}
	
	public double izracunajPlatu() {
		double koeficijent = this.sprema.getKoefijent();
		double plata = this.osnovnaPlata *(koeficijent + 0.004*this.staz);
		return plata;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
