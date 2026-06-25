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

import menadzer.IzdavanjeMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import menadzer.OsobaMenadzer;
import model.IzdavanjeVozila; // Prilagođeno tvom nazivu klase iz metode
import model.Rezervacija;
import model.Vozilo;
import model.Agent;

public class IzmenaIzdavanjaForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtRezervacijaId, txtVoziloId, txtAgentIzdao, txtAgentPrimio, 
	                   txtKmPreuzimanje, txtKmVracanje, txtStvarniDatum, txtKazna;

	public IzmenaIzdavanjaForma(IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer, 
			VoziloMenadzer voziloMenadzer, OsobaMenadzer osobaMenadzer, IzdavanjeVozila izdaja) {
		
		setTitle("Izmena Izdavanja Vozila");
		setModal(true);
		setBounds(100, 100, 460, 430); 
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Izmena izdavanja (ID: " + izdaja.getIdIzdaje() + ")");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 350, 25);
		getContentPane().add(lblNaslov);

		int y = 50;
		txtRezervacijaId = kreirajPolje(y += 30);
		txtVoziloId = kreirajPolje(y += 30);
		txtAgentIzdao = kreirajPolje(y += 30);
		txtAgentPrimio = kreirajPolje(y += 30);
		txtKmPreuzimanje = kreirajPolje(y += 30);
		txtKmVracanje = kreirajPolje(y += 30);
		txtStvarniDatum = kreirajPolje(y += 30);
		txtKazna = kreirajPolje(y += 30);

		String[] labele = {
			"ID Rezervacije:", 
			"ID Vozila:", 
			"Agent izdao (Korisničko ime):",
			"Agent primio (Korisničko ime):",
			"KM pri preuzimanju:",
			"KM pri vraćanju:",
			"Stvarni datum vraćanja (YYYY-MM-DD):",
			"Naplaćena kazna:"
		};
		
		y = 50;
		for (String l : labele) {
			JLabel lbl = new JLabel(l);
			lbl.setBounds(20, y += 30, 220, 20);
			getContentPane().add(lbl);
		}

		JButton btnSacuvaj = new JButton("Sačuvaj");
		btnSacuvaj.setBackground(Color.GREEN);
		btnSacuvaj.setBounds(90, 340, 100, 30); 
		getContentPane().add(btnSacuvaj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(230, 340, 100, 30); 
		getContentPane().add(btnOtkazi);
		
		// Inicijalno popunjavanje polja trenutnim podacima iz prosleđenog objekta
		if (izdaja.getRezervacija() != null) {
			txtRezervacijaId.setText(String.valueOf(izdaja.getRezervacija().getIdRezervacije()));
		}
		if (izdaja.getVozilo() != null) {
			txtVoziloId.setText(String.valueOf(izdaja.getVozilo().getIdVozila()));
		}
		if (izdaja.getAgentIzdao() != null) {
			txtAgentIzdao.setText(izdaja.getAgentIzdao().getKorisnickoIme());
		}
		if (izdaja.getAgentPrimio() != null) {
			txtAgentPrimio.setText(izdaja.getAgentPrimio().getKorisnickoIme());
		}
		txtKmPreuzimanje.setText(String.valueOf(izdaja.getKilometrazaPriPreuzimanju()));
		txtKmVracanje.setText(String.valueOf(izdaja.getKilometrazaPriVracanju()));
		txtStvarniDatum.setText(izdaja.getStvarniDatumVracanja() != null ? izdaja.getStvarniDatumVracanja().toString() : "");
		txtKazna.setText(String.valueOf(izdaja.getNaplacenaKazna()));
		
		// --- AKCIJA ZA DUGME SAČUVAJ ---
		btnSacuvaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// 1. Parsiranje osnovnih tekstualnih unosa
					int rezId = Integer.parseInt(txtRezervacijaId.getText().trim());
					int vozId = Integer.parseInt(txtVoziloId.getText().trim());
					String korImeIzdao = txtAgentIzdao.getText().trim();
					String korImePrimio = txtAgentPrimio.getText().trim();
					
					int kmPreuzimanje = Integer.parseInt(txtKmPreuzimanje.getText().trim());
					int kmVracanje = Integer.parseInt(txtKmVracanje.getText().trim());
					double kazna = Double.parseDouble(txtKazna.getText().trim());
					
					LocalDate stvarniDatum = txtStvarniDatum.getText().trim().isEmpty() ? null : LocalDate.parse(txtStvarniDatum.getText().trim());

					// 2. Pronalaženje entiteta preko tvojih menadžerskih metoda
					Rezervacija rezervacija = rezervacijeMenadzer.pronadjiRezervacijuPoId(rezId);
					if (rezervacija == null) {
						JOptionPane.showMessageDialog(IzmenaIzdavanjaForma.this, "Rezervacija sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					Vozilo vozilo = voziloMenadzer.pronadjiVoziloPoId(vozId);
					if (vozilo == null) {
						JOptionPane.showMessageDialog(IzmenaIzdavanjaForma.this, "Vozilo sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					Agent agentIzdao = osobaMenadzer.pronadjiAgentaPoKorisnickomImenu(korImeIzdao);
					if (!korImeIzdao.isEmpty() && agentIzdao == null) {
						JOptionPane.showMessageDialog(IzmenaIzdavanjaForma.this, "Agent koji je izdao vozilo ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					Agent agentPrimio = osobaMenadzer.pronadjiAgentaPoKorisnickomImenu(korImePrimio);
					if (!korImePrimio.isEmpty() && agentPrimio == null) {
						JOptionPane.showMessageDialog(IzmenaIzdavanjaForma.this, "Agent koji je primio vozilo ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Validacija uslova pre slanja u menadžer (Čisto preventivni GUI ispis)
					if (kmVracanje < kmPreuzimanje) {
						JOptionPane.showMessageDialog(IzmenaIzdavanjaForma.this, 
								"Kilometraža pri vraćanju mora biti veća od kilometraže pri preuzimanju!", "Greška", JOptionPane.WARNING_MESSAGE);
						return;
					}

					// 3. Poziv tvoje backend metode `izmeniIzdaju`
					izdavanjeMenadzer.izmeniIzdaju(
							izdaja, 
							rezervacija, 
							vozilo, 
							agentIzdao, 
							agentPrimio, 
							kmPreuzimanje, 
							kmVracanje, 
							stvarniDatum, 
							kazna
					);
					
					JOptionPane.showMessageDialog(IzmenaIzdavanjaForma.this, "Izdavanje uspešno izmenjeno!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
					dispose();

				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(IzmenaIzdavanjaForma.this, "ID-jevi, kilometraže i kazna moraju biti ispravno uneti brojevi!", "Greška", JOptionPane.ERROR_MESSAGE);
				} catch (DateTimeParseException ex) {
					JOptionPane.showMessageDialog(IzmenaIzdavanjaForma.this, "Format datuma mora biti YYYY-MM-DD!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnOtkazi.addActionListener(e -> dispose());
	}

	private JTextField kreirajPolje(int y) {
		JTextField tf = new JTextField();
		tf.setBounds(250, y, 160, 20); // Malo pomereno udesno zbog dužih labela
		getContentPane().add(tf);
		return tf;
	}
}