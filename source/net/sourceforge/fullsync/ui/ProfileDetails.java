package net.sourceforge.fullsync.ui;

import java.io.File;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuleSetDescriptor;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import net.sourceforge.fullsync.impl.AdvancedRuleSetDescriptor;
import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
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
public class ProfileDetails extends org.eclipse.swt.widgets.Composite {

	private ProfileManager profileManager;
	private Label labelTypeDescription;
	private Combo comboType;
	private Label label16;
	private Text textDescription;
	private Label label15;
	private Label label1;
	private Text textRuleSet;
	private Label label4;
	private Group advancedRuleOptionsGroup;
	private Text textAcceptPattern;
	private Label label14;
	private Text textIgnorePatter;
	private Label label13;
	private Button deleteOnDestinationButton;
	private Button syncSubsButton;
	private Group simplyfiedOptionsGroup;
	private Button rbAdvancedRuleSet;
	private Button rbSimplyfiedRuleSet;
	private Group ruleSetGroup;
	private Label label12;
	private Text textDestinationPassword;
	private Label label11;
	private Text textDestinationUsername;
	private Label label10;
	private Button buttonDestinationBuffered;
	private Label label9;
	private Button buttonBrowseDst;
	private Text textDestination;
	private Label label3;
	private Label label8;
	private Text textSourcePassword;
	private Label label6;
	private Text textSourceUsername;
	private Label label5;
	private Button buttonSourceBuffered;
	private Label label7;
	private Button buttonBrowseSrc;
	private Text textSource;
	private Label label2;
	private Text textName;
	private String profileName;
	
	public ProfileDetails(Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	/**
	* Initializes the GUI.
	* Auto-generated code - any changes you make will disappear.
	*/
	public void initGUI(){
		try {
			preInitGUI();
			this.setSize(470, 324);

			GridLayout thisLayout = new GridLayout(7, true);
			thisLayout.marginWidth = 5;
			thisLayout.marginHeight = 5;
			thisLayout.numColumns = 7;
			thisLayout.makeColumnsEqualWidth = false;
			thisLayout.horizontalSpacing = 5;
			thisLayout.verticalSpacing = 5;
			this.setLayout(thisLayout);
            {
                label1 = new Label(this, SWT.NONE);
                GridData label1LData = new GridData();
                label1.setLayoutData(label1LData);
                label1.setText("Name:");
            }
            {
                textName = new Text(this, SWT.BORDER);
                GridData textNameLData = new GridData();
                textName.setToolTipText("Name for the profile");
                textNameLData.horizontalAlignment = GridData.FILL;
                textNameLData.horizontalSpan = 6;
                textName.setLayoutData(textNameLData);
            }
            {
                label15 = new Label(this, SWT.NONE);
                label15.setText("Description:");
            }
            {
                textDescription = new Text(this, SWT.BORDER);
                GridData textDescriptionLData = new GridData();
                textDescriptionLData.horizontalSpan = 6;
                textDescriptionLData.horizontalAlignment = GridData.FILL;
                textDescription.setLayoutData(textDescriptionLData);
            }
            {
                label2 = new Label(this, SWT.NONE);
                label2.setText("Source:");
                GridData label2LData = new GridData();
                label2.setLayoutData(label2LData);
            }
            {
                textSource = new Text(this, SWT.BORDER);
                GridData textSourceLData = new GridData();
                textSource.setToolTipText("Source location");
                textSourceLData.horizontalAlignment = GridData.FILL;
                textSourceLData.horizontalSpan = 5;
                textSourceLData.grabExcessHorizontalSpace = true;
                textSource.setLayoutData(textSourceLData);
            }
            {
                buttonBrowseSrc = new Button(this, SWT.PUSH | SWT.CENTER);
                buttonBrowseSrc.setText("...");
                GridData buttonBrowseSrcLData = new GridData();
                buttonBrowseSrc.setLayoutData(buttonBrowseSrcLData);
                buttonBrowseSrc.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent evt) {
                        buttonBrowseSrcWidgetSelected(evt);
                    }
                });
            }
            {
                label7 = new Label(this, SWT.NONE);
            }
            {
                buttonSourceBuffered = new Button(this, SWT.CHECK | SWT.LEFT);
                buttonSourceBuffered.setText("buffered");
            }
            {
                label5 = new Label(this, SWT.NONE);
                label5.setText("Username:");
            }
            {
                textSourceUsername = new Text(this, SWT.BORDER);
            }
            {
                label6 = new Label(this, SWT.NONE);
                label6.setText("Password:");
            }
            {
                textSourcePassword = new Text(this, SWT.BORDER);
            }
            {
                label8 = new Label(this, SWT.NONE);
            }
            {
                label3 = new Label(this, SWT.NONE);
                label3.setText("Destination:");
                GridData label3LData = new GridData();
                label3.setLayoutData(label3LData);
            }
            {
                textDestination = new Text(this, SWT.BORDER);
                GridData textDestinationLData = new GridData();
                textDestination.setToolTipText("Destination location");
                textDestinationLData.horizontalAlignment = GridData.FILL;
                textDestinationLData.horizontalSpan = 5;
                textDestinationLData.grabExcessHorizontalSpace = true;
                textDestination.setLayoutData(textDestinationLData);
            }
            {
                buttonBrowseDst = new Button(this, SWT.PUSH | SWT.CENTER);
                buttonBrowseDst.setText("...");
                GridData buttonBrowseDstLData = new GridData();
                buttonBrowseDst.setLayoutData(buttonBrowseDstLData);
                buttonBrowseDst.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent evt) {
                        buttonBrowseDstWidgetSelected(evt);
                    }
                });
            }
            {
                label9 = new Label(this, SWT.NONE);
            }
            {
                buttonDestinationBuffered = new Button(this, SWT.CHECK
                    | SWT.LEFT);
                buttonDestinationBuffered.setText("buffered");
            }
            {
                label10 = new Label(this, SWT.NONE);
                label10.setText("Username:");
            }
            {
                textDestinationUsername = new Text(this, SWT.BORDER);
            }
            {
                label11 = new Label(this, SWT.NONE);
                label11.setText("Password:");
            }
            {
                textDestinationPassword = new Text(this, SWT.BORDER);
            }
            {
                label12 = new Label(this, SWT.NONE);
            }
            {
                label16 = new Label(this, SWT.NONE);
                label16.setText("Type:");
            }
            {
                comboType = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
                comboType.addModifyListener(new ModifyListener() {
                    public void modifyText(ModifyEvent evt) {
                        if( comboType.getText().equals( "Publish/Update" ) )
                            labelTypeDescription.setText( "Needs buffer on destination.");
                        else if( comboType.getText().equals( "Backup" ) )
                            labelTypeDescription.setText( "Needs no buffering." );
                    }
                });
                comboType.add( "Publish/Update" );
                comboType.add( "Backup" );
            }
            {
                labelTypeDescription = new Label(this, SWT.NONE);
                labelTypeDescription.setText("Description");
            }
            {
                ruleSetGroup = new Group(this, SWT.NONE);
                GridLayout ruleSetGroupLayout = new GridLayout();
                GridData ruleSetGroupLData = new GridData();
                ruleSetGroupLData.horizontalSpan = 7;
                ruleSetGroupLData.widthHint = 443;
                ruleSetGroupLData.heightHint = 143;
                ruleSetGroupLData.horizontalIndent = 5;
                ruleSetGroup.setLayoutData(ruleSetGroupLData);
                ruleSetGroupLayout.makeColumnsEqualWidth = true;
                ruleSetGroupLayout.numColumns = 2;
                ruleSetGroup.setLayout(ruleSetGroupLayout);
                ruleSetGroup.setText("RuleSet");
                {
                    rbSimplyfiedRuleSet = new Button(ruleSetGroup, SWT.RADIO | SWT.LEFT);
                    rbSimplyfiedRuleSet.setText("Simple Rule Set");
                    rbSimplyfiedRuleSet.setSelection(true);
                    rbSimplyfiedRuleSet
                        .addSelectionListener(new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent evt) {
                                selectRuleSetButton(rbSimplyfiedRuleSet);
                            }
                        });
                }
                {
                    rbAdvancedRuleSet = new Button(ruleSetGroup, SWT.RADIO | SWT.LEFT);
                    rbAdvancedRuleSet.setText("Advanced Rule Set");
                    GridData rbAdvancedRuleSetLData = new GridData();
                    rbAdvancedRuleSetLData.widthHint = 112;
                    rbAdvancedRuleSetLData.heightHint = 16;
                    rbAdvancedRuleSet.setLayoutData(rbAdvancedRuleSetLData);
                    rbAdvancedRuleSet
                        .addSelectionListener(new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent evt) {
                                selectRuleSetButton(rbAdvancedRuleSet);
                            }
                        });
                }
                {
                    simplyfiedOptionsGroup = new Group(ruleSetGroup, SWT.NONE);
                    GridLayout simplyfiedOptionsGroupLayout = new GridLayout();
                    GridData simplyfiedOptionsGroupLData = new GridData();
                    simplyfiedOptionsGroupLData.widthHint = 187;
                    simplyfiedOptionsGroupLData.heightHint = 98;
                    simplyfiedOptionsGroupLData.verticalAlignment = GridData.BEGINNING;
                    simplyfiedOptionsGroup.setLayoutData(simplyfiedOptionsGroupLData);
                    simplyfiedOptionsGroupLayout.numColumns = 2;
                    simplyfiedOptionsGroup.setLayout(simplyfiedOptionsGroupLayout);
                    simplyfiedOptionsGroup.setText("Simple Rule Options");
                    {
                        syncSubsButton = new Button(simplyfiedOptionsGroup, SWT.CHECK | SWT.LEFT);
                        syncSubsButton.setText("Sync Subdirectories");
                        GridData syncSubsButtonLData = new GridData();
                        syncSubsButton
                            .setToolTipText("Recurre into subdirectories?");
                        syncSubsButtonLData.widthHint = 115;
                        syncSubsButtonLData.heightHint = 16;
                        syncSubsButtonLData.horizontalSpan = 2;
                        syncSubsButton.setLayoutData(syncSubsButtonLData);
                    }
                    {
                        deleteOnDestinationButton = new Button(simplyfiedOptionsGroup, SWT.CHECK | SWT.LEFT);
                        deleteOnDestinationButton
                            .setText("Delete on Destination");
                        GridData deleteOnDestinationButtonLData = new GridData();
                        deleteOnDestinationButton
                            .setToolTipText("Delete files missing on the source folder?");
                        deleteOnDestinationButtonLData.widthHint = 124;
                        deleteOnDestinationButtonLData.heightHint = 16;
                        deleteOnDestinationButtonLData.horizontalSpan = 2;
                        deleteOnDestinationButton.setLayoutData(deleteOnDestinationButtonLData);
                    }
                    {
                        label13 = new Label(simplyfiedOptionsGroup, SWT.NONE);
                        label13.setText("Ignore pattern");
                    }
                    {
                        textIgnorePatter = new Text(simplyfiedOptionsGroup, SWT.BORDER);
                        GridData textIgnorePatterLData = new GridData();
                        textIgnorePatter.setToolTipText("Ignore RegExp");
                        textIgnorePatterLData.heightHint = 13;
                        textIgnorePatterLData.grabExcessHorizontalSpace = true;
                        textIgnorePatterLData.horizontalAlignment = GridData.FILL;
                        textIgnorePatter.setLayoutData(textIgnorePatterLData);
                    }
                    {
                        label14 = new Label(simplyfiedOptionsGroup, SWT.NONE);
                        label14.setText("Accept pattern");
                    }
                    {
                        textAcceptPattern = new Text(simplyfiedOptionsGroup, SWT.BORDER);
                        GridData textAcceptPatternLData = new GridData();
                        textAcceptPatternLData.heightHint = 13;
                        textAcceptPatternLData.grabExcessHorizontalSpace = true;
                        textAcceptPatternLData.horizontalAlignment = GridData.FILL;
                        textAcceptPattern.setLayoutData(textAcceptPatternLData);
                    }
                }
                {
                    advancedRuleOptionsGroup = new Group(ruleSetGroup, SWT.NONE);
                    GridLayout advancedRuleOptionsGroupLayout = new GridLayout();
                    GridData advancedRuleOptionsGroupLData = new GridData();
                    advancedRuleOptionsGroup.setEnabled(false);
                    advancedRuleOptionsGroupLData.widthHint = 172;
                    advancedRuleOptionsGroupLData.heightHint = 31;
                    advancedRuleOptionsGroupLData.verticalAlignment = GridData.BEGINNING;
                    advancedRuleOptionsGroup.setLayoutData(advancedRuleOptionsGroupLData);
                    advancedRuleOptionsGroupLayout.numColumns = 2;
                    advancedRuleOptionsGroup.setLayout(advancedRuleOptionsGroupLayout);
                    advancedRuleOptionsGroup.setText("Advanced Rule Options");
                    {
                        label4 = new Label(advancedRuleOptionsGroup, SWT.NONE);
                        GridData label4LData = new GridData();
                        label4.setEnabled(false);
                        label4.setLayoutData(label4LData);
                        label4.setText("RuleSet:");
                    }
                    {
                        textRuleSet = new Text(advancedRuleOptionsGroup, SWT.BORDER);
                        GridData textRuleSetLData = new GridData();
                        textRuleSet.setEnabled(false);
                        textRuleSetLData.widthHint = 100;
                        textRuleSetLData.heightHint = 13;
                        textRuleSet.setLayoutData(textRuleSetLData);
                    }
                }
            }
			this.layout();
	
			postInitGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** Add your pre-init code in here 	*/
	public void preInitGUI(){
	}

	/** Add your post-init code in here 	*/
	public void postInitGUI()
	{
	    textSourcePassword.setEchoChar( '*' );
	    textDestinationPassword.setEchoChar( '*' );
	}

	public void setProfileManager( ProfileManager manager )
	{
	    this.profileManager = manager;
	}
	public void setProfileName( String name )
	{
	    this.profileName = name;
	    
	    if( profileName == null )
	        return;
	    
	    Profile p = profileManager.getProfile( profileName );
	    if( p == null )
	        throw new IllegalArgumentException( "profile does not exist" );
	        
        textName.setText( p.getName() );
        textSource.setText( p.getSource().getUri().toString() );
        buttonSourceBuffered.setSelection( "syncfiles".equals( p.getSource().getBufferStrategy() ) );
        if( p.getSource().getUsername() != null )
            textSourceUsername.setText( p.getSource().getUsername() );
        if( p.getSource().getPassword() != null )
            textSourcePassword.setText( p.getSource().getPassword() );
        textDestination.setText( p.getDestination().getUri().toString() );
        buttonDestinationBuffered.setSelection( "syncfiles".equals( p.getDestination().getBufferStrategy() ) );
        if( p.getDestination().getUsername() != null )
            textDestinationUsername.setText( p.getDestination().getUsername() );
        if( p.getDestination().getPassword() != null )
            textDestinationPassword.setText( p.getDestination().getPassword() );
        
        if( p.getSynchronizationType() != null && p.getSynchronizationType().length() > 0 )
             comboType.setText( p.getSynchronizationType() );
        else comboType.select( 0 );
        
        RuleSetDescriptor ruleSetDescriptor = p.getRuleSet();
        // TODO [Michele] I don't like this extend use of instanceof.
        // I'll try to find a better way soon.
        if (ruleSetDescriptor instanceof SimplyfiedRuleSetDescriptor) {
        	selectRuleSetButton(rbSimplyfiedRuleSet);
        	rbSimplyfiedRuleSet.setSelection(true);
        	rbAdvancedRuleSet.setSelection(false);
        	SimplyfiedRuleSetDescriptor simpleDesc = (SimplyfiedRuleSetDescriptor)ruleSetDescriptor;
        	syncSubsButton.setSelection(simpleDesc.isSyncSubDirs());
        	deleteOnDestinationButton.setSelection(simpleDesc.isDeleteOnDestination());
        	textIgnorePatter.setText(simpleDesc.getIgnorePattern());
        	textAcceptPattern.setText(simpleDesc.getTakePattern());
        } else {
        	selectRuleSetButton(rbAdvancedRuleSet);
        	rbSimplyfiedRuleSet.setSelection(false);
        	rbAdvancedRuleSet.setSelection(true);
        	AdvancedRuleSetDescriptor advDesc = (AdvancedRuleSetDescriptor) ruleSetDescriptor;
        	textRuleSet.setText(advDesc.getRuleSetName());
        }
	}
	
	public static void showProfile( Shell parent, ProfileManager manager, String name ){
		try {
		    /*
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			shell.setText( "Profile Details" );
			ProfileDetails inst = new ProfileDetails(shell, SWT.NULL);
			inst.setProfileManager( manager );
			inst.setProfileName( name );
			shell.setImage( new Image( display, "images/Button_Edit.gif" ) );
			shell.setLayout(new org.eclipse.swt.layout.FillLayout());
			shell.setSize( shell.computeSize( inst.getSize().x, inst.getSize().y ) );
			shell.open();
			*/
		    WizardDialog dialog = new WizardDialog( parent );
		    ProfileDetailsPage page = new ProfileDetailsPage( dialog, manager, name );
		    dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void apply()
	{
	    ConnectionDescription src, dst;
	    try {
	        src = new ConnectionDescription( textSource.getText(), "" );
	        if( buttonSourceBuffered.getSelection() )
	            src.setBufferStrategy( "syncfiles" );
	        if( textSourceUsername.getText().length() > 0 )
	        {
	            src.setUsername( textSourceUsername.getText() );
	            src.setPassword( textSourcePassword.getText() );
	        }
	        dst = new ConnectionDescription( textDestination.getText(), "" );
	        if( buttonDestinationBuffered.getSelection() )
	            dst.setBufferStrategy( "syncfiles" );
	        if( textDestinationUsername.getText().length() > 0 )
	        {
	            dst.setUsername( textDestinationUsername.getText() );
	            dst.setPassword( textDestinationPassword.getText() );
	        }
	    } catch( Exception e ) {
	        MessageBox mb = new MessageBox( this.getShell(), SWT.ICON_ERROR );
	        mb.setText( "Exception" );
            mb.setMessage( "The following error occured: "+e.toString() );
            mb.open();
            return;
	    }

	    if( profileName == null || !textName.getText().equals( profileName ) )
	    {
	        Profile pr = profileManager.getProfile( textName.getText() );
	        if( pr != null )
	        {
	            MessageBox mb = new MessageBox( this.getShell(), SWT.ICON_ERROR );
	            mb.setText( "Duplicate Entry" );
	            mb.setMessage( "A Profile with the same name already exists." );
	            mb.open();
	            return;
	        }
	    }
	    
	    Profile p;
	    
		RuleSetDescriptor ruleSetDescriptor = null;
    	if (rbSimplyfiedRuleSet.getSelection()) {
			ruleSetDescriptor = new SimplyfiedRuleSetDescriptor(syncSubsButton.getSelection(), 
					deleteOnDestinationButton.getSelection(),
					textIgnorePatter.getText(),
					textAcceptPattern.getText());
    	}
    	if (rbAdvancedRuleSet.getSelection()) {
    		String ruleSetName = textRuleSet.getText();
    		ruleSetDescriptor = new AdvancedRuleSetDescriptor(ruleSetName);
    	}

    	if( profileName == null )
        {
            p = new Profile( textName.getText(), src, dst, ruleSetDescriptor );
            p.setSynchronizationType( comboType.getText() );
            p.setDescription( textDescription.getText() );
            profileManager.addProfile( p );
        } else {
            p = profileManager.getProfile( profileName );
            p.setName( textName.getText() );
            p.setDescription( textDescription.getText() );
            p.setSynchronizationType( comboType.getText() );
            p.setSource( src );
            p.setDestination( dst );
    		
            p.setRuleSet( ruleSetDescriptor );
        }
        profileManager.save();
	}

	/** Auto-generated event handler method */
	protected void buttonCancelWidgetSelected(SelectionEvent evt){
		getShell().dispose();
	}

	/** Auto-generated event handler method */
	protected void buttonBrowseSrcWidgetSelected(SelectionEvent evt){
		DirectoryDialog dd = new DirectoryDialog( getShell() );
		dd.setMessage( "Choose local source directory." );
		String str = dd.open();
		if( str != null )
		    textSource.setText( new File( str ).toURI().toString() );
	}

	/** Auto-generated event handler method */
	protected void buttonBrowseDstWidgetSelected(SelectionEvent evt){
		DirectoryDialog dd = new DirectoryDialog( getShell() );
		dd.setMessage( "Choose local source directory." );
		String str = dd.open();
		if( str != null )
		    textDestination.setText( new File( str ).toURI().toString() );

	}
	
	protected void selectRuleSetButton(Button button) {
		
		if (button.equals(rbSimplyfiedRuleSet)) {
			advancedRuleOptionsGroup.setEnabled(false);
			label4.setEnabled(false);
			textRuleSet.setEnabled(false);
			simplyfiedOptionsGroup.setEnabled(true);
			deleteOnDestinationButton.setEnabled(true);
			syncSubsButton.setEnabled(true);
		}
		else {
			advancedRuleOptionsGroup.setEnabled(true);
			label4.setEnabled(true);
			textRuleSet.setEnabled(true);
			simplyfiedOptionsGroup.setEnabled(false);
			deleteOnDestinationButton.setEnabled(false);
			syncSubsButton.setEnabled(false);
		}
		
	}
}
