package model;
import enums.*;
import java.util.ArrayList;
import java.time.*;

public class Rezervacija {
	private int idRezervacije;
	private ModelVozila modelVozila;
	private LocalDate datumOd;
	private LocalDate datumDo;
	private StatusRezervacije status;
	private double osnovnaCena;
	private ArrayList<DodatnaUsluga> listaDodatnihUsluga;
	private LocalDate datumPravljenja;
	private Klijent klijent;
	public Rezervacija(int idRezervacije, ModelVozila modelVozila, LocalDate datumOd, LocalDate datumDo,
			 double osnovnaCena, LocalDate datumPravljenja, Klijent klijent) {
		super();
		this.idRezervacije = idRezervacije;
		this.modelVozila = modelVozila;
		this.datumOd = datumOd;
		this.datumDo = datumDo;
		this.status = StatusRezervacije.NA_CEKANJU;
		this.osnovnaCena = osnovnaCena;
		this.listaDodatnihUsluga = new ArrayList<DodatnaUsluga>();
		this.datumPravljenja = datumPravljenja;
		this.klijent=klijent;
	}
	
	public Klijent getKlijent() {
		return klijent;
	}

	public void setKlijent(Klijent klijent) {
		this.klijent = klijent;
	}

	@Override
	public String toString() {
		return "Rezervacija [idRezervacije=" + idRezervacije + ", modelVozila=" + modelVozila + ", datumOd=" + datumOd
				+ ", datumDo=" + datumDo + ", status=" + status + ", osnovnaCena=" + osnovnaCena
				+ ", listaDodatnihUsluga=" + listaDodatnihUsluga + ", datumPravljenja=" + datumPravljenja + ", klijent="
				+ klijent + "]";
	}

	public int getIdRezervacije() {
		return idRezervacije;
	}
	public void setIdRezervacije(int idRezervacije) {
		this.idRezervacije = idRezervacije;
	}
	public ModelVozila getModelVozila() {
		return modelVozila;
	}
	public void setModelVozila(ModelVozila modelVozila) {
		this.modelVozila = modelVozila;
	}
	public LocalDate getDatumOd() {
		return datumOd;
	}
	public void setDatumOd(LocalDate datumOd) {
		this.datumOd = datumOd;
	}
	public LocalDate getDatumDo() {
		return datumDo;
	}
	public void setDatumDo(LocalDate datumDo) {
		this.datumDo = datumDo;
	}
	public StatusRezervacije getStatus() {
		return status;
	}
	public void setStatus(StatusRezervacije status) {
		this.status = status;
	}
	public double getOsnovnaCena() {
		return osnovnaCena;
	}
	public void setOsnovnaCena(double osnovnaCena) {
		this.osnovnaCena = osnovnaCena;
	}
	public ArrayList<DodatnaUsluga> getListaDodatnihUsluga() {
		return listaDodatnihUsluga;
	}
	public void setListaDodatnihUsluga(ArrayList<DodatnaUsluga> listaDodatnihUsluga) {
		this.listaDodatnihUsluga = listaDodatnihUsluga;
	}

	public void dodajDodatnuUslugu(DodatnaUsluga usluga) {
        this.listaDodatnihUsluga.add(usluga);
    }
	public LocalDate getDatumPravljenja() {
		return datumPravljenja;
	}
	public void setDatumPravljenja(LocalDate datumPravljenja) {
		this.datumPravljenja = datumPravljenja;
	}
	
}
