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

import menadzer.IzdavanjeMenadzer;
import model.IzdavanjeVozila; // Prilagodi tačan naziv klase ako je samo Izdavanje

public class PrikazIzdavanjaTabela extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabela;
	private DefaultTableModel tableModel;

	public PrikazIzdavanjaTabela(IzdavanjeMenadzer izdavanjeMenadzer) {
		setTitle("Pregled svih izdavanja vozila");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 950, 450); // Proširen prozor zbog detaljnih kolona o kilometraži i kaznama
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 10)); 

		// Naslov na vrhu
		JLabel lblNaslov = new JLabel("Lista svih izdavanja vozila u sistemu");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNaslov, BorderLayout.NORTH);

		// Kolone prilagođene tvojoj metodi izmene i zahtevima za ID-jeve i korisnička imena
		String[] kolone = {
			"ID Izdavanja", 
			"ID Rezervacije", 
			"ID Vozila", 
			"Agent izdao", 
			"Agent primio", 
			"KM preuzimanje", 
			"KM vraćanje", 
			"Datum vraćanja", 
			"Kazna", 
			"Ukupna cena"
		};
		
		// Inicijalizacija modela tabele (zabranjeno menjanje ćelija)
		tableModel = new DefaultTableModel(kolone, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; 
			}
		};

		// Povezivanje modela sa tabelom
		tabela = new JTable(tableModel);
		
		// Prolazak kroz sva izdavanja iz menedžera i punjenje tabele
		for (IzdavanjeVozila i : izdavanjeMenadzer.getSvaIzdavanja()) {
			
			// Izvlačenje ID-ja rezervacije
			String rezIdPrikaz = "Nema";
			if (i.getRezervacija() != null) {
				rezIdPrikaz = String.valueOf(i.getRezervacija().getIdRezervacije());
			}

			// Izvlačenje ID-ja vozila
			String vozIdPrikaz = "Nema";
			if (i.getVozilo() != null) {
				vozIdPrikaz = String.valueOf(i.getVozilo().getIdVozila());
			}

			// Izvlačenje korisničkog imena agenta koji je izdao vozilo
			String agentIzdaoPrikaz = "Nema";
			if (i.getAgentIzdao() != null) {
				agentIzdaoPrikaz = i.getAgentIzdao().getKorisnickoIme();
			}

			// Izvlačenje korisničkog imena agenta koji je primio vozilo
			String agentPrimioPrikaz = "Nije vraćeno";
			if (i.getAgentPrimio() != null) {
				agentPrimioPrikaz = i.getAgentPrimio().getKorisnickoIme();
			}

			// Formatiranje stvarnog datuma vraćanja
			String datumVracanjaPrikaz = (i.getStvarniDatumVracanja() != null) ? i.getStvarniDatumVracanja().toString() : "Nije vraćeno";

			Object[] red = {
				i.getIdIzdaje(), // Prilagodi geter za ID izdavanja ako se zove getId()
				rezIdPrikaz,
				vozIdPrikaz,
				agentIzdaoPrikaz,
				agentPrimioPrikaz,
				i.getKilometrazaPriPreuzimanju() + " km",
				i.getKilometrazaPriVracanju() > 0 ? i.getKilometrazaPriVracanju() + " km" : "/",
				datumVracanjaPrikaz,
				String.format("%.2f RSD", i.getNaplacenaKazna()),
				String.format("%.2f RSD", i.getUkupnaCena()) // Prikaz ukupne cene (osnovna + kazna)
			};
			tableModel.addRow(red);
		}

		// JScrollPane za skrolovanje tabele
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