package org.zephyrsoft.jmultiburn.sermon;

public enum Setting {
	
	BASE_DIR("base.dir"), SERMON_DIR("sermons.dir"), TEMP_DIR("temp.dir"), BURNERS("burners");
	
	private String key;
	
	private Setting(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
}
