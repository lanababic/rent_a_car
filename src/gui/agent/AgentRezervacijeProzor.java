package gui.agent;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import menadzer.FinansijeMenadzer;
import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import model.Rezervacija;
import enums.StatusRezervacije;

public class AgentRezervacijeProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JTextField txtIdRezervacije;
	private JLabel lblPodaciRezervacije;
	
	private JButton btnPotvrdi;
	private JButton btnOdbi;
	private JButton btnOdbiIstekle; // Ovo dugme je sada uvek dostupno

	private Rezervacija pronadjenaRezervacija = null;

	public AgentRezervacijeProzor(OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		setTitle("Upravljanje Rezervacijama");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Naslov
		JLabel lblNaslov = new JLabel("Obrada i Validacija Rezervacija");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(20, 15, 300, 25);
		contentPane.add(lblNaslov);
		
		// --- GLOBALNA METODA (UVEK DOSTUPNA) ---
		btnOdbiIstekle = new JButton("Odbi Sve Istekle Rezervacije u Sistemu");
		btnOdbiIstekle.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnOdbiIstekle.setBounds(60, 60, 360, 40);
		btnOdbiIstekle.setEnabled(true); // <--- UVEK DOSTUPNO!
		contentPane.add(btnOdbiIstekle);
		
		// --- POJEDINAČNA PRETRAGA ---
		JLabel lblIdRez = new JLabel("Unesite ID za pojedinačnu obradu:");
		lblIdRez.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblIdRez.setBounds(20, 130, 200, 25);
		contentPane.add(lblIdRez);
		
		txtIdRezervacije = new JTextField();
		txtIdRezervacije.setBounds(220, 130, 100, 25);
		contentPane.add(txtIdRezervacije);
		
		JButton btnPretrazi = new JButton("Pretraži");
		btnPretrazi.setBounds(335, 130, 100, 25);
		contentPane.add(btnPretrazi);
		
		lblPodaciRezervacije = new JLabel("Rezervacija: Nije izabrana");
		lblPodaciRezervacije.setFont(new Font("Tahoma", Font.ITALIC, 12));
		lblPodaciRezervacije.setBounds(20, 170, 440, 25);
		contentPane.add(lblPodaciRezervacije);
		
		// --- POJEDINAČNE AKCIJE ---
		btnPotvrdi = new JButton("Potvrdi Rezervaciju");
		btnPotvrdi.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnPotvrdi.setBounds(100, 210, 280, 40);
		btnPotvrdi.setEnabled(false); 
		contentPane.add(btnPotvrdi);
		
		btnOdbi = new JButton("Odbi Rezervaciju (Ručno)");
		btnOdbi.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnOdbi.setBounds(100, 265, 280, 40);
		btnOdbi.setEnabled(false);
		contentPane.add(btnOdbi);
		
		// Dugme Nazad
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(365, 335, 100, 30);
		contentPane.add(btnNazad);
		
		// --- LOGIKA I AKCIJE NA DUGMIĆIMA ---
		
		// AKCIJA: Odbi sve istekle rezervacije (Radi u pozadini nad celom listom)
		btnOdbiIstekle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Pretpostavka: tvoj rezervacijeMenadzer ima metodu getSveRezervacije() ili slično.
				// Prolazimo kroz sve rezervacije i čistimo one koje su prodate/zaboravljene na čekanju.
				ArrayList<Rezervacija> sveRezervacije = rezervacijeMenadzer.getSveRezervacije(); 
				
				if (sveRezervacije != null && !sveRezervacije.isEmpty()) {
					for (Rezervacija r : new ArrayList<>(sveRezervacije)) { 
						rezervacijeMenadzer.odbiIstekleRezervacije(r);
					}
					JOptionPane.showMessageDialog(null, "Sistem je uspešno proverio i odbio sve istekle rezervacije!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "Nema rezervacija u sistemu za proveru.", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
				}
				resetujProzor();
			}
		});
		
		// Akcija za pretragu pojedinačne rezervacije
		btnPretrazi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int idRez = Integer.parseInt(txtIdRezervacije.getText().trim());
					pronadjenaRezervacija = rezervacijeMenadzer.pronadjiRezervacijuPoId(idRez);
					
					if (pronadjenaRezervacija != null) {
						lblPodaciRezervacije.setText("Model: " + pronadjenaRezervacija.getModelVozila() + 
								" | Trenutni Status: " + pronadjenaRezervacija.getStatus());
						
						btnPotvrdi.setEnabled(true);
						btnOdbi.setEnabled(true);
					} else {
						JOptionPane.showMessageDialog(null, "Rezervacija sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						resetujProzor();
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "ID Rezervacije mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// Akcija: Potvrdi pojedinačnu rezervaciju
		btnPotvrdi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean imaVozila = izdavanjeMenadzer.isModelDostupan(
						pronadjenaRezervacija.getModelVozila(), 
						pronadjenaRezervacija.getDatumOd(), 
						pronadjenaRezervacija.getDatumDo(), 
						voziloMenadzer
				);
				
				if (!pronadjenaRezervacija.getStatus().equals(StatusRezervacije.NA_CEKANJU)) {
					JOptionPane.showMessageDialog(null, "Rezervacija nije u statusu 'NA ČEKANJU'!", "Upozorenje", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if (!imaVozila) {
					JOptionPane.showMessageDialog(null, "Nema dostupnih vozila za taj period!", "Nema slobodnih vozila", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				rezervacijeMenadzer.potvrdiRezervaciju(pronadjenaRezervacija, voziloMenadzer, izdavanjeMenadzer);
				JOptionPane.showMessageDialog(null, "Rezervacija je uspešno POTVRĐENA!");
				
				resetujProzor();
				txtIdRezervacije.setText("");
			}
		});
		
		// Akcija: Odbi pojedinačnu rezervaciju (Ručno)
		btnOdbi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!pronadjenaRezervacija.getStatus().equals(StatusRezervacije.NA_CEKANJU)) {
					JOptionPane.showMessageDialog(null, "Rezervacija nije u statusu 'NA ČEKANJU'!", "Upozorenje", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				rezervacijeMenadzer.odbiRezervaciju(pronadjenaRezervacija);
				JOptionPane.showMessageDialog(null, "Rezervacija je uspešno ODBIJENA.");
				
				resetujProzor();
				txtIdRezervacije.setText("");
			}
		});
		
		// Akcija za povratak nazad
		btnNazad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AgentProzor ap = new AgentProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				ap.setVisible(true);
				dispose();
			}
		});
		
		setLocationRelativeTo(null);
	}
	
	private void resetujProzor() {
		lblPodaciRezervacije.setText("Rezervacija: Nije izabrana");
		btnPotvrdi.setEnabled(false);
		btnOdbi.setEnabled(false);
		pronadjenaRezervacija = null;
	}
}