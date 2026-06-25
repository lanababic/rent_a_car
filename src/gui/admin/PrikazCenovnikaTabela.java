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

import menadzer.FinansijeMenadzer;
import model.Cenovnik;

public class PrikazCenovnikaTabela extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabela;
	private DefaultTableModel tableModel;

	public PrikazCenovnikaTabela(FinansijeMenadzer finansijeMenadzer) {
		setTitle("Pregled svih cenovnika");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 400);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 10));

		JLabel lblNaslov = new JLabel("Lista aktivnih i sačuvanih cenovnika");
		lblNaslov.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNaslov.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNaslov, BorderLayout.NORTH);

		String[] kolone = {"ID", "Od", "Do", "Godišnja pretplata", "Kazna kašnjenja", "Popust kat.", "Dani najma"};
		
		tableModel = new DefaultTableModel(kolone, 0) {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tabela = new JTable(tableModel);
		
		// Provera getera (npr. finansijeMenadzer.getSviCenovnici())
		for (Cenovnik c : finansijeMenadzer.getSviCenovnici()) {
			Object[] red = {
				c.getIdCenovnika(),
				c.getDatumPocetka(),
				c.getDatumKraja(),
				c.getCenaGodisnjePretplate(),
				c.getKaznaZaKasnjenje(),
				c.getPopustZaKategorije(),
				c.getDaniNajma()
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