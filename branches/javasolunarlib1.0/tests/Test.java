

import java.util.Calendar;
import java.util.Iterator;

import org.redout.solunarlib.Period;
import org.redout.solunarlib.Solunar;
import org.redout.solunarlib.SolunarFacade;
import org.redout.solunarlib.StringUtil;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SolunarFacade facade = SolunarFacade.getInstance();
		Calendar myDay = Calendar.getInstance();
		myDay.set(2010, 9, 4);
		Solunar s = facade.getForDate(myDay, 41, -95);
		System.out.println(StringUtil.convertTimeToString(s.getSolRST().getRise()));
		System.out.println(StringUtil.convertTimeToString(s.getSolRST().getSet()));
		System.out.println(StringUtil.convertTimeToString(s.getSolRST().getTransit()));
		
		System.out.println(StringUtil.convertTimeToString(s.getMoonRST().getRise()));
		System.out.println(StringUtil.convertTimeToString(s.getMoonRST().getSet()));
		System.out.println(StringUtil.convertTimeToString(s.getMoonRST().getTransit()));
		
		System.out.println(StringUtil.convertTimeToString(s.getMoonUnderFoot().getUnderfootTime()));
		System.out.println(s.getMoonPhase().getPhaseName());
		System.out.println(s.getMoonPhase().getIllumination());
		System.out.println(s.getMoonPhase().getAge());
		
		for (Iterator iterator = s.getMinors().iterator(); iterator.hasNext();) {
			Period period = (Period) iterator.next();
			System.out.println(period.getStart() + " - " + period.getStop());
			
		}
		for (Iterator iterator = s.getMajors().iterator(); iterator.hasNext();) {
			Period period = (Period) iterator.next();
			System.out.println(period.getStart() + " - " + period.getStop());
			
		}
		System.out.println(s.getDayScale());

	}

}
