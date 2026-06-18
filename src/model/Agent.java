package model;
import enums.*;
import java.time.LocalDate;

public class Agent extends Zaposleni {
	private int brojUspesnihProdaja;
	
	public Agent(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String email,
			String korisnickoIme, String lozinka, StrucnaSprema sprema, int staz, double osnovnaPlata) {
		super(ime, prezime, pol, datumRodj, telefon, email, korisnickoIme, lozinka, sprema, staz, osnovnaPlata);
		// TODO Auto-generated constructor stub
		this.brojUspesnihProdaja=0;
	}
	
		
	@Override
	public String toString() {
		return "Agent [brojUspesnihProdaja=" + brojUspesnihProdaja + "]";
	}
	
	public int getBrojUspesnihProdaja() {
		return brojUspesnihProdaja;
	}
	public void povecajBrojUspesnihProdaja() {
		this.brojUspesnihProdaja++;
	}
	public void potvrdiRezervaciju(Rezervacija rez) {
		if(rez.getStatus()==StatusRezervacije.NA_CEKANJU) {
			rez.setStatus(StatusRezervacije.POTVRDJENO);
		}
		else {
			System.out.println("Nije na na cekanju pa ne moze");
		}
	}
}
