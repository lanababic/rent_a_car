package gui.agent;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import gui.LoginForm;
import menadzer.FinansijeMenadzer;
import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;

import gui.admin.RegistracijaKlijentaForma;

public class AgentProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	// Menadžeri sačuvani kao polja klase (ako zatrebaju unutar klase)
	private OsobaMenadzer osobaMenadzer;
	private VoziloMenadzer voziloMenadzer;
	private FinansijeMenadzer finansijeMenadzer;
	private IzdavanjeMenadzer izdavanjeMenadzer;
	private RezervacijeMenadzer rezervacijeMenadzer;

	public AgentProzor(OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		this.osobaMenadzer = osobaMenadzer;
		this.voziloMenadzer = voziloMenadzer;
		this.finansijeMenadzer = finansijeMenadzer;
		this.izdavanjeMenadzer = izdavanjeMenadzer;
		this.rezervacijeMenadzer = rezervacijeMenadzer;
		
		setTitle("Agentski Panel - Glavni Meni");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 450); // Blago povećana visina (sa 400 na 450) zbog 4 dugmeta
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov
		JLabel lblNaslov = new JLabel("Dobrodošli u Agentski Panel");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNaslov.setBounds(10, 25, 464, 30);
		contentPane.add(lblNaslov);
		
		// 1. Dugme: Iznajmljivanje
		JButton btnIznajmljivanje = new JButton("Iznajmljivanje Vozila");
		btnIznajmljivanje.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnIznajmljivanje.setBounds(100, 80, 280, 45);
		contentPane.add(btnIznajmljivanje);
		
		// 2. Dugme: Rezervacije
		JButton btnRezervacije = new JButton("Upravljanje Rezervacijama");
		btnRezervacije.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnRezervacije.setBounds(100, 140, 280, 45);
		contentPane.add(btnRezervacije);
		
		// 3. Dugme: Registracija Klijenta
		JButton btnRegistracijaKlijenta = new JButton("Registracija Klijenta");
		btnRegistracijaKlijenta.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnRegistracijaKlijenta.setBounds(100, 200, 280, 45);
		contentPane.add(btnRegistracijaKlijenta);
		
		// 4. Dugme: Vozila
		JButton btnVozila = new JButton("Pregled Vozila");
		btnVozila.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnVozila.setBounds(100, 260, 280, 45);
		contentPane.add(btnVozila);
		
		// Dugme za odjavu
		JButton btnOdjava = new JButton("Odjava");
		btnOdjava.setBounds(365, 360, 100, 30);
		contentPane.add(btnOdjava);
		
		// --- AKCIJE NA DUGMIĆIMA ---
		
		// Akcija za Iznajmljivanje
		btnIznajmljivanje.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Ovde instanciraš svoj prozor za iznajmljivanje, npr:
				IznajmljivanjeProzor iznProzor = new IznajmljivanjeProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				iznProzor.setVisible(true);
				dispose();
			}
		});
		
		// Akcija za Rezervacije
		btnRezervacije.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AgentRezervacijeProzor rezProzor = new AgentRezervacijeProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				rezProzor.setVisible(true);
				dispose();
			}
		});
		
		btnRegistracijaKlijenta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegistracijaKlijenta regForma = new RegistracijaKlijenta(osobaMenadzer);
				regForma.setVisible(true);
			}
		});
		
		// Akcija za Vozila
		btnVozila.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PregledVozilaProzor vozilaProzor = new PregledVozilaProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				vozilaProzor.setVisible(true);
				dispose();
			}
		});
		
		// Akcija za Odjavu
		btnOdjava.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				osobaMenadzer.odjava();
				LoginForm login = new LoginForm(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				login.setVisible(true);
				dispose(); // Zatvara trenutni agentski prozor
			}
		});
		
		// Centriraj prozor na ekranu
		setLocationRelativeTo(null);
	}
}