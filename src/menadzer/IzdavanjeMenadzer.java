package menadzer;

import model.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
	public String getPutanjaIzdavanja() {
		return putanjaIzdavanja;
	}


	public void sacuvajIzdavanja(String putanjaIzdavanja) {
	    ArrayList<String> lines = new ArrayList<String>();
	    for (IzdavanjeVozila iv : this.svaIzdavanja) {
	        
	        String datumVracanja = (iv.getStvarniDatumVracanja() != null) ? iv.getStvarniDatumVracanja().toString() : "null";
	        String korisnickoImePrimio = (iv.getAgentPrimio() != null) ? iv.getAgentPrimio().getKorisnickoIme() : "null";
	        
	        String line = iv.getIdIzdaje() + ";" + 
	                      iv.getRezervacija().getIdRezervacije() + ";" + 
	                      iv.getVozilo().getIdVozila() + ";" + 
	                      iv.getAgentIzdao().getKorisnickoIme() + ";" +  
	                      korisnickoImePrimio + ";" + 
	                      iv.getKilometrazaPriPreuzimanju() + ";" + 
	                      iv.getKilometrazaPriVracanju() + ";" + 
	                      datumVracanja + ";" + 
	                      iv.getNaplacenaKazna()+ ";" + 
	                      iv.getUkupnaCena()+ ";" + 
	                      iv.getDatumPravljenjaIzdaje();
	                      
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
	            
	            Agent primio = null;
	            if (!parts[4].equals("null")) {
	                primio = korMen.pronadjiAgentaPoKorisnickomImenu(parts[4]);
	            }
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
	            iv.setUkupnaCena(Double.parseDouble(parts[9]));
	            iv.setDatumPravljenjaIzdaje(LocalDate.parse(parts[10]));
	            
	            this.svaIzdavanja.add(iv);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	private int generisiNoviIdIznajmljivanjeVozila() {
		int max = 0;
		for(IzdavanjeVozila iv : this.svaIzdavanja) {
			if(iv.getIdIzdaje() > max) {
				max = iv.getIdIzdaje();
			}
		}
		return max + 1;
	}
	private boolean isVoziloSlobodnoUPeriodu(Vozilo v, LocalDate trazeniOd, LocalDate trazeniDo) {
	    // Prolazimo kroz sva izdanja/potvrđene rezervacije u sistemu
	    for (IzdavanjeVozila izdavanje : svaIzdavanja) {
	        
	        // Zanimaju nas samo zapisi koji se odnose na ovo konkretno fizičko vozilo
	        if (izdavanje.getVozilo() != null && izdavanje.getVozilo().getIdVozila() == v.getIdVozila()) {
	            
	            // Moramo proveriti status povezane rezervacije
	            // Ako je rezervacija OTKAZANA ili ODBIJENA, taj termin je ponovo SLOBODAN!
	            StatusRezervacije statusRez = izdavanje.getRezervacija().getStatus();
	            if (statusRez == StatusRezervacije.POTVRDJENO || statusRez == StatusRezervacije.NA_CEKANJU) {
	                
	                // Uzimamo datume iz rezervacije koja je vezana za ovo izdavanje
	                LocalDate zauzetoOd = izdavanje.getRezervacija().getDatumOd();
	                LocalDate zauzetoDo = izdavanje.getRezervacija().getDatumDo();
	                
	                // Provera preklapanja: ako se periodi preklapaju, vozilo je ZAUZETO
	                if (!(trazeniOd.isAfter(zauzetoDo) || trazeniDo.isBefore(zauzetoOd))) {
	                    return false; // Pronađeno je preklapanje, vozilo NIJE slobodno
	                }
	            }
	        }
	    }
	    return true; // Nema nikakvih preklapanja u aktivnim terminima, vozilo je slobodno!
	}
	public boolean isModelDostupan(ModelVozila model, LocalDate trazeniOd, LocalDate trazeniDo, VoziloMenadzer vozMen) {
	    // 1. Izvučeš sva fizička vozila iz sistema koja pripadaju ovom modelu
	    ArrayList<Vozilo> svaVozilaOvogModela = vozMen.pronadjiVozilaPoModelu(model);	
	    
	    // 2. Proveriš svako vozilo pojedinačno preko metode iznad
	    for (Vozilo v : svaVozilaOvogModela) {
	        if (isVoziloSlobodnoUPeriodu(v, trazeniOd, trazeniDo)) {
	            return true; // Našli smo bar JEDAN primerak koji nema preklapanje! Klijent može da rezerviše.
	        }
	    }
	    return false; // Svi fizički primerci ovog modela su zauzeti u tom periodu.
	}
	public Vozilo OdaberiVozilo(ArrayList<Vozilo> listaMogucihVozila, Rezervacija rezervacija) {
		//treba nekako da predlozi sva moguca vozila pa da se unese id vozila i da to bude vozilo
		listaMogucihVozila.removeIf(v -> !isVoziloSlobodnoUPeriodu(v, rezervacija.getDatumOd(), rezervacija.getDatumDo()));
		if (listaMogucihVozila.isEmpty()) {
	        return null; // Nema slobodnih vozila
	    }
		int idVozila=0;//kasnije s gui ce biti neki pravi id;
		Vozilo odabrano = listaMogucihVozila.get(idVozila);
		return odabrano;
	}
//	public void IznajmiVoziloSamoPotvrda(Rezervacija rezervacija, RezervacijeMenadzer rezMen, VoziloMenadzer vozMen) {
//		int idIzdaje = generisiNoviIdIznajmljivanjeVozila();
//		rezMen.potvrdiRezervaciju(rezervacija, vozMen, this);
//		ArrayList<Vozilo> listaMogucihVozila = vozMen.pronadjiVozilaSlobodnaPrekoModela(rezervacija.getModelVozila());
//		Vozilo vozilo = OdaberiVozilo(listaMogucihVozila);
//		IzdavanjeVozila idavanjeVozila = new IzdavanjeVozila(idIzdaje,rezervacija,vozilo, null, null,0,0, null);
//		sacuvajIzdavanja(this.putanjaIzdavanja);
//	}
	public void predloziJosDodatnihUsluga(Rezervacija rezervacija, RezervacijeMenadzer rezMen, FinansijeMenadzer finMen){
		ArrayList<DodatnaUsluga> listaDodatnih = new ArrayList<>();
		rezMen.predloziDodatneUsluge(listaDodatnih);
		for(DodatnaUsluga u: listaDodatnih) {
			rezervacija.dodajDodatnuUslugu(u);
			if(u.getNaziv().equals("Produzeni dan")) {
				LocalDate noviDatumDo = rezervacija.getDatumDo().plusDays(1);
				rezervacija.setDatumDo(noviDatumDo);
			}
		}
		double osnovnaCena = rezMen.izracunajOsnovnuCenu(rezervacija, finMen);
		rezervacija.setOsnovnaCena(osnovnaCena);
	}
	public void IzdavanjeVozilaPrviDeo(Rezervacija rezervacija, VoziloMenadzer vozMen, OsobaMenadzer osobMen, RezervacijeMenadzer rezMen, FinansijeMenadzer finMen) {
		ArrayList<Vozilo> listaMogucihVozila = new ArrayList<>();
		listaMogucihVozila = vozMen.pronadjiVozilaPoModelu(rezervacija.getModelVozila());//zapravo ne trena pronadjiVozilaSlobodnaPrekoModela jer ce mozda vozilo koje nije trnutno DOSTUPSNO biti posle
		Vozilo odabranoVozilo = OdaberiVozilo(listaMogucihVozila, rezervacija);
		Osoba trenutnoUlogovan = osobMen.getTrenutnoUlogovan();
		Agent trenutniAgent = osobMen.pronadjiAgentaPoKorisnickomImenu(trenutnoUlogovan.getKorisnickoIme());
		if(trenutniAgent == null) {
			//prijavi gresku
		}
		int kilometrazaPriPreuzimanju = odabranoVozilo.getTrenutnaKilometraza();
		int idIzdaje = generisiNoviIdIznajmljivanjeVozila();
		predloziJosDodatnihUsluga(rezervacija, rezMen,  finMen);
		IzdavanjeVozila izdaja = new IzdavanjeVozila(idIzdaje,rezervacija, odabranoVozilo, trenutniAgent,null,kilometrazaPriPreuzimanju, 0, null);		
		odabranoVozilo.setStatus(StatusVozila.IZDATO);
		vozMen.sacuvajVozila(vozMen.getPutanjaVozila());
		rezMen.sacuvajRezervacije(rezMen.getPutanjaRezervacije());
		this.svaIzdavanja.add(izdaja);
		sacuvajIzdavanja(this.putanjaIzdavanja);
	}
	public double izracunajCenuKazne(IzdavanjeVozila izd, FinansijeMenadzer finMen) {
		Rezervacija r = izd.getRezervacija();
		long brojDana = ChronoUnit.DAYS.between(r.getDatumDo(), izd.getStvarniDatumVracanja());
		Cenovnik trenutniCenovnik= finMen.OdrediTrenutniCenovnik();
		double cenaKazne = 0;
		if(brojDana>=1) {
			cenaKazne =trenutniCenovnik.getKaznaZaKasnjenje()*brojDana;
		}
		return cenaKazne;
	}
	public void IzdavanjeVozilaVracanje(IzdavanjeVozila izdaja, int novaKilometraza, VoziloMenadzer vozMen, OsobaMenadzer osobMen, FinansijeMenadzer finMen) {
		Osoba trenutnoUlogovan = osobMen.getTrenutnoUlogovan();
		Agent trenutniAgent = osobMen.pronadjiAgentaPoKorisnickomImenu(trenutnoUlogovan.getKorisnickoIme());
		if(trenutniAgent == null) {
			//prijavi gresku
		}
		izdaja.setAgentPrimio(trenutniAgent);
		Vozilo izdatoVozilo = izdaja.getVozilo();
		izdatoVozilo.setTrenutnaKilometraza(novaKilometraza);
		izdaja.setKilometrazaPriVracanju(novaKilometraza);
		LocalDate danas = LocalDate.now();
		izdaja.setStvarniDatumVracanja(danas);
		Rezervacija rezervacija = izdaja.getRezervacija();
		double cenaKazne = 0;
		Cenovnik trenutniCenovnik = finMen.OdrediTrenutniCenovnik();
		if(danas.isAfter(rezervacija.getDatumDo())) {
			cenaKazne=izracunajCenuKazne(izdaja, finMen);
		}
		double ukupnaCena= cenaKazne+izdaja.getUkupnaCena();
		izdaja.setUkupnaCena(ukupnaCena);
		izdaja.setNaplacenaKazna(cenaKazne);
		izdatoVozilo.setStatus(StatusVozila.DOSTUPNO);
		vozMen.sacuvajVozila(vozMen.getPutanjaVozila());
		sacuvajIzdavanja(this.putanjaIzdavanja);
		
	}
	public ArrayList<IzdavanjeVozila> ucitajSvaIzdavanjaOdDana(LocalDate datumDana){
		ArrayList<IzdavanjeVozila> lista = new ArrayList<>();
		for(IzdavanjeVozila iz: this.svaIzdavanja) {
			if(iz.getDatumPravljenjaIzdaje().equals(datumDana)) {
				lista.add(iz);
			}
		}
		return lista;
	}
	public ArrayList<IzdavanjeVozila> odrediPrihodeZbogIzdajaUPeriodu(LocalDate datumOd, LocalDate datumDo){
		ArrayList<IzdavanjeVozila> lista = new ArrayList<>();
		for(IzdavanjeVozila iz: this.svaIzdavanja) {
			if(iz.getRezervacija().getDatumOd().isAfter(datumOd)&& iz.getRezervacija().getDatumDo().isBefore(datumDo)) {
				lista.add(iz);
			}
		}
		return lista;
	}
	public void vratiNaDostupnaZbogRezervacijeBezPojave() {
		for(IzdavanjeVozila iz: this.svaIzdavanja) {
			if(iz.getRezervacija().getStatus().equals(StatusRezervacije.OTKAZANO)) {
				iz.getVozilo().setStatus(StatusVozila.DOSTUPNO);
			}
		}
		sacuvajIzdavanja(this.putanjaIzdavanja);
	}
	public void obrisiIzdaju(int idIzdaje) {
		this.svaIzdavanja.removeIf(iz -> iz.getIdIzdaje() == idIzdaje);
		sacuvajIzdavanja(this.putanjaIzdavanja);
	}
}
