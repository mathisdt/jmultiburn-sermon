import java.io.*;
import java.util.*;

/** Klasse um die lokalen Einstellungen aus der local.properties zu laden
 *  ==> mittels des "Singleton" Design Patterns implementiert <== */
public class DB {
	private static final DB _instance = new DB();
	
	private ResourceBundle RESOURCE_BUNDLE = null;
	
	private DB() {
		RESOURCE_BUNDLE = ResourceBundle.getBundle("local");
	}
	
	private static DB getInstance() {
		return _instance;
	}

	private static String getString(String key) {
		try {
			return getInstance().RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}
	
	public static String[] getBurners() {
		Vector burners_temp = new Vector();
		int i = 1;
		while (true) {
			String burner = getInstance().getString("burner" + i++);
			if (burner != null) {
				burners_temp.add(burner);
			} else {
				break;
			}
		}
		String[] burners_array = new String[burners_temp.size()];
		for (int j = 0; j < burners_temp.size(); j++) {
			burners_array[j] = (String)burners_temp.elementAt(j);
		}
		return burners_array;
	}
	
	public static String getSermonsDir() {
		return getInstance().getString("sermonsdir");
	}
}