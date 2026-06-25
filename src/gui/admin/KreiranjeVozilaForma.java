package gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import enums.StatusVozila;
import menadzer.VoziloMenadzer;
import model.ModelVozila;

public class KreiranjeVozilaForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtIdModela, txtRegistracija, txtKilometraza;
	private JComboBox<StatusVozila> cbStatus;

	public KreiranjeVozilaForma(VoziloMenadzer voziloMenadzer) {
		setTitle("Registracija Novog Vozila");
		setModal(true);
		setBounds(100, 100, 420, 320);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Unos podataka za novo vozilo");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 300, 25);
		getContentPane().add(lblNaslov);

		int y = 50;
		txtIdModela = kreirajPolje(y+=30);
		txtRegistracija = kreirajPolje(y+=30);
		txtKilometraza = kreirajPolje(y+=30);
		
		cbStatus = new JComboBox<>(StatusVozila.values());
		cbStatus.setBounds(200, y+=30, 160, 20);
		getContentPane().add(cbStatus);

		String[] labele = {"ID Modela Vozila:", "Registracija:", "Trenutna kilometraža:", "Status vozila:"};
		y = 50;
		for (String l : labele) {
			JLabel lbl = new JLabel(l);
			lbl.setBounds(20, y+=30, 180, 20);
			getContentPane().add(lbl);
		}

		JButton btnSacuvaj = new JButton("Sačuvaj");
		btnSacuvaj.setBackground(Color.GREEN);
		btnSacuvaj.setBounds(80, 220, 100, 30);
		getContentPane().add(btnSacuvaj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(200, 220, 100, 30);
		getContentPane().add(btnOtkazi);

		// --- AKCIJA SAČUVAJ ---
		btnSacuvaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String idModelaStr = txtIdModela.getText().trim();
					String registracija = txtRegistracija.getText().trim();
					String kilometrazaStr = txtKilometraza.getText().trim();
					StatusVozila status = (StatusVozila) cbStatus.getSelectedItem();

					if (idModelaStr.isEmpty() || registracija.isEmpty() || kilometrazaStr.isEmpty()) {
						JOptionPane.showMessageDialog(KreiranjeVozilaForma.this, 
								"Sva polja moraju biti popunjena!", "Greška", JOptionPane.WARNING_MESSAGE);
						return;
					}

					int idModela = Integer.parseInt(idModelaStr);
					int kilometraza = Integer.parseInt(kilometrazaStr);

					if (kilometraza < 0) {
						JOptionPane.showMessageDialog(KreiranjeVozilaForma.this, 
								"Kilometraža ne može biti negativna!", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Pronalaženje objekta ModelVozila preko unetog ID-ja
					// NAPOMENA: Prilagodi poziv ako ti se metoda/menadžer zove drugačije
					ModelVozila modelVozila = voziloMenadzer.pronadjiModelPoId(idModela);

					if (modelVozila == null) {
						JOptionPane.showMessageDialog(KreiranjeVozilaForma.this, 
								"Model vozila sa ID-jem " + idModela + " ne postoji u sistemu!", 
								"Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Pozivanje tvoje metode za dodavanje vozila
					voziloMenadzer.dodajNovoVozilo(modelVozila, registracija, kilometraza, status);
					
					JOptionPane.showMessageDialog(KreiranjeVozilaForma.this, "Vozilo je uspešno registrovano!");
					dispose();

				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(KreiranjeVozilaForma.this, 
							"ID modela i kilometraža moraju biti brojevi!", "Greška", JOptionPane.ERROR_MESSAGE);
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