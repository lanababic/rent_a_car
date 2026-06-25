package gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import menadzer.FinansijeMenadzer;
import menadzer.OsobaMenadzer;
import model.Pretplata;
import model.Klijent;

public class IzmenaPretplataForma extends JDialog {

	private static final long serialVersionUID = 1L;
	// Promenjeno iz txtKlijentId u txtKorisnickoIme
	private JTextField txtKorisnickoIme, txtDatumPocetka, txtDatumKraja, txtCena;

	public IzmenaPretplataForma(FinansijeMenadzer finansijeMenadzer, OsobaMenadzer osobaMenadzer, Pretplata pretplata) {
		setTitle("Izmena Pretplate");
		setModal(true);
		setBounds(100, 100, 420, 320); 
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Izmena podataka za Pretplatu (ID: " + pretplata.getIdPretplate() + ")");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 350, 25);
		getContentPane().add(lblNaslov);

		int y = 50;
		txtKorisnickoIme = kreirajPolje(y += 30);
		txtDatumPocetka = kreirajPolje(y += 30);
		txtDatumKraja = kreirajPolje(y += 30);
		txtCena = kreirajPolje(y += 30);

		// Prilagođena prva labela za korisničko ime
		String[] labele = {
			"Korisničko ime klijenta:", 
			"Datum početka (YYYY-MM-DD):", 
			"Datum kraja (YYYY-MM-DD):", 
			"Cena pretplate:"
		};
		
		y = 50;
		for (String l : labele) {
			JLabel lbl = new JLabel(l);
			lbl.setBounds(20, y += 30, 180, 20);
			getContentPane().add(lbl);
		}

		JButton btnSacuvaj = new JButton("Sačuvaj");
		btnSacuvaj.setBackground(Color.GREEN);
		btnSacuvaj.setBounds(80, 220, 100, 30); 
		getContentPane().add(btnSacuvaj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(200, 220, 100, 30); 
		getContentPane().add(btnOtkazi);
		
		// Inicijalno popunjavanje: uzima se korisničko ime klijenta iz pretplate
		txtKorisnickoIme.setText(pretplata.getKlijent().getKorisnickoIme()); 
		txtDatumPocetka.setText(pretplata.getDatumPocetak().toString());
		txtDatumKraja.setText(pretplata.getDatumKraj() != null ? pretplata.getDatumKraj().toString() : "");
		txtCena.setText(String.valueOf(pretplata.getCena()));
		
		btnSacuvaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String korIme = txtKorisnickoIme.getText().trim();
					LocalDate dp = LocalDate.parse(txtDatumPocetka.getText().trim());
					LocalDate dk = txtDatumKraja.getText().trim().isEmpty() ? null : LocalDate.parse(txtDatumKraja.getText().trim());
					double cena = Double.parseDouble(txtCena.getText().trim());
					
					if (korIme.isEmpty()) {
						JOptionPane.showMessageDialog(IzmenaPretplataForma.this, "Korisničko ime ne može biti prazno!", "Greška", JOptionPane.WARNING_MESSAGE);
						return;
					}

					// Pretraga klijenta preko korisničkog imena u OsobaMenadzer-u
					Klijent klijent = osobaMenadzer.getSviKlijenti().stream()
							.filter(k -> k.getKorisnickoIme().equalsIgnoreCase(korIme))
							.findFirst()
							.orElse(null);
					
					if (klijent == null) {
						JOptionPane.showMessageDialog(IzmenaPretplataForma.this, "Klijent sa tim korisničkim imenom ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if (dk != null && dk.isBefore(dp)) {
						JOptionPane.showMessageDialog(IzmenaPretplataForma.this, "Datum kraja ne može biti pre početka!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Poziv tvoje backend metode sa ispravno pronađenim klijentom
					finansijeMenadzer.izmeniPretplatu(pretplata, klijent, dp, dk, cena);
					
					JOptionPane.showMessageDialog(IzmenaPretplataForma.this, "Pretplata uspešno izmenjena!");
					dispose();

				} catch (DateTimeParseException ex) {
					JOptionPane.showMessageDialog(IzmenaPretplataForma.this, "Format datuma mora biti YYYY-MM-DD!", "Greška", JOptionPane.ERROR_MESSAGE);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(IzmenaPretplataForma.this, "Cena mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnOtkazi.addActionListener(e -> dispose());
	}

	private JTextField kreirajPolje(int y) {
		JTextField tf = new JTextField();
		tf.setBounds(200, y, 160, 20);
		getContentPane().add(tf);
		return tf;
	}
}