package main;

import menadzer.*;
import enums.*;
import model.*;
import gui.LoginForm;

public class Main {

	public static void main(String[] args) {
		VoziloMenadzer voziloMenadzer = new VoziloMenadzer();
		OsobaMenadzer osobaMenadzer = new OsobaMenadzer();
		RezervacijeMenadzer rezervacijeMenadzer = new RezervacijeMenadzer();
		FinansijeMenadzer finansijeMenadzer = new FinansijeMenadzer();
		IzdavanjeMenadzer izdavanjeMenadzer = new IzdavanjeMenadzer();
		
	    voziloMenadzer.ucitajPodatke();
	    osobaMenadzer.ucitajPodatke(); // pretplata privremeno null.
	    rezervacijeMenadzer.ucitajPodatke(voziloMenadzer, osobaMenadzer);
	    finansijeMenadzer.ucitajPodatke(osobaMenadzer, rezervacijeMenadzer); 
	    osobaMenadzer.poveziKlijenteSaPretplatama(finansijeMenadzer, osobaMenadzer.getPutanjaKlijeti());
	    izdavanjeMenadzer.ucitajPodatke(rezervacijeMenadzer, voziloMenadzer, osobaMenadzer);


		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginForm loginProzor = new LoginForm(osobaMenadzer,voziloMenadzer, finansijeMenadzer,izdavanjeMenadzer, rezervacijeMenadzer);
					loginProzor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
