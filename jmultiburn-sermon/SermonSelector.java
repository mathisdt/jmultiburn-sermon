import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

public class SermonSelector extends JFrame implements ActionListener {

	private JPanel liste = null;
	private Vector buttons = null;
	
	private BurnWindow burnWindow = null;
	
	public SermonSelector() {
		super("Predigten brennen");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					exit();
				}
			}
		);
		
		File dir = new File(DB.getSermonsDir());
		File[] files = dir.listFiles();
		liste = new JPanel(new SpringLayout());
		buttons = new Vector();
		
		if (files == null || files.length == 0) {
			liste.add(new JLabel("Keine MP3s zum Brennen vorhanden!"));
		} else {
			Arrays.sort(files);
			Vector filevector = new Vector(Arrays.asList(files));
			for (int i = 0; i < filevector.size(); i++) {
				// pro Predigt:
				int trennung = ((File)filevector.elementAt(i)).getName().lastIndexOf("-");
				String vorname = ((File)filevector.elementAt(i)).getName().substring(0, trennung);
				String rate = ((File)filevector.elementAt(i)).getName().substring(trennung+1, ((File)filevector.elementAt(i)).getName().lastIndexOf("."));
				String bettername;
				String rest = vorname.substring(11);
				if (rest.indexOf("-")>0) {
					bettername = rest.substring(0,rest.indexOf("-")) + " (" + rest.substring(rest.indexOf("-")+1) + ")";
				} else {
					bettername = rest;
				}
				bettername = replace(bettername, "_", " ");
				bettername = replace(bettername, "fruehstueck", "frühstück");
				bettername = replace(bettername, "Maenner", "Männer");
				bettername = replace(bettername, "Joerg", "Jörg");
				JLabel datelabel = new JLabel(vorname.substring(8,10)+"."+vorname.substring(5,7)+"."+vorname.substring(0,4));
				JLabel namelabel = new JLabel(bettername);
				JLabel ratelabel = new JLabel("(" + rate + ")");
				datelabel.setFont(new Font("sansserif", Font.BOLD, 14));
				datelabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
				namelabel.setFont(new Font("sansserif", Font.BOLD, 14));
				namelabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
				ratelabel.setFont(new Font("sansserif", Font.BOLD, 14));
				ratelabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
				
				JButton button = new JButton("Brennen!");
				button.setActionCommand(((File)filevector.elementAt(i)).getName());
				button.addActionListener(this);
				button.setBackground(Color.WHITE);
				button.setForeground(Color.BLACK);
				buttons.add(button);
				
				// Elemente hinzufügen
				JPanel datepanel = new JPanel();
				datepanel.setLayout(new BoxLayout(datepanel, BoxLayout.Y_AXIS));
				datepanel.add(datelabel);
				JPanel namepanel = new JPanel();
				namepanel.setLayout(new BoxLayout(namepanel, BoxLayout.Y_AXIS));
				namepanel.add(namelabel);
				JPanel ratepanel = new JPanel();
				ratepanel.setLayout(new BoxLayout(ratepanel, BoxLayout.Y_AXIS));
				ratepanel.add(ratelabel);
				JPanel buttonpanel = new JPanel();
				//buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.Y_AXIS));
				buttonpanel.add(button);
				if (i % 2 == 1) {
					datepanel.setBackground(Color.lightGray);
					namepanel.setBackground(Color.lightGray);
					ratepanel.setBackground(Color.lightGray);
					buttonpanel.setBackground(Color.lightGray);
				}
				liste.add(datepanel);
				liste.add(namepanel);
				liste.add(ratepanel);
				liste.add(buttonpanel);
			}
			SpringUtilities.makeCompactGrid(liste,
                    files.length, 4, //rows, cols
                    0, 0, //initialX, initialY
                    0, 0);//xPad, yPad
		}
		JScrollPane scroller = new JScrollPane(liste);
		scroller.getVerticalScrollBar().setUnitIncrement(20);
		setContentPane(scroller);
		pack();
        setSize(new Dimension(924, 668));
		setLocation(new Point(50, 50));
		setVisible(true);
		liste.scrollRectToVisible(new Rectangle(0, ((int)liste.getSize().getHeight()) - 1, 1, 1));
	}
	
	protected void exit() {
		// stelle sicher, dass es sicher ist, das Programm zu schließen
		if (burnWindow==null) {
			// schließe das Programm
			System.exit(0);
		}
	}
	
	public void actionPerformed(ActionEvent ae) {
//		System.out.println("Predigt: " + ae.getActionCommand());
		// Buttons setzen
		for (int i = 0; i < buttons.size(); i++) {
			JButton button = (JButton)buttons.elementAt(i);
			if (ae.getSource()==button) {
				button.setBackground(Color.green);
			}
			button.setEnabled(false);
		}
		// jetzt Brennfenster öffnen
		burnWindow = new BurnWindow("-s", DB.getSermonsDir() + ae.getActionCommand(), DB.getBurners(), this);
	}
	
	public void closeBurnWindow() {
		for (int i = 0; i < buttons.size(); i++) {
			JButton button = (JButton)buttons.elementAt(i);
			button.setBackground(Color.WHITE);
			button.setEnabled(true);
		}
		burnWindow.setVisible(false);
		burnWindow.dispose();
		burnWindow = null;
	}
	
	public static String replace(String in, String toreplace, String replacewith) {
        String ret = new String(in.toString());
		while (ret.indexOf(toreplace)>=0) {
	  		ret = ret.substring(0, ret.indexOf(toreplace)) +
	  			replacewith +
            		ret.substring(ret.indexOf(toreplace) + toreplace.length());
		}
		return ret;
	}
	
}
