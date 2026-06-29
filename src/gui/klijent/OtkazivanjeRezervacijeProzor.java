package gui.klijent;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import enums.StatusRezervacije;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import model.Osoba;
import model.Rezervacija;

public class OtkazivanjeRezervacijeProzor extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtIdRezervacije;

	public OtkazivanjeRezervacijeProzor(RezervacijeMenadzer rezervacijeMenadzer, OsobaMenadzer osobaMenadzer) {
		
		setTitle("Otkazivanje Rezervacije");
		setModal(true);
		setBounds(100, 100, 380, 200);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Otkazivanje rezervacije klijenta");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 320, 25);
		getContentPane().add(lblNaslov);

		JLabel lblIdRezervacije = new JLabel("Unesite ID Rezervacije:");
		lblIdRezervacije.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblIdRezervacije.setBounds(20, 60, 140, 20);
		getContentPane().add(lblIdRezervacije);

		txtIdRezervacije = new JTextField();
		txtIdRezervacije.setBounds(170, 60, 160, 20);
		getContentPane().add(txtIdRezervacije);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(60, 110, 110, 30);
		getContentPane().add(btnOtkazi);

		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(190, 110, 110, 30);
		getContentPane().add(btnNazad);

		// --- AKCIJA ZA OTKAZIVANJE ---
		btnOtkazi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String idStr = txtIdRezervacije.getText().trim();
					if (idStr.isEmpty()) {
						JOptionPane.showMessageDialog(OtkazivanjeRezervacijeProzor.this, 
								"Molimo unesite ID rezervacije!", "Greška", JOptionPane.WARNING_MESSAGE);
						return;
					}

					int idRezervacije = Integer.parseInt(idStr);
					
					// Pronalaženje rezervacije preko tvog menadžera
					Rezervacija rezervacija = rezervacijeMenadzer.pronadjiRezervacijuPoId(idRezervacije);

					if (rezervacija == null) {
						JOptionPane.showMessageDialog(OtkazivanjeRezervacijeProzor.this, 
								"Rezervacija sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// SIGURNOSNA PROVERA: Da li rezervacija stvarno pripada trenutno ulogovanom klijentu?
					Osoba trenutnoUlogovan = osobaMenadzer.getTrenutnoUlogovan();
					if (trenutnoUlogovan == null || !rezervacija.getKlijent().getKorisnickoIme().equals(trenutnoUlogovan.getKorisnickoIme())) {
						JOptionPane.showMessageDialog(OtkazivanjeRezervacijeProzor.this, 
								"Nemate pravo da otkažete ovu rezervaciju jer nije Vaša!", "Zabranjen pristup", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Provera statusa pre poziva metode (da bismo dali lepši info ako je već otkazana ili završena)
					if (rezervacija.getStatus().equals(StatusRezervacije.OTKAZANO)) {
						JOptionPane.showMessageDialog(OtkazivanjeRezervacijeProzor.this, 
								"Ova rezervacija je već ranije otkazana!", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					if (!rezervacija.getStatus().equals(StatusRezervacije.NA_CEKANJU) && !rezervacija.getStatus().equals(StatusRezervacije.POTVRDJENO)) {
						JOptionPane.showMessageDialog(OtkazivanjeRezervacijeProzor.this, 
								"Nije moguće otkazati rezervaciju koja je u statusu: " + rezervacija.getStatus(), "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Poziv tvoje metode iz RezervacijeMenadzer (pod pretpostavkom da se nalazi u njemu, pošto koristi sacuvajRezervacije(this.putanja))
					rezervacijeMenadzer.otkaziRezervacijuKlijent(rezervacija, osobaMenadzer);

					JOptionPane.showMessageDialog(OtkazivanjeRezervacijeProzor.this, 
							"Rezervacija uspešno otkazana!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
					dispose();

				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(OtkazivanjeRezervacijeProzor.this, 
							"ID rezervacije mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnNazad.addActionListener(e -> dispose());
	}
}