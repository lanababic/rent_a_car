package gui.admin;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import menadzer.RezervacijeMenadzer;
import model.DodatnaUsluga;

public class PrikazDodatnihUslugaTabela extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabela;
	private DefaultTableModel tableModel;

	public PrikazDodatnihUslugaTabela(RezervacijeMenadzer rezervacijeMenadzer) {
		setTitle("Pregled Dodatnih Usluga");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 350);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 10));

		JLabel lblNaslov = new JLabel("Lista svih dodatnih usluga");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNaslov, BorderLayout.NORTH);

		String[] kolone = {"ID Usluge", "Naziv Dodatne Usluge"};
		
		tableModel = new DefaultTableModel(kolone, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tabela = new JTable(tableModel);
		
		// Punjenje tabele iz liste sveDodatneUsluge
		for (DodatnaUsluga u : rezervacijeMenadzer.getSveDodatneUsluge()) {
			Object[] red = {
				u.getIdDodatneUsluge(),
				u.getNaziv() // Ako se geter za naziv u klasi zove drugačije, prilagodi ga ovde
			};
			tableModel.addRow(red);
		}

		JScrollPane scrollPane = new JScrollPane(tabela);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		JPanel panelJug = new JPanel();
		JButton btnZatvori = new JButton("Zatvori");
		btnZatvori.addActionListener(e -> dispose());
		panelJug.add(btnZatvori);
		contentPane.add(panelJug, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
	}
}