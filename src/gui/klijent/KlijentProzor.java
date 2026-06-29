package gui.klijent;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import enums.ZahtevPretplate;
import gui.LoginForm;
import menadzer.FinansijeMenadzer;
import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import model.Klijent;
import model.Osoba;


public class KlijentProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private OsobaMenadzer osobaMenadzer;

	public KlijentProzor(OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		this.osobaMenadzer = osobaMenadzer;
		
		setTitle("Klijentski Panel - Glavni Meni");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 470);
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


		JButton btnPretplata = new JButton("Zahtev za pretplatu");
		btnPretplata.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnPretplata.setBounds(100, 100, 280, 35);
		contentPane.add(btnPretplata);

		JButton btnDostupnaVozila = new JButton("Dostupna vozila");
		btnDostupnaVozila.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnDostupnaVozila.setBounds(100, 155, 280, 35);
		contentPane.add(btnDostupnaVozila);

		JButton btnRezervisi = new JButton("Rezerviši");
		btnRezervisi.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnRezervisi.setBounds(100, 210, 280, 35);
		contentPane.add(btnRezervisi);
		
		JButton btnSveRezervacije = new JButton("Sve Rezervacije");
		btnSveRezervacije.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSveRezervacije.setBounds(100, 265, 280, 35);
		contentPane.add(btnSveRezervacije);
		
		JButton btnOtkaziRez = new JButton("Otkazi rezervaciju");
		btnOtkaziRez.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnOtkaziRez.setBounds(100, 320, 280, 35);
		contentPane.add(btnOtkaziRez);
		


		JButton btnOdjava = new JButton("Odjava");
		btnOdjava.setBounds(365, 370, 100, 30);
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
				Osoba ulogovan = osobaMenadzer.getTrenutnoUlogovan();
				Klijent k = osobaMenadzer.pronadjiKlijentaPoKorisnickomImenu(ulogovan.getKorisnickoIme());//mislim da ne mora provera jer nema sile da je ovde a da nije klijent
				if(k.getPretplata()!=null && k.getPretplata().getDatumKraj().isAfter(LocalDate.now())) {
					RezervacijaProzor rezervacijaProzor = new RezervacijaProzor(rezervacijeMenadzer, osobaMenadzer, voziloMenadzer, finansijeMenadzer);
					rezervacijaProzor.setVisible(true);
				}
				else {
					JOptionPane.showMessageDialog(
							KlijentProzor.this, 
							"Nemate važecu pretplatu", 
							"Uplatite pretplatu",
							JOptionPane.ERROR_MESSAGE
						);
				}
			}
		});
		
		btnSveRezervacije.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PregledSveRezervacijeKlijent rezervacijaProzor = new PregledSveRezervacijeKlijent(rezervacijeMenadzer, osobaMenadzer);
				rezervacijaProzor.setVisible(true);
			}
		});
		
		btnOtkaziRez.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OtkazivanjeRezervacijeProzor to = new OtkazivanjeRezervacijeProzor(rezervacijeMenadzer, osobaMenadzer);
				to.setVisible(true);
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