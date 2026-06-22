package menadzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enums.KategorijaKlijenta;
import enums.Pol;
import enums.StrucnaSprema;
import enums.ZahtevPretplate;
import model.Admin;
import model.Agent;
import model.Klijent;
import model.Osoba;
import model.Pretplata;
import model.Zaposleni;

public class OsobaMenadzer {
	private ArrayList<Klijent> sviKlijenti;
	private ArrayList<Agent> sviAgenti;
	private ArrayList<Admin> sviAdmini;
	private final String putanjaKlijeti = "podaci/klijenti.csv";
	private final String putanjaAgenti = "podaci/agenti.csv";
	private final String putanjaAdmini = "podaci/admini.csv";
	private Osoba trenutnoUlogovan;
	
	public OsobaMenadzer(FinansijeMenadzer finMen) {
		this.sviKlijenti = new ArrayList<>();
		this.sviAgenti = new ArrayList<>();
		this.sviAdmini = new ArrayList<>();
		this.trenutnoUlogovan=null;
		
		ucitajKlijente(this.putanjaKlijeti, finMen);
		ucitajAgente(this.putanjaAgenti);
		ucitajAdmine(this.putanjaAdmini);
	}
	
	public String getPutanjaKlijeti() {
		return putanjaKlijeti;
	}

	public String getPutanjaAgenti() {
		return putanjaAgenti;
	}

	public String getPutanjaAdmini() {
		return putanjaAdmini;
	}

	private void ucitajKlijente(String putanjaKlijenti, FinansijeMenadzer finMen) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(putanjaKlijenti));
			for (String line: lines) {
				String[] parts = line.split(";");
				Klijent k = new Klijent(
						parts[0],								//ime
						parts[1],								//prezime
						Pol.valueOf(parts[2]),					//pol
						LocalDate.parse(parts[3]),				//datum Rodj
						parts[4],								//relfon
						parts[5],								//email
						parts[6],								//lozinka
						KategorijaKlijenta.valueOf(parts[7]),	//kategirjaKlijenta
						LocalDate.parse(parts[8]),				//datumVozacke
						Integer.parseInt(parts[9])				//brojKasnjenja
				);			
				LocalDate datumOtkazivanja = null;
	            if (!parts[10].equals("null")) {
	                datumOtkazivanja = LocalDate.parse(parts[10]);
	            }
	            k.setDatumOtkazivanja(datumOtkazivanja);
	            Pretplata pretplata = null;
	            if (!parts[11].equals("null")) {
	                int idPretplate =Integer.parseInt(parts[11]);
	                pretplata = finMen.PronadjiPretplatuPoId(idPretplate);
	            }
	            k.setPretplata(pretplata);
	            ZahtevPretplate zahtev = ZahtevPretplate.valueOf(parts[12]);
	            k.setZahtev(zahtev);
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
	                parts[5],                               // email
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
	                parts[5],                               // email
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
			String idPretplateS = (k.getPretplata() != null) ? String.valueOf(k.getPretplata().getIdPretplate()) : "null";
			String line = k.getIme() + ";" + 
                    k.getPrezime() + ";" + 
                    k.getPol() + ";" + 
                    k.getDatumRodj() + ";" + 
                    k.getTelefon() + ";" + 
                    k.getEmail() + ";" + 
                    k.getLozinka() + ";" + 
                    k.getKategorija() + ";" + 
                    k.getDatumVozacke() + ";" + 
                    k.getBrojKasnjenja()+ ";" + 
                    k.getDatumOtkazivanja()+ ";" + 
                    idPretplateS+ ";" +
                    k.getZahtev();
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
	                      a.getOsnovnaPlata();
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
	public boolean korisnickoImePostoji(String korIme) {
	    for(Admin a : sviAdmini) if(a.getKorisnickoIme().equals(korIme)) return true;
	    for(Agent a : sviAgenti) if(a.getKorisnickoIme().equals(korIme)) return true;
	    for(Klijent k : sviKlijenti) if(k.getKorisnickoIme().equals(korIme)) return true; // ili email za klijenta
	    return false;
	}
	public void logIn(String korisnickoIme, String lozinka) {
		for(Admin ad: this.sviAdmini ) {
			String korIme = ad.getKorisnickoIme();
			String loz = ad.getLozinka();
			if(korIme.equals(korisnickoIme) && loz.equals(lozinka)) {
				this.trenutnoUlogovan=ad;
			}
		}
		for(Agent ad: this.sviAgenti) {
			String korIme = ad.getKorisnickoIme();
			String loz = ad.getLozinka();
			if(korIme.equals(korisnickoIme) && loz.equals(lozinka)) {
				this.trenutnoUlogovan=ad;
			}
		}
		for(Klijent ad: this.sviKlijenti) {
			String korIme = ad.getKorisnickoIme();
			String loz = ad.getLozinka();
			String email = ad.getEmail();
			if((korIme.equals(korisnickoIme) || korIme.equals(email)) && loz.equals(lozinka)) {
				this.trenutnoUlogovan=ad;
			}
		}
	}
	public void odjava() {
		this.trenutnoUlogovan=null;
	}
	public void registrujAgenta(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String adresa,
			String korisnickoIme, String lozinka, StrucnaSprema sprema, int staz) {
		if(!korisnickoImePostoji(korisnickoIme)) {
			Agent novi = new Agent(ime, prezime,pol, datumRodj,telefon, adresa,korisnickoIme, lozinka, sprema, staz, 0);
			double osnovnaPlata = izracunajPlatu(novi);
			novi.setOsnovnaPlata(osnovnaPlata);
			sviAgenti.add(novi);
			sacuvajAgente(this.putanjaAgenti);
		}
	}
	public void registrujAdmina(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String adresa,
			String korisnickoIme, String lozinka, StrucnaSprema sprema, int staz) {
		if(!korisnickoImePostoji(korisnickoIme)) {
			Admin novi = new Admin(ime, prezime, pol,  datumRodj, telefon, adresa,korisnickoIme, lozinka,  sprema, staz, 0);
			double osnovnaPlata = izracunajPlatu(novi);
			novi.setOsnovnaPlata(osnovnaPlata);
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
		if(!korisnickoImePostoji(email)) {
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

	public Osoba getTrenutnoUlogovan() {
		return trenutnoUlogovan;
	}
	public Map<String, Double> odrediRashodeUPeriodu(LocalDate datumOd, LocalDate datumDo){
		Map<String, Double> rashodi = new HashMap<>();
		long brojDana = ChronoUnit.DAYS.between(datumOd, datumDo);
		double brojMeseci = brojDana/30;
		int brojMeseciInt = (int) brojMeseci;
		for(Admin ad: this.sviAdmini) {	
			String korIme = ad.getKorisnickoIme();
			Double plata = ad.getOsnovnaPlata()*brojMeseciInt;
			rashodi.put(korIme, plata);
		}
		for(Agent ad: this.sviAgenti) {
			String korIme = ad.getKorisnickoIme();
			Double plata = ad.getOsnovnaPlata()*brojMeseciInt;
			rashodi.put(korIme, plata);
		}
		return rashodi;
	}
	public void podnesiZahtev() {
		Klijent k = pronadjiKlijentaPoKorisnickomImenu(this.trenutnoUlogovan.getKorisnickoIme());
		if (k.getBrojKasnjenja() > 5) {
	        k.setZahtev(ZahtevPretplate.ODBIJEN); // Automatski se odbija 
	    } else {
	        k.setZahtev(ZahtevPretplate.POSLAT);
	    }
	    sacuvajKlijente(this.putanjaKlijeti);
	}
	public void odobriZahtev(Klijent k) {
		k.setZahtev(ZahtevPretplate.ODOBREN);
		sacuvajKlijente(this.putanjaKlijeti);
	}
	public void odbiZahtev(Klijent k) {
		k.setZahtev(ZahtevPretplate.ODBIJEN);
		sacuvajKlijente(this.putanjaKlijeti);
	}
	public void odbiSveStoKasne() {
		for(Klijent k: this.sviKlijenti) {
			if(k.getBrojKasnjenja()>5) {
				odbiZahtev(k);
			}
		}
		sacuvajKlijente(this.putanjaKlijeti);
	}
	public void obrisiAdmina(String korisnickoIme) {
		this.sviAdmini.removeIf(ad -> ad.getKorisnickoIme().equals(korisnickoIme));
		sacuvajAdmine(this.putanjaAdmini);
	}
	public void obrisiAgenta(String korisnickoIme) {
		this.sviAgenti.removeIf(ad -> ad.getKorisnickoIme().equals(korisnickoIme));
		sacuvajAgente(this.putanjaAgenti);
	}
	public void obrisiKlijenta(String korisnickoIme) {
		this.sviKlijenti.removeIf(ad -> ad.getKorisnickoIme().equals(korisnickoIme));
		sacuvajKlijente(this.putanjaKlijeti);
	}
	
}
