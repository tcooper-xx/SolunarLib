package org.redout.solunarlib;

public class MathUtil {
	public static final double twoPI = Math.PI * 2;
	public static final double RADEG = 180/Math.PI;
	public static final double DEGRAD = Math.PI/180;
	
	
	
	public static Double[] myModf(double fullDouble) {
        int intVal = (int)fullDouble;
        double remainder = fullDouble - intVal;

        Double[] retVal = new Double[2];
        retVal[0] = new Double(intVal);
        retVal[1] = remainder;

        return retVal;
	}
	
	public static double sind(double x) {
		return Math.sin((x)*DEGRAD);
	}
	
	public static double cosd(double x) {
		return Math.cos((x)*DEGRAD);
	}
	
	public static float ipart (double x) {
		//returns the true integer part, even for negative numbers
		Float a;
		if (x != 0) {
		a = new Float(x/Math.abs(x) * Math.floor(Math.abs(x)));
		}
		else {
		    a=new Float(0);
		}
		return a.floatValue();
	}
	
	public static double fpart (double x)
	//returns fractional part of a number
	{
		x = x - Math.floor(x);
		if ( x < 0) {
			x = x + 1;
		}
	return x;
	}
	
	public static Quad quad (double ym, double y0, double yp) {
		/*
		finds a parabola through three points and returns values of coordinates of
		extreme value (xe, ye) and zeros if any (z1, z2) assumes that the x values are
		-1, 0, +1
		*/
	
		double a, b, c, dx, dis, XE, YE, Z1, Z2;
		int NZ;
		NZ = 0;
		XE = 0;
		YE = 0;
		Z1 = 0;
		Z2 = 0;
		a = .5 * (ym + yp) - y0;
		b = .5 * (yp - ym);
		c = y0;
		XE = (0.0 - b) / (a * 2.0); //              'x coord of symmetry line
		YE = (a * XE + b) * XE + c; //      'extreme value for y in interval
		dis = b * b - 4.0 * a * c;   //    'discriminant
		//more nested if's
			if ( dis > 0.000000 ) {                 //'there are zeros
			dx = (0.5 * Math.sqrt(dis)) / (Math.abs(a));
			Z1 = XE - dx;
			Z2 = XE + dx;
			if (Math.abs(Z1) <= 1) {
				NZ = NZ + 1 ;   // 'This zero is in interval
			}
			if (Math.abs(Z2) <= 1) {
				NZ = NZ + 1  ;   //'This zero is in interval
			}
			if (Z1 < -1) {
				Z1 = Z2;
			}
		}
		Quad returnQuad = new Quad();
		returnQuad.setXe(XE);
		returnQuad.setYe(YE);
		returnQuad.setZ1(Z1);
		returnQuad.setZ2(Z2);
		returnQuad.setNz(NZ);

		return returnQuad;
	}
}
