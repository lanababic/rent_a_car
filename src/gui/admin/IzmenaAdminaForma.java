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
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import enums.Pol;
import enums.StrucnaSprema;
import menadzer.OsobaMenadzer;
import model.Admin;

public class IzmenaAdminaForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtIme, txtPrezime, txtDatumRodj, txtTelefon, txtAdresa, txtKorIme, txtStaz, txtPlata; // Dodato txtPlata
	private JPasswordField txtLozinka;
	private JComboBox<Pol> cbPol;
	private JComboBox<StrucnaSprema> cbSprema;

	public IzmenaAdminaForma(OsobaMenadzer osobaMenadzer, Admin admin) {
		setTitle("Izmena Admina");
		setModal(true);
		setBounds(100, 100, 420, 530); 
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Izmena podataka za Administratora");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 300, 25);
		getContentPane().add(lblNaslov);

		int y = 50;
		txtIme = kreirajPolje(y+=30);
		txtPrezime = kreirajPolje(y+=30);
		
		cbPol = new JComboBox<>(Pol.values());
		cbPol.setBounds(200, y+=30, 160, 20);
		getContentPane().add(cbPol);
		
		txtDatumRodj = kreirajPolje(y+=30);
		txtTelefon = kreirajPolje(y+=30);
		txtAdresa = kreirajPolje(y+=30);
		txtKorIme = kreirajPolje(y+=30);
		
		txtLozinka = new JPasswordField();
		txtLozinka.setBounds(200, y+=30, 160, 20);
		getContentPane().add(txtLozinka);
		
		cbSprema = new JComboBox<>(StrucnaSprema.values());
		cbSprema.setBounds(200, y+=30, 160, 20);
		getContentPane().add(cbSprema);
		
		txtStaz = kreirajPolje(y+=30);
		txtPlata = kreirajPolje(y+=30); 

		String[] labele = {"Ime:", "Prezime:", "Pol:", "Datum rođenja (YYYY-MM-DD):", "Telefon:", "Adresa:", "Korisničko ime:", "Lozinka:", "Stručna sprema:", "Staž (godine):", "Osnovna plata:"};
		y = 50;
		for (String l : labele) {
			JLabel lbl = new JLabel(l);
			lbl.setBounds(20, y+=30, 180, 20);
			getContentPane().add(lbl);
		}

		JButton btnSacuvaj = new JButton("Sačuvaj");
		btnSacuvaj.setBackground(Color.GREEN);
		btnSacuvaj.setBounds(80, 450, 100, 30); 
		getContentPane().add(btnSacuvaj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(200, 450, 100, 30); 
		getContentPane().add(btnOtkazi);
		
		txtIme.setText(admin.getIme());
		txtPrezime.setText(admin.getPrezime());
		cbPol.setSelectedItem(admin.getPol());
		txtDatumRodj.setText(admin.getDatumRodj().toString());
		txtTelefon.setText(admin.getTelefon());
		txtAdresa.setText(admin.getEmail()); 
		txtKorIme.setText(admin.getKorisnickoIme());
		txtLozinka.setText(admin.getLozinka());
		cbSprema.setSelectedItem(admin.getSprema());
		txtStaz.setText(String.valueOf(admin.getStaz()));
		txtPlata.setText(String.valueOf(admin.getOsnovnaPlata())); 
		
		btnSacuvaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					String ime = txtIme.getText().trim();
					String prezime = txtPrezime.getText().trim();
					Pol pol = (Pol) cbPol.getSelectedItem();
					LocalDate datumRodj = LocalDate.parse(txtDatumRodj.getText().trim());
					String telefon = txtTelefon.getText().trim();
					String adresa = txtAdresa.getText().trim();
					String korIme = txtKorIme.getText().trim();
					String lozinka = new String(txtLozinka.getPassword());
					StrucnaSprema sprema = (StrucnaSprema) cbSprema.getSelectedItem();
					int staz = Integer.parseInt(txtStaz.getText().trim());
					double osnovnaPlata = Double.parseDouble(txtPlata.getText().trim()); 
					
					if (ime.isEmpty() || prezime.isEmpty() || korIme.isEmpty() || lozinka.isEmpty()) {
						JOptionPane.showMessageDialog(IzmenaAdminaForma.this, "Sva polja moraju biti popunjena!", "Greška", JOptionPane.WARNING_MESSAGE);
						return;
					}

					if (!korIme.equals(admin.getKorisnickoIme()) && osobaMenadzer.korisnickoImePostoji(korIme)) {
						JOptionPane.showMessageDialog(IzmenaAdminaForma.this, "Korisničko ime već postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					osobaMenadzer.izmeniAdmina(admin, ime, prezime, pol, datumRodj, telefon, adresa, korIme, lozinka, sprema, staz, osnovnaPlata);
					JOptionPane.showMessageDialog(IzmenaAdminaForma.this, "Podaci o administratoru su uspešno izmenjeni!");
					dispose();

				} catch (DateTimeParseException ex) {
					JOptionPane.showMessageDialog(IzmenaAdminaForma.this, "Format datuma mora biti YYYY-MM-DD!", "Greška", JOptionPane.ERROR_MESSAGE);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(IzmenaAdminaForma.this, "Staž i plata moraju biti brojevi!", "Greška", JOptionPane.ERROR_MESSAGE);
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