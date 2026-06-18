package model;
import enums.*;
import java.time.*;

public class DodatnaUsluga {
	private int idDodatneUsluge;
	private String naziv;
	public DodatnaUsluga(int idDodatneUsluge, String naziv) {
		super();
		this.idDodatneUsluge = idDodatneUsluge;
		this.naziv = naziv;
	}
	public int getIdDodatneUsluge() {
		return idDodatneUsluge;
	}
	public void setIdDodtneUsluge(int idDodatneUsluge) {
		this.idDodatneUsluge = idDodatneUsluge;
	}
	public String getNaziv() {
		return naziv;
	}
	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}
	@Override
	public String toString() {
		return "DodatnaUsluga [idDodtneUsluge=" + idDodatneUsluge + ", naziv=" + naziv + "]";
	}
	
}
