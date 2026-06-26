package gui.agent;

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

import menadzer.FinansijeMenadzer;
import model.Pretplata;

public class PregledPretplataTabela extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabela;
	private DefaultTableModel tableModel;

	public PregledPretplataTabela(FinansijeMenadzer finansijeMenadzer) {
		setTitle("Pregled svih pretplata");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 400); 
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 10)); 

		// Naslov na vrhu
		JLabel lblNaslov = new JLabel("Lista svih pretplata u sistemu");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNaslov, BorderLayout.NORTH);

		// Nazivi kolona u tabeli za Pretplatu
		String[] kolone = {"ID Pretplate", "Korisničko ime klijenta", "Datum početka", "Datum kraja", "Cena"};
		
		// Inicijalizacija modela tabele (onemogućeno direktno menjanje ćelija dvoklikom)
		tableModel = new DefaultTableModel(kolone, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; 
			}
		};

		// Povezivanje modela sa tabelom
		tabela = new JTable(tableModel);
		
		// Prolazak kroz listu svih pretplata iz menadžera i punjenje tabele
		for (Pretplata p : finansijeMenadzer.getSvePretplate()) {
			
			// Izvlačenje korisničkog imena klijenta (provera za svaki slučaj ako je null)
			String korisnickoImeKlijenta = "Nije definisan";
			if (p.getKlijent() != null) {
				korisnickoImeKlijenta = p.getKlijent().getKorisnickoIme();
			}

			// Formatiranje prikaza datuma kraja (ako može biti null)
			String datumKrajaPrikaz = (p.getDatumKraj() != null) ? p.getDatumKraj().toString() : "Nema";

			Object[] red = {
				p.getIdPretplate(),
				korisnickoImeKlijenta,
				p.getDatumPocetak() != null ? p.getDatumPocetak().toString() : "Nema",
				datumKrajaPrikaz,
				String.format("%.2f RSD", p.getCena()) // Formatirana cena sa valutom
			};
			tableModel.addRow(red);
		}

		// JScrollPane za skrolovanje i prikaz zaglavlja kolona
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