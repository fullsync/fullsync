package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.ExceptionHandler;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
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
        
        public PartContainer( CrontabPart crontabPart )
        {
            this.part = crontabPart;
            
            label = new Label( CrontabScheduleOptions.this, SWT.NULL );
            label.setText( part.name );
    	    
    	    radioAll = new Button( CrontabScheduleOptions.this, SWT.CHECK );
    	    radioAll.setText( Messages.getString("CrontabScheduleOptions.all") ); //$NON-NLS-1$
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
    	            final Shell shell = new Shell( getShell(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.TOOL );
    	            shell.setLayout( new GridLayout(2, true) );
    	            shell.setText( Messages.getString("CrontabScheduleOptions.Select")+part.name ); //$NON-NLS-1$
    	            
    	            final List table = new List( shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI );
    	            GridData data = new GridData( GridData.FILL, GridData.FILL, true, true, 2, 1 );
    	            data.heightHint = 230;
    	            table.setLayoutData( data );
    	            for( int i = part.low; i <= part.high; i++ )
    	            {
    	                //TableItem item = new TableItem( table, SWT.NULL );
    	                //item.setText( String.valueOf( i ) );
    	                table.add( String.valueOf( i ) );
    	            }
    	            try {
    	                table.select( part.createInstance(text.getText()).getIntArray(-part.low) );
    	            } catch( DataParseException dpe ) {
    	                ExceptionHandler.reportException( dpe );
    	                // TODO report exception
    	            }
    	            
    	            Button buttonOk = new Button( shell, SWT.NULL );
    	            buttonOk.setLayoutData( new GridData( GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL ) );
    	            buttonOk.setText( Messages.getString("CrontabScheduleOptions.Ok") ); //$NON-NLS-1$
    	            buttonOk.addSelectionListener( new SelectionAdapter() {
    	                public void widgetSelected(SelectionEvent e)
    	                {
    	                    text.setText( part.createInstance( table.getSelectionIndices(), -part.low ).pattern );
    	                    shell.dispose();
    	                }
    	            } );
    	            
    	            Button buttonClose = new Button( shell, SWT.NULL );
    	            buttonClose.setLayoutData( new GridData( GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL ) );
    	            buttonClose.setText( Messages.getString("CrontabScheduleOptions.Close") ); //$NON-NLS-1$
    	            buttonClose.addSelectionListener( new SelectionAdapter() {
    	                public void widgetSelected(SelectionEvent e)
    	                {
    	                    shell.dispose();
    	                }
    	            } );

    	            shell.setLocation( buttonChoose.toDisplay( 0, 0 ) );
    	            shell.setSize( 150, 300 );
    	            shell.layout();
    	    		shell.open();
    	    		Display display = getDisplay();
    				while( !shell.isDisposed() ) {
    					if (!display.readAndDispatch())
    						display.sleep();
    				}
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
			ExceptionHandler.reportException( e );
		}
	}
	
	public String getSchedulingName()  
	{
		return Messages.getString("CrontabScheduleOptions.Crontab"); //$NON-NLS-1$
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
