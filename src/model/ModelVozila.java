package model;
import enums.*;
import java.time.*;

public class ModelVozila {
	private int idModela;
	private KategorijaVozila kategorijaVozila;
	private String naziv;
	private String proizvodjac;
	public ModelVozila(int id, KategorijaVozila kategorijaVozila, String naziv, String proizvodjac) {
		super();
		this.idModela = id;
		this.kategorijaVozila = kategorijaVozila;
		this.naziv = naziv;
		this.proizvodjac = proizvodjac;
	}
//	@Override
//	public String toString() {
//		return "ModelVozila [id=" + idModela + ", kategorijaVozila=" + kategorijaVozila + ", naziv=" + naziv
//				+ ", proizvodjac=" + proizvodjac + "]";
//	}
	@Override
	public String toString() {
		return naziv;
	}
	public int getId() {
		return idModela;
	}
	public void setId(int id) {
		this.idModela = id;
	}
	public KategorijaVozila getKategorijaVozila() {
		return kategorijaVozila;
	}
	public void setKategorijaVozila(KategorijaVozila kategorijaVozila) {
		this.kategorijaVozila = kategorijaVozila;
	}
	public String getNaziv() {
		return naziv;
	}
	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}
	public String getProizvodjac() {
		return proizvodjac;
	}
	public void setProizvodjac(String proizvodjac) {
		this.proizvodjac = proizvodjac;
	}
	

}
