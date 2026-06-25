package menadzer;

import model.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import enums.*;

public class VoziloMenadzer {
	private ArrayList<Vozilo> svaVozila;
	private ArrayList<ModelVozila> sviModeliVozila;
	private final String putanjaVozila = "podaci/vozila.csv";
	private final String putanjaModeliVozila = "podaci/modeliVozila.csv";
	
	public VoziloMenadzer() {
	    this.svaVozila = new ArrayList<>();
	    this.sviModeliVozila = new ArrayList<>();
	}

	public void ucitajPodatke() {
	    ucitajModeleVozila(this.putanjaModeliVozila);
	    ucitajVozila(this.putanjaVozila);
	}
	
	public ArrayList<Vozilo> getSvaVozila() {
		return svaVozila;
	}

	public void setSvaVozila(ArrayList<Vozilo> svaVozila) {
		this.svaVozila = svaVozila;
	}

	public ArrayList<ModelVozila> getSviModeliVozila() {
		return sviModeliVozila;
	}

	public void setSviModeliVozila(ArrayList<ModelVozila> sviModeliVozila) {
		this.sviModeliVozila = sviModeliVozila;
	}

	public void sacuvajModeleVozila(String putanjaModeli) {
	    ArrayList<String> lines = new ArrayList<String>();
	    for (ModelVozila m : this.sviModeliVozila) {
	        String line = m.getId() + ";" + 
	                      m.getKategorijaVozila() + ";" + 
	                      m.getNaziv() + ";" + 
	                      m.getProizvodjac();
	        lines.add(line);
	    }
	    try {
	        Files.write(Paths.get(putanjaModeli), lines);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void ucitajModeleVozila(String putanjaModeli) {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(putanjaModeli));
	        for (String line : lines) {
	            String[] parts = line.split(";");
	            
	            ModelVozila m = new ModelVozila(
	                Integer.parseInt(parts[0]),             // idModela
	                KategorijaVozila.valueOf(parts[1]),     // kategorijaVozila (enum)
	                parts[2],                               // naziv
	                parts[3]                                // proizvodjac
	            );
	            
	            this.sviModeliVozila.add(m);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void sacuvajVozila(String putanjaVozila) {
	    ArrayList<String> lines = new ArrayList<String>();
	    for (Vozilo v : this.svaVozila) {
	        String line = v.getIdVozila() + ";" + 
	                      v.getModelVozila().getId() + ";" + 
	                      v.getRegistracijaVozila() + ";" + 
	                      v.getTrenutnaKilometraza() + ";" + 
	                      v.getStatus();
	        lines.add(line);
	    }
	    try {
	        Files.write(Paths.get(putanjaVozila), lines);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void ucitajVozila(String putanjaVozila) {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(putanjaVozila));
	        for (String line : lines) {
	            String[] parts = line.split(";");
	            
	            int idModela = Integer.parseInt(parts[1]);
	            ModelVozila pronadjeniModel = pronadjiModelPoId(idModela);
	            
	            Vozilo v = new Vozilo(
	                Integer.parseInt(parts[0]),             // idVozila
	                pronadjeniModel,                        // prosleđujemo ceo objekat modela
	                parts[2],                               // registracijaVozila
	                Integer.parseInt(parts[3]),             // trenutnaKilometraza
	                StatusVozila.valueOf(parts[4])     		// status (enum)
	            );
	            
	            this.svaVozila.add(v);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public ModelVozila pronadjiModelPoId(int id) {
	    for (ModelVozila m : this.sviModeliVozila) {
	        if (m.getId() == id) {
	            return m;
	        }
	    }
	    return null; // Ili možete baciti izuzetak ako model mora da postoji
	}
	public Vozilo pronadjiVoziloPoId(int id) {
	    for (Vozilo v : this.svaVozila) {
	        if (v.getIdVozila() == id) {
	            return v;
	        }
	    }
	    return null; 
	}
	public ArrayList<Vozilo> pronadjiVozilaPoModelu(ModelVozila modelVozila){
		ArrayList<Vozilo> lista = new ArrayList<>();
		for(Vozilo v: this.svaVozila) {
			if(v.getModelVozila().getId()==(modelVozila.getId())) {
				lista.add(v);
			}
		}
		return lista;
	}
	public ArrayList<Vozilo> pronadjiVozilaSlobodnaTrenutno(){
		ArrayList<Vozilo> lista = new ArrayList<>();
		for(Vozilo v: this.svaVozila) {
			if(v.getStatus().equals(StatusVozila.DOSTUPNO)) {
				lista.add(v);
			}
		}
		return lista;
	}
	public ArrayList<Vozilo> pronadjiVozilaZauzetaTrenutno(){
		ArrayList<Vozilo> lista = new ArrayList<>();
		for(Vozilo v: this.svaVozila) {
			if(v.getStatus().equals(StatusVozila.IZDATO)) {
				lista.add(v);
			}
		}
		return lista;
	}
	public ArrayList<Vozilo> pronadjiVozilaSlobodnaTrenutnoPrekoModela(ModelVozila modelVozila){
		ArrayList<Vozilo> lista = new ArrayList<>();
		ArrayList<Vozilo> listaSlobodnih = pronadjiVozilaSlobodnaTrenutno();
		for(Vozilo v: listaSlobodnih) {
			if(v.getModelVozila().getId() == modelVozila.getId()) {
				lista.add(v);
			}
		}
		return lista;
	}

	public String getPutanjaVozila() {
		return putanjaVozila;
	}

	public String getPutanjaModeliVozila() {
		return putanjaModeliVozila;
	}
	public int generisiNoviIdModelaVozila() {
		int max = 0;
		for(ModelVozila c : this.sviModeliVozila) {
			if(c.getId() > max) {
				max = c.getId();
			}
		}
		return max + 1;
	}
	public int generisiNoviIdVozila() {
		int max = 0;
		for(Vozilo c : this.svaVozila) {
			if(c.getIdVozila() > max) {
				max = c.getIdVozila();
			}
		}
		return max + 1;
	}
	public void dodajNoviModelVozila(KategorijaVozila kategorijaVozila,String naziv, String proizvodjac) {
		int idModela = generisiNoviIdModelaVozila();
		ModelVozila m = new ModelVozila(idModela, kategorijaVozila, naziv, proizvodjac);
		this.sviModeliVozila.add(m);
		sacuvajModeleVozila(this.putanjaModeliVozila);
	}
	public void dodajNovoVozilo(ModelVozila modelVozila, String registracijaVozila, int trenutnaKilometraza,
			 StatusVozila status) {
		int idVozila = generisiNoviIdVozila();
		Vozilo v = new Vozilo(idVozila,modelVozila, registracijaVozila, trenutnaKilometraza,status);
		this.svaVozila.add(v);
		sacuvajVozila(this.putanjaVozila);
	}
	public void obrisiModelVozila(int idModela) {
		this.sviModeliVozila.removeIf(m -> m.getId() == idModela);
	    this.svaVozila.removeIf(v -> v.getModelVozila().getId() == idModela);
		sacuvajModeleVozila(this.putanjaModeliVozila);
		sacuvajVozila(this.putanjaVozila);
		
	}
	public void obrisiVozilo(int idVozila) {
		this.svaVozila.removeIf(v -> v.getIdVozila() == idVozila);
		sacuvajVozila(this.putanjaVozila);
	}
	public void izmeniVozilo(Vozilo vozilo, ModelVozila modelVozila, String registracijaVozila, int trenutnaKilometraza,
			 StatusVozila status) {
	    if (modelVozila != null) { vozilo.setModelVozila(modelVozila); }
	    if (registracijaVozila != null) { vozilo.setRegistracijaVozila(registracijaVozila); }
	    if (trenutnaKilometraza > vozilo.getTrenutnaKilometraza()) { vozilo.setTrenutnaKilometraza(trenutnaKilometraza); }
	    if (status != null) { vozilo.setStatus(status); }
	    sacuvajVozila(this.putanjaVozila);
	}
	public void izmeniModelVozila(ModelVozila model,KategorijaVozila kategorijaVozila, String naziv, String proizvodjac) {
		if (kategorijaVozila != null) { model.setKategorijaVozila(kategorijaVozila); }
	    if (naziv != null) { model.setNaziv(naziv); }
	    if (proizvodjac != null) { model.setProizvodjac(proizvodjac); }
	    sacuvajModeleVozila(this.putanjaModeliVozila);
	}
	
	

}
