package gui.admin;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import menadzer.FinansijeMenadzer;
import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;

public class IzvestajiProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	// Reference ka menadžerima ako zatrebaju unutar podprozora za izveštaje
	private OsobaMenadzer osobaMenadzer;
	private VoziloMenadzer voziloMenadzer;
	private FinansijeMenadzer finansijeMenadzer;
	private IzdavanjeMenadzer izdavanjeMenadzer;
	private RezervacijeMenadzer rezervacijeMenadzer;

	public IzvestajiProzor(OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, 
			FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, 
			RezervacijeMenadzer rezervacijeMenadzer) {
		
		this.osobaMenadzer = osobaMenadzer;
		this.voziloMenadzer = voziloMenadzer;
		this.finansijeMenadzer = finansijeMenadzer;
		this.izdavanjeMenadzer = izdavanjeMenadzer;
		this.rezervacijeMenadzer = rezervacijeMenadzer;
		
		setTitle("Izveštaji i Statistika");
		// Koristimo DISPOSE_ON_CLOSE da zatvaranjem ovog prozora ne ugasimo ceo program
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
		setBounds(100, 100, 450, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov prozora
		JLabel lblNaslov = new JLabel("Izaberite Tip Izveštaja");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNaslov.setBounds(10, 25, 414, 30);
		contentPane.add(lblNaslov);
		
		// 1. Dugme: Izdavanja
		JButton btnIzdavanja = new JButton("Izveštaj o Izdavanjima");
		btnIzdavanja.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnIzdavanja.setBounds(75, 90, 280, 40);
		contentPane.add(btnIzdavanja);
		
		// 2. Dugme: Rezervacije
		JButton btnRezervacije = new JButton("Izveštaj o Rezervacijama");
		btnRezervacije.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnRezervacije.setBounds(75, 150, 280, 40);
		contentPane.add(btnRezervacije);
		
		// 3. Dugme: Modeli vozila
		JButton btnModeli = new JButton("Statistika Modela Vozila");
		btnModeli.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnModeli.setBounds(75, 210, 280, 40);
		contentPane.add(btnModeli);
		
		// 4. Dugme: Prihodi i rashodi
		JButton btnFinansije = new JButton("Izveštaj o Prihodima i Rashodima");
		btnFinansije.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnFinansije.setBounds(75, 270, 280, 40);
		contentPane.add(btnFinansije);
		
		// Dugme za povratak nazad
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNazad.setBounds(165, 350, 100, 30);
		contentPane.add(btnNazad);
		
		// --- AKCIJE NA DUGMIĆIMA ---
		
		btnIzdavanja.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IzdavanjaIzvestajProzor izvestajIzdavanja = new IzdavanjaIzvestajProzor(osobaMenadzer, izdavanjeMenadzer);
				izvestajIzdavanja.setVisible(true);
			}
		});
		
		btnRezervacije.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RezervacijeIzvestajProzor prozorRezervacije = new RezervacijeIzvestajProzor(rezervacijeMenadzer);
				prozorRezervacije.setVisible(true);
			}
		});
		
		btnModeli.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Prosleđujemo menadžere koji drže metode i podatke o vozilima/modelima
				ModeliIzvestajProzor moduliProzor = new ModeliIzvestajProzor(voziloMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				moduliProzor.setVisible(true);
			}
		});
		
		btnFinansije.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Prosleđujemo osobaMenadzer (za rashode) i izdavanjeMenadzer (za prihode)
				PrihodiRashodiIzvestajProzor finansijeProzor = new PrihodiRashodiIzvestajProzor(osobaMenadzer, izdavanjeMenadzer);
				finansijeProzor.setVisible(true);
			}
		});
		
		// Akcija za povratak natrag u glavni admin prozor
		btnNazad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose(); // Zatvara samo ovaj prozor i vraća fokus na AdminProzor
			}
		});
		
		// Centriranje prozora na ekranu
		setLocationRelativeTo(null);
	}
}