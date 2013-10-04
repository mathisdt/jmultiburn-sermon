package org.zephyrsoft.jmultiburn.sermon.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.zephyrsoft.jmultiburn.sermon.BurnMonitor;
import org.zephyrsoft.jmultiburn.sermon.DB;
import org.zephyrsoft.jmultiburn.sermon.model.MultiBurnCommand;
import org.zephyrsoft.jmultiburn.sermon.model.SermonPart;

public class BurnWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private JScrollPane burnDispScrl;
	private JTextArea burnDisplay;
	private boolean userQuit;
	private MainWindow parent = null;
	
	public BurnWindow(SermonPart sermonPart, String[] burnDevices, MainWindow parent) {
		this.parent = parent;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		userQuit = false;
		setTitle("Brennen...");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		Box boxNorth = new Box(0);
		Box boxCenter = new Box(0);
		Box boxSouth = new Box(0);
		contentPane.add(boxNorth, BorderLayout.NORTH);
		contentPane.add(boxCenter, BorderLayout.CENTER);
		contentPane.add(boxSouth, BorderLayout.SOUTH);
		burnDisplay = new JTextArea(24, 80);
		burnDisplay.setBorder(BorderFactory.createEmptyBorder(2, 5, 15, 0));
		burnDispScrl =
			new JScrollPane(burnDisplay, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		Font burnDisplayFont = burnDisplay.getFont();
		burnDisplay.setFont(new Font("Monospaced", 0, burnDisplayFont.getSize()));
		burnDisplay.setEditable(false);
		JButton closeButton = new JButton("Schließen");
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				exit();
			}
		});
		JLabel messagesLabel = new JLabel("Meldungen:");
		boxNorth.add(messagesLabel);
		boxNorth.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 5));
		boxCenter.add(burnDispScrl);
		boxCenter.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		boxSouth.add(Box.createHorizontalGlue());
		boxSouth.add(closeButton);
		boxSouth.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pack();
		setSize(getSize().width + 20, getSize().height + 30);
		setResizable(false);
		MultiBurnCommand command;
		switch (sermonPart.getSermon().getSourceType()) {
			case SINGLE_FILE:
				command =
					MultiBurnCommand.forBurnSingleFile(sermonPart.getSource(), String.valueOf(sermonPart.getIndex()),
						burnDevices);
				break;
			case DIRECTORY:
				command = MultiBurnCommand.forBurnDirectory(sermonPart.getSource(), burnDevices);
				break;
			default:
				throw new IllegalArgumentException("unknown source type");
		}
		runMultiburn(command);
	}
	
	private Process multiburnProcess;
	private File tempDir;
	private BurnMonitor burnMonitor;
	
	private void runMultiburn(MultiBurnCommand command) {
		try {
			tempDir = new File(DB.getTempDir());
			if (!tempDir.exists() || !tempDir.canWrite()) {
				// temporäres Verzeichnis ist kaputt, also lieber das aktuelle Verzeichnis nehmen
				tempDir = null;
			}
			multiburnProcess = Runtime.getRuntime().exec(command.toArray(), null, tempDir);
			burnMonitor = new BurnMonitor(burnDisplay, burnDispScrl, multiburnProcess);
			setVisible(true);
		} catch (IOException i1) {
			JOptionPane.showMessageDialog(this, "Could not run multiburn!", "Error", JOptionPane.ERROR_MESSAGE);
			dispose();
		}
	}
	
	public boolean getUserQuitState() {
		return userQuit;
	}
	
	private void exit() {
		Object[] options = {"Ja, wirklich!", "Nein, lieber doch nicht..."};
		int answer =
			JOptionPane
				.showOptionDialog(
					this,
					"Wirklich das Brenn-Fenster schließen?\n\nJede CD, die gerade gebrannt wird, ist damit unbrauchbar!\n\nJetzt schließen?",
					"Beenden?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		if (answer != JOptionPane.CLOSED_OPTION && answer == 0) {
			userQuit = true;
			try {
				// ursprünglichen Prozess zerstören
				multiburnProcess.destroy();
				// alle Forks zerstören, die der Prozess selbst initiiert hat
				Runtime.getRuntime().exec(MultiBurnCommand.forKillMultiBurn().toArray());
				// die temporären Dateien löschen, sonst läuft die Platte voll
				Runtime.getRuntime().exec(
					"rm -rf " + (tempDir == null ? "" : tempDir.getAbsolutePath() + File.separator) + ".multiburn");
			} catch (IOException i8) {
				JOptionPane.showMessageDialog(this,
					"Could not kill multiburn\nPlease run 'killall -9 multiburn' yourself", "Error",
					JOptionPane.ERROR_MESSAGE);
			}
			parent.closeBurnWindow();
		}
	}
}
