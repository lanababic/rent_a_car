package model;
import enums.*;
import java.time.*;

public class DodatnaUsluga {
	private int idDodtneUsluge;
	private String naziv;
	public DodatnaUsluga(int idDodtneUsluge, String naziv) {
		super();
		this.idDodtneUsluge = idDodtneUsluge;
		this.naziv = naziv;
	}
	public int getIdDodtneUsluge() {
		return idDodtneUsluge;
	}
	public void setIdDodtneUsluge(int idDodtneUsluge) {
		this.idDodtneUsluge = idDodtneUsluge;
	}
	public String getNaziv() {
		return naziv;
	}
	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}
	@Override
	public String toString() {
		return "DodatnaUsluga [idDodtneUsluge=" + idDodtneUsluge + ", naziv=" + naziv + "]";
	}
	
}
