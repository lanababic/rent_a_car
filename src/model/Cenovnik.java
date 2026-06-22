package model;
import java.util.ArrayList;
import enums.*;
import java.time.*;
import java.util.HashMap;
import java.util.Map;

public class Cenovnik {
	private int idCenovnika;
	private LocalDate datumPocetka;
	private LocalDate datumKraja;
	private double cenaGodisnjePretplate;
	private double kaznaZaKasnjenje;
	private double popustZaKategorije;
	private int daniNajma;
	private Map<KategorijaVozila, Double> cenaNajma;
	private Map<DodatnaUsluga, Double> cenaDodatneUsluge;
	public Cenovnik(int idCenovnika, LocalDate datumPocetka, LocalDate datumKraja, double cenaGodisnjePretplate,
			double kaznaZaKasnjenje, double popustZaKategorije, int daniNajma) {
		super();
		this.idCenovnika = idCenovnika;
		this.datumPocetka = datumPocetka;
		this.datumKraja = datumKraja;
		this.cenaGodisnjePretplate = cenaGodisnjePretplate;
		this.kaznaZaKasnjenje = kaznaZaKasnjenje;
		this.popustZaKategorije= popustZaKategorije;
		this.daniNajma= daniNajma;
		this.cenaDodatneUsluge = new HashMap<>();
		this.cenaNajma = new HashMap<>();
	}
	
	@Override
	public String toString() {
		return "Cenovnik [idCenovnika=" + idCenovnika + ", datumPocetka=" + datumPocetka + ", datumKraja=" + datumKraja
				+ ", cenaGodisnjePretplate=" + cenaGodisnjePretplate + ", kaznaZaKasnjenje=" + kaznaZaKasnjenje
				+ ", popustZaKategorije=" + popustZaKategorije + ", daniNajma=" + daniNajma + ", cenaNajma=" + cenaNajma
				+ ", cenaDodatneUsluge=" + cenaDodatneUsluge + "]";
	}

	public int getDaniNajma() {
		return daniNajma;
	}
	public void setDaniNajma(int daniNajma) {
		this.daniNajma = daniNajma;
	}
	public double getPopustZaKategorije() {
		return popustZaKategorije;
	}
	public void setPopustZaKategorije(double popustZaKategorije) {
		this.popustZaKategorije = popustZaKategorije;
	}
	public int getIdCenovnika() {
		return idCenovnika;
	}
	public void setIdCenovnika(int idCenovnika) {
		this.idCenovnika = idCenovnika;
	}
	public LocalDate getDatumPocetka() {
		return datumPocetka;
	}
	public void setDatumPocetka(LocalDate datumPocetka) {
		this.datumPocetka = datumPocetka;
	}
	public LocalDate getDatumKraja() {
		return datumKraja;
	}
	public void setDatumKraja(LocalDate datumKraja) {
		this.datumKraja = datumKraja;
	}
	public double getCenaGodisnjePretplate() {
		return cenaGodisnjePretplate;
	}
	public void setCenaGodisnjePretplate(double cenaGodisnjePretplate) {
		this.cenaGodisnjePretplate = cenaGodisnjePretplate;
	}
	public double getKaznaZaKasnjenje() {
		return kaznaZaKasnjenje;
	}
	public void setKaznaZaKasnjenje(double kaznaZaKasnjenje) {
		this.kaznaZaKasnjenje = kaznaZaKasnjenje;
	}
	public Map<KategorijaVozila, Double> getCenaNajma() {
		return cenaNajma;
	}
	public Double getCenaNajmaKonkretno(KategorijaVozila kat) {
        return this.cenaNajma.getOrDefault(kat, 0.0);
	}
	public void setCenaNajma(KategorijaVozila kat, double cena) {
        this.cenaNajma.put(kat, cena);
	}
	public Map<DodatnaUsluga, Double> getCenaDodatneUsluge() {
		return cenaDodatneUsluge;
	}
	public Double getCenaDodatneUslugeKonkretno(DodatnaUsluga usluga) {
        return this.cenaDodatneUsluge.getOrDefault(usluga, 0.0);
	}
	public void setCenaDodatneUsluge(DodatnaUsluga usluga, double cena) {
        this.cenaDodatneUsluge.put(usluga, cena);
	}
	
}
