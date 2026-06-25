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

import menadzer.OsobaMenadzer;
import model.Klijent; 

public class PrikazKlijentaTabela extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabela;
	private DefaultTableModel tableModel;

	public PrikazKlijentaTabela(OsobaMenadzer osobaMenadzer) {
		setTitle("Pregled svih klijenata");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 850, 400); // Malo širi prozor zbog specifičnih datuma
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 10)); 

		// Naslov na vrhu
		JLabel lblNaslov = new JLabel("Lista svih registrovanih klijenata");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNaslov, BorderLayout.NORTH);

		// Nazivi kolona prilagođeni specifičnim atributima Klijenta
		String[] kolone = {"Email / Kor. ime", "Ime", "Prezime", "Pol", "Datum rođenja", "Telefon", "Kategorija", "Datum vozačke", "Broj kašnjenja"};
		
		// Inicijalizacija modela tabele (onemogućeno direktno menjanje ćelija dvoklikom)
		tableModel = new DefaultTableModel(kolone, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; 
			}
		};

		tabela = new JTable(tableModel);
		
		// Prolazak kroz listu svih klijenata i punjenje redova
		for (Klijent kl : osobaMenadzer.getSviKlijenti()) {
			Object[] red = {
				kl.getEmail(), // Email igra ulogu i jedinstvenog korisničkog imena
				kl.getIme(),
				kl.getPrezime(),
				kl.getPol(),
				kl.getDatumRodj(),
				kl.getTelefon(),
				kl.getKategorija(),
				kl.getDatumVozacke(),
				kl.getBrojKasnjenja()
			};
			tableModel.addRow(red);
		}

		// JScrollPane za skrolovanje i zaglavlje tabele
		JScrollPane scrollPane = new JScrollPane(tabela);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		// Donji panel sa dugmetom Zatvori
		JPanel panelJug = new JPanel();
		JButton btnZatvori = new JButton("Zatvori");
		btnZatvori.addActionListener(e -> dispose());
		panelJug.add(btnZatvori);
		contentPane.add(panelJug, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
	}
}