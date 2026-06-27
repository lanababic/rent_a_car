package gui.admin;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import menadzer.VoziloMenadzer;
import model.ModelVozila;

public class ModelVozilaCrudProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private VoziloMenadzer voziloMenadzer;

	public ModelVozilaCrudProzor(VoziloMenadzer voziloMenadzer) {
		this.voziloMenadzer = voziloMenadzer;
		
		setTitle("Upravljanje Modelima Vozila");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 380);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNaslov = new JLabel("Opcije za Modele Vozila");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(10, 20, 364, 25);
		contentPane.add(lblNaslov);
		
		JButton btnRegistruj = new JButton("Dodaj novi model");
		btnRegistruj.setBounds(60, 70, 260, 35);
		contentPane.add(btnRegistruj);
		
		JButton btnIzmeni = new JButton("Izmeni model");
		btnIzmeni.setBounds(60, 120, 260, 35);
		contentPane.add(btnIzmeni);
		
//		JButton btnObrisi = new JButton("Obriši model");
//		btnObrisi.setBounds(60, 170, 260, 35);
//		contentPane.add(btnObrisi);
		
		JButton btnPrikaziSve = new JButton("Prikaži sve modele");
		btnPrikaziSve.setBounds(60, 170, 260, 35);
		contentPane.add(btnPrikaziSve);
		
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(140, 290, 100, 30);
		contentPane.add(btnNazad);
		
		// --- AKCIJE ---

		btnRegistruj.addActionListener(e -> {
			KreiranjeModelaVozilaForma forma = new KreiranjeModelaVozilaForma(voziloMenadzer);
			forma.setVisible(true);
		});
		
		btnIzmeni.addActionListener(e -> {
			String idStr = JOptionPane.showInputDialog(ModelVozilaCrudProzor.this, 
					"Unesite ID modela koji menjate:", "Izmena modela", JOptionPane.QUESTION_MESSAGE);
			
			if (idStr != null && !idStr.trim().isEmpty()) {
				try {
					int idModela = Integer.parseInt(idStr.trim());
					ModelVozila modelZaIzmenu = null;
					for (ModelVozila m : voziloMenadzer.getSviModeliVozila()) {
						if (m.getId() == idModela) {
							modelZaIzmenu = m;
							break;
						}
					}
					
					if (modelZaIzmenu != null) {
						IzmenaModelaVozilaForma forma = new IzmenaModelaVozilaForma(voziloMenadzer, modelZaIzmenu);
						forma.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(ModelVozilaCrudProzor.this, 
								"Model sa unetim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(ModelVozilaCrudProzor.this, "ID mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
//		btnObrisi.addActionListener(e -> {
//			String idStr = JOptionPane.showInputDialog(ModelVozilaCrudProzor.this, 
//					"Unesite ID modela za brisanje (Brišu se i sva vozila tog modela!):", "Brisanje modela", JOptionPane.WARNING_MESSAGE);
//			
//			if (idStr != null && !idStr.trim().isEmpty()) {
//				try {
//					int idModela = Integer.parseInt(idStr.trim());
//					boolean postoji = voziloMenadzer.getSviModeliVozila().stream().anyMatch(m -> m.getId() == idModela);
//					
//					if (postoji) {
//						int p = JOptionPane.showConfirmDialog(ModelVozilaCrudProzor.this, 
//								"Sigurno želite da obrišete model ID " + idModela + " i sva njegova vozila?", "Potvrda", JOptionPane.YES_NO_OPTION);
//						if (p == JOptionPane.YES_OPTION) {
//							voziloMenadzer.obrisiModelVozila(idModela);
//							JOptionPane.showMessageDialog(ModelVozilaCrudProzor.this, "Model uspešno obrisan.");
//						}
//					} else {
//						JOptionPane.showMessageDialog(ModelVozilaCrudProzor.this, "Model sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
//					}
//				} catch (NumberFormatException ex) {
//					JOptionPane.showMessageDialog(ModelVozilaCrudProzor.this, "ID mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
//				}
//			}
//		});
		
		btnPrikaziSve.addActionListener(e -> {
			PrikazModelaVozilaTabela tabela = new PrikazModelaVozilaTabela(voziloMenadzer);
			tabela.setVisible(true);
		});
		
		btnNazad.addActionListener(e -> dispose());
		setLocationRelativeTo(null);
	}
}