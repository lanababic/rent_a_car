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
import enums.KategorijaKlijenta; 
import menadzer.OsobaMenadzer;
import model.Klijent; 

public class IzmenaKlijentaForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtIme, txtPrezime, txtDatumRodj, txtTelefon, txtEmail, txtDatumVozacke, txtKasnjenja; 
	private JPasswordField txtLozinka;
	private JComboBox<Pol> cbPol;
	private JComboBox<KategorijaKlijenta> cbKategorija; // ComboBox za kategoriju klijenta

	public IzmenaKlijentaForma(OsobaMenadzer osobaMenadzer, Klijent klijent) {
		setTitle("Izmena Klijenta");
		setModal(true);
		setBounds(100, 100, 420, 450); // Prilagođena visina jer ima manje polja nego kod zaposlenih
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Izmena podataka za Klijenta");
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
		txtEmail = kreirajPolje(y+=30); // Služi i kao korisničko ime
		
		txtLozinka = new JPasswordField();
		txtLozinka.setBounds(200, y+=30, 160, 20);
		getContentPane().add(txtLozinka);
		
		cbKategorija = new JComboBox<>(KategorijaKlijenta.values());
		cbKategorija.setBounds(200, y+=30, 160, 20);
		getContentPane().add(cbKategorija);
		
		txtDatumVozacke = kreirajPolje(y+=30);
		txtKasnjenja = kreirajPolje(y+=30); 

		// Labele prilagođene atributima klijenta
		String[] labele = {"Ime:", "Prezime:", "Pol:", "Datum rođenja (YYYY-MM-DD):", "Telefon:", "Email (Korisničko ime):", "Lozinka:", "Kategorija klijenta:", "Datum vozačke (YYYY-MM-DD):", "Broj kašnjenja:"};
		y = 50;
		for (String l : labele) {
			JLabel lbl = new JLabel(l);
			lbl.setBounds(20, y+=30, 180, 20);
			getContentPane().add(lbl);
		}

		JButton btnSacuvaj = new JButton("Sačuvaj");
		btnSacuvaj.setBackground(Color.GREEN);
		btnSacuvaj.setBounds(80, 370, 100, 30); 
		getContentPane().add(btnSacuvaj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(200, 370, 100, 30); 
		getContentPane().add(btnOtkazi);
		
		// Inicijalno popunjavanje formi podacima klijenta
		txtIme.setText(klijent.getIme());
		txtPrezime.setText(klijent.getPrezime());
		cbPol.setSelectedItem(klijent.getPol());
		txtDatumRodj.setText(klijent.getDatumRodj().toString());
		txtTelefon.setText(klijent.getTelefon());
		txtEmail.setText(klijent.getEmail()); 
		txtLozinka.setText(klijent.getLozinka());
		cbKategorija.setSelectedItem(klijent.getKategorija());
		txtDatumVozacke.setText(klijent.getDatumVozacke().toString());
		txtKasnjenja.setText(String.valueOf(klijent.getBrojKasnjenja()));
		
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
						JOptionPane.showMessageDialog(IzmenaKlijentaForma.this, "Sva obavezna polja moraju biti popunjena!", "Greška", JOptionPane.WARNING_MESSAGE);
						return;
					}

					// Pošto je email ujedno i korisničko ime, ako ga promeni proveravamo bazu
					if (!email.equals(klijent.getEmail()) && osobaMenadzer.korisnickoImePostoji(email)) {
						JOptionPane.showMessageDialog(IzmenaKlijentaForma.this, "Email/Korisničko ime je već zauzeto!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Pozivanje tvoje metode izmeniKlijenta, pretplata i zahtev se prosleđuju neizmenjeni iz objekta klijenta
					osobaMenadzer.izmeniKlijenta(klijent, ime, prezime, pol, datumRodj, telefon, email, lozinka, kategorija, datumVozacke, brojKasnjenja, klijent.getPretplata(), klijent.getZahtev());
					
					JOptionPane.showMessageDialog(IzmenaKlijentaForma.this, "Podaci o klijentu su uspešno izmenjeni!");
					dispose();

				} catch (DateTimeParseException ex) {
					JOptionPane.showMessageDialog(IzmenaKlijentaForma.this, "Format datuma mora biti YYYY-MM-DD!", "Greška", JOptionPane.ERROR_MESSAGE);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(IzmenaKlijentaForma.this, "Broj kašnjenja mora biti ceo broj!", "Greška", JOptionPane.ERROR_MESSAGE);
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