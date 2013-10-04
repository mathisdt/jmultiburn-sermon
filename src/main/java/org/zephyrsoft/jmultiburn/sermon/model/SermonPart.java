package org.zephyrsoft.jmultiburn.sermon.model;

/**
 * Represents a part of a sermon.
 */
public class SermonPart {
	
	private Integer index;
	private String source;
	private Sermon sermon;
	
	public Integer getIndex() {
		return index;
	}
	
	public String getSource() {
		return source;
	}
	
	public Sermon getSermon() {
		return sermon;
	}
	
	public void setIndex(Integer index) {
		this.index = index;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public void setSermon(Sermon sermon) {
		this.sermon = sermon;
	}
	
}
