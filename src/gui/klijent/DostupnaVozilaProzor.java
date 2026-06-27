package gui.klijent;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import enums.KategorijaVozila;
import enums.StatusVozila;
import menadzer.IzdavanjeMenadzer;
import menadzer.VoziloMenadzer;
import model.ModelVozila;
import model.Vozilo;     

public class DostupnaVozilaProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabela;
	private DefaultTableModel tableModel;
	private VoziloMenadzer voziloMenadzer;
	private IzdavanjeMenadzer izdavanjeMenadzer;

	// Polja za filtriranje
	private JTextField txtNazivModel;
	private JTextField txtProizvodjac;
	private JComboBox<Object> cbKategorija; 
	
	private JTextField txtIdVozila;
	private JTextField txtDatumOd;
	private JTextField txtDatumDo;

	public DostupnaVozilaProzor(VoziloMenadzer voziloMenadzer, IzdavanjeMenadzer izdavanjeMenadzer) {
		this.voziloMenadzer = voziloMenadzer;
		this.izdavanjeMenadzer = izdavanjeMenadzer;

		setTitle("Pregled dostupnih vozila");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 850, 600); 

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 10));

		// --- SEVERNI PANEL: NASLOV I FILTERI ---
		JPanel panelSever = new JPanel(new BorderLayout(0, 10));

		JLabel lblNaslov = new JLabel("Dostupna vozila za iznajmljivanje");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		panelSever.add(lblNaslov, BorderLayout.NORTH);

		// Panel sa poljima za unos filtera
		JPanel panelFilteri = new JPanel(new GridLayout(2, 3, 10, 5));

		panelFilteri.add(new JLabel("Naziv modela:"));
		panelFilteri.add(new JLabel("Proizvođač:"));
		panelFilteri.add(new JLabel("Kategorija vozila:"));

		txtNazivModel = new JTextField();
		txtProizvodjac = new JTextField();
		cbKategorija = new JComboBox<>();
		cbKategorija.addItem("Sve");
		for (KategorijaVozila kat : KategorijaVozila.values()) {
			cbKategorija.addItem(kat);
		}

		panelFilteri.add(txtNazivModel);
		panelFilteri.add(txtProizvodjac);
		panelFilteri.add(cbKategorija);

		panelSever.add(panelFilteri, BorderLayout.CENTER);

		// Dugme za pokretanje filtriranja i reset
		JPanel panelFilterDugmici = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnFiltriraj = new JButton("Filtriraj");
		JButton btnResetuj = new JButton("Prikaži sve");
		panelFilterDugmici.add(btnResetuj);
		panelFilterDugmici.add(btnFiltriraj);
		
		panelSever.add(panelFilterDugmici, BorderLayout.SOUTH);
		contentPane.add(panelSever, BorderLayout.NORTH);

		// --- CENTAR: TABELA ---
		String[] kolone = {"ID Vozila", "Proizvođač", "Model", "Kategorija", "Registracija", "Kilometraža"};
		
		tableModel = new DefaultTableModel(kolone, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tabela = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(tabela);
		contentPane.add(scrollPane, BorderLayout.CENTER);

//		// --- JUG: DUGME ZA ZATVARANJE ---
//		JPanel panelJug = new JPanel();
//		JButton btnZatvori = new JButton("Zatvori");
//		btnZatvori.addActionListener(e -> dispose());
//		panelJug.add(btnZatvori);
//		contentPane.add(panelJug, BorderLayout.SOUTH);
//		
		JPanel panelJugKombinovani = new JPanel(new BorderLayout(0, 10));

		// Okvir sa naslovom za proveru perioda
		JPanel panelProvera = new JPanel(new GridLayout(2, 3, 10, 5));
		panelProvera.setBorder(new TitledBorder("Provera slobodnih termina za vozilo"));

		panelProvera.add(new JLabel("ID Vozila:"));
		panelProvera.add(new JLabel("Datum od (GGGG-MM-DD):"));
		panelProvera.add(new JLabel("Datum do (GGGG-MM-DD):"));

		txtIdVozila = new JTextField();
		txtDatumOd = new JTextField();
		txtDatumDo = new JTextField();

		panelProvera.add(txtIdVozila);
		panelProvera.add(txtDatumOd);
		panelProvera.add(txtDatumDo);

		panelJugKombinovani.add(panelProvera, BorderLayout.NORTH);

		// Donji dugmići (Proveri i Zatvori)
		JPanel panelDugmiciAkcije = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		JButton btnProveriTermin = new JButton("Proveri dostupnost");
		JButton btnZatvori = new JButton("Zatvori");
		panelDugmiciAkcije.add(btnProveriTermin);
		panelDugmiciAkcije.add(btnZatvori);

		panelJugKombinovani.add(panelDugmiciAkcije, BorderLayout.SOUTH);
		contentPane.add(panelJugKombinovani, BorderLayout.SOUTH);
		

		// --- AKCIJE NA DUGMIĆIMA ZA FILTERE ---
		btnFiltriraj.addActionListener(e -> osveziTabelu());
		
		btnResetuj.addActionListener(e -> {
			txtNazivModel.setText("");
			txtProizvodjac.setText("");
			cbKategorija.setSelectedIndex(0); // Vraća na "Sve"
			osveziTabelu();
		});

		btnProveriTermin.addActionListener(e -> akcijaProveriTermin());
		btnZatvori.addActionListener(e -> dispose());

		osveziTabelu();

		setLocationRelativeTo(null);
		
	}

	/**
	 * Metoda prolazi kroz sva vozila, primenjuje filtere i puni tabelu.
	 */
	private void osveziTabelu() {
		// Brišemo stare podatke iz tabele
		tableModel.setRowCount(0);

		// Kupljenje vrednosti iz UI komponenti i pretvaranje u mala slova radi lakše pretrage
		String filterNaziv = txtNazivModel.getText().trim().toLowerCase();
		String filterProizvodjac = txtProizvodjac.getText().trim().toLowerCase();
		Object odabranaKategorija = cbKategorija.getSelectedItem();

		for (Vozilo v : voziloMenadzer.getSvaVozila()) {
			
			// 1. KLIJENT VIDI SAMO SLOBODNA/DOSTUPNA VOZILA
			// (Zameni sa tvojom tačnom enum vrednošću ako nije SLOBODAN, npr. DOSTUPNO)
			if (v.getStatus() != StatusVozila.DOSTUPNO) {
				continue; 
			}

			ModelVozila model = v.getModelVozila();
			if (model == null) {
				continue; // Preskačemo vozila koja nemaju dodeljen model
			}

			// 2. FILTRIRANJE PO NAZIVU MODELA (ako je uneto)
			if (!filterNaziv.isEmpty() && !model.getNaziv().toLowerCase().contains(filterNaziv)) {
				continue;
			}

			// 3. FILTRIRANJE PO PROIZVOĐAČU (ako je uneto)
			if (!filterProizvodjac.isEmpty() && !model.getProizvodjac().toLowerCase().contains(filterProizvodjac)) {
				continue;
			}

			// 4. FILTRIRANJE PO KATEGORIJI (ako nije izabrano "Sve")
			if (!odabranaKategorija.equals("Sve")) {
				KategorijaVozila katEnum = (KategorijaVozila) odabranaKategorija;
				if (model.getKategorijaVozila() != katEnum) {
					continue;
				}
			}

			// Ako je vozilo prošlo sve provere, dodajemo ga u tabelu
			Object[] red = {
				v.getIdVozila(),
				model.getProizvodjac(),
				model.getNaziv(),
				model.getKategorijaVozila(),
				v.getRegistracijaVozila(),
				v.getTrenutnaKilometraza() + " km"
			};
			tableModel.addRow(red);
		}
	}
	private void akcijaProveriTermin() {
		String idTekst = txtIdVozila.getText().trim();
		String odTekst = txtDatumOd.getText().trim();
		String doTekst = txtDatumDo.getText().trim();

		// 1. Provera da li su polja prazna
		if (idTekst.isEmpty() || odTekst.isEmpty() || doTekst.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Morate popuniti sva polja (ID, Datum od, Datum do).", "Greška", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// 2. Parsiranje ID-ja i pronalaženje vozila
		int idVozila;
		try {
			idVozila = Integer.parseInt(idTekst);
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "ID Vozila mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
			return;
		}

		Vozilo vozilo = voziloMenadzer.pronadjiVoziloPoId(idVozila);
		if (vozilo == null) {
			JOptionPane.showMessageDialog(this, "Vozilo sa unetim ID-jem ne postoji u sistemu.", "Vozilo nije pronađeno", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 3. Parsiranje datuma (Očekivani format YYYY-MM-DD, npr: 2026-07-01)
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate trazeniOd;
		LocalDate trazeniDo;

		try {
			trazeniOd = LocalDate.parse(odTekst, formatter);
			trazeniDo = LocalDate.parse(doTekst, formatter);
		} catch (DateTimeParseException ex) {
			JOptionPane.showMessageDialog(this, "Datumi moraju biti u formatu GGGG-MM-DD (npr. 2026-06-15)!", "Greška u formatu", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Dodatna logička provera za datume
		if (trazeniDo.isBefore(trazeniOd)) {
			JOptionPane.showMessageDialog(this, "Datum završetka ne može biti pre datuma početka!", "Greška u datumima", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 4. Poziv tvoje metode iz IzdavanjeMenadzer
		boolean slobodno = izdavanjeMenadzer.isVoziloSlobodnoUPeriodu(vozilo, trazeniOd, trazeniDo);

		// 5. Prikazivanje krajnjeg ishoda korisniku
		if (slobodno) {
			JOptionPane.showMessageDialog(
				this, 
				"Vozilo " + vozilo.getModelVozila().getProizvodjac() + " " + vozilo.getModelVozila().getNaziv() + 
				" (Reg: " + vozilo.getRegistracijaVozila() + ") je SLOBODNO u izabranom periodu!", 
				"Termin je slobodan", 
				JOptionPane.INFORMATION_MESSAGE
			);
		} else {
			JOptionPane.showMessageDialog(
				this, 
				"Nažalost, vozilo je već ZAUZETO u tom periodu ili se termini preklapaju.", 
				"Termin zauzet", 
				JOptionPane.WARNING_MESSAGE
			);
		}
	}
}