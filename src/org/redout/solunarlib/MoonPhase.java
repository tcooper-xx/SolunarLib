package org.redout.solunarlib;

public class MoonPhase {
	public static String PHASE_NEW = "New";
	public static String PHASE_FULL = "Full";
	public static String PHASE_1STQUARTER = "First Quarter";
	public static String PHASE_LASTQUARTER = "Last Quarter";
	public static String PHASE_WAXING_CRESCENT = "Waxing Crescent";
	public static String PHASE_WANING_CRESCENT = "Waning Crescent";
	public static String PHASE_WAXING_GIBBOUS = "Waxing Gibbous";
	public static String PHASE_WANING_GIBBOUS = "Waning Gibbous";
	public static String PHASE_UNKNOWN = "Moon Phase Unknown";
	
	private String PhaseName;
	private double PhaseTime; //Need Convert Time to String
	private double illumination;
	private double age;
	public String getPhaseName() {
		return PhaseName;
	}
	public void setPhaseName(String phaseName) {
		PhaseName = phaseName;
	}
	public double getPhaseTime() {
		return PhaseTime;
	}
	public void setPhaseTime(double phaseTime) {
		PhaseTime = phaseTime;
	}
	public double getIllumination() {
		return illumination;
	}
	public void setIllumination(double illumination) {
		this.illumination = illumination;
	}
	public double getAge() {
		return age;
	}
	public void setAge(double age) {
		this.age = age;
	}
	

}
