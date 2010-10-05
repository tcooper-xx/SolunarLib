package org.redout.solunarlib;

import java.util.ArrayList;
import java.util.Calendar;

public class Solunar implements Comparable<Solunar> {
	private Calendar dayOf;
	private double longitude;
	private double latitude;
	
	private RiseSetTransit solRST;
	private RiseSetTransit moonRST;
	private UnderFoot moonUnderFoot;
	private MoonPhase moonPhase;
	private ArrayList<Period> minors = new ArrayList<Period>();
	private ArrayList<Period> majors = new ArrayList<Period>();
	private int dayScale;
	
	public Calendar getDayOf() {
		return dayOf;
	}
	public void setDayOf(Calendar dayOf) {
		this.dayOf = dayOf;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public RiseSetTransit getSolRST() {
		return solRST;
	}
	public void setSolRST(RiseSetTransit solRST) {
		this.solRST = solRST;
	}
	public RiseSetTransit getMoonRST() {
		return moonRST;
	}
	public void setMoonRST(RiseSetTransit moonRST) {
		this.moonRST = moonRST;
	}
	public UnderFoot getMoonUnderFoot() {
		return moonUnderFoot;
	}
	public void setMoonUnderFoot(UnderFoot moonUnderFoot) {
		this.moonUnderFoot = moonUnderFoot;
	}
	public MoonPhase getMoonPhase() {
		return moonPhase;
	}
	public void setMoonPhase(MoonPhase moonPhase) {
		this.moonPhase = moonPhase;
	}
	public ArrayList<Period> getMinors() {
		return minors;
	}
	public void setMinors(ArrayList<Period> minors) {
		this.minors = minors;
	}
	public ArrayList<Period> getMajors() {
		return majors;
	}
	public void setMajors(ArrayList<Period> majors) {
		this.majors = majors;
	}
	public int getDayScale() {
		return dayScale;
	}
	public void setDayScale(int dayScale) {
		this.dayScale = dayScale;
	}
	
	public int compareTo(Solunar o) {
		return this.dayOf.compareTo(o.dayOf);
	}
}
