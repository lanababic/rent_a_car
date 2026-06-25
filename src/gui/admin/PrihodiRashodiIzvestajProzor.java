package gui.admin;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import model.IzdavanjeVozila; // Prilagodi putanju paketa ako je potrebno

public class PrihodiRashodiIzvestajProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabelaFinansija;
	private DefaultTableModel tableModel;
	private JLabel lblUkupnoNovac;
	
	private JTextField txtDatumOd;
	private JTextField txtDatumDo;
	
	private OsobaMenadzer osobaMenadzer;
	private IzdavanjeMenadzer izdavanjeMenadzer;

	public PrihodiRashodiIzvestajProzor(OsobaMenadzer osobaMenadzer, IzdavanjeMenadzer izdavanjeMenadzer) {
		this.osobaMenadzer = osobaMenadzer;
		this.izdavanjeMenadzer = izdavanjeMenadzer;
		
		setTitle("Finansijski Izveštaj - Prihodi i Rashodi");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 650, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov
		JLabel lblNaslov = new JLabel("Pregled Prihoda i Rashoda u Periodu");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setBounds(10, 11, 614, 25);
		contentPane.add(lblNaslov);
		
		// --- FILTERI ZA DATUM ---
		JLabel lblDatumOd = new JLabel("Datum od (YYYY-MM-DD):");
		lblDatumOd.setBounds(40, 60, 150, 20);
		contentPane.add(lblDatumOd);
		
		txtDatumOd = new JTextField("2026-01-01");
		txtDatumOd.setBounds(190, 60, 110, 20);
		contentPane.add(txtDatumOd);
		txtDatumOd.setColumns(10);
		
		JLabel lblDatumDo = new JLabel("Datum do (YYYY-MM-DD):");
		lblDatumDo.setBounds(340, 60, 150, 20);
		contentPane.add(lblDatumDo);
		
		txtDatumDo = new JTextField("2026-12-31");
		txtDatumDo.setBounds(490, 60, 110, 20);
		contentPane.add(txtDatumDo);
		txtDatumDo.setColumns(10);
		
		// --- DUGMIĆI ---
		JButton btnPrihodi = new JButton("Prikaži Prihode");
		btnPrihodi.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPrihodi.setBounds(100, 110, 180, 35);
		contentPane.add(btnPrihodi);
		
		JButton btnRashodi = new JButton("Prikaži Rashode");
		btnRashodi.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnRashodi.setBounds(350, 110, 180, 35);
		contentPane.add(btnRashodi);
		
		// --- TABELA ---
		tableModel = new DefaultTableModel();
		tabelaFinansija = new JTable(tableModel);
		
		JScrollPane scrollPane = new JScrollPane(tabelaFinansija);
		scrollPane.setBounds(40, 165, 550, 230);
		contentPane.add(scrollPane);
		
		// Labela za ukupan iznos novca na dnu
		lblUkupnoNovac = new JLabel("Ukupan iznos: 0.00 RSD");
		lblUkupnoNovac.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblUkupnoNovac.setBounds(40, 410, 400, 25);
		contentPane.add(lblUkupnoNovac);
		
		JButton btnZatvori = new JButton("Zatvori");
		btnZatvori.setBounds(490, 420, 100, 30);
		contentPane.add(btnZatvori);
		
		// --- AKCIJE NA DUGMIĆIMA ---
		
		// Prihodi (iz IzdavanjeMenadzer)
		btnPrihodi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prikaziPrihode();
			}
		});
		
		// Rashodi (iz OsobaMenadzer)
		btnRashodi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prikaziRashode();
			}
		});
		
		btnZatvori.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setLocationRelativeTo(null);
	}
	
	// Metoda za punjenje tabele prihodima
	private void prihodiPrihode() { // Zove se iz akcije dugmeta
	}
	
	private void prikaziPrihode() {
		try {
			LocalDate datumOd = LocalDate.parse(txtDatumOd.getText().trim());
			LocalDate datumDo = LocalDate.parse(txtDatumDo.getText().trim());
			
			// Postavljanje kolona za tabelu prihoda
			String[] kolone = {"ID Izdavanja", "Klijent", "Kategorija Vozila", "Iznos (Cena)"};
			tableModel.setColumnIdentifiers(kolone);
			tableModel.setRowCount(0);
			
			// Poziv tvoje metode iz IzdavanjeMenadzer
			ArrayList<IzdavanjeVozila> listaPrihoda = izdavanjeMenadzer.odrediPrihodeUPeriodu(datumOd, datumDo);
			
			for (IzdavanjeVozila iz : listaPrihoda) {
				Object[] red = {
					iz.getIdIzdaje(),
					iz.getRezervacija().getKlijent().getKorisnickoIme(),
					iz.getRezervacija().getModelVozila().getKategorijaVozila(), 
					iz.getUkupnaCena()
				};
				tableModel.addRow(red);
			}
			
			// Računanje i ispis ukupnog prihoda preko tvoje druge metode
			double ukupno = izdavanjeMenadzer.ukupanPrihodUPeriodu(datumOd, datumDo);
			lblUkupnoNovac.setText("Ukupan PRIHOD u periodu: " + String.format("%.2f", ukupno) + " RSD");
			
		} catch (DateTimeParseException ex) {
			prikaziGreskuZaDatum();
		}
	}
	
	// Metoda za punjenje tabele rashodima (plate zaposlenih)
	private void prikaziRashode() {
		try {
			LocalDate datumOd = LocalDate.parse(txtDatumOd.getText().trim());
			LocalDate datumDo = LocalDate.parse(txtDatumDo.getText().trim());
			
			// Postavljanje kolona za tabelu rashoda (pošto je reč o mapi Korisničko Ime -> Plata)
			String[] kolone = {"Korisničko Ime Zaposlenog", "Isplaćena Plata za Period"};
			tableModel.setColumnIdentifiers(kolone);
			tableModel.setRowCount(0);
			
			// Poziv tvoje metode iz OsobaMenadzer
			Map<String, Double> mapaRashoda = osobaMenadzer.odrediRashodeUPeriodu(datumOd, datumDo);
			
			// Prolazak kroz Mapu i upisivanje u tabelu
			for (Map.Entry<String, Double> entry : mapaRashoda.entrySet()) {
				Object[] red = {
					entry.getKey(),   // Korisničko ime (String)
					entry.getValue()  // Ukupna plata (Double)
				};
				tableModel.addRow(red);
			}
			
			// Računanje i ispis ukupnog rashoda preko tvoje druge metode
			double ukupno = osobaMenadzer.ukupanRaskohUPeriodu(datumOd, datumDo);
			lblUkupnoNovac.setText("Ukupan RASHOD u periodu: " + String.format("%.2f", ukupno) + " RSD");
			
		} catch (DateTimeParseException ex) {
			prikaziGreskuZaDatum();
		}
	}
	
	private void prikaziGreskuZaDatum() {
		JOptionPane.showMessageDialog(this, "Datumi moraju biti u formatu GGGG-MM-DD!", "Greška", JOptionPane.ERROR_MESSAGE);
	}
}