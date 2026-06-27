package menadzer;

import model.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enums.*;

public class FinansijeMenadzer {
	private ArrayList<Pretplata> svePretplate;
    private ArrayList<Cenovnik> sviCenovnici;
    private String putanjaPretplate;
    private String putanjaCenovnika;
    private Cenovnik trenutniCenovnik;
    
    public FinansijeMenadzer() {
        this.svePretplate = new ArrayList<>();
        this.sviCenovnici = new ArrayList<>();
        this.putanjaPretplate = "podaci/pretplate.csv";
        this.putanjaCenovnika = "podaci/cenovnici.csv";
    }

    public void ucitajPodatke(OsobaMenadzer osobaMen, RezervacijeMenadzer rezMen) {
        ucitajPretplate(this.putanjaPretplate, osobaMen);
        ucitajCenovnike(this.putanjaCenovnika, rezMen);
        this.trenutniCenovnik = OdrediTrenutniCenovnik();
    }

    public FinansijeMenadzer(String putanjaPretplate, String putanjaCenovnika, OsobaMenadzer osobaMen, RezervacijeMenadzer rezMen) {
        this.svePretplate = new ArrayList<>();
        this.sviCenovnici = new ArrayList<>();
        this.putanjaPretplate = putanjaPretplate;
        this.putanjaCenovnika = putanjaCenovnika;
        ucitajPretplate(putanjaPretplate, osobaMen);
        ucitajCenovnike(putanjaCenovnika, rezMen);
        this.trenutniCenovnik = OdrediTrenutniCenovnik();
    }
	
	public String getPutanjaPretplate() {
		return putanjaPretplate;
	}

	public String getPutanjaCenovnika() {
		return putanjaCenovnika;
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
	                LocalDate.parse(parts[2]),          // datumPocetak //datumKraja je na 3 mestu
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
	                      c.getKaznaZaKasnjenje()+ ";" + 
	                      c.getPopustZaKategorije()+ ";" + 
	                      c.getDaniNajma();
	        
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
	                Double.parseDouble(parts[4]),            // kaznaZaKasnjenje
	                Double.parseDouble(parts[5]),            // popustZaKategorije
	                Integer.parseInt(parts[6])             // daniNajma
	            );
	            
	            // 1. Rekonstrukcija mape cenaNajma (parts[5]) -> KategorijaVozila (Enum)
	            if (parts.length > 7 && !parts[7].trim().isEmpty()) {
	                String[] paroviNajma = parts[7].split(",");
	                for (String par : paroviNajma) {
	                    String[] kljucIValue = par.split(":");
	                    KategorijaVozila kat = KategorijaVozila.valueOf(kljucIValue[0].trim());
	                    double cena = Double.parseDouble(kljucIValue[1].trim());
	                    c.setCenaNajma(kat, cena); // Koristimo tvoj seter iz klase
	                }
	            }
	            
	            // 2. Rekonstrukcija mape cenaDodatneUsluge (parts[6]) -> Objekat DodatnaUsluga
	            if (parts.length > 8 && !parts[8].trim().isEmpty()) {
	                String[] paroviUsluga = parts[8].split(",");
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
	public Cenovnik OdrediTrenutniCenovnik() {
		LocalDate danas = LocalDate.now();
		for(Cenovnik c: this.sviCenovnici) {
			if(!c.getDatumKraja().isBefore(danas) && !c.getDatumPocetka().isAfter(danas)) {
				return c;
			}
		}
		return null;
	}
	public Pretplata PronadjiPretplatuPoId(int idPretplate) {
		for(Pretplata p: this.svePretplate) {
			if(p.getIdPretplate()==idPretplate) {
				return p;
			}
		}
		return null;
	}
	public void promeniDaneNajma(int daniNajma) {
		this.trenutniCenovnik.setDaniNajma(daniNajma);
		sacuvajCenovnike(this.putanjaCenovnika);
	}
	public int generisiNoviIdCenovnika() {
		int max = 0;
		for(Cenovnik c : this.sviCenovnici) {
			if(c.getIdCenovnika() > max) {
				max = c.getIdCenovnika();
			}
		}
		return max + 1;
	}
	public int generisiNoviIdPretplate() {
		int max = 0;
		for(Pretplata p : this.svePretplate) {
			if(p.getIdPretplate() > max) {
				max = p.getIdPretplate();
			}
		}
		return max + 1;
	}
	public void dodajSveCeneDodatnimUslugama(Cenovnik c, RezervacijeMenadzer rezMen) {
		for(DodatnaUsluga du: rezMen.sveDodatneUsluge) {
			double cena =0;//treba u gui
			c.setCenaDodatneUsluge(du, cena);
		}
	}
	public void dodajSveDodatneUslugeCene(Cenovnik c, RezervacijeMenadzer rezMen) {
		int koliko = 0;//treba u gui
		DodatnaUsluga d = new DodatnaUsluga(1, "Produzeni dan");
		double cenaProduzenogDana =0;//treba u gui
		c.setCenaDodatneUsluge(d, cenaProduzenogDana);
		for(int i=0; i<=koliko; i++) {
			int idDodatneUsluge = rezMen.generisiNoviIdDodatneUsluge();
			String naziv = "";//treba u gui
			DodatnaUsluga du = new DodatnaUsluga(idDodatneUsluge, naziv);
			double cena =0;
			c.setCenaDodatneUsluge(du, cena);
		}
	}
	public void dodajJosJednuDodatnuUslugu(Cenovnik c, RezervacijeMenadzer rezMen, String nazivDodatneUsluge, double cenaDodatneUsluge) {
		int idDodatneUsluge = rezMen.generisiNoviIdDodatneUsluge();
		DodatnaUsluga du = new DodatnaUsluga(idDodatneUsluge, nazivDodatneUsluge);
		c.setCenaDodatneUsluge(du, cenaDodatneUsluge);
	}
	public void odrediCenuProduzenogDana(Cenovnik c, RezervacijeMenadzer rezMen, double cenaProduzenogDana) {
		DodatnaUsluga d = new DodatnaUsluga(1, "Produzeni dan");
		c.setCenaDodatneUsluge(d, cenaProduzenogDana);
	}
	public void dodajSveCeneNajma(Cenovnik c) {
		for(KategorijaVozila k: KategorijaVozila.values()) {
			double cena = 0;//treba u gui
			c.setCenaNajma(k, cena);
		}
	}
	//treba i proveriti da li su dani vazenja cenovnika slobodni tj da se ne poslepaju s drugim cenovnicima
	public boolean isDostupniDaniCenovika(LocalDate datumPocetka, LocalDate datumKraja) {//treba pozvati u gui kad bude unosio datume za cenovnik
		for(Cenovnik c: this.sviCenovnici) {
			if (c.getDatumPocetka().isBefore(datumKraja) && c.getDatumKraja().isAfter(datumPocetka)) {
	            return false; // Pronađeno je preklapanje, dani NISU dostupni!
	        }
		}
		return true;
	}
	public ArrayList<Pretplata> odrediPrihodeZbogPretplataUPeriodu(LocalDate datumOd, LocalDate datumDo){
		ArrayList<Pretplata> lista = new ArrayList<>();
		for(Pretplata p: this.svePretplate) {
			if (!p.getDatumPocetak().isBefore(datumOd) && !p.getDatumPocetak().isAfter(datumDo)) {
				lista.add(p);
			}
		}
		return lista;
	}
	public void napraviCenovnik(LocalDate datumPocetka, LocalDate datumKraja, double cenaGodisnjePretplate, double kaznaZaKasnjenje,
			double popustZaKategorije, int daniNajma, RezervacijeMenadzer rezMen, double cenaProduzenogDana) {
		int idCenovnika = generisiNoviIdCenovnika();
		Cenovnik c = new Cenovnik(idCenovnika, datumPocetka, datumKraja, cenaGodisnjePretplate,
				kaznaZaKasnjenje,popustZaKategorije,daniNajma);
		odrediCenuProduzenogDana(c, rezMen, cenaProduzenogDana);
		dodajSveCeneNajma(c);
		sacuvajCenovnike(this.putanjaCenovnika);
	}
	public void napraviNovuPretplatu(Klijent klijent, OsobaMenadzer osobMen) {
		if(klijent.getBrojKasnjenja()<=5 && klijent.getZahtev().equals(ZahtevPretplate.ODOBREN)) {
			int idPretplate = generisiNoviIdPretplate();
			LocalDate datumPocetak = LocalDate.now();
			double cena = this.trenutniCenovnik.getCenaGodisnjePretplate();
			Pretplata p = new Pretplata(idPretplate, klijent, datumPocetak, cena);
			this.svePretplate.add(p);
			klijent.setPretplata(p);
			klijent.setZahtev(ZahtevPretplate.NEMA);
			osobMen.sacuvajKlijente(osobMen.getPutanjaKlijeti());
			sacuvajPretplate(this.putanjaPretplate);
		}
		else{
			//ispisi da ne moze u gui
		}
	}
	public void produziPretplatu(Pretplata p, OsobaMenadzer osobMen) {
		double cena = this.trenutniCenovnik.getCenaGodisnjePretplate();
		Klijent k = p.getKlijent();
		if(k.getBrojKasnjenja()<=5 && k.getZahtev().equals(ZahtevPretplate.ODOBREN)) {
			LocalDate noviKraj = p.getDatumKraj().plusYears(1);
			p.setDatumKraj(noviKraj);
			p.setCena(cena);
			k.setPretplata(p);
			k.setZahtev(ZahtevPretplate.NEMA);
			osobMen.sacuvajKlijente(osobMen.getPutanjaKlijeti());
			sacuvajPretplate(this.putanjaPretplate);
		}
		else {
			//ispisi da ne moze u gui
		}
	}
	public void obrisiCenovnik(int idCenovnika) {
		this.sviCenovnici.removeIf(c -> c.getIdCenovnika() == idCenovnika);
		sacuvajCenovnike(this.putanjaCenovnika);
	}
	public void obrisiPretplatu(int idPretplate) {
		this.svePretplate.removeIf(c -> c.getIdPretplate() == idPretplate);
		sacuvajPretplate(this.putanjaPretplate);
	}
	public void izmeniPretplatu(Pretplata pretplata, Klijent klijent, LocalDate datumPocetak,LocalDate datumKraj, double cena) {
		if (klijent != null) { pretplata.setKlijent(klijent); }
	    if (datumPocetak != null) { pretplata.setDatumPocetak(datumPocetak); }
	    if (datumKraj != null) { pretplata.setDatumKraj(datumKraj); }
	    if (cena > 0) { pretplata.setCena(cena); }
	    sacuvajPretplate(this.putanjaPretplate);
	}
	public void izmeniCenovnik(Cenovnik cenovnik, LocalDate datumPocetka, LocalDate datumKraja, double cenaGodisnjePretplate,
			double kaznaZaKasnjenje, double popustZaKategorije, int daniNajma,
			Map<KategorijaVozila, Double> cenaNajma, Map<DodatnaUsluga, Double> cenaDodatneUsluge) {
		if (datumPocetka != null) { cenovnik.setDatumPocetka(datumPocetka); }
	    if (datumKraja != null) { cenovnik.setDatumKraja(datumKraja); }
	    if (cenaGodisnjePretplate > 0) { cenovnik.setCenaGodisnjePretplate(cenaGodisnjePretplate); }
	    if (kaznaZaKasnjenje > 0) { cenovnik.setKaznaZaKasnjenje(kaznaZaKasnjenje); }
	    if (popustZaKategorije >= 0) { cenovnik.setPopustZaKategorije(popustZaKategorije); }
	    if (daniNajma > 0) { cenovnik.setDaniNajma(daniNajma); }
	    if (cenaNajma != null && !cenaNajma.isEmpty()) { cenovnik.setCenaNajmaFull(cenaNajma); }
	    if (cenaDodatneUsluge != null && !cenaDodatneUsluge.isEmpty()) { cenovnik.setCenaDodatneUslugeFull(cenaDodatneUsluge); }
		sacuvajCenovnike(this.putanjaCenovnika);
	}

	public ArrayList<Pretplata> getSvePretplate() {
		return svePretplate;
	}

	public void setSvePretplate(ArrayList<Pretplata> svePretplate) {
		this.svePretplate = svePretplate;
	}

	public ArrayList<Cenovnik> getSviCenovnici() {
		return sviCenovnici;
	}

	public void setSviCenovnici(ArrayList<Cenovnik> sviCenovnici) {
		this.sviCenovnici = sviCenovnici;
	}
	public void napraviCenovnik(LocalDate datumPocetka, LocalDate datumKraja, double cenaGodisnjePretplate, double kaznaZaKasnjenje,
			double popustZaKategorije, int daniNajma, RezervacijeMenadzer rezMen, double cenaProduzenogDana, 
			java.util.Map<String, Double> privremeneUsluge, java.util.Map<enums.KategorijaVozila, Double> ceneKategorija) {
		
		int idCenovnika = generisiNoviIdCenovnika();
		Cenovnik c = new Cenovnik(idCenovnika, datumPocetka, datumKraja, cenaGodisnjePretplate,
				kaznaZaKasnjenje, popustZaKategorije, daniNajma);
		
		odrediCenuProduzenogDana(c, rezMen, cenaProduzenogDana);
		
		// Umesto stare metode sa nulama, punimo cenovnik unetim cenama iz GUI-ja:
		for (java.util.Map.Entry<enums.KategorijaVozila, Double> entry : ceneKategorija.entrySet()) {
			c.setCenaNajma(entry.getKey(), entry.getValue());
		}
		
		// Punjenje dodatnih usluga
		for (java.util.Map.Entry<String, Double> entry : privremeneUsluge.entrySet()) {
			dodajJosJednuDodatnuUslugu(c, rezMen, entry.getKey(), entry.getValue());
		}
		
		this.sviCenovnici.add(c); // ili kako god da ti se zove lista u menadžeru
		sacuvajCenovnike(this.putanjaCenovnika);
	}
	
	

}
