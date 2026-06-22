package model;
import enums.*;
import java.time.*;

public class Klijent extends Osoba {
	private KategorijaKlijenta kategorija;
	private LocalDate datumVozacke;
	private int brojKasnjenja;
	private LocalDate datumOtkazivanja;
	private Pretplata pretplata;
	private ZahtevPretplate zahtev;
	
	public Klijent(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String email,
			 String lozinka, KategorijaKlijenta kategorija, LocalDate datumVozacke,
			int brojKasnjenja) {
		super(ime, prezime, pol, datumRodj, telefon, email, email, lozinka);
		this.kategorija = kategorija;
		this.datumVozacke = datumVozacke;
		this.brojKasnjenja = brojKasnjenja;
		this.datumOtkazivanja = null;
		this.pretplata=null;
		this.zahtev=ZahtevPretplate.NEMA;
	}
	public ZahtevPretplate getZahtev() {
		return zahtev;
	}
	public void setZahtev(ZahtevPretplate zahtev) {
		this.zahtev = zahtev;
	}
	public Pretplata getPretplata() {
		return pretplata;
	}
	public void setPretplata(Pretplata pretplata) {
		this.pretplata = pretplata;
	}
	public LocalDate getDatumOtkazivanja() {
		return datumOtkazivanja;
	}
	public void setDatumOtkazivanja(LocalDate datumOtkazivanja) {
		this.datumOtkazivanja = datumOtkazivanja;
	}
	public KategorijaKlijenta getKategorija() {
		return kategorija;
	}
	public void setKategorija(KategorijaKlijenta kategorija) {
		this.kategorija = kategorija;
	}
	public LocalDate getDatumVozacke() {
		return datumVozacke;
	}
	public void setDatumVozacke(LocalDate datumVozacke) {
		this.datumVozacke = datumVozacke;
	}
	public int getBrojKasnjenja() {
		return brojKasnjenja;
	}
	public void setBrojKasnjenja(int brojKasnjenja) {
		this.brojKasnjenja = brojKasnjenja;
	}
	@Override
	public String toString() {
		return "Klijent [kategorija=" + kategorija + ", datumVozacke=" + datumVozacke + ", brojKasnjenja="
				+ brojKasnjenja + ", datumOtkazivanja=" + datumOtkazivanja + ", pretplata=" + pretplata + ", zahtev="
				+ zahtev + "]";
	}
	

}
