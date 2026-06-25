package gui.admin;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import menadzer.FinansijeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import model.Rezervacija;

public class RezervacijaCrudProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private RezervacijeMenadzer rezervacijeMenadzer;
	private OsobaMenadzer osobaMenadzer;
	private VoziloMenadzer voziloMenadzer;

	public RezervacijaCrudProzor(RezervacijeMenadzer rezervacijeMenadzer, OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer) {
		this.rezervacijeMenadzer = rezervacijeMenadzer;
		this.osobaMenadzer = osobaMenadzer;
		this.voziloMenadzer = voziloMenadzer;
		
		setTitle("Upravljanje Rezervacijama");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Visina prozora je postavljena na 330 jer imamo standardna 4 dugmeta + Nazad
		setBounds(100, 100, 400, 380); 
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNaslov = new JLabel("Opcije za Rezervacije");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(10, 20, 364, 25);
		contentPane.add(lblNaslov);
		
		// 1. KREIRAJ
		JButton btnKreiraj = new JButton("Kreiraj novu rezervaciju");
		btnKreiraj.setBounds(60, 70, 260, 35);
		contentPane.add(btnKreiraj);
		
		// 2. IZMENI
		JButton btnIzmeni = new JButton("Izmeni rezervaciju");
		btnIzmeni.setBounds(60, 120, 260, 35);
		contentPane.add(btnIzmeni);
		
		// 3. OBRIŠI
		JButton btnObrisi = new JButton("Obriši rezervaciju");
		btnObrisi.setBounds(60, 170, 260, 35);
		contentPane.add(btnObrisi);
		
		// 4. PRIKAŽI SVE
		JButton btnPrikaziSve = new JButton("Prikaži sve rezervacije");
		btnPrikaziSve.setBounds(60, 220, 260, 35);
		contentPane.add(btnPrikaziSve);
		
		// NAZAD
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(140, 270, 100, 30);
		contentPane.add(btnNazad);
		
		// --- AKCIJE ---

		// 1. Akcija za kreiranje
		btnKreiraj.addActionListener(e -> {
			KreiranjeRezervacijeForma forma = new KreiranjeRezervacijeForma(rezervacijeMenadzer, osobaMenadzer, voziloMenadzer, finansijeMenadzer);
			forma.setVisible(true);
		});
		
		// 2. Akcija za izmenu (Traži se ID rezervacije)
		btnIzmeni.addActionListener(e -> {
			String idStr = JOptionPane.showInputDialog(RezervacijaCrudProzor.this, 
					"Unesite ID rezervacije koju menjate:", "Izmena rezervacije", JOptionPane.QUESTION_MESSAGE);
			
			if (idStr != null && !idStr.trim().isEmpty()) {
				try {
					int idRezervacije = Integer.parseInt(idStr.trim());
					
					// Pretpostavka je da geter vraća listu svih rezervacija
					Rezervacija rezervacijaZaIzmenu = rezervacijeMenadzer.getSveRezervacije().stream()
							.filter(r -> r.getIdRezervacije() == idRezervacije) // Prilagodi geter za ID ako se zove drugačije
							.findFirst()
							.orElse(null);
					
					if (rezervacijaZaIzmenu != null) {
						IzmenaRezervacijeForma forma = new IzmenaRezervacijeForma(rezervacijeMenadzer, osobaMenadzer, voziloMenadzer,finansijeMenadzer, rezervacijaZaIzmenu);
						forma.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(RezervacijaCrudProzor.this, 
								"Rezervacija sa unetim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(RezervacijaCrudProzor.this, "ID mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// 3. Akcija za brisanje
		btnObrisi.addActionListener(e -> {
			String idStr = JOptionPane.showInputDialog(RezervacijaCrudProzor.this, 
					"Unesite ID rezervacije za brisanje:", "Brisanje rezervacije", JOptionPane.WARNING_MESSAGE);
			
			if (idStr != null && !idStr.trim().isEmpty()) {
				try {
					int idRezervacije = Integer.parseInt(idStr.trim());
					
					boolean postoji = rezervacijeMenadzer.getSveRezervacije().stream()
							.anyMatch(r -> r.getIdRezervacije() == idRezervacije);
					
					if (postoji) {
						int p = JOptionPane.showConfirmDialog(RezervacijaCrudProzor.this, 
								"Da li ste sigurni da želite da obrišete rezervaciju ID: " + idRezervacije + "?", 
								"Potvrda brisanja", JOptionPane.YES_NO_OPTION);
						
						if (p == JOptionPane.YES_OPTION) {
							// Pretpostavka: metoda u menadžeru prima ID za brisanje
							rezervacijeMenadzer.obrisiRezervaciju(idRezervacije); 
							JOptionPane.showMessageDialog(RezervacijaCrudProzor.this, "Rezervacija uspešno obrisana.");
						}
					} else {
						JOptionPane.showMessageDialog(RezervacijaCrudProzor.this, "Rezervacija sa unetim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(RezervacijaCrudProzor.this, "ID mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// 4. Akcija za prikaz svih u tabeli
		btnPrikaziSve.addActionListener(e -> {
			PrikazRezervacijaTabela tabela = new PrikazRezervacijaTabela(rezervacijeMenadzer);
			tabela.setVisible(true);
		});
		
		// Nazad akcija
		btnNazad.addActionListener(e -> dispose());
		setLocationRelativeTo(null);
	}
}