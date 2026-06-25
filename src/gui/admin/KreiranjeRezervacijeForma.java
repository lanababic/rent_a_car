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

import menadzer.RezervacijeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.VoziloMenadzer;
import menadzer.FinansijeMenadzer;
import model.Klijent;
import model.ModelVozila;

public class KreiranjeRezervacijeForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtModelVozilaId, txtKorisnickoIme, txtDatumOd;

	public KreiranjeRezervacijeForma(RezervacijeMenadzer rezervacijeMenadzer, OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer) {
		// Pretpostavka je da FinansijeMenadzer možeš dobiti iz nekog glavnog prozora ili preko rezervacijeMenadzer-a.
		// Ako tvoj RezervacijeMenadzer već ima referencu na FinansijeMenadzer, prilagodi kod ispod.
		
		setTitle("Kreiranje Nove Rezervacije");
		setModal(true);
		setBounds(100, 100, 420, 280); 
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

		JButton btnKreiraj = new JButton("Kreiraj");
		btnKreiraj.setBackground(Color.GREEN);
		btnKreiraj.setBounds(80, 180, 100, 30); 
		getContentPane().add(btnKreiraj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(200, 180, 100, 30); 
		getContentPane().add(btnOtkazi);

		// --- AKCIJA ZA DUGME KREIRAJ ---
		btnKreiraj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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

					// 1. Korišćenje tvoje metode: pronadjiModelPoId
					ModelVozila modelVozila = voziloMenadzer.pronadjiModelPoId(modelId);

					if (modelVozila == null) {
						JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
								"Model vozila sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// 2. Korišćenje tvoje metode: pronadjiKlijentaPoKorisnickomImenu
					Klijent klijent = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu(korIme);

					if (klijent == null) {
						JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
								"Klijent sa tim korisničkim imenom ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// 3. Provera uslova iz tvoje metode `napraviRezervaciju`
					boolean vozackaStarijaOd2Godine = klijent.getDatumVozacke().isBefore(LocalDate.now().minusYears(2));
					boolean otkazaoPoslednjih24h = rezervacijeMenadzer.isOtkazaoUPoslenjih24h(klijent);

					if (vozackaStarijaOd2Godine && !otkazaoPoslednjih24h) {
						
						// Poziv tvoje backend metode
						rezervacijeMenadzer.napraviRezervaciju(modelVozila, datumOd, klijent, finansijeMenadzer);
						
						JOptionPane.showMessageDialog(KreiranjeRezervacijeForma.this, 
								"Rezervacija uspešno kreirana!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
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