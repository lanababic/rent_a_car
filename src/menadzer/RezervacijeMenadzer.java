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
import model.Rezervacija;
import model.Vozilo;

public class RezervacijeMenadzer {
	private ArrayList<Rezervacija> sveRezervacije;
	private ArrayList<DodatnaUsluga> sveDodatneUsluge;
	private final String putanjaRezervacije = "podaci/rezervacije.csv";
	private final String putanjaDodatneUsluge = "podaci/dodatneUsluge.csv";
	
	public RezervacijeMenadzer(VoziloMenadzer vozMen, OsobaMenadzer osobMen) {
		this.sveRezervacije = new ArrayList<>();
		this.sveDodatneUsluge = new ArrayList<>();
		
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
	            if(parts.length > 8 && !parts[8].trim().isEmpty()) {
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
		if(r.getStatus()==StatusRezervacije.NA_CEKANJU && imaVozila) {
			r.setStatus(StatusRezervacije.POTVRDJENO);
		}
		else {
			System.out.println("nije na cekanju");
		}
		sacuvajRezervacije(this.putanjaRezervacije);
	}
	public void otkaziIstekleRezervacije(Rezervacija rezervacija) {
		LocalDate danas = LocalDate.now();
		if(rezervacija.getDatumOd().equals(danas) && rezervacija.getStatus().equals(StatusRezervacije.NA_CEKANJU)) {
			rezervacija.setStatus(StatusRezervacije.ODBIJENO);
		}
		sacuvajRezervacije(this.putanjaRezervacije);
	}
	public void odbiRezervaciju(Rezervacija r){
		if(r.getStatus()==StatusRezervacije.NA_CEKANJU) {
			r.setStatus(StatusRezervacije.ODBIJENO);
		}
		else {
			System.out.println("nije na cekanju");
		}
		sacuvajRezervacije(this.putanjaRezervacije);
	}
	public void otkaziRezervaciju(Rezervacija r, OsobaMenadzer osobMen) {
		r.setStatus(StatusRezervacije.OTKAZANO);
		//treba dodati da pronadje korisnika po trenutno ulogovanom korisniku pa ko je otkazao i ako je klijent da onda atrbut klijenta otkazao bude true i to nekako na 24h
		sacuvajRezervacije(this.putanjaRezervacije);
	}
	public boolean isOtkazaoUPoslenjih24h(Klijent klijent) {
		LocalDate danas = LocalDate.now();
		LocalDate danas24h = LocalDate.now().minusDays(1);
		LocalDate datumOtkazivanja = klijent.getDatumOtkazivanja();
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
	private int generisiNoviIdRezervacije() {
		int max = 0;
		for(Rezervacija r : this.sveRezervacije) {
			if(r.getIdRezervacije() > max) {
				max = r.getIdRezervacije();
			}
		}
		return max + 1;
	}
	public double izracunajOsnovnuCenu(Rezervacija r, FinansijeMenadzer finMen) {
		long brojDana = ChronoUnit.DAYS.between(r.getDatumOd(), r.getDatumDo());
		Cenovnik trenutniCenovnik= finMen.OdrediTrenutniCenovnik();
		KategorijaVozila kategorija = r.getModelVozila().getKategorijaVozila();
		double cenaPoDanu = trenutniCenovnik.getCenaNajmaKonkretno(kategorija);
		double cenaDana = cenaPoDanu*brojDana;
		double cenaDodatnih = 0;
		ArrayList<DodatnaUsluga> listaDodatnih = r.getListaDodatnihUsluga();
		for(DodatnaUsluga u: listaDodatnih) {
			cenaDodatnih = cenaDodatnih+trenutniCenovnik.getCenaDodatneUslugeKonkretno(u);
		}
		double osnovnaCena = cenaDana+cenaDodatnih;
		return osnovnaCena;
	}
	public void napraviRezervaciju( ModelVozila modelVozila, LocalDate datumOd, Klijent klijent, FinansijeMenadzer finMen) {
		Cenovnik trenutniCenovnik= finMen.OdrediTrenutniCenovnik();
		int idRezervacije = generisiNoviIdRezervacije();
		LocalDate datumPravljenja = LocalDate.now();
		if(klijent.getDatumVozacke().isBefore(LocalDate.now().minusYears(2)) || !isOtkazaoUPoslenjih24h(klijent) ) {
			ArrayList<DodatnaUsluga> listaDodatnih = new ArrayList<>();
			//treba da ponudi dodatne usluge i doda u listu one koje zeli i doda ih koliko ih hoce npr 3 produzena dana
			LocalDate datumDo = racunanjeDatumaDo(datumOd, listaDodatnih, finMen);
			Rezervacija rezervacija = new Rezervacija(idRezervacije, modelVozila, datumOd, datumDo, 0,datumPravljenja, klijent);
			rezervacija.setListaDodatnihUsluga(listaDodatnih);
			double osnovnaCena = izracunajOsnovnuCenu(rezervacija, finMen);
			rezervacija.setOsnovnaCena(osnovnaCena);
			if(!klijent.getKategorija().equals(KategorijaKlijenta.BEZ_KATEGORIJE)) {
				double osnCena = rezervacija.getOsnovnaCena();
				osnCena = osnCena*trenutniCenovnik.getPopustZaKategorije();
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
			if(r.getDatumPravljenja().equals(datumDana));
			lista.add(r);
		}
		return lista;
	}
	

}
