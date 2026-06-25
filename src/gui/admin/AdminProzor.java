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

import gui.LoginForm;
import menadzer.FinansijeMenadzer;
import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;

public class AdminProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private OsobaMenadzer osobaMenadzer;
	// Ako ti zatrebaju ostali menadžeri za vozila ili izveštaje, dodaćeš ih ovde:
	// private VoziloMenadzer voziloMenadzer;
	// private RezervacijeMenadzer rezervacijeMenadzer;

	public AdminProzor(OsobaMenadzer osobaMenadzer,VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		this.osobaMenadzer = osobaMenadzer;
		
		setTitle("Administratorski Panel - Glavni Meni");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov
		JLabel lblNaslov = new JLabel("Dobrodošli u Administratorski Panel");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNaslov.setBounds(10, 30, 464, 30);
		contentPane.add(lblNaslov);
		
		// 1. Dugme: CRUD Upravljanje Podacima
		JButton btnCrud = new JButton("CRUD");
		btnCrud.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCrud.setBounds(100, 100, 280, 45);
		contentPane.add(btnCrud);
		
		// 2. Dugme: Izveštaji
		JButton btnIzvestaji = new JButton("Izvestaji");
		btnIzvestaji.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnIzvestaji.setBounds(100, 165, 280, 45);
		contentPane.add(btnIzvestaji);
		
		// 3. Dugme: Grafici / Chartovi
		JButton btnChartovi = new JButton("Grafički Prikazi i Statistika");
		btnChartovi.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnChartovi.setBounds(100, 230, 280, 45);
		contentPane.add(btnChartovi);
		
		// Dugme za odjavu
		JButton btnOdjava = new JButton("Odjava");
		btnOdjava.setBounds(365, 315, 100, 30);
		contentPane.add(btnOdjava);
		
		// --- AKCIJE NA DUGMIĆIMA ---
		
		// Otvaranje CRUD selekcionog prozora
		btnCrud.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Ovde otvaraš novi prozor gde biraš entitet (Admini, Agenti, Vozila...)
				CrudMeniProzor crudProzor = new CrudMeniProzor(osobaMenadzer, voziloMenadzer,rezervacijeMenadzer, finansijeMenadzer,izdavanjeMenadzer);
				crudProzor.setVisible(true);
				// Ukoliko želiš da se ovaj glavni meni sakrije dok radiš u CRUD-u, otkomentariši sledeću liniju:
				// setVisible(false);
			}
		});
		
		// Otvaranje prozora za izveštaje
		btnIzvestaji.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IzvestajiProzor izvestajiProzor = new IzvestajiProzor();
				izvestajiProzor.setVisible(true);
			}
		});
		
		// Otvaranje prozora sa grafikonima
		btnChartovi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StatistikaGraficiProzor graficiProzor = new StatistikaGraficiProzor();
				graficiProzor.setVisible(true);
			}
		});
		
		// Odjava sa sistema
		btnOdjava.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				osobaMenadzer.odjava();
				LoginForm login = new LoginForm(osobaMenadzer,voziloMenadzer, finansijeMenadzer,izdavanjeMenadzer, rezervacijeMenadzer);
				login.setVisible(true);
				dispose(); // Zatvara trenutni admin prozor
			}
		});
		
		// Centriraj prozor na ekranu
		setLocationRelativeTo(null);
	}
}