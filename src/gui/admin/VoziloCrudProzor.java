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
import model.Vozilo;

public class VoziloCrudProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private VoziloMenadzer voziloMenadzer;

	public VoziloCrudProzor(VoziloMenadzer voziloMenadzer) {
		this.voziloMenadzer = voziloMenadzer;
		
		setTitle("Upravljanje Vozilima");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 380);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNaslov = new JLabel("Opcije za Vozila");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(10, 20, 364, 25);
		contentPane.add(lblNaslov);
		
		// 1. Dugme: Registruj novo vozilo
		JButton btnRegistruj = new JButton("Registruj novo vozilo");
		btnRegistruj.setBounds(60, 70, 260, 35);
		contentPane.add(btnRegistruj);
		
		// 2. Dugme: Izmeni postojeće vozilo
		JButton btnIzmeni = new JButton("Izmeni vozilo");
		btnIzmeni.setBounds(60, 120, 260, 35);
		contentPane.add(btnIzmeni);
		
		// 3. Dugme: Obriši vozilo
		JButton btnObrisi = new JButton("Obriši vozilo");
		btnObrisi.setBounds(60, 170, 260, 35);
		contentPane.add(btnObrisi);
		
		// 4. Dugme: Prikaži sva vozila
		JButton btnPrikaziSve = new JButton("Prikaži sva vozila");
		btnPrikaziSve.setBounds(60, 220, 260, 35);
		contentPane.add(btnPrikaziSve);
		
		// Dugme za povratak nazad
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(140, 290, 100, 30);
		contentPane.add(btnNazad);
		
		
		btnRegistruj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				KreiranjeVozilaForma forma = new KreiranjeVozilaForma(voziloMenadzer);
				forma.setVisible(true);
			}
		});
		
		
		btnIzmeni.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String idStr = JOptionPane.showInputDialog(VoziloCrudProzor.this, 
						"Unesite ID vozila koje menjate:", 
						"Izmena vozila", JOptionPane.QUESTION_MESSAGE);
				
				if (idStr != null && !idStr.trim().isEmpty()) {
					try {
						int idVozila = Integer.parseInt(idStr.trim());
						
						
						Vozilo voziloZaIzmenu = null;
						for (Vozilo v : voziloMenadzer.getSvaVozila()) {
							if (v.getIdVozila() == idVozila) {
								voziloZaIzmenu = v;
								break;
							}
						}
						
						if (voziloZaIzmenu != null) {
							IzmenaVozilaForma forma = new IzmenaVozilaForma(voziloMenadzer, voziloZaIzmenu);
							forma.setVisible(true);
						} else {
							JOptionPane.showMessageDialog(VoziloCrudProzor.this, 
									"Vozilo sa unetim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						}
						
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(VoziloCrudProzor.this, 
								"ID vozila mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		// 3. BRISANJE
		btnObrisi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String idStr = JOptionPane.showInputDialog(VoziloCrudProzor.this, 
						"Unesite ID vozila za brisanje:", 
						"Brisanje vozila", JOptionPane.WARNING_MESSAGE);
				
				if (idStr != null && !idStr.trim().isEmpty()) {
					try {
						int idVozila = Integer.parseInt(idStr.trim());
						
						
						boolean postoji = false;
						for (Vozilo v : voziloMenadzer.getSvaVozila()) {
							if (v.getIdVozila() == idVozila) {
								postoji = true;
								break;
							}
						}
						
						if (postoji) {
							int potvrdno = JOptionPane.showConfirmDialog(VoziloCrudProzor.this, 
									"Da li ste sigurni da želite da obrišete vozilo sa ID-jem: " + idVozila + "?", 
									"Potvrda brisanja", JOptionPane.YES_NO_OPTION);
							
							if (potvrdno == JOptionPane.YES_OPTION) {
								voziloMenadzer.obrisiVozilo(idVozila);
								JOptionPane.showMessageDialog(VoziloCrudProzor.this, "Vozilo je uspešno obrisano.");
							}
						} else {
							JOptionPane.showMessageDialog(VoziloCrudProzor.this, 
									"Vozilo sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						}
						
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(VoziloCrudProzor.this, 
								"ID vozila mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		// 4. PRIKAZ SVIH
		btnPrikaziSve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrikazVozilaTabela prikazProzor = new PrikazVozilaTabela(voziloMenadzer);
				prikazProzor.setVisible(true);
			}
		});
		
		btnNazad.addActionListener(e -> dispose());
		
		setLocationRelativeTo(null);
	}
}