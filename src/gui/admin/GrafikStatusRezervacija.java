package gui.admin;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler.LegendPosition;

// Uvozi tvoje klase iz drugih paketa (prilagodi tačne nazive paketa ako se razlikuju)
import enums.StatusRezervacije;
import menadzer.RezervacijeMenadzer;

public class GrafikStatusRezervacija extends JPanel {

    private static final long serialVersionUID = 1L;
    private RezervacijeMenadzer rezervacijeMenadzer;

    public GrafikStatusRezervacija(RezervacijeMenadzer rezervacijeMenadzer) {
        
        setLayout(new BorderLayout());

        // 1. Kreiranje kružnog grafikona (Pie Chart)
        PieChart chart = new PieChartBuilder()
                .width(600)
                .height(450)
                .title("Statusi rezervacija kreiranih u poslednjih 30 dana")
                .build();

        // Podešavanje stila grafikona
        chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
        chart.getStyler().setCircular(true); // Osigurava da grafik uvek bude pravilan krug

        // 2. Izvlačenje podataka preko tvoje metode iz menadžera
        int naCekanju = rezervacijeMenadzer.brojRezervacijaSStatusom(StatusRezervacije.NA_CEKANJU);
        int potvrdjeno = rezervacijeMenadzer.brojRezervacijaSStatusom(StatusRezervacije.POTVRDJENO);
        int odbijeno = rezervacijeMenadzer.brojRezervacijaSStatusom(StatusRezervacije.ODBIJENO);
        int otkazano = rezervacijeMenadzer.brojRezervacijaSStatusom(StatusRezervacije.OTKAZANO);

        // 3. Dodavanje podataka na grafik samo ako je broj veći od 0 (da ne prikazuje prazne labele)
        if (naCekanju > 0) chart.addSeries("NA ČEKANJU", naCekanju);
        if (potvrdjeno > 0) chart.addSeries("POTVRĐENO", potvrdjeno);
        if (odbijeno > 0) chart.addSeries("ODBIJENO", odbijeno);
        if (otkazano > 0) chart.addSeries("OTKAZANO", otkazano);
        
        // Ako nema nijedne rezervacije u tom periodu, stavljamo "Prazno" čisto da se grafik ne sruši
        if (naCekanju == 0 && potvrdjeno == 0 && odbijeno == 0 && otkazano == 0) {
            chart.addSeries("Nema podataka za prethodnih 30 dana", 1);
        }

        // 4. Kreiranje XChart panela i dodavanje u naš JPanel
        JPanel chartPanel = new XChartPanel<PieChart>(chart);
        add(chartPanel, BorderLayout.CENTER);
    }
}