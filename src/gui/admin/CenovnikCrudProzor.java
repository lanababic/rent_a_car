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

import menadzer.FinansijeMenadzer;
import menadzer.RezervacijeMenadzer;
import model.Cenovnik;

public class CenovnikCrudProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private FinansijeMenadzer finansijeMenadzer;
	private RezervacijeMenadzer rezervacijeMenadzer;

	public CenovnikCrudProzor(FinansijeMenadzer finansijeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		this.finansijeMenadzer = finansijeMenadzer;
		this.rezervacijeMenadzer = rezervacijeMenadzer;
		
		setTitle("Upravljanje Cenovnicima");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Povećana visina prozora sa 330 na 380 da bi stalo novo dugme
		setBounds(100, 100, 400, 380); 
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNaslov = new JLabel("Opcije za Cenovnike");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(10, 20, 364, 25);
		contentPane.add(lblNaslov);
		
		JButton btnKreiraj = new JButton("Kreiraj novi cenovnik");
		btnKreiraj.setBounds(60, 70, 260, 35);
		contentPane.add(btnKreiraj);
		
		// NOVO DUGME: Izmeni cenovnik
		JButton btnIzmeni = new JButton("Izmeni cenovnik");
		btnIzmeni.setBounds(60, 120, 260, 35);
		contentPane.add(btnIzmeni);
		
		// Pomerene Y koordinate za ostalu dugmad naniže za po 50px
		JButton btnObrisi = new JButton("Obriši cenovnik");
		btnObrisi.setBounds(60, 170, 260, 35);
		contentPane.add(btnObrisi);
		
		JButton btnPrikaziSve = new JButton("Prikaži sve cenovnike");
		btnPrikaziSve.setBounds(60, 220, 260, 35);
		contentPane.add(btnPrikaziSve);
		
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(140, 290, 100, 30);
		contentPane.add(btnNazad);
		
		// --- AKCIJE ---

		btnKreiraj.addActionListener(e -> {
			KreiranjeCenovnikaForma forma = new KreiranjeCenovnikaForma(finansijeMenadzer, rezervacijeMenadzer);
			forma.setVisible(true);
		});
		
		// AKCIJA ZA IZMENU: Traži se ID, proverava postojanje i otvara se forma za izmenu
		btnIzmeni.addActionListener(e -> {
			String idStr = JOptionPane.showInputDialog(CenovnikCrudProzor.this, 
					"Unesite ID cenovnika kojeg menjate:", "Izmena cenovnika", JOptionPane.QUESTION_MESSAGE);
			
			if (idStr != null && !idStr.trim().isEmpty()) {
				try {
					int idCenovnika = Integer.parseInt(idStr.trim());
					
					// Pronalaženje cenovnika u listi preko ID-ja
					Cenovnik cenovnikZaIzmenu = finansijeMenadzer.getSviCenovnici().stream()
							.filter(c -> c.getIdCenovnika() == idCenovnika)
							.findFirst()
							.orElse(null);
					
					if (cenovnikZaIzmenu != null) {
						// Pretpostavka je da se klasa zove IzmenaCenovnikaForma i da prima menadžere i objekat cenovnika
						IzmenaCenovnikaForma forma = new IzmenaCenovnikaForma(finansijeMenadzer, rezervacijeMenadzer, cenovnikZaIzmenu);
						forma.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(CenovnikCrudProzor.this, 
								"Cenovnik sa unetim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(CenovnikCrudProzor.this, 
							"ID mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		btnObrisi.addActionListener(e -> {
			String idStr = JOptionPane.showInputDialog(CenovnikCrudProzor.this, 
					"Unesite ID cenovnika za brisanje:", "Brisanje cenovnika", JOptionPane.WARNING_MESSAGE);
			
			if (idStr != null && !idStr.trim().isEmpty()) {
				try {
					int idCenovnika = Integer.parseInt(idStr.trim());
					boolean postoji = finansijeMenadzer.getSviCenovnici().stream()
							.anyMatch(c -> c.getIdCenovnika() == idCenovnika);
					
					if (postoji) {
						int p = JOptionPane.showConfirmDialog(CenovnikCrudProzor.this, 
								"Da li ste sigurni da želite da obrišete cenovnik ID: " + idCenovnika + "?", 
								"Potvrda", JOptionPane.YES_NO_OPTION);
						if (p == JOptionPane.YES_OPTION) {
							finansijeMenadzer.obrisiCenovnik(idCenovnika);
							JOptionPane.showMessageDialog(CenovnikCrudProzor.this, "Cenovnik uspešno obrisan.");
						}
					} else {
						JOptionPane.showMessageDialog(CenovnikCrudProzor.this, "Cenovnik sa unetim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(CenovnikCrudProzor.this, "ID mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		btnPrikaziSve.addActionListener(e -> {
			PrikazCenovnikaTabela tabela = new PrikazCenovnikaTabela(finansijeMenadzer);
			tabela.setVisible(true);
		});
		
		btnNazad.addActionListener(e -> dispose());
		setLocationRelativeTo(null);
	}
}