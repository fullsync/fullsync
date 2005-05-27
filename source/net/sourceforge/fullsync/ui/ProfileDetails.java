package net.sourceforge.fullsync.ui;

import java.io.File;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuleSetDescriptor;
import net.sourceforge.fullsync.impl.AdvancedRuleSetDescriptor;
import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;
import net.sourceforge.fullsync.schedule.Schedule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
	private Combo comboPatternsType;
	private Button buttonResetError;
	private Button buttonEnabled;
	private Button buttonScheduling;
	private Label label17;
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
	private Text textIgnorePattern;
	private Label label13;
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

			GridLayout thisLayout = new GridLayout(7, true);
			thisLayout.marginWidth = 10;
			thisLayout.marginHeight = 10;
			thisLayout.numColumns = 7;
			thisLayout.makeColumnsEqualWidth = false;
			thisLayout.horizontalSpacing = 5;
			thisLayout.verticalSpacing = 5;
			this.setLayout(thisLayout);
            {
                label1 = new Label(this, SWT.NONE);
                GridData label1LData = new GridData();
                label1.setLayoutData(label1LData);
                label1.setText(Messages.getString("ProfileDetails.Name.Label")+":"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            {
                textName = new Text(this, SWT.BORDER);
                GridData textNameLData = new GridData();
                textName.setToolTipText(Messages.getString("ProfileDetails.Name.ToolTip")); //$NON-NLS-1$
                textNameLData.horizontalAlignment = GridData.FILL;
                textNameLData.horizontalSpan = 6;
                textName.setLayoutData(textNameLData);
            }
            {
                label15 = new Label(this, SWT.NONE);
                label15.setText(Messages.getString("ProfileDetails.Description.Label")+":"); //$NON-NLS-1$ //$NON-NLS-2$
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
                label2.setText(Messages.getString("ProfileDetails.Source.Label")+":"); //$NON-NLS-1$ //$NON-NLS-2$
                GridData label2LData = new GridData();
                label2.setLayoutData(label2LData);
            }
            {
                textSource = new Text(this, SWT.BORDER);
                GridData textSourceLData = new GridData();
                textSource.setToolTipText(Messages.getString("ProfileDetails.Source.ToolTip")); //$NON-NLS-1$
                textSourceLData.horizontalAlignment = GridData.FILL;
                textSourceLData.horizontalSpan = 5;
                textSourceLData.grabExcessHorizontalSpace = true;
                textSource.setLayoutData(textSourceLData);
            }
            {
                buttonBrowseSrc = new Button(this, SWT.PUSH | SWT.CENTER);
                buttonBrowseSrc.setText("..."); //$NON-NLS-1$
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
                buttonSourceBuffered.setText(Messages.getString("ProfileDetails.Buffered.Label")); //$NON-NLS-1$
                buttonSourceBuffered.setEnabled( false );
            }
            {
                label5 = new Label(this, SWT.NONE);
                label5.setText(Messages.getString("ProfileDetails.Username.Label")+":"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            {
                textSourceUsername = new Text(this, SWT.BORDER);
                GridData textSourceUsernameLData = new GridData();
                textSourceUsernameLData.horizontalAlignment = GridData.FILL;
                textSourceUsernameLData.grabExcessHorizontalSpace = true;
                textSourceUsername.setLayoutData(textSourceUsernameLData);
            }
            {
                label6 = new Label(this, SWT.NONE);
                label6.setText(Messages.getString("ProfileDetails.Password.Label")+":"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            {
                textSourcePassword = new Text(this, SWT.BORDER);
                GridData textSourcePasswordLData = new GridData();
                textSourcePasswordLData.horizontalAlignment = GridData.FILL;
                textSourcePasswordLData.grabExcessHorizontalSpace = true;
                textSourcePassword.setLayoutData(textSourcePasswordLData);
            }
            {
                label8 = new Label(this, SWT.NONE);
            }
            {
                label3 = new Label(this, SWT.NONE);
                label3.setText(Messages.getString("ProfileDetails.Destination.Label")); //$NON-NLS-1$
                GridData label3LData = new GridData();
                label3.setLayoutData(label3LData);
            }
            {
                textDestination = new Text(this, SWT.BORDER);
                GridData textDestinationLData = new GridData();
                textDestination.setToolTipText(Messages.getString("ProfileDetails.Destination.ToolTip")); //$NON-NLS-1$
                textDestinationLData.horizontalAlignment = GridData.FILL;
                textDestinationLData.horizontalSpan = 5;
                textDestinationLData.grabExcessHorizontalSpace = true;
                textDestination.setLayoutData(textDestinationLData);
            }
            {
                buttonBrowseDst = new Button(this, SWT.PUSH | SWT.CENTER);
                buttonBrowseDst.setText("..."); //$NON-NLS-1$
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
                buttonDestinationBuffered = new Button(this, SWT.CHECK | SWT.LEFT);
                buttonDestinationBuffered.setText(Messages.getString("ProfileDetails.Buffered.Label")); //$NON-NLS-1$
                //buttonDestinationBuffered.setEnabled( false );
            }
            {
                label10 = new Label(this, SWT.NONE);
                label10.setText(Messages.getString("ProfileDetails.Username.Label")+":"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            {
                textDestinationUsername = new Text(this, SWT.BORDER);
                GridData textDestinationUsernameLData = new GridData();
                textDestinationUsernameLData.horizontalAlignment = GridData.FILL;
                textDestinationUsername.setLayoutData(textDestinationUsernameLData);
            }
            {
                label11 = new Label(this, SWT.NONE);
                label11.setText(Messages.getString("ProfileDetails.Password.Label")+":"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            {
                textDestinationPassword = new Text(this, SWT.BORDER);
                GridData textDestinationPasswordLData = new GridData();
                textDestinationPasswordLData.horizontalAlignment = GridData.FILL;
                textDestinationPassword.setLayoutData(textDestinationPasswordLData);
            }
            {
                label12 = new Label(this, SWT.NONE);
            }
            {
                label16 = new Label(this, SWT.NONE);
                label16.setText(Messages.getString("ProfileDetails.Type.Label")); //$NON-NLS-1$
            }
            {
                comboType = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
                comboType.addModifyListener(new ModifyListener() {
                    public void modifyText(ModifyEvent evt) {
                        if( comboType.getText().equals( "Publish/Update" ) ) //$NON-NLS-1$
                        {
                            labelTypeDescription.setText( Messages.getString("ProfileDetails.ProfileDescription.Publish") ); //$NON-NLS-1$
                            buttonSourceBuffered.setSelection( false );
                            buttonDestinationBuffered.setSelection( true );
                        } else if( comboType.getText().equals( "Backup Copy" ) ) { //$NON-NLS-1$
                            labelTypeDescription.setText( Messages.getString("ProfileDetails.ProfileDescription.BackupCopy") ); //$NON-NLS-1$
	                        buttonSourceBuffered.setSelection( false );
	                        buttonDestinationBuffered.setSelection( false );
                        } else if( comboType.getText().equals( "Exact Copy" ) ) { //$NON-NLS-1$
                            labelTypeDescription.setText( Messages.getString("ProfileDetails.ProfileDescription.ExactCopy") ); //$NON-NLS-1$
	                        buttonSourceBuffered.setSelection( false );
	                        buttonDestinationBuffered.setSelection( false );
                        } else if( comboType.getText().equals( "Two Way Sync" ) ) { //$NON-NLS-1$
                            labelTypeDescription.setText( Messages.getString("ProfileDetails.ProfileDescription.TwoWaySync") ); //$NON-NLS-1$
                            buttonSourceBuffered.setSelection( false );
                            buttonDestinationBuffered.setSelection( false );
                        }
                   }
                });
            }
            {
                labelTypeDescription = new Label(this, SWT.WRAP);
                labelTypeDescription.setText(Messages.getString("ProfileDetails.Description.Label")); //$NON-NLS-1$
                GridData labelTypeDescriptionLData = new GridData();
                labelTypeDescriptionLData.heightHint = 40;
                labelTypeDescriptionLData.horizontalSpan = 5;
                labelTypeDescriptionLData.horizontalAlignment = GridData.FILL;
                labelTypeDescription.setLayoutData(labelTypeDescriptionLData);
            }
            {
                label17 = new Label(this, SWT.NONE);
                label17.setText(Messages.getString("ProfileDetails.Scheduling")); //$NON-NLS-1$
            }
            {
                buttonScheduling = new Button(this, SWT.PUSH | SWT.CENTER);
                buttonScheduling.setText(Messages.getString("ProfileDetails.Edit_Scheduling")); //$NON-NLS-1$
                buttonScheduling.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent evt) {
                        ScheduleSelectionDialog dialog = new ScheduleSelectionDialog( getShell(), SWT.NULL );
                        dialog.setSchedule( (Schedule)buttonScheduling.getData() );
                        dialog.open();
                        
                        buttonScheduling.setData( dialog.getSchedule() );
                    }
                });
            }
            {
                buttonEnabled = new Button(this, SWT.CHECK | SWT.RIGHT);
                buttonEnabled.setText(Messages.getString("ProfileDetails.Enabled")); //$NON-NLS-1$
            }
            {
                buttonResetError = new Button(this, SWT.CHECK | SWT.RIGHT);
                buttonResetError.setText(Messages.getString("ProfileDetails.Reset_ErrorFlag")); //$NON-NLS-1$
                GridData buttonResetErrorLData = new GridData();
                buttonResetErrorLData.horizontalSpan = 3;
                buttonResetError.setLayoutData(buttonResetErrorLData);
            }
            {
                ruleSetGroup = new Group(this, SWT.NONE);
                GridLayout ruleSetGroupLayout = new GridLayout();
                GridData ruleSetGroupLData = new GridData();
                ruleSetGroupLData.horizontalSpan = 7;
                ruleSetGroupLData.horizontalIndent = 5;
                ruleSetGroupLData.horizontalAlignment = GridData.FILL;
                ruleSetGroupLData.verticalAlignment = GridData.BEGINNING;
                ruleSetGroup.setLayoutData(ruleSetGroupLData);
                ruleSetGroupLayout.numColumns = 2;
                ruleSetGroupLayout.makeColumnsEqualWidth = true;
                ruleSetGroupLayout.horizontalSpacing = 20;
                ruleSetGroup.setLayout(ruleSetGroupLayout);
                ruleSetGroup.setText(Messages.getString("ProfileDetails.RuleSet")); //$NON-NLS-1$
                {
                    rbSimplyfiedRuleSet = new Button(ruleSetGroup, SWT.RADIO | SWT.LEFT);
                    rbSimplyfiedRuleSet.setText(Messages.getString("ProfileDetails.Simple_Rule_Set")); //$NON-NLS-1$
                    rbSimplyfiedRuleSet.setSelection(true);
                    GridData rbSimplyfiedRuleSetLData = new GridData();
                    rbSimplyfiedRuleSetLData.grabExcessHorizontalSpace = true;
                    rbSimplyfiedRuleSetLData.horizontalAlignment = GridData.FILL;
                    rbSimplyfiedRuleSet.setLayoutData(rbSimplyfiedRuleSetLData);
                    rbSimplyfiedRuleSet
                        .addSelectionListener(new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent evt) {
                                selectRuleSetButton(rbSimplyfiedRuleSet);
                            }
                        });
                }
                {
                    rbAdvancedRuleSet = new Button(ruleSetGroup, SWT.RADIO | SWT.LEFT);
                    rbAdvancedRuleSet.setText(Messages.getString("ProfileDetails.Advanced_Rule_Set")); //$NON-NLS-1$
                    GridData rbAdvancedRuleSetLData = new GridData();
                    rbAdvancedRuleSetLData.heightHint = 16;
                    rbAdvancedRuleSetLData.grabExcessHorizontalSpace = true;
                    rbAdvancedRuleSetLData.horizontalAlignment = GridData.FILL;
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
                    simplyfiedOptionsGroupLData.verticalAlignment = GridData.BEGINNING;
                    simplyfiedOptionsGroupLData.grabExcessHorizontalSpace = true;
                    simplyfiedOptionsGroupLData.horizontalAlignment = GridData.FILL;
                    simplyfiedOptionsGroup.setLayoutData(simplyfiedOptionsGroupLData);
                    simplyfiedOptionsGroupLayout.numColumns = 3;
                    simplyfiedOptionsGroup.setLayout(simplyfiedOptionsGroupLayout);
                    simplyfiedOptionsGroup.setText(Messages.getString("ProfileDetails.Simple_Rule_Options")); //$NON-NLS-1$
                    {
                        syncSubsButton = new Button(simplyfiedOptionsGroup, SWT.CHECK | SWT.LEFT);
                        syncSubsButton.setText(Messages.getString("ProfileDetails.Sync_SubDirs")); //$NON-NLS-1$
                        GridData syncSubsButtonLData = new GridData();
                        syncSubsButton
                            .setToolTipText(Messages.getString("ProfileDetails.Rucurre")); //$NON-NLS-1$
                        syncSubsButtonLData.horizontalSpan = 3;
                        syncSubsButton.setLayoutData(syncSubsButtonLData);
                    }
                    {
                        label13 = new Label(simplyfiedOptionsGroup, SWT.NONE);
                        label13.setText(Messages.getString("ProfileDetails.Ingore_Pattern")); //$NON-NLS-1$
                    }
                    {
                        textIgnorePattern = new Text(simplyfiedOptionsGroup, SWT.BORDER);
                        GridData textIgnorePatterLData = new GridData();
                        textIgnorePattern.setToolTipText(Messages.getString("ProfileDetails.Ignore_ToolTip")); //$NON-NLS-1$
                        textIgnorePatterLData.heightHint = 13;
                        //textIgnorePatterLData.widthHint = 100;
                        textIgnorePatterLData.grabExcessHorizontalSpace = true;
                        textIgnorePatterLData.horizontalAlignment = GridData.FILL;
                        textIgnorePattern.setLayoutData(textIgnorePatterLData);
                    }
					{
						comboPatternsType = new Combo(simplyfiedOptionsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
						GridData comboPatternsTypeLData = new GridData();
						comboPatternsTypeLData.verticalSpan = 2;
						comboPatternsType.setLayoutData(comboPatternsTypeLData);
					}
                    {
                        label14 = new Label(simplyfiedOptionsGroup, SWT.NONE);
                        label14.setText(Messages.getString("ProfileDetails.Accept_Pattern")); //$NON-NLS-1$
                    }
                    {
                        textAcceptPattern = new Text(simplyfiedOptionsGroup, SWT.BORDER);
                        GridData textAcceptPatternLData = new GridData();
                        textAcceptPattern.setToolTipText(Messages.getString("ProfileDetails.Accept_ToolTip")); //$NON-NLS-1$
                        textAcceptPatternLData.heightHint = 13;
                        //textAcceptPatternLData.widthHint = 100;
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
                    advancedRuleOptionsGroupLData.heightHint = 31;
                    advancedRuleOptionsGroupLData.verticalAlignment = GridData.BEGINNING;
                    advancedRuleOptionsGroupLData.grabExcessHorizontalSpace = true;
                    advancedRuleOptionsGroupLData.horizontalAlignment = GridData.FILL;
                    advancedRuleOptionsGroup.setLayoutData(advancedRuleOptionsGroupLData);
                    advancedRuleOptionsGroupLayout.numColumns = 2;
                    advancedRuleOptionsGroup.setLayout(advancedRuleOptionsGroupLayout);
                    advancedRuleOptionsGroup.setText(Messages.getString("ProfileDetails.Advanced_Rule_Options")); //$NON-NLS-1$
                    {
                        label4 = new Label(advancedRuleOptionsGroup, SWT.NONE);
                        GridData label4LData = new GridData();
                        label4.setEnabled(false);
                        label4.setLayoutData(label4LData);
                        label4.setText(Messages.getString("ProfileDetails.RuleSet_2")); //$NON-NLS-1$
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
            comboType.add( "Publish/Update" ); //$NON-NLS-1$
            comboType.add( "Backup Copy" ); //$NON-NLS-1$
            comboType.add( "Exact Copy" ); //$NON-NLS-1$
            comboType.add( "Two Way Sync" ); //$NON-NLS-1$

            comboPatternsType.add("RegExp");
			comboPatternsType.add("Wildcard");

			this.layout();
			this.setSize(500, 409);
	
			postInitGUI();
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
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
	    
	    comboType.select(0);
	    comboPatternsType.select(0);
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
	        throw new IllegalArgumentException( Messages.getString("ProfileDetails.profile_does_not_exist") ); //$NON-NLS-1$
	        
        textName.setText( p.getName() );
        textDescription.setText( p.getDescription() );
        textSource.setText( p.getSource().getUri().toString() );
        buttonSourceBuffered.setSelection( "syncfiles".equals( p.getSource().getBufferStrategy() ) ); //$NON-NLS-1$
        if( p.getSource().getUsername() != null )
            textSourceUsername.setText( p.getSource().getUsername() );
        if( p.getSource().getPassword() != null )
            textSourcePassword.setText( p.getSource().getPassword() );
        textDestination.setText( p.getDestination().getUri().toString() );
        buttonDestinationBuffered.setSelection( "syncfiles".equals( p.getDestination().getBufferStrategy() ) ); //$NON-NLS-1$
        if( p.getDestination().getUsername() != null )
            textDestinationUsername.setText( p.getDestination().getUsername() );
        if( p.getDestination().getPassword() != null )
            textDestinationPassword.setText( p.getDestination().getPassword() );
        
        if( p.getSynchronizationType() != null && p.getSynchronizationType().length() > 0 )
             comboType.setText( p.getSynchronizationType() );
        else comboType.select( 0 );
        
        buttonScheduling.setData( p.getSchedule() );
        buttonEnabled.setSelection( p.isEnabled() );
        
        RuleSetDescriptor ruleSetDescriptor = p.getRuleSet();

        if (ruleSetDescriptor instanceof SimplyfiedRuleSetDescriptor) {
        	selectRuleSetButton(rbSimplyfiedRuleSet);
        	rbSimplyfiedRuleSet.setSelection(true);
        	rbAdvancedRuleSet.setSelection(false);
        	SimplyfiedRuleSetDescriptor simpleDesc = (SimplyfiedRuleSetDescriptor)ruleSetDescriptor;
        	syncSubsButton.setSelection(simpleDesc.isSyncSubDirs());
        	textIgnorePattern.setText(simpleDesc.getIgnorePattern());
        	textAcceptPattern.setText(simpleDesc.getTakePattern());
        	comboPatternsType.setText(simpleDesc.getPatternsType());
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
		    WizardDialog dialog = new WizardDialog( parent, SWT.APPLICATION_MODAL );
		    ProfileDetailsPage page = new ProfileDetailsPage( dialog, manager, name );
		    dialog.show();
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}
	public void apply()
	{
	    ConnectionDescription src, dst;
	    try {
	        src = new ConnectionDescription( textSource.getText(), "" ); //$NON-NLS-1$
	        if( buttonSourceBuffered.getSelection() )
	            src.setBufferStrategy( "syncfiles" ); //$NON-NLS-1$
	        if( textSourceUsername.getText().length() > 0 )
	        {
	            src.setUsername( textSourceUsername.getText() );
	            src.setPassword( textSourcePassword.getText() );
	        }
	        dst = new ConnectionDescription( textDestination.getText(), "" ); //$NON-NLS-1$
	        if( buttonDestinationBuffered.getSelection() )
	            dst.setBufferStrategy( "syncfiles" ); //$NON-NLS-1$
	        if( textDestinationUsername.getText().length() > 0 )
	        {
	            dst.setUsername( textDestinationUsername.getText() );
	            dst.setPassword( textDestinationPassword.getText() );
	        }
	    } catch( Exception e ) {
	        ExceptionHandler.reportException( e );
            return;
	    }

	    if( profileName == null || !textName.getText().equals( profileName ) )
	    {
	        Profile pr = profileManager.getProfile( textName.getText() );
	        if( pr != null )
	        {
	            MessageBox mb = new MessageBox( this.getShell(), SWT.ICON_ERROR );
	            mb.setText( Messages.getString("ProfileDetails.Duplicate_Entry") ); //$NON-NLS-1$
	            mb.setMessage( Messages.getString("ProfileDetails.Profile_already_exists") ); //$NON-NLS-1$
	            mb.open();
	            return;
	        }
	    }
	    
	    Profile p;
	    
		RuleSetDescriptor ruleSetDescriptor = null;
    	if (rbSimplyfiedRuleSet.getSelection()) {
			ruleSetDescriptor = new SimplyfiedRuleSetDescriptor(syncSubsButton.getSelection(), 
					textIgnorePattern.getText(),
					textAcceptPattern.getText(),
					comboPatternsType.getText());
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
            p.setSchedule( (Schedule)buttonScheduling.getData() );
            p.setEnabled( buttonEnabled.getSelection() );
            if( buttonResetError.getSelection() )
                p.setLastError( 0, null );
            profileManager.addProfile( p );
        } else {
            p = profileManager.getProfile( profileName );
            p.beginUpdate();
            p.setName( textName.getText() );
            p.setDescription( textDescription.getText() );
            p.setSynchronizationType( comboType.getText() );
            p.setSource( src );
            p.setDestination( dst );
            p.setSchedule( (Schedule)buttonScheduling.getData() );
            p.setEnabled( buttonEnabled.getSelection() );
    		
            p.setRuleSet( ruleSetDescriptor );
            if( buttonResetError.getSelection() )
                p.setLastError( 0, null );
            p.endUpdate();
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
		dd.setMessage( Messages.getString("ProfileDetails.Choose_source_dir") ); //$NON-NLS-1$
		String str = dd.open();
		if( str != null )
		    textSource.setText( new File( str ).toURI().toString() );
	}

	/** Auto-generated event handler method */
	protected void buttonBrowseDstWidgetSelected(SelectionEvent evt){
		DirectoryDialog dd = new DirectoryDialog( getShell() );
		dd.setMessage( Messages.getString("ProfileDetails.Choose_source_dir") ); //$NON-NLS-1$
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
			syncSubsButton.setEnabled(true);
			label13.setEnabled(true);
			label14.setEnabled(true);
			textAcceptPattern.setEnabled(true);
			textIgnorePattern.setEnabled(true);
			comboPatternsType.setEnabled(true);
		}
		else {
			advancedRuleOptionsGroup.setEnabled(true);
			label4.setEnabled(true);
			textRuleSet.setEnabled(true);
			simplyfiedOptionsGroup.setEnabled(false);
			syncSubsButton.setEnabled(false);
			label13.setEnabled(false);
			label14.setEnabled(false);
			textAcceptPattern.setEnabled(false);
			textIgnorePattern.setEnabled(false);
			comboPatternsType.setEnabled(false);
		}
		
	}
}
