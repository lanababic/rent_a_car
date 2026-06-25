package gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import enums.ZahtevPretplate;
import menadzer.FinansijeMenadzer;
import menadzer.OsobaMenadzer;
import model.Klijent;

public class KreiranjePretplateForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtKorisnickoIme;

	public KreiranjePretplateForma(FinansijeMenadzer finansijeMenadzer, OsobaMenadzer osobaMenadzer) {
		setTitle("Kreiranje Nove Pretplate");
		setModal(true);
		setBounds(100, 100, 400, 200); 
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Kreiranje nove godišnje pretplate");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 350, 25);
		getContentPane().add(lblNaslov);

		JLabel lblKorisnickoIme = new JLabel("Korisničko ime klijenta:");
		lblKorisnickoIme.setBounds(20, 60, 150, 20);
		getContentPane().add(lblKorisnickoIme);

		txtKorisnickoIme = new JTextField();
		txtKorisnickoIme.setBounds(180, 60, 180, 20);
		getContentPane().add(txtKorisnickoIme);

		JButton btnKreiraj = new JButton("Kreiraj");
		btnKreiraj.setBackground(Color.GREEN);
		btnKreiraj.setBounds(60, 110, 110, 30); 
		getContentPane().add(btnKreiraj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(200, 110, 110, 30); 
		getContentPane().add(btnOtkazi);

		// --- AKCIJA ZA KREIRANJE ---
		btnKreiraj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String korIme = txtKorisnickoIme.getText().trim();

				if (korIme.isEmpty()) {
					JOptionPane.showMessageDialog(KreiranjePretplateForma.this, 
							"Morate uneti korisničko ime klijenta!", "Greška", JOptionPane.WARNING_MESSAGE);
					return;
				}

				// Pronalaženje klijenta preko OsobaMenadzer-a na osnovu korisničkog imena
				Klijent klijent = osobaMenadzer.getSviKlijenti().stream()
						.filter(k -> k.getKorisnickoIme().equalsIgnoreCase(korIme))
						.findFirst()
						.orElse(null);

				if (klijent == null) {
					JOptionPane.showMessageDialog(KreiranjePretplateForma.this, 
							"Klijent sa tim korisničkim imenom ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Provera uslova iz tvoje metode `napraviNovuPretplatu` pre pozivanja
				if (klijent.getBrojKasnjenja() <= 5 && klijent.getZahtev().equals(ZahtevPretplate.ODOBREN)) {
					
					// Poziv tvoje backend metode
					finansijeMenadzer.napraviNovuPretplatu(klijent, osobaMenadzer);
					
					JOptionPane.showMessageDialog(KreiranjePretplateForma.this, 
							"Pretplata je uspešno kreirana za klijenta: " + klijent.getIme() + " " + klijent.getPrezime());
					dispose();
				} else {
					// GUI ispis za tvoju else granu u slučaju neispunjenih uslova
					String razlog = "";
					if (klijent.getBrojKasnjenja() > 5) {
						razlog += "- Klijent ima više od 5 kašnjenja (" + klijent.getBrojKasnjenja() + ").\n";
					}
					if (!klijent.getZahtev().equals(ZahtevPretplate.ODOBREN)) {
						razlog += "- Zahtev za pretplatu nije postavljen na ODOBREN (trenutno: " + klijent.getZahtev() + ").";
					}

					JOptionPane.showMessageDialog(KreiranjePretplateForma.this, 
							"Nije moguće kreirati pretplatu!\n\nRazlog:\n" + razlog, 
							"Neuspešno kreiranje", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnOtkazi.addActionListener(e -> dispose());
	}
}