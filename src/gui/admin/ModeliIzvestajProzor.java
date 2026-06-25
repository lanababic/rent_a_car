package gui.admin;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import model.ModelVozila; 
import model.IzdavanjeVozila;
import model.Rezervacija;

public class ModeliIzvestajProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabelaPrikaza;
	private DefaultTableModel tableModel;
	private JLabel lblUkupno;
	
	private JTextField txtDatumOd;
	private JTextField txtDatumDo;
	private JComboBox<ModelVozila> cbModeli;
	
	private VoziloMenadzer voziloMenadzer;
	private IzdavanjeMenadzer izdavanjeMenadzer;
	private RezervacijeMenadzer rezervacijeMenadzer;

	public ModeliIzvestajProzor(VoziloMenadzer voziloMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		this.voziloMenadzer = voziloMenadzer;
		this.izdavanjeMenadzer = izdavanjeMenadzer;
		this.rezervacijeMenadzer = rezervacijeMenadzer;
		
		setTitle("Izveštaj po Modelu Vozila");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 530);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov
		JLabel lblNaslov = new JLabel("Statistika Rezervacija i Izdavanja po Modelu");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setBounds(10, 11, 664, 25);
		contentPane.add(lblNaslov);
		
		// --- FILTERI ---
		
		// Model vozila padajući meni
		JLabel lblModel = new JLabel("Izaberite model:");
		lblModel.setBounds(40, 60, 110, 20);
		contentPane.add(lblModel);
		
		cbModeli = new JComboBox<>();
		// Pretpostavka: voziloMenadzer ima metodu getSviModeli() koja vraća listu svih modela u sistemu
		for (ModelVozila m : voziloMenadzer.getSviModeliVozila()) {
			cbModeli.addItem(m);
		}
		cbModeli.setBounds(150, 60, 180, 22);
		contentPane.add(cbModeli);
		
		// Datum Od
		JLabel lblDatumOd = new JLabel("Datum od (YYYY-MM-DD):");
		lblDatumOd.setBounds(40, 100, 150, 20);
		contentPane.add(lblDatumOd);
		
		txtDatumOd = new JTextField("2026-01-01");
		txtDatumOd.setBounds(190, 100, 110, 20);
		contentPane.add(txtDatumOd);
		txtDatumOd.setColumns(10);
		
		// Datum Do
		JLabel lblDatumDo = new JLabel("Datum do (YYYY-MM-DD):");
		lblDatumDo.setBounds(350, 100, 150, 20);
		contentPane.add(lblDatumDo);
		
		txtDatumDo = new JTextField("2026-12-31");
		txtDatumDo.setBounds(500, 100, 110, 20);
		contentPane.add(txtDatumDo);
		txtDatumDo.setColumns(10);
		
		// --- DUGMIĆI ZA POKRETANJE METODA ---
		
		JButton btnPrikaziRezervacije = new JButton("Prikaži Rezervacije");
		btnPrikaziRezervacije.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPrikaziRezervacije.setBounds(120, 150, 200, 35);
		contentPane.add(btnPrikaziRezervacije);
		
		JButton btnPrikaziIzdavanja = new JButton("Prikaži Iznajmljivanja (Izdavanja)");
		btnPrikaziIzdavanja.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPrikaziIzdavanja.setBounds(360, 150, 240, 35);
		contentPane.add(btnPrikaziIzdavanja);
		
		// --- TABELA ---
		tableModel = new DefaultTableModel();
		tabelaPrikaza = new JTable(tableModel);
		
		JScrollPane scrollPane = new JScrollPane(tabelaPrikaza);
		scrollPane.setBounds(40, 205, 600, 230);
		contentPane.add(scrollPane);
		
		// Oznaka na dnu za ukupan broj (length liste)
		lblUkupno = new JLabel("Ukupan broj zapisa: 0");
		lblUkupno.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblUkupno.setBounds(40, 445, 350, 20);
		contentPane.add(lblUkupno);
		
		JButton btnZatvori = new JButton("Zatvori");
		btnZatvori.setBounds(540, 450, 100, 30);
		contentPane.add(btnZatvori);
		
		// --- AKCIJE NA DUGMIĆIMA ---
		
		// Dugme za prikaz rezervacija o modelu
		btnPrikaziRezervacije.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prikaziRezervacijeModela();
			}
		});
		
		// Dugme za prikaz izdavanja o modelu
		btnPrikaziIzdavanja.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prikaziIzdavanjaModela();
			}
		});
		
		btnZatvori.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setLocationRelativeTo(null);
	}
	
	// 1. Logika za prikaz Rezervacija (koristi tvoju drugu metodu)
	private void prikaziRezervacijeModela() {
		ModelVozila selektovaniModel = (ModelVozila) cbModeli.getSelectedItem();
		if (selektovaniModel == null) return;
		
		try {
			LocalDate datumOd = LocalDate.parse(txtDatumOd.getText().trim());
			LocalDate datumDo = LocalDate.parse(txtDatumDo.getText().trim());
			
			// Postavljamo kolone specifične za rezervaciju
			String[] kolone = {"ID Rezervacije", "Klijent", "Kategorija", "Datum Kreiranja", "Status Rezervacije"};
			tableModel.setColumnIdentifiers(kolone);
			tableModel.setRowCount(0); // Čistimo stare podatke
			
			// Poziv tvoje metode iz RezervacijeMenadzer preko ID-ja modela
			ArrayList<Rezervacija> listaRezervacija = rezervacijeMenadzer.izvestajRezervacijaOModeluVozila(selektovaniModel.getId(), datumOd, datumDo);
			
			for (Rezervacija r : listaRezervacija) {
				Object[] red = {
					r.getIdRezervacije(),
					r.getKlijent().getKorisnickoIme(),
					r.getModelVozila().getKategorijaVozila(),
					r.getDatumPravljenja(),
					r.getStatus()
				};
				tableModel.addRow(red);
			}
			
			// Prikaz ukupne dužine liste na dnu
			lblUkupno.setText("Ukupan broj rezervacija za model: " + listaRezervacija.size());
			
		} catch (DateTimeParseException ex) {
			prikaziGreskuZaDatum();
		}
	}
	
	// 2. Logika za prikaz Izdavanja (koristi tvoju prvu metodu)
	private void prikaziIzdavanjaModela() {
		ModelVozila selektovaniModel = (ModelVozila) cbModeli.getSelectedItem();
		if (selektovaniModel == null) return;
		
		try {
			LocalDate datumOd = LocalDate.parse(txtDatumOd.getText().trim());
			LocalDate datumDo = LocalDate.parse(txtDatumDo.getText().trim());

			String[] kolone = {"ID Izdavanja", "Agent Izdao", "Klijent", "Kategorija", "Datum Izdavanja"};
			tableModel.setColumnIdentifiers(kolone);
			tableModel.setRowCount(0);
			
			// Poziv tvoje metode iz IzdavanjeMenadzer preko ID-ja modela
			ArrayList<IzdavanjeVozila> listaIzdavanja = izdavanjeMenadzer.izvestajIzdavanjaOModeluVozila(selektovaniModel.getId(), datumOd, datumDo);
			
			for (IzdavanjeVozila iz : listaIzdavanja) {
				Object[] red = {
					iz.getIdIzdaje(),
					iz.getAgentIzdao().getKorisnickoIme(),
					iz.getRezervacija().getKlijent().getKorisnickoIme(),
					iz.getRezervacija().getModelVozila().getKategorijaVozila(),
					iz.getDatumPravljenjaIzdaje()
				};
				tableModel.addRow(red);
			}
			
			// Prikaz ukupne dužine liste na dnu
			lblUkupno.setText("Ukupan broj izdavanja za model: " + listaIzdavanja.size());
			
		} catch (DateTimeParseException ex) {
			prikaziGreskuZaDatum();
		}
	}
	
	private void prikaziGreskuZaDatum() {
		JOptionPane.showMessageDialog(this, "Datumi moraju biti u formatu GGGG-MM-DD!", "Greška", JOptionPane.ERROR_MESSAGE);
	}
}