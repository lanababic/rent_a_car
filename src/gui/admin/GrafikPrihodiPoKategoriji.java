package gui.admin;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler.LegendPosition;

// Uvozi tvoje klase iz drugih paketa (prilagodi tačne nazive paketa ako se razlikuju)
import enums.KategorijaKlijenta;
import model.IzdavanjeVozila;
import menadzer.IzdavanjeMenadzer;

public class GrafikPrihodiPoKategoriji extends JPanel {

    private static final long serialVersionUID = 1L;
    
    // Čuvamo referencu na menadžer iz kojeg vučemo podatke i metode
    private IzdavanjeMenadzer izdavanjeMenadzer; 

    public GrafikPrihodiPoKategoriji(IzdavanjeMenadzer izdavanjeMenadzer) {
        this.izdavanjeMenadzer = izdavanjeMenadzer;
        
        setLayout(new BorderLayout());

        // 1. Kreiranje grafikona
        CategoryChart chart = new CategoryChartBuilder()
                .width(900)
                .height(600)
                .title("Prihodi u prethodnih 12 meseci po kategorijama klijenata")
                .xAxisTitle("Mesec")
                .yAxisTitle("Prihod (RSD)")
                .build();

        // Podešavanje stila
        chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
        chart.getStyler().setLabelsVisible(false); 

        // 2. Priprema lista za podatke
        List<String> xDataMeseci = new ArrayList<>();
        List<Double> yStudent = new ArrayList<>();
        List<Double> yPenzioner = new ArrayList<>();
        List<Double> yFirma = new ArrayList<>();
        List<Double> yBezKategorije = new ArrayList<>();
        List<Double> yUkupno = new ArrayList<>();

        // Trenutni datum i formatiranje (npr. "06.2026")
        LocalDate danas = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.yyyy");

        // Prolazimo kroz prethodnih 12 meseci
        for (int i = 11; i >= 0; i--) {
            // Izračunavanje opsega za trenutni mesec u petlji
        	LocalDate posmatraniMesec = danas.minusMonths(i);
        	LocalDate pocetakMeseca = posmatraniMesec.withDayOfMonth(1).minusDays(1);
            LocalDate krajMeseca = posmatraniMesec.withDayOfMonth(posmatraniMesec.lengthOfMonth()).plusDays(1);

            xDataMeseci.add(danas.minusMonths(i).format(formatter));

            // Pozivanje metoda direktno iz prosleđenog menadžera
            ArrayList<IzdavanjeVozila> listaStudent = this.izdavanjeMenadzer.odrediPrihodeUPerioduZaKategoriju(pocetakMeseca, krajMeseca, KategorijaKlijenta.STUDENT);
            double prihodStudent = this.izdavanjeMenadzer.ukupanPrihodOdKategorije(listaStudent);
            yStudent.add(prihodStudent);

            ArrayList<IzdavanjeVozila> listaPenzioner = this.izdavanjeMenadzer.odrediPrihodeUPerioduZaKategoriju(pocetakMeseca, krajMeseca, KategorijaKlijenta.PENZIONER);
            double prihodPenzioner = this.izdavanjeMenadzer.ukupanPrihodOdKategorije(listaPenzioner);
            yPenzioner.add(prihodPenzioner);

            ArrayList<IzdavanjeVozila> listaFirma = this.izdavanjeMenadzer.odrediPrihodeUPerioduZaKategoriju(pocetakMeseca, krajMeseca, KategorijaKlijenta.FIRMA);
            double prihodFirma = this.izdavanjeMenadzer.ukupanPrihodOdKategorije(listaFirma);
            yFirma.add(prihodFirma);

            ArrayList<IzdavanjeVozila> listaBezKat = this.izdavanjeMenadzer.odrediPrihodeUPerioduZaKategoriju(pocetakMeseca, krajMeseca, KategorijaKlijenta.BEZ_KATEGORIJE);
            double prihodBezKat = this.izdavanjeMenadzer.ukupanPrihodOdKategorije(listaBezKat);
            yBezKategorije.add(prihodBezKat);

            // Ukupan mesečni prihod kao zbir svih kategorija
            double prihodUkupno = prihodStudent + prihodPenzioner + prihodFirma + prihodBezKat;
            yUkupno.add(prihodUkupno);
        }

        // 3. Dodavanje serija u grafikon
        chart.addSeries("STUDENT", xDataMeseci, yStudent);
        chart.addSeries("PENZIONER", xDataMeseci, yPenzioner);
        chart.addSeries("FIRMA", xDataMeseci, yFirma);
        chart.addSeries("BEZ_KATEGORIJE", xDataMeseci, yBezKategorije);
        chart.addSeries("UKUPAN PRIHOD", xDataMeseci, yUkupno);

        // 4. Kreiranje panela i dodavanje na GUI
        JPanel chartPanel = new XChartPanel<CategoryChart>(chart);
        add(chartPanel, BorderLayout.CENTER);
    }
}