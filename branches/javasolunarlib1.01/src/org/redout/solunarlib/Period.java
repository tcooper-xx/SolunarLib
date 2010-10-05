package org.redout.solunarlib;

public class Period {
	public final static String TYPE_MAJOR = "Major";
	public final static String TYPE_MINOR = "Minor";
	
	private String type;
	private String start;
	private String stop;
	
	public Period(String type) {
		this.setType(type);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getStop() {
		return stop;
	}
	public void setStop(String stop) {
		this.stop = stop;
	}
	
	
}
