package org.zephyrsoft.jmultiburn.sermon.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a sermon.
 */
public class Sermon implements Iterable<SermonPart> {

	private List<SermonPart> parts = new ArrayList<>();
	private String date;
	private String name;
	private SourceType sourceType;

	public boolean addPart(SermonPart part) {
		part.setSermon(this);
		return parts.add(part);
	}

	public int getPartCount() {
		return parts.size();
	}

	@Override
	public Iterator<SermonPart> iterator() {
		return parts.iterator();
	}

	public String getDate() {
		return date;
	}

	public String getName() {
		return name;
	}

	public SourceType getSourceType() {
		return sourceType;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Sermon other = (Sermon) obj;
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (!date.equals(other.date)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
