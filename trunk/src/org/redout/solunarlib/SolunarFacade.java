package org.redout.solunarlib;

import java.util.Calendar;


public class SolunarFacade {
	private static final SolunarFacade facade = new SolunarFacade();
	
	private SolunarWorkflow workflow = SolunarWorkflow.getInstance();
		
	public static SolunarFacade getInstance()
	{
		return facade;
	}
	
	public Solunar getForDate(Calendar dayOf, double lat, double lon) {
		return workflow.getForDate(dayOf, lat, lon);
	}
}
