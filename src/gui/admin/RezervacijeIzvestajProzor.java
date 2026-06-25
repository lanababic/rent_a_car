package gui.admin;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

import menadzer.RezervacijeMenadzer;
// Uvezi klasu Rezervacija u zavisnosti od njenog paketa (npr. klase.Rezervacija)
import model.Rezervacija; 

public class RezervacijeIzvestajProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabelaRezervacija;
	private DefaultTableModel tableModel;
	private JLabel lblUkupnoRezervacija;
	
	private JTextField txtDatumOd;
	private JTextField txtDatumDo;
	
	private RezervacijeMenadzer rezervacijeMenadzer;

	public RezervacijeIzvestajProzor(RezervacijeMenadzer rezervacijeMenadzer) {
		this.rezervacijeMenadzer = rezervacijeMenadzer;
		
		setTitle("Izveštaj o Rezervacijama");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 520);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov prozora
		JLabel lblNaslov = new JLabel("Statistika i Izveštaj Rezervacija");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(10, 11, 664, 25);
		contentPane.add(lblNaslov);
		
		// --- INPUTI ZA DATUM ---
		JLabel lblDatumOd = new JLabel("Datum od (YYYY-MM-DD):");
		lblDatumOd.setBounds(40, 60, 150, 20);
		contentPane.add(lblDatumOd);
		
		txtDatumOd = new JTextField("2026-01-01");
		txtDatumOd.setBounds(190, 60, 110, 20);
		contentPane.add(txtDatumOd);
		txtDatumOd.setColumns(10);
		
		JLabel lblDatumDo = new JLabel("Datum do (YYYY-MM-DD):");
		lblDatumDo.setBounds(360, 60, 150, 20);
		contentPane.add(lblDatumDo);
		
		txtDatumDo = new JTextField("2026-12-31");
		txtDatumDo.setBounds(510, 60, 110, 20);
		contentPane.add(txtDatumDo);
		txtDatumDo.setColumns(10);
		
		// --- 3 DUGMETA ZA FILTERE ---
		JButton btnPrihvacene = new JButton("Prihvaćene");
		btnPrihvacene.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPrihvacene.setBounds(40, 110, 180, 35);
		contentPane.add(btnPrihvacene);
		
		JButton btnOdbijene = new JButton("Odbijene");
		btnOdbijene.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnOdbijene.setBounds(250, 110, 180, 35);
		contentPane.add(btnOdbijene);
		
		JButton btnOtkazane = new JButton("Otkazane");
		btnOtkazane.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnOtkazane.setBounds(460, 110, 180, 35);
		contentPane.add(btnOtkazane);
		
		// --- TABELA ---
		// Prilagodi kolone onome što tvoja klasa Rezervacija ima (ID, klijent, vozilo, datum kreiranja...)
		String[] kolone = {"ID", "Klijent", "Model vozila", "Datum rezervacije", "Status"};
		tableModel = new DefaultTableModel(kolone, 0);
		tabelaRezervacija = new JTable(tableModel);
		
		JScrollPane scrollPane = new JScrollPane(tabelaRezervacija);
		scrollPane.setBounds(40, 165, 600, 240);
		contentPane.add(scrollPane);
		
		// Oznaka za ukupan broj (veličina liste) ispod tabele
		lblUkupnoRezervacija = new JLabel("Ukupan broj rezervacija: 0");
		lblUkupnoRezervacija.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblUkupnoRezervacija.setBounds(40, 420, 300, 20);
		contentPane.add(lblUkupnoRezervacija);
		
		// Dugme za nazad / zatvaranje
		JButton btnZatvori = new JButton("Zatvori");
		btnZatvori.setBounds(540, 440, 100, 30);
		contentPane.add(btnZatvori);
		
		// --- AKCIJE NA DUGMIĆIMA ---
		
		// 1. Dugme: Prihvaćene
		btnPrihvacene.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prikaziRezervacije(1); // 1 označava Potvrđene
			}
		});
		
		// 2. Dugme: Odbijene
		btnOdbijene.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prikaziRezervacije(2); // 2 označava Odbijene
			}
		});
		
		// 3. Dugme: Otkazane
		btnOtkazane.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prikaziRezervacije(3); // 3 označava Otkazane
			}
		});
		
		btnZatvori.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setLocationRelativeTo(null);
	}
	
	// Pomoćna metoda koja poziva odgovarajuću metodu iz menadžera u zavisnosti od tipa (1, 2 ili 3)
	private void prikaziRezervacije(int tipStatusa) {
		tableModel.setRowCount(0); // Očisti tabelu pre novog ispisa
		
		try {
			// Parsiranje datuma
			LocalDate datumOd = LocalDate.parse(txtDatumOd.getText().trim());
			LocalDate datumDo = LocalDate.parse(txtDatumDo.getText().trim());
			
			ArrayList<Rezervacija> filtriranaLista = new ArrayList<>();
			
			// Biranje adekvatne metode iz menadžera na osnovu kliknutog dugmeta
			if (tipStatusa == 1) {
				filtriranaLista = rezervacijeMenadzer.izvestajPotvrdjenihRezervacijaUPeriodu(datumOd, datumDo);
			} else if (tipStatusa == 2) {
				filtriranaLista = rezervacijeMenadzer.izvestajOdbijenihRezervacijaUPeriodu(datumOd, datumDo);
			} else if (tipStatusa == 3) {
				filtriranaLista = rezervacijeMenadzer.izvestajOtkazanihRezervacijaUPeriodu(datumOd, datumDo);
			}
			
			// Punjenje tabele dobijenim podacima
			for (Rezervacija r : filtriranaLista) {
				Object[] red = {
					r.getIdRezervacije(),
					r.getKlijent().getKorisnickoIme(), 
					r.getModelVozila().getNaziv(),   
					r.getDatumPravljenja(),          
					r.getStatus()
				};
				tableModel.addRow(red);
			}
			
			// Prikaz ukupne dužine (length / size) liste ispod tabele
			lblUkupnoRezervacija.setText("Ukupan broj rezervacija: " + filtriranaLista.size());
			
		} catch (DateTimeParseException ex) {
			JOptionPane.showMessageDialog(this, "Greška! Datumi moraju biti u formatu GGGG-MM-DD.", "Neispravan Format", JOptionPane.ERROR_MESSAGE);
		}
	}
}