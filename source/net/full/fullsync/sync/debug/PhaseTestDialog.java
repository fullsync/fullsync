package net.full.fullsync.sync.debug;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import net.full.fullsync.sync.Phase;
import net.full.fullsync.sync.base.BaseThreadedPhase;
import net.full.fullsync.sync.files.ActionGenerator;
import net.full.fullsync.sync.files.DebugActionExecutor;
import net.full.fullsync.sync.files.FileSyncInputElement;
import net.full.fullsync.sync.files.SimpleFileSyncRulesProvider;
import net.full.fullsync.sync.files.TwoSideTraveller;
import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.impl.ExactCopyActionDecider;
import net.sourceforge.fullsync.impl.SimplyfiedSyncRules;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
public class PhaseTestDialog extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private Composite compositeButtons;
	private PhaseInfoComposite phaseInfoComposite3;
	private Button buttonAddInput;
	private PhaseInfoComposite phaseInfoCompositeActionGenerator;
	private PhaseInfoComposite phaseInfoCompositeTraveller;
	private StyledText styledTextLog;
    
    private Timer timer;

    private Phase phase1;
    private Phase phase2;
    private Phase phase3;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public static void main(String[] args) {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			PhaseTestDialog inst = new PhaseTestDialog(shell, SWT.NULL);
			inst.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PhaseTestDialog(Shell parent, int style) {
		super(parent, style);
	}

	public void open() {
		try {
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);

			GridLayout dialogShellLayout = new GridLayout();
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.layout();
			dialogShell.pack();
			dialogShell.setSize(464, 288);
            {
            	GridData phaseInfoCompositeTravellerLData = new GridData();
            	phaseInfoCompositeTravellerLData.horizontalAlignment = GridData.FILL;
                phaseInfoCompositeTraveller = new PhaseInfoComposite(
                    dialogShell,
                    SWT.NONE);
                phaseInfoCompositeTraveller.setLayoutData(phaseInfoCompositeTravellerLData);
            }
            {
            	GridData phaseInfoCompositeActionGeneratorLData = new GridData();
            	phaseInfoCompositeActionGeneratorLData.horizontalAlignment = GridData.FILL;
                phaseInfoCompositeActionGenerator = new PhaseInfoComposite(
                    dialogShell,
                    SWT.NONE);
                phaseInfoCompositeActionGenerator.setLayoutData(phaseInfoCompositeActionGeneratorLData);
            }
            {
            	GridData phaseInfoComposite3LData = new GridData();
            	phaseInfoComposite3LData.horizontalAlignment = GridData.FILL;
                phaseInfoComposite3 = new PhaseInfoComposite(
                    dialogShell,
                    SWT.NONE);
                phaseInfoComposite3.setLayoutData(phaseInfoComposite3LData);
            }
            {
                styledTextLog = new StyledText(dialogShell, SWT.BORDER);
                GridData styledTextLogLData = new GridData();
                styledTextLogLData.grabExcessHorizontalSpace = true;
                styledTextLogLData.grabExcessVerticalSpace = true;
                styledTextLogLData.horizontalAlignment = GridData.FILL;
                styledTextLogLData.verticalAlignment = GridData.FILL;
                styledTextLogLData.horizontalSpan = 2;
                styledTextLog.setLayoutData(styledTextLogLData);
            }
            {
                compositeButtons = new Composite(dialogShell, SWT.NONE);
                GridLayout compositeButtonsLayout = new GridLayout();
                compositeButtonsLayout.numColumns = 4;
                compositeButtonsLayout.marginWidth = 0;
                compositeButtonsLayout.marginHeight = 0;
                GridData compositeButtonsLData = new GridData();
                compositeButtonsLData.horizontalAlignment = GridData.FILL;
                compositeButtonsLData.horizontalSpan = 2;
                compositeButtons.setLayoutData(compositeButtonsLData);
                compositeButtons.setLayout(compositeButtonsLayout);
                {
                    buttonAddInput = new Button(compositeButtons, SWT.PUSH
                        | SWT.CENTER);
                    buttonAddInput.setText("Add Input");
                    buttonAddInput.addSelectionListener(new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent evt) {
                            buttonAddInputWidgetSelected(evt);
                        }
                    });
                }
            }
            setupExample();
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
            timer.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void setupExample()
    {
        phase1 = new BaseThreadedPhase( new TwoSideTraveller() );
        phase2 = new BaseThreadedPhase( new ActionGenerator( new ExactCopyActionDecider() ) );
        phase3 = new BaseThreadedPhase( new DebugActionExecutor() );
        
        phase1.attach( phase2 );
        phase2.attach( phase3 );
        
        phaseInfoCompositeTraveller.setPhase( phase1 );
        phaseInfoCompositeActionGenerator.setPhase( phase2 );
        phaseInfoComposite3.setPhase( phase3 );
        
        timer = new Timer( true );
        timer.schedule( new TimerTask() {
            public void run()
            {
                phaseInfoCompositeTraveller.updateComponent();
                phaseInfoCompositeActionGenerator.updateComponent();
                phaseInfoComposite3.updateComponent();
            }
        }, 100, 1000 );
    }
    
    private void buttonAddInputWidgetSelected(SelectionEvent evt) 
    {
        try {
            FileSystemManager manager = new FileSystemManager();
            Site source = manager.createConnection( new ConnectionDescription( "file:/E:/testing/source", "" ) );
            Site destination = manager.createConnection( new ConnectionDescription( "file:/E:/testing/destination", "" ) );
            
            SimplyfiedSyncRules ruleSet = new SimplyfiedSyncRules();
            SimpleFileSyncRulesProvider rulesProvider = new SimpleFileSyncRulesProvider( ruleSet );
            
            FileSyncInputElement input = new FileSyncInputElement( source, destination, rulesProvider );
            phase1.elementProcessingStarted();
            phase1.elementProcessed( input );
            phase1.elementProcessingFinished();
        } catch( Exception ex ) {
            ex.printStackTrace();
        }
	}
}
