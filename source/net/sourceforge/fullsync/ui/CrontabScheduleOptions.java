package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.schedule.CrontabPart;
import net.sourceforge.fullsync.schedule.CrontabSchedule;
import net.sourceforge.fullsync.schedule.Schedule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class CrontabScheduleOptions extends ScheduleOptions 
{
    class PartContainer
    {
        private CrontabPart part;
        private Label label;
        private Button radioAll;
        private Button radioSelect;
        private Text text;
        private Button buttonChoose;
        
        public PartContainer( CrontabPart part )
        {
            this.part = part;
            
            label = new Label( CrontabScheduleOptions.this, SWT.NULL );
            label.setText( part.name );
    	    
    	    radioAll = new Button( CrontabScheduleOptions.this, SWT.CHECK );
    	    radioAll.setText( "all" );
    	    radioAll.setSelection( true );
    	    radioAll.addListener( SWT.Selection, new Listener() {
    	        public void handleEvent(Event event)
                {
    	            text.setEnabled( !radioAll.getSelection() );
    	            buttonChoose.setEnabled( !radioAll.getSelection() );
                }
    	    } );

    	    /*
    	    radioSelect = new Button( CrontabScheduleOptions.this, SWT.RADIO );
    	    radioSelect.addSelectionListener( new SelectionAdapter() {
    	        public void widgetSelected( SelectionEvent e )
                {
    	            text.setEnabled( true );
    	            buttonChoose.setEnabled( true );
                }
    	    } );
    	    */

    	    text = new Text( CrontabScheduleOptions.this, SWT.BORDER );
    	    text.setLayoutData( new GridData( GridData.FILL, GridData.CENTER, true, false ) );
    	    text.setText( "*" );
    	    text.setEnabled( false );
    	    
    	    buttonChoose = new Button( CrontabScheduleOptions.this, SWT.NULL );
    	    buttonChoose.setText( "..." );
    	    buttonChoose.setEnabled( false );
    	    buttonChoose.addSelectionListener( new SelectionAdapter() {
    	        public void widgetSelected(SelectionEvent arg0)
    	        {
    	            // TODO show popup so the user can choose
    	        }
    	    });
    	    
        }
        public void setInstance( CrontabPart.Instance instance )
        {
            if( instance.all )
            {
                text.setText( "*" );
            } else {
                text.setText( instance.pattern );
            }
            radioAll.setSelection( instance.all );
            text.setEnabled( !instance.all );
            buttonChoose.setEnabled( !instance.all );
        }
        public CrontabPart.Instance getInstance()
        	throws DataParseException
        {
            if( radioAll.getSelection() )
                 return part.createInstance();
            else return part.createInstance( text.getText() );
        }
    }
    
    private PartContainer[] parts;
    
	public CrontabScheduleOptions(Composite parent, int style) 
	{
		super(parent, style);
		initGUI();
	}
	
	private void initGUI() 
	{
		try {
			this.setLayout( new GridLayout( 4, false ) );

			CrontabPart[] cronParts = CrontabPart.ALL_PARTS;
			parts = new PartContainer[cronParts.length];
		    for( int i = 0; i < parts.length; i++ )
		        parts[i] = new PartContainer( cronParts[i] );
		    
			this.setSize(390, 260);
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getSchedulingName()  
	{
		return "Crontab";
	}

	public boolean canHandleSchedule(Schedule schedule)  
	{
		return schedule instanceof CrontabSchedule;
	}

	public void setSchedule(Schedule schedule)  
	{
	    if( schedule instanceof CrontabSchedule )
		{
		    CrontabPart.Instance[] instances = ((CrontabSchedule)schedule).getParts();
		    for( int i = 0; i < instances.length; i++ )
		    {
		        parts[i].setInstance( instances[i] );
		    }
	    }
	}

	public Schedule getSchedule()
		throws DataParseException
	{
	    return new CrontabSchedule(
	            parts[0].getInstance(),
	            parts[1].getInstance(),
	            parts[2].getInstance(),
	            parts[3].getInstance(),
	            parts[4].getInstance() );
	}
}
