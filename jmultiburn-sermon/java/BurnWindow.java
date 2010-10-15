import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

class BurnWindow extends javax.swing.JFrame implements java.awt.event.ActionListener {
	private static final long serialVersionUID = 1L;
	private Runtime runEnviron;
	private javax.swing.JScrollPane burnDispScrl;
	private javax.swing.JTextArea burnDisplay;
	private boolean userQuit;
	private SermonSelector parent = null;
	
	protected static final String MULTIBURN_COMMAND = "multiburn-sermon";
	
	BurnWindow(String multiburnParameter, String fileToBurn, String part, String[] burnDevices, SermonSelector parent1) {
		parent = parent1;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		Container c4;
		Box b5;
		Box b6;
		Box b7;
		Font f8;
		int i9;
		JButton j10;
		JLabel j11;
		List<String> multiburnCommandLine = new ArrayList<String>();
		userQuit = false;
		setTitle("Brennen...");
		c4 = getContentPane();
		c4.setLayout(new java.awt.BorderLayout());
		b5 = new javax.swing.Box(0);
		b6 = new javax.swing.Box(0);
		b7 = new javax.swing.Box(0);
		c4.add(b5, "North");
		c4.add(b6, "Center");
		c4.add(b7, "South");
		burnDisplay = new javax.swing.JTextArea(24, 80);
		burnDisplay.setBorder(BorderFactory.createEmptyBorder(2, 5, 15, 0));
		burnDispScrl =
			new javax.swing.JScrollPane(burnDisplay, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		f8 = burnDisplay.getFont();
		i9 = f8.getSize();
		burnDisplay.setFont(new java.awt.Font("Monospaced", 0, i9));
		burnDisplay.setEditable(false);
		j10 = new javax.swing.JButton("Schließen");
		j10.setActionCommand("quit");
		j10.addActionListener(this);
		j11 = new javax.swing.JLabel("Meldungen:");
		b5.add(j11);
		b5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 0, 5));
		b6.add(burnDispScrl);
		b6.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5));
		b7.add(javax.swing.Box.createHorizontalGlue());
		b7.add(j10);
		b7.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pack();
		setSize(getSize().width + 20, getSize().height + 30);
		setResizable(false);
		multiburnCommandLine.add(MULTIBURN_COMMAND);
		multiburnCommandLine.add(multiburnParameter);
		multiburnCommandLine.add(fileToBurn);
		if (part == null || part.length() == 0) {
			part = "0";
		}
		multiburnCommandLine.add(part);
		for (String device : burnDevices) {
			multiburnCommandLine.add(device);
		}
		runMultiburn(multiburnCommandLine);
	}
	
	private Process multiburnProcess;
	private File tempDir;
	
	private void runMultiburn(List<String> commandLine) {
		runEnviron = Runtime.getRuntime();
		try {
			tempDir = new File(DB.getTempDir());
			if (!tempDir.exists() || !tempDir.canWrite()) {
				// temporäres Verzeichnis ist kaputt, also lieber das aktuelle Verzeichnis nehmen
				tempDir = null;
			}
			String[] commandLineArray = commandLine.toArray(new String[0]);
			multiburnProcess = runEnviron.exec(commandLineArray, null, tempDir);
			new BurnMonitor(burnDisplay, burnDispScrl, multiburnProcess);
			setVisible(true);
		} catch (IOException i1) {
			javax.swing.JOptionPane.showMessageDialog(this, "Could not run " + MULTIBURN_COMMAND
				+ ".\nPlease check that it is in your $PATH.", "Error", 0);
			dispose();
		}
	}
	
	public boolean getUserQuitState() {
		return userQuit;
	}
	
	public void actionPerformed(java.awt.event.ActionEvent a1) {
		if (a1.getActionCommand().equals("quit")) {
			exit();
		}
	}
	
	protected void exit() {
		Object[] options = { "Ja, wirklich!", "Nein, lieber doch nicht..." };
		int answer =
			javax.swing.JOptionPane
				.showOptionDialog(
					this,
					"Wirklich das Brenn-Fenster schließen?\n\nJede CD, die gerade gebrannt wird, ist damit unbrauchbar!\n\nJetzt schließen?",
					"Beenden?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		if (answer != JOptionPane.CLOSED_OPTION && answer == 0) {
			Process p2;
			Process p3;
			userQuit = true;
			try {
				// ursprünglichen Prozess zerstören
				multiburnProcess.destroy();
				// alle Forks zerstören, die der Prozess selbst initiiert hat
				p2 = runEnviron.exec("killall -9 " + MULTIBURN_COMMAND);
				// die temporären Dateien löschen, sonst läuft die Platte voll
				p3 = runEnviron.exec("rm -rf " + (tempDir == null ? "" : tempDir) + ".multiburn");
			} catch (IOException i8) {
				javax.swing.JOptionPane.showMessageDialog(this, "Could not kill " + MULTIBURN_COMMAND
					+ "\nPlease run 'killall -9 " + MULTIBURN_COMMAND + "'", "Error", 0);
			}
			parent.closeBurnWindow();
		}
	}
}
