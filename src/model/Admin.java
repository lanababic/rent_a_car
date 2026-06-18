package model;
import enums.*;
import java.time.LocalDate;

public class Admin extends Agent{
	private int brojUposlenih;

	@Override
	public String toString() {
		return "Admin [brojUposlenih=" + brojUposlenih + "]";
	}

	public Admin(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String email,
			String korisnickoIme, String lozinka, StrucnaSprema sprema, int staz, double osnovnaPlata) {
		super(ime, prezime, pol, datumRodj, telefon, email, korisnickoIme, lozinka, sprema, staz, osnovnaPlata);
		// TODO Auto-generated constructor stub
		this.brojUposlenih = 0;
	}

	public int getBrojZaposlenih() {
		return brojUposlenih;
	}

	public void setBrojZaposlenih(int brojZaposlenih) {
		this.brojUposlenih = brojZaposlenih;
	}
	
}
