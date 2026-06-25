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

import menadzer.OsobaMenadzer;
import model.Admin;

public class AdminCrudProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private OsobaMenadzer osobaMenadzer;

	public AdminCrudProzor(OsobaMenadzer osobaMenadzer) {
		this.osobaMenadzer = osobaMenadzer;
		
		setTitle("Upravljanje Administratorima");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 380);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNaslov = new JLabel("Opcije za Administratora");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(10, 20, 364, 25);
		contentPane.add(lblNaslov);
		
		// 1. Dugme: Registruj novog
		JButton btnRegistruj = new JButton("Registruj novog administratora");
		btnRegistruj.setBounds(60, 70, 260, 35);
		contentPane.add(btnRegistruj);
		
		// 2. Dugme: Izmeni postojećeg
		JButton btnIzmeni = new JButton("Izmeni administratora");
		btnIzmeni.setBounds(60, 120, 260, 35);
		contentPane.add(btnIzmeni);
		
		// 3. Dugme: Obriši
		JButton btnObrisi = new JButton("Obriši administratora");
		btnObrisi.setBounds(60, 170, 260, 35);
		contentPane.add(btnObrisi);
		
		// 4. Dugme: Prikaži sve
		JButton btnPrikaziSve = new JButton("Prikaži sve administratore");
		btnPrikaziSve.setBounds(60, 220, 260, 35);
		contentPane.add(btnPrikaziSve);
		
		// Dugme za povratak nazad
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(140, 290, 100, 30);
		contentPane.add(btnNazad);
		
		btnRegistruj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegistracijaAdminaForma forma = new RegistracijaAdminaForma(osobaMenadzer);
				forma.setVisible(true);
			}
		});
		
		
		btnIzmeni.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String korIme = JOptionPane.showInputDialog(AdminCrudProzor.this, 
						"Unesite korisničko ime administratora kojeg menjate:", 
						"Izmena administratora", JOptionPane.QUESTION_MESSAGE);
				
				if (korIme != null && !korIme.trim().isEmpty()) {
					korIme = korIme.trim();
					Admin adminZaIzmenu = osobaMenadzer.pronadjiAdminaPoKorisnickomImenu(korIme); 
					
					if (adminZaIzmenu != null) {
						IzmenaAdminaForma forma = new IzmenaAdminaForma(osobaMenadzer, adminZaIzmenu);
						forma.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(AdminCrudProzor.this, 
								"Administrator sa tim korisničkim imenom ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		// 3. BRISANJE
		btnObrisi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String korIme = JOptionPane.showInputDialog(AdminCrudProzor.this, 
						"Unesite korisničko ime administratora za brisanje:", 
						"Brisanje administratora", JOptionPane.WARNING_MESSAGE);
				
				if (korIme != null && !korIme.trim().isEmpty()) {
					korIme = korIme.trim();
					
					// Provera da li uopšte postoji pre brisanja
					if (osobaMenadzer.korisnickoImePostoji(korIme)) {
						int potvrdno = JOptionPane.showConfirmDialog(AdminCrudProzor.this, 
								"Da li ste sigurni da želite da obrišete admina: " + korIme + "?", 
								"Potvrda brisanja", JOptionPane.YES_NO_OPTION);
						
						if (potvrdno == JOptionPane.YES_OPTION) {
							osobaMenadzer.obrisiAdmina(korIme);
							JOptionPane.showMessageDialog(AdminCrudProzor.this, "Administrator je uspešno obrisan.");
						}
					} else {
						JOptionPane.showMessageDialog(AdminCrudProzor.this, 
								"Korisničko ime ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		btnPrikaziSve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrikazAdminaTabela prikazProzor = new PrikazAdminaTabela(osobaMenadzer);
				prikazProzor.setVisible(true);
			}
		});
		
		btnNazad.addActionListener(e -> dispose());
		
		setLocationRelativeTo(null);
	}
}