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
		
		ucitajModeleVozila(this.putanjaModeliVozila);
		ucitajVozila(this.putanjaVozila);
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
			if(v.getModelVozila().equals(modelVozila)) {
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
	
	

}
