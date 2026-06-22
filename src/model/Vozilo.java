package model;
import enums.*;
import java.time.*;

public class Vozilo {
	private int idVozila;
	private ModelVozila modelVozila;
	private String registracijaVozila;
	private int trenutnaKilometraza;
	private StatusVozila status;
	public Vozilo(int idVozila, ModelVozila modelVozila, String registracijaVozila, int trenutnaKilometraza,
			 StatusVozila status) {
		super();
		this.idVozila = idVozila;
		this.modelVozila = modelVozila;
		this.registracijaVozila = registracijaVozila;
		this.trenutnaKilometraza = trenutnaKilometraza;
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "Vozilo [idVozila=" + idVozila + ", modelVozila=" + modelVozila + ", registracijaVozila="
				+ registracijaVozila + ", trenutnaKilometraza=" + trenutnaKilometraza + ", status=" + status + "]";
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
	public StatusVozila getStatus() {
		return status;
	}
	public void setStatus(StatusVozila status) {
		this.status = status;
	}
	
}
