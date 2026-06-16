package model;
import enums.*;
import java.time.*;

public class Pretplata {
	private int idPretplate;
	private Klijent klijent;
	private LocalDate datumPocetak;
	private LocalDate datumKraj;
	private double cena;
	public Pretplata(int idPretplate, Klijent klijent, LocalDate datumPocetak, LocalDate datumKraj, double cena) {
		super();
		this.idPretplate = idPretplate;
		this.klijent = klijent;
		this.datumPocetak = datumPocetak;
		this.datumKraj = datumKraj;
		this.cena = cena;
	}
	@Override
	public String toString() {
		return "Pretplata [idPretplate=" + idPretplate + ", klijent=" + klijent + ", datumPocetak=" + datumPocetak
				+ ", datumKraj=" + datumKraj + ", cena=" + cena + "]";
	}
	public int getIdPretplate() {
		return idPretplate;
	}
	public void setIdPretplate(int idPretplate) {
		this.idPretplate = idPretplate;
	}
	public Klijent getKlijent() {
		return klijent;
	}
	public void setKlijent(Klijent klijent) {
		this.klijent = klijent;
	}
	public LocalDate getDatumPocetak() {
		return datumPocetak;
	}
	public void setDatumPocetak(LocalDate datumPocetak) {
		this.datumPocetak = datumPocetak;
	}
	public LocalDate getDatumKraj() {
		return datumKraj;
	}
	public void setDatumKraj(LocalDate datumKraj) {
		this.datumKraj = datumKraj;
	}
	public double getCena() {
		return cena;
	}
	public void setCena(double cena) {
		this.cena = cena;
	}
	
}
