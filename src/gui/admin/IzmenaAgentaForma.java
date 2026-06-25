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
import model.Agent; 

public class IzmenaAgentaForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtIme, txtPrezime, txtDatumRodj, txtTelefon, txtEmail, txtKorIme, txtStaz, txtPlata; 
	private JPasswordField txtLozinka;
	private JComboBox<Pol> cbPol;
	private JComboBox<StrucnaSprema> cbSprema;

	public IzmenaAgentaForma(OsobaMenadzer osobaMenadzer, Agent agent) {
		setTitle("Izmena Agenta");
		setModal(true);
		setBounds(100, 100, 420, 530); 
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Izmena podataka za Agenta");
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
		txtEmail = kreirajPolje(y+=30); // Kod agenta se koristi email umesto adrese
		txtKorIme = kreirajPolje(y+=30);
		
		txtLozinka = new JPasswordField();
		txtLozinka.setBounds(200, y+=30, 160, 20);
		getContentPane().add(txtLozinka);
		
		cbSprema = new JComboBox<>(StrucnaSprema.values());
		cbSprema.setBounds(200, y+=30, 160, 20);
		getContentPane().add(cbSprema);
		
		txtStaz = kreirajPolje(y+=30);
		txtPlata = kreirajPolje(y+=30); 

		// Prilagođena labela "Email:" umesto "Adresa:"
		String[] labele = {"Ime:", "Prezime:", "Pol:", "Datum rođenja (YYYY-MM-DD):", "Telefon:", "Email:", "Korisničko ime:", "Lozinka:", "Stručna sprema:", "Staž (godine):", "Osnovna plata:"};
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
		
		// Inicijalno popunjavanje polja podacima izabranog agenta
		txtIme.setText(agent.getIme());
		txtPrezime.setText(agent.getPrezime());
		cbPol.setSelectedItem(agent.getPol());
		txtDatumRodj.setText(agent.getDatumRodj().toString());
		txtTelefon.setText(agent.getTelefon());
		txtEmail.setText(agent.getEmail()); 
		txtKorIme.setText(agent.getKorisnickoIme());
		txtLozinka.setText(agent.getLozinka());
		cbSprema.setSelectedItem(agent.getSprema());
		txtStaz.setText(String.valueOf(agent.getStaz()));
		txtPlata.setText(String.valueOf(agent.getOsnovnaPlata())); 
		
		btnSacuvaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					String ime = txtIme.getText().trim();
					String prezime = txtPrezime.getText().trim();
					Pol pol = (Pol) cbPol.getSelectedItem();
					LocalDate datumRodj = LocalDate.parse(txtDatumRodj.getText().trim());
					String telefon = txtTelefon.getText().trim();
					String email = txtEmail.getText().trim();
					String korIme = txtKorIme.getText().trim();
					String lozinka = new String(txtLozinka.getPassword());
					StrucnaSprema sprema = (StrucnaSprema) cbSprema.getSelectedItem();
					int staz = Integer.parseInt(txtStaz.getText().trim());
					double osnovnaPlata = Double.parseDouble(txtPlata.getText().trim()); 
					
					if (ime.isEmpty() || prezime.isEmpty() || korIme.isEmpty() || lozinka.isEmpty() || email.isEmpty()) {
						JOptionPane.showMessageDialog(IzmenaAgentaForma.this, "Sva polja moraju biti popunjena!", "Greška", JOptionPane.WARNING_MESSAGE);
						return;
					}

					if (!korIme.equals(agent.getKorisnickoIme()) && osobaMenadzer.korisnickoImePostoji(korIme)) {
						JOptionPane.showMessageDialog(IzmenaAgentaForma.this, "Korisničko ime već postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Pozivanje tvoje metode izmeniAgenta prosleđene u zahtevu
					osobaMenadzer.izmeniAgenta(agent, ime, prezime, pol, datumRodj, telefon, email, korIme, lozinka, sprema, staz, osnovnaPlata);
					JOptionPane.showMessageDialog(IzmenaAgentaForma.this, "Podaci o agentu su uspešno izmenjeni!");
					dispose();

				} catch (DateTimeParseException ex) {
					JOptionPane.showMessageDialog(IzmenaAgentaForma.this, "Format datuma mora biti YYYY-MM-DD!", "Greška", JOptionPane.ERROR_MESSAGE);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(IzmenaAgentaForma.this, "Staž i plata moraju biti brojevi!", "Greška", JOptionPane.ERROR_MESSAGE);
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