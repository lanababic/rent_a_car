package gui.agent;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import menadzer.FinansijeMenadzer;
import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import model.Vozilo;

public class PregledVozilaProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabelaVozila;
	private DefaultTableModel tableModel;
	
	private JTextField txtIdVozila;
	private JTextField txtDatumOd;
	private JTextField txtDatumDo;

	public PregledVozilaProzor(OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		setTitle("Pregled Vozila i Provera Dostupnosti");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 650, 520);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov
		JLabel lblNaslov = new JLabel("Evidencija i Provera Zauzetosti Vozila");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(20, 15, 350, 25);
		contentPane.add(lblNaslov);
		
		// 1. Dugme: Prikaz svih vozila
		JButton btnPrikaziSve = new JButton("Prikaži Sva Vozila");
		btnPrikaziSve.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnPrikaziSve.setBounds(20, 55, 160, 30);
		contentPane.add(btnPrikaziSve);
		
		// Tabela za prikaz vozila
		String[] kolone = {"ID Vozila", "Model", "Registracija", "Kilometraža", "Status"};
		tableModel = new DefaultTableModel(kolone, 0);
		tabelaVozila = new JTable(tableModel);
		
		JScrollPane scrollPane = new JScrollPane(tabelaVozila);
		scrollPane.setBounds(20, 100, 595, 200);
		contentPane.add(scrollPane);
		
		// --- PANEL ZA PROVERU DOSTUPNOSTI ---
		JLabel lblNaslovProvere = new JLabel("Provera dostupnosti za određeni period:");
		lblNaslovProvere.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNaslovProvere.setBounds(20, 320, 300, 20);
		contentPane.add(lblNaslovProvere);
		
		JLabel lblIdVozila = new JLabel("ID Vozila:");
		lblIdVozila.setBounds(20, 355, 80, 20);
		contentPane.add(lblIdVozila);
		
		txtIdVozila = new JTextField();
		txtIdVozila.setBounds(100, 355, 80, 20);
		contentPane.add(txtIdVozila);
		
		JLabel lblDatumOd = new JLabel("Datum Od (YYYY-MM-DD):");
		lblDatumOd.setBounds(200, 355, 150, 20);
		contentPane.add(lblDatumOd);
		
		txtDatumOd = new JTextField();
		txtDatumOd.setBounds(355, 355, 100, 20);
		contentPane.add(txtDatumOd);
		
		JLabel lblDatumDo = new JLabel("Datum Do (YYYY-MM-DD):");
		lblDatumDo.setBounds(200, 390, 150, 20);
		contentPane.add(lblDatumDo);
		
		txtDatumDo = new JTextField();
		txtDatumDo.setBounds(355, 390, 100, 20);
		contentPane.add(txtDatumDo);
		
		// 2. Dugme: Provera dostupnosti
		JButton btnProveriDostupnost = new JButton("Proveri Dostupnost");
		btnProveriDostupnost.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnProveriDostupnost.setBounds(20, 425, 160, 30);
		contentPane.add(btnProveriDostupnost);
		
		// Dugme Nazad
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(515, 435, 100, 30);
		contentPane.add(btnNazad);
		
		// --- AKCIJE ---
		
		// Akcija za prikaz svih vozila u tabeli
		btnPrikaziSve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableModel.setRowCount(0); // Očisti tabelu
				
				// Pretpostavka: voziloMenadzer ima metodu getSvaVozila() koja vraća listu/ArrayList svih vozila
				ArrayList<Vozilo> svaVozila = voziloMenadzer.getSvaVozila();
				
				if (svaVozila != null) {
					for (Vozilo v : svaVozila) {
						Object[] red = {
							v.getIdVozila(),
							v.getModelVozila(),
							v.getRegistracijaVozila(),
							v.getTrenutnaKilometraza(),
							v.getStatus()
						};
						tableModel.addRow(red);
					}
				}
			}
		});
		
		// Akcija za proveru slobodnih termina (Vezano za tvoju metodu)
		btnProveriDostupnost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int idVozila = Integer.parseInt(txtIdVozila.getText().trim());
					LocalDate trazeniOd = LocalDate.parse(txtDatumOd.getText().trim());
					LocalDate trazeniDo = LocalDate.parse(txtDatumDo.getText().trim());
					
					if (trazeniOd.isAfter(trazeniDo)) {
						JOptionPane.showMessageDialog(null, "Datum 'Od' ne može biti nakon datuma 'Do'!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					// Pronalaženje vozila preko ID-ja u menadžeru
					Vozilo izabranoVozilo = voziloMenadzer.pronadjiVoziloPoId(idVozila); // Prilagodi naziv metode ako je drugačiji
					
					if (izabranoVozilo == null) {
						JOptionPane.showMessageDialog(null, "Vozilo sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					// Poziv tvoje metode iz IzdavanjeMenadzer (Napomena: proveri da li je metoda javna (public) u menadžeru!)
					boolean slobodno = izdavanjeMenadzer.isVoziloSlobodnoUPeriodu(izabranoVozilo, trazeniOd, trazeniDo);
					
					if (slobodno) {
						JOptionPane.showMessageDialog(null, "Vozilo " + izabranoVozilo.getModelVozila() + " JE SLOBODNO u traženom periodu!", "Dostupno", JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null, "Vozilo " + izabranoVozilo.getModelVozila() + " je ZAUZETO u traženom periodu.", "Nije dostupno", JOptionPane.WARNING_MESSAGE);
					}
					
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "ID Vozila mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				} catch (DateTimeParseException ex) {
					JOptionPane.showMessageDialog(null, "Datumi moraju biti u formatu YYYY-MM-DD!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// Akcija za Nazad
		btnNazad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AgentProzor ap = new AgentProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				ap.setVisible(true);
				dispose();
			}
		});
		
		setLocationRelativeTo(null);
	}
}