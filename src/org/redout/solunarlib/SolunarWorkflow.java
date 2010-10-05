package org.redout.solunarlib;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SolunarWorkflow {
	private static final SolunarWorkflow workflow = new SolunarWorkflow();
	private AstroWorkflow astroWorkflow = AstroWorkflow.getInstance();
	
	public static SolunarWorkflow getInstance()
	{
		return workflow;
	}
	
	public Solunar getForDate(Calendar dayOf, double lat, double lon) {
		/***
		  We take the longitude in as -n = west.  However the astro calculations actually
		  use the reverse convention.  We'll store it in our Solunar object as -95 = 95W
		  but whenever using it to calculate we'll pass it as 95 = 95W... Confusing huh?
		  
		*/
		Solunar s = new Solunar();
		s.setDayOf(dayOf);
		s.setLatitude(lat);
		s.setLongitude(lon); 
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
		
		int year = Integer.parseInt(yearFormat.format(dayOf.getTime()));
		int month = Integer.parseInt(monthFormat.format(dayOf.getTime()));
		int day = Integer.parseInt(dayFormat.format(dayOf.getTime()));
		double ut = 0.0;
		double jd = getJulianDate(year, month, day, ut);
		double tzOffsetHours = dayOf.getTimeZone().getOffset(dayOf.getTimeInMillis())/60/60/1000;
		double zone = tzOffsetHours/24;
		double adjustedJulianDate = (jd - 2400000.5 - zone);
		s.setSolRST(astroWorkflow.getRST(1, adjustedJulianDate, 0.0 - lon, lat));
		s.setMoonRST(astroWorkflow.getRST(0, adjustedJulianDate, 0.0 - lon, lat));
		s.setMoonUnderFoot(astroWorkflow.getUnderfoot(adjustedJulianDate, lon));
		s.setMoonPhase(astroWorkflow.getMoonPhase(adjustedJulianDate));
		if (s.getMoonRST().getRise() >=1 && s.getMoonRST().getRise() <=23) {
			s.getMinors().add(workflow.getMinor(s.getMoonRST().getRise()));
		}
		if (s.getMoonRST().getSet() >=1 && s.getMoonRST().getSet() <=23) {
			s.getMinors().add(workflow.getMinor(s.getMoonRST().getSet()));
		}
		if (s.getMoonRST().getTransit() >=1.5 && s.getMoonRST().getTransit() <=22.5) {
			s.getMajors().add(workflow.getMajor(s.getMoonRST().getTransit()));
		}
		if (s.getMoonUnderFoot().getUnderfootTime() >=1.5 && s.getMoonUnderFoot().getUnderfootTime() <=22.5) {
			s.getMajors().add(workflow.getMajor(s.getMoonUnderFoot().getUnderfootTime()));
		}
		int moonPhaseScale = workflow.getMoonPhaseScale(s.getMoonPhase().getIllumination());
		int dayScale = workflow.getDayScale(s.getMoonRST().getRise(), s.getMoonRST().getSet(), s.getMoonRST().getTransit(),	s.getMoonUnderFoot().getUnderfootTime(), s.getSolRST().getRise(), s.getSolRST().getSet());
		
		s.setDayScale(moonPhaseScale + dayScale);
		return s;
	}
	
	private Period getMinor(double moonRiseOrSet)	{
		Period minor = new Period(Period.TYPE_MINOR);
		
	    double minorstart, minorstop;
	    minorstart = moonRiseOrSet - 1.0;
	    minorstop = moonRiseOrSet + 1.0;
	    minor.setStart(StringUtil.convertTimeToString(minorstart));
	    minor.setStop(StringUtil.convertTimeToString(minorstop));
	    return minor;
	}

	private Period getMajor(double moontransit) {
		Period major = new Period(Period.TYPE_MAJOR);
	    double majorstart, majorstop;
	    majorstart = moontransit - 1.5;
	    majorstop = moontransit + 1.5;
	    major.setStart(StringUtil.convertTimeToString(majorstart));
	    major.setStop(StringUtil.convertTimeToString(majorstop));
	    return major;
	}

	private int getMoonPhaseScale (double moonphase) {
	    int scale = 0;
	    if( Math.abs(moonphase) <  0.9 ) {		//new
	        scale = 3;
	    }
	    else if( Math.abs(moonphase) <  6.0 ) {
	        scale = 2;
	    }
	    else if( Math.abs(moonphase) <  9.9 ) {
	        scale = 1;
	    }
	    else if( Math.abs(moonphase) > 99 ) {		//full
	        scale = 3;
	    }
	    else if( Math.abs(moonphase) > 94 ) {
	        scale = 2;
	    }
	    else if( Math.abs(moonphase) > 90.1 ) {
	        scale = 1;
	    }
	    else {
	        scale = 0;
	    }

	    return scale;
	}

	private int getDayScale (double moonrise, double moonset, double moontransit,
	            double moonunder, double sunrise, double sunset) {
		int locsoldayscale = 0;
		//check minor1 and sunrise
		if ((sunrise >= (moonrise - 1.0)) && (sunrise <= (moonrise + 1.0))){
		    locsoldayscale++;
		}
		//check minor1 and sunset
		if ((sunset >= (moonrise - 1.0)) && (sunset <= (moonrise + 1.0))){
		    locsoldayscale++;
		}
		//check minor2 and sunrise
		if ((sunrise >= (moonset - 1.0)) && (sunrise <= (moonset + 1.0))){
		    locsoldayscale++;
		}
		//check minor2 and sunset
		if ((sunset >= (moonset - 1.0)) && (sunset <= (moonset + 1.0))){
		    locsoldayscale++;
		}
		//check major1 and sunrise
		if ((sunrise >= (moontransit - 2.0)) && (sunrise <= (moontransit + 2.0))){
		    locsoldayscale++;
		}
		//check major1 and sunset
		if ((sunset >= (moontransit - 2.0)) && (sunset <= (moontransit + 2.0))){
		    locsoldayscale++;
		}
		//check major2 and sunrise
		if ((sunrise >= (moonunder - 2.0)) && (sunrise <= (moonunder + 2.0))){
		    locsoldayscale++;
		}
		//check major2 and sunset
		if ((sunset >= (moonunder - 2.0)) && (sunset <= (moonunder + 2.0))){
		    locsoldayscale++;
		}
	
		//catch a >2 scale, tho this shouldn't happen.
		if (locsoldayscale > 2) {
		    locsoldayscale = 2;
		}
	
		return locsoldayscale;
	}

	private double getJulianDate(int year, int month, int day, double UT)
	{
		double locJD, b, c, d, e, f, g;
		Double[] result;

		result = MathUtil.myModf((month + 9)/12);
		b = result[0];
		result = MathUtil.myModf((7 * (year + b))/4);
		c = result[0];
		result = MathUtil.myModf((275 * month)/9);
		d = result[0];		
		
		e = 367 * year - c + d + day + 1721013.5 + UT/24;
		f = (100 * year + month - 190002.5);
		g = f/Math.abs(f);
		locJD = e - 0.5 * g + 0.5;
	return (locJD);
	}

}
