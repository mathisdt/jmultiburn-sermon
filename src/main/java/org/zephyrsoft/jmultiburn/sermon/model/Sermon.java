package org.zephyrsoft.jmultiburn.sermon.model;


/**
 * Represents a sermon line.
 */
public class Sermon {
	
	private String date;
	private String name;
	private SourceType sourceType;
	private String source;
	private int parts;
	
	public String getDate() {
		return date;
	}
	
	public String getName() {
		return name;
	}
	
	public SourceType getSourceType() {
		return sourceType;
	}
	
	public String getSource() {
		return source;
	}
	
	public int getParts() {
		return parts;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public void setParts(int parts) {
		this.parts = parts;
	}
	
}
