package gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import enums.StatusRezervacije; // Pretpostavka za lokaciju tvog enuma
import menadzer.RezervacijeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.VoziloMenadzer;
import menadzer.FinansijeMenadzer;
import model.Rezervacija;
import model.Klijent;
import model.ModelVozila;

public class IzmenaRezervacijeForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtModelVozilaId, txtKorisnickoIme, txtDatumOd, txtDatumDo, txtDatumPravljenja;
	private JComboBox<StatusRezervacije> cbStatus;

	public IzmenaRezervacijeForma(RezervacijeMenadzer rezervacijeMenadzer, OsobaMenadzer osobaMenadzer, 
			VoziloMenadzer voziloMenadzer, FinansijeMenadzer finMen, Rezervacija rezervacija) {
		
		setTitle("Izmena Rezervacije");
		setModal(true);
		setBounds(100, 100, 440, 380); 
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Izmena rezervacije (ID: " + rezervacija.getIdRezervacije() + ")");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 350, 25);
		getContentPane().add(lblNaslov);

		int y = 50;
		txtModelVozilaId = kreirajPolje(y += 30);
		txtKorisnickoIme = kreirajPolje(y += 30);
		txtDatumOd = kreirajPolje(y += 30);
		txtDatumDo = kreirajPolje(y += 30);
		txtDatumPravljenja = kreirajPolje(y += 30);
		
		cbStatus = new JComboBox<>(StatusRezervacije.values());
		cbStatus.setBounds(210, y += 30, 160, 20);
		getContentPane().add(cbStatus);

		String[] labele = {
			"ID Modela vozila:", 
			"Korisničko ime klijenta:", 
			"Datum početka (YYYY-MM-DD):",
			"Datum kraja (YYYY-MM-DD):",
			"Datum pravljenja (YYYY-MM-DD):",
			"Status rezervacije:"
		};
		
		y = 50;
		for (String l : labele) {
			JLabel lbl = new JLabel(l);
			lbl.setBounds(20, y += 30, 180, 20);
			getContentPane().add(lbl);
		}

		JButton btnSacuvaj = new JButton("Sačuvaj");
		btnSacuvaj.setBackground(Color.GREEN);
		btnSacuvaj.setBounds(80, 290, 100, 30); 
		getContentPane().add(btnSacuvaj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(200, 290, 100, 30); 
		getContentPane().add(btnOtkazi);
		
		// Inicijalno popunjavanje polja trenutnim podacima iz objekta rezervacije
		if (rezervacija.getModelVozila() != null) {
			txtModelVozilaId.setText(String.valueOf(rezervacija.getModelVozila().getId()));
		}
		if (rezervacija.getKlijent() != null) {
			txtKorisnickoIme.setText(rezervacija.getKlijent().getKorisnickoIme());
		}
		txtDatumOd.setText(rezervacija.getDatumOd() != null ? rezervacija.getDatumOd().toString() : "");
		txtDatumDo.setText(rezervacija.getDatumDo() != null ? rezervacija.getDatumDo().toString() : "");
		txtDatumPravljenja.setText(rezervacija.getDatumPravljenja() != null ? rezervacija.getDatumPravljenja().toString() : "");
		cbStatus.setSelectedItem(rezervacija.getStatus());
		
		// --- AKCIJA ZA DUGME SAČUVAJ ---
		btnSacuvaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String modelIdStr = txtModelVozilaId.getText().trim();
					String korIme = txtKorisnickoIme.getText().trim();
					
					if (modelIdStr.isEmpty() || korIme.isEmpty()) {
						JOptionPane.showMessageDialog(IzmenaRezervacijeForma.this, 
								"Sva polja moraju biti popunjena!", "Greška", JOptionPane.WARNING_MESSAGE);
						return;
					}

					int modelId = Integer.parseInt(modelIdStr);
					LocalDate datumOd = LocalDate.parse(txtDatumOd.getText().trim());
					LocalDate datumDo = LocalDate.parse(txtDatumDo.getText().trim());
					LocalDate datumPravljenja = LocalDate.parse(txtDatumPravljenja.getText().trim());
					StatusRezervacije status = (StatusRezervacije) cbStatus.getSelectedItem();

					// 1. Pronalaženje modela pomoću tvoje metode
					ModelVozila modelVozila = voziloMenadzer.pronadjiModelPoId(modelId);
					if (modelVozila == null) {
						JOptionPane.showMessageDialog(IzmenaRezervacijeForma.this, 
								"Model vozila sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// 2. Pronalaženje klijenta pomoću tvoje metode
					Klijent klijent = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu(korIme);
					if (klijent == null) {
						JOptionPane.showMessageDialog(IzmenaRezervacijeForma.this, 
								"Klijent sa tim korisničkim imenom ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					if (datumDo.isBefore(datumOd)) {
						JOptionPane.showMessageDialog(IzmenaRezervacijeForma.this, 
								"Datum kraja ne može biti pre datuma početka!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// 3. Poziv tvoje backend metode `izmeniRezervaciju`
					// Lista dodatnih usluga se prosleđuje nepromenjena iz same rezervacije
					rezervacijeMenadzer.izmeniRezervaciju(
							rezervacija, 
							modelVozila, 
							datumOd, 
							datumDo, 
							datumPravljenja, 
							klijent, 
							status, 
							rezervacija.getListaDodatnihUsluga(), // Prilagodi naziv getera ako se razlikuje
							finMen
					);
					
					JOptionPane.showMessageDialog(IzmenaRezervacijeForma.this, 
							"Rezervacija uspešno izmenjena!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
					dispose();

				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(IzmenaRezervacijeForma.this, 
							"ID modela vozila mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				} catch (DateTimeParseException ex) {
					JOptionPane.showMessageDialog(IzmenaRezervacijeForma.this, 
							"Format datuma mora biti YYYY-MM-DD!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnOtkazi.addActionListener(e -> dispose());
	}

	private JTextField kreirajPolje(int y) {
		JTextField tf = new JTextField();
		tf.setBounds(210, y, 160, 20);
		getContentPane().add(tf);
		return tf;
	}
}