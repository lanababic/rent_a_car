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

import menadzer.RezervacijeMenadzer;
import model.DodatnaUsluga;

public class DodatnaUslugaCrudProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private RezervacijeMenadzer rezervacijeMenadzer;

	public DodatnaUslugaCrudProzor(RezervacijeMenadzer rezervacijeMenadzer) {
		this.rezervacijeMenadzer = rezervacijeMenadzer;
		
		setTitle("Upravljanje Dodatnim Uslugama");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 330);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNaslov = new JLabel("Opcije za Dodatne Usluge");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(10, 20, 364, 25);
		contentPane.add(lblNaslov);
		
		JButton btnDodaj = new JButton("Dodaj novu dodatnu uslugu");
		btnDodaj.setBounds(60, 70, 260, 35);
		contentPane.add(btnDodaj);
		
		JButton btnObrisi = new JButton("Obriši dodatnu uslugu");
		btnObrisi.setBounds(60, 120, 260, 35);
		contentPane.add(btnObrisi);
		
		JButton btnPrikaziSve = new JButton("Prikaži sve dodatne usluge");
		btnPrikaziSve.setBounds(60, 170, 260, 35);
		contentPane.add(btnPrikaziSve);
		
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(140, 240, 100, 30);
		contentPane.add(btnNazad);
		
		// --- AKCIJE ---

		// 1. DODAVANJE
		btnDodaj.addActionListener(e -> {
			KreiranjeDodatneUslugeForma forma = new KreiranjeDodatneUslugeForma(rezervacijeMenadzer);
			forma.setVisible(true);
		});
		
		// 2. BRISANJE
		btnObrisi.addActionListener(e -> {
			String idStr = JOptionPane.showInputDialog(DodatnaUslugaCrudProzor.this, 
					"Unesite ID dodatne usluge za brisanje:", 
					"Brisanje usluge", JOptionPane.WARNING_MESSAGE);
			
			if (idStr != null && !idStr.trim().isEmpty()) {
				try {
					int idUsluge = Integer.parseInt(idStr.trim());
					
					// Provera postojanja usluge
					boolean postoji = rezervacijeMenadzer.getSveDodatneUsluge().stream()
							.anyMatch(u -> u.getIdDodatneUsluge() == idUsluge);
					
					if (postoji) {
						int p = JOptionPane.showConfirmDialog(DodatnaUslugaCrudProzor.this, 
								"Da li ste sigurni da želite da obrišete uslugu sa ID-jem: " + idUsluge + "?", 
								"Potvrda brisanja", JOptionPane.YES_NO_OPTION);
						
						if (p == JOptionPane.YES_OPTION) {
							// Poziv tvoje metode obrisiDodadnuUslugu
							rezervacijeMenadzer.obrisiDodadnuUslugu(idUsluge);
							JOptionPane.showMessageDialog(DodatnaUslugaCrudProzor.this, "Usluga je uspešno obrisana.");
						}
					} else {
						JOptionPane.showMessageDialog(DodatnaUslugaCrudProzor.this, 
								"Usluga sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
					
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(DodatnaUslugaCrudProzor.this, 
							"ID usluge mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// 3. PRIKAZ SVIH
		btnPrikaziSve.addActionListener(e -> {
			PrikazDodatnihUslugaTabela tabela = new PrikazDodatnihUslugaTabela(rezervacijeMenadzer);
			tabela.setVisible(true);
		});
		
		btnNazad.addActionListener(e -> dispose());
		
		setLocationRelativeTo(null);
	}
}