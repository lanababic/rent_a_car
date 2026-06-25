package gui.admin;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import menadzer.RezervacijeMenadzer;

public class KreiranjeDodatneUslugeForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtNaziv;

	public KreiranjeDodatneUslugeForma(RezervacijeMenadzer rezervacijeMenadzer) {
		setTitle("Dodavanje Dodatne Usluge");
		setModal(true);
		setBounds(100, 100, 400, 180);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Unesite naziv nove dodatne usluge:");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNaslov.setBounds(20, 15, 300, 20);
		getContentPane().add(lblNaslov);

		txtNaziv = new JTextField();
		txtNaziv.setBounds(20, 45, 344, 25);
		getContentPane().add(txtNaziv);

		JButton btnSacuvaj = new JButton("Sačuvaj");
		btnSacuvaj.setBackground(Color.GREEN);
		btnSacuvaj.setBounds(60, 90, 100, 30);
		getContentPane().add(btnSacuvaj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(220, 90, 100, 30);
		getContentPane().add(btnOtkazi);

		btnSacuvaj.addActionListener(e -> {
			String naziv = txtNaziv.getText().trim();

			if (naziv.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Naziv ne može biti prazan!", "Greška", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Poziv tvoje metode dodajNovuDodatnuUslugu
			rezervacijeMenadzer.dodajNovuDodatnuUslugu(naziv);
			JOptionPane.showMessageDialog(this, "Usluga je uspešno sačuvana!");
			dispose();
		});

		btnOtkazi.addActionListener(e -> dispose());
	}
}