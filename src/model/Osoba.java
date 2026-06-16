package model;

import enums.Pol;
import java.time.LocalDate;

public abstract class Osoba {
	private String ime;
	private String prezime;
	private Pol pol;
	private LocalDate datumRodj;
	private String telefon;
	private String adresa;
	private String korisnickoIme;
	private String lozinka;
	

	public Osoba(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String adresa,
			String korisnickoIme, String lozinka) {
		super();
		this.ime = ime;
		this.prezime = prezime;
		this.pol = pol;
		this.datumRodj = datumRodj;
		this.telefon = telefon;
		this.adresa = adresa;
		this.korisnickoIme = korisnickoIme;
		this.lozinka = lozinka;
	}


	@Override
	public String toString() {
		return "Klijent [ime=" + ime + ", prezime=" + prezime + ", pol=" + pol + ", datumRodj=" + datumRodj
				+ ", telefon=" + telefon + ", adresa=" + adresa + ", korisnickoIme=" + korisnickoIme + ", lozinka="
				+ lozinka + "]";
	}


	public String getIme() {
		return ime;
	}


	public void setIme(String ime) {
		this.ime = ime;
	}


	public String getPrezime() {
		return prezime;
	}


	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}


	public Pol getPol() {
		return pol;
	}


	public void setPol(Pol pol) {
		this.pol = pol;
	}


	public LocalDate getDatumRodj() {
		return datumRodj;
	}


	public void setDatumRodj(LocalDate datumRodj) {
		this.datumRodj = datumRodj;
	}


	public String getTelefon() {
		return telefon;
	}


	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}


	public String getAdresa() {
		return adresa;
	}


	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}


	public String getKorisnickoIme() {
		return korisnickoIme;
	}


	public void setKorisnickoIme(String korisnickoIme) {
		this.korisnickoIme = korisnickoIme;
	}


	public String getLozinka() {
		return lozinka;
	}


	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
