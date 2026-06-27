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
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Klijent;
import model.Rezervacija;

class RezervacijeMenadzerTest {
	
	private OsobaMenadzer osobaMenadzer;
    private VoziloMenadzer voziloMenadzer;
    private RezervacijeMenadzer rezervacijeMenadzer;
    private FinansijeMenadzer finansijeMenadzer;

    private final String VOZILA_TEST = "podaci/vozila_test.csv";
    private final String MODELI_TEST = "podaci/modeliVozila_test.csv";
    private final String KLIJENTI_TEST = "podaci/klijenti_test.csv";
    private final String AGENTI_TEST = "podaci/agenti_test.csv";
    private final String ADMINI_TEST = "podaci/admini_test.csv";
    private final String REZERVACIJE_TEST = "podaci/rezervacije_test.csv";
    private final String USLUGE_TEST = "podaci/dodatneUsluge_test.csv";

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get("podaci"));
        
        // --- OsobaMenadzer podaci ---
        List<String> klijentiPodaci = List.of("Lana;Lukic;ZENSKO;2002-05-14;063111222;lana@email.com;lana@email.com;lozinka123;BEZ_KATEGORIJE;2021-06-15;0;null;null;NEMA");
        Files.write(Paths.get(KLIJENTI_TEST), klijentiPodaci);
        List<String> agentiPodaci = List.of("Marko;Markovic;MUSK0;1995-03-20;064111222;marko@email.com;marko_agent;lozinka123;VSS;5;0.0");
        Files.write(Paths.get(AGENTI_TEST), agentiPodaci);
        List<String> adminiPodaci = List.of("Jovan;Jovanovic;MUSK0;1990-01-10;065111222;jovan@email.com;jovan_admin;lozinka123;MASTER;10;0.0");
        Files.write(Paths.get(ADMINI_TEST), adminiPodaci);

        // --- VoziloMenadzer podaci ---
        List<String> modeliPodaci = List.of("1;ECONOMY;Polo;VW", "2;LUXURY;A6;Audi");
        Files.write(Paths.get("podaci/modeliVozila_test.csv"), modeliPodaci);
        List<String> vozilaPodaci = List.of("1;1;NS-123-AA;50000;DOSTUPNO");
        Files.write(Paths.get("podaci/vozila_test.csv"), vozilaPodaci);

        // --- RezervacijeMenadzer podaci ---
        // DodatneUsluge: id;naziv
        List<String> uslugePodaci = List.of(
            "1;GPS navigacija",
            "2;Produzeni dan"
        );
        Files.write(Paths.get(USLUGE_TEST), uslugePodaci);

        // Rezervacije: id;idModela;datumOd;datumDo;status;osnovnaCena;datumPravljenja;klijentKorisnickoIme;idUsluga(odvojeni zarezom)
        // Pravimo jednu rezervaciju NA_CEKANJU koja počinje sutra, i jednu POTVRDJENU koja je "istekla" (od pre par dana) za test no-show-a
        LocalDate sutra = LocalDate.now().plusDays(1);
        LocalDate prePetDana = LocalDate.now().minusDays(5);
        LocalDate preDvaDana = LocalDate.now().minusDays(2);
        
        List<String> rezervacijePodaci = List.of(
            "1;1;" + sutra + ";" + sutra.plusDays(3) + ";NA_CEKANJU;4500.0;" + LocalDate.now() + ";lana@email.com;1",
            "2;2;" + prePetDana + ";" + preDvaDana + ";POTVRDJENO;12000.0;" + prePetDana + ";lana@email.com;null"
        );
        Files.write(Paths.get(REZERVACIJE_TEST), rezervacijePodaci);

        // Inicijalizacija menadžera redom
        osobaMenadzer = new OsobaMenadzer(KLIJENTI_TEST, AGENTI_TEST, ADMINI_TEST);
        voziloMenadzer = new VoziloMenadzer("podaci/vozila_test.csv", "podaci/modeliVozila_test.csv");
        
        // Inicijalizujemo RezervacijeMenadzer prosleđujući kompletne menadžere
        rezervacijeMenadzer = new RezervacijeMenadzer(REZERVACIJE_TEST, USLUGE_TEST, voziloMenadzer, osobaMenadzer);
        
        finansijeMenadzer = new FinansijeMenadzer("podaci/pretplate_test.csv", "podaci/cenovnici_test.csv", osobaMenadzer, rezervacijeMenadzer);
        osobaMenadzer.poveziKlijenteSaPretplatama(finansijeMenadzer, KLIJENTI_TEST);
    }

    @Test
    void testOdbiRezervacijuUspesno() {
        Rezervacija r = rezervacijeMenadzer.pronadjiRezervacijuPoId(1);
        assertNotNull(r);
        assertEquals(enums.StatusRezervacije.NA_CEKANJU, r.getStatus());
        
        rezervacijeMenadzer.odbiRezervaciju(r);
        
        assertEquals(enums.StatusRezervacije.ODBIJENO, r.getStatus(), 
                "Status rezervacije koja je bila NA_CEKANJU mora preći u ODBIJENO.");
    }

    @Test
    void testOtkaziRezervacijuKlijentAzuoriraDatum() {
        Rezervacija r = rezervacijeMenadzer.pronadjiRezervacijuPoId(1);
        Klijent k = r.getKlijent();
        assertNull(k.getDatumOtkazivanja(), "Inicijalno, klijent nema zabeležen datum otkazivanja.");
        
        rezervacijeMenadzer.otkaziRezervacijuKlijent(r, osobaMenadzer);
        
        assertEquals(enums.StatusRezervacije.OTKAZANO, r.getStatus());
        assertEquals(LocalDate.now(), k.getDatumOtkazivanja(), 
                "Klijentu mora biti postavljen današnji datum kao datum otkazivanja.");
    }

    @Test
    void testOtkaziRezervacijeBezPojaveNoShow() {
        // Rezervacija ID 2 je POTVRDJENO, a datumOd je bio pre 5 dana (klijent se nije pojavio)
        Rezervacija r2 = rezervacijeMenadzer.pronadjiRezervacijuPoId(2);
        assertEquals(enums.StatusRezervacije.POTVRDJENO, r2.getStatus());
        
        rezervacijeMenadzer.otkaziRezervacijeBezPojave(osobaMenadzer, null); // izdMen nam ne treba za ovu metodu
        
        assertEquals(enums.StatusRezervacije.OTKAZANO, r2.getStatus(), 
                "Potvrđena rezervacija čiji je datum početka prošao mora automatski biti OTKAZANA.");
    }

    @Test
    void testIsOtkazaoUPoslenjih24hFalseKadaJeNull() {
        Klijent k = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu("lana@email.com");
        k.setDatumOtkazivanja(null);
        
        boolean otkazao = rezervacijeMenadzer.isOtkazaoUPoslenjih24h(k);
        
        assertFalse(otkazao, "Metoda mora vratiti false ako klijent nikada ranije nije otkazao.");
    }

    @Test
    void testIsOtkazaoUPoslenjih24hTrueKadaJeOtkazaoDanas() {
        Klijent k = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu("lana@email.com");
        // Simuliramo da je otkazao upravo sada (unutar 24h)
        k.setDatumOtkazivanja(LocalDate.now());
        
        boolean otkazao = rezervacijeMenadzer.isOtkazaoUPoslenjih24h(k);
        
        assertTrue(otkazao, "Metoda mora vratiti true ako je datum otkazivanja unutar poslednjih 24h.");
    }

    @Test
    void testOdbiIstekleRezervacijeNaDanPocetka() {
        Rezervacija r = rezervacijeMenadzer.pronadjiRezervacijuPoId(1);
        // Postavljamo datum početka rezervacije na danas, a i dalje je na čekanju
        r.setDatumOd(LocalDate.now());
        
        rezervacijeMenadzer.odbiIstekleRezervacije(r);
        
        assertEquals(enums.StatusRezervacije.ODBIJENO, r.getStatus(), 
                "Rezervacija na čekanju mora biti odbijena ako je došao dan njenog početka, a nije potvrđena.");
    }

    @Test
    void testGenerisiNoviIdRezervacije() {
        // Imamo ID 1 i ID 2 u fajlu, sledeći mora biti 3
        int noviId = rezervacijeMenadzer.generisiNoviIdRezervacije();
        assertEquals(3, noviId);
    }

    @Test
    void testUcitajSveRezervacijeOdKlijenta() {
        Klijent k = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu("lana@email.com");
        ArrayList<Rezervacija> klijentoveRezervacije = rezervacijeMenadzer.ucitajSveRezervacijeOdKlijenta(k);
        
        // Lana ima obe rezervacije iz našeg fajla
        assertEquals(2, klijentoveRezervacije.size(), "Lana bi trebala da ima tačno 2 rezervacije.");
    }

}
