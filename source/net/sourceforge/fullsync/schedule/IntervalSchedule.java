/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.schedule;



/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class IntervalSchedule implements Schedule 
{
	long next;
	long span;
		
	public IntervalSchedule( long firstSpan, long span )
	{
		this.next = System.currentTimeMillis() + firstSpan;
		this.span = span;
	}
		
	public long getNextOccurrence( long now )
	{
		return next>now?next:now;
	}
	
	public void update()
	{
		this.next = next+span;
	}
}
