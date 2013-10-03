package org.zephyrsoft.jmultiburn.sermon.ui;

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
import java.util.ArrayList;
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
import org.zephyrsoft.jmultiburn.sermon.DB;
import org.zephyrsoft.jmultiburn.sermon.SermonProvider;
import org.zephyrsoft.jmultiburn.sermon.model.Sermon;
import org.zephyrsoft.jmultiburn.sermon.model.SourceType;
import org.zephyrsoft.jmultiburn.sermon.ui.util.SpringUtilities;

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
		
		List<Sermon> sermons = SermonProvider.readSermons();
		liste = new JPanel(new SpringLayout());
		buttons = new LinkedList<Object>();
		
		if (sermons == null || sermons.size() == 0) {
			liste.add(new JLabel("Keine MP3s zum Brennen vorhanden!"));
		} else {
			int i = 0;
			for (Sermon sermon : sermons) {
				JLabel datelabel = new JLabel(sermon.getDate());
				JLabel namelabel = new JLabel(sermon.getName());
				datelabel.setFont(new Font("sansserif", Font.BOLD, 14));
				datelabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
				namelabel.setFont(new Font("sansserif", Font.BOLD, 14));
				namelabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
				
				List<JButton> createdButtons = new ArrayList<JButton>();
				if (sermon.getParts() == 1) {
					JButton button = new JButton("Brennen!");
					button.setActionCommand(sermon.getSource());
					button.addActionListener(this);
					button.setBackground(Color.WHITE);
					button.setForeground(Color.BLACK);
					buttons.add(button);
					createdButtons.add(button);
				} else {
					// more than one CD
					for (int count = 1; count <= sermon.getParts(); count++) {
						JButton button = new JButton("CD " + count);
						button.setActionCommand(sermon.getSource() + SEPARATOR + count);
						button.addActionListener(this);
						button.setBackground(Color.WHITE);
						button.setForeground(Color.BLACK);
						buttons.add(button);
						createdButtons.add(button);
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
				i++;
				liste.add(datepanel);
				liste.add(namepanel);
				liste.add(buttonpanel);
			}
			SpringUtilities.makeCompactGrid(liste, sermons.size(), 3, // rows, cols
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
			burnWindow = new BurnWindow(SourceType.SINGLE_FILE, fileName, part, DB.getBurners(), this);
		} else {
			// only one part: part number 0 means "don't cut in parts"
			burnWindow = new BurnWindow(SourceType.SINGLE_FILE, ae.getActionCommand(), "0", DB.getBurners(), this);
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
	
}
