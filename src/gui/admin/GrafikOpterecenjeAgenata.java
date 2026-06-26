package gui.admin;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler.LegendPosition;

// Uvozi klase iz tvojih modela i menadžera
import model.Agent;
import menadzer.OsobaMenadzer;
import menadzer.IzdavanjeMenadzer;

public class GrafikOpterecenjeAgenata extends JPanel {

    private static final long serialVersionUID = 1L;
    private OsobaMenadzer osobaMenadzer;
    private IzdavanjeMenadzer izdavanjeMenadzer;

    public GrafikOpterecenjeAgenata(OsobaMenadzer osobaMenadzer, IzdavanjeMenadzer izdavanjeMenadzer) {
        this.osobaMenadzer = osobaMenadzer;
        this.izdavanjeMenadzer = izdavanjeMenadzer;
        
        setLayout(new BorderLayout());

        // 1. Kreiranje kružnog grafikona
        PieChart chart = new PieChartBuilder()
                .width(600)
                .height(450)
                .title("Opterećenje agenata po broju obrađenih rezervacija (poslednjih 30 dana)")
                .build();

        // Podešavanje stila
        chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
        chart.getStyler().setCircular(true);

        // 2. Prolazak kroz sve agente i izvlačenje podataka
        ArrayList<Agent> sviAgenti = this.osobaMenadzer.getSviAgenti();
        int ukupnoOgradjenihSve = 0;

        for (Agent a : sviAgenti) {
            // Pozivamo tvoju metodu iz IzdavanjeMenadzer za svakog agenta
            int brojObradjenih = this.izdavanjeMenadzer.brojObradjenRezervacijaOdAgenta(a);
            
            // Dodajemo agenta na grafik samo ako je obradio bar jednu rezervaciju
            if (brojObradjenih > 0) {
                // Prikazujemo ime i prezime agenta u legendi (pretpostavka da ima ove getere)
                String imeAgenta = a.getIme() + " " + a.getPrezime();
                chart.addSeries(imeAgenta, brojObradjenih);
                ukupnoOgradjenihSve += brojObradjenih;
            }
        }

        // Ako nijedan agent nije obradio ništa u prethodnih 30 dana
        if (ukupnoOgradjenihSve == 0) {
            chart.addSeries("Nema aktivnih rezervacija za agente", 1);
        }

        // 3. Prikaz grafikona na panelu
        JPanel chartPanel = new XChartPanel<PieChart>(chart);
        add(chartPanel, BorderLayout.CENTER);
    }
}