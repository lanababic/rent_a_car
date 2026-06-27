package gui.klijent;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;

import gui.LoginForm;
import menadzer.FinansijeMenadzer;
import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import enums.*;


public class KlijentProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private OsobaMenadzer osobaMenadzer;

	public KlijentProzor(OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		this.osobaMenadzer = osobaMenadzer;
		
		setTitle("Klijentski Panel - Glavni Meni");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov
		JLabel lblNaslov = new JLabel("Dobrodošli u Klijentski Panel");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNaslov.setBounds(10, 30, 464, 30);
		contentPane.add(lblNaslov);
		
		// 1. Dugme: Zahtev za pretplatu
		JButton btnPretplata = new JButton("Zahtev za pretplatu");
		btnPretplata.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnPretplata.setBounds(100, 100, 280, 45);
		contentPane.add(btnPretplata);
		
		// 2. Dugme: Dostupna vozila
		JButton btnDostupnaVozila = new JButton("Dostupna vozila");
		btnDostupnaVozila.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnDostupnaVozila.setBounds(100, 165, 280, 45);
		contentPane.add(btnDostupnaVozila);
		
		// 3. Dugme: Rezerviši
		JButton btnRezervisi = new JButton("Rezerviši");
		btnRezervisi.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnRezervisi.setBounds(100, 230, 280, 45);
		contentPane.add(btnRezervisi);


		JButton btnOdjava = new JButton("Odjava");
		btnOdjava.setBounds(365, 315, 100, 30);
		contentPane.add(btnOdjava);
		
		btnPretplata.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 1. Pozivamo metodu menadžera koja postavlja status u zavisnosti od kašnjenja
				osobaMenadzer.podnesiZahtev();
				
				// 2. Pronalazimo trenutno ulogovanog klijenta da bismo proverili ishod
				// (Koristimo istu logiku iz tvoje metode: tražimo ga preko trenutno ulogovanog korisničkog imena)
				String trenutnoIme = osobaMenadzer.getTrenutnoUlogovan().getKorisnickoIme();
				model.Klijent klijent = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu(trenutnoIme);
				
				// 3. Proveravamo status zahteva i prikazujemo odgovarajući dijalog korisniku
				if (klijent.getZahtev() == ZahtevPretplate.ODBIJEN) {
					JOptionPane.showMessageDialog(
						KlijentProzor.this, 
						"Vaš zahtev za pretplatu je odbijen jer imate više od 5 kašnjenja.", 
						"Zahtev Odbijen", 
						JOptionPane.ERROR_MESSAGE
					);
				} else if (klijent.getZahtev() == ZahtevPretplate.POSLAT) {
					JOptionPane.showMessageDialog(
						KlijentProzor.this, 
						"Zahtev za pretplatu je uspešno poslat.", 
						"Zahtev Poslat", 
						JOptionPane.INFORMATION_MESSAGE
					);
				}
			}
		});
		
		btnDostupnaVozila.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Ovde otvaraš pregled dostupnih vozila
				DostupnaVozilaProzor vozilaProzor = new DostupnaVozilaProzor(voziloMenadzer, izdavanjeMenadzer);
				vozilaProzor.setVisible(true);
			}
		});
		
		btnRezervisi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RezervacijaProzor rezervacijaProzor = new RezervacijaProzor(rezervacijeMenadzer, osobaMenadzer, voziloMenadzer, finansijeMenadzer);
				rezervacijaProzor.setVisible(true);
			}
		});
		
		btnOdjava.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				osobaMenadzer.odjava();
				LoginForm login = new LoginForm(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				login.setVisible(true);
				dispose(); // Zatvara trenutni klijentski prozor
			}
		});
		
		// Centriraj prozor na ekranu
		setLocationRelativeTo(null);
	}
}