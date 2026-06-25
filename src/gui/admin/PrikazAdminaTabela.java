package gui.admin;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;

import menadzer.OsobaMenadzer;
import model.Admin;

public class PrikazAdminaTabela extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabela;
	private DefaultTableModel tableModel;

	public PrikazAdminaTabela(OsobaMenadzer osobaMenadzer) {
		setTitle("Pregled svih administratora");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 400); // Širi prozor da bi stala sva polja
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 10)); // Koristimo BorderLayout za lakše pakovanje tabele

		// Naslov na vrhu
		JLabel lblNaslov = new JLabel("Lista svih registrovanih administratora");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNaslov, BorderLayout.NORTH);

		// Nazivi kolona u tabeli
		String[] kolone = {"Korisničko ime", "Ime", "Prezime", "Pol", "Datum rođenja", "Telefon", "Adresa", "Stručna sprema", "Staž", "Plata"};
		
		// Inicijalizacija modela tabele (onemogućavamo direktno menjanje ćelija dvoklikom)
		tableModel = new DefaultTableModel(kolone, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; 
			}
		};

		// Povezivanje modela sa tabelom
		tabela = new JTable(tableModel);
		
		// Ubacivanje podataka iz liste 'sviAdmini' u model tabele
		// NAPOMENA: Ako ti se geter zove drugačije (npr. getSviAdmini()), prilagodi ovaj poziv
		for (Admin ad : osobaMenadzer.getSviAdmini()) {
			Object[] red = {
				ad.getKorisnickoIme(),
				ad.getIme(),
				ad.getPrezime(),
				ad.getPol(),
				ad.getDatumRodj(),
				ad.getTelefon(),
				ad.getEmail(), // ili getEmail() u zavisnosti šta koristiš
				ad.getSprema(),
				ad.getStaz(),
				ad.getOsnovnaPlata()
			};
			tableModel.addRow(red);
		}

		// JScrollPane je obavezan da bi se videli nazivi kolona i omogućio scrollbar
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