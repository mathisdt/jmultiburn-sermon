package org.zephyrsoft.jmultiburn.sermon;

import static org.zephyrsoft.jmultiburn.sermon.DB.BASE_DIR_PROPERTY;
import java.io.File;

class Start {
	
	public static void main(String[] args) {
		// check base directory
		if (System.getProperty(BASE_DIR_PROPERTY) == null) {
			System.err.println("base directory not set, use parameter -Dbase.dir=...");
			System.exit(1);
		}
		File baseDir = new File(System.getProperty(BASE_DIR_PROPERTY));
		if (!baseDir.exists() || !baseDir.isDirectory() || !baseDir.canRead()) {
			System.err.println("base directory could not be read: " + System.getProperty(BASE_DIR_PROPERTY));
			System.exit(2);
		}
		// start GUI
		new MainWindow();
	}
	
}
