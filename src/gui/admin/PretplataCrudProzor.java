package gui.admin;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import menadzer.FinansijeMenadzer;
import menadzer.OsobaMenadzer;
import model.Pretplata;
import model.Klijent;
import enums.ZahtevPretplate;

public class PretplataCrudProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private FinansijeMenadzer finansijeMenadzer;
	private OsobaMenadzer osobaMenadzer;

	public PretplataCrudProzor(FinansijeMenadzer finansijeMenadzer, OsobaMenadzer osobaMenadzer) {
		this.finansijeMenadzer = finansijeMenadzer;
		this.osobaMenadzer = osobaMenadzer;
		
		setTitle("Upravljanje Pretplatama");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Visina prozora je postavljena na 430 da bi komotno stalo 5 dugmadi + Nazad
		setBounds(100, 100, 400, 430); 
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNaslov = new JLabel("Opcije za Pretplate");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(10, 20, 364, 25);
		contentPane.add(lblNaslov);
		
		// 1. KREIRAJ
		JButton btnKreiraj = new JButton("Kreiraj novu pretplatu");
		btnKreiraj.setBounds(60, 70, 260, 35);
		contentPane.add(btnKreiraj);
		
		// 2. IZMENI
		JButton btnIzmeni = new JButton("Izmeni pretplatu");
		btnIzmeni.setBounds(60, 120, 260, 35);
		contentPane.add(btnIzmeni);
		
		// 3. PRODUŽI (Novo specifično dugme)
		JButton btnProduzi = new JButton("Produži pretplatu");
		btnProduzi.setBounds(60, 170, 260, 35);
		contentPane.add(btnProduzi);
		
		// 4. OBRIŠI
		JButton btnObrisi = new JButton("Obriši pretplatu");
		btnObrisi.setBounds(60, 220, 260, 35);
		contentPane.add(btnObrisi);
		
		// 5. PRIKAŽI SVE
		JButton btnPrikaziSve = new JButton("Prikaži sve pretplate");
		btnPrikaziSve.setBounds(60, 270, 260, 35);
		contentPane.add(btnPrikaziSve);
		
		// NAZAD
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(140, 340, 100, 30);
		contentPane.add(btnNazad);
		
		// --- AKCIJE ---

		// 1. Akcija za Kreiranje
		btnKreiraj.addActionListener(e -> {
			// Otvara formu gde biraš klijenta koji ima ODOBREN zahtev
			KreiranjePretplateForma forma = new KreiranjePretplateForma(finansijeMenadzer, osobaMenadzer);
			forma.setVisible(true);
		});
		
		// 2. Akcija za Izmenu
		btnIzmeni.addActionListener(e -> {
			String idStr = JOptionPane.showInputDialog(PretplataCrudProzor.this, 
					"Unesite ID pretplate za izmenu:", "Izmena pretplate", JOptionPane.QUESTION_MESSAGE);
			
			if (idStr != null && !idStr.trim().isEmpty()) {
				try {
					int idPretplate = Integer.parseInt(idStr.trim());
					Pretplata pretplataZaIzmenu = finansijeMenadzer.getSvePretplate().stream()
							.filter(p -> p.getIdPretplate() == idPretplate)
							.findFirst()
							.orElse(null);
					
					if (pretplataZaIzmenu != null) {
						IzmenaPretplataForma forma = new IzmenaPretplataForma(finansijeMenadzer, osobaMenadzer, pretplataZaIzmenu);
						forma.setVisible(true);
					} else {
						JOptionPane.showMessageDialog(PretplataCrudProzor.this, "Pretplata sa unetim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(PretplataCrudProzor.this, "ID mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// 3. Akcija za Produžavanje (Ispisuje GUI greške na osnovu tvoje logike)
		btnProduzi.addActionListener(e -> {
			String idStr = JOptionPane.showInputDialog(PretplataCrudProzor.this, 
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
							int potvrda = JOptionPane.showConfirmDialog(PretplataCrudProzor.this, 
									"Da li ste sigurni da želite da produžite pretplatu za klijenta: " + k.getIme() + " " + k.getPrezime() + "?", 
									"Potvrda", JOptionPane.YES_NO_OPTION);
							
							if (potvrda == JOptionPane.YES_OPTION) {
								finansijeMenadzer.produziPretplatu(p, osobaMenadzer);
								JOptionPane.showMessageDialog(PretplataCrudProzor.this, "Pretplata uspešno produžena za godinu dana.");
							}
						} else {
							// GUI ispis greške iz tvoje else grane
							JOptionPane.showMessageDialog(PretplataCrudProzor.this, 
									"Nemoguće produžiti pretplatu!\nKlijent ima više od 5 kašnjenja ili zahtev nije odobren.", 
									"Greška pri produžavanju", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(PretplataCrudProzor.this, "Pretplata sa unetim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(PretplataCrudProzor.this, "ID mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// 4. Akcija za Brisanje
		btnObrisi.addActionListener(e -> {
			String idStr = JOptionPane.showInputDialog(PretplataCrudProzor.this, 
					"Unesite ID pretplate za brisanje:", "Brisanje pretplate", JOptionPane.WARNING_MESSAGE);
			
			if (idStr != null && !idStr.trim().isEmpty()) {
				try {
					int idPretplate = Integer.parseInt(idStr.trim());
					boolean postoji = finansijeMenadzer.getSvePretplate().stream()
							.anyMatch(p -> p.getIdPretplate() == idPretplate);
					
					if (postoji) {
						int p = JOptionPane.showConfirmDialog(PretplataCrudProzor.this, 
								"Da li ste sigurni da želite da obrišete pretplatu ID: " + idPretplate + "?", 
								"Potvrda", JOptionPane.YES_NO_OPTION);
						if (p == JOptionPane.YES_OPTION) {
							finansijeMenadzer.obrisiPretplatu(idPretplate);
							JOptionPane.showMessageDialog(PretplataCrudProzor.this, "Pretplata uspešno obrisana.");
						}
					} else {
						JOptionPane.showMessageDialog(PretplataCrudProzor.this, "Pretplata sa unetim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(PretplataCrudProzor.this, "ID mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// 5. Akcija za Prikaz svih
		btnPrikaziSve.addActionListener(e -> {
			PrikazPretplataTabela tabela = new PrikazPretplataTabela(finansijeMenadzer);
			tabela.setVisible(true);
		});
		
		btnNazad.addActionListener(e -> dispose());
		setLocationRelativeTo(null);
	}
}