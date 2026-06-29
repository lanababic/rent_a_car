package gui.klijent;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import gui.admin.PrikazDodatnihUslugaTabela;
import menadzer.FinansijeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import model.Klijent;
import model.ModelVozila;
import model.Osoba;
import model.DodatnaUsluga;
import model.Rezervacija;

public class RezervacijaProzor extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtModelVozilaId, txtDatumOd, txtIdUsluge;
	
	// Lista u kojoj pamtimo privremeno dodate usluge pre nego što se kreira sama rezervacija
	private ArrayList<DodatnaUsluga> odabraneUsluge = new ArrayList<>();

	public RezervacijaProzor(RezervacijeMenadzer rezervacijeMenadzer, OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer) {
		
		setTitle("Kreiranje Nove Rezervacije");
		setModal(true);
		setBounds(100, 100, 440, 360); // Povećana visina prozora zbog novih komponenti za usluge
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Kreiranje nove rezervacije");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 350, 25);
		getContentPane().add(lblNaslov);

		int y = 50;
		txtModelVozilaId = kreirajPolje(y += 30);
		txtDatumOd = kreirajPolje(y += 30);

		String[] labele = {
			"ID Modela vozila:",  
			"Datum početka (YYYY-MM-DD):"
		};
		
		y = 50;
		for (String l : labele) {
			JLabel lbl = new JLabel(l);
			lbl.setBounds(20, y += 30, 180, 20);
			getContentPane().add(lbl);
		}

		// --- SEKCIJA ZA DODATNE USLUGE ---
		JLabel lblIdUsluge = new JLabel("ID Dodatne Usluge:");
		lblIdUsluge.setBounds(20, 170, 180, 20);
		getContentPane().add(lblIdUsluge);

		txtIdUsluge = new JTextField();
		txtIdUsluge.setBounds(210, 170, 60, 20);
		getContentPane().add(txtIdUsluge);

		JButton btnDodajUslugu = new JButton("Dodaj Uslugu");
		btnDodajUslugu.setBounds(280, 169, 120, 22);
		getContentPane().add(btnDodajUslugu);
		
		PrikazDodatnihUslugaTabela prikazUsluga = new PrikazDodatnihUslugaTabela(rezervacijeMenadzer);
		prikazUsluga.setLocation(20, 100);
		prikazUsluga.setVisible(true);
		
		btnDodajUslugu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					String unosIdUsluge = txtIdUsluge.getText().trim();
					if(unosIdUsluge.isEmpty()) {
						JOptionPane.showMessageDialog(RezervacijaProzor.this, 
								"Molimo unesite ID usluge iz otvorene tabele.", "Obaveštenje", JOptionPane.WARNING_MESSAGE);
						return;
					}
					
					int idUsluge = Integer.parseInt(unosIdUsluge);
					DodatnaUsluga du = rezervacijeMenadzer.pronadjiUsluguPoId(idUsluge);
					
					if (du != null) {
						odabraneUsluge.add(du);
						JOptionPane.showMessageDialog(RezervacijaProzor.this, 
								"Usluga '" + du.getNaziv() + "' privremeno dodata u ovu rezervaciju!\n" +
								"Ukupno dodato usluga: " + odabraneUsluge.size());
						txtIdUsluge.setText("");
					} else {
						JOptionPane.showMessageDialog(RezervacijaProzor.this, 
								"Usluga sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
					
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(RezervacijaProzor.this, 
							"ID Usluge mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		// ----------------------------------

		// Pomerena dugmad niže na y = 250
		JButton btnKreiraj = new JButton("Kreiraj");
		btnKreiraj.setBackground(Color.GREEN);
		btnKreiraj.setBounds(80, 250, 100, 30); 
		getContentPane().add(btnKreiraj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(200, 250, 100, 30); 
		getContentPane().add(btnOtkazi);

		// --- AKCIJA ZA DUGME KREIRAJ ---
		btnKreiraj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prikazUsluga.setVisible(false);
				try {
					String modelIdStr = txtModelVozilaId.getText().trim();
					String datumOdStr = txtDatumOd.getText().trim();

					if (modelIdStr.isEmpty() || datumOdStr.isEmpty()) {
						JOptionPane.showMessageDialog(RezervacijaProzor.this, 
								"Sva polja moraju biti popunjena!", "Greška", JOptionPane.WARNING_MESSAGE);
						return;
					}

					int modelId = Integer.parseInt(modelIdStr);
					LocalDate datumOd = LocalDate.parse(datumOdStr);

					ModelVozila modelVozila = voziloMenadzer.pronadjiModelPoId(modelId);
					if (modelVozila == null) {
						JOptionPane.showMessageDialog(RezervacijaProzor.this, 
								"Model vozila sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					Osoba trenUlg = osobaMenadzer.getTrenutnoUlogovan();
					Klijent trenutnoUlogovanKlijent = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu(trenUlg.getKorisnickoIme());

					if (trenutnoUlogovanKlijent == null) {
						JOptionPane.showMessageDialog(RezervacijaProzor.this, 
								"Niste ulogovani kao klijent!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					boolean vozackaStarijaOd2Godine = trenutnoUlogovanKlijent.getDatumVozacke().isBefore(LocalDate.now().minusYears(2));
					boolean otkazaoPoslednjih24h = rezervacijeMenadzer.isOtkazaoUPoslenjih24h(trenutnoUlogovanKlijent);

					if (vozackaStarijaOd2Godine && !otkazaoPoslednjih24h) {
						
						// 1. Kreiranje same rezervacije
						rezervacijeMenadzer.napraviRezervaciju(modelVozila, datumOd, trenutnoUlogovanKlijent, finansijeMenadzer);
						
						// 2. Vezivanje svih sakupljenih usluga za tu novu rezervaciju
						ArrayList<Rezervacija> sveRez = rezervacijeMenadzer.getSveRezervacije();
						if (!sveRez.isEmpty()) {
							Rezervacija novaRezervacija = sveRez.get(sveRez.size() - 1); // Uzimamo poslednju kreiranu
							
							for (DodatnaUsluga du : odabraneUsluge) {
								novaRezervacija.dodajDodatnuUslugu(du);
							}
						}
						
						JOptionPane.showMessageDialog(RezervacijaProzor.this, 
								"Rezervacija uspešno kreirana sa " + odabraneUsluge.size() + " dodatnih usluga!", 
								"Uspeh", JOptionPane.INFORMATION_MESSAGE);
						dispose();
					} else {
						String razlog = "";
						if (!vozackaStarijaOd2Godine) {
							razlog += "- Vozačka dozvola klijenta mora biti starija od 2 godine.\n";
						}
						if (otkazaoPoslednjih24h) {
							razlog += "- Klijent je otkazao rezervaciju u poslednjih 24h.\n";
						}

						JOptionPane.showMessageDialog(RezervacijaProzor.this, 
								"Nije moguće napraviti rezervaciju!\n\nRazlog:\n" + razlog, 
								"Neuspešno kreiranje", JOptionPane.ERROR_MESSAGE);
					}

				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(RezervacijaProzor.this, 
							"ID modela vozila mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				} catch (DateTimeParseException ex) {
					JOptionPane.showMessageDialog(RezervacijaProzor.this, 
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