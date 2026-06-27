package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import gui.admin.AdminProzor;
import gui.agent.AgentProzor;
import gui.klijent.KlijentProzor;
import menadzer.FinansijeMenadzer;
import menadzer.IzdavanjeMenadzer;
import menadzer.OsobaMenadzer;
import menadzer.RezervacijeMenadzer;
import menadzer.VoziloMenadzer;
import model.Admin;
import model.Agent;
import model.Klijent;
import model.Osoba;

public class LoginForm extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private OsobaMenadzer osobaMenadzer;


	/**
	 * Create the frame.
	 */
	public LoginForm(OsobaMenadzer osobaMenadzer,VoziloMenadzer voziloMenadzer, FinansijeMenadzer finansijeMenadzer, IzdavanjeMenadzer izdavanjeMenadzer, RezervacijeMenadzer rezervacijeMenadzer) {
		setTitle("Log in");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.osobaMenadzer = osobaMenadzer;
		
		JButton btnNewButton = new JButton("Uloguj se");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    
			    String korisnickoIme = textField.getText().trim();
			    String lozinka = new String(passwordField.getPassword());

			    if (korisnickoIme.isEmpty() || lozinka.isEmpty()) {
			        JOptionPane.showMessageDialog(LoginForm.this, "Molimo unesite sva polja!", "Greška", JOptionPane.WARNING_MESSAGE);
			        return;
			    }
			    Osoba ulogovanaOsoba = osobaMenadzer.logIn(korisnickoIme, lozinka);

			    if (ulogovanaOsoba != null) {
			        JOptionPane.showMessageDialog(LoginForm.this, "Uspešna prijava! Dobrodošli, " + ulogovanaOsoba.getIme());
			        
			        if (ulogovanaOsoba instanceof Admin) {
			            AdminProzor adminProzor = new AdminProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer,  izdavanjeMenadzer, rezervacijeMenadzer);
			            adminProzor.setVisible(true);
			            LoginForm.this.dispose();
			        } else if (ulogovanaOsoba instanceof Agent) {
			            AgentProzor agentProzor = new AgentProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
			            agentProzor.setVisible(true);
			        } else if (ulogovanaOsoba instanceof Klijent) {
			            KlijentProzor klijentProzor = new KlijentProzor(osobaMenadzer, voziloMenadzer, finansijeMenadzer, izdavanjeMenadzer, rezervacijeMenadzer);
			            klijentProzor.setVisible(true);
			        }

			        // Zatvori login prozor
			        LoginForm.this.dispose();
			    } else {
			        // Prijava neuspešna
			        JOptionPane.showMessageDialog(LoginForm.this, "Neispravno korisničko ime ili lozinka!", "Greška pri prijavi", JOptionPane.ERROR_MESSAGE);
			    }
			}
		});
		btnNewButton.setForeground(Color.BLACK);
		btnNewButton.setBackground(Color.GREEN);
		btnNewButton.setBounds(157, 190, 115, 34);
		contentPane.add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(209, 33, 166, 34);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblKorisnickoIme = new JLabel("unesite korisnicko ime");
		lblKorisnickoIme.setBounds(46, 43, 153, 24);
		contentPane.add(lblKorisnickoIme);
		
		JLabel lblLozinka = new JLabel("unesite lozinku");
		lblLozinka.setBounds(46, 123, 102, 24);
		contentPane.add(lblLozinka);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(209, 111, 166, 34);
		contentPane.add(passwordField);

	}
}
