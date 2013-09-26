package org.zephyrsoft.jmultiburn.sermon;

import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;

/**
 * holds the settings which are initially read from the file local.properties
 */
public class DB {
	
	// singleton
	private static final DB _instance = new DB();
	
	private Properties properties = null;
	
	private DB() {
		properties = new Properties();
		String fileName = System.getProperty("base.dir", "..") + "/local.properties";
		try {
			properties.load(new FileInputStream(fileName));
		} catch (Exception e) {
			throw new RuntimeException("error while reading " + fileName, e);
		}
		
	}
	
	private static DB getInstance() {
		return _instance;
	}
	
	private static String getString(String key) {
		try {
			return getInstance().properties.getProperty(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}
	
	public static String[] getBurners() {
		List<String> burners = new LinkedList<>();
		int i = 1;
		while (true) {
			String burner = getString("burner" + i++);
			if (burner != null) {
				burners.add(burner);
			} else {
				break;
			}
		}
		return burners.toArray(new String[burners.size()]);
	}
	
	public static String getSermonsDir() {
		return getString("sermonsdir");
	}
	
	public static String getTempDir() {
		return getString("tempdir");
	}
}
