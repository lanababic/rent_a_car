package menadzer;

import model.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import enums.*;

public class IzdavanjeMenadzer {
	private ArrayList<IzdavanjeVozila> svaIzdavanja;
	private final String putanjaIzdavanja = "podaci/izdavanja.csv";
	
	public IzdavanjeMenadzer( RezervacijeMenadzer rezMen, VoziloMenadzer vozMen, OsobaMenadzer korMen) {
		this.svaIzdavanja = new ArrayList<>();
		
		ucitajIzdavanja(this.putanjaIzdavanja, rezMen, vozMen,korMen);
	}
	
	
	public void sacuvajIzdavanja(String putanjaIzdavanja) {
	    ArrayList<String> lines = new ArrayList<String>();
	    for (IzdavanjeVozila iv : this.svaIzdavanja) {
	        
	        String datumVracanja = (iv.getStvarniDatumVracanja() != null) ? iv.getStvarniDatumVracanja().toString() : "null";
	        
	        String line = iv.getIdIzdaje() + ";" + 
	                      iv.getRezervacija().getIdRezervacije() + ";" + 
	                      iv.getVozilo().getIdVozila() + ";" + 
	                      iv.getAgentIzdao().getKorisnickoIme() + ";" +  
	                      iv.getAgentPrimio().getKorisnickoIme() + ";" + 
	                      iv.getKilometrazaPriPreuzimanju() + ";" + 
	                      iv.getKilometrazaPriVracanju() + ";" + 
	                      datumVracanja + ";" + 
	                      iv.getNaplacenaKazna(); 
	                      
	        lines.add(line);
	    }
	    try {
	        Files.write(Paths.get(putanjaIzdavanja), lines);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void ucitajIzdavanja(String putanjaIzdavanja, RezervacijeMenadzer rezMen, VoziloMenadzer vozMen, OsobaMenadzer korMen) {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(putanjaIzdavanja));
	        for (String line : lines) {
	            String[] parts = line.split(";");
	            
	            Rezervacija rez = rezMen.pronadjiRezervacijuPoId(Integer.parseInt(parts[1]));
	            Vozilo voz = vozMen.pronadjiVoziloPoId(Integer.parseInt(parts[2])); 
	            Agent izdao = korMen.pronadjiAgentaPoKorisnickomImenu(parts[3]);
	            Agent primio = korMen.pronadjiAgentaPoKorisnickomImenu(parts[4]);
	            
	            LocalDate datumVracanja = null;
	            if (!parts[7].equals("null")) {
	                datumVracanja = LocalDate.parse(parts[7]);
	            }
	            
	            IzdavanjeVozila iv = new IzdavanjeVozila(
	                Integer.parseInt(parts[0]),             // idIzdaje
	                rez,                                    // objekat Rezervacija
	                voz,                                    // objekat Vozilo
	                izdao,                                  // objekat Agent (izdao)
	                primio,                                 // objekat Agent (primio)
	                Integer.parseInt(parts[5]),             // kilometrazaPriPreuzimanju
	                Integer.parseInt(parts[6]),             // kilometrazaPriVracanju
	                datumVracanja                           // stvarniDatumVracanja (LocalDate ili null)
	            );
	            
	            iv.setNaplacenaKazna(Double.parseDouble(parts[8]));
	            
	            this.svaIzdavanja.add(iv);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
