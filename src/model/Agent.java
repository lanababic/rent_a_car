package model;
import enums.*;
import java.time.LocalDate;

public class Agent extends Zaposleni {
	
	public Agent(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String email,
			String korisnickoIme, String lozinka, StrucnaSprema sprema, int staz, double osnovnaPlata) {
		super(ime, prezime, pol, datumRodj, telefon, email, korisnickoIme, lozinka, sprema, staz, osnovnaPlata);
		// TODO Auto-generated constructor stub
	}

//	@Override
//	public String toString() {
//		return "Agent [toString()=" + super.toString() + ", getSprema()=" + getSprema() + ", getStaz()=" + getStaz()
//				+ ", getOsnovnaPlata()=" + getOsnovnaPlata() + ", getIme()=" + getIme() + ", getPrezime()="
//				+ getPrezime() + ", getPol()=" + getPol() + ", getDatumRodj()=" + getDatumRodj() + ", getTelefon()="
//				+ getTelefon() + ", getEmail()=" + getEmail() + ", getKorisnickoIme()=" + getKorisnickoIme()
//				+ ", getLozinka()=" + getLozinka() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
//	}
	@Override
	public String toString() {
	    return this.getKorisnickoIme(); // ComboBox 
	}
		
	
}
