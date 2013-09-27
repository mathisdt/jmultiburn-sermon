package org.zephyrsoft.jmultiburn.sermon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

public class MainWindow extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = -4438912327573399733L;
	
	public static int CD_LENGTH = 78;
	
	private static String SEPARATOR = "|";
	
	private JPanel liste = null;
	private List<Object> buttons = null;
	
	private BurnWindow burnWindow = null;
	
	public MainWindow() {
		super("Predigten brennen");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		
		File dir = new File(DB.getSermonsDir());
		File[] files = dir.listFiles();
		int fileListLength = files.length;
		liste = new JPanel(new SpringLayout());
		buttons = new LinkedList<Object>();
		
		if (files == null || fileListLength == 0) {
			liste.add(new JLabel("Keine MP3s zum Brennen vorhanden!"));
		} else {
			Arrays.sort(files);
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				// pro Predigt:
				int trennung = file.getName().lastIndexOf("-");
				if (trennung < 0) {
					fileListLength--;
					continue;
				}
				String vorname = file.getName().substring(0, trennung);
				String rate = file.getName().substring(trennung + 1, file.getName().lastIndexOf("."));
				String bettername;
				String rest = vorname.substring(11);
				if (rest.indexOf("-") > 0) {
					bettername =
						rest.substring(0, rest.indexOf("-")) + " (" + rest.substring(rest.indexOf("-") + 1) + ")";
				} else {
					bettername = rest;
				}
				bettername = replace(bettername, "_", " ");
				bettername = replace(bettername, "fruehstueck", "frühstück");
				bettername = replace(bettername, "Maenner", "Männer");
				bettername = replace(bettername, "Joerg", "Jörg");
				
				// Länge in Minuten und Sekunden berechnen
				double bitrate = Double.valueOf(rate.substring(0, 2));
				double filesize = file.length();
				double length = Math.floor(filesize / bitrate * 0.008);
				double min = Math.floor(length / 60);
				
				JLabel datelabel =
					new JLabel(vorname.substring(8, 10) + "." + vorname.substring(5, 7) + "." + vorname.substring(0, 4));
				JLabel namelabel = new JLabel(bettername);
				JLabel ratelabel = new JLabel("(" + rate + ")");
				datelabel.setFont(new Font("sansserif", Font.BOLD, 14));
				datelabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
				namelabel.setFont(new Font("sansserif", Font.BOLD, 14));
				namelabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
				ratelabel.setFont(new Font("sansserif", Font.BOLD, 14));
				ratelabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
				
				List<JButton> createdButtons = new ArrayList<JButton>();
				if (min < CD_LENGTH) {
					JButton button = new JButton("Brennen!");
					button.setActionCommand(file.getName());
					button.addActionListener(this);
					button.setBackground(Color.WHITE);
					button.setForeground(Color.BLACK);
					buttons.add(button);
					createdButtons.add(button);
				} else {
					// Spezialbehandlung für Predigten, die zu lang für eine CD sind
					int count = 1;
					while (min - ((count - 1) * CD_LENGTH) >= 0) {
						JButton button = new JButton("CD " + count);
						button.setActionCommand(file.getName() + SEPARATOR + count);
						button.addActionListener(this);
						button.setBackground(Color.WHITE);
						button.setForeground(Color.BLACK);
						buttons.add(button);
						createdButtons.add(button);
						count++;
					}
				}
				
				// Elemente hinzufügen
				JPanel datepanel = new JPanel();
				datepanel.setLayout(new BoxLayout(datepanel, BoxLayout.Y_AXIS));
				datepanel.add(datelabel);
				JPanel namepanel = new JPanel();
				namepanel.setLayout(new BoxLayout(namepanel, BoxLayout.Y_AXIS));
				namepanel.add(namelabel);
				JPanel buttonpanel = new JPanel();
				// buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.Y_AXIS));
				for (JButton button : createdButtons) {
					buttonpanel.add(button);
				}
				if (i % 2 == 1) {
					datepanel.setBackground(Color.lightGray);
					namepanel.setBackground(Color.lightGray);
					buttonpanel.setBackground(Color.lightGray);
				}
				liste.add(datepanel);
				liste.add(namepanel);
				liste.add(buttonpanel);
			}
			SpringUtilities.makeCompactGrid(liste, fileListLength, 3, // rows, cols
				0, 0, // initialX, initialY
				0, 0);// xPad, yPad
		}
		JScrollPane scroller = new JScrollPane(liste);
		scroller.getVerticalScrollBar().setUnitIncrement(20);
		setContentPane(scroller);
		pack();
		setSize(new Dimension(924, 668));
		setLocation(new Point(50, 50));
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setVisible(true);
		liste.scrollRectToVisible(new Rectangle(0, ((int) liste.getSize().getHeight()) - 1, 1, 1));
	}
	
	protected void exit() {
		// stelle sicher, dass es sicher ist, das Programm zu schließen
		if (burnWindow == null) {
			// schließe das Programm
			System.exit(0);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		// System.out.println("Predigt: " + ae.getActionCommand());
		// Buttons setzen
		for (int i = 0; i < buttons.size(); i++) {
			JButton button = (JButton) buttons.get(i);
			if (ae.getSource() == button) {
				button.setBackground(Color.green);
			}
			button.setEnabled(false);
		}
		// jetzt Brennfenster öffnen
		if (ae.getActionCommand().contains(SEPARATOR)) {
			StringTokenizer t = new StringTokenizer(ae.getActionCommand(), SEPARATOR);
			String fileName = t.nextToken();
			String part = t.nextToken();
			burnWindow = new BurnWindow("-s", DB.getSermonsDir() + fileName, part, DB.getBurners(), this);
		} else {
			// keine mehrteilige Predigt: Part "0" heißt "Predigt nicht aufteilen"
			burnWindow = new BurnWindow("-s", DB.getSermonsDir() + ae.getActionCommand(), "0", DB.getBurners(), this);
		}
	}
	
	public void closeBurnWindow() {
		for (int i = 0; i < buttons.size(); i++) {
			JButton button = (JButton) buttons.get(i);
			button.setBackground(Color.WHITE);
			button.setEnabled(true);
		}
		burnWindow.setVisible(false);
		burnWindow.dispose();
		burnWindow = null;
	}
	
	public static String replace(String in, String toreplace, String replacewith) {
		String ret = new String(in.toString());
		while (ret.indexOf(toreplace) >= 0) {
			ret =
				ret.substring(0, ret.indexOf(toreplace)) + replacewith
					+ ret.substring(ret.indexOf(toreplace) + toreplace.length());
		}
		return ret;
	}
	
}
