package gui.admin;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import menadzer.RezervacijeMenadzer;
import model.DodatnaUsluga;
import model.Rezervacija;

public class PrikazRezervacijaTabela extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabela;
	private DefaultTableModel tableModel;

	public PrikazRezervacijaTabela(RezervacijeMenadzer rezervacijeMenadzer) {
		setTitle("Pregled svih rezervacija");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 850, 450); // Malo proširen prozor zbog većeg broja kolona
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 10)); 

		// Naslov na vrhu
		JLabel lblNaslov = new JLabel("Lista svih rezervacija u sistemu");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNaslov, BorderLayout.NORTH);

		
		String[] kolone = {
			    "ID Rezervacije", "Model Vozila", "Klijent (Korisničko ime)", 
			    "Datum od", "Datum do", "Datum kreiranja", "Status", 
			    "Dodatne usluge", "Osnovna cena" 
			};
		
		// Inicijalizacija modela tabele (zabranjeno direktno menjanje ćelija dvoklikom)
		tableModel = new DefaultTableModel(kolone, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; 
			}
		};

		// Povezivanje modela sa tabelom
		tabela = new JTable(tableModel);
		
		// Prolazak kroz sve rezervacije i punjenje redova
		for (Rezervacija r : rezervacijeMenadzer.getSveRezervacije()) {
			
			// Bezbedno izvlačenje naziva modela vozila
			String modelPrikaz = "Nije definisan";
			if (r.getModelVozila() != null) {
				modelPrikaz = r.getModelVozila().getNaziv(); // Prilagodi geter ako se zove npr. getNazivModela()
			}

			// Bezbedno izvlačenje korisničkog imena klijenta
			String klijentPrikaz = "Nije definisan";
			if (r.getKlijent() != null) {
				klijentPrikaz = r.getKlijent().getKorisnickoIme();
			}
			
			String dodatneUslugePrikaz = "Nema";
			if (r.getListaDodatnihUsluga() != null && !r.getListaDodatnihUsluga().isEmpty()) {
			    dodatneUslugePrikaz = "";
			    for (DodatnaUsluga u : r.getListaDodatnihUsluga()) {
			        // Možeš staviti u.getNaziv() ako želiš tekstualni naziv umesto ID-ja
			        dodatneUslugePrikaz += u.getIdDodatneUsluge() + ", "; 
			    }
			    // Sređivanje zareza na kraju: ako String nije prazan, brišemo poslednja 2 karaktera (zarez i razmak)
			    if (dodatneUslugePrikaz.endsWith(", ")) {
			        dodatneUslugePrikaz = dodatneUslugePrikaz.substring(0, dodatneUslugePrikaz.length() - 2);
			    }
			}

			Object[] red = {
				r.getIdRezervacije(),
				modelPrikaz,
				klijentPrikaz,
				r.getDatumOd() != null ? r.getDatumOd().toString() : "Nema",
				r.getDatumDo() != null ? r.getDatumDo().toString() : "Nema",
				r.getDatumPravljenja() != null ? r.getDatumPravljenja().toString() : "Nema",
				r.getStatus(),
				dodatneUslugePrikaz,
				String.format("%.2f RSD", r.getOsnovnaCena())
			};
			tableModel.addRow(red);
		}

		// JScrollPane za skrolovanje tabele i prikaz zaglavlja
		JScrollPane scrollPane = new JScrollPane(tabela);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		// Donji panel za dugme Zatvori
		JPanel panelJug = new JPanel();
		JButton btnZatvori = new JButton("Zatvori");
		btnZatvori.addActionListener(e -> dispose());
		panelJug.add(btnZatvori);
		contentPane.add(panelJug, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
	}
}