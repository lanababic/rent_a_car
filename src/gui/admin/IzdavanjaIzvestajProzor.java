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
import menadzer.OsobaMenadzer;
// Uvezi svoje klase za Agenta i IzdavanjeVozila (promeni putanju paketa ako je drugačija)
import model.Agent; 
import model.IzdavanjeVozila;

public class IzdavanjaIzvestajProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabelaVozila;
	private DefaultTableModel tableModel;
	private JLabel lblUkupnoVozila;
	
	private JTextField txtDatumOd;
	private JTextField txtDatumDo;
	private JComboBox<Agent> cbAgenti;
	
	private OsobaMenadzer osobaMenadzer;
	private IzdavanjeMenadzer izdavanjeMenadzer;

	public IzdavanjaIzvestajProzor(OsobaMenadzer osobaMenadzer, IzdavanjeMenadzer izdavanjeMenadzer) {
		this.osobaMenadzer = osobaMenadzer;
		this.izdavanjeMenadzer = izdavanjeMenadzer;
		
		setTitle("Izveštaj o Izdavanjima po Agentu");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 650, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov
		JLabel lblNaslov = new JLabel("Pregled Izdavanja Vozila po Agentu i Periodu");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setBounds(10, 11, 614, 25);
		contentPane.add(lblNaslov);
		
		// --- FILTERI ---
		
		// Agent selekcija
		JLabel lblAgent = new JLabel("Izaberite agenta:");
		lblAgent.setBounds(30, 60, 110, 20);
		contentPane.add(lblAgent);
		
		cbAgenti = new JComboBox<>();
		// Pretpostavka: osobaMenadzer ima metodu getSviAgenti() koja vraća ArrayList<Agent> ili slično
		// Napuni combo box svim agentima iz sistema
		for (Agent a : osobaMenadzer.getSviAgenti()) {
			cbAgenti.addItem(a);
		}
		cbAgenti.setBounds(140, 60, 150, 22);
		contentPane.add(cbAgenti);
		
		// Datum Od
		JLabel lblDatumOd = new JLabel("Datum od (YYYY-MM-DD):");
		lblDatumOd.setBounds(30, 95, 150, 20);
		contentPane.add(lblDatumOd);
		
		txtDatumOd = new JTextField("2026-01-01"); // Primer podrazumevane vrednosti
		txtDatumOd.setBounds(180, 95, 110, 20);
		contentPane.add(txtDatumOd);
		txtDatumOd.setColumns(10);
		
		// Datum Do
		JLabel lblDatumDo = new JLabel("Datum do (YYYY-MM-DD):");
		lblDatumDo.setBounds(320, 95, 150, 20);
		contentPane.add(lblDatumDo);
		
		txtDatumDo = new JTextField("2026-12-31");
		txtDatumDo.setBounds(470, 95, 110, 20);
		contentPane.add(txtDatumDo);
		txtDatumDo.setColumns(10);
		
		// Dugme za prikaz tabele
		JButton btnPrikazi = new JButton("Prikaži vozila");
		btnPrikazi.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPrikazi.setBounds(230, 140, 180, 35);
		contentPane.add(btnPrikazi);
		
		// --- TABELA ---
		
		// Kolone prilagođene klasi IzdavanjeVozila
		String[] kolone = {"ID Izdavanja", "Vozilo", "Agent", "Datum Izdavanja"};
		tableModel = new DefaultTableModel(kolone, 0);
		tabelaVozila = new JTable(tableModel);
		
		JScrollPane scrollPane = new JScrollPane(tabelaVozila);
		scrollPane.setBounds(30, 190, 574, 200);
		contentPane.add(scrollPane);
		
		// Labela za ukupan broj vozila (dužina liste)
		lblUkupnoVozila = new JLabel("Ukupan broj vozila: 0");
		lblUkupnoVozila.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblUkupnoVozila.setBounds(30, 405, 250, 20);
		contentPane.add(lblUkupnoVozila);
		
		// Dugme za zatvoriti
		JButton btnZatvori = new JButton("Zatvori");
		btnZatvori.setBounds(504, 420, 100, 30);
		contentPane.add(btnZatvori);
		
		// --- AKCIJE ---
		
		btnPrikazi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generisiIzvestaj();
			}
		});
		
		btnZatvori.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setLocationRelativeTo(null);
	}
	
	private void generisiIzvestaj() {
		// Očistimo tabelu pre novog prikaza
		tableModel.setRowCount(0);
		
		Agent selektovaniAgent = (Agent) cbAgenti.getSelectedItem();
		if (selektovaniAgent == null) {
			JOptionPane.showMessageDialog(this, "Nema dostupnih agenata u sistemu.", "Greška", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			// Parsiranje unetih datuma iz tekstualnih polja u LocalDate
			LocalDate datumOd = LocalDate.parse(txtDatumOd.getText().trim());
			LocalDate datumDo = LocalDate.parse(txtDatumDo.getText().trim());
			
			// Poziv tvoje metode iz IzdavanjeMenadzer
			ArrayList<IzdavanjeVozila> listaIzvestaja = izdavanjeMenadzer.izvestajIzdavanjaOdAgenta(selektovaniAgent, datumOd, datumDo);
			
			// Punjenje tabele dobijenim podacima
			for (IzdavanjeVozila iz : listaIzvestaja) {
				Object[] red = {
					iz.getIdIzdaje(),                             
					iz.getVozilo().getIdVozila(),				//mozda bolje ceo toStrinh
					iz.getAgentIzdao().getKorisnickoIme(),       
					iz.getDatumPravljenjaIzdaje()           
				};
				tableModel.addRow(red);
			}
			
			// Postavljanje ukupnog broja vozila koristeći veličinu (.size()) liste
			int ukupanBroj = listaIzvestaja.size();
			lblUkupnoVozila.setText("Ukupan broj vozila: " + ukupanBroj);
			
		} catch (DateTimeParseException ex) {
			JOptionPane.showMessageDialog(this, "Datumi moraju biti u formatu GGGG-MM-DD (npr. 2026-05-15)!", "Greška u formatu datuma", JOptionPane.ERROR_MESSAGE);
		}
	}
}