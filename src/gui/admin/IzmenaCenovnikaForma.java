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
import menadzer.RezervacijeMenadzer;
import model.Cenovnik;

public class IzmenaCenovnikaForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtDatumPocetka, txtDatumKraja, txtCenaPretplate, txtKazna, txtPopust, txtDaniNajma;

	public IzmenaCenovnikaForma(FinansijeMenadzer finansijeMenadzer, RezervacijeMenadzer rezervacijeMenadzer, Cenovnik cenovnik) {
		setTitle("Izmena Cenovnika");
		setModal(true);
		setBounds(100, 100, 420, 380); 
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Izmena podataka za Cenovnik (ID: " + cenovnik.getIdCenovnika() + ")");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 350, 25);
		getContentPane().add(lblNaslov);

		int y = 50;
		txtDatumPocetka = kreirajPolje(y += 30);
		txtDatumKraja = kreirajPolje(y += 30);
		txtCenaPretplate = kreirajPolje(y += 30);
		txtKazna = kreirajPolje(y += 30);
		txtPopust = kreirajPolje(y += 30);
		txtDaniNajma = kreirajPolje(y += 30);

		// Postavljanje labela sa leve strane
		String[] labele = {
			"Datum početka (YYYY-MM-DD):", 
			"Datum kraja (YYYY-MM-DD):", 
			"Godišnja pretplata:", 
			"Kazna za kašnjenje:", 
			"Popust za kategorije (%):", 
			"Dani najma:"
		};
		
		y = 50;
		for (String l : labele) {
			JLabel lbl = new JLabel(l);
			lbl.setBounds(20, y += 30, 180, 20);
			getContentPane().add(lbl);
		}

		JButton btnSacuvaj = new JButton("Sačuvaj");
		btnSacuvaj.setBackground(Color.GREEN);
		btnSacuvaj.setBounds(80, 280, 100, 30); 
		getContentPane().add(btnSacuvaj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(200, 280, 100, 30); 
		getContentPane().add(btnOtkazi);
		
		// Inicijalno popunjavanje polja postojećim podacima iz prosleđenog cenovnika
		txtDatumPocetka.setText(cenovnik.getDatumPocetka().toString());
		txtDatumKraja.setText(cenovnik.getDatumKraja().toString());
		txtCenaPretplate.setText(String.valueOf(cenovnik.getCenaGodisnjePretplate()));
		txtKazna.setText(String.valueOf(cenovnik.getKaznaZaKasnjenje()));
		txtPopust.setText(String.valueOf(cenovnik.getPopustZaKategorije()));
		txtDaniNajma.setText(String.valueOf(cenovnik.getDaniNajma()));
		
		// Akcija za dugme Sačuvaj
		btnSacuvaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Parsiranje i preuzimanje vrednosti iz polja
					LocalDate datumPocetka = LocalDate.parse(txtDatumPocetka.getText().trim());
					LocalDate datumKraja = LocalDate.parse(txtDatumKraja.getText().trim());
					double cenaPretplate = Double.parseDouble(txtCenaPretplate.getText().trim());
					double kazna = Double.parseDouble(txtKazna.getText().trim());
					double popust = Double.parseDouble(txtPopust.getText().trim());
					int daniNajma = Integer.parseInt(txtDaniNajma.getText().trim());
					
					// Validacija datuma (da krajnji datum ne bude pre početnog)
					if (datumKraja.isBefore(datumPocetka)) {
						JOptionPane.showMessageDialog(IzmenaCenovnikaForma.this, 
								"Datum kraja ne može biti pre datuma početka!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (!validirajPopust(popust)) {
						JOptionPane.showMessageDialog(IzmenaCenovnikaForma.this, 
								"Popust mora biti vrednost između 0 i 1 (npr. 0.15 za 15%)!", 
								"Greška", JOptionPane.WARNING_MESSAGE);
						return; 
					}

					// Pozivanje tvoje metode izmeniCenovnik iz FinansijeMenadzer-a.
					// Mape prosleđujemo direktno iz starog objekta jer ih ovde ne menjamo.
					finansijeMenadzer.izmeniCenovnik(
							cenovnik, 
							datumPocetka, 
							datumKraja, 
							cenaPretplate, 
							kazna, 
							popust, 
							daniNajma, 
							cenovnik.getCenaNajma(),        // ili cenovnik.getCenaNajmaFull() u zavisnosti od getera
							cenovnik.getCenaDodatneUsluge() // ili cenovnik.getCenaDodatneUslugeFull()
					);
					
					JOptionPane.showMessageDialog(IzmenaCenovnikaForma.this, "Cenovnik uspešno izmenjen!");
					dispose();

				} catch (DateTimeParseException ex) {
					JOptionPane.showMessageDialog(IzmenaCenovnikaForma.this, 
							"Format datuma mora biti YYYY-MM-DD!", "Greška", JOptionPane.ERROR_MESSAGE);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(IzmenaCenovnikaForma.this, 
							"Cene, kazne, popust i dani moraju biti numeričke vrednosti!", "Greška", JOptionPane.ERROR_MESSAGE);
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
	private boolean validirajPopust(double popust) {
		if (popust < 0 || popust > 1) {
			return false;
		}
		return true;
	}
}