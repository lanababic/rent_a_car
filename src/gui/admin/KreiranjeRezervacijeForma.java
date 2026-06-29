package gui.admin;

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

import menadzer.RezervacijeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.VoziloMenadzer;
import menadzer.FinansijeMenadzer;
import model.Klijent;
import model.ModelVozila;
import model.DodatnaUsluga;
import model.Rezervacija;

public class KreiranjeRezervacijeForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtModelVozilaId, txtKorisnickoIme, txtDatumOd, txtIdUsluge;
	
	// Lista u kojoj pamtimo privremeno dodate usluge pre nego što se kreira sama rezervacija
	private ArrayList<DodatnaUsluga> odabraneUsluge = new ArrayList<>();

	public KreiranjeRezervacijeForma(RezervacijeMenadzer rezervacijeMenadzer, OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer) {

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
		txtKorisnickoIme = kreirajPolje(y += 30);
		txtDatumOd = kreirajPolje(y += 30);

		String[] labele = {
			"ID Modela vozila:", 
			"Korisničko ime klijenta:", 
			"Datum početka (YYYY-MM-DD):"
		};
		
		y = 50;
		for (String l : labele) {
			JLabel lbl = new JLabel(l);
			lbl.setBounds(20, y += 30, 180, 20);
			getContentPane().add(lbl);
		}

		// --- NOVO: SEKCIJA ZA DODATNE USLUGE UNUTAR KREIRANJA ---
		JLabel lblIdUsluge = new JLabel("ID Dodatne Usluge:");
		lblIdUsluge.setBounds(20, 170, 180, 20);
		getContentPane().add(lblIdUsluge);

		txtIdUsluge = new JTextField();
		txtIdUsluge.setBounds(210, 170, 60, 20); // Kraće polje samo za unos ID-ja
		getContentPane().add(txtIdUsluge);

		JButton btnDodajUslugu = new JButton("Dodaj Uslugu");
		btnDodajUslugu.setBounds(280, 169, 120, 22);
		getContentPane().add(btnDodajUslugu);
		
		PrikazDodatnihUslugaTabela prikazUsluga = new PrikazDodatnihUslugaTabela(rezervacijeMenadzer);
		prikazUsluga.setVisible(true);


		btnDodajUslugu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					String unosIdUsluge = txtIdUsluge.getText().trim();
					if(unosIdUsluge.isEmpty()) {
						JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
								"Molimo unesite ID usluge iz otvorene tabele.", "Obaveštenje", JOptionPane.WARNING_MESSAGE);
						return;
					}
					
					int idUsluge = Integer.parseInt(unosIdUsluge);
					DodatnaUsluga du = rezervacijeMenadzer.pronadjiUsluguPoId(idUsluge);
					
					if (du != null) {
						odabraneUsluge.add(du);
						JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
								"Usluga '" + du.getNaziv() + "' privremeno dodata u ovu rezervaciju!\n" +
								"Ukupno dodato usluga: " + odabraneUsluge.size());
						txtIdUsluge.setText("");
					} else {
						JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
								"Usluga sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
					
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
							"ID Usluge mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		// --------------------------------------------------------

		// Pomaknuta dugmad niže zbog novih polja
		JButton btnKreiraj = new JButton("Kreiraj");
		btnKreiraj.setBackground(Color.GREEN);
		btnKreiraj.setBounds(80, 250, 100, 30); 
		getContentPane().add(btnKreiraj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(200, 250, 100, 30); 
		getContentPane().add(btnOtkazi);
		
		btnKreiraj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prikazUsluga.setVisible(true);
				try {
					String modelIdStr = txtModelVozilaId.getText().trim();
					String korIme = txtKorisnickoIme.getText().trim();
					String datumOdStr = txtDatumOd.getText().trim();

					if (modelIdStr.isEmpty() || korIme.isEmpty() || datumOdStr.isEmpty()) {
						JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
								"Sva polja moraju biti popunjena!", "Greška", JOptionPane.WARNING_MESSAGE);
						return;
					}

					int modelId = Integer.parseInt(modelIdStr);
					LocalDate datumOd = LocalDate.parse(datumOdStr);

					ModelVozila modelVozila = voziloMenadzer.pronadjiModelPoId(modelId);
					if (modelVozila == null) {
						JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
								"Model vozila sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					Klijent klijent = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu(korIme);
					if (klijent == null) {
						JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
								"Klijent sa tim korisničkim imenom ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					boolean vozackaStarijaOd2Godine = klijent.getDatumVozacke().isBefore(LocalDate.now().minusYears(2));
					boolean otkazaoPoslednjih24h = rezervacijeMenadzer.isOtkazaoUPoslenjih24h(klijent);

					if (vozackaStarijaOd2Godine && !otkazaoPoslednjih24h) {
						
						// 1. Poziv tvoje backend metode za kreiranje bazične rezervacije
						rezervacijeMenadzer.napraviRezervaciju(modelVozila, datumOd, klijent, finansijeMenadzer);
						
						// 2. Pronalaženje te tek kreirane rezervacije kako bismo joj vezali sakupljene dodatne usluge
						// (Pretpostavka je da se nova rezervacija nalazi na kraju liste ili je tražimo preko klijenta/datuma)
						ArrayList<Rezervacija> sveRez = rezervacijeMenadzer.getSveRezervacije();
						if (!sveRez.isEmpty()) {
							Rezervacija novaRezervacija = sveRez.get(sveRez.size() - 1); // Uzimamo poslednju dodatu
							
							// 3. Povezivanje svih izabranih usluga sa novom rezervacijom
							for (DodatnaUsluga du : odabraneUsluge) {
								novaRezervacija.dodajDodatnuUslugu(du);
							}
						}
						
						JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
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

						JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
								"Nije moguće napraviti rezervaciju!\n\nRazlog:\n" + razlog, 
								"Neuspešno kreiranje", JOptionPane.ERROR_MESSAGE);
					}

				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
							"ID modela vozila mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				} catch (DateTimeParseException ex) {
					JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
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