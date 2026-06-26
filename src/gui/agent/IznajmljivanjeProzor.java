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

import menadzer.FinansijeMenadzer;
import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;

public class IznajmljivanjeProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	// Menadžeri prosleđeni kroz konstruktor
	private OsobaMenadzer osobaMenadzer;
	private VoziloMenadzer voziloMenadzer;
	private FinansijeMenadzer finansijeMenadzer;
	private IzdavanjeMenadzer izdavanjeMenadzer;
	private RezervacijeMenadzer rezervacijeMenadzer;

	public IznajmljivanjeProzor(OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		this.osobaMenadzer = osobaMenadzer;
		this.voziloMenadzer = voziloMenadzer;
		this.finansijeMenadzer = finansijeMenadzer;
		this.izdavanjeMenadzer = izdavanjeMenadzer;
		this.rezervacijeMenadzer = rezervacijeMenadzer;
		
		setTitle("Upravljanje Iznajmljivanjem");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // DISPOSE jer ne želimo da ugasi ceo program ako se zatvori ovaj podprozor
		setBounds(100, 100, 500, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov
		JLabel lblNaslov = new JLabel("Iznajmljivanje i Prijem Vozila");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNaslov.setBounds(10, 25, 464, 30);
		contentPane.add(lblNaslov);
		
		// 1. Dugme: Iznajmi vozilo
		JButton btnIznajmi = new JButton("Iznajmi Vozilo");
		btnIznajmi.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnIznajmi.setBounds(100, 85, 280, 45);
		contentPane.add(btnIznajmi);
		
		// 2. Dugme: Primi vozilo
		JButton btnPrimi = new JButton("Primi Vozilo (Povrat)");
		btnPrimi.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnPrimi.setBounds(100, 145, 280, 45);
		contentPane.add(btnPrimi);
		
		// Dugme za povratak nazad
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(365, 215, 100, 30);
		contentPane.add(btnNazad);
		
		// --- AKCIJE NA DUGMIĆIMA ---
		
		// Akcija za Iznajmljivanje novog vozila
		btnIznajmi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Otvaranje forme/prozora za unos podataka o novom iznajmljivanju
				NovaIzdavanjaProzor noviUnos = new NovaIzdavanjaProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer,  izdavanjeMenadzer,  rezervacijeMenadzer);
				noviUnos.setVisible(true);
				dispose();
			}
		});
		
		// Akcija za Prijem vozila nazad na stanje
		btnPrimi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Otvaranje forme/prozora za vraćanje vozila i obračun troškova
				PrijemVozilaProzor prijem = new PrijemVozilaProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer,  izdavanjeMenadzer,  rezervacijeMenadzer);
				prijem.setVisible(true);
				dispose();
			}
		});
		
		// Akcija za povratak na AgentProzor
		btnNazad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Kreira se i otvara ponovo glavni agentski prozor, a ovaj se zatvara
				AgentProzor agentProzor = new AgentProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				agentProzor.setVisible(true);
				dispose(); 
			}
		});
		
		// Centriraj prozor na ekranu
		setLocationRelativeTo(null);
	}
}