package menadzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.ModelVozila;
import model.Vozilo;

class VoziloMenadzerTest {
	
	private OsobaMenadzer osobaMenadzer;
    private VoziloMenadzer voziloMenadzer;
    private RezervacijeMenadzer rezervacijeMenadzer;
    private FinansijeMenadzer finansijeMenadzer;

    private final String VOZILA_TEST = "podaci/vozila_test.csv";
    private final String MODELI_TEST = "podaci/modeliVozila_test.csv";
    private final String KLIJENTI_TEST = "podaci/klijenti_test.csv";
    private final String AGENTI_TEST = "podaci/agenti_test.csv";
    private final String ADMINI_TEST = "podaci/admini_test.csv";


    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get("podaci"));
        
        // --- Podaci za OsobaMenadzer ---
        List<String> klijentiPodaci = List.of("Lana;Lukic;ZENSKO;2002-05-14;063111222;lana@email.com;lana@email.com;lozinka123;BEZ_KATEGORIJE;2021-06-15;0;null;null;NEMA");
        Files.write(Paths.get(KLIJENTI_TEST), klijentiPodaci);
        List<String> agentiPodaci = List.of("Marko;Markovic;MUSK0;1995-03-20;064111222;marko@email.com;marko_agent;lozinka123;VSS;5;0.0");
        Files.write(Paths.get(AGENTI_TEST), agentiPodaci);
        List<String> adminiPodaci = List.of("Jovan;Jovanovic;MUSK0;1990-01-10;065111222;jovan@email.com;jovan_admin;lozinka123;MASTER;10;0.0");
        Files.write(Paths.get(ADMINI_TEST), adminiPodaci);

        // --- Podaci za VoziloMenadzer ---
        // ModelVozila: id;kategorija;naziv;proizvodjac
        List<String> modeliPodaci = List.of(
            "1;ECONOMY;Polo;VW",
            "2;LUXURY;A6;Audi"
        );
        Files.write(Paths.get(MODELI_TEST), modeliPodaci);

        // Vozilo: idVozila;idModela;registracija;trenutnaKilometraza;status
        List<String> vozilaPodaci = List.of(
            "1;1;NS-123-AA;50000;DOSTUPNO",
            "2;1;BG-456-BB;60000;IZDATO",
            "3;2;NI-789-CC;120000;DOSTUPNO"
        );
        Files.write(Paths.get(VOZILA_TEST), vozilaPodaci);

        // Inicijalizacija menadžera - sada sa ispravljenim konstruktorom
        osobaMenadzer = new OsobaMenadzer(KLIJENTI_TEST, AGENTI_TEST, ADMINI_TEST);
        voziloMenadzer = new VoziloMenadzer(VOZILA_TEST, MODELI_TEST);
        
        rezervacijeMenadzer = new RezervacijeMenadzer("podaci/rezervacije_test.csv", "podaci/dodatneUsluge_test.csv", voziloMenadzer, osobaMenadzer);
        finansijeMenadzer = new FinansijeMenadzer("podaci/pretplate_test.csv", "podaci/cenovnici_test.csv", osobaMenadzer, rezervacijeMenadzer);
        osobaMenadzer.poveziKlijenteSaPretplatama(finansijeMenadzer, KLIJENTI_TEST);
    }
 // ==========================================
    // 1. TESTOVI ZA PRETRAGE I FILTRIRANJE
    // ==========================================

    @Test
    void testPronadjiVoziloPoId() {
        Vozilo v = voziloMenadzer.pronadjiVoziloPoId(1);
        assertNotNull(v, "Vozilo sa ID-em 1 mora postojati.");
        assertEquals("NS-123-AA", v.getRegistracijaVozila());
        assertEquals(enums.StatusVozila.DOSTUPNO, v.getStatus());
    }

    @Test
    void testPronadjiVozilaSlobodnaTrenutno() {
        ArrayList<Vozilo> slobodna = voziloMenadzer.pronadjiVozilaSlobodnaTrenutno();
        // Iz inicijalnih podataka, vozilo 1 i 3 su DOSTUPNA
        assertEquals(2, slobodna.size(), "Trebalo bi da postoje tačno 2 slobodna vozila.");
        for (Vozilo v : slobodna) {
            assertEquals(enums.StatusVozila.DOSTUPNO, v.getStatus());
        }
    }

    @Test
    void testPronadjiVozilaSlobodnaTrenutnoPrekoModela() {
        ModelVozila modelPolo = voziloMenadzer.pronadjiModelPoId(1); // VW Polo
        assertNotNull(modelPolo);
        
        ArrayList<Vozilo> slobodniPolo = voziloMenadzer.pronadjiVozilaSlobodnaTrenutnoPrekoModela(modelPolo);
        // Od dva Pola (ID 1 i 2), samo je prvi (NS-123-AA) DOSTUPAN
        assertEquals(1, slobodniPolo.size(), "Trebalo bi da postoji samo jedan slobodan VW Polo.");
        assertEquals("NS-123-AA", slobodniPolo.get(0).getRegistracijaVozila());
    }

    // ==========================================
    // 2. TESTOVI ZA IDENTIFIKATORE I DODAVANJE
    // ==========================================

    @Test
    void testGenerisiNoviIdVozila() {
        // Najveći ID u fajlu je 3, sledeći mora biti 4
        int noviId = voziloMenadzer.generisiNoviIdVozila();
        assertEquals(4, noviId, "Sledeći generisani ID vozila mora biti 4.");
    }

    @Test
    void testDodajNovoVozilo() {
        ModelVozila model = voziloMenadzer.pronadjiModelPoId(2); // Audi A6
        int brojPreDodavanja = voziloMenadzer.getSvaVozila().size();
        
        voziloMenadzer.dodajNovoVozilo(model, "SU-999-XX", 1000, enums.StatusVozila.DOSTUPNO);
        
        assertEquals(brojPreDodavanja + 1, voziloMenadzer.getSvaVozila().size(), "Broj vozila u listi se mora povećati za 1.");
        
        Vozilo najnovije = voziloMenadzer.pronadjiVoziloPoId(4); // Generisani ID je 4
        assertNotNull(najnovije, "Novokreirano vozilo mora biti pronađeno sa ID-em 4.");
        assertEquals("SU-999-XX", najnovije.getRegistracijaVozila());
    }

    // ==========================================
    // 3. TESTOVI ZA CRUD (IZMENE I BRISANJE)
    // ==========================================

    @Test
    void testObrisiVozilo() {
        int brojPreBrisanja = voziloMenadzer.getSvaVozila().size();
        
        voziloMenadzer.obrisiVozilo(2); // Brišemo BG-456-BB
        
        assertEquals(brojPreBrisanja - 1, voziloMenadzer.getSvaVozila().size(), "Lista bi trebala imati jedno vozilo manje.");
        assertNull(voziloMenadzer.pronadjiVoziloPoId(2), "Obrisano vozilo ne sme više biti u sistemu.");
    }

    @Test
    void testIzmeniVoziloUspesnaKilometraza() {
        Vozilo vozilo = voziloMenadzer.pronadjiVoziloPoId(1); // Trenutno 50000 km
        assertNotNull(vozilo);
        
        // Povećavamo kilometražu na 55000 i menjamo status u IZDATO
        voziloMenadzer.izmeniVozilo(vozilo, null, "NS-123-MOD", 55000, enums.StatusVozila.IZDATO);
        
        assertEquals("NS-123-MOD", vozilo.getRegistracijaVozila());
        assertEquals(55000, vozilo.getTrenutnaKilometraza(), "Kilometraža je trebala da se ažurira.");
        assertEquals(enums.StatusVozila.IZDATO, vozilo.getStatus());
    }

    @Test
    void testIzmeniVoziloIgnorisiManjuKilometrazu() {
        Vozilo vozilo = voziloMenadzer.pronadjiVoziloPoId(1); // Trenutno 50000 km
        assertNotNull(vozilo);
        
        // Pokušavamo da smanjimo kilometražu na 40000 (Zakon fizike/biznis pravilo: `trenutnaKilometraza > vozilo.getTrenutnaKilometraza()`)
        voziloMenadzer.izmeniVozilo(vozilo, null, null, 40000, null);
        
        assertEquals(50000, vozilo.getTrenutnaKilometraza(), "Sistem ne sme da dozvoli smanjenje kilometraže vozila.");
    }
}
