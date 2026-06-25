package gui.admin;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import menadzer.*;

public class CrudMeniProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private OsobaMenadzer osobaMenadzer;
	private VoziloMenadzer voziloMenadzer;
	private RezervacijeMenadzer rezervacijeMenadzer;
	private FinansijeMenadzer finansijeMenadzer;
	private IzdavanjeMenadzer izdavanjeMenadzer;

	public CrudMeniProzor(OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, 
			RezervacijeMenadzer rezervacijeMenadzer, FinansijeMenadzer finansijeMenadzer, 
			IzdavanjeMenadzer izdavanjeMenadzer) {
		
		this.osobaMenadzer = osobaMenadzer;
		this.voziloMenadzer = voziloMenadzer;
		this.rezervacijeMenadzer = rezervacijeMenadzer;
		this.finansijeMenadzer = finansijeMenadzer;
		this.izdavanjeMenadzer = izdavanjeMenadzer;
		
		setTitle("CRUD Upravljanje Podacima");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 650, 520); // Malo povećana visina radi boljeg rasporeda
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNaslov = new JLabel("Odaberite entitet za upravljanje");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(10, 15, 614, 25);
		contentPane.add(lblNaslov);


		JPanel panelKorisnici = new JPanel();
		panelKorisnici.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Korisnički Nalozi", TitledBorder.LEFT, TitledBorder.TOP));
		panelKorisnici.setBounds(20, 60, 280, 165);
		panelKorisnici.setLayout(null);
		contentPane.add(panelKorisnici);
		
		JButton btnAdmin = new JButton("Administratori");
		btnAdmin.setBounds(20, 25, 240, 30);
		panelKorisnici.add(btnAdmin);
		
		JButton btnAgent = new JButton("Agenti");
		btnAgent.setBounds(20, 70, 240, 30);
		panelKorisnici.add(btnAgent);
		
		JButton btnKlijent = new JButton("Klijenti");
		btnKlijent.setBounds(20, 115, 240, 30);
		panelKorisnici.add(btnKlijent);


		JPanel panelVozila = new JPanel();
		panelVozila.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Vozila", TitledBorder.LEFT, TitledBorder.TOP));
		panelVozila.setBounds(330, 60, 280, 165);
		panelVozila.setLayout(null);
		contentPane.add(panelVozila);
		
		JButton btnVozilo = new JButton("Vozila");
		btnVozilo.setBounds(20, 45, 240, 30);
		panelVozila.add(btnVozilo);
		
		JButton btnModelVozila = new JButton("Modeli Vozila");
		btnModelVozila.setBounds(20, 95, 240, 30);
		panelVozila.add(btnModelVozila);


		JPanel panelOperacije = new JPanel();
		panelOperacije.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Rezervacije i Izdavanja", TitledBorder.LEFT, TitledBorder.TOP));
		panelOperacije.setBounds(20, 240, 280, 165);
		panelOperacije.setLayout(null);
		contentPane.add(panelOperacije);
		
		JButton btnRezervacija = new JButton("Rezervacije");
		btnRezervacija.setBounds(20, 45, 240, 30);
		panelOperacije.add(btnRezervacija);
		
		JButton btnIzdavanje = new JButton("Izdavanje Vozila");
		btnIzdavanje.setBounds(20, 95, 240, 30);
		panelOperacije.add(btnIzdavanje);


		JPanel panelFinansije = new JPanel();
		panelFinansije.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Finansije", TitledBorder.LEFT, TitledBorder.TOP));
		panelFinansije.setBounds(330, 240, 280, 165);
		panelFinansije.setLayout(null);
		contentPane.add(panelFinansije);
		
		JButton btnPretplata = new JButton("Pretplate");
		btnPretplata.setBounds(20, 25, 240, 30);
		panelFinansije.add(btnPretplata);
		
		JButton btnCenovnik = new JButton("Cenovnici");
		btnCenovnik.setBounds(20, 70, 240, 30);
		panelFinansije.add(btnCenovnik);
		
		JButton btnDodatnaUsluga = new JButton("Dodatne Usluge");
		btnDodatnaUsluga.setBounds(20, 115, 240, 30);
		panelFinansije.add(btnDodatnaUsluga);

		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(510, 430, 100, 30);
		contentPane.add(btnNazad);
		
		// --- AKCIJE ---
		btnNazad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		btnAdmin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AdminCrudProzor adminCrud = new AdminCrudProzor(osobaMenadzer);
				adminCrud.setVisible(true);
			}
		});
		btnAgent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AgentCrudProzor agentCrud = new AgentCrudProzor(osobaMenadzer);
				agentCrud.setVisible(true);
			}
		});
		btnKlijent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				KlijentCrudProzor klijentCrud = new KlijentCrudProzor(osobaMenadzer);
				klijentCrud.setVisible(true);
			}
		});
		btnVozilo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VoziloCrudProzor voziloCrud = new VoziloCrudProzor(voziloMenadzer);
				voziloCrud.setVisible(true);
			}
		});
		btnModelVozila.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ModelVozilaCrudProzor modelCrud = new ModelVozilaCrudProzor(voziloMenadzer);
				modelCrud.setVisible(true);
			}
		});
		btnDodatnaUsluga.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DodatnaUslugaCrudProzor usluga = new DodatnaUslugaCrudProzor(rezervacijeMenadzer);
				usluga.setVisible(true);
			}
		});
		btnCenovnik.addActionListener(e -> {
			CenovnikCrudProzor cenovnikCrud = new CenovnikCrudProzor(finansijeMenadzer, rezervacijeMenadzer);
			cenovnikCrud.setVisible(true);
		});
		btnPretplata.addActionListener(e -> {
			PretplataCrudProzor pretplataCrud = new PretplataCrudProzor(finansijeMenadzer, osobaMenadzer);
			pretplataCrud.setVisible(true);
		});
		btnRezervacija.addActionListener(e -> {
			RezervacijaCrudProzor rezCrud = new RezervacijaCrudProzor(rezervacijeMenadzer, osobaMenadzer, voziloMenadzer, finansijeMenadzer);
			rezCrud.setVisible(true);
		});
		btnIzdavanje.addActionListener(e -> {
			IzdavanjeCrudProzor izdCrud = new IzdavanjeCrudProzor(izdavanjeMenadzer, osobaMenadzer, voziloMenadzer,  rezervacijeMenadzer);
			izdCrud.setVisible(true);
		});
		
		setLocationRelativeTo(null);
	}
}