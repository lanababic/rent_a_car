package menadzer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import enums.KategorijaKlijenta;
import enums.StatusRezervacije;
import enums.StatusVozila;
import model.Agent;
import model.IzdavanjeVozila;
import model.ModelVozila;
import model.Rezervacija;
import model.Vozilo;

class IzdavanjeMenadzerTest {

    private OsobaMenadzer osobaMenadzer;
    private VoziloMenadzer voziloMenadzer;
    private RezervacijeMenadzer rezervacijeMenadzer;
    private FinansijeMenadzer finansijeMenadzer;
    private IzdavanjeMenadzer izdavanjeMenadzer;

    private final String KLIJENTI_TEST = "podaci/klijenti_test.csv";
    private final String AGENTI_TEST = "podaci/agenti_test.csv";
    private final String ADMINI_TEST = "podaci/admini_test.csv";
    private final String VOZILA_TEST = "podaci/vozila_test.csv";
    private final String MODELI_TEST = "podaci/modeliVozila_test.csv";
    private final String REZERVACIJE_TEST = "podaci/rezervacije_test.csv";
    private final String DODATNE_TEST = "podaci/dodatneUsluge_test.csv";
    private final String CENOVNICI_TEST = "podaci/cenovnici_test.csv";
    private final String PRETPLATE_TEST = "podaci/pretplate_test.csv";
    private final String IZDAVANJA_TEST = "podaci/izdavanja_test.csv";

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get("podaci"));

        // 1. Osobe (Klijenti, Agenti)
        List<String> klijentiPodaci = List.of(
        	    "Lana;Lukic;ZENSKO;2002-05-14;063111222;lana@email.com;lana@email.com;lozinka123;STUDENT;2021-06-15;0;null;1;ODOBREN"
        	);
        Files.write(Paths.get(KLIJENTI_TEST), klijentiPodaci);

        List<String> agentiPodaci = List.of(
            "Marko;Markovic;MUSK0;1995-03-20;064111222;marko@email.com;marko_agent;lozinka123;VSS;5;60000.0",
            "Nikola;Nikolic;MUSK0;1997-04-12;064333444;nikola@email.com;nikola_agent;lozinka123;VSS;2;55000.0"
        );
        Files.write(Paths.get(AGENTI_TEST), agentiPodaci);
        Files.write(Paths.get(ADMINI_TEST), List.of());

        // 2. Vozila i Modeli
        Files.write(Paths.get(MODELI_TEST), List.of("1;ECONOMY;Polo;VW"));
        // idVozila; idModela; tablice; kilometraza; status
        Files.write(Paths.get(VOZILA_TEST), List.of(
            "1;1;NS-123-AA;50000;DOSTUPNO",
            "2;1;NS-456-BB;60000;DOSTUPNO"
        ));

        // 3. Rezervacije i Usluge
        Files.write(Paths.get(DODATNE_TEST), List.of("1;Produzeni dan"));
        
        LocalDate danas = LocalDate.now();
        // id; klijentEmail; idModela; datumOd; datumDo; osnovnaCena; status; dodatneUslugeIds
        List<String> rezervacijePodaci = List.of(
        	    "1;1;" + danas.minusDays(5) + ";" + danas.minusDays(2) + ";POTVRDJENO;9000.0;" + danas.minusDays(5) + ";lana@email.com;1",
        	    "2;1;" + danas.plusDays(2) + ";" + danas.plusDays(5) + ";POTVRDJENO;9000.0;" + danas.now() + ";lana@email.com;"
        	);
        	Files.write(Paths.get(REZERVACIJE_TEST), rezervacijePodaci);
        // 4. Finansije (potrebne za kazne i testiranje metoda koje traže cenovnik)
        LocalDate pocetakGodine = danas.withDayOfYear(1);
        LocalDate krajGodine = danas.withDayOfYear(danas.lengthOfYear());
        Files.write(Paths.get(CENOVNICI_TEST), List.of("1;" + pocetakGodine + ";" + krajGodine + ";12000.0;1000.0;0.1;3;ECONOMY:3000.0;1:1500.0"));
        Files.write(Paths.get(PRETPLATE_TEST), List.of());

        // 5. Izdavanja (Inicijalni podaci za test učitavanja)
        // idIzdaje; idRezervacije; idVozila; agentIzdao; agentPrimio; kmPreuzimanje; kmVracanje; stvarniDatumVracanja; kazna; ukupnaCena; datumPravljenja
        List<String> izdavanjaPodaci = List.of(
            "1;1;1;marko_agent;nikola_agent;50000;50500;" + danas.minusDays(2) + ";0.0;9000.0;" + danas.minusDays(5)
        );
        Files.write(Paths.get(IZDAVANJA_TEST), izdavanjaPodaci);

        // Instanciranje svih menadžera u lančanom redosledu
        osobaMenadzer = new OsobaMenadzer(KLIJENTI_TEST, AGENTI_TEST, ADMINI_TEST);
        voziloMenadzer = new VoziloMenadzer(VOZILA_TEST, MODELI_TEST);
        rezervacijeMenadzer = new RezervacijeMenadzer(REZERVACIJE_TEST, DODATNE_TEST, voziloMenadzer, osobaMenadzer);
        finansijeMenadzer = new FinansijeMenadzer(PRETPLATE_TEST, CENOVNICI_TEST, osobaMenadzer, rezervacijeMenadzer);
        
        // Glavni menadžer pod testom
        izdavanjeMenadzer = new IzdavanjeMenadzer(IZDAVANJA_TEST, rezervacijeMenadzer, voziloMenadzer, osobaMenadzer);
    }

    // ==========================================
    // 1. TESTOVI ZA UCITAVANJE I PRETRAGU
    // ==========================================

    @Test
    void testUcitajIzdavanjaUspesno() {
        assertFalse(izdavanjeMenadzer.getSvaIzdavanja().isEmpty());
        IzdavanjeVozila iv = izdavanjeMenadzer.pronadjiIzdavanjePoId(1);
        assertNotNull(iv);
        assertEquals("marko_agent", iv.getAgentIzdao().getKorisnickoIme());
        assertEquals("nikola_agent", iv.getAgentPrimio().getKorisnickoIme());
        assertEquals(50000, iv.getKilometrazaPriPreuzimanju());
    }

    @Test
    void testPronadjiIzdavanjePoIdNepostojeci() {
        IzdavanjeVozila iv = izdavanjeMenadzer.pronadjiIzdavanjePoId(999);
        assertNull(iv);
    }

    // ==========================================
    // 2. TESTOVI ZA PROVERU DOSTUPNOSTI VOZILA
    // ==========================================

    @Test
    void testIsVoziloSlobodnoUPerioduZauzeto() {
        Vozilo v1 = voziloMenadzer.pronadjiVoziloPoId(1);
        // Rezervacija 1 pokriva period od danas-5 do danas-2, tako da u tom opsegu vozilo 1 NIJE slobodno
        LocalDate odDat = LocalDate.now().minusDays(4);
        LocalDate doDat = LocalDate.now().minusDays(3);
        
        boolean slobodno = izdavanjeMenadzer.isVoziloSlobodnoUPeriodu(v1, odDat, doDat);
        assertFalse(slobodno, "Vozilo ne bi trebalo da bude slobodno jer se preklapa sa Rezervacijom 1.");
    }

    @Test
    void testIsVoziloSlobodnoUPerioduSlobodno() {
        Vozilo v1 = voziloMenadzer.pronadjiVoziloPoId(1);
        // Period u dalekoj budućnosti je potpuno slobodan
        LocalDate odDat = LocalDate.now().plusYears(1);
        LocalDate doDat = odDat.plusDays(5);
        
        boolean slobodno = izdavanjeMenadzer.isVoziloSlobodnoUPeriodu(v1, odDat, doDat);
        assertTrue(slobodno);
    }

    @Test
    void testIsModelDostupan() {
        ModelVozila model = voziloMenadzer.pronadjiVoziloPoId(1).getModelVozila();
        // Vozilo 2 pripada istom modelu (ID: 1) ali nema nijednu rezervaciju/izdavanje vezano za sebe, pa model mora biti dostupan
        LocalDate odDat = LocalDate.now().minusDays(4);
        LocalDate doDat = LocalDate.now().minusDays(3);

        boolean dostupan = izdavanjeMenadzer.isModelDostupan(model, odDat, doDat, voziloMenadzer);
        assertTrue(dostupan, "Model mora biti dostupan jer je Vozilo 2 iz istog modela potpuno slobodno.");
    }

    // ==========================================
    // 3. TESTOVI ZA PROCES IZDAVANJA I VRACANJA
    // ==========================================

    @Test
    void testIzdavanjeVozilaPrviDeo() {
        Rezervacija rez2 = rezervacijeMenadzer.pronadjiRezervacijuPoId(2);
        Vozilo v2 = voziloMenadzer.pronadjiVoziloPoId(2);
        
        // Postavljamo trenutno ulogovanog agenta kako bi ga metoda unutar menadžera uspešno pokupila
        Agent agent = osobaMenadzer.pronadjiAgentaPoKorisnickomImenu("marko_agent");
        osobaMenadzer.setTrenutnoUlogovan(agent);

        int brojIzdavanjaPre = izdavanjeMenadzer.getSvaIzdavanja().size();

        izdavanjeMenadzer.IzdavanjeVozilaPrviDeo(rez2, voziloMenadzer, osobaMenadzer, rezervacijeMenadzer, finansijeMenadzer, v2, new ArrayList<>());

        assertEquals(brojIzdavanjaPre + 1, izdavanjeMenadzer.getSvaIzdavanja().size());
        assertEquals(StatusVozila.IZDATO, v2.getStatus(), "Status vozila mora preći u IZDATO.");
    }

    @Test
    void testIzracunajCenuKazneSaKasnjenjem() {
        IzdavanjeVozila iv = izdavanjeMenadzer.pronadjiIzdavanjePoId(1);
        // Rezervacija se završava pre 2 dana (danas-2), a stvarni datum vraćanja postavljamo na danas (2 dana kašnjenja)
        iv.setStvarniDatumVracanja(LocalDate.now());

        // Kazna u cenovniku je postavljena na 1000.0 po danu. 2 dana * 1000.0 = 2000.0
        double kazna = izdavanjeMenadzer.izracunajCenuKazne(iv, finansijeMenadzer);
        assertEquals(2000.0, kazna);
    }

    @Test
    void testIzdavanjeVozilaVracanjeSaKaznom() {
        // Kreiramo aktivno izdavanje za potrebe testa vraćanja
        Rezervacija rez2 = rezervacijeMenadzer.pronadjiRezervacijuPoId(2);
        // Postavimo da je rok za vraćanje bio juče kako bismo izazvali kaznu od 1 dan
        rez2.setDatumDo(LocalDate.now().minusDays(1));
        
        Vozilo v2 = voziloMenadzer.pronadjiVoziloPoId(2);
        v2.setStatus(StatusVozila.IZDATO);
        v2.setTrenutnaKilometraza(60000);

        Agent agent = osobaMenadzer.pronadjiAgentaPoKorisnickomImenu("nikola_agent");
        osobaMenadzer.setTrenutnoUlogovan(agent);

        IzdavanjeVozila novoIzdavanje = new IzdavanjeVozila(2, rez2, v2, agent, null, 60000, 0, null);
        novoIzdavanje.setUkupnaCena(9000.0); // Osnovna cena
        izdavanjeMenadzer.getSvaIzdavanja().add(novoIzdavanje);

        // Vraćamo vozilo danas sa novom kilometražom od 60500
        izdavanjeMenadzer.IzdavanjeVozilaVracanje(novoIzdavanje, 60500, voziloMenadzer, osobaMenadzer, finansijeMenadzer);

        assertEquals(60500, v2.getTrenutnaKilometraza());
        assertEquals(StatusVozila.DOSTUPNO, v2.getStatus(), "Nakon vraćanja, vozilo ponovo postaje DOSTUPNO.");
        assertEquals(LocalDate.now(), novoIzdavanje.getStvarniDatumVracanja());
        assertEquals("nikola_agent", novoIzdavanje.getAgentPrimio().getKorisnickoIme());
        
        // 1 dan kašnjenja * 1000.0 kazna = 1000.0. Ukupno: 9000 + 1000 = 10000.0
        assertEquals(1000.0, novoIzdavanje.getNaplacenaKazna());
        assertEquals(10000.0, novoIzdavanje.getUkupnaCena());
    }

    // ==========================================
    // 4. TESTOVI ZA FINANSIJSKE IZVEŠTAJE I STATISTIKE
    // ==========================================

    @Test
    void testUkupanPrihodUPeriodu() {
        LocalDate odDat = LocalDate.now().minusDays(10);
        LocalDate doDat = LocalDate.now().plusDays(10);
        
        // Jedino izdavanje u sistemu ima ukupnu cenu 9000.0 i rezervaciju unutar ovog opsega
        double prihod = izdavanjeMenadzer.ukupanPrihodUPeriodu(odDat, doDat);
        assertEquals(9000.0, prihod);
    }

    @Test
    void testUkupanPrihodOdKategorije() {
        LocalDate odDat = LocalDate.now().minusDays(10);
        LocalDate doDat = LocalDate.now().plusDays(10);
        
        ArrayList<IzdavanjeVozila> lista = izdavanjeMenadzer.odrediPrihodeUPerioduZaKategoriju(odDat, doDat, KategorijaKlijenta.STUDENT);
        double prihod = izdavanjeMenadzer.ukupanPrihodOdKategorije(lista);
        
        assertEquals(1, lista.size());
        assertEquals(9000.0, prihod);
    }

    @Test
    void testBrojObradjenihRezervacijaOdAgenta() {
        Agent agent = osobaMenadzer.pronadjiAgentaPoKorisnickomImenu("marko_agent");
        // Izdavanje 1 ima datum pravljenja danas-5, što upada u poslednjih mesec dana
        int obradjene = izdavanjeMenadzer.brojObradjenRezervacijaOdAgenta(agent);
        assertEquals(1, obradjene);
    }

    @Test
    void testImaliIzdajuTaRezervacija() {
        Rezervacija rez1 = rezervacijeMenadzer.pronadjiRezervacijuPoId(1);
        Rezervacija rez2 = rezervacijeMenadzer.pronadjiRezervacijuPoId(2);

        assertTrue(izdavanjeMenadzer.imaliIzdajuTaRezervacija(rez1));
        assertFalse(izdavanjeMenadzer.imaliIzdajuTaRezervacija(rez2));
    }

    // ==========================================
    // 5. TESTOVI ZA MODIFIKACIJU I BRISANJE
    // ==========================================

    @Test
    void testObrisiIzdaju() {
        assertNotNull(izdavanjeMenadzer.pronadjiIzdavanjePoId(1));
        izdavanjeMenadzer.obrisiIzdaju(1);
        assertNull(izdavanjeMenadzer.pronadjiIzdavanjePoId(1));
    }

    @Test
    void testVratiNaDostupnaZbogRezervacijeBezPojave() {
        IzdavanjeVozila iv = izdavanjeMenadzer.pronadjiIzdavanjePoId(1);
        Vozilo v = iv.getVozilo();
        v.setStatus(StatusVozila.IZDATO);
        
        // Simuliramo situaciju gde klijent nije došao i rezervacija se otkazuje
        iv.getRezervacija().setStatus(StatusRezervacije.OTKAZANO);
        
        izdavanjeMenadzer.vratiNaDostupnaZbogRezervacijeBezPojave();
        assertEquals(StatusVozila.DOSTUPNO, v.getStatus(), "Vozilo mora biti ponovo dostupno ako je rezervacija otkazana.");
    }
}