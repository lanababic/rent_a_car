package model;
import enums.*;
import java.time.*;

public class IzdavanjeVozila {
	private int idIzdaje;
	private Rezervacija rezervacija;
	private Vozilo vozilo;
	private Agent agentIzdao;
	private Agent agentPrimio;
	private int kilometrazaPriPreuzimanju;
	private int kilometrazaPriVracanju;
	private LocalDate stvarniDatumVracanja;
	private double naplacenaKazna;
	public IzdavanjeVozila(int idIzdaje, Rezervacija rezervacija, Vozilo vozilo, Agent agentIzdao, Agent agentPrimio,
			int kilometrazaPriPreuzimanju, int kilometrazaPriVracanju, LocalDate stvarniDatumVracanja) {
		super();
		this.idIzdaje = idIzdaje;
		this.rezervacija = rezervacija;
		this.vozilo = vozilo;
		this.agentIzdao = agentIzdao;
		this.agentPrimio = agentPrimio;
		this.kilometrazaPriPreuzimanju = kilometrazaPriPreuzimanju;
		this.kilometrazaPriVracanju = kilometrazaPriVracanju;
		this.stvarniDatumVracanja = stvarniDatumVracanja;
		this.naplacenaKazna = 0.0;
	}
	@Override
	public String toString() {
		return "IzdavanjeVozila [idIzdaje=" + idIzdaje + ", rezervacija=" + rezervacija + ", vozilo=" + vozilo
				+ ", agentIzdao=" + agentIzdao + ", agentPrimio=" + agentPrimio + ", kilometrazaPriPreuzimanju="
				+ kilometrazaPriPreuzimanju + ", kilometrazaPriVracanju=" + kilometrazaPriVracanju
				+ ", stvarniDatumVracanja=" + stvarniDatumVracanja + ", naplacenaKazna=" + naplacenaKazna + "]";
	}
	public int getIdIzdaje() {
		return idIzdaje;
	}
	public void setIdIzdaje(int idIzdaje) {
		this.idIzdaje = idIzdaje;
	}
	public Rezervacija getRezervacija() {
		return rezervacija;
	}
	public void setRezervacija(Rezervacija rezervacija) {
		this.rezervacija = rezervacija;
	}
	public Vozilo getVozilo() {
		return vozilo;
	}
	public void setVozilo(Vozilo vozilo) {
		this.vozilo = vozilo;
	}
	public Agent getAgentIzdao() {
		return agentIzdao;
	}
	public void setAgentIzdao(Agent agentIzdao) {
		this.agentIzdao = agentIzdao;
	}
	public Agent getAgentPrimio() {
		return agentPrimio;
	}
	public void setAgentPrimio(Agent agentPrimio) {
		this.agentPrimio = agentPrimio;
	}
	public int getKilometrazaPriPreuzimanju() {
		return kilometrazaPriPreuzimanju;
	}
	public void setKilometrazaPriPreuzimanju(int kilometrazaPriPreuzimanju) {
		this.kilometrazaPriPreuzimanju = kilometrazaPriPreuzimanju;
	}
	public int getKilometrazaPriVracanju() {
		return kilometrazaPriVracanju;
	}
	public void setKilometrazaPriVracanju(int kilometrazaPriVracanju) {
		this.kilometrazaPriVracanju = kilometrazaPriVracanju;
	}
	public LocalDate getStvarniDatumVracanja() {
		return stvarniDatumVracanja;
	}
	public void setStvarniDatumVracanja(LocalDate stvarniDatumVracanja) {
		this.stvarniDatumVracanja = stvarniDatumVracanja;
	}
	public double getNaplacenaKazna() {
		return naplacenaKazna;
	}
	public void setNaplacenaKazna(double naplacenaKazna) {
		this.naplacenaKazna = naplacenaKazna;
	}
	
}
