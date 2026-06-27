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
	private String putanjaKlijenti;
    private String putanjaAgenti;
    private String putanjaAdmini;
	private Osoba trenutnoUlogovan;
	
	public OsobaMenadzer() {
	    this.sviKlijenti = new ArrayList<>();
	    this.sviAgenti = new ArrayList<>();
	    this.sviAdmini = new ArrayList<>();
	    this.trenutnoUlogovan = null;
	    this.putanjaKlijenti = "podaci/klijenti.csv";
        this.putanjaAgenti = "podaci/agenti.csv";
        this.putanjaAdmini = "podaci/admini.csv";
	}

	public void ucitajPodatke() {
	    ucitajKlijente(this.putanjaKlijenti);
	    ucitajAgente(this.putanjaAgenti);
	    ucitajAdmine(this.putanjaAdmini);
	}
	public OsobaMenadzer(String putanjaKlijenti, String putanjaAgenti, String putanjaAdmini) {
		this.sviKlijenti = new ArrayList<>();
	    this.sviAgenti = new ArrayList<>();
	    this.sviAdmini = new ArrayList<>();
	    this.trenutnoUlogovan = null;
	    this.putanjaKlijenti = putanjaKlijenti;
	    this.putanjaAgenti = putanjaAgenti;
	    this.putanjaAdmini = putanjaAdmini;
	    ucitajKlijente(putanjaKlijenti);
	    ucitajAgente(putanjaAgenti);
	    ucitajAdmine(putanjaAdmini);
	}
	
	public String getPutanjaKlijeti() {
		return this.putanjaKlijenti;
	}

	public String getPutanjaAgenti() {
		return this.putanjaAgenti;
	}

	public String getPutanjaAdmini() {
		return this.putanjaAdmini;
	}

	private void ucitajKlijente(String putanjaKlijenti) {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(putanjaKlijenti));
	        for (String line: lines) {
	            String[] parts = line.split(";");
	            Klijent k = new Klijent(
	                parts[0], parts[1], Pol.valueOf(parts[2]), LocalDate.parse(parts[3]),
	                parts[4], parts[5], parts[7], KategorijaKlijenta.valueOf(parts[8]),
	                LocalDate.parse(parts[9]), Integer.parseInt(parts[10])
	                //Lana;Lukić;ZENSKO;2002-05-14;063111222;c;c;c;BEZ_KATEGORIJE;2021-06-15;0;null;1;NEMA
	            );            
	            LocalDate datumOtkazivanja = parts[11].equals("null") ? null : LocalDate.parse(parts[11]);
	            k.setDatumOtkazivanja(datumOtkazivanja);
	            
	            // Privremeno stavljamo null, jer pretplate još nisu učitane
	            k.setPretplata(null); 
	            
	            ZahtevPretplate zahtev = ZahtevPretplate.valueOf(parts[13]);
	            k.setZahtev(zahtev);
	            this.sviKlijenti.add(k);
	        }
	    } catch(IOException e) {
	        e.printStackTrace();
	    }
	}
	public void poveziKlijenteSaPretplatama(FinansijeMenadzer finMen, String putanjaKlijenti) {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(putanjaKlijenti));
	        for (String line : lines) {
	            String[] parts = line.split(";");
	            String emailKlijenta = parts[5]; // email/korisničko ime klijenta
	            
	            if (!parts[12].equals("null")) {
	                int idPretplate = Integer.parseInt(parts[12]);
	                Klijent k = this.pronadjiKlijentaPoKorisnickomImenu(emailKlijenta);
	                Pretplata p = finMen.PronadjiPretplatuPoId(idPretplate);
	                
	                if (k != null && p != null) {
	                    k.setPretplata(p);
	                }
	            }
	        }
	    } catch (IOException e) {
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
                    k.getKorisnickoIme() + ";" + 
                    k.getEmail() + ";" + 
                    k.getLozinka() + ";" + 
                    k.getKategorija() + ";" + 
                    k.getDatumVozacke() + ";" + 
                    k.getBrojKasnjenja()+ ";" + 
                    k.getDatumOtkazivanja()+ ";" + 
                    idPretplateS+ ";" +
                    k.getZahtev();
			lines.add(line);
			//Lana;Lukić;ZENSKO;2002-05-14;063111222;c;c;c;BEZ_KATEGORIJE;2021-06-15;0;null;1;NEMA
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
	public Osoba logIn(String korisnickoIme, String lozinka) {
	    for(Admin ad: this.sviAdmini ) {
	        if(ad.getKorisnickoIme().equals(korisnickoIme) && ad.getLozinka().equals(lozinka)) {
	            this.trenutnoUlogovan = ad;
	            return ad;
	        }
	    }
	    for(Agent ag: this.sviAgenti) {
	        if(ag.getKorisnickoIme().equals(korisnickoIme) && ag.getLozinka().equals(lozinka)) {
	            this.trenutnoUlogovan = ag;
	            return ag;
	        }
	    }
	    for(Klijent kl: this.sviKlijenti) {
	        if((kl.getKorisnickoIme().equals(korisnickoIme) || kl.getEmail().equals(korisnickoIme)) && kl.getLozinka().equals(lozinka)) {
	            this.trenutnoUlogovan = kl;
	            return kl;
	        }
	    }
	    return null; // Pogrešno korisničko ime ili lozinka
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
		double plata = radnik.getOsnovac() *(koeficijent + 0.004*radnik.getStaz());
		return plata;
	}
	public void registrujNovogKlijenta(String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String email,
			 String lozinka, KategorijaKlijenta kategorija, LocalDate datumVozacke,
			int brojKasnjenja) {
		if(!korisnickoImePostoji(email)) {
			Klijent novi = new Klijent(ime, prezime,pol, datumRodj, telefon, email,lozinka, kategorija,datumVozacke, brojKasnjenja);
			sviKlijenti.add(novi);
			sacuvajKlijente(this.putanjaKlijenti);
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
	public double ukupanRaskohUPeriodu(LocalDate datumOd, LocalDate datumDo) {
		Map<String, Double> rashodi =odrediRashodeUPeriodu(datumOd, datumDo);
		double ukupno = 0;
		for (Double vrednost : rashodi.values()) {
			ukupno = ukupno + vrednost;
		}
		return ukupno;
	}
	public void podnesiZahtev() {
		Klijent k = pronadjiKlijentaPoKorisnickomImenu(this.trenutnoUlogovan.getKorisnickoIme());
		if (k.getBrojKasnjenja() > 5) {
	        k.setZahtev(ZahtevPretplate.ODBIJEN); // Automatski se odbija 
	    } else {
	        k.setZahtev(ZahtevPretplate.POSLAT);
	    }
	    sacuvajKlijente(this.putanjaKlijenti);
	}
	public void odobriZahtev(Klijent k) {
		if(k.getBrojKasnjenja()<6) {
			k.setZahtev(ZahtevPretplate.ODOBREN);
			sacuvajKlijente(this.putanjaKlijenti);
		}
	}
	public void odbiZahtev(Klijent k) {
		k.setZahtev(ZahtevPretplate.ODBIJEN);
		sacuvajKlijente(this.putanjaKlijenti);
	}
	public void odbiSveStoKasne() {
		for(Klijent k: this.sviKlijenti) {
			if(k.getBrojKasnjenja()>5) {
				odbiZahtev(k);
			}
		}
		sacuvajKlijente(this.putanjaKlijenti);
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
		sacuvajKlijente(this.putanjaKlijenti);
	}
	public void izmeniAgenta(Agent agent, String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String email,
			String korisnickoIme, String lozinka, StrucnaSprema sprema, int staz, double osnovnaPlata) {
		if (ime != null) { agent.setIme(ime); }
	    if (prezime != null) { agent.setPrezime(prezime); }
	    if (pol != null) { agent.setPol(pol); }
	    if (datumRodj != null) { agent.setDatumRodj(datumRodj); }
	    if (telefon != null) { agent.setTelefon(telefon); }
	    if (email != null) { agent.setEmail(email); }
	    if (korisnickoIme != null && !korisnickoImePostoji(korisnickoIme)) { agent.setKorisnickoIme(korisnickoIme); }
	    if (lozinka != null) { agent.setLozinka(lozinka); }
	    if (sprema != null) { agent.setSprema(sprema); }
	    if( staz>=0) {agent.setStaz(staz);}
	    agent.setOsnovnaPlata(izracunajPlatu(agent));
	    sacuvajAgente(this.putanjaAgenti);
	}
	public ArrayList<Klijent> getSviKlijenti() {
		return sviKlijenti;
	}

	public void setSviKlijenti(ArrayList<Klijent> sviKlijenti) {
		this.sviKlijenti = sviKlijenti;
	}

	public ArrayList<Agent> getSviAgenti() {
		return sviAgenti;
	}

	public void setSviAgenti(ArrayList<Agent> sviAgenti) {
		this.sviAgenti = sviAgenti;
	}

	public ArrayList<Admin> getSviAdmini() {
		return sviAdmini;
	}

	public void setSviAdmini(ArrayList<Admin> sviAdmini) {
		this.sviAdmini = sviAdmini;
	}

	public void izmeniAdmina(Admin agent, String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String email,
			String korisnickoIme, String lozinka, StrucnaSprema sprema, int staz, double osnovnaPlata) {
		if (ime != null) { agent.setIme(ime); }
	    if (prezime != null) { agent.setPrezime(prezime); }
	    if (pol != null) { agent.setPol(pol); }
	    if (datumRodj != null) { agent.setDatumRodj(datumRodj); }
	    if (telefon != null) { agent.setTelefon(telefon); }
	    if (email != null) { agent.setEmail(email); }
	    if (korisnickoIme != null && !korisnickoImePostoji(korisnickoIme)) { agent.setKorisnickoIme(korisnickoIme); }
	    if (lozinka != null) { agent.setLozinka(lozinka); }
	    if (sprema != null) { agent.setSprema(sprema); }
	    if( staz>=0) {agent.setStaz(staz);}
	    agent.setOsnovnaPlata(izracunajPlatu(agent));
	    sacuvajAdmine(this.putanjaAdmini);
	}
	public void izmeniKlijenta(Klijent klijent, String ime, String prezime, Pol pol, LocalDate datumRodj, String telefon, String email,
			 String lozinka, KategorijaKlijenta kategorija, LocalDate datumVozacke, int brojKasnjenja, Pretplata pretplata, ZahtevPretplate zahtev) {
		if (ime != null) { klijent.setIme(ime); }
	    if (prezime != null) { klijent.setPrezime(prezime); }
	    if (pol != null) { klijent.setPol(pol); }
	    if (datumRodj != null) { klijent.setDatumRodj(datumRodj); }
	    if (telefon != null) { klijent.setTelefon(telefon); }
	    if (email != null && !korisnickoImePostoji(email)) { klijent.setKorisnickoIme(email); klijent.setEmail(email); }
	    if (lozinka != null) { klijent.setLozinka(lozinka); }
	    if (kategorija != null) { klijent.setKategorija(kategorija); }
	    if (datumVozacke != null) { klijent.setDatumVozacke(datumVozacke); }
	    if (brojKasnjenja>=0) {klijent.setBrojKasnjenja(brojKasnjenja);}
	    if (pretplata!= null) {klijent.setPretplata(pretplata);}
	    if (zahtev != null) {klijent.setZahtev(zahtev);}
	    sacuvajKlijente(this.putanjaKlijenti);
	}

	public String getPutanjaKlijenti() {
		return putanjaKlijenti;
	}
	public void setTrenutnoUlogovan(Osoba trenutnoUlogovan) {
		this.trenutnoUlogovan = trenutnoUlogovan;
	}
	
}
