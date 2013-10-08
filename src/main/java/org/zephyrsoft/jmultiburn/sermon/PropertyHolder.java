package org.zephyrsoft.jmultiburn.sermon;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import com.google.common.base.Splitter;
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
		logValues("current settings:");
		File userPropertiesFile = new File(userPropertiesPath);
		if (userPropertiesFile.exists() && userPropertiesFile.canRead()) {
			LOG.info("loading user settings");
			properties.load(new FileReader(userPropertiesFile));
			logValues("result after merging user settings:");
		} else {
			LOG.info("no user settings found");
		}
	}
	
	private void logValues(String title) {
		LOG.debug(title);
		for (Setting setting : Setting.values()) {
			LOG.debug("   {} = {}", setting.getKey(), properties.getProperty(setting.getKey()));
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
	
	public List<String> getPropertyList(String property) {
		List<String> result = new LinkedList<>();
		Splitter splitter = Splitter.on(Pattern.compile("[,;:]")).trimResults().omitEmptyStrings();
		Iterable<String> iterable = splitter.split(getProperty(property));
		for (String item : iterable) {
			result.add(item);
		}
		return result;
	}
	
	public void setUserPropertiesPath(String userPropertiesPath) {
		this.userPropertiesPath = userPropertiesPath;
	}
	
}
