package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.schedule.Schedule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;


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
public class ScheduleSelectionDialog extends org.eclipse.swt.widgets.Dialog 
{
    class NullScheduleOptions extends ScheduleOptions
    {
        public NullScheduleOptions( Composite parent, int style )
        {
            super(parent, style);
        }
        public String getSchedulingName()
        {
            return "none";
        }
        public boolean canHandleSchedule( Schedule sched )
        {
            return false;
        }
        public Schedule getSchedule()
        {
            return null;
        }
        public void setSchedule( Schedule sched )
        {
        }
    }
    
	private Group groupOptions;
	private Combo cbType;
	private Button buttonCancel;
	private Button buttonOk;
	private org.eclipse.swt.widgets.Shell dialogShell;
	private Label labelScheduleType;

	private Schedule schedule;

	public ScheduleSelectionDialog( Shell parent, int style ) 
	{
		super(parent, style);
		
	}

	public void open() 
	{
		try {
		    dialogShell = new Shell( getParent(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.TOOL );
			GridLayout thisLayout = new GridLayout();
			thisLayout.numColumns = 3;
			GridLayout thisLayout1 = new GridLayout();
			thisLayout1.numColumns = 3;
			dialogShell.setLayout(thisLayout1);
            {
                labelScheduleType = new Label(dialogShell, SWT.NONE);
                labelScheduleType.setText("Scheduling Type:");
                GridData labelScheduleTypeLData = new GridData();
                labelScheduleTypeLData.widthHint = 89;
                labelScheduleTypeLData.heightHint = 14;
                labelScheduleType.setLayoutData(labelScheduleTypeLData);
            }
            {
                cbType = new Combo(dialogShell, SWT.DROP_DOWN | SWT.READ_ONLY);
                GridData cbTypeLData = new GridData();
                cbTypeLData.widthHint = 85;
                cbTypeLData.heightHint = 25;
                cbType.setLayoutData(cbTypeLData);
                cbType.addListener( SWT.Modify, new Listener() {
                    public void handleEvent(Event arg0)
                    {
                        ((StackLayout)groupOptions.getLayout()).topControl 
                        	= groupOptions.getChildren()[cbType.getSelectionIndex()];
                        groupOptions.layout();
                    }
                });
            }
            {
                groupOptions = new Group(dialogShell, SWT.NONE);
                StackLayout groupOptionsLayout = new StackLayout();
                GridData groupOptionsLData = new GridData();
                groupOptionsLData.grabExcessHorizontalSpace = true;
                groupOptionsLData.grabExcessVerticalSpace = true;
                groupOptionsLData.horizontalAlignment = GridData.FILL;
                groupOptionsLData.verticalAlignment = GridData.FILL;
                groupOptionsLData.horizontalSpan = 3;
                groupOptions.setLayoutData(groupOptionsLData);
                groupOptions.setLayout(groupOptionsLayout);
                groupOptions.setText("Options");
            }
            {
                buttonOk = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
                buttonOk.setText("Ok");
                GridData buttonOkLData = new GridData();
                buttonOk.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent evt) {
                        schedule = ((ScheduleOptions)((StackLayout)groupOptions.getLayout()).topControl).getSchedule();
                        dialogShell.dispose();
                    }
                });
                
                buttonOkLData.horizontalSpan = 2;
                buttonOkLData.grabExcessHorizontalSpace = true;
                buttonOkLData.horizontalAlignment = GridData.END;
                buttonOk.setLayoutData(buttonOkLData);
            }
            {
                buttonCancel = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
                buttonCancel.setText("Cancel");
                buttonCancel.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent evt) {
                        dialogShell.dispose();
                    }
                });
            }
            addScheduleOptions( new NullScheduleOptions( groupOptions, SWT.NULL ) );
            cbType.select( 0 );
    		addScheduleOptions( new IntervalScheduleOptions( groupOptions, SWT.NULL ) );

    		Display display = dialogShell.getDisplay();
			dialogShell.setSize(346, 280);
			Rectangle rect = getParent().getBounds();
			dialogShell.setLocation( 
			        rect.x + (rect.width /2) - dialogShell.getSize().x/2,
			        rect.y + (rect.height/2) - dialogShell.getSize().y/2);
			dialogShell.layout();
    		dialogShell.open();
			while( !dialogShell.isDisposed() ) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addScheduleOptions( ScheduleOptions options )
	{
	    cbType.add( options.getSchedulingName() );
	    if( options.canHandleSchedule( schedule ) )
        {
            cbType.setText( options.getSchedulingName() );
            options.setSchedule( schedule );
        }
	}

	public void setSchedule( Schedule schedule )
	{
	    this.schedule = schedule;
	}
	
	public Schedule getSchedule()
	{
	    return this.schedule;
	}
}
