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

import menadzer.VoziloMenadzer;
import model.Vozilo;

public class PrikazVozilaTabela extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabela;
	private DefaultTableModel tableModel;

	public PrikazVozilaTabela(VoziloMenadzer voziloMenadzer) {
		setTitle("Pregled svih vozila");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 400); 
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 10)); 

		// Naslov na vrhu
		JLabel lblNaslov = new JLabel("Lista svih registrovanih vozila u sistemu");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNaslov, BorderLayout.NORTH);

		// Nazivi kolona u tabeli za Vozilo
		String[] kolone = {"ID Vozila", "Model (ID - Naziv)", "Registracija", "Trenutna kilometraža", "Status vozila"};
		
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
		
		// Prolazak kroz listu svih vozila iz menadžera i punjenje tabele
		for (Vozilo v : voziloMenadzer.getSvaVozila()) {
			
			// Formatiramo prikaz modela. Ako objekat modela postoji, spajamo njegov ID i naziv
			String modelPrikaz = "Nije dodeljen";
			if (v.getModelVozila() != null) {
				// Prilagodi getere (npr. getIdModela() ili getNazivModela() / getMarka()) u zavisnosti od tvoje klase ModelVozila
				modelPrikaz = v.getModelVozila().getId() + " - " + v.getModelVozila().getNaziv();
			}

			Object[] red = {
				v.getIdVozila(),
				modelPrikaz,
				v.getRegistracijaVozila(),
				v.getTrenutnaKilometraza() + " km",
				v.getStatus()
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