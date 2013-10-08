package org.zephyrsoft.jmultiburn.sermon;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public class PropertyHolder {
	
	private static final Logger LOG = LoggerFactory.getLogger(PropertyHolder.class);
	
	@Autowired
	@Qualifier("defaultProperties")
	private Properties properties;
	
	@Value("#{systemProperties['user.home']}/.jmultiburn-sermon/application.properties")
	private String userPropertiesPath;
	
	public void init() throws IOException {
		File userPropertiesFile = new File(userPropertiesPath);
		if (userPropertiesFile.exists() && userPropertiesFile.canRead()) {
			LOG.info("loading user settings");
			properties.load(new FileReader(userPropertiesFile));
		} else {
			LOG.info("no user settings found");
		}
	}
	
	public String getProperty(String property) {
		String result = properties.getProperty(property);
		if (result == null) {
			// try to get from system properties
			result = System.getProperty(property);
		}
		return result;
	}
	
	public List<String> getPropertyListWithPrefix(String prefix) {
		List<String> result = new LinkedList<>();
		int i = 1;
		while (true) {
			String property = properties.getProperty(prefix + i++);
			if (property != null) {
				result.add(property);
			} else {
				break;
			}
		}
		return result;
	}
	
	public void setUserPropertiesPath(String userPropertiesPath) {
		this.userPropertiesPath = userPropertiesPath;
	}
	
}
