package org.redout.solunarlib;

public class StringUtil {
	public static String convertTimeToString (double doubletime)
	{
	    Double i, d;
	/*split the time into hours (i) and minutes (d)*/
	    Double[] modfResults = MathUtil.myModf(doubletime);
	    i = modfResults[0];
	    d = modfResults[1];
	    d = d * 60;
	    if (d >= 59.5) {
	        i = i + 1;
	        d = 0.0;
	    }
	/*convert times to a string*/
	    if (d < 9.5) {
	        return(i.intValue() + ":" + "0" +d.intValue());
	    }
	    else {
	        return(i.intValue() + ":" + d.intValue());
	    }
	}
}
