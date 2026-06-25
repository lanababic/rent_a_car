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

public class KlijentCrudProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private OsobaMenadzer osobaMenadzer;

	public KlijentCrudProzor(OsobaMenadzer osobaMenadzer) {
		this.osobaMenadzer = osobaMenadzer;
		
		setTitle("Administracija - Upravljanje Klijentima");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 380);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNaslov = new JLabel("Opcije za Klijente (Admin Panel)");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(10, 20, 364, 25);
		contentPane.add(lblNaslov);
		
		JButton btnRegistruj = new JButton("Registruj novog klijenta");
		btnRegistruj.setBounds(60, 70, 260, 35);
		contentPane.add(btnRegistruj);
		
		JButton btnIzmeni = new JButton("Izmeni klijenta");
		btnIzmeni.setBounds(60, 120, 260, 35);
		contentPane.add(btnIzmeni);
		
		JButton btnObrisi = new JButton("Obriši klijenta");
		btnObrisi.setBounds(60, 170, 260, 35);
		contentPane.add(btnObrisi);
		
		JButton btnPrikaziSve = new JButton("Prikaži sve klijente");
		btnPrikaziSve.setBounds(60, 220, 260, 35);
		contentPane.add(btnPrikaziSve);
		
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(140, 290, 100, 30);
		contentPane.add(btnNazad);
		
		// --- AKCIJE ---
		btnRegistruj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegistracijaKlijentaForma forma = new RegistracijaKlijentaForma(osobaMenadzer);
				forma.setVisible(true);
			}
		});
		
		btnIzmeni.addActionListener(e -> {
			String korIme = JOptionPane.showInputDialog(KlijentCrudProzor.this, 
					"Unesite korisničko ime klijenta kojeg menjate:", 
					"Izmena klijenta", JOptionPane.QUESTION_MESSAGE);
			
			if (korIme != null && !korIme.trim().isEmpty()) {
				korIme = korIme.trim();
				// Pretpostavka za naziv klase (Klijent) i metode pronalaska
				model.Klijent klijentZaIzmenu = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu(korIme); 
				
				if (klijentZaIzmenu != null) {
					IzmenaKlijentaForma forma = new IzmenaKlijentaForma(osobaMenadzer, klijentZaIzmenu);
					forma.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(KlijentCrudProzor.this, 
							"Klijent sa tim korisničkim imenom ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		btnObrisi.addActionListener(e -> {
			String korIme = JOptionPane.showInputDialog(KlijentCrudProzor.this, 
					"Unesite korisničko ime klijenta za brisanje:",
					"Brisanje klijenta", JOptionPane.WARNING_MESSAGE);
			
			if (korIme != null && !korIme.trim().isEmpty()) {
				korIme = korIme.trim();
				int p = JOptionPane.showConfirmDialog(KlijentCrudProzor.this, 
						"Da li ste sigurni da želite da obrišete klijenta: " + korIme + "?", 
						"Potvrda brisanja", JOptionPane.YES_NO_OPTION);
				
				if (p == JOptionPane.YES_OPTION) {
					osobaMenadzer.obrisiKlijenta(korIme);
					JOptionPane.showMessageDialog(KlijentCrudProzor.this, "Klijent je uspešno obrisan.");
				}
			}
		});
		
		btnPrikaziSve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrikazKlijentaTabela prikazProzor = new PrikazKlijentaTabela(osobaMenadzer);
				prikazProzor.setVisible(true);
			}
		});
		btnNazad.addActionListener(e -> dispose());
		
		setLocationRelativeTo(null);
	}
}