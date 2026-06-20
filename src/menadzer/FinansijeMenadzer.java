package menadzer;

import model.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import enums.*;

public class FinansijeMenadzer {
	private ArrayList<Pretplata> svePretplate;
	private ArrayList<Cenovnik> sviCenovnici;
	private final String putanjaPretplate = "podaci/pretplate.csv";
	private final String putanjaCenovnika = "podaci/cenovnici.csv";
	
	public FinansijeMenadzer(OsobaMenadzer osobaMen, RezervacijeMenadzer rezMen) {
		this.svePretplate = new ArrayList<>();
		this.sviCenovnici = new ArrayList<>();
		
		ucitajPretplate(this.putanjaPretplate, osobaMen);
		ucitajCenovnike(this.putanjaCenovnika, rezMen);
	}
	
	public void sacuvajPretplate(String putanjaPretplate) {
	    ArrayList<String> lines = new ArrayList<String>();
	    for (Pretplata p : this.svePretplate) {
	        String line = p.getIdPretplate() + ";" + 
	                      p.getKlijent().getKorisnickoIme() + ";" + // Čuvamo jedinstveni e-mail/korisničko ime klijenta
	                      p.getDatumPocetak() + ";" + 
	                      p.getDatumKraj() + ";" + 
	                      p.getCena();
	        lines.add(line);
	    }
	    try {
	        Files.write(Paths.get(putanjaPretplate), lines);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	private void ucitajPretplate(String putanjaPretplate, OsobaMenadzer osobaMen) {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(putanjaPretplate));
	        for (String line : lines) {
	            String[] parts = line.split(";");
	            
	            String korImeKlijenta = parts[1];
	            // Pronalazimo objekat Klijent iz OsobaMenadzer-a preko njegovog korisničkog imena / e-maila
	            Klijent pronadjeniKlijent = osobaMen.pronadjiKlijentaPoKorisnickomImenu(korImeKlijenta);
	            
	            Pretplata p = new Pretplata(
	                Integer.parseInt(parts[0]),         // idPretplate
	                pronadjeniKlijent,                  // prosleđujemo ceo objekat klijenta
	                LocalDate.parse(parts[2]),          // datumPocetak
	                LocalDate.parse(parts[3]),          // datumKraj
	                Double.parseDouble(parts[4])        // cena
	            );
	            
	            this.svePretplate.add(p);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void sacuvajCenovnike(String putanjaCenovnici) {
	    ArrayList<String> lines = new ArrayList<String>();
	    
	    for (Cenovnik c : this.sviCenovnici) {
	        String line = c.getIdCenovnika() + ";" + 
	                      c.getDatumPocetka() + ";" + 
	                      c.getDatumKraja() + ";" + 
	                      c.getCenaGodisnjePretplate() + ";" + 
	                      c.getKaznaZaKasnjenje();
	        
	        // 1. Pakovanje mape cenaNajma (Ključ je Enum KategorijaVozila)
	        String dodatakNajam = "";
	        for (KategorijaVozila kat : c.getCenaNajma().keySet()) {
	            if (!dodatakNajam.isEmpty()) {
	                dodatakNajam += ",";
	            }
	            dodatakNajam += kat.name() + ":" + c.getCenaNajma().get(kat);
	        }
	        
	        // 2. Pakovanje mape cenaDodatneUsluge (Ključ je objekat DodatnaUsluga)
	        String dodatakUsluge = "";
	        for (DodatnaUsluga u : c.getCenaDodatneUsluge().keySet()) {
	            if (!dodatakUsluge.isEmpty()) {
	                dodatakUsluge += ",";
	            }
	            dodatakUsluge += u.getIdDodatneUsluge() + ":" + c.getCenaDodatneUsluge().get(u);
	        }
	        
	        // Spajamo sve u konačnu liniju: osnovno ; najam_mape ; usluge_mape
	        line = line + ";" + dodatakNajam + ";" + dodatakUsluge;
	        lines.add(line);
	    }
	    
	    try {
	        Files.write(Paths.get(putanjaCenovnici), lines);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void ucitajCenovnike(String putanjaCenovnici, RezervacijeMenadzer rezMen) {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(putanjaCenovnici));
	        for (String line : lines) {
	            String[] parts = line.split(";");
	            
	            Cenovnik c = new Cenovnik(
	                Integer.parseInt(parts[0]),             // idCenovnika
	                LocalDate.parse(parts[1]),              // datumPocetka
	                LocalDate.parse(parts[2]),              // datumKraja
	                Double.parseDouble(parts[3]),           // cenaGodisnjePretplate
	                Double.parseDouble(parts[4])            // kaznaZaKasnjenje
	            );
	            
	            // 1. Rekonstrukcija mape cenaNajma (parts[5]) -> KategorijaVozila (Enum)
	            if (parts.length > 5 && !parts[5].trim().isEmpty()) {
	                String[] paroviNajma = parts[5].split(",");
	                for (String par : paroviNajma) {
	                    String[] kljucIValue = par.split(":");
	                    KategorijaVozila kat = KategorijaVozila.valueOf(kljucIValue[0].trim());
	                    double cena = Double.parseDouble(kljucIValue[1].trim());
	                    c.setCenaNajma(kat, cena); // Koristimo tvoj seter iz klase
	                }
	            }
	            
	            // 2. Rekonstrukcija mape cenaDodatneUsluge (parts[6]) -> Objekat DodatnaUsluga
	            if (parts.length > 6 && !parts[6].trim().isEmpty()) {
	                String[] paroviUsluga = parts[6].split(",");
	                for (String par : paroviUsluga) {
	                    String[] kljucIValue = par.split(":");
	                    int idUsluge = Integer.parseInt(kljucIValue[0].trim());
	                    double cena = Double.parseDouble(kljucIValue[1].trim());
	                    
	                    // Pronalazimo objekat usluge preko prosleđenog menadžera
	                    // NAPOMENA: Ako ti je metoda pronadjiUsluguPoId tamo privatna, stavi je na public da je vidi ovde
	                    DodatnaUsluga u = rezMen.pronadjiUsluguPoId(idUsluge);
	                    if (u != null) {
	                        c.setCenaDodatneUsluge(u, cena); // Koristimo tvoj seter iz klase
	                    }
	                }
	            }
	            
	            this.sviCenovnici.add(c);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}
