package org.zephyrsoft.jmultiburn.sermon;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zephyrsoft.jmultiburn.sermon.ui.BurnWindow;

public class BurnMonitor implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(BurnMonitor.class);
	
	private JTextArea displayArea;
	private JScrollPane scroller;
	private Process burnProcess;
	private BurnWindow burnWindow;
	
	public BurnMonitor(JTextArea displayArea, JScrollPane scroller, Process process) {
		this.burnProcess = process;
		this.scroller = scroller;
		this.displayArea = displayArea;
		Thread thread = new Thread(this, "Std Out Monitoring Thread");
		thread.start();
	}
	
	@Override
	public void run() {
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(burnProcess.getInputStream()));
		burnWindow = (BurnWindow) displayArea.getTopLevelAncestor();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				displayArea.append(line);
				displayArea.append("\n");
				scroller.getViewport().setViewPosition(
					new Point(0, displayArea.getSize().height - scroller.getSize().height));
			}
			if (((BurnWindow) displayArea.getTopLevelAncestor()).getUserQuitState() == false) {
				displayArea.append("\n\n");
				displayArea.append("----- multiburn has exited. -----");
				javax.swing.JOptionPane.showMessageDialog(displayArea,
					"multiburn has exited.\nPlease check the 'Messages' window\nfor error messages.", "Error",
					JOptionPane.ERROR_MESSAGE);
			}
		} catch (IOException i2) {
			LOG.warn("an error ocurred while monitoring multiburn", i2);
		}
	}
}
