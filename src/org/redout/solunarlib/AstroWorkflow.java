package org.redout.solunarlib;

public class AstroWorkflow {
	private static final AstroWorkflow workflow = new AstroWorkflow();
	
	public static AstroWorkflow getInstance()
	{
		return workflow;
	}
	
	public RiseSetTransit getRST (int object, double date, double ourlong, double ourlat ) {
		RiseSetTransit rst = new RiseSetTransit();
		Double riseTime =0D;
		Double setTime=0D;
		Double transitTime =0D;
		double above = 0;
		Double ym=0D;
		Double y0;
		Double yp;
		
		Double[] sinho = new Double[2];
		int doesRise=0;
		int doesSet=0;
		int doesTransit=0;
		int hour=1;
		int check=0;

		Double sl = MathUtil.sind(ourlat);
		Double cl = MathUtil.cosd(ourlat);
		sinho[0] = .002327D;
		sinho[1] = -0.014544D;
		ym = sinalt(object, date, hour - 1, ourlong, cl, sl) - sinho[object];
		if (ym > 0) {
			above = 1;
		}
		else {
			above = 0;
		}
		do
		{
			y0 = sinalt(object, date, hour, ourlong, cl, sl) - sinho[object];
			yp = sinalt(object, date, hour + 1, ourlong, cl, sl) - sinho[object];

			Quad ourQuad = MathUtil.quad(ym, y0, yp);
			switch (ourQuad.getNz())
			{
				case 0:	//nothing  - go to next time slot
					break;
				case 1:                      //simple rise / set event
					if (ym < 0) {       //must be a rising event
						riseTime = hour + ourQuad.getZ1();
						doesRise = 1;
					}
					else {	//must be setting
						setTime = hour + ourQuad.getZ1();
						doesSet = 1;
					}
					break;
				case 2:                      //rises and sets within interval
					if (ourQuad.getYe() < 0) {       //minimum - so set then rise
						riseTime = hour + ourQuad.getZ2();
						setTime = hour + ourQuad.getZ1();
					}
					else {    //maximum - so rise then set
						riseTime = hour + ourQuad.getZ1();
						setTime = hour + ourQuad.getZ2();
					}
					doesRise = 1;
					doesSet = 1;
					break;
					}

			ym = yp;     //reuse the ordinate in the next interval
			hour = hour + 2;
			check = (doesRise * doesSet);
		}
		while ((hour != 25) && (check != 1));
		// end rise-set loop
		
		//GET TRANSIT TIME
		hour = 0; //reset hour
		transitTime = getTransit(object, date, hour, ourlong);
		if (transitTime != 0.0) {
			doesTransit = 1;
		}
		if (object == 0){
			//System.out.println("\nMOON");
			rst.setBody(0);
		}
		else {
			//System.out.println("\n\nSUN");
			rst.setBody(1);
		}
		//logic to sort the various rise, transit set states
		// nested if's...sorry
		
		if ((doesRise == 1) || (doesSet == 1) || (doesTransit == 1)) {   //current object rises, sets or transits today
			if (doesRise == 1) {
				rst.setRise(riseTime);
				
			}
			else {
				rst.setRise(0.0);
				//System.out.println ("\ndoes not rise");
			}
			if (doesTransit == 1) {
				rst.setTransit(transitTime);
			}
			else {
				rst.setTransit(0.0);
				//System.out.println ("\ndoes not transit");
			}
			if (doesSet == 1) {
				rst.setSet(setTime);
			}
			else {
				rst.setSet(0.0);
				//System.out.println ("\ndoes not set");
			}

		}
		else { //current object not so simple
			if (above == 1) {
				//System.out.println ("\nalways above horizon");
				rst.setNoState(1);
			}
			else {
				//System.out.println ("\nalways below horizon");
				rst.setNoState(-1);
			}
		}
		//thats it were done.
		
		return rst;
	}
	
	double sinalt (int object, double mjd0, int hour, double ourlong, double cphi,
            double sphi ) {
		/*
		returns sine of the altitude of either the sun or the moon given the modified
		julian day number at midnight UT and the hour of the UT day, the longitude of
		the observer, and the sine and cosine of the latitude of the observer
		*/
		
		double loc_sinalt;   //sine of the altitude, return value;
		double instant, t;
		double lha;			//hour angle
		instant = mjd0 + hour / 24.0;
		t = (instant - 51544.5) / 36525;
		
		BodyPos position = new BodyPos();
		if (object == 0) {
			position = getMoonPos (t);
		}
		else {
			position=getSunPos (t);
		}
		
		lha = 15.0 * (lmst(instant, ourlong) - position.getRa());    //hour angle of object
		loc_sinalt = sphi * MathUtil.sind(position.getDec()) + cphi * MathUtil.cosd(position.getDec()) * MathUtil.cosd(lha);
		return (loc_sinalt);
	}
	
	public double lmst (double mjd, double ourlong) {
		//returns the local siderial time for the modified julian date and longitude
		double value;
		float mjd0;
		double ut;
		double t;
		double gmst;
		mjd0 = MathUtil.ipart(mjd);
		ut = (mjd - mjd0) * 24;
		t = (mjd0 - 51544.5) / 36525;
		gmst = 6.697374558 + 1.0027379093 * ut;
		gmst = gmst + (8640184.812866 + (.093104 - .0000062 * t) * t) * t / 3600;
		value = 24.0 * MathUtil.fpart((gmst - ourlong / 15.0) / 24.0);
		return (value);
	}
	
	public BodyPos getSunPos (double t) {
		/*
		Returns RA and DEC of Sun to roughly 1 arcmin for few hundred years either side
		of J2000.0
		*/
	
		double COSEPS = 0.91748;
		double SINEPS = 0.39778;
		double m, dL, L, rho, sl;
		double RA, DEC;
		double x, y, z;
		m = MathUtil.twoPI * MathUtil.fpart(0.993133 + 99.997361 * t);        //Mean anomaly
		dL = 6893 * Math.sin(m) + 72 * Math.sin(2 * m);          //Eq centre
		L = MathUtil.twoPI * MathUtil.fpart(0.7859453 + m / MathUtil.twoPI + (6191.2 * t + dL) / 1296000);
		sl = Math.sin(L);
		x = Math.cos(L);
		y = COSEPS * sl;
		z = SINEPS * sl;
		rho = Math.sqrt(1 - z * z);
		DEC = (360 / MathUtil.twoPI) * Math.atan2(z , rho);
		RA = (48 / MathUtil.twoPI) * Math.atan2(y , (x + rho));
		if (RA < 0) {
			RA = RA + 24;
		}
		BodyPos returnPos = new BodyPos();
		returnPos.setRa(RA);
		returnPos.setDec(DEC);
		return returnPos;
	}
	
	public BodyPos getMoonPos (double t) {
		/*
		returns ra and dec of Moon to 5 arc min (ra) and 1 arc min (dec) for a few
		centuries either side of J2000.0 Predicts rise and set times to within minutes
		for about 500 years in past - TDT and UT time diference may become significant
		for long times
		*/
		
		double ARC = 206264.8062;
		double COSEPS = 0.91748;
		double SINEPS = 0.39778;
		double L0, L, LS, d, F;
		L0 = MathUtil.fpart(.606433 + 1336.855225 * t);    //'mean long Moon in revs
		L = MathUtil.twoPI * MathUtil.fpart(.374897 + 1325.55241 * t); //'mean anomaly of Moon
		LS = MathUtil.twoPI * MathUtil.fpart(.993133 + 99.997361 * t); //'mean anomaly of Sun
		d = MathUtil.twoPI * MathUtil.fpart(.827361 + 1236.853086 * t); //'diff longitude sun and moon
		F = MathUtil.twoPI * MathUtil.fpart(.259086 + 1342.227825 * t); //'mean arg latitude
		//' longitude correction terms
		double dL, h;
		dL = 22640 * Math.sin(L) - 4586 * Math.sin(L - 2 * d);
		dL = dL + 2370 * Math.sin(2 * d) + 769 * Math.sin(2 * L);
		dL = dL - 668 * Math.sin(LS) - 412 * Math.sin(2 * F);
		dL = dL - 212 * Math.sin(2 * L - 2 * d) - 206 * Math.sin(L + LS - 2 * d);
		dL = dL + 192 * Math.sin(L + 2 * d) - 165 * Math.sin(LS - 2 * d);
		dL = dL - 125 * Math.sin(d) - 110 * Math.sin(L + LS);
		dL = dL + 148 * Math.sin(L - LS) - 55 * Math.sin(2 * F - 2 * d);
		//' latitude arguments
		double S, N, lmoon, bmoon;
		S = F + (dL + 412 * Math.sin(2 * F) + 541 * Math.sin(LS)) / ARC;
		h = F - 2 * d;
		//' latitude correction terms
		N = -526 * Math.sin(h) + 44 * Math.sin(L + h) - 31 * Math.sin(h - L) - 23 * Math.sin(LS + h);
		N = N + 11 * Math.sin(h - LS) - 25 * Math.sin(F - 2 * L) + 21 * Math.sin(F - L);
		lmoon = MathUtil.twoPI * MathUtil.fpart(L0 + dL / 1296000); //  'Lat in rads
		bmoon = (18520 * Math.sin(S) + N) / ARC;  //     'long in rads
		//' convert to equatorial coords using a fixed ecliptic
		double CB, x, V, W, y, Z, rho, DEC, RA;
		CB = Math.cos(bmoon);
		x = CB * Math.cos(lmoon);
		V = CB * Math.sin(lmoon);
		W = Math.sin(bmoon);
		y = COSEPS * V - SINEPS * W;
		Z = SINEPS * V + COSEPS * W;
		rho = Math.sqrt(1.0 - Z * Z);
		DEC = (360.0 / MathUtil.twoPI) * Math.atan2(Z , rho);
		RA = (48.0 / MathUtil.twoPI) * Math.atan2(y , (x + rho));
		if (RA < 0) {
			RA = RA + 24.0;
		}
		BodyPos returnPos = new BodyPos();
		returnPos.setRa(RA);
		returnPos.setDec(DEC);
		return returnPos;
	}

	
	
	public double getTransit (int object, double mjd0, int hour, double ourlong)
	{
		//double ra = 0.0;
		double instant, t;
		double lha;			//local hour angle
		double loc_transit =0;	// transit time, return value.
		int min = 0;
	    int hourarray[] = new int[255];
	    int minarray[] = new int[615];
	    double LA;  //local angle
	    int sLA;    //sign of angle
	    double mintime;

	//loop through all 24 hours of the day and store the sign of the angle in an array
	//actually loop through 25 hours if we reach the 25th hour with out a transit then no transit condition today.

			while (hour < 25.0)
		{
			instant = mjd0 + hour / 24.0;
			t = (instant - 51544.5) / 36525;
			BodyPos pos;
			if (object == 0) {
				pos = getMoonPos (t);
			}
			else {
				pos = getSunPos (t);
			}
			lha = (lmst(instant, ourlong) - pos.getRa());
	        LA = lha * 15.04107;    //convert hour angle to degrees
	        sLA = new Double(LA/Math.abs(LA)).intValue();      //sign of angle
			hourarray[hour] = sLA;
			hour++;
		}
	//search array for the when the angle first goes from negative to positive
			int i = 0;
			while (i < 25)
	        {
	            loc_transit = i;
	            if (hourarray[i] - hourarray[i+1] == -2) {
	                //we found our hour
	                break;
	            }

	            i++;
	        }
	//check for no transit, return zero
	        if (loc_transit > 23) {
	            // no transit today
	            loc_transit = 0.0;
	            return loc_transit;
	        }

	//loop through all 60 minutes of the hour and store sign of the angle in an array
		mintime = loc_transit;
		while (min < 60)
		{
			instant = mjd0 + mintime / 24.0;
			t = (instant - 51544.5) / 36525;
			BodyPos pos;
			if (object == 0) {
				pos = getMoonPos (t);
			}
			else {
				pos =getSunPos (t);
			}
			lha = (lmst(instant, ourlong) - pos.getRa());
			LA = lha * 15.04107;
	        sLA = (int)(LA/Math.abs(LA));
	        minarray[min] = sLA;
			min++;
	        mintime = mintime + 0.016667;		//increment 1 minute
		}

	    i = 0;
		while (i < 60)
	    {
	        if (minarray[i] - minarray[i+1] == -2) {
	        //we found our min
	        break;
	        }
	        i++;
	        loc_transit = loc_transit + 0.016667;
	    }
	return (loc_transit);
	}
	
	public UnderFoot getUnderfoot (double date, double underlong)	{
		UnderFoot underFoot = new UnderFoot();
		double moonunderTime;
		moonunderTime = getTransit (0, date, 0, underlong);
		if (moonunderTime != 0.0) {
			underFoot.setUnderFoot(true);
			underFoot.setUnderfootTime(moonunderTime);

		}
		else {
				underFoot.setUnderFoot(false);
		}

		return underFoot;
	}
	
	public MoonPhase getMoonPhase (double date)	{
		MoonPhase moonPhase = new MoonPhase();
		
	    int PriPhaseOccurs; //1 = yes, 0 = no
	    int i = 0;
	    double ourhour =0;
	    double hour = -1;
	    double ls, lm, diff;
	    double instant, t;
	    double phase;
	    double hourarray[] = new double[255];
	    double minarray[] = new double[255];
	    double illumin;
	    double PriPhaseTime =0;

	/*some notes on structure of hourarray[]
	 *  increment is 15mins
	 * i =  0, hourarray[0] = hour -1, hour 23 of prev day.
	 * i =  1, hourarry[1] = hour -0.75, hour 23.15 of prev day.
	 * i = 4, hourarray[4] = hour 0 of today.
	 * i = 52, hourarray[52] = hour 12 of today.
	 * i = 99, hourarray[99] = hour 23.75 of today.
	 * i = 100, hourarray[100] = hour 0 of nextday.
	 * 
	 * to convert i to todays hour = (i/4 -1)
	 */

	//find and store illumination for every 1/4 hour in an array
	while (i < 104)
	{
	    instant = date + hour / 24.0;
	    t = (instant - 51544.5) / 36525;
	    lm = getMoonLong (t);
	    ls = getSunLong (t);
	    diff = lm - ls;
		phase = (1.0 - MathUtil.cosd(lm - ls))/2;
		phase *=100;
		if (diff < 0) {
			diff += 360;
		}
		if (diff > 180) {
			phase *= -1;
		}
	    illumin = Math.abs(phase);
	    hourarray[i] = illumin;
	    i++;
	    hour+= 0.25;
	}
	i = 0;
	while (i < 104)
	{
	    ourhour = i;
	    ourhour = ((ourhour/4) - 1);
	    //check for a new moon
	    if ((hourarray[i] < hourarray[i+1]) && (hourarray[i] < 0.001)) {
	       break;
	    }
	    //check for a full moon
	    if ( (hourarray[i] > hourarray[i+1]) && (hourarray[i] > 99.9999) ){
	        break;
	    }
	    //check for a first quarter
	    if ( (hourarray[i] < hourarray[i+1]) && (hourarray[i] > 50) && (hourarray[i] < 50.5)){
	        break;
	    }
	    //check for a last quarter
	    if ( (hourarray[i] > hourarray[i+1]) && (hourarray[i] < 50) && (hourarray[i] > 49.5) ){
	        break;
	    }

	    i++;
	}
	if ( ourhour < 0 || ourhour >= 24 ) {
	    PriPhaseOccurs = 0;
	}
	else {
	    PriPhaseOccurs = 1;
	}

	if (PriPhaseOccurs == 1){
	    //check every min start with the previous hour
	    if (ourhour > 0) {
	    	   hour = MathUtil.ipart(ourhour) - 1;
	    }
	    else {
	   hour = MathUtil.ipart(ourhour);
	    }

	    PriPhaseTime = hour;
	    i = 0;
	    while (i < 120)
	    {
	        instant = date + hour / 24.0;
	        t = (instant - 51544.5) / 36525;
	        lm = getMoonLong (t);
	        ls = getSunLong (t);
	        diff = lm - ls;
	        phase = (1.0 - MathUtil.cosd(lm - ls))/2;
	        phase *=100;
	        if (diff < 0) {
	            diff += 360;
	        }
	        if (diff > 180) {
	            phase *= -1;
	        }
	// we are getting age at the wrong time here, maybe for a primary phase
	// we should use a static age, like we do for illumin.        
	        //age = fabs(diff/13);
	        illumin = Math.abs(phase);
	        minarray[i] = illumin;
	        hour = hour + 0.016667;
	        i++;
	    }

	    i = 0;
	    while (i < 120)
	    {
	        //check for a new moon
	        if ((minarray[i] < minarray[i+1]) && (minarray[i] < 0.1)) {
	            moonPhase.setAge(0);
	            moonPhase.setIllumination(0);
	            moonPhase.setPhaseName(MoonPhase.PHASE_NEW);
	            break;
	        }
	        //check for a full moon
	        if ( (minarray[i] > minarray[i+1]) && (minarray[i] > 99) ){
	            moonPhase.setAge(14);
	            moonPhase.setIllumination(100);
	            moonPhase.setPhaseName(MoonPhase.PHASE_FULL);
	            break;
	        }
	        //check for a first quarter
	        if ( (minarray[i] < minarray[i+1]) && (minarray[i] > 50) && (minarray[i] < 51)){
	            moonPhase.setAge(7);
	            moonPhase.setIllumination(50);
	            moonPhase.setPhaseName(MoonPhase.PHASE_1STQUARTER);
	            break;
	        }
	        //check for a last quarter
	        if ( (minarray[i] > minarray[i+1]) && (minarray[i] < 50) && (minarray[i] > 49) ){
	            moonPhase.setAge(21);
	            moonPhase.setIllumination(50);
	            moonPhase.setPhaseName(MoonPhase.PHASE_LASTQUARTER);
	            break;
	        }
	        PriPhaseTime = PriPhaseTime + 0.016667;
	        i++;
	    }

	}
	else {
	//if we didn't find a primary phase, check the phase at noon.
//	    date = (JD - 2400000.5);
	    instant = date + .5;//check at noon
	    t = (instant - 51544.5) / 36525;
	    lm = getMoonLong (t);
	    ls = getSunLong (t);
	    diff = lm - ls;
	    phase = (1.0 - MathUtil.cosd(lm - ls))/2;
	    phase *=100;
	    if (diff < 0) {
	        diff += 360;
	    }
	    if (diff > 180) {
	        phase *= -1;
	    }
	    //age = fabs((lm - ls)/13);
	    //age = Math.abs(diff/13);
		//illumin = Math.abs(phase);
		moonPhase.setAge(Math.abs(diff/13));
		moonPhase.setIllumination(Math.abs(phase));
			//Get phase type
	        if( Math.abs(phase) <  50 && phase < 0 ) {
	        	moonPhase.setPhaseName(MoonPhase.PHASE_WANING_CRESCENT);
			}
			else if( Math.abs(phase) <  50 && phase > 0 ) {
				moonPhase.setPhaseName(MoonPhase.PHASE_WAXING_CRESCENT);
			}
			else if( Math.abs(phase) < 100 && phase < 0 ) {
				moonPhase.setPhaseName(MoonPhase.PHASE_WANING_GIBBOUS);
			}
			else if( Math.abs(phase) < 100 && phase > 0 ) {
				moonPhase.setPhaseName(MoonPhase.PHASE_WAXING_GIBBOUS);
			}
			else {
			    moonPhase.setPhaseName(MoonPhase.PHASE_UNKNOWN);
			}
	}
	if (PriPhaseOccurs == 1){
		moonPhase.setPhaseTime(PriPhaseTime);
	}

	return moonPhase;
	}
	
	double getMoonLong (double t)
	{
		double ARC = 206264.8062;

		double L0, L, LS, d, F;
		double moonlong;
		L0 = MathUtil.fpart(.606433 + 1336.855225 * t);    //'mean long Moon in revs
		L = MathUtil.twoPI * MathUtil.fpart(.374897 + 1325.55241 * t); //'mean anomaly of Moon
		LS = MathUtil.twoPI * MathUtil.fpart(.993133 + 99.997361 * t); //'mean anomaly of Sun
		d = MathUtil.twoPI * MathUtil.fpart(.827361 + 1236.853086 * t); //'diff longitude sun and moon
		F = MathUtil.twoPI * MathUtil.fpart(.259086 + 1342.227825 * t); //'mean arg latitude
		//' longitude correction terms
		double dL, h;
		dL = 22640 * Math.sin(L) - 4586 * Math.sin(L - 2 * d);
		dL = dL + 2370 * Math.sin(2 * d) + 769 * Math.sin(2 * L);
		dL = dL - 668 * Math.sin(LS) - 412 * Math.sin(2 * F);
		dL = dL - 212 * Math.sin(2 * L - 2 * d) - 206 * Math.sin(L + LS - 2 * d);
		dL = dL + 192 * Math.sin(L + 2 * d) - 165 * Math.sin(LS - 2 * d);
		dL = dL - 125 * Math.sin(d) - 110 * Math.sin(L + LS);
		dL = dL + 148 * Math.sin(L - LS) - 55 * Math.sin(2 * F - 2 * d);
		//' latitude arguments
		double S, N, lmoon, bmoon;
		S = F + (dL + 412 * Math.sin(2 * F) + 541 * Math.sin(LS)) / ARC;
		h = F - 2 * d;
		//' latitude correction terms
		N = -526 * Math.sin(h) + 44 * Math.sin(L + h) - 31 * Math.sin(h - L) - 23 * Math.sin(LS + h);
		N = N + 11 * Math.sin(h - LS) - 25 * Math.sin(F - 2 * L) + 21 * Math.sin(F - L);
		lmoon = MathUtil.twoPI * MathUtil.fpart(L0 + dL / 1296000); //  'Lat in rads
		bmoon = (18520 * Math.sin(S) + N) / ARC;  //     'long in rads
		moonlong = lmoon * MathUtil.RADEG;
	return moonlong;
	}

	double getSunLong (double t)
	{
		double m, dL, L;
		double sunlong;
		m = MathUtil.twoPI * MathUtil.fpart(0.993133 + 99.997361 * t);        //Mean anomaly
		dL = 6893 * Math.sin(m) + 72 * Math.sin(2 * m);          //Eq centre
		L = MathUtil.twoPI * MathUtil.fpart(0.7859453 + m / MathUtil.twoPI + (6191.2 * t + dL) / 1296000);
		sunlong = L * MathUtil.RADEG;
		return sunlong;
	}
	
}
