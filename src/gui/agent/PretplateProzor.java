package gui.agent;

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

import enums.ZahtevPretplate;
import gui.admin.PretplataCrudProzor;
import menadzer.FinansijeMenadzer;
import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import model.Klijent;
import model.Pretplata;

public class PretplateProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	// Menadžeri prosleđeni kroz konstruktor
	private OsobaMenadzer osobaMenadzer;
	private VoziloMenadzer voziloMenadzer;
	private FinansijeMenadzer finansijeMenadzer;
	private IzdavanjeMenadzer izdavanjeMenadzer;
	private RezervacijeMenadzer rezervacijeMenadzer;

	public PretplateProzor(OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		this.osobaMenadzer = osobaMenadzer;
		this.voziloMenadzer = voziloMenadzer;
		this.finansijeMenadzer = finansijeMenadzer;
		this.izdavanjeMenadzer = izdavanjeMenadzer;
		this.rezervacijeMenadzer = rezervacijeMenadzer;
		
		setTitle("Upravljanje Pretplatama");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov
		JLabel lblNaslov = new JLabel("Meni za Upravljanje Pretplatama");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNaslov.setBounds(10, 25, 464, 30);
		contentPane.add(lblNaslov);
		
		// 1. Dugme: Pregled svih pretplata
		JButton btnPregled = new JButton("Pregled Svih Pretplata");
		btnPregled.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnPregled.setBounds(100, 80, 280, 45);
		contentPane.add(btnPregled);
		
		// 2. Dugme: Napravi pretplatu
		JButton btnKreiraj = new JButton("Napravi Novu Pretplatu");
		btnKreiraj.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnKreiraj.setBounds(100, 140, 280, 45);
		contentPane.add(btnKreiraj);
		
		// 3. Dugme: Produži pretplatu
		JButton btnProduzi = new JButton("Produži Pretplatu");
		btnProduzi.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnProduzi.setBounds(100, 200, 280, 45);
		contentPane.add(btnProduzi);
		
		// 4. Dugme: Zahtevi
		JButton btnZahtevi = new JButton("Zahtevi za Pretplate");
		btnZahtevi.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnZahtevi.setBounds(100, 260, 280, 45);
		contentPane.add(btnZahtevi);
		
		// Dugme Nazad
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(365, 360, 100, 30);
		contentPane.add(btnNazad);
		
		// --- AKCIJE NA DUGMIĆIMA ---
		
		// 1. Akcija: Pregled svih pretplata
		btnPregled.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Otvaranje prozora sa tabelom svih pretplata
				PregledPretplataTabela pregled = new PregledPretplataTabela(finansijeMenadzer);
				pregled.setVisible(true);
			}
		});
		
		// 2. Akcija: Napravi pretplatu
		btnKreiraj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NovaPretplataForma novaPf = new NovaPretplataForma(finansijeMenadzer, osobaMenadzer);
				novaPf.setVisible(true);
			}
		});
		
//		// 3. Akcija: Produži pretplatu
//		btnProduzi.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// TODO: Otvaranje forme za produžavanje trajanja postojeće pretplate
//				// ProduziPretplatuForma produziPf = new ProduziPretplatuForma(finansijeMenadzer);
//				// produziPf.setVisible(true);
//			}
//		});
		btnProduzi.addActionListener(e -> {
			String idStr = JOptionPane.showInputDialog(PretplateProzor.this, 
					"Unesite ID pretplate koju produžavate:", "Produžavanje pretplate", JOptionPane.QUESTION_MESSAGE);
			
			if (idStr != null && !idStr.trim().isEmpty()) {
				try {
					int idPretplate = Integer.parseInt(idStr.trim());
					Pretplata p = finansijeMenadzer.getSvePretplate().stream()
							.filter(pr -> pr.getIdPretplate() == idPretplate)
							.findFirst()
							.orElse(null);
					
					if (p != null) {
						Klijent k = p.getKlijent();
						// Provera uslova iz tvoje metode produziPretplatu
						if (k.getBrojKasnjenja() <= 5 && k.getZahtev().equals(ZahtevPretplate.ODOBREN)) {
							int potvrda = JOptionPane.showConfirmDialog(PretplateProzor.this, 
									"Da li ste sigurni da želite da produžite pretplatu za klijenta: " + k.getIme() + " " + k.getPrezime() + "?", 
									"Potvrda", JOptionPane.YES_NO_OPTION);
							
							if (potvrda == JOptionPane.YES_OPTION) {
								finansijeMenadzer.produziPretplatu(p, osobaMenadzer);
								JOptionPane.showMessageDialog(PretplateProzor.this, "Pretplata uspešno produžena za godinu dana.");
							}
						} else {
							// GUI ispis greške iz tvoje else grane
							JOptionPane.showMessageDialog(PretplateProzor.this, 
									"Nemoguće produžiti pretplatu!\nKlijent ima više od 5 kašnjenja ili zahtev nije odobren.", 
									"Greška pri produžavanju", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(PretplateProzor.this, "Pretplata sa unetim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(PretplateProzor.this, "ID mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// 4. Akcija: Zahtevi
		btnZahtevi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Otvaranje prozora za odobravanje/odbijanje online zahteva za pretplate
				 ZahteviZaPretplateProzor zahteviProzor = new ZahteviZaPretplateProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				 zahteviProzor.setVisible(true);
				 setVisible(false);
			}
		});
		
		// Akcija za Nazad (Povratak na glavni agentski panel)
		btnNazad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AgentProzor ap = new AgentProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				ap.setVisible(true);
				dispose(); // Zatvara trenutni prozor za pretplate
			}
		});
		
		// Centriranje prozora
		setLocationRelativeTo(null);
	}
}