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
	private int osnovnaCena;
	private ArrayList<DodatnaUsluga> listaDodatnihUsluga;
	private LocalDate datumPravljenja;
	public Rezervacija(int idRezervacije, ModelVozila modelVozila, LocalDate datumOd, LocalDate datumDo,
			 int osnovnaCena, LocalDate datumPravljenja) {
		super();
		this.idRezervacije = idRezervacije;
		this.modelVozila = modelVozila;
		this.datumOd = datumOd;
		this.datumDo = datumDo;
		this.status = StatusRezervacije.NA_CEKANJU;
		this.osnovnaCena = osnovnaCena;
		this.listaDodatnihUsluga = new ArrayList<DodatnaUsluga>();
		this.datumPravljenja = datumPravljenja;
	}
	@Override
	public String toString() {
		return "Rezervacija [idRezervacije=" + idRezervacije + ", modelVozila=" + modelVozila + ", datumOd=" + datumOd
				+ ", datumDo=" + datumDo + ", status=" + status + ", osnovnaCena=" + osnovnaCena + "]";
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
	public int getOsnovnaCena() {
		return osnovnaCena;
	}
	public void setOsnovnaCena(int osnovnaCena) {
		this.osnovnaCena = osnovnaCena;
	}
	public ArrayList<DodatnaUsluga> getListaDodatnihUsluga() {
		return listaDodatnihUsluga;
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
