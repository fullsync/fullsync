/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.schedule;




/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class IntervalSchedule implements Schedule 
{
	private static final long serialVersionUID = 1;
	
    long firstInterval;
    long interval;
	long next;
		
	public IntervalSchedule( long firstInterval, long interval )
	{
	    this.firstInterval = firstInterval;
	    this.interval = interval;
	    
		this.next = System.currentTimeMillis() + firstInterval;
	}
		
	public long getNextOccurrence( long now )
	{
		return next>now?next:now;
	}
	
	public void setLastOccurrence( long now )
	{
		this.next = now + interval;
	}
	
	public long getFirstInterval()
	{
	    return firstInterval;
	}
	
	public long getInterval()
	{
	    return interval;
	}
}
