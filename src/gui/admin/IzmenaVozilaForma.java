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
import model.Vozilo;

public class IzmenaVozilaForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtIdModela, txtRegistracija, txtKilometraza;
	private JComboBox<StatusVozila> cbStatus;

	public IzmenaVozilaForma(VoziloMenadzer voziloMenadzer, Vozilo vozilo) {
		setTitle("Izmena Vozila");
		setModal(true);
		setBounds(100, 100, 420, 320);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Izmena podataka za vozilo (ID: " + vozilo.getIdVozila() + ")");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 350, 25);
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

		// --- POPUNJAVANJE TRENUTNIM PODACIMA ---
		if (vozilo.getModelVozila() != null) {
			txtIdModela.setText(String.valueOf(vozilo.getModelVozila().getId())); // Pretpostavka da je geter getIdModela()
		}
		txtRegistracija.setText(vozilo.getRegistracijaVozila());
		txtKilometraza.setText(String.valueOf(vozilo.getTrenutnaKilometraza()));
		cbStatus.setSelectedItem(vozilo.getStatus());

		// --- AKCIJA SAČUVAJ ---
		btnSacuvaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String idModelaStr = txtIdModela.getText().trim();
					String registracija = txtRegistracija.getText().trim();
					String kilometrazaStr = txtKilometraza.getText().trim();
					StatusVozila status = (StatusVozila) cbStatus.getSelectedItem();

					if (idModelaStr.isEmpty() || registracija.isEmpty() || kilometrazaStr.isEmpty()) {
						JOptionPane.showMessageDialog(IzmenaVozilaForma.this, 
								"Sva polja moraju biti popunjena!", "Greška", JOptionPane.WARNING_MESSAGE);
						return;
					}

					int idModela = Integer.parseInt(idModelaStr);
					int novaKilometraza = Integer.parseInt(kilometrazaStr);

					// Provera pravila iz tvoje metode: kilometraža mora biti veća od stare
					if (novaKilometraza <= vozilo.getTrenutnaKilometraza()) {
						JOptionPane.showMessageDialog(IzmenaVozilaForma.this, 
								"Nova kilometraža mora biti veća od trenutne (" + vozilo.getTrenutnaKilometraza() + " km)!", 
								"Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Pronalaženje objekta ModelVozila preko unetog ID-ja
					ModelVozila modelVozila = voziloMenadzer.pronadjiModelPoId(idModela);

					if (modelVozila == null) {
						JOptionPane.showMessageDialog(IzmenaVozilaForma.this, 
								"Model vozila sa ID-jem " + idModela + " ne postoji u sistemu!", 
								"Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Pozivanje tvoje metode za izmenu vozila
					voziloMenadzer.izmeniVozilo(vozilo, modelVozila, registracija, novaKilometraza, status);
					
					JOptionPane.showMessageDialog(IzmenaVozilaForma.this, "Podaci o vozilu su uspešno izmenjeni!");
					dispose();

				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(IzmenaVozilaForma.this, 
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