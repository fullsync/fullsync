package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.schedule.Schedule;

import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public abstract class ScheduleOptions extends Composite
{
    public ScheduleOptions(Composite parent, int style) 
	{
		super(parent, style);
	}
    
    public abstract String getSchedulingName();
    public abstract boolean canHandleSchedule( Schedule sched );
    public abstract void setSchedule( Schedule sched );
    public abstract Schedule getSchedule() throws DataParseException;
}
