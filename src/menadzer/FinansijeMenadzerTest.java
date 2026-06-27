package menadzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import enums.ZahtevPretplate;
import model.Cenovnik;
import model.Klijent;
import model.Pretplata;

class FinansijeMenadzerTest {
    
    private OsobaMenadzer osobaMenadzer;
    private VoziloMenadzer voziloMenadzer;
    private RezervacijeMenadzer rezervacijeMenadzer;
    private FinansijeMenadzer finansijeMenadzer;

    private final String KLIJENTI_TEST = "podaci/klijenti_test.csv";
    private final String AGENTI_TEST = "podaci/agenti_test.csv";
    private final String ADMINI_TEST = "podaci/admini_test.csv";
    private final String PRETPLATE_TEST = "podaci/pretplate_test.csv";
    private final String CENOVNICI_TEST = "podaci/cenovnici_test.csv";

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get("podaci"));
        
        // --- Podaci za klijente ---
        // Sinhronizovano sa sacuvajKlijente metodom (14 kolona)
        // Indeksi: 5 je korisnickoIme, 6 je email (oba stavljamo na lana@email.com)
        List<String> klijentiPodaci = List.of(
            "Lana;Lukic;ZENSKO;2002-05-14;063111222;lana@email.com;lana@email.com;lozinka123;BEZ_KATEGORIJE;2021-06-15;0;null;1;ODOBREN",
            "Lozinka;Lukic;ZENSKO;2002-05-14;063111222;loza@email.com;loza@email.com;lozinka123;BEZ_KATEGORIJE;2021-06-15;6;null;null;ODOBREN"
        );
        Files.write(Paths.get(KLIJENTI_TEST), klijentiPodaci);
        
        // Sinhronizovano sa sacuvajAgente i sacuvajAdmine (11 kolona)
        List<String> agentiPodaci = List.of("Marko;Markovic;MUSK0;1995-03-20;064111222;marko@email.com;marko_agent;lozinka123;VSS;5;60000.0");
        Files.write(Paths.get(AGENTI_TEST), agentiPodaci);
        
        List<String> adminiPodaci = List.of("Jovan;Jovanovic;MUSK0;1990-01-10;065111222;jovan@email.com;jovan_admin;lozinka123;MASTER;10;90000.0");
        Files.write(Paths.get(ADMINI_TEST), adminiPodaci);

        // --- Ostali bazični fajlovi ---
        Files.write(Paths.get("podaci/modeliVozila_test.csv"), List.of("1;ECONOMY;Polo;VW"));
        Files.write(Paths.get("podaci/vozila_test.csv"), List.of("1;1;NS-123-AA;50000;DOSTUPNO"));
        Files.write(Paths.get("podaci/dodatneUsluge_test.csv"), List.of("1;Produzeni dan"));
        Files.write(Paths.get("podaci/rezervacije_test.csv"), List.of());

        // --- FinansijeMenadzer podaci ---
        LocalDate pocetakGodine = LocalDate.now().withDayOfYear(1);
        LocalDate krajGodine = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        
        List<String> cenovniciPodaci = List.of(
            "1;" + pocetakGodine + ";" + krajGodine + ";12000.0;500.0;0.1;3;ECONOMY:3000.0,LUXURY:8000.0;1:1500.0"
        );
        Files.write(Paths.get(CENOVNICI_TEST), cenovniciPodaci);

        // Za pretplate sada imamo i datumKraj (indeks 3) i cenu (indeks 4)
        List<String> pretplatePodaci = List.of(
            "1;lana@email.com;" + LocalDate.now().minusMonths(1) + ";" + LocalDate.now().plusMonths(11) + ";12000.0"
        );
        Files.write(Paths.get(PRETPLATE_TEST), pretplatePodaci);
       
        // Instanciranje menadžera u ispravnom redosledu
        osobaMenadzer = new OsobaMenadzer(KLIJENTI_TEST, AGENTI_TEST, ADMINI_TEST);
        voziloMenadzer = new VoziloMenadzer("podaci/vozila_test.csv", "podaci/modeliVozila_test.csv");
        rezervacijeMenadzer = new RezervacijeMenadzer("podaci/rezervacije_test.csv", "podaci/dodatneUsluge_test.csv", voziloMenadzer, osobaMenadzer);
        
        // Glavni menadžer koji testiramo
        finansijeMenadzer = new FinansijeMenadzer(PRETPLATE_TEST, CENOVNICI_TEST, osobaMenadzer, rezervacijeMenadzer);
        
        // Eksplicitno povezujemo klijente sa njihovim učitanim pretplatama
        osobaMenadzer.poveziKlijenteSaPretplatama(finansijeMenadzer, KLIJENTI_TEST);
    }

    // ==========================================
    // 1. TESTOVI ZA CENOVNIKE I SELEKCIJU
    // ==========================================

    @Test
    void testOdrediTrenutniCenovnik() {
        Cenovnik aktuelni = finansijeMenadzer.OdrediTrenutniCenovnik();
        assertNotNull(aktuelni, "Sistem mora pronaći aktuelni cenovnik za današnji datum.");
        assertEquals(1, aktuelni.getIdCenovnika());
        assertEquals(12000.0, aktuelni.getCenaGodisnjePretplate());
    }

    @Test
    void testIsDostupniDaniCenovnikaPreklapanje() {
        LocalDate danas = LocalDate.now();
        boolean slobodno = finansijeMenadzer.isDostupniDaniCenovika(danas, danas.plusDays(10));
        assertFalse(slobodno, "Metoda mora vratiti false jer se prosleđeni datumi preklapaju sa postojećim cenovnikom.");
    }

    @Test
    void testIsDostupniDaniCenovnikaSlobodno() {
        LocalDate sledecaGodinaPocetak = LocalDate.now().plusYears(1).withDayOfYear(1);
        LocalDate sledecaGodinaKraj = sledecaGodinaPocetak.plusMonths(3);
        
        boolean slobodno = finansijeMenadzer.isDostupniDaniCenovika(sledecaGodinaPocetak, sledecaGodinaKraj);
        assertTrue(slobodno, "Sledeća godina bi trebala da bude slobodna za kreiranje novog cenovnika.");
    }

    // ==========================================
    // 2. TESTOVI ZA BIZNIS LOGIKU PRETPLATA
    // ==========================================

//    @Test
//    void testNapraviNovuPretplatuUspesno() {
//        Klijent klijent = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu("lana@email.com");
//        assertNotNull(klijent);
//        
//        // POPRAVKA: Pošto setUp kroz CSV već učita i poveže pretplatu Lani, 
//        // ručno je odvezujemo da bismo testirali kreiranje potpuno NOVE pretplate.
//        klijent.setPretplata(null); 
//        assertNull(klijent.getPretplata(), "Klijent sada nema ugrađenu pretplatu.");
//        
//        int preDodavanja = finansijeMenadzer.getSvePretplate().size();
//        
//        finansijeMenadzer.napraviNovuPretplatu(klijent, osobaMenadzer);
//        
//        assertEquals(preDodavanja + 1, finansijeMenadzer.getSvePretplate().size());
//        assertNotNull(klijent.getPretplata(), "Klijentu mora biti dodeljen objekat nove pretplate.");
//        assertEquals(ZahtevPretplate.NEMA, klijent.getZahtev(), "Nakon uspešne pretplate, status zahteva se vraća na NEMA.");
//    }
    @Test
    void testNapraviNovuPretplatuUspesno() {
        Klijent klijent = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu("lana@email.com");
        assertNotNull(klijent);
        
        klijent.setPretplata(null); 
        assertNull(klijent.getPretplata(), "Klijent sada nema ugrađenu pretplatu.");
        int preDodavanja = finansijeMenadzer.getSvePretplate().size();
        
        finansijeMenadzer.napraviNovuPretplatu(klijent, osobaMenadzer);
        
        assertEquals(preDodavanja + 1, finansijeMenadzer.getSvePretplate().size());
        assertNotNull(klijent.getPretplata(), "Klijentu mora biti dodeljen objekat nove pretplate.");
        assertEquals(ZahtevPretplate.NEMA, klijent.getZahtev(), "Nakon uspešne pretplate, status zahteva se vraća na NEMA.");
    }

    @Test
    void testNapraviNovuPretplatuNeuspesnoZbogKasnjenja() {
        Klijent klijentKaznjen = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu("loza@email.com");
        assertNotNull(klijentKaznjen);
        
        int preDodavanja = finansijeMenadzer.getSvePretplate().size();
        
        finansijeMenadzer.napraviNovuPretplatu(klijentKaznjen, osobaMenadzer);
        
        assertEquals(preDodavanja, finansijeMenadzer.getSvePretplate().size(), 
                "Sistem ne sme kreirati novu pretplatu klijentu koji ima više od 5 kašnjenja.");
        assertEquals(ZahtevPretplate.ODOBREN, klijentKaznjen.getZahtev(), "Zahtev ne sme biti poništen na NEMA ako je odbijen.");
    }

    @Test
    void testProduziPretplatuUspesno() {
        Pretplata p = finansijeMenadzer.PronadjiPretplatuPoId(1);
        assertNotNull(p);
        LocalDate stariKraj = p.getDatumKraj();
        
        // POPRAVKA: U metodi produziPretplatu proveravaš broj kašnjenja (<=5).
        // Lana ima 0 kašnjenja pa će uspešno proći kroz if i produžiti pretplatu.
        finansijeMenadzer.produziPretplatu(p, osobaMenadzer);
        
        assertEquals(stariKraj.plusYears(1), p.getDatumKraj(), 
                "Datum završetka pretplate se mora produžiti za tačno godinu dana.");
    }

    // ==========================================
    // 3. TESTOVI ZA GENERATOR IDENTIFIKATORA
    // ==========================================

    @Test
    void testGenerisiNoviIdCenovnika() {
        int sledeciId = finansijeMenadzer.generisiNoviIdCenovnika();
        assertEquals(2, sledeciId, "Pošto imamo cenovnik sa ID 1, sledeći generisani mora biti 2.");
    }

    @Test
    void testGenerisiNoviIdPretplate() {
        int sledeciId = finansijeMenadzer.generisiNoviIdPretplate();
        assertEquals(2, sledeciId, "Pošto imamo pretplatu sa ID 1, sledeća generisana mora biti 2.");
    }
}