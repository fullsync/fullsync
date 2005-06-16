package net.full.fullsync.sync.debug;

import net.full.fullsync.sync.Element;
import net.full.fullsync.sync.ElementProcessedListener;
import net.full.fullsync.sync.Phase;
import net.full.fullsync.sync.PhaseState;
import net.full.fullsync.sync.PhaseStateChangeListener;
import net.full.fullsync.sync.files.FileSyncElement;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
public class PhaseInfoComposite extends org.eclipse.swt.widgets.Composite 
{
    private Phase phase;
    
	private Label labelName;
	private Label labelState;
	private Label labelLastElementProcessed;
	private Label labelElementsProcessed;
	private Button buttonActions;
	private Label labelRunningTime;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void main(String[] args) {
		showGUI();
	}
		
	/**
	* Auto-generated method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void showGUI() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		PhaseInfoComposite inst = new PhaseInfoComposite(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public PhaseInfoComposite(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	private void initGUI() {
		try {
			GridLayout thisLayout = new GridLayout();
			this.setLayout(thisLayout);
			thisLayout.numColumns = 5;
			thisLayout.marginHeight = 0;
			thisLayout.marginWidth = 0;
			this.setSize(407, 40);
            {
                labelName = new Label(this, SWT.NONE);
                labelName.setText("<name>");
            }
            {
                labelState = new Label(this, SWT.NONE);
                labelState.setText("<state>");
            }
            {
                labelElementsProcessed = new Label(this, SWT.NONE);
                labelElementsProcessed.setText("<elements>");
            }
            {
                labelRunningTime = new Label(this, SWT.NONE);
                labelRunningTime.setText("<running time>");
            }
            {
                buttonActions = new Button(this, SWT.ARROW | SWT.DOWN);
                GridData buttonActionsLData = new GridData();
                buttonActionsLData.grabExcessHorizontalSpace = true;
                buttonActionsLData.horizontalAlignment = GridData.END;
                buttonActions.setLayoutData(buttonActionsLData);
                buttonActions.setText(">");
                buttonActions.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent evt) {
                        buttonActionsWidgetSelected(evt);
                    }
                });
            }
            {
                labelLastElementProcessed = new Label(this, SWT.NONE);
                GridData labelLastElementProcessedLData = new GridData();
                labelLastElementProcessedLData.horizontalSpan = 5;
                labelLastElementProcessedLData.horizontalAlignment = GridData.FILL;
                labelLastElementProcessed.setLayoutData(labelLastElementProcessedLData);
                labelLastElementProcessed.setText("<last element>");
            }
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void buttonActionsWidgetSelected(SelectionEvent evt) {
        Menu menu = new Menu( getShell(), SWT.POP_UP );
        
        MenuItem start = new MenuItem( menu, SWT.PUSH );
        start.setText( "start" );
        start.addSelectionListener( new SelectionAdapter() {
            public void widgetSelected( SelectionEvent arg0 )
            {
                phase.start();
            }
        } );

        MenuItem pause = new MenuItem( menu, SWT.PUSH );
        pause.setText( "pause" );
        pause.addSelectionListener( new SelectionAdapter() {
            public void widgetSelected( SelectionEvent arg0 )
            {
                phase.pause();
            }
        } );

        MenuItem resume = new MenuItem( menu, SWT.PUSH );
        resume.setText( "resume" );
        resume.addSelectionListener( new SelectionAdapter() {
            public void widgetSelected( SelectionEvent arg0 )
            {
                phase.resume();
            }
        } );

        MenuItem cancel = new MenuItem( menu, SWT.PUSH );
        cancel.setText( "start" );
        cancel.addSelectionListener( new SelectionAdapter() {
            public void widgetSelected( SelectionEvent arg0 )
            {
                phase.cancel();
            }
        } );
        
        menu.setVisible(true);
    }

    public void setPhase( Phase phase )
    {
        
        this.phase = phase;
        if( phase != null )
        {
            phase.addStateChangeListener( new PhaseStateChangeListener() {
                public void phaseStateChanged(Phase source,final PhaseState oldState,final PhaseState newState)
                {
                    getDisplay().syncExec( new Runnable() {
                        public void run()
                        {
                            labelState.setText( newState.getName() );
                            //styledTextLog.append( "changing state from "+oldState+" to "+newState+"\n" );
                            //styledTextLog.setSelection( styledTextLog.getCharCount() );
                        }
                    } );
                }
            } );
            
            phase.addElementProcessedListener( new ElementProcessedListener() {
                public void elementProcessingStarted()
                {
                    getDisplay().syncExec( new Runnable() {
                        public void run()
                        {
                            labelLastElementProcessed.setText( "processing started" );
                        }
                    } );
                }
                public void elementProcessed( final Element element )
                {
                    getDisplay().syncExec( new Runnable() {
                        public void run()
                        {
                            FileSyncElement e = (FileSyncElement)element;
                            labelLastElementProcessed.setText( e.getRelativePath() );
                            //styledTextLog.append( "processed "+e.getRelativePath()+"\n" );
                            //styledTextLog.setSelection( styledTextLog.getCharCount() );
                        }
                    } );
                }
                public void elementProcessingFinished()
                {
                    getDisplay().syncExec( new Runnable() {
                        public void run()
                        {
                            labelLastElementProcessed.setText( "processing finished" );
                        }
                    } );
                }
            } );
        }
    }
    public Phase getPhase()
    {
        return phase;
    }
    
    public void updateComponent()
    {
        getDisplay().syncExec( new Runnable() {
            public void run()
            {
                //labelName.setText( phase.getName() );
                labelElementsProcessed.setText( String.valueOf( phase.getProcessedElementCount() ) );
                labelRunningTime.setText( phase.getElapsedRunningTime().toString() );
            }
        } );
    }
}
