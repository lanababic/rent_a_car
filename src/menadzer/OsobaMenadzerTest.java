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

import enums.Pol;
import enums.StrucnaSprema;
import model.Admin;
import model.Agent;
import model.Klijent;
import model.Osoba;

class OsobaMenadzerTest {

    private OsobaMenadzer osobaMenadzer;
    private VoziloMenadzer voziloMenadzer;
    private RezervacijeMenadzer rezervacijeMenadzer;
    private FinansijeMenadzer finansijeMenadzer;

    // Definišemo testne putanje
    private final String KLIJENTI_TEST = "podaci/klijenti_test.csv";
    private final String AGENTI_TEST = "podaci/agenti_test.csv";
    private final String ADMINI_TEST = "podaci/admini_test.csv";

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get("podaci"));
        
        // Klijent Lana: Pol.ZENSKO, KategorijaKlijenta.BEZ_KATEGORIJE, ZahtevPretplate.NEMA
        List<String> klijentiPodaci = List.of("Lana;Lukic;ZENSKO;2002-05-14;063111222;lana@email.com;lana@email.com;lozinka123;BEZ_KATEGORIJE;2021-06-15;0;null;null;NEMA");
        Files.write(Paths.get(KLIJENTI_TEST), klijentiPodaci);
        
        // Agent Marko
        List<String> agentiPodaci = List.of("Marko;Markovic;MUSK0;1995-03-20;064111222;marko@email.com;marko_agent;lozinka123;VSS;5;0.0");
        Files.write(Paths.get(AGENTI_TEST), agentiPodaci);
        
        // Admin Jovan
        List<String> adminiPodaci = List.of("Jovan;Jovanovic;MUSK0;1990-01-10;065111222;jovan@email.com;jovan_admin;lozinka123;MASTER;10;0.0");
        Files.write(Paths.get(ADMINI_TEST), adminiPodaci);

        // Inicijalizacija menadžera sa ispravljenim fajlovima
        osobaMenadzer = new OsobaMenadzer(KLIJENTI_TEST, AGENTI_TEST, ADMINI_TEST);
        voziloMenadzer = new VoziloMenadzer("podaci/vozila_test.csv", "podaci/modeliVozila_test.csv");
        rezervacijeMenadzer = new RezervacijeMenadzer("podaci/rezervacije_test.csv", "podaci/dodatneUsluge_test.csv", voziloMenadzer, osobaMenadzer);
        finansijeMenadzer = new FinansijeMenadzer("podaci/pretplate_test.csv", "podaci/cenovnici_test.csv", osobaMenadzer, rezervacijeMenadzer);
        
        osobaMenadzer.poveziKlijenteSaPretplatama(finansijeMenadzer, KLIJENTI_TEST);
    }
 // ==========================================
    // 1. TESTOVI ZA LOGIN I AUTENTIFIKACIJU
    // ==========================================

    @Test
    void testLogInUspesanKlijentPrekoEmaila() {
        Osoba ulogovan = osobaMenadzer.logIn("lana@email.com", "lozinka123");
        assertNotNull(ulogovan, "Klijent bi trebao uspešno da se uloguje preko email-a.");
        assertTrue(ulogovan instanceof Klijent);
        assertEquals(osobaMenadzer.getTrenutnoUlogovan(), ulogovan);
    }

    @Test
    void testLogInUspesanAgentPrekoKorisnickogImena() {
        Osoba ulogovan = osobaMenadzer.logIn("marko_agent", "lozinka123");
        assertNotNull(ulogovan, "Agent bi trebao uspešno da se uloguje preko korisničkog imena.");
        assertTrue(ulogovan instanceof Agent);
    }

    @Test
    void testLogInNeuspesanPogresnaLozinka() {
        Osoba ulogovan = osobaMenadzer.logIn("lana@email.com", "pogresna_lozinka");
        assertNull(ulogovan, "Logovanje ne bi trebalo da uspe sa pogrešnom lozinkom.");
    }

    @Test
    void testOdjava() {
        osobaMenadzer.logIn("lana@email.com", "lozinka123");
        assertNotNull(osobaMenadzer.getTrenutnoUlogovan());
        
        osobaMenadzer.odjava();
        assertNull(osobaMenadzer.getTrenutnoUlogovan(), "Nakon odjave, trenutno ulogovani korisnik mora biti null.");
    }

    // ==========================================
    // 2. TESTOVI ZA FINANSIJE I OBRAČUN PLATA
    // ==========================================

    @Test
    void testIzracunajPlatu() {
        // Uzimamo agenta Marka (Sprema VISOKA ima svoj koeficijent, npr. uzmimo primer da radi)
        Agent agent = osobaMenadzer.pronadjiAgentaPoKorisnickomImenu("marko_agent");
        assertNotNull(agent);
        
        // Provera formule: plata = osnovac * (koeficijent + 0.004 * staz)
        double koeficijent = agent.getSprema().getKoefijent();
        double ocekivanaPlata = agent.getOsnovac() * (koeficijent + 0.004 * agent.getStaz());
        
        double izracunataPlata = osobaMenadzer.izracunajPlatu(agent);
        assertEquals(ocekivanaPlata, izracunataPlata, 0.01, "Formula za obračun plate ne vraća tačnu vrednost.");
    }

    @Test
    void testUkupanRashodUPerioduZaDvaMeseca() {
        // Postavićemo period od tačno 60 dana (što je 2 meseca prema formuli: brojDana / 30)
        LocalDate danas = LocalDate.now();
        LocalDate zaSestdesetDana = danas.plusDays(60);
        
        // Ručno izračunajmo očekivane plate za 2 meseca za sve zaposlene u testu
        double ukupnePlateZaMesec = 0;
        for (Agent a : osobaMenadzer.getSviAgenti()) {
            ukupnePlateZaMesec += a.getOsnovnaPlata();
        }
        for (Admin ad : osobaMenadzer.getSviAdmini()) {
            ukupnePlateZaMesec += ad.getOsnovnaPlata();
        }
        
        double ocekivaniRashod = ukupnePlateZaMesec * 2;
        double stvarniRashod = osobaMenadzer.ukupanRaskohUPeriodu(danas, zaSestdesetDana);
        
        assertEquals(ocekivaniRashod, stvarniRashod, 0.01, "Ukupan rashod za period od 2 meseca nije ispravan.");
    }

    // ==========================================
    // 3. TESTOVI ZA POSLOVNU LOGIKU ZAHTEVA
    // ==========================================

    @Test
    void testPodnesiZahtevAutomatskoOdbijanjeAkoKasni() {
        // Simuliramo da se loguje klijent koji ima više od 5 kašnjenja
        Klijent klijent = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu("lana@email.com"); // pošto je korisničko ime isto što i email
        assertNotNull(klijent);
        klijent.setBrojKasnjenja(6); // Više od zakonskih 5
        
        // Logujemo je da bi trenutnoUlogovan bio postavljen
        osobaMenadzer.logIn("lana@email.com", "lozinka123");
        
        osobaMenadzer.podnesiZahtev();
        
        assertEquals(enums.ZahtevPretplate.ODBIJEN, klijent.getZahtev(), "Zahtev bi trebao biti automatski ODBIJEN ako klijent ima > 5 kašnjenja.");
    }

//    @Test
//    void testPodnesiZahtevUspesnoSlanje() {
//        Klijent klijent = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu("lana@email.com");
//        klijent.setBrojKasnjenja(2); // Manje od 5
//        
//        osobaMenadzer.logIn("lana@email.com", "lozinka123");
//        osobaMenadzer.podnesiZahtev();
//        
//        assertEquals(enums.ZahtevPretplate.POSLAT, klijent.getZahtev(), "Zahtev bi trebao biti u statusu POSLAT jer klijent ne kasni previše.");
//    }
    @Test
    void testPodnesiZahtevUspesnoSlanje() {
        // 1. Logujemo klijenta direktno
        osobaMenadzer.logIn("lana@email.com", "lozinka123");
        
        // 2. Izvlačimo klijenta koji je trenutno ulogovan u menadžeru
        Klijent klijent = (Klijent) osobaMenadzer.getTrenutnoUlogovan();
        assertNotNull(klijent, "Klijent mora biti ulogovan.");
        klijent.setBrojKasnjenja(2); 
        osobaMenadzer.podnesiZahtev();
        assertEquals(enums.ZahtevPretplate.POSLAT, klijent.getZahtev(), 
                "Zahtev bi trebao biti u statusu POSLAT jer klijent ima manje od 5 kašnjenja.");
    }

    @Test
    void testOdbiSveStoKasne() {
        // Dodajmo još jednog klijenta koji kasni preko modifikacije liste
        Klijent k1 = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu("lana@email.com");
        k1.setBrojKasnjenja(7);
        k1.setZahtev(enums.ZahtevPretplate.POSLAT);
        
        osobaMenadzer.odbiSveStoKasne();
        
        assertEquals(enums.ZahtevPretplate.ODBIJEN, k1.getZahtev(), "Metoda mora prebaciti u ODBIJEN sve klijente sa > 5 kašnjenja.");
    }

    // ==========================================
    // 4. TESTOVI ZA CRUD I PROMENE PODATAKA
    // ==========================================

    @Test
    void testKorisnickoImePostoji() {
        assertTrue(osobaMenadzer.korisnickoImePostoji("marko_agent"), "Korisničko ime agenta bi trebalo da bude prepoznato.");
        assertTrue(osobaMenadzer.korisnickoImePostoji("lana@email.com"), "Korisničko ime klijenta bi trebalo da bude prepoznato.");
        assertFalse(osobaMenadzer.korisnickoImePostoji("nepostojeci_user"), "Nepostojeći korisnik ne sme vratiti true.");
    }

    @Test
    void testRegistrujAgentaOnemoguciDuplikat() {
        int inicijalniBroj = osobaMenadzer.getSviAgenti().size();
        osobaMenadzer.registrujAgenta("Novi", "Prezime", Pol.MUSK0, LocalDate.of(1990, 5, 5), 
                "061", "Adresa", "marko_agent", "sifra", StrucnaSprema.SSS, 2);
        
        assertEquals(inicijalniBroj, osobaMenadzer.getSviAgenti().size(), 
                "Sistem ne sme dozvoliti registraciju agenta sa postojećim korisničkim imenom.");
    }

    @Test
    void testIzmeniKlijentaUspesno() {
        Klijent klijent = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu("lana@email.com");
        assertNotNull(klijent);
        
        osobaMenadzer.izmeniKlijenta(klijent, "NovoIme", "NovoPrezime", null, null, null, null, null, null, null, 3, null, null);
        
        assertEquals("NovoIme", klijent.getIme(), "Ime klijenta je trebalo biti izmenjeno.");
        assertEquals("NovoPrezime", klijent.getPrezime(), "Prezime klijenta je trebalo biti izmenjeno.");
        assertEquals(3, klijent.getBrojKasnjenja(), "Broj kašnjenja je trebao biti ažuriran.");
    }
    

    
}