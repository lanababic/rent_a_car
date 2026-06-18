package menadzer;

import model.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import enums.*;

public class RezervacijeMenadzer {
	private ArrayList<Rezervacija> sveRezervacije;
	private ArrayList<DodatnaUsluga> sveDodatneUsluge;
	private final String putanjaRezervacije = "podaci/rezervacije.csv";
	private final String putanjaDodatneUsluge = "podaci/dodatneUsluge.csv";
	
	public RezervacijeMenadzer(VoziloMenadzer vozMen) {
		this.sveRezervacije = new ArrayList<>();
		this.sveDodatneUsluge = new ArrayList<>();
		
		ucitajDodatneUsluge(this.putanjaDodatneUsluge);
		ucitajRezervacije(this.putanjaRezervacije, vozMen);
	}
	
	public void sacuvajRezervacije(String putanjaRezervacije) {
	    ArrayList<String> linesRezervacije = new ArrayList<String>();
	    
	    for (Rezervacija r : this.sveRezervacije) {
	        String line = r.getIdRezervacije() + ";" + 
	                      r.getModelVozila().getId() + ";" + 
	                      r.getDatumOd() + ";" + 
	                      r.getDatumDo() + ";" + 
	                      r.getStatus() + ";" + 
	                      r.getOsnovnaCena() + ";" + 
	                      r.getDatumPravljenja();
	        
	        String dodatak = "";
	        for (DodatnaUsluga u : r.getListaDodatnihUsluga()) {
	            if (!dodatak.isEmpty()) {
	                dodatak += ","; // da ne bi bio zarez na kraju
	            }
	            dodatak += u.getIdDodatneUsluge();
	        }
	        line = line+";"+dodatak;
	        linesRezervacije.add(line);
	    }
	    
	    try {
	        Files.write(Paths.get(putanjaRezervacije), linesRezervacije);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	private void ucitajRezervacije(String putanjaRezervacije, VoziloMenadzer vozMen) {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(putanjaRezervacije));
	        for (String line : lines) {
	            String[] parts = line.split(";");
	            
	            int idModela = Integer.parseInt(parts[1]);
	            ModelVozila pronadjeniModel = vozMen.pronadjiModelPoId(idModela);
	            
	            Rezervacija r = new Rezervacija(
	                Integer.parseInt(parts[0]),             // idRezervacije
	                pronadjeniModel,                        // modelVozila
	                LocalDate.parse(parts[2]),              // datumOd
	                LocalDate.parse(parts[3]),              // datumDo
	                Integer.parseInt(parts[5]),             // osnovnaCena
	                LocalDate.parse(parts[6])               // datumPravljenja
	            );
	            StatusRezervacije stvarniStatus = StatusRezervacije.valueOf(parts[4]);
	            r.setStatus(stvarniStatus); 
	            if(parts.length > 7 && !parts[7].trim().isEmpty()) {
	            	String[] idDodatnihUsluga = parts[7].split(",");
		            for (String s : idDodatnihUsluga) {
		            	int id = Integer.parseInt(s.trim());
		            	DodatnaUsluga d = pronadjiUsluguPoId(id);
		            	if (d != null) {
	                        r.dodajDodatnuUslugu(d);
	                    }
		            }
	            }
	            this.sveRezervacije.add(r);
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void sacuvajDodatneUsluge(String putanjaUsluge) {
	    ArrayList<String> lines = new ArrayList<String>();
	    for (DodatnaUsluga u : this.sveDodatneUsluge) {
	        String line = u.getIdDodatneUsluge() + ";" + 
	                      u.getNaziv();
	        lines.add(line);
	    }
	    try {
	        Files.write(Paths.get(putanjaUsluge), lines);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	private void ucitajDodatneUsluge(String putanjaDodatneUsluge) {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(putanjaDodatneUsluge));
	        for (String line : lines) {
	            String[] parts = line.split(";");
	            
	            DodatnaUsluga u = new DodatnaUsluga(
	                Integer.parseInt(parts[0]), // idDodatneUsluge
	                parts[1]                    // naziv
	            );
	            
	            this.sveDodatneUsluge.add(u);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public DodatnaUsluga pronadjiUsluguPoId(int id) {
	    for (DodatnaUsluga u : this.sveDodatneUsluge) {
	        if (u.getIdDodatneUsluge() == id) {
	            return u;
	        }
	    }
	    return null;
	}
	public Rezervacija pronadjiRezervacijuPoId(int id) {
	    for (Rezervacija r : this.sveRezervacije) {
	        if (r.getIdRezervacije() == id) {
	            return r;
	        }
	    }
	    return null;
	}
	public void potvrdiRezervaciju(Rezervacija r){
		if(r.getStatus()==StatusRezervacije.NA_CEKANJU) {
			r.setStatus(StatusRezervacije.POTVRDJENO);
		}
		else {
			System.out.println("nije na cekanju");
		}
	}
	public void odbiRezervaciju(Rezervacija r){
		if(r.getStatus()==StatusRezervacije.NA_CEKANJU) {
			r.setStatus(StatusRezervacije.ODBIJENO);
		}
		else {
			System.out.println("nije na cekanju");
		}
	}
	public void otkaziRezervaciju(Rezervacija r) {
		r.setStatus(StatusRezervacije.OTKAZANO);
	}
	

}
