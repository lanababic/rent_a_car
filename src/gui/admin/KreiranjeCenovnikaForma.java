package gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import enums.KategorijaVozila;
import menadzer.FinansijeMenadzer;
import menadzer.RezervacijeMenadzer;

public class KreiranjeCenovnikaForma extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtPocetak, txtKraj, txtPretplata, txtKazna, txtPopust, txtDaniNajma, txtCenaProduzenog;
	
	private Map<String, Double> privremeneUsluge = new HashMap<>();
	// Nova mapa za čuvanje cena po kategorijama vozila
	private Map<KategorijaVozila, Double> ceneKategorija = new HashMap<>();

	public KreiranjeCenovnikaForma(FinansijeMenadzer finansijeMenadzer, RezervacijeMenadzer rezMen) {
		setTitle("Kreiranje Novog Cenovnika");
		setModal(true);
		setBounds(100, 100, 480, 420);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		JLabel lblNaslov = new JLabel("Unos podataka za novi cenovnik");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNaslov.setBounds(20, 11, 300, 25);
		getContentPane().add(lblNaslov);

		int y = 50;
		txtPocetak = kreirajPolje(y+=30);
		txtKraj = kreirajPolje(y+=30);
		txtPretplata = kreirajPolje(y+=30);
		txtKazna = kreirajPolje(y+=30);
		txtPopust = kreirajPolje(y+=30);
		txtDaniNajma = kreirajPolje(y+=30);
		txtCenaProduzenog = kreirajPolje(y+=30);

		String[] labele = {
			"Datum početka (YYYY-MM-DD):", "Datum kraja (YYYY-MM-DD):", 
			"Cena godišnje pretplate:", "Kazna za kašnjenje:", 
			"Popust za kategorije:", "Dani najma:", "Cena produženog dana:"
		};
		y = 50;
		for (String l : labele) {
			JLabel lbl = new JLabel(l);
			lbl.setBounds(20, y+=30, 220, 20);
			getContentPane().add(lbl);
		}

		// --- DUGME ZA DODATNE USLUGE ---
		JButton btnDodajUslugu = new JButton("Dodaj dodatnu uslugu (+)");
		btnDodajUslugu.setBackground(Color.ORANGE);
		btnDodajUslugu.setBounds(20, 320, 200, 30);
		getContentPane().add(btnDodajUslugu);

		btnDodajUslugu.addActionListener(e -> {
			String naziv = JOptionPane.showInputDialog(this, "Unesite naziv dodatne usluge:");
			if (naziv != null && !naziv.trim().isEmpty()) {
				String cenaStr = JOptionPane.showInputDialog(this, "Unesite cenu za uslugu '" + naziv + "':");
				try {
					double cena = Double.parseDouble(cenaStr);
					privremeneUsluge.put(naziv.trim(), cena);
					JOptionPane.showMessageDialog(this, "Usluga uspešno dodata! (Ukupno: " + privremeneUsluge.size() + ")");
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this, "Cena mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// --- GLAVNA DUGMAD ---
		JButton btnSacuvaj = new JButton("Sačuvaj");
		btnSacuvaj.setBackground(Color.GREEN);
		btnSacuvaj.setBounds(240, 320, 100, 30);
		getContentPane().add(btnSacuvaj);

		JButton btnOtkazi = new JButton("Otkaži");
		btnOtkazi.setBounds(350, 320, 100, 30);
		getContentPane().add(btnOtkazi);

		btnSacuvaj.addActionListener(e -> {
			try {
				// 1. Validacija osnovnih polja
				LocalDate pocetak = LocalDate.parse(txtPocetak.getText().trim());
				LocalDate kraj = LocalDate.parse(txtKraj.getText().trim());
				double pretplata = Double.parseDouble(txtPretplata.getText().trim());
				double kazna = Double.parseDouble(txtKazna.getText().trim());
				double popust = Double.parseDouble(txtPopust.getText().trim());
				int daniNajma = Integer.parseInt(txtDaniNajma.getText().trim());
				double cenaProduzenog = Double.parseDouble(txtCenaProduzenog.getText().trim());

				// 2. GUI unos cena najma za SVAKU kategoriju iz enuma
				ceneKategorija.clear(); // osiguravamo da je čista ako je kliknuo više puta
				for (KategorijaVozila k : KategorijaVozila.values()) {
					String unosCene = JOptionPane.showInputDialog(this, 
							"Unesite cenu najma za kategoriju: " + k.name(), 
							"Unos cene najma", JOptionPane.QUESTION_MESSAGE);
					
					if (unosCene == null) {
						JOptionPane.showMessageDialog(this, "Morate uneti cene za sve kategorije da biste sačuvali cenovnik!", "Prekinuto", JOptionPane.WARNING_MESSAGE);
						return; 
					}
					if (!validirajPopust(popust)) {
						JOptionPane.showMessageDialog(this, 
								"Popust mora biti vrednost između 0 i 1 (npr. 0.15 za 15%)!", 
								"Greška", JOptionPane.WARNING_MESSAGE);
						return; 
					}
					
					try {
						double cenaKat = Double.parseDouble(unosCene.trim());
						ceneKategorija.put(k, cenaKat);
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(this, "Cena za kategoriju " + k.name() + " mora biti broj! Pokušajte ponovo.", "Greška", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				// 3. Poziv izmenjene metode iz FinansijeMenadzer
				finansijeMenadzer.napraviCenovnik(pocetak, kraj, pretplata, kazna, popust, daniNajma, rezMen, cenaProduzenog, privremeneUsluge, ceneKategorija);
				
				JOptionPane.showMessageDialog(this, "Cenovnik sa svim cenama najma i uslugama je uspešno kreiran!");
				dispose();

			} catch (DateTimeParseException ex) {
				JOptionPane.showMessageDialog(this, "Format datuma mora biti YYYY-MM-DD!", "Greška", JOptionPane.ERROR_MESSAGE);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Sva polja za cene i dane moraju biti ispravni brojevi!", "Greška", JOptionPane.ERROR_MESSAGE);
			}
		});

		btnOtkazi.addActionListener(e -> dispose());
	}

	private JTextField kreirajPolje(int y) {
		JTextField tf = new JTextField();
		tf.setBounds(260, y, 160, 20);
		getContentPane().add(tf);
		return tf;
	}
	private boolean validirajPopust(double popust) {
		if (popust < 0 || popust > 1) {
			return false;
		}
		return true;
	}
}