package gui.admin;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import model.IzdavanjeVozila; // Prilagodi tačan naziv modela ako je npr. IzdavanjeVozila

public class IzdavanjeCrudProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private IzdavanjeMenadzer izdavanjeMenadzer;
	private OsobaMenadzer osobaMenadzer;
	private VoziloMenadzer voziloMenadzer;

	public IzdavanjeCrudProzor(IzdavanjeMenadzer izdavanjeMenadzer, OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		this.izdavanjeMenadzer = izdavanjeMenadzer;
		this.osobaMenadzer = osobaMenadzer;
		this.voziloMenadzer = voziloMenadzer;
		
		setTitle("Upravljanje Izdavanjima");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Smanjena visina prozora na 280 jer imamo 3 dugmeta + Nazad
		setBounds(100, 100, 400, 330); 
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNaslov = new JLabel("Opcije za Izdavanja");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(10, 20, 364, 25);
		contentPane.add(lblNaslov);
		
		// 1. IZMENI (Y koordinata pomerena na 70)
		JButton btnIzmeni = new JButton("Izmeni izdavanje");
		btnIzmeni.setBounds(60, 70, 260, 35);
		contentPane.add(btnIzmeni);
		
		// 2. OBRIŠI
		JButton btnObrisi = new JButton("Obriši izdavanje");
		btnObrisi.setBounds(60, 120, 260, 35);
		contentPane.add(btnObrisi);
		
		// 3. PRIKAŽI SVE
		JButton btnPrikaziSve = new JButton("Prikaži sva izdavanja");
		btnPrikaziSve.setBounds(60, 170, 260, 35);
		contentPane.add(btnPrikaziSve);
		
		// NAZAD
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(140, 220, 100, 30);
		contentPane.add(btnNazad);
		
		// --- AKCIJE ---
		
		// 1. Akcija za izmenu (Traži se ID izdavanja)
		btnIzmeni.addActionListener(e -> {
			String idStr = JOptionPane.showInputDialog(IzdavanjeCrudProzor.this, 
					"Unesite ID izdavanja koje menjate:", "Izmena izdavanja", JOptionPane.QUESTION_MESSAGE);
			
			if (idStr != null && !idStr.trim().isEmpty()) {
				try {
					int idIzdavanja = Integer.parseInt(idStr.trim());
					
					// Pretpostavka da geter vraća listu svih izdavanja iz menadžera
					IzdavanjeVozila izdavanjeZaIzmenu = izdavanjeMenadzer.getSvaIzdavanja().stream()
							.filter(i -> i.getIdIzdaje() == idIzdavanja) // Prilagodi geter za ID ako je drugačiji
							.findFirst()
							.orElse(null);
					
					if (izdavanjeZaIzmenu != null) {
						IzmenaIzdavanjaForma forma = new IzmenaIzdavanjaForma(izdavanjeMenadzer, rezervacijeMenadzer, voziloMenadzer, osobaMenadzer, izdavanjeZaIzmenu);
							forma.setVisible(true);					
							} 
					else {
						JOptionPane.showMessageDialog(IzdavanjeCrudProzor.this, 
								"Izdavanje sa unetim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(IzdavanjeCrudProzor.this, "ID mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// 2. Akcija za brisanje
		btnObrisi.addActionListener(e -> {
			String idStr = JOptionPane.showInputDialog(IzdavanjeCrudProzor.this, 
					"Unesite ID izdavanja za brisanje:", "Brisanje izdavanja", JOptionPane.WARNING_MESSAGE);
			
			if (idStr != null && !idStr.trim().isEmpty()) {
				try {
					int idIzdavanja = Integer.parseInt(idStr.trim());
					
					boolean postoji = izdavanjeMenadzer.getSvaIzdavanja().stream()
							.anyMatch(i -> i.getIdIzdaje() == idIzdavanja);
					
					if (postoji) {
						int p = JOptionPane.showConfirmDialog(IzdavanjeCrudProzor.this, 
								"Da li ste sigurni da želite da obrišete izdavanje ID: " + idIzdavanja + "?", 
								"Potvrda brisanja", JOptionPane.YES_NO_OPTION);
						
						if (p == JOptionPane.YES_OPTION) {
							// Pretpostavka: metoda u menadžeru uklanja izdavanje preko ID-ja
							izdavanjeMenadzer.obrisiIzdaju(idIzdavanja);
							JOptionPane.showMessageDialog(IzdavanjeCrudProzor.this, "Izdavanje uspešno obrisano.");
						}
					} else {
						JOptionPane.showMessageDialog(IzdavanjeCrudProzor.this, "Izdavanje sa unetim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(IzdavanjeCrudProzor.this, "ID mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// 3. Akcija za prikaz svih u tabeli
		btnPrikaziSve.addActionListener(e -> {
			PrikazIzdavanjaTabela tabela = new PrikazIzdavanjaTabela(izdavanjeMenadzer);
			tabela.setVisible(true);
		});
		
		// Nazad akcija
		btnNazad.addActionListener(e -> dispose());
		setLocationRelativeTo(null);
	}
}