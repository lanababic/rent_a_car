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
import model.Agent; // Uvoz klase Agent

public class PrikazAgentaTabela extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabela;
	private DefaultTableModel tableModel;

	public PrikazAgentaTabela(OsobaMenadzer osobaMenadzer) {
		setTitle("Pregled svih agenata");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 400); 
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 10)); 

		// Naslov na vrhu
		JLabel lblNaslov = new JLabel("Lista svih registrovanih agenata");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNaslov, BorderLayout.NORTH);

		// Nazivi kolona u tabeli prilagođeni Agentu
		String[] kolone = {"Korisničko ime", "Ime", "Prezime", "Pol", "Datum rođenja", "Telefon", "Email", "Stručna sprema", "Staž", "Plata"};
		
		// Inicijalizacija modela tabele (zabranjeno direktno menjanje ćelija)
		tableModel = new DefaultTableModel(kolone, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; 
			}
		};

		// Povezivanje modela sa tabelom
		tabela = new JTable(tableModel);
		
		// Prođi kroz listu svih agenata iz menadžera i ubaci ih u tabelu
		// NAPOMENA: Prilagodi metodu getSviAgenti() ako se u OsobaMenadzer zove drugačije
		for (Agent ag : osobaMenadzer.getSviAgenti()) {
			Object[] red = {
				ag.getKorisnickoIme(),
				ag.getIme(),
				ag.getPrezime(),
				ag.getPol(),
				ag.getDatumRodj(),
				ag.getTelefon(),
				ag.getEmail(),
				ag.getSprema(),
				ag.getStaz(),
				ag.getOsnovnaPlata()
			};
			tableModel.addRow(red);
		}

		// JScrollPane omogućava skrolovanje i prikaz zaglavlja kolona
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