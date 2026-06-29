package gui.klijent;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import model.DodatnaUsluga;
import model.Osoba;
import model.Rezervacija;

public class PregledSveRezervacijeKlijent extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabela;
	private DefaultTableModel tableModel;

	public PregledSveRezervacijeKlijent(RezervacijeMenadzer rezervacijeMenadzer, OsobaMenadzer osobaMenadzer) {
		setTitle("Moje Rezervacije");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 850, 450); 
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 10)); 

		// Naslov na vrhu
		JLabel lblNaslov = new JLabel("Pregled Vaših rezervacija");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNaslov, BorderLayout.NORTH);

		// Kolone prilagođene klijentu (uklonjena kolona "Klijent" jer su sve njegove)
		String[] kolone = {
			"ID Rezervacije", "Model Vozila", "Datum od", "Datum do", 
			"Datum kreiranja", "Status", "Dodatne usluge", "Osnovna cena" 
		};
		
		tableModel = new DefaultTableModel(kolone, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; 
			}
		};

		tabela = new JTable(tableModel);
		
		// 1. Dobijanje trenutno ulogovanog korisnika/klijenta
		Osoba trenutnoUlogovan = osobaMenadzer.getTrenutnoUlogovan();
		String ulogovanoKorIme = (trenutnoUlogovan != null) ? trenutnoUlogovan.getKorisnickoIme() : "";

		// Prolazak kroz sve rezervacije i filtriranje
		for (Rezervacija r : rezervacijeMenadzer.getSveRezervacije()) {
			
			// Provera: ako rezervacija nema klijenta ili se korisničko ime ne poklapa, preskoči je
			if (r.getKlijent() == null || !r.getKlijent().getKorisnickoIme().equals(ulogovanoKorIme)) {
				continue;
			}
			
			// Bezbedno izvlačenje naziva modela vozila
			String modelPrikaz = "Nije definisan";
			if (r.getModelVozila() != null) {
				modelPrikaz = r.getModelVozila().getNaziv(); 
			}
			
			// Izvlačenje naziva dodatnih usluga
			String dodatneUslugePrikaz = "Nema";
			if (r.getListaDodatnihUsluga() != null && !r.getListaDodatnihUsluga().isEmpty()) {
				dodatneUslugePrikaz = "";
				for (DodatnaUsluga u : r.getListaDodatnihUsluga()) {
					dodatneUslugePrikaz += u.getNaziv() + ", "; 
				}
				if (dodatneUslugePrikaz.endsWith(", ")) {
					dodatneUslugePrikaz = dodatneUslugePrikaz.substring(0, dodatneUslugePrikaz.length() - 2);
				}
			}

			// Formiranje reda bez kolone za klijenta
			Object[] red = {
				r.getIdRezervacije(),
				modelPrikaz,
				r.getDatumOd() != null ? r.getDatumOd().toString() : "Nema",
				r.getDatumDo() != null ? r.getDatumDo().toString() : "Nema",
				r.getDatumPravljenja() != null ? r.getDatumPravljenja().toString() : "Nema",
				r.getStatus(),
				dodatneUslugePrikaz,
				String.format("%.2f RSD", r.getOsnovnaCena())
			};
			tableModel.addRow(red);
		}

		JScrollPane scrollPane = new JScrollPane(tabela);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		JPanel panelJug = new JPanel();
		JButton btnZatvori = new JButton("Zatvori");
		btnZatvori.addActionListener(e -> dispose());
		panelJug.add(btnZatvori);
		contentPane.add(panelJug, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
	}
}