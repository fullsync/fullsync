package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.schedule.IntervalSchedule;
import net.sourceforge.fullsync.schedule.Schedule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
* This code was generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* *************************************
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED
* for this machine, so Jigloo or this code cannot be used legally
* for any corporate or commercial purpose.
* *************************************
*/
public class IntervalScheduleOptions extends ScheduleOptions 
{
	private Label label1;
	private Text textCount;
	private Combo cbUnit;

	public IntervalScheduleOptions(Composite parent, int style) 
	{
		super(parent, style);
		initGUI();
		cbUnit.select( 0 );
	}

	private void initGUI() 
	{
		try {
			GridLayout thisLayout = new GridLayout();
			thisLayout.numColumns = 3;
			this.setLayout(thisLayout);
			this.setSize(265, 32);
            {
                label1 = new Label(this, SWT.NONE);
                label1.setText(Messages.getString("IntervalScheduleOptions.ExecuteEvery")); //$NON-NLS-1$
                GridData label1LData = new GridData();
                label1LData.widthHint = 74;
                label1LData.heightHint = 15;
                label1.setLayoutData(label1LData);
            }
            {
                textCount = new Text(this, SWT.BORDER | SWT.RIGHT);
                textCount.setText("1"); //$NON-NLS-1$
                GridData textCountLData = new GridData();
                textCountLData.widthHint = 50;
                textCountLData.heightHint = 13;
                textCount.setLayoutData(textCountLData);
            }
            {
                cbUnit = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
                // TODO sadly we can't support "days","months" as the interval is starting
                // with program startup
                cbUnit.setItems(new java.lang.String[] {Messages.getString("IntervalScheduleOptions.seconds"),Messages.getString("IntervalScheduleOptions.minutes"),Messages.getString("IntervalScheduleOptions.hours")}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
			this.layout();
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}
	public String getSchedulingName()
    {
        return Messages.getString("IntervalScheduleOptions.Interval"); //$NON-NLS-1$
    }
	public boolean canHandleSchedule( Schedule sched )
    {
	    return sched instanceof IntervalSchedule; 
    }
	public void setSchedule( Schedule sched )
	{
	    if( sched instanceof IntervalSchedule )
	    {
	        IntervalSchedule is = (IntervalSchedule)sched;
	        textCount.setText( String.valueOf( is.getInterval()/1000 ) );
	        cbUnit.select( 0 );
	    }
	}
	public Schedule getSchedule()
	{
	    long multi = 1;
	    switch( cbUnit.getSelectionIndex() )
	    {
	    case 2: multi *= 60;
	    case 1: multi *= 60;
	    case 0: multi *= 1000;
	    }
	    long interval = Long.parseLong( textCount.getText() ) * multi; 
	    return new IntervalSchedule( interval, interval );
	}
}
