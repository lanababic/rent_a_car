package gui.admin;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import enums.KategorijaVozila;
import menadzer.VoziloMenadzer;
import model.ModelVozila;

public class IzmenaModelaVozilaForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtNaziv, txtProizvodjac;
	private JComboBox<KategorijaVozila> cbKategorija;

	public IzmenaModelaVozilaForma(VoziloMenadzer voziloMenadzer, ModelVozila model) {
		setTitle("Izmena Modela Vozila");
		setModal(true);
		setBounds(100, 100, 400, 280);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Izmena modela (ID: " + model.getId() + ")");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 300, 25);
		getContentPane().add(lblNaslov);

		int y = 50;
		cbKategorija = new JComboBox<>(KategorijaVozila.values());
		cbKategorija.setBounds(180, y+=30, 160, 20);
		getContentPane().add(cbKategorija);

		txtNaziv = new JTextField();
		txtNaziv.setBounds(180, y+=30, 160, 20);
		getContentPane().add(txtNaziv);

		txtProizvodjac = new JTextField();
		txtProizvodjac.setBounds(180, y+=30, 160, 20);
		getContentPane().add(txtProizvodjac);

		String[] labele = {"Kategorija vozila:", "Naziv modela:", "Proizvođač:"};
		y = 50;
		for (String l : labele) {
			JLabel lbl = new JLabel(l);
			lbl.setBounds(20, y+=30, 150, 20);
			getContentPane().add(lbl);
		}

		JButton btnSacuvaj = new JButton("Sačuvaj");
		btnSacuvaj.setBackground(Color.GREEN);
		btnSacuvaj.setBounds(60, 180, 100, 30);
		getContentPane().add(btnSacuvaj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(200, 180, 100, 30);
		getContentPane().add(btnOtkazi);

		// --- POPUNJAVANJE PODACIMA ---
		cbKategorija.setSelectedItem(model.getKategorijaVozila());
		txtNaziv.setText(model.getNaziv());
		txtProizvodjac.setText(model.getProizvodjac());

		btnSacuvaj.addActionListener(e -> {
			String naziv = txtNaziv.getText().trim();
			String proizvodjac = txtProizvodjac.getText().trim();
			KategorijaVozila kat = (KategorijaVozila) cbKategorija.getSelectedItem();

			if (naziv.isEmpty() || proizvodjac.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Sva polja moraju biti popunjena!", "Greška", JOptionPane.WARNING_MESSAGE);
				return;
			}

			voziloMenadzer.izmeniModelVozila(model, kat, naziv, proizvodjac);
			JOptionPane.showMessageDialog(this, "Podaci o modelu su uspešno izmenjeni!");
			dispose();
		});

		btnOtkazi.addActionListener(e -> dispose());
	}
}