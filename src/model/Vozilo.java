package model;
import enums.*;
import java.time.*;

public class Vozilo {
	private int idVozila;
	private ModelVozila modelVozila;
	private String registracijaVozila;
	private int trenutnaKilometraza;
	private KategorijaVozila kategorijaVozila;
	private StatusRezervacije status;
	public Vozilo(int idVozila, ModelVozila modelVozila, String registracijaVozila, int trenutnaKilometraza,
			KategorijaVozila kategorijaVozila, StatusRezervacije status) {
		super();
		this.idVozila = idVozila;
		this.modelVozila = modelVozila;
		this.registracijaVozila = registracijaVozila;
		this.trenutnaKilometraza = trenutnaKilometraza;
		this.kategorijaVozila = kategorijaVozila;
		this.status = status;
	}
	@Override
	public String toString() {
		return "Vozilo [idVozila=" + idVozila + ", modelVozila=" + modelVozila + ", registracijaVozila="
				+ registracijaVozila + ", trenutnaKilometraza=" + trenutnaKilometraza + ", kategorijaVozila="
				+ kategorijaVozila + ", status=" + status + "]";
	}
	public int getIdVozila() {
		return idVozila;
	}
	public void setIdVozila(int idVozila) {
		this.idVozila = idVozila;
	}
	public ModelVozila getModelVozila() {
		return modelVozila;
	}
	public void setModelVozila(ModelVozila modelVozila) {
		this.modelVozila = modelVozila;
	}
	public String getRegistracijaVozila() {
		return registracijaVozila;
	}
	public void setRegistracijaVozila(String registracijaVozila) {
		this.registracijaVozila = registracijaVozila;
	}
	public int getTrenutnaKilometraza() {
		return trenutnaKilometraza;
	}
	public void setTrenutnaKilometraza(int trenutnaKilometraza) {
		this.trenutnaKilometraza = trenutnaKilometraza;
	}
	public KategorijaVozila getKategorijaVozila() {
		return kategorijaVozila;
	}
	public void setKategorijaVozila(KategorijaVozila kategorijaVozila) {
		this.kategorijaVozila = kategorijaVozila;
	}
	public StatusRezervacije getStatus() {
		return status;
	}
	public void setStatus(StatusRezervacije status) {
		this.status = status;
	}
	

}
