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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import enums.StatusRezervacije;
import gui.admin.PrikazDodatnihUslugaTabela;
import menadzer.FinansijeMenadzer;
import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import model.Rezervacija;
import model.Vozilo;
// Pretpostavka da imaš model za Dodatnu Uslugu, uvezi ako je potrebno:
// import model.DodatnaUsluga; 

public class NovaIzdavanjaProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JTextField txtIdRezervacije;
	private JTextField txtIdVozila;
	private JTextField txtIdUsluge; // Novo polje za ID dodatne usluge
	private JTable tabelaVozila;
	private DefaultTableModel tableModel;
	
	private JButton btnDodajUslugu; // Izvučeno kao polje da bismo menjali setEnabled
	private JButton btnPokreniIzdavanje;
	
	// Čuvanje tekuće rezervacije nakon što je agent pronađe
	private Rezervacija pronadjenaRezervacija = null;

	public NovaIzdavanjaProzor(OsobaMenadzer osobaMenadzer, VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		setTitle("Nova Izdavanja - Pokretanje");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 560); // Malo povećana visina prozora zbog novih elemenata
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// --- 1. KORAK: UNOS REZERVACIJE ---
		JLabel lblIdRezervacije = new JLabel("Unesite ID Rezervacije:");
		lblIdRezervacije.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblIdRezervacije.setBounds(20, 20, 140, 25);
		contentPane.add(lblIdRezervacije);
		
		txtIdRezervacije = new JTextField();
		txtIdRezervacije.setBounds(160, 20, 120, 25);
		contentPane.add(txtIdRezervacije);
		
		JButton btnPretraziRezervaciju = new JButton("Pretraži");
		btnPretraziRezervaciju.setBounds(290, 20, 100, 25);
		contentPane.add(btnPretraziRezervaciju);
		
		// --- Tabela za prikaz mogućih vozila ---
		JLabel lblTabelaNaslov = new JLabel("Dostupna vozila za model iz rezervacije:");
		lblTabelaNaslov.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblTabelaNaslov.setBounds(20, 65, 300, 20);
		contentPane.add(lblTabelaNaslov);
		
		String[] kolone = {"ID Vozila", "Model / Marka", "Registracija", "Kilometraža", "Status"};
		tableModel = new DefaultTableModel(kolone, 0);
		tabelaVozila = new JTable(tableModel);
		
		JScrollPane scrollPane = new JScrollPane(tabelaVozila);
		scrollPane.setBounds(20, 90, 545, 210);
		contentPane.add(scrollPane);
		
		// --- NOVO: SEKCIJA ZA DODATNE USLUGE ---
		JLabel lblIdUsluge = new JLabel("ID Dodatne Usluge:");
		lblIdUsluge.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblIdUsluge.setBounds(20, 315, 140, 25);
		contentPane.add(lblIdUsluge);
		
		txtIdUsluge = new JTextField();
		txtIdUsluge.setBounds(160, 315, 120, 25);
		txtIdUsluge.setEnabled(false); // Zaključano dok se ne nađe rezervacija
		contentPane.add(txtIdUsluge);
		
		btnDodajUslugu = new JButton("Dodaj Dodatnu Uslugu");
		btnDodajUslugu.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnDodajUslugu.setBounds(290, 315, 200, 25);
		btnDodajUslugu.setEnabled(false); // Zaključano dok se ne nađe rezervacija
		contentPane.add(btnDodajUslugu);
		
		// --- 2. KORAK: IZBOR VOZILA I IZDAVANJE ---
		JLabel lblIdVozila = new JLabel("Unesite ID Vozila za izdavanje:");
		lblIdVozila.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblIdVozila.setBounds(20, 380, 180, 25);
		contentPane.add(lblIdVozila);
		
		txtIdVozila = new JTextField();
		txtIdVozila.setBounds(200, 380, 100, 25);
		txtIdVozila.setEnabled(false);
		contentPane.add(txtIdVozila);
		
		btnPokreniIzdavanje = new JButton("Pokreni Izdavanje");
		btnPokreniIzdavanje.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPokreniIzdavanje.setBounds(320, 380, 160, 25);
		btnPokreniIzdavanje.setEnabled(false);
		contentPane.add(btnPokreniIzdavanje);
		
		// Dugme Nazad
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(465, 475, 100, 30);
		contentPane.add(btnNazad);
		
		// --- LOGIKA I AKCIJE ---
		
		// 1. Akcija za pretragu rezervacije
		btnPretraziRezervaciju.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int idRez = Integer.parseInt(txtIdRezervacije.getText().trim());
					pronadjenaRezervacija = rezervacijeMenadzer.pronadjiRezervacijuPoId(idRez);
					
					
					if (pronadjenaRezervacija != null) {
						if(!pronadjenaRezervacija.getStatus().equals(StatusRezervacije.POTVRDJENO)) {
							JOptionPane.showMessageDialog(null, "Rezervacija nije potvrdjena!");
						}
						else if(izdavanjeMenadzer.imaliIzdajuTaRezervacija(pronadjenaRezervacija)) {
							JOptionPane.showMessageDialog(null, "Rezervacija je vec izdata");
						}
						else {
							JOptionPane.showMessageDialog(null, "Rezervacija pronađena!");
							tableModel.setRowCount(0);
							
							ArrayList<Vozilo> mogucaVozila = listaMogucihVozila(pronadjenaRezervacija, voziloMenadzer);
							
							for (Vozilo v : mogucaVozila) {
								Object[] red = {
									v.getIdVozila(),
									v.getModelVozila(),
									v.getRegistracijaVozila(),
									v.getTrenutnaKilometraza(),
									v.getStatus()
								};
								tableModel.addRow(red);
							}
							
							// Otključaj sve komponente jer imamo aktivnu rezervaciju
							txtIdVozila.setEnabled(true);
							btnPokreniIzdavanje.setEnabled(true);
							txtIdUsluge.setEnabled(true);
							btnDodajUslugu.setEnabled(true);
						}
						
					} else {
						JOptionPane.showMessageDialog(null, "Rezervacija sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
						resetujFormu();
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "ID Rezervacije mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// NOVO: 2. Akcija za dodavanje dodatne usluge
		btnDodajUslugu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Svaki put kada se klikne, prvo otvara tabelu prikaza dodatnih usluga koju već imaš spremljenu
				PrikazDodatnihUslugaTabela prikazUsluga = new PrikazDodatnihUslugaTabela(rezervacijeMenadzer);
				prikazUsluga.setVisible(true);
				
				try {
					String unosIdUsluge = txtIdUsluge.getText().trim();
					if(unosIdUsluge.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Molimo unesite ID usluge iz otvorene tabele.", "Obaveštenje", JOptionPane.WARNING_MESSAGE);
						return;
					}
					
					int idUsluge = Integer.parseInt(unosIdUsluge);
					
					// Poziv tvoje metode
					dodajJosJednuDodatnuUslugu(idUsluge, pronadjenaRezervacija, rezervacijeMenadzer);
					
					JOptionPane.showMessageDialog(null, "Dodatna usluga uspešno dodata u rezervaciju!");
					txtIdUsluge.setText(""); // Očisti polje za sledeći unos ako zatreba
					
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "ID Usluge mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// 3. Akcija za pokretanje izdavanja
		btnPokreniIzdavanje.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int idVozila = Integer.parseInt(txtIdVozila.getText().trim());
					boolean voziloJeUListi = false;
					ArrayList<Vozilo> mogucaVozila = listaMogucihVozila(pronadjenaRezervacija, voziloMenadzer);
					Vozilo izabranoVozilo = null;
					
					for (Vozilo v : mogucaVozila) {
						if (v.getIdVozila() == idVozila) {
							voziloJeUListi = true;
							izabranoVozilo = v;
							break;
						}
					}//ovo da nisi uneo neki random id vozila koje postoji a nije ga trenutno moguce jer nije dostupno
					
					if (voziloJeUListi && izabranoVozilo != null) {
						
						// Poziv tvoje metode iz izdavanjeMenadzer sa svim potrebnim parametrima
						izdavanjeMenadzer.IzdavanjeVozilaPrviDeo(
							pronadjenaRezervacija, 
							voziloMenadzer, 
							osobaMenadzer, 
							rezervacijeMenadzer, 
							finansijeMenadzer, 
							izabranoVozilo, 
							mogucaVozila
						);
						
						// Obaveštenje korisniku o uspešnom ishodu
						JOptionPane.showMessageDialog(
							null, 
							"Uspešno pokrenuto izdavanje!\n" +
							"Vozilo: " + izabranoVozilo.getModelVozila() + " (" + izabranoVozilo.getRegistracijaVozila() + ")\n" +
							"Status vozila je promenjen u IZDATO.", 
							"Uspeh", 
							JOptionPane.INFORMATION_MESSAGE
						);
						
						// Resetovanje forme za sledeći unos
						resetujFormu();
						
					} else {
						JOptionPane.showMessageDialog(null, "Izabrani ID vozila nije na listi!", "Greška", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "ID Vozila mora biti broj!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// Akcija za Nazad
		btnNazad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IznajmljivanjeProzor ip = new IznajmljivanjeProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
				ip.setVisible(true);
				dispose();
			}
		});
		
		setLocationRelativeTo(null);
	}
	
	// Tvoja metoda za dodavanje dodatne usluge
	public void dodajJosJednuDodatnuUslugu(int idDodatneUsluge, Rezervacija rezervacija, RezervacijeMenadzer rezMen) {
		// Pretpostavka: unutar rezMen metoda vraća objekat tipa DodatnaUsluga
		model.DodatnaUsluga du = rezMen.pronadjiUsluguPoId(idDodatneUsluge); 
		if (du != null) {
			rezervacija.dodajDodatnuUslugu(du);
		} else {
			JOptionPane.showMessageDialog(null, "Usluga sa tim ID-jem ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public ArrayList<Vozilo> listaMogucihVozila(Rezervacija rezervacija, VoziloMenadzer vozMen) {
		return vozMen.pronadjiVozilaPoModelu(rezervacija.getModelVozila());
	}
	
	private void resetujFormu() {
		tableModel.setRowCount(0);
		txtIdVozila.setText("");
		txtIdVozila.setEnabled(false);
		txtIdUsluge.setText("");
		txtIdUsluge.setEnabled(false);
		btnPokreniIzdavanje.setEnabled(false);
		btnDodajUslugu.setEnabled(false);
		pronadjenaRezervacija = null;
	}
}