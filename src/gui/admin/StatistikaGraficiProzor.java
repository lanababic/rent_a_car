package gui.admin;

import java.awt.BorderLayout;
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

public class StatistikaGraficiProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private OsobaMenadzer osobaMenadzer;
	private IzdavanjeMenadzer izdavanjeMenadzer;
	private RezervacijeMenadzer rezervacijeMenadzer;
	private FinansijeMenadzer finansijeMenadzer;

	public StatistikaGraficiProzor(OsobaMenadzer osobaMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, 
			RezervacijeMenadzer rezervacijeMenadzer, FinansijeMenadzer finansijeMenadzer) {
		
		this.osobaMenadzer = osobaMenadzer;
		this.izdavanjeMenadzer = izdavanjeMenadzer;
		this.rezervacijeMenadzer = rezervacijeMenadzer;
		this.finansijeMenadzer = finansijeMenadzer;
		
		setTitle("Grafički Prikazi i Statistika");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 480, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov prozora
		JLabel lblNaslov = new JLabel("Vizuelni Izveštaji i Grafikoni");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNaslov.setBounds(10, 25, 444, 30);
		contentPane.add(lblNaslov);
		
		JButton btnPrihodiKlijenti = new JButton("Prihodi po kategoriji klijenta");
		btnPrihodiKlijenti.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnPrihodiKlijenti.setBounds(70, 90, 320, 45);
		contentPane.add(btnPrihodiKlijenti);


		JButton btnStatusRezervacija = new JButton("Status rezervacija (poslednjih 30 dana)");
		btnStatusRezervacija.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnStatusRezervacija.setBounds(70, 155, 320, 45);
		contentPane.add(btnStatusRezervacija);


		JButton btnOpterecenjeAgenta = new JButton("Opterećenje agenata (poslednjih 30 dana)");
		btnOpterecenjeAgenta.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnOpterecenjeAgenta.setBounds(70, 220, 320, 45);
		contentPane.add(btnOpterecenjeAgenta);
		
		
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNazad.setBounds(180, 305, 100, 30);
		contentPane.add(btnNazad);


		btnPrihodiKlijenti.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        JFrame prozorZaGrafik = new JFrame("Prikaz Prihoda po Kategorijama");
		        prozorZaGrafik.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		        prozorZaGrafik.setSize(950, 650); 
		        prozorZaGrafik.setLayout(new BorderLayout());
		        GrafikPrihodiPoKategoriji panelSaGrafikom = new GrafikPrihodiPoKategoriji(izdavanjeMenadzer);
		        prozorZaGrafik.add(panelSaGrafikom, BorderLayout.CENTER);
		        prozorZaGrafik.setLocationRelativeTo(null);
		        prozorZaGrafik.setVisible(true);
				
			}
		});


		btnStatusRezervacija.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 1. Kreiramo novi prozor za pitu
		        JFrame prozorZaPitu = new JFrame("Prikaz Statusa Rezervacija");
		        prozorZaPitu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		        prozorZaPitu.setSize(650, 500); // Ne mora biti prevelik, pita lepo staje ovde
		        prozorZaPitu.setLayout(new BorderLayout());
		        
		        // 2. Instanciramo naš novi panel i prosleđujemo mu rezervacijeMenadzer koji već imaš u klasi
		        GrafikStatusRezervacija panelSaPitom = new GrafikStatusRezervacija(rezervacijeMenadzer);
		        
		        // 3. Dodajemo panel u centar novog prozora
		        prozorZaPitu.add(panelSaPitom, BorderLayout.CENTER);
		        
		        // 4. Centriramo prozor i prikazujemo ga
		        prozorZaPitu.setLocationRelativeTo(null);
		        prozorZaPitu.setVisible(true);
			}
		});
		
		btnOpterecenjeAgenta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 1. Kreiramo novi prozor za grafikon opterećenja agenata
		        JFrame prozorZaAgente = new JFrame("Prikaz Opterećenja Agenata");
		        prozorZaAgente.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		        prozorZaAgente.setSize(700, 500);
		        prozorZaAgente.setLayout(new BorderLayout());
		        
		        // 2. Instanciramo novi panel i prosleđujemo oba potrebna menadžera iz klase
		        GrafikOpterecenjeAgenata panelAgenti = new GrafikOpterecenjeAgenata(osobaMenadzer, izdavanjeMenadzer);
		        
		        // 3. Dodajemo panel u centar novog prozora
		        prozorZaAgente.add(panelAgenti, BorderLayout.CENTER);
		        
		        // 4. Centriramo prozor i prikazujemo ga
		        prozorZaAgente.setLocationRelativeTo(null);
		        prozorZaAgente.setVisible(true);
			}
		});
		
		
		btnNazad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		
		setLocationRelativeTo(null);
	}
}