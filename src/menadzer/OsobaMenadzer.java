package menadzer;

import model.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import enums.*;

public class OsobaMenadzer {
	private ArrayList<Klijent> sviKlijenti;
	private ArrayList<Agent> sviAgenti;
	private ArrayList<Admin> sviAdmini;
	private final String putanjaKlijeti = "podaci/klijenti.csv";
	private final String putanjaAgenti = "podaci/agenti.csv";
	private final String putanjaAdmini = "podaci/admini.csv";
	private Osoba trenutnoUlogovan;
	private ArrayList<String> svaKorisnickaImena;
	
	public OsobaMenadzer() {
		this.sviKlijenti = new ArrayList<>();
		this.sviAgenti = new ArrayList<>();
		this.sviAdmini = new ArrayList<>();
		this.svaKorisnickaImena = new ArrayList<>();
		this.trenutnoUlogovan=null;
		
		ucitajKlijente("podaci/klijenti.csv");
		ucitajAgente("podaci/agenti.csv");
		ucitajAdmine("podaci/admini.csv");
		ucitajSvaKorisnickaImena();
	}
	
	private void ucitajKlijente(String putanjaKlijenti) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(putanjaKlijenti));
			for (String line: lines) {
				String[] parts = line.split(";");
				Klijent k = new Klijent(parts[0], parts[1], Pol.valueOf(parts[2]), LocalDate.parse(parts[3]),parts[4], parts[5],parts[6],  KategorijaKlijenta.valueOf(parts[7]),LocalDate.parse(parts[8]), Integer.parseInt(parts[9]));				
				this.sviKlijenti.add(k);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	private void ucitajAdmine(String putanjaAdmini) {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(putanjaAdmini));
	        for (String line : lines) {
	            String[] parts = line.split(";");
	            
	            Admin a = new Admin(
	                parts[0],                               // ime
	                parts[1],                               // prezime
	                Pol.valueOf(parts[2]),                  // pol (enum)
	                LocalDate.parse(parts[3]),              // datumRodj
	                parts[4],                               // telefon
	                parts[5],                               // adresa
	                parts[6],                               // korisnickoIme
	                parts[7],                               // lozinka
	                StrucnaSprema.valueOf(parts[8]),        // sprema (enum)
	                Integer.parseInt(parts[9]),             // staz
	                Double.parseDouble(parts[10])          // osnovnaPlata
	            );
	            
	            this.sviAdmini.add(a); 
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	private void ucitajAgente(String putanjaAgenti) {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(putanjaAgenti));
	        for (String line : lines) {
	            String[] parts = line.split(";");
	            
	            Agent agent = new Agent(
	                parts[0],                               // ime
	                parts[1],                               // prezime
	                Pol.valueOf(parts[2]),                  // pol (enum)
	                LocalDate.parse(parts[3]),              // datumRodj
	                parts[4],                               // telefon
	                parts[5],                               // adresa
	                parts[6],                               // korisnickoIme
	                parts[7],                               // lozinka
	                StrucnaSprema.valueOf(parts[8]),        // sprema (enum)
	                Integer.parseInt(parts[9]),             // staz
	                Double.parseDouble(parts[10])           // osnovnaPlata
	            );
	            
	            this.sviAgenti.add(agent); 
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void sacuvajKlijente(String putanjaKlijeti) {
		ArrayList<String> lines = new ArrayList<String>();
		for(Klijent k: this.sviKlijenti) {
			String line = k.getIme() + ";" + 
                    k.getPrezime() + ";" + 
                    k.getPol() + ";" + 
                    k.getDatumRodj() + ";" + 
                    k.getTelefon() + ";" + 
                    k.getEmail() + ";" + 
                    k.getKorisnickoIme() + ";" + 
                    k.getLozinka() + ";" + 
                    k.getKategorija() + ";" + 
                    k.getDatumVozacke() + ";" + 
                    k.getBrojKasnjenja();
			lines.add(line);
		}
		try {
			Files.write(Paths.get(putanjaKlijeti), lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sacuvajAdmine(String putanjaAdmini) {
	    ArrayList<String> lines = new ArrayList<String>();
	    for (Admin a : this.sviAdmini) {
	        String line = a.getIme() + ";" + 
	                      a.getPrezime() + ";" + 
	                      a.getPol() + ";" + 
	                      a.getDatumRodj() + ";" + 
	                      a.getTelefon() + ";" + 
	                      a.getEmail() + ";" + 
	                      a.getKorisnickoIme() + ";" + 
	                      a.getLozinka() + ";" + 
	                      a.getSprema() + ";" + 
	                      a.getStaz() + ";" + 
	                      a.getOsnovnaPlata() + ";" + 
	                      a.getBrojUspesnihProdaja();
	        lines.add(line);
	    }
	    try {
	        Files.write(Paths.get(putanjaAdmini), lines);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void sacuvajAgente(String putanjaAgenti) {
	    ArrayList<String> lines = new ArrayList<String>();
	    for (Agent agent : this.sviAgenti) {
	        String line = agent.getIme() + ";" + 
	                      agent.getPrezime() + ";" + 
	                      agent.getPol() + ";" + 
	                      agent.getDatumRodj() + ";" + 
	                      agent.getTelefon() + ";" + 
	                      agent.getEmail() + ";" + 
	                      agent.getKorisnickoIme() + ";" + 
	                      agent.getLozinka() + ";" + 
	                      agent.getSprema() + ";" + 
	                      agent.getStaz() + ";" + 
	                      agent.getOsnovnaPlata();
	        lines.add(line);
	    }
	    try {
	        Files.write(Paths.get(putanjaAgenti), lines);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void ucitajSvaKorisnickaImena() {
		for(Admin ad: sviAdmini) {
			String korIme = ad.getKorisnickoIme();
			this.svaKorisnickaImena.add(korIme);
		}
		for(Agent ad: sviAgenti) {
			String korIme = ad.getKorisnickoIme();
			this.svaKorisnickaImena.add(korIme);
		}
		for(Klijent ad: sviKlijenti) {
			String korIme = ad.getKorisnickoIme();
			this.svaKorisnickaImena.add(korIme);
		}
		
	}
	public void logIn(String korisnickoIme, String lozinka) {
		for(Admin ad: sviAdmini) {
			String korIme = ad.getKorisnickoIme();
			String loz = ad.getLozinka();
			if(korIme.equals(korisnickoIme) && loz.endsWith(lozinka)) {
				this.trenutnoUlogovan=ad;
			}
		}
		for(Agent ad: sviAgenti) {
			String korIme = ad.getKorisnickoIme();
			String loz = ad.getLozinka();
			if(korIme.equals(korisnickoIme) && loz.endsWith(lozinka)) {
				this.trenutnoUlogovan=ad;
			}
		}
		for(Klijent ad: sviKlijenti) {
			String korIme = ad.getKorisnickoIme();
			String loz = ad.getLozinka();
			String email = ad.getEmail();
			if((korIme.equals(korisnickoIme) || korIme.equals(email)) && loz.endsWith(lozinka)) {
				this.trenutnoUlogovan=ad;
			}
		}
	}
	public void odjava() {
		this.trenutnoUlogovan=null;
	}
	public void registrujAgenta(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String adresa,
			String korisnickoIme, String lozinka, StrucnaSprema sprema, int staz, double osnovnaPlata) {
		if(!this.svaKorisnickaImena.contains(korisnickoIme)) {
			Agent novi = new Agent(ime, prezime,pol, datumRodj,telefon, adresa,korisnickoIme, lozinka, sprema, staz, osnovnaPlata);
			sviAgenti.add(novi);
			sacuvajAgente(this.putanjaAgenti);
		}
	}
	public void registrujAdmina(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String adresa,
			String korisnickoIme, String lozinka, StrucnaSprema sprema, int staz, double osnovnaPlata) {
		if(!this.svaKorisnickaImena.contains(korisnickoIme)) {
			Admin novi = new Admin(ime, prezime, pol,  datumRodj, telefon, adresa,korisnickoIme, lozinka,  sprema, staz, osnovnaPlata);
			sviAdmini.add(novi);
			sacuvajAdmine(this.putanjaAdmini);
		}
	}
	public double izracunajPlatu(Zaposleni radnik) {
		double koeficijent = radnik.getSprema().getKoefijent();
		double plata = radnik.getOsnovnaPlata() *(koeficijent + 0.004*radnik.getStaz());
		return plata;
	}
	public void registrujNovogKlijenta(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String email,
			 String lozinka, KategorijaKlijenta kategorija, LocalDate datumVozacke,
			int brojKasnjenja) {
		if(!this.svaKorisnickaImena.contains(email)) {
			Klijent novi = new Klijent(ime, prezime,pol, datumRodj, telefon, email,lozinka, kategorija,datumVozacke, brojKasnjenja);
			sviKlijenti.add(novi);
			sacuvajKlijente(this.putanjaKlijeti);
		}
	}
	public Agent pronadjiAgentaPoKorisnickomImenu(String korIme) {
	    for (Agent m : this.sviAgenti) {
	        if (m.getKorisnickoIme().equals(korIme)) {
	            return m;
	        }
	    }
	    return null; 
	}
	public Admin pronadjiAdminaPoKorisnickomImenu(String korIme) {
	    for (Admin m : this.sviAdmini) {
	        if (m.getKorisnickoIme().equals(korIme)) {
	            return m;
	        }
	    }
	    return null; 
	}
	public Klijent pronadjiKlijentaPoKorisnickomImenu(String korIme) {
	    for (Klijent m : this.sviKlijenti) {
	        if (m.getKorisnickoIme().equals(korIme)) {
	            return m;
	        }
	    }
	    return null; 
	}
}
