package net.sourceforge.fullsync.ui;

import java.io.File;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuleSetDescriptor;
import net.sourceforge.fullsync.impl.AdvancedRuleSetDescriptor;
import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.schedule.Schedule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
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
public class ProfileDetailsTabbed extends org.eclipse.swt.widgets.Composite {

	private ProfileManager profileManager;
	private Combo comboFilterType;
	private Label label18;
	private Button buttonFileFilter;
//	private Combo comboPatternsType;
	private Button buttonResetError;
	private Button buttonEnabled;
	private Button buttonScheduling;
	private Label labelTypeDescription;
	private Combo comboType;
	private Label label16;
	private Text textDescription;
	private Label label15;
	private Label label1;
	private Text textRuleSet;
	private Label label4;
	private Group advancedRuleOptionsGroup;
//	private Text textAcceptPattern;
//	private Label label14;
//	private Text textIgnorePattern;
	private Group groupDestination;
	private Label labelSourceUrl;
	private TreeItem treeItemFilters;
	private TreeItem treeItemLocations;
	private TreeItem treeItemGeneral;
	private Group groupAutomated;
	private Group groupGeneral;
	private Group groupSource;
	private Composite compositeTabFilters;
	private Composite compositeTabLocations;
	private Composite compositeTabGeneral;
	private Composite compositeMain;
	private Tree treeTabs;
	private SashForm sashForm;
	private Button buttonUseFileFilter;
	private Label labelFilterDescription;
//	private Label label13;
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
	private Button buttonBrowseDst;
	private Text textDestination;
	private Label labelDestinationUrl;
	private Label label8;
	private Text textSourcePassword;
	private Label label6;
	private Text textSourceUsername;
	private Label label5;
	private Button buttonSourceBuffered;
	private Button buttonBrowseSrc;
	private Text textSource;
	private Text textName;
	
	private String profileName;
	
	private FileFilter filter;
	
	public ProfileDetailsTabbed(Composite parent, int style) {
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

			GridLayout thisLayout = new GridLayout();
			thisLayout.marginWidth = 0;
			thisLayout.marginHeight = 0;
			this.setLayout(thisLayout);
            {
                sashForm = new SashForm(this, SWT.NONE);
                GridData sashFormLData = new GridData();
                sashFormLData.grabExcessHorizontalSpace = true;
                sashFormLData.grabExcessVerticalSpace = true;
                sashFormLData.horizontalAlignment = GridData.FILL;
                sashFormLData.verticalAlignment = GridData.FILL;
                sashForm.setLayoutData(sashFormLData);
                sashForm.setSize(60, 30);
                {
                    treeTabs = new Tree(sashForm, SWT.NONE);
                    treeTabs.addSelectionListener(new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent evt) {
                            treeTabsWidgetSelected(evt);
                        }
                    });
                    {
                        treeItemGeneral = new TreeItem(treeTabs, SWT.NONE);
                        treeItemGeneral.setText("General");
                    }
                    {
                        treeItemLocations = new TreeItem(treeTabs, SWT.NONE);
                        treeItemLocations.setText("Locations");
                    }
                    {
                        treeItemFilters = new TreeItem(treeTabs, SWT.NONE);
                        treeItemFilters.setText("Filters");
                    }
                }
                {
                    compositeMain = new Composite(sashForm, SWT.NONE);
                    StackLayout compositeMainLayout = new StackLayout();
                    compositeMain.setLayout(compositeMainLayout);
                    {
                        compositeTabGeneral = new Composite(
                            compositeMain,
                            SWT.NONE);
                        treeItemGeneral.setData(compositeTabGeneral);
                        GridLayout compositeTabGeneralLayout = new GridLayout();
                        compositeTabGeneral.setLayout(compositeTabGeneralLayout);
                        {
                            groupGeneral = new Group(
                                compositeTabGeneral,
                                SWT.NONE);
                            GridLayout groupGeneralLayout = new GridLayout();
                            groupGeneralLayout.numColumns = 2;
                            groupGeneral.setLayout(groupGeneralLayout);
                            GridData groupGeneralLData = new GridData();
                            groupGeneralLData.grabExcessHorizontalSpace = true;
                            groupGeneralLData.horizontalAlignment = GridData.FILL;
                            groupGeneral.setLayoutData(groupGeneralLData);
                            groupGeneral.setText("General");
                            {
                                label1 = new Label(groupGeneral, SWT.NONE);
                                label1
                                    .setText(Messages
                                        .getString("ProfileDetails.Name.Label") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                            {
                            	GridData textNameLData = new GridData();
                            	textNameLData.grabExcessHorizontalSpace = true;
                            	textNameLData.horizontalAlignment = GridData.FILL;
                                textName = new Text(groupGeneral, SWT.BORDER);
                                textName.setLayoutData(textNameLData);
                                textName.setToolTipText(Messages
                                    .getString("ProfileDetails.Name.ToolTip")); //$NON-NLS-1$
                            }
                            {
                                label15 = new Label(groupGeneral, SWT.NONE);
                                label15
                                    .setText(Messages
                                        .getString("ProfileDetails.Description.Label") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                            {
                            	GridData textDescriptionLData = new GridData();
                            	textDescriptionLData.horizontalAlignment = GridData.FILL;
                                textDescription = new Text(
                                    groupGeneral,
                                    SWT.BORDER);
                                textDescription.setLayoutData(textDescriptionLData);
                            }
                            {
                                label16 = new Label(groupGeneral, SWT.NONE);
                                label16.setText(Messages
                                    .getString("ProfileDetails.Type.Label")); //$NON-NLS-1$
                            }
                            {
                                comboType = new Combo(
                                    groupGeneral,
                                    SWT.DROP_DOWN | SWT.READ_ONLY);
                                comboType
                                    .addModifyListener(new ModifyListener() {
                                        public void modifyText(ModifyEvent evt) {
                                            if (comboType.getText().equals(
                                                "Publish/Update")) //$NON-NLS-1$
                                            {
                                                labelTypeDescription
                                                    .setText(Messages
                                                        .getString("ProfileDetails.ProfileDescription.Publish")); //$NON-NLS-1$
                                                buttonSourceBuffered
                                                    .setSelection(false);
                                                buttonDestinationBuffered
                                                    .setSelection(true);
                                            } else if (comboType.getText()
                                                .equals("Backup Copy")) { //$NON-NLS-1$
                                                labelTypeDescription
                                                    .setText(Messages
                                                        .getString("ProfileDetails.ProfileDescription.BackupCopy")); //$NON-NLS-1$
                                                buttonSourceBuffered
                                                    .setSelection(false);
                                                buttonDestinationBuffered
                                                    .setSelection(false);
                                            } else if (comboType.getText()
                                                .equals("Exact Copy")) { //$NON-NLS-1$
                                                labelTypeDescription
                                                    .setText(Messages
                                                        .getString("ProfileDetails.ProfileDescription.ExactCopy")); //$NON-NLS-1$
                                                buttonSourceBuffered
                                                    .setSelection(false);
                                                buttonDestinationBuffered
                                                    .setSelection(false);
                                            } else if (comboType.getText()
                                                .equals("Two Way Sync")) { //$NON-NLS-1$
                                                labelTypeDescription
                                                    .setText(Messages
                                                        .getString("ProfileDetails.ProfileDescription.TwoWaySync")); //$NON-NLS-1$
                                                buttonSourceBuffered
                                                    .setSelection(false);
                                                buttonDestinationBuffered
                                                    .setSelection(false);
                                            }
                                        }
                                    });
                            }
                            {
                            	GridData labelTypeDescriptionLData = new GridData();
                            	labelTypeDescriptionLData.horizontalSpan = 2;
                            	labelTypeDescriptionLData.horizontalAlignment = GridData.FILL;
                                labelTypeDescription = new Label(
                                    groupGeneral,
                                    SWT.WRAP);
                                labelTypeDescription.setLayoutData(labelTypeDescriptionLData);
                                labelTypeDescription
                                    .setText(Messages
                                        .getString("ProfileDetails.Description.Label")); //$NON-NLS-1$
                            }
                        }
                        {
                            groupAutomated = new Group(
                                compositeTabGeneral,
                                SWT.NONE);
                            GridLayout groupAutomatedLayout = new GridLayout();
                            groupAutomatedLayout.makeColumnsEqualWidth = true;
                            groupAutomatedLayout.numColumns = 2;
                            groupAutomated.setLayout(groupAutomatedLayout);
                            GridData groupAutomatedLData = new GridData();
                            groupAutomatedLData.horizontalAlignment = GridData.FILL;
                            groupAutomated.setLayoutData(groupAutomatedLData);
                            groupAutomated.setText("Automated execution");
                            {
                                buttonScheduling = new Button(
                                    groupAutomated,
                                    SWT.PUSH | SWT.CENTER);
                                buttonScheduling
                                    .setText(Messages
                                        .getString("ProfileDetails.Edit_Scheduling")); //$NON-NLS-1$
                                buttonScheduling
                                    .addSelectionListener(new SelectionAdapter() {
                                        public void widgetSelected(
                                            SelectionEvent evt) {
                                            ScheduleSelectionDialog dialog = new ScheduleSelectionDialog(
                                                getShell(),
                                                SWT.NULL);
                                            dialog
                                                .setSchedule((Schedule) buttonScheduling
                                                    .getData());
                                            dialog.open();

                                            buttonScheduling.setData(dialog
                                                .getSchedule());
                                        }
                                    });
                            }
                            {
                            	GridData buttonResetErrorLData = new GridData();
                            	buttonResetErrorLData.horizontalSpan = 2;
                                buttonResetError = new Button(
                                    groupAutomated,
                                    SWT.CHECK | SWT.RIGHT);
                                buttonResetError.setLayoutData(buttonResetErrorLData);
                                buttonResetError
                                    .setText(Messages
                                        .getString("ProfileDetails.Reset_ErrorFlag")); //$NON-NLS-1$
                            }
                            {
                            	GridData buttonEnabledLData = new GridData();
                            	buttonEnabledLData.horizontalSpan = 2;
                                buttonEnabled = new Button(
                                    groupAutomated,
                                    SWT.CHECK | SWT.RIGHT);
                                buttonEnabled.setLayoutData(buttonEnabledLData);
                                buttonEnabled.setText(Messages
                                    .getString("ProfileDetails.Enabled")); //$NON-NLS-1$
                            }
                        }
                    }
                    {
                        compositeTabLocations = new Composite(
                            compositeMain,
                            SWT.NONE);
                        treeItemLocations.setData(compositeTabLocations);
                        GridLayout compositeTabLocationsLayout = new GridLayout();
                        GridData compositeTabLocationsLData = new GridData();
                        compositeTabLocations.setLayoutData(compositeTabLocationsLData);
                        compositeTabLocations.setLayout(compositeTabLocationsLayout);
                        {
                            groupSource = new Group(
                                compositeTabLocations,
                                SWT.NONE);
                            GridLayout groupSourceLayout = new GridLayout();
                            groupSourceLayout.numColumns = 3;
                            groupSource.setLayout(groupSourceLayout);
                            GridData groupSourceLData = new GridData();
                            groupSourceLData.grabExcessHorizontalSpace = true;
                            groupSourceLData.horizontalAlignment = GridData.FILL;
                            groupSourceLData.grabExcessVerticalSpace = true;
                            groupSourceLData.verticalAlignment = GridData.FILL;
                            groupSource.setLayoutData(groupSourceLData);
                            {
                                labelSourceUrl = new Label(
                                    groupSource,
                                    SWT.NONE);
                                labelSourceUrl.setText("Url:");
                            }
                            groupSource.setText(Messages.getString("ProfileDetails.Source.Label")); //$NON-NLS-1$ 
                            {
                                GridData textSourceLData = new GridData();
                                textSourceLData.horizontalAlignment = GridData.FILL;
                                textSourceLData.grabExcessHorizontalSpace = true;
                                textSource = new Text(groupSource, SWT.BORDER);
                                textSource.setLayoutData(textSourceLData);
                                textSource
                                    .setToolTipText(Messages
                                        .getString("ProfileDetails.Source.ToolTip")); //$NON-NLS-1$
                            }
                            {
                                buttonBrowseSrc = new Button(
                                    groupSource,
                                    SWT.PUSH | SWT.CENTER);
                                buttonBrowseSrc.setText("..."); //$NON-NLS-1$
                                buttonBrowseSrc
                                    .addSelectionListener(new SelectionAdapter() {
                                        public void widgetSelected(
                                            SelectionEvent evt) {
                                            buttonBrowseSrcWidgetSelected(evt);
                                        }
                                    });
                            }
                            {
                                buttonSourceBuffered = new Button(
                                    groupSource,
                                    SWT.CHECK | SWT.LEFT);
                                buttonSourceBuffered
                                    .setText(Messages
                                        .getString("ProfileDetails.Buffered.Label")); //$NON-NLS-1$
                                        GridData buttonSourceBufferedLData = new GridData();
                                        buttonSourceBufferedLData.horizontalSpan = 3;
                                        buttonSourceBuffered.setLayoutData(buttonSourceBufferedLData);
                                buttonSourceBuffered.setEnabled(false);
                            }
                            {
                                label5 = new Label(groupSource, SWT.NONE);
                                label5
                                    .setText(Messages
                                        .getString("ProfileDetails.Username.Label") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                            {
                            	GridData textSourceUsernameLData = new GridData();
                            	textSourceUsernameLData.horizontalAlignment = GridData.FILL;
                            	textSourceUsernameLData.horizontalSpan = 2;
                                textSourceUsername = new Text(
                                    groupSource,
                                    SWT.BORDER);
                                textSourceUsername.setLayoutData(textSourceUsernameLData);
                            }
                            {
                                label6 = new Label(groupSource, SWT.NONE);
                                label6
                                    .setText(Messages
                                        .getString("ProfileDetails.Password.Label") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                            {
                            	GridData textSourcePasswordLData = new GridData();
                            	textSourcePasswordLData.horizontalAlignment = GridData.FILL;
                            	textSourcePasswordLData.horizontalSpan = 2;
                                textSourcePassword = new Text(
                                    groupSource,
                                    SWT.BORDER);
                                textSourcePassword.setLayoutData(textSourcePasswordLData);
                            }
                            {
                                label8 = new Label(groupSource, SWT.NONE);
                            }
                        }
                        {
                            groupDestination = new Group(
                                compositeTabLocations,
                                SWT.NONE);
                            GridLayout groupDestinationLayout = new GridLayout();
                            groupDestinationLayout.numColumns = 3;
                            groupDestination.setLayout(groupDestinationLayout);
                            GridData groupDestinationLData = new GridData();
                            groupDestinationLData.horizontalAlignment = GridData.FILL;
                            groupDestinationLData.grabExcessHorizontalSpace = true;
                            groupDestinationLData.grabExcessVerticalSpace = true;
                            groupDestinationLData.verticalAlignment = GridData.FILL;
                            groupDestination.setLayoutData(groupDestinationLData);
                            groupDestination.setText(Messages.getString("ProfileDetails.Destination.Label")); //$NON-NLS-1$
                            {
                                labelDestinationUrl = new Label(groupDestination, SWT.NONE);
                                labelDestinationUrl.setText("Url:");
                            }
                            {
                            	GridData textDestinationLData = new GridData();
                            	textDestinationLData.horizontalAlignment = GridData.FILL;
                            	textDestinationLData.grabExcessHorizontalSpace = true;
                                textDestination = new Text(
                                    groupDestination,
                                    SWT.BORDER);
                                textDestination.setLayoutData(textDestinationLData);
                                textDestination
                                    .setToolTipText(Messages
                                        .getString("ProfileDetails.Destination.ToolTip")); //$NON-NLS-1$
                            }
                            {
                                buttonBrowseDst = new Button(
                                    groupDestination,
                                    SWT.PUSH | SWT.CENTER);
                                buttonBrowseDst.setText("..."); //$NON-NLS-1$
                                buttonBrowseDst
                                    .addSelectionListener(new SelectionAdapter() {
                                        public void widgetSelected(
                                            SelectionEvent evt) {
                                            buttonBrowseDstWidgetSelected(evt);
                                        }
                                    });
                            }
                            {
                            	GridData buttonDestinationBufferedLData = new GridData();
                            	buttonDestinationBufferedLData.horizontalSpan = 3;
                                buttonDestinationBuffered = new Button(
                                    groupDestination,
                                    SWT.CHECK | SWT.LEFT);
                                buttonDestinationBuffered.setLayoutData(buttonDestinationBufferedLData);
                                buttonDestinationBuffered
                                    .setText(Messages
                                        .getString("ProfileDetails.Buffered.Label")); //$NON-NLS-1$
                                //buttonDestinationBuffered.setEnabled( false );
                            }
                            {
                                label10 = new Label(groupDestination, SWT.NONE);
                                label10
                                    .setText(Messages
                                        .getString("ProfileDetails.Username.Label") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                            {
                            	GridData textDestinationUsernameLData = new GridData();
                            	textDestinationUsernameLData.horizontalAlignment = GridData.FILL;
                            	textDestinationUsernameLData.horizontalSpan = 2;
                                textDestinationUsername = new Text(
                                    groupDestination,
                                    SWT.BORDER);
                                textDestinationUsername.setLayoutData(textDestinationUsernameLData);
                            }
                            {
                                label11 = new Label(groupDestination, SWT.NONE);
                                label11
                                    .setText(Messages
                                        .getString("ProfileDetails.Password.Label") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                            {
                            	GridData textDestinationPasswordLData = new GridData();
                            	textDestinationPasswordLData.horizontalAlignment = GridData.FILL;
                            	textDestinationPasswordLData.horizontalSpan = 2;
                                textDestinationPassword = new Text(
                                    groupDestination,
                                    SWT.BORDER);
                                textDestinationPassword.setLayoutData(textDestinationPasswordLData);
                            }
                            {
                                label12 = new Label(groupDestination, SWT.NONE);
                            }
                        }
                    }
                    {
                        compositeTabFilters = new Composite(
                            compositeMain,
                            SWT.NONE);
                        treeItemFilters.setData(compositeTabFilters);
                        GridLayout compositeTabFiltersLayout = new GridLayout();
                        compositeTabFiltersLayout.makeColumnsEqualWidth = true;
                        compositeTabFilters.setLayout(compositeTabFiltersLayout);
                        {
                            ruleSetGroup = new Group(
                                compositeTabFilters,
                                SWT.NONE);
                            GridLayout ruleSetGroupLayout = new GridLayout();
                            ruleSetGroupLayout.makeColumnsEqualWidth = true;
                            ruleSetGroupLayout.horizontalSpacing = 20;
                            GridData ruleSetGroupLData = new GridData();
                            ruleSetGroupLData.horizontalAlignment = GridData.FILL;
                            ruleSetGroupLData.grabExcessHorizontalSpace = true;
                            ruleSetGroup.setLayoutData(ruleSetGroupLData);
                            ruleSetGroup.setLayout(ruleSetGroupLayout);
                            ruleSetGroup.setText(Messages
                                .getString("ProfileDetails.RuleSet")); //$NON-NLS-1$
                            {
                                rbSimplyfiedRuleSet = new Button(
                                    ruleSetGroup,
                                    SWT.RADIO | SWT.LEFT);
                                rbSimplyfiedRuleSet
                                    .setText(Messages
                                        .getString("ProfileDetails.Simple_Rule_Set")); //$NON-NLS-1$
                                rbSimplyfiedRuleSet.setSelection(true);
                                GridData rbSimplyfiedRuleSetLData = new GridData();
                                rbSimplyfiedRuleSetLData.grabExcessHorizontalSpace = true;
                                rbSimplyfiedRuleSetLData.horizontalAlignment = GridData.FILL;
                                rbSimplyfiedRuleSet
                                    .setLayoutData(rbSimplyfiedRuleSetLData);
                                rbSimplyfiedRuleSet
                                    .addSelectionListener(new SelectionAdapter() {
                                        public void widgetSelected(
                                            SelectionEvent evt) {
                                            selectRuleSetButton(rbSimplyfiedRuleSet);
                                        }
                                    });
                            }
                            {
                                simplyfiedOptionsGroup = new Group(
                                    ruleSetGroup,
                                    SWT.NONE);
                                GridLayout simplyfiedOptionsGroupLayout = new GridLayout();
                                GridData simplyfiedOptionsGroupLData = new GridData();
                                simplyfiedOptionsGroupLData.verticalAlignment = GridData.BEGINNING;
                                simplyfiedOptionsGroupLData.grabExcessHorizontalSpace = true;
                                simplyfiedOptionsGroupLData.horizontalAlignment = GridData.FILL;
                                simplyfiedOptionsGroup
                                    .setLayoutData(simplyfiedOptionsGroupLData);
                                simplyfiedOptionsGroupLayout.numColumns = 3;
                                simplyfiedOptionsGroup
                                    .setLayout(simplyfiedOptionsGroupLayout);
                                simplyfiedOptionsGroup
                                    .setText(Messages
                                        .getString("ProfileDetails.Simple_Rule_Options")); //$NON-NLS-1$
                                {
                                    syncSubsButton = new Button(
                                        simplyfiedOptionsGroup,
                                        SWT.CHECK | SWT.LEFT);
                                    syncSubsButton
                                        .setText(Messages
                                            .getString("ProfileDetails.Sync_SubDirs")); //$NON-NLS-1$
                                    GridData syncSubsButtonLData = new GridData();
                                    syncSubsButton.setToolTipText(Messages
                                        .getString("ProfileDetails.Rucurre")); //$NON-NLS-1$
                                    syncSubsButtonLData.horizontalSpan = 3;
                                    syncSubsButton
                                        .setLayoutData(syncSubsButtonLData);
                                }
//                                {
//                                    label13 = new Label(
//                                        simplyfiedOptionsGroup,
//                                        SWT.NONE);
//                                    label13
//                                        .setText(Messages
//                                            .getString("ProfileDetails.Ingore_Pattern")); //$NON-NLS-1$
//                                }
//                                {
//                                    textIgnorePattern = new Text(
//                                        simplyfiedOptionsGroup,
//                                        SWT.BORDER);
//                                    GridData textIgnorePatterLData = new GridData();
//                                    textIgnorePattern
//                                        .setToolTipText(Messages
//                                            .getString("ProfileDetails.Ignore_ToolTip")); //$NON-NLS-1$
//                                    textIgnorePatterLData.heightHint = 13;
//                                    //textIgnorePatterLData.widthHint = 100;
//                                    textIgnorePatterLData.grabExcessHorizontalSpace = true;
//                                    textIgnorePatterLData.horizontalAlignment = GridData.FILL;
//                                    textIgnorePattern
//                                        .setLayoutData(textIgnorePatterLData);
//                                }
//                                {
//                                    comboPatternsType = new Combo(
//                                        simplyfiedOptionsGroup,
//                                        SWT.DROP_DOWN | SWT.READ_ONLY);
//                                    GridData comboPatternsTypeLData = new GridData();
//                                    comboPatternsTypeLData.verticalSpan = 2;
//                                    comboPatternsType
//                                        .setLayoutData(comboPatternsTypeLData);
//                                }
//                                {
//                                    label14 = new Label(
//                                        simplyfiedOptionsGroup,
//                                        SWT.NONE);
//                                    label14
//                                        .setText(Messages
//                                            .getString("ProfileDetails.Accept_Pattern")); //$NON-NLS-1$
//                                }
//                                {
//                                    textAcceptPattern = new Text(
//                                        simplyfiedOptionsGroup,
//                                        SWT.BORDER);
//                                    GridData textAcceptPatternLData = new GridData();
//                                    textAcceptPattern
//                                        .setToolTipText(Messages
//                                            .getString("ProfileDetails.Accept_ToolTip")); //$NON-NLS-1$
//                                    textAcceptPatternLData.heightHint = 13;
//                                    //textAcceptPatternLData.widthHint = 100;
//                                    textAcceptPatternLData.grabExcessHorizontalSpace = true;
//                                    textAcceptPatternLData.horizontalAlignment = GridData.FILL;
//                                    textAcceptPattern
//                                        .setLayoutData(textAcceptPatternLData);
//                                }
                                {
                                    buttonUseFileFilter = new Button(
                                        simplyfiedOptionsGroup,
                                        SWT.CHECK | SWT.LEFT);
                                    GridData buttonUseFileFilterLData = new GridData();
                                    buttonUseFileFilterLData.horizontalSpan = 3;
                                    buttonUseFileFilter
                                        .setLayoutData(buttonUseFileFilterLData);
                                    buttonUseFileFilter
                                        .setText("Use file filter");
                                    buttonUseFileFilter.setSelection(true);
                                    buttonUseFileFilter
                                        .addSelectionListener(new SelectionAdapter() {
                                            public void widgetSelected(
                                                SelectionEvent evt) {
                                                if (buttonUseFileFilter
                                                    .getSelection()) {
                                                    label18.setEnabled(true);
                                                    comboFilterType
                                                        .setEnabled(true);
                                                    buttonFileFilter
                                                        .setEnabled(true);
                                                    labelFilterDescription
                                                        .setEnabled(true);
                                                } else {
                                                    label18.setEnabled(false);
                                                    comboFilterType
                                                        .setEnabled(false);
                                                    buttonFileFilter
                                                        .setEnabled(false);
                                                    labelFilterDescription
                                                        .setEnabled(false);
                                                }
                                            }
                                        });
                                }
                                {
                                    label18 = new Label(
                                        simplyfiedOptionsGroup,
                                        SWT.NONE);
                                    label18.setText("Files Filter: ");
                                }
                                {
                                    GridData comboFilterTypeLData = new GridData();
                                    comboFilterTypeLData.widthHint = 60;
                                    comboFilterTypeLData.heightHint = 21;
                                    comboFilterType = new Combo(
                                        simplyfiedOptionsGroup,
                                        SWT.DROP_DOWN | SWT.READ_ONLY);
                                    comboFilterType
                                        .setLayoutData(comboFilterTypeLData);
                                    comboFilterType.add("Include");
                                    comboFilterType.add("Exclude");
                                    comboFilterType.select(0);
                                }
                                {
                                    buttonFileFilter = new Button(
                                        simplyfiedOptionsGroup,
                                        SWT.PUSH | SWT.CENTER);
                                    buttonFileFilter.setText("Set Filter...");
                                    buttonFileFilter
                                        .addSelectionListener(new SelectionAdapter() {
                                            public void widgetSelected(SelectionEvent evt) {
                                                try {
                                                    WizardDialog dialog = new WizardDialog(getShell(), SWT.APPLICATION_MODAL);
                                                    FileFilterPage page = new FileFilterPage(dialog, filter);
                                                    dialog.show();
                                                    FileFilter newfilter = page.getFileFilter();
                                                    if (newfilter != null) {
                                                        filter = newfilter;
                                                        labelFilterDescription.setText(filter.toString());
                                                    }
                                                } catch (Exception e) {
                                                    ExceptionHandler.reportException(e);
                                                }
                                            }
                                        });
                                }
                                {
                                    labelFilterDescription = new Label(simplyfiedOptionsGroup,
                                        SWT.SHADOW_NONE | SWT.WRAP | SWT.BORDER);
                                    GridData labelFilterDescriptionLData = new GridData();
                                    labelFilterDescriptionLData.horizontalSpan = 3;
                                    labelFilterDescriptionLData.horizontalAlignment = GridData.FILL;
                                    labelFilterDescriptionLData.heightHint = 48;
                                    labelFilterDescriptionLData.widthHint = 300;
                                    labelFilterDescriptionLData.grabExcessHorizontalSpace = true;
                                    labelFilterDescription.setLayoutData(labelFilterDescriptionLData);
                                    labelFilterDescription.setText("");
                                }
                            }
                            {
                                rbAdvancedRuleSet = new Button(
                                    ruleSetGroup,
                                    SWT.RADIO | SWT.LEFT);
                                rbAdvancedRuleSet
                                    .setText(Messages
                                        .getString("ProfileDetails.Advanced_Rule_Set")); //$NON-NLS-1$
                                GridData rbAdvancedRuleSetLData = new GridData();
                                rbAdvancedRuleSetLData.grabExcessHorizontalSpace = true;
                                rbAdvancedRuleSetLData.horizontalAlignment = GridData.FILL;
                                rbAdvancedRuleSet
                                    .setLayoutData(rbAdvancedRuleSetLData);
                                rbAdvancedRuleSet
                                    .addSelectionListener(new SelectionAdapter() {
                                        public void widgetSelected(
                                            SelectionEvent evt) {
                                            selectRuleSetButton(rbAdvancedRuleSet);
                                        }
                                    });
                            }
                            {
                                advancedRuleOptionsGroup = new Group(
                                    ruleSetGroup,
                                    SWT.NONE);
                                GridLayout advancedRuleOptionsGroupLayout = new GridLayout();
                                GridData advancedRuleOptionsGroupLData = new GridData();
                                advancedRuleOptionsGroup.setEnabled(false);
                                advancedRuleOptionsGroupLData.heightHint = 31;
                                advancedRuleOptionsGroupLData.verticalAlignment = GridData.BEGINNING;
                                advancedRuleOptionsGroupLData.grabExcessHorizontalSpace = true;
                                advancedRuleOptionsGroupLData.horizontalAlignment = GridData.FILL;
                                advancedRuleOptionsGroup
                                    .setLayoutData(advancedRuleOptionsGroupLData);
                                advancedRuleOptionsGroupLayout.numColumns = 2;
                                advancedRuleOptionsGroup
                                    .setLayout(advancedRuleOptionsGroupLayout);
                                advancedRuleOptionsGroup
                                    .setText(Messages
                                        .getString("ProfileDetails.Advanced_Rule_Options")); //$NON-NLS-1$
                                {
                                    label4 = new Label(
                                        advancedRuleOptionsGroup,
                                        SWT.NONE);
                                    GridData label4LData = new GridData();
                                    label4.setEnabled(false);
                                    label4.setLayoutData(label4LData);
                                    label4.setText(Messages
                                        .getString("ProfileDetails.RuleSet_2")); //$NON-NLS-1$
                                }
                                {
                                    textRuleSet = new Text(
                                        advancedRuleOptionsGroup,
                                        SWT.BORDER);
                                    GridData textRuleSetLData = new GridData();
                                    textRuleSet.setEnabled(false);
                                    textRuleSetLData.widthHint = 100;
                                    textRuleSetLData.heightHint = 13;
                                    textRuleSet.setLayoutData(textRuleSetLData);
                                }
                            }
                        }
                    }
                }
                sashForm.setWeights(new int[] {2,5});
            }
            comboType.add( "Publish/Update" ); //$NON-NLS-1$
            comboType.add( "Backup Copy" ); //$NON-NLS-1$
            comboType.add( "Exact Copy" ); //$NON-NLS-1$
            comboType.add( "Two Way Sync" ); //$NON-NLS-1$

//            comboPatternsType.add("RegExp");
//			comboPatternsType.add("Wildcard");

			this.layout();
			this.setSize(609, 533);
	
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
//	    comboPatternsType.select(0);
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
        filter = null;
        
        if (ruleSetDescriptor instanceof SimplyfiedRuleSetDescriptor) {
        	selectRuleSetButton(rbSimplyfiedRuleSet);
        	rbSimplyfiedRuleSet.setSelection(true);
        	rbAdvancedRuleSet.setSelection(false);
        	SimplyfiedRuleSetDescriptor simpleDesc = (SimplyfiedRuleSetDescriptor)ruleSetDescriptor;
        	syncSubsButton.setSelection(simpleDesc.isSyncSubDirs());
//        	textIgnorePattern.setText(simpleDesc.getIgnorePattern());
//        	textAcceptPattern.setText(simpleDesc.getTakePattern());
//        	comboPatternsType.setText(simpleDesc.getPatternsType());
        	FileFilter fileFilter = simpleDesc.getFileFilter();
        	filter = fileFilter;
        	if (fileFilter != null) {
        		labelFilterDescription.setText(fileFilter.toString());
        	}
        	else {
        		labelFilterDescription.setText("");
        	}
        	if (simpleDesc.isFilterSelectsFiles()) {
        		comboFilterType.select(0);
        	}
        	else {
        		comboFilterType.select(1);
        	}
        	boolean useFilter = simpleDesc.isUseFilter();
        	buttonUseFileFilter.setSelection(useFilter);
        	if (useFilter) {
				label18.setEnabled(true);
				comboFilterType.setEnabled(true);
				buttonFileFilter.setEnabled(true);
				labelFilterDescription.setEnabled(true);
        	}
        	else {
				label18.setEnabled(false);
				comboFilterType.setEnabled(false);
				buttonFileFilter.setEnabled(false);
				labelFilterDescription.setEnabled(false);
        	}
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
					"",
					"",
					"",
					filter,
					comboFilterType.getSelectionIndex() == 0,
					buttonUseFileFilter.getSelection());
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
//			label13.setEnabled(true);
//			label14.setEnabled(true);
//			textAcceptPattern.setEnabled(true);
//			textIgnorePattern.setEnabled(true);
//			comboPatternsType.setEnabled(true);
		}
		else {
			advancedRuleOptionsGroup.setEnabled(true);
			label4.setEnabled(true);
			textRuleSet.setEnabled(true);
			simplyfiedOptionsGroup.setEnabled(false);
			syncSubsButton.setEnabled(false);
//			label13.setEnabled(false);
//			label14.setEnabled(false);
//			textAcceptPattern.setEnabled(false);
//			textIgnorePattern.setEnabled(false);
//			comboPatternsType.setEnabled(false);
		}
		
	}
	
	private void treeTabsWidgetSelected(SelectionEvent evt) {
        ((StackLayout)compositeMain.getLayout()).topControl = 
            (Composite)treeTabs.getSelection()[0].getData();
        compositeMain.layout();
	}
}
