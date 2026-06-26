package gui.agent;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import menadzer.FinansijeMenadzer;
import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import model.IzdavanjeVozila; // Prilagodi import u zavisnosti od paketa modela

public class PrijemVozilaProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JTextField txtIdIzdaje;
	private JTextField txtNovaKilometraza;
	private JButton btnZavrsiPrijem;
	
	// Čuvanje tekućeg izdavanja nakon provere
	private IzdavanjeVozila pronadjenoIzdavanje = null;

	public PrijemVozilaProzor(OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		setTitle("Prijem Vozila (Povrat)");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov
		JLabel lblNaslov = new JLabel("Prijem i Zatvaranje Izdavanja");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(20, 15, 300, 25);
		contentPane.add(lblNaslov);
		
		// --- KORAK 1: PRONALAŽENJE IZDAJE ---
		JLabel lblIdIzdaje = new JLabel("Unesite ID Izdavanja:");
		lblIdIzdaje.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblIdIzdaje.setBounds(20, 65, 140, 25);
		contentPane.add(lblIdIzdaje);
		
		txtIdIzdaje = new JTextField();
		txtIdIzdaje.setBounds(160, 65, 120, 25);
		contentPane.add(txtIdIzdaje);
		
		JButton btnProveri = new JButton("Proveri");
		btnProveri.setBounds(295, 65, 100, 25);
		contentPane.add(btnProveri);
		
		// --- KORAK 2: UNOS KILOMETRAŽE I ZATVARANJE ---
		JLabel lblNovaKilometraza = new JLabel("Nova Kilometraža:");
		lblNovaKilometraza.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNovaKilometraza.setBounds(20, 140, 140, 25);
		contentPane.add(lblNovaKilometraza);
		
		txtNovaKilometraza = new JTextField();
		txtNovaKilometraza.setBounds(160, 140, 120, 25);
		txtNovaKilometraza.setEnabled(false); // Zaključano dok provera ne prođe
		contentPane.add(txtNovaKilometraza);
		
		btnZavrsiPrijem = new JButton("Završi Prijem Vozila");
		btnZavrsiPrijem.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnZavrsiPrijem.setBounds(100, 200, 220, 40);
		btnZavrsiPrijem.setEnabled(false); // Zaključano dok provera ne prođe
		contentPane.add(btnZavrsiPrijem);
		
		// Dugme Nazad
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(315, 265, 100, 30);
		contentPane.add(btnNazad);
		
		// --- LOGIKA I AKCIJE ---
		
		// 1. Akcija za proveru ID izdavanja
		btnProveri.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int idIzdaje = Integer.parseInt(txtIdIzdaje.getText().trim());
					
					// Pretpostavka da imaš metodu pronadjiIzdavanjePoId u izdavanjeMenadzer
					pronadjenoIzdavanje = izdavanjeMenadzer.pronadjiIzdavanjePoId(idIzdaje);
					
					if (pronadjenoIzdavanje != null) {
						// Provera da li je vozilo već vraćeno (da li postoji agent koji ga je primio)
						if (pronadjenoIzdavanje.getAgentPrimio() != null) {
							JOptionPane.showMessageDialog(null, "Ovo vozilo je već vraćeno i izdavanje je zatvoreno!", "Obaveštenje", JOptionPane.WARNING_MESSAGE);
							resetujFormu();
						} else {
							JOptionPane.showMessageDialog(null, "Izdavanje pronađeno! Vozilo: " + pronadjenoIzdavanje.getVozilo().getModelVozila() + 
									"\nKilometraža pri preuzimanju: " + pronadjenoIzdavanje.getKilometrazaPriPreuzimanju());
							
							// Otključavanje polja za unos kilometraže i konačni prijem
							txtNovaKilometraza.setEnabled(true);
							btnZavrsiPrijem.setEnabled(true);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Izdavanje sa tim ID-jem ne postoji u sistemu!", "Greška", JOptionPane.ERROR_MESSAGE);
						resetujFormu();
					}
					
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "ID Izdavanja mora biti ceo broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// 2. Akcija za završavanje prijema vozila (Vraćanje)
		btnZavrsiPrijem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int novaKilometraza = Integer.parseInt(txtNovaKilometraza.getText().trim());
					int staraKilometraza = pronadjenoIzdavanje.getKilometrazaPriPreuzimanju();
					
					// Logička provera da li je nova kilometraža manja od stare
					if (novaKilometraza < staraKilometraza) {
						JOptionPane.showMessageDialog(null, "Nova kilometraža ne može biti manja od kilometraže pri preuzimanju (" + staraKilometraza + ")!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					// Poziv tvoje metode iz IzdavanjeMenadzer
					izdavanjeMenadzer.IzdavanjeVozilaVracanje(
						pronadjenoIzdavanje, 
						novaKilometraza, 
						voziloMenadzer, 
						osobaMenadzer, 
						finansijeMenadzer
					);
					
					// Prikaz rezultata (obračunate ukupne cene i kazne iz objekta nakon obrade u metodi)
					JOptionPane.showMessageDialog(null, 
						"Vozilo uspešno primljeno nazad na stanje!\n\n" +
						"Naplaćena kazna: " + pronadjenoIzdavanje.getNaplacenaKazna() + " RSD\n" +
						"Ukupna cena za plaćanje: " + pronadjenoIzdavanje.getUkupnaCena() + " RSD\n" +
						"Status vozila je promenjen u DOSTUPNO.", 
						"Uspešan prijem", JOptionPane.INFORMATION_MESSAGE);
					
					resetujFormu();
					txtIdIzdaje.setText("");
					
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Nova kilometraža mora biti ceo broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// Akcija za povratak
		btnNazad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IznajmljivanjeProzor ip = new IznajmljivanjeProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				ip.setVisible(true);
				dispose();
			}
		});
		
		setLocationRelativeTo(null);
	}
	
	// Resetovanje stanja forme
	private void resetujFormu() {
		txtNovaKilometraza.setText("");
		txtNovaKilometraza.setEnabled(false);
		btnZavrsiPrijem.setEnabled(false);
		pronadjenoIzdavanje = null;
	}
}