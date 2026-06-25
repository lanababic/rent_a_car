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

import menadzer.OsobaMenadzer;
import model.Admin;
import model.Agent;

public class AgentCrudProzor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private OsobaMenadzer osobaMenadzer;

	public AgentCrudProzor(OsobaMenadzer osobaMenadzer) {
		this.osobaMenadzer = osobaMenadzer;
		
		setTitle("Upravljanje Agentima");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 380);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNaslov = new JLabel("Opcije za Agente");
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setBounds(10, 20, 364, 25);
		contentPane.add(lblNaslov);
		
		JButton btnRegistruj = new JButton("Registruj novog agenta");
		btnRegistruj.setBounds(60, 70, 260, 35);
		contentPane.add(btnRegistruj);
		
		JButton btnIzmeni = new JButton("Izmeni agenta");
		btnIzmeni.setBounds(60, 120, 260, 35);
		contentPane.add(btnIzmeni);
		
		JButton btnObrisi = new JButton("Obriši agenta");
		btnObrisi.setBounds(60, 170, 260, 35);
		contentPane.add(btnObrisi);
		
		JButton btnPrikaziSve = new JButton("Prikaži sve agente");
		btnPrikaziSve.setBounds(60, 220, 260, 35);
		contentPane.add(btnPrikaziSve);
		
		JButton btnNazad = new JButton("Nazad");
		btnNazad.setBounds(140, 290, 100, 30);
		contentPane.add(btnNazad);
		
		
		btnRegistruj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegistracijaAgentaForma forma = new RegistracijaAgentaForma(osobaMenadzer);
				forma.setVisible(true);
			}
		});
		
		btnIzmeni.addActionListener(e -> {
			String korIme = JOptionPane.showInputDialog(AgentCrudProzor.this, 
					"Unesite korisničko ime agenta kojeg menjate:", 
					"Izmena agenta", JOptionPane.QUESTION_MESSAGE);
			
			if (korIme != null && !korIme.trim().isEmpty()) {
				korIme = korIme.trim();
				Agent agentZaIzmenu = osobaMenadzer.pronadjiAgentaPoKorisnickomImenu(korIme); 
				
				if (agentZaIzmenu != null) {
					IzmenaAgentaForma forma = new IzmenaAgentaForma(osobaMenadzer, agentZaIzmenu);
					forma.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(AgentCrudProzor.this, 
							"Agent sa tim korisničkim imenom ne postoji!", "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		btnObrisi.addActionListener(e -> {
			String korIme = JOptionPane.showInputDialog(this, "Unesite korisničko ime agenta za brisanje:");
			if (korIme != null && !korIme.trim().isEmpty()) {
				int p = JOptionPane.showConfirmDialog(this, "Sigurno brisanje?", "Potvrda", JOptionPane.YES_NO_OPTION);
				if (p == JOptionPane.YES_OPTION) {
					osobaMenadzer.obrisiAgenta(korIme.trim());
				}
			}
		});
		
		btnPrikaziSve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrikazAgentaTabela prikazProzor = new PrikazAgentaTabela(osobaMenadzer);
				prikazProzor.setVisible(true);
			}
		});
		
		btnNazad.addActionListener(e -> dispose());
		setLocationRelativeTo(null);
	}
}