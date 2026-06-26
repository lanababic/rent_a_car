package gui.agent;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
import model.Klijent; // Prilagodi import ako je model.Klijent drugačije lociran
import enums.ZahtevPretplate; // Prilagodi import za enum

public class ZahteviZaPretplateProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JTextField txtKorisnickoIme;
	private JLabel lblPodaciKlijenta;
	
	private JButton btnOdobri;
	private JButton btnOdbi;
	private JButton btnOdbiSveStoKasne;

	private Klijent pronadjeniKlijent = null;

	public ZahteviZaPretplateProzor(OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		setTitle("Zahtevi za Pretplate");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov
		JLabel lblNaslov = new JLabel("Obrada Zahteva za Online Pretplate");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(20, 15, 350, 25);
		contentPane.add(lblNaslov);
		
		// --- MULTI-AKCIJA (UVEK DOSTUPNA) ---
		btnOdbiSveStoKasne = new JButton("Odbi Sve Klijente Koji Kasne (>5 kašnjenja)");
		btnOdbiSveStoKasne.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnOdbiSveStoKasne.setBounds(50, 60, 380, 40);
		btnOdbiSveStoKasne.setEnabled(true); 
		contentPane.add(btnOdbiSveStoKasne);
		
		// --- POJEDINAČNA PRETRAGA PREKO KORISNIČKOG IMENA ---
		JLabel lblKorIme = new JLabel("Korisničko ime (Email):");
		lblKorIme.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblKorIme.setBounds(20, 130, 150, 25);
		contentPane.add(lblKorIme);
		
		txtKorisnickoIme = new JTextField();
		txtKorisnickoIme.setBounds(170, 130, 150, 25);
		contentPane.add(txtKorisnickoIme);
		
		JButton btnPretrazi = new JButton("Pretraži");
		btnPretrazi.setBounds(330, 130, 100, 25);
		contentPane.add(btnPretrazi);
		
		lblPodaciKlijenta = new JLabel("Klijent: Nije izabran");
		lblPodaciKlijenta.setFont(new Font("Tahoma", Font.ITALIC, 12));
		lblPodaciKlijenta.setBounds(20, 170, 440, 25);
		contentPane.add(lblPodaciKlijenta);
		
		// --- POJEDINAČNE AKCIJE ZA ODOBRAVANJE/ODBIJANJE ---
		btnOdobri = new JButton("Odobri Zahtev");
		btnOdobri.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnOdobri.setBounds(100, 210, 280, 40);
		btnOdobri.setEnabled(false); 
		contentPane.add(btnOdobri);
		
		btnOdbi = new JButton("Odbi Zahtev");
		btnOdbi.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnOdbi.setBounds(100, 265, 280, 40);
		btnOdbi.setEnabled(false); 
		contentPane.add(btnOdbi);
		
		// Dugme Nazad
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(365, 335, 100, 30);
		contentPane.add(btnNazad);
		
		// --- LOGIKA I AKCIJE NA DUGMIĆIMA ---
		
		// 1. Akcija: Odbi sve što kasne
		btnOdbiSveStoKasne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Poziv tvoje metode iz OsobaMenadzer-a
				osobaMenadzer.odbiSveStoKasne();
				JOptionPane.showMessageDialog(null, "Sistem je uspešno odbio zahteve svim klijentima sa više od 5 kašnjenja!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
				resetujProzor();
			}
		});
		
		// Akcija za pretragu klijenta
		btnPretrazi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String korIme = txtKorisnickoIme.getText().trim();
				
				if(korIme.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Molimo unesite korisničko ime!", "Upozorenje", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				// Pretpostavka da u osobaMenadzer imaš metodu za pronalaženje klijenta po korisničkom imenu / emailu
				// Ako ti se metoda zove npr. pronadjiKlijentaPoKorisnickomImenu, preimenuj je ovde:
				pronadjeniKlijent = (Klijent) osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu(korIme); 
				
				if (pronadjeniKlijent != null) {
					// Provera da li je zahtev uopšte poslat (ZahtevPretplate.POSLAT)
					if (pronadjeniKlijent.getZahtev() != ZahtevPretplate.POSLAT) {
						JOptionPane.showMessageDialog(null, "Za ovog klijenta nije poslat zahtev za pretplatu! (Trenutni status: " + pronadjeniKlijent.getZahtev() + ")", "Obaveštenje", JOptionPane.WARNING_MESSAGE);
						resetujProzor();
					} else {
						// Ako je zahtev poslat, ispisujemo osnovne podatke i otključavamo gumbe
						lblPodaciKlijenta.setText("Ime: " + pronadjeniKlijent.getIme() + " " + pronadjeniKlijent.getPrezime() + " | Kašnjenja: " + pronadjeniKlijent.getBrojKasnjenja());
						btnOdobri.setEnabled(true);
						btnOdbi.setEnabled(true);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Klijent sa tim korisničkim imenom ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					resetujProzor();
				}
			}
		});
		
		// 2. Akcija: Odobri zahtev
		btnOdobri.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Dvostruka sigurnosna provera za kašnjenja pre nego što pozovemo metodu, čisto zbog povratne poruke
				if (pronadjeniKlijent.getBrojKasnjenja() >= 6) {
					JOptionPane.showMessageDialog(null, "Zahtev ne može biti odobren jer klijent ima 6 ili više kašnjenja!", "Odbijeno automatski", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// Poziv tvoje metode iz OsobaMenadzer
				osobaMenadzer.odobriZahtev(pronadjeniKlijent);
				JOptionPane.showMessageDialog(null, "Zahtev za pretplatu je uspešno ODOBREN klijentu " + pronadjeniKlijent.getKorisnickoIme());
				
				resetujProzor();
				txtKorisnickoIme.setText("");
			}
		});
		
		// 3. Akcija: Odbi zahtev
		btnOdbi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Poziv tvoje metode iz OsobaMenadzer
				osobaMenadzer.odbiZahtev(pronadjeniKlijent);
				JOptionPane.showMessageDialog(null, "Zahtev za pretplatu je uspešno ODBIJEN klijentu " + pronadjeniKlijent.getKorisnickoIme());
				
				resetujProzor();
				txtKorisnickoIme.setText("");
			}
		});
		
		// Akcija za Nazad (Vraća na PretplateProzor)
		btnNazad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PretplateProzor pp = new PretplateProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				pp.setVisible(true);
				dispose();
			}
		});
		
		setLocationRelativeTo(null);
	}
	
	private void resetujProzor() {
		lblPodaciKlijenta.setText("Klijent: Nije izabran");
		btnOdobri.setEnabled(false);
		btnOdbi.setEnabled(false);
		pronadjeniKlijent = null;
	}
}