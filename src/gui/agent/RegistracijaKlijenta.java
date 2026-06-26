package gui.agent;

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

import enums.KategorijaKlijenta;
import enums.Pol;
import menadzer.OsobaMenadzer;

public class RegistracijaKlijenta extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtIme, txtPrezime, txtDatumRodj, txtTelefon, txtEmail, txtDatumVozacke, txtKasnjenja;
	private JPasswordField txtLozinka;
	private JComboBox<Pol> cbPol;
	private JComboBox<KategorijaKlijenta> cbKategorija;

	public RegistracijaKlijenta(OsobaMenadzer osobaMenadzer) {
		setTitle("Registracija novog Klijenta");
		setModal(true);
		setBounds(100, 100, 420, 500);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Unos podataka za novog Klijenta");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 300, 25);
		getContentPane().add(lblNaslov);

		int y = 50;
		txtIme = kreirajPolje(y+=30);
		txtPrezime = kreirajPolje(y+=30);
		
		cbPol = new JComboBox<>(Pol.values());
		cbPol.setBounds(220, y+=30, 160, 20);
		getContentPane().add(cbPol);
		
		txtDatumRodj = kreirajPolje(y+=30);
		txtTelefon = kreirajPolje(y+=30);
		txtEmail = kreirajPolje(y+=30);
		
		txtLozinka = new JPasswordField();
		txtLozinka.setBounds(220, y+=30, 160, 20);
		getContentPane().add(txtLozinka);
		
		cbKategorija = new JComboBox<>(KategorijaKlijenta.values());
		cbKategorija.setBounds(220, y+=30, 160, 20);
		getContentPane().add(cbKategorija);
		
		txtDatumVozacke = kreirajPolje(y+=30);
		txtKasnjenja = kreirajPolje(y+=30);

		String[] labele = {"Ime:", "Prezime:", "Pol:", "Datum rođenja (YYYY-MM-DD):", "Telefon:", "Email (Korisničko ime):", "Lozinka:", "Kategorija klijenta:", "Datum vozačke (YYYY-MM-DD):", "Broj kašnjenja:"};
		y = 50;
		for (String l : labele) {
			JLabel lbl = new JLabel(l);
			lbl.setBounds(20, y+=30, 190, 20);
			getContentPane().add(lbl);
		}

		JButton btnSacuvaj = new JButton("Sačuvaj");
		btnSacuvaj.setBackground(Color.GREEN);
		btnSacuvaj.setBounds(90, 420, 100, 30);
		getContentPane().add(btnSacuvaj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(210, 420, 100, 30);
		getContentPane().add(btnOtkazi);

		btnSacuvaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String ime = txtIme.getText().trim();
					String prezime = txtPrezime.getText().trim();
					Pol pol = (Pol) cbPol.getSelectedItem();
					LocalDate datumRodj = LocalDate.parse(txtDatumRodj.getText().trim());
					String telefon = txtTelefon.getText().trim();
					String email = txtEmail.getText().trim();
					String lozinka = new String(txtLozinka.getPassword());
					KategorijaKlijenta kategorija = (KategorijaKlijenta) cbKategorija.getSelectedItem();
					LocalDate datumVozacke = LocalDate.parse(txtDatumVozacke.getText().trim());
					int brojKasnjenja = Integer.parseInt(txtKasnjenja.getText().trim());

					if (ime.isEmpty() || prezime.isEmpty() || email.isEmpty() || lozinka.isEmpty()) {
						JOptionPane.showMessageDialog(RegistracijaKlijenta.this, "Sva obavezna polja moraju biti popunjena!", "Greška", JOptionPane.WARNING_MESSAGE);
						return;
					}

					if (osobaMenadzer.korisnickoImePostoji(email)) {
						JOptionPane.showMessageDialog(RegistracijaKlijenta.this, "Klijent sa tim Email-om već postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					osobaMenadzer.registrujNovogKlijenta(ime, prezime, pol, datumRodj, telefon, email, lozinka, kategorija, datumVozacke, brojKasnjenja);
					JOptionPane.showMessageDialog(RegistracijaKlijenta.this, "Klijent uspešno registrovan i upisan u CSV!");
					dispose();

				} catch (DateTimeParseException ex) {
					JOptionPane.showMessageDialog(RegistracijaKlijenta.this, "Datumi moraju biti u formatu YYYY-MM-DD!", "Greška", JOptionPane.ERROR_MESSAGE);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(RegistracijaKlijenta.this, "Broj kašnjenja mora biti numerička vrednost!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnOtkazi.addActionListener(e -> dispose());
		
	}

	private JTextField kreirajPolje(int y) {
		JTextField tf = new JTextField();
		tf.setBounds(220, y, 160, 20);
		getContentPane().add(tf);
		return tf;
	}
}