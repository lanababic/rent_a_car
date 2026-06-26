package menadzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import enums.KategorijaKlijenta;
import enums.KategorijaVozila;
import enums.StatusRezervacije;
import model.Cenovnik;
import model.DodatnaUsluga;
import model.IzdavanjeVozila;
import model.Klijent;
import model.ModelVozila;
import model.Osoba;
import model.Rezervacija;
import model.Vozilo;

public class RezervacijeMenadzer {
	private ArrayList<Rezervacija> sveRezervacije;
	public ArrayList<DodatnaUsluga> sveDodatneUsluge;
	private final String putanjaRezervacije = "podaci/rezervacije.csv";
	private final String putanjaDodatneUsluge = "podaci/dodatneUsluge.csv";
	
	public RezervacijeMenadzer() {
	    this.sveRezervacije = new ArrayList<>();
	    this.sveDodatneUsluge = new ArrayList<>();
	}

	public void ucitajPodatke(VoziloMenadzer vozMen, OsobaMenadzer osobMen) {
	    ucitajDodatneUsluge(this.putanjaDodatneUsluge);
	    ucitajRezervacije(this.putanjaRezervacije, vozMen, osobMen);
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
	                      r.getDatumPravljenja()+ ";" + 
	                      (r.getKlijent() != null ? r.getKlijent().getKorisnickoIme() : "null");	        
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
	private void ucitajRezervacije(String putanjaRezervacije, VoziloMenadzer vozMen, OsobaMenadzer osobMen) {
	    try {
	        List<String> lines = Files.readAllLines(Paths.get(putanjaRezervacije));
	        for (String line : lines) {
	            String[] parts = line.split(";");
	            
	            int idModela = Integer.parseInt(parts[1]);
	            String korIme= parts[7];
	            ModelVozila pronadjeniModel = vozMen.pronadjiModelPoId(idModela);
	            Klijent pronadjeniKlijent = osobMen.pronadjiKlijentaPoKorisnickomImenu(korIme);
	            
	            Rezervacija r = new Rezervacija(
	                Integer.parseInt(parts[0]),             // idRezervacije
	                pronadjeniModel,                        // modelVozila
	                LocalDate.parse(parts[2]),              // datumOd
	                LocalDate.parse(parts[3]),              // datumDo
	                Double.parseDouble(parts[5]),			// osnovnaCena
	                LocalDate.parse(parts[6]),               // datumPravljenja
	                pronadjeniKlijent						//klijent koji je rezervisao
	            );
	            StatusRezervacije stvarniStatus = StatusRezervacije.valueOf(parts[4]);
	            r.setStatus(stvarniStatus); 
	            if(parts.length > 8 && !parts[8].trim().isEmpty() && !parts[8].equals("null")) {
	            	String[] idDodatnihUsluga = parts[8].split(",");
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
	public ArrayList<Rezervacija> getSveRezervacije() {
		return sveRezervacije;
	}

	public void setSveRezervacije(ArrayList<Rezervacija> sveRezervacije) {
		this.sveRezervacije = sveRezervacije;
	}

	public ArrayList<DodatnaUsluga> getSveDodatneUsluge() {
		return sveDodatneUsluge;
	}

	public void setSveDodatneUsluge(ArrayList<DodatnaUsluga> sveDodatneUsluge) {
		this.sveDodatneUsluge = sveDodatneUsluge;
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
	public void potvrdiRezervaciju(Rezervacija r, VoziloMenadzer vozMen, IzdavanjeMenadzer izdMen){
		boolean imaVozila = izdMen.isModelDostupan(r.getModelVozila(), r.getDatumOd(), r.getDatumDo(), vozMen);
		if(r.getStatus().equals(StatusRezervacije.NA_CEKANJU) && imaVozila) {
			r.setStatus(StatusRezervacije.POTVRDJENO);
		}
		else {
			//treba da kaze da nije na cekanju
		}
		sacuvajRezervacije(this.putanjaRezervacije);
	}
	public void odbiIstekleRezervacije(Rezervacija rezervacija) {
		LocalDate danas = LocalDate.now();
		if(rezervacija.getDatumOd().equals(danas) && rezervacija.getStatus().equals(StatusRezervacije.NA_CEKANJU)) {
			rezervacija.setStatus(StatusRezervacije.ODBIJENO);
		}
		if(rezervacija.getDatumOd().isBefore(danas) && rezervacija.getStatus().equals(StatusRezervacije.NA_CEKANJU)) {
			rezervacija.setStatus(StatusRezervacije.ODBIJENO);
		}
		sacuvajRezervacije(this.putanjaRezervacije);
	}
	public void odbiRezervaciju(Rezervacija r){
		if(r.getStatus().equals(StatusRezervacije.NA_CEKANJU)) {
			r.setStatus(StatusRezervacije.ODBIJENO);
		}
		else {
			//treba da kaze da nije na cekanju
		}
		sacuvajRezervacije(this.putanjaRezervacije);
	}
	public void otkaziRezervacijuKlijent(Rezervacija r, OsobaMenadzer osobMen) {
		LocalDate danas = LocalDate.now();
		if(r.getStatus().equals(StatusRezervacije.NA_CEKANJU) || r.getStatus().equals(StatusRezervacije.POTVRDJENO)) {
			r.setStatus(StatusRezervacije.OTKAZANO);
			r.getKlijent().setDatumOtkazivanja(danas);
		}
		
		osobMen.sacuvajKlijente(osobMen.getPutanjaKlijeti());
		sacuvajRezervacije(this.putanjaRezervacije);
	}
	public void otkaziRezervacijuZaposleni(Rezervacija r, OsobaMenadzer osobMen) {
		r.setStatus(StatusRezervacije.OTKAZANO);
		sacuvajRezervacije(this.putanjaRezervacije);
	}//mozda \gent ne moza da otkaze recervaije
	
	public void otkaziRezervacijeBezPojave(OsobaMenadzer osobMen, IzdavanjeMenadzer izdMen) {
		LocalDate danas = LocalDate.now();
		for(Rezervacija r: this.sveRezervacije) {
			if(r.getStatus().equals(StatusRezervacije.POTVRDJENO) && r.getDatumOd().isBefore(danas)) {
				otkaziRezervacijuKlijent(r,osobMen);
			}
		}
		sacuvajRezervacije(this.putanjaRezervacije);
	}
	public boolean isOtkazaoUPoslenjih24h(Klijent klijent) {
		LocalDate danas = LocalDate.now();
		LocalDate danas24h = LocalDate.now().minusDays(1);
		LocalDate datumOtkazivanja = klijent.getDatumOtkazivanja();
		if (datumOtkazivanja == null) {
	        return false;
	    }
		if(datumOtkazivanja.isAfter(danas24h) && datumOtkazivanja.isBefore(danas)) {
			return true;
		}
		return false;
	}
	public LocalDate racunanjeDatumaDo(LocalDate datumOd, ArrayList<DodatnaUsluga> listaDodatnih, FinansijeMenadzer finMen) {
		int dodatniDani = 0;
		for(DodatnaUsluga u: listaDodatnih) {
			if(u.getNaziv().equals("Produzeni dan")) {
				dodatniDani++;
			}
		}
		Cenovnik trenutniCenovnik= finMen.OdrediTrenutniCenovnik();
		int minDani = trenutniCenovnik.getDaniNajma();
		int ukupnoDana = minDani+dodatniDani;
		LocalDate datumDo = datumOd.plusDays(ukupnoDana);
		return datumDo;
		
	}
	public boolean imaliSlobodnihVozilaTogModela(VoziloMenadzer vozMen, ModelVozila modelVozila) {
		ArrayList<Vozilo> lista = vozMen.pronadjiVozilaPoModelu(modelVozila);
		return lista.isEmpty();
	}
	public int generisiNoviIdRezervacije() {
		int max = 0;
		for(Rezervacija r : this.sveRezervacije) {
			if(r.getIdRezervacije() > max) {
				max = r.getIdRezervacije();
			}
		}
		return max + 1;
	}
	public double izracunajOsnovnuCenu(Rezervacija r, FinansijeMenadzer finMen) {
		//long brojDana = ChronoUnit.DAYS.between(r.getDatumOd(), r.getDatumDo());
		Cenovnik trenutniCenovnik= finMen.OdrediTrenutniCenovnik();
		KategorijaVozila kategorija = r.getModelVozila().getKategorijaVozila();
		ArrayList<DodatnaUsluga> listaDodatnih = r.getListaDodatnihUsluga();
		double cenaPoDanuObican = trenutniCenovnik.getCenaNajmaKonkretno(kategorija);
		int osnovniDani = trenutniCenovnik.getDaniNajma();
		double cenaDana = cenaPoDanuObican*osnovniDani;
		double cenaDodatnih = 0;
		for(DodatnaUsluga u: listaDodatnih) {
			cenaDodatnih = cenaDodatnih+trenutniCenovnik.getCenaDodatneUslugeKonkretno(u);
		}
		double osnovnaCena = cenaDana+cenaDodatnih;
		return osnovnaCena;
	}
	public void predloziDodatneUsluge(ArrayList<DodatnaUsluga> listaDodatnih) {
		int koliko = 0;//preko gui ce biti neki broj
		for(DodatnaUsluga u: this.sveDodatneUsluge) {
			koliko = 0;
			for(int i =0; i<koliko; i++) {
				listaDodatnih.add(u);
			}
		}
	}
	public void napraviRezervaciju( ModelVozila modelVozila, LocalDate datumOd, Klijent klijent, FinansijeMenadzer finMen) {
		Cenovnik trenutniCenovnik= finMen.OdrediTrenutniCenovnik();
		int idRezervacije = generisiNoviIdRezervacije();
		LocalDate datumPravljenja = LocalDate.now();
		if(klijent.getDatumVozacke().isBefore(LocalDate.now().minusYears(2)) && !isOtkazaoUPoslenjih24h(klijent) ) {
			ArrayList<DodatnaUsluga> listaDodatnih = new ArrayList<>();
			predloziDodatneUsluge(listaDodatnih);
			LocalDate datumDo = racunanjeDatumaDo(datumOd, listaDodatnih, finMen);
			Rezervacija rezervacija = new Rezervacija(idRezervacije, modelVozila, datumOd, datumDo, 0,datumPravljenja, klijent);
			rezervacija.setListaDodatnihUsluga(listaDodatnih);
			double osnovnaCena = izracunajOsnovnuCenu(rezervacija, finMen);
			rezervacija.setOsnovnaCena(osnovnaCena);
			if(!klijent.getKategorija().equals(KategorijaKlijenta.BEZ_KATEGORIJE)) {
				double osnCena = rezervacija.getOsnovnaCena();
				osnCena = osnCena*(1-trenutniCenovnik.getPopustZaKategorije());
				rezervacija.setOsnovnaCena(osnCena);
			}
			this.sveRezervacije.add(rezervacija);
			sacuvajRezervacije(this.putanjaRezervacije);
		}
		else {
			//treba da ispise da ne moze
		}
	}//nije gotova metoda

	public String getPutanjaRezervacije() {
		return putanjaRezervacije;
	}

	public String getPutanjaDodatneUsluge() {
		return putanjaDodatneUsluge;
	}
	public ArrayList<Rezervacija> ucitajSveRezervacijeOdDana(LocalDate datumDana){
		ArrayList<Rezervacija> lista = new ArrayList<>();
		for(Rezervacija r: this.sveRezervacije) {
			if(r.getDatumPravljenja().equals(datumDana)) {
				lista.add(r);
			};
		}
		return lista;
	}
	public ArrayList<Rezervacija> ucitajSveRezervacijeOdKlijenta(Klijent klijent){
		ArrayList<Rezervacija> lista = new ArrayList<>();
		for(Rezervacija r: this.sveRezervacije) {
			if(r.getKlijent().equals(klijent)) {
				lista.add(r);
			}
		}
		return lista;
	}
	public ArrayList<Rezervacija> ucitajSveSvojeRezervacije(OsobaMenadzer osobMen){
		Osoba osoba = osobMen.getTrenutnoUlogovan();
		Klijent k = osobMen.pronadjiKlijentaPoKorisnickomImenu(osoba.getKorisnickoIme());
		return ucitajSveRezervacijeOdKlijenta(k);
	}
	public int generisiNoviIdDodatneUsluge() {
		int max = 0;
		for(DodatnaUsluga r : this.sveDodatneUsluge) {
			if(r.getIdDodatneUsluge() > max) {
				max = r.getIdDodatneUsluge();
			}
		}
		return max + 1;
	}
	public void dodajNovuDodatnuUslugu(String naziv) {
		int idDodatneUsluge = generisiNoviIdDodatneUsluge();
		DodatnaUsluga u = new  DodatnaUsluga(idDodatneUsluge, naziv);
		this.sveDodatneUsluge.add(u);
		sacuvajDodatneUsluge(this.putanjaDodatneUsluge);
	}
	public void obrisiDodadnuUslugu(int idUsluge) {
		this.sveDodatneUsluge.removeIf(v -> v.getIdDodatneUsluge() == idUsluge);
		sacuvajDodatneUsluge(this.putanjaDodatneUsluge);
	}
	public void obrisiRezervaciju(int idRezervacije) {
		this.sveRezervacije.removeIf(v -> v.getIdRezervacije() == idRezervacije);
		sacuvajRezervacije(this.putanjaRezervacije);
	}
	public void izmeniRezervaciju(Rezervacija rezervacija, ModelVozila modelVozila, LocalDate datumOd, LocalDate datumDo,
			  LocalDate datumPravljenja, Klijent klijent, StatusRezervacije status,
			  ArrayList<DodatnaUsluga> listaDodatnihUsluga, FinansijeMenadzer finMen) {
		if (modelVozila != null) { rezervacija.setModelVozila(modelVozila); }
	    if (datumOd != null) { rezervacija.setDatumOd(datumOd); }
	    if (datumDo != null) { rezervacija.setDatumDo(datumDo); }
	    if (datumPravljenja != null) { rezervacija.setDatumPravljenja(datumPravljenja); }
	    if (klijent != null) { rezervacija.setKlijent(klijent); }
	    if (status != null) { rezervacija.setStatus(status); }
	    if (listaDodatnihUsluga != null) { rezervacija.setListaDodatnihUsluga(listaDodatnihUsluga); }
	    rezervacija.setOsnovnaCena(izracunajOsnovnuCenu(rezervacija, finMen));
	    sacuvajRezervacije(this.putanjaRezervacije);
	}
	public ArrayList<Rezervacija> izvestajPotvrdjenihRezervacijaUPeriodu(LocalDate datumOd, LocalDate datumDo){
		ArrayList<Rezervacija> lista = new ArrayList<>();
		for(Rezervacija r: this.sveRezervacije) {
			if(r.getDatumPravljenja().isAfter(datumOd)&&r.getDatumPravljenja().isBefore(datumDo)&&r.getStatus().equals(StatusRezervacije.POTVRDJENO)) {
				lista.add(r);
			}
		}
		
		return lista;
	}
	public ArrayList<Rezervacija> izvestajOdbijenihRezervacijaUPeriodu(LocalDate datumOd, LocalDate datumDo){
		ArrayList<Rezervacija> lista = new ArrayList<>();
		for(Rezervacija r: this.sveRezervacije) {
			if(r.getDatumPravljenja().isAfter(datumOd)&&r.getDatumPravljenja().isBefore(datumDo)&&r.getStatus().equals(StatusRezervacije.ODBIJENO)) {
				lista.add(r);
			}
		}
		
		return lista;
	}
	public ArrayList<Rezervacija> izvestajOtkazanihRezervacijaUPeriodu(LocalDate datumOd, LocalDate datumDo){
		ArrayList<Rezervacija> lista = new ArrayList<>();
		for(Rezervacija r: this.sveRezervacije) {
			if(r.getDatumPravljenja().isAfter(datumOd)&&r.getDatumPravljenja().isBefore(datumDo)&&r.getStatus().equals(StatusRezervacije.OTKAZANO)) {
				lista.add(r);
			}
		}
		
		return lista;
	}
	public ArrayList<Rezervacija> izvestajRezervacijaOModeluVozila(int idModela, LocalDate datumOd, LocalDate datumDo){
		ArrayList<Rezervacija> lista = new ArrayList<>();
		for(Rezervacija r: this.sveRezervacije) {
			if(r.getDatumPravljenja().isAfter(datumOd)&&r.getDatumPravljenja().isBefore(datumDo)&&r.getModelVozila().getId()==idModela) {
				lista.add(r);
			}
		}
		
		return lista;
	}
	public int brojRezervacijaSStatusom(StatusRezervacije status) {
		LocalDate danas = LocalDate.now();
		LocalDate mesecPre = danas.minusMonths(1);
		int ukupno = 0;
		for(Rezervacija r: this.sveRezervacije) {
			if(!r.getDatumPravljenja().isAfter(danas)&&!r.getDatumPravljenja().isBefore(mesecPre)&&r.getStatus().equals(status)) {
				ukupno++;
			}
		}
		return ukupno;
	}

}
