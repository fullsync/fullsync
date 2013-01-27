/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */

package net.sourceforge.fullsync.ui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Vector;

import net.full.fs.ui.ConnectionConfiguration;
import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuleSetDescriptor;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.impl.AdvancedRuleSetDescriptor;
import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.filefiltertree.FileFilterTree;
import net.sourceforge.fullsync.schedule.Schedule;

import org.apache.commons.vfs2.FileSystemException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ProfileDetailsTabbed implements DisposeListener {

	private TabFolder tabs;
	private Text textProfileName;
	private Text textProfileDescription;

	private static final String EXPANDED_KEY = "Expanded";
	private static final String FILTER_KEY = "Filter";
	private ProfileManager profileManager;
	private Label labelFilesFilter;
	private Button buttonFileFilter;
	private Button buttonResetError;
	private Button buttonEnabled;
	private Button buttonScheduling;
	private Label labelTypeDescription;
	private Combo comboType;
	private Text textRuleSet;
	private Label labelRuleName;
	private ConnectionConfiguration dstConnectionConfiguration;
	private ConnectionConfiguration srcConnectionConfiguration;
	private Button buttonUseFileFilter;
	private Text textFilterDescription;
	private Button syncSubsButton;
	private Button rbAdvancedRuleSet;
	private Button rbSimplyfiedRuleSet;

	private TabItem tabSubDirs;
	private Button buttonSetFilter;
	private Button buttonRemoveFilter;

	private Tree directoryTree;
	private final Vector<TreeItem> treeItemsWithFilter = new Vector<TreeItem>();
	private HashMap<String, FileFilter> itemsMap = new HashMap<String, FileFilter>();
	private Site sourceSite;
	private final FileSystemManager fsm = new FileSystemManager();

	private String profileName;

	private FileFilter filter;

	private Composite m_parent;
	private int m_style;
	private String lastSourceLoaded = null;

	public ProfileDetailsTabbed(Composite parent, int style) {
		m_parent = parent;
		m_style = style;
		m_parent.addDisposeListener(this);
		try {
			tabs = new TabFolder(m_parent, m_style);
			GridData tabsData = new GridData(SWT.FILL, SWT.FILL, true, true);
			tabs.setLayoutData(tabsData);
			TabItem tabGeneral = new TabItem(tabs, SWT.NULL);
			tabGeneral.setText("General"); // FIXME: move text to translation file
			tabGeneral.setControl(initGeneralTab(tabs));

			TabItem tabSource = new TabItem(tabs, SWT.NULL);
			tabSource.setText(Messages.getString("ProfileDetails.Source.Label")); //$NON-NLS-1$
			tabSource.setControl(initSourceTab(tabs));

			TabItem tabDestination = new TabItem(tabs, SWT.NULL);
			tabDestination.setText(Messages.getString("ProfileDetails.Destination.Label")); //$NON-NLS-1$
			tabDestination.setControl(initDestinationTab(tabs));

			TabItem tabFilters = new TabItem(tabs, SWT.NULL);
			tabFilters.setText("Filters"); // FIXME: move text to translation file
			tabFilters.setControl(initFiltersTab(tabs));

			tabSubDirs = new TabItem(tabs, SWT.NULL);
			tabSubDirs.setText("Subdirectories"); // FIXME: move text to translation file
			tabSubDirs.setControl(initSubDirsTab(tabs));

			tabs.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					treeTabsWidgetSelected(e);
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent e) {
					treeTabsWidgetSelected(e);
				}
			});
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	/**
	 * initGenralTab creates all controls of the first tab.
	 *
	 * @param parent
	 *            parent element for the control
	 * @return composite to be placed inside the general tab
	 */
	private Composite initGeneralTab(final Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));

		// profile name
		Label nameLabel = new Label(c, SWT.NONE);
		nameLabel.setText(Messages.getString("ProfileDetails.Name.Label") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		GridData textNameData = new GridData();
		textNameData.grabExcessHorizontalSpace = true;
		textNameData.horizontalAlignment = SWT.FILL;
		textProfileName = new Text(c, SWT.BORDER);
		textProfileName.setLayoutData(textNameData);
		textProfileName.setToolTipText(Messages.getString("ProfileDetails.Name.ToolTip")); //$NON-NLS-1$
		// profile description
		Label descriptionLabel = new Label(c, SWT.NONE);
		descriptionLabel.setText(Messages.getString("ProfileDetails.Description.Label") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		GridData textDescriptionData = new GridData();
		textDescriptionData.horizontalAlignment = SWT.FILL;
		textProfileDescription = new Text(c, SWT.BORDER);
		textProfileDescription.setLayoutData(textDescriptionData);
		// sync type
		Label typeLabel = new Label(c, SWT.NONE);
		typeLabel.setText(Messages.getString("ProfileDetails.Type.Label")); //$NON-NLS-1$
		comboType = new Combo(c, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData comboTypeData = new GridData(SWT.FILL);
		comboTypeData.horizontalAlignment = SWT.FILL;
		comboType.setLayoutData(comboTypeData);
		comboType.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent evt) {
				srcConnectionConfiguration.setBuffered(false);
				dstConnectionConfiguration.setBuffered(false);
				if (comboType.getText().equals("Publish/Update")) {
					labelTypeDescription.setText(Messages.getString("ProfileDetails.ProfileDescription.Publish")); //$NON-NLS-1$
					srcConnectionConfiguration.setBuffered(true);
				}
				else if (comboType.getText().equals("Backup Copy")) {
					labelTypeDescription.setText(Messages.getString("ProfileDetails.ProfileDescription.BackupCopy")); //$NON-NLS-1$
				}
				else if (comboType.getText().equals("Exact Copy")) {
					labelTypeDescription.setText(Messages.getString("ProfileDetails.ProfileDescription.ExactCopy")); //$NON-NLS-1$
				}
				else if (comboType.getText().equals("Two Way Sync")) {
					labelTypeDescription.setText(Messages.getString("ProfileDetails.ProfileDescription.TwoWaySync")); //$NON-NLS-1$
				}
			}
		});
		comboType.add("Publish/Update");
		comboType.add("Backup Copy");
		comboType.add("Exact Copy");
		comboType.add("Two Way Sync");

		new Label(c, SWT.NONE); // area below the type label should be empty
		GridData labelTypeDescriptionData = new GridData();
		labelTypeDescriptionData.horizontalAlignment = SWT.FILL;
		labelTypeDescription = new Label(c, SWT.WRAP);
		labelTypeDescription.setLayoutData(labelTypeDescriptionData);
		labelTypeDescription.setText(Messages.getString("ProfileDetails.Description.Label")); //$NON-NLS-1$
		// automated execution
		Label labelAutomatedExecution = new Label(c, SWT.NONE);
		labelAutomatedExecution.setText("Automated Execution");
		buttonEnabled = new Button(c, SWT.CHECK | SWT.RIGHT);
		buttonEnabled.setText(Messages.getString("ProfileDetails.Enabled")); //$NON-NLS-1$
		new Label(c, SWT.NONE); // area below the automated execution label should be empty
		buttonScheduling = new Button(c, SWT.PUSH | SWT.CENTER);
		buttonScheduling.setText(Messages.getString("ProfileDetails.Edit_Scheduling")); //$NON-NLS-1$
		buttonScheduling.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				ScheduleSelectionDialog dialog = new ScheduleSelectionDialog(m_parent.getShell(), SWT.NULL);
				dialog.setSchedule((Schedule) buttonScheduling.getData());
				dialog.open();
				buttonScheduling.setData(dialog.getSchedule());
			}
		});
		new Label(c, SWT.NONE); // area below the automated execution label should be empty
		buttonResetError = new Button(c, SWT.CHECK | SWT.RIGHT); // TODO: make this a button?
		buttonResetError.setText(Messages.getString("ProfileDetails.Reset_ErrorFlag")); //$NON-NLS-1$

		return c;
	}

	/**
	 * initSourceTab creates all controls of the second tab.
	 *
	 * @param parent
	 *            parent element for the control
	 * @return composite to be placed inside the source tab
	 */
	private Composite initSourceTab(final Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		GridData cData = new GridData();
		cData.grabExcessHorizontalSpace = true;
		cData.grabExcessVerticalSpace = false;
		cData.horizontalAlignment = SWT.FILL;
		cData.verticalAlignment = SWT.FILL;
		c.setLayoutData(cData);

		// source
		srcConnectionConfiguration = new ConnectionConfiguration(c);
		srcConnectionConfiguration.setBufferedEnabled(false);

		return c;
	}

	/**
	 * initDestinationTab creates all controls of the tab.
	 *
	 * @param parent
	 *            parent element for the control
	 * @return composite to be placed inside the destination tab
	 */
	private Composite initDestinationTab(final Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		GridData cData = new GridData();
		cData.grabExcessHorizontalSpace = true;
		cData.grabExcessVerticalSpace = false;
		cData.horizontalAlignment = SWT.FILL;
		cData.verticalAlignment = SWT.FILL;
		c.setLayoutData(cData);

		// destination
		dstConnectionConfiguration = new ConnectionConfiguration(c);

		return c;
	}

	/**
	 * initFiltersTab creates all controls of the third tab.
	 *
	 * @param parent
	 *            parent element for the control
	 * @return composite to be placed inside the filters tab
	 */
	private Composite initFiltersTab(final Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));

		// simple rule-set
		rbSimplyfiedRuleSet = new Button(c, SWT.RADIO | SWT.LEFT);
		rbSimplyfiedRuleSet.setText(Messages.getString("ProfileDetails.Simple_Rule_Set")); //$NON-NLS-1$
		rbSimplyfiedRuleSet.setSelection(true);
		GridData rbSimplyfiedRuleSetData = new GridData();
		rbSimplyfiedRuleSetData.grabExcessHorizontalSpace = true;
		rbSimplyfiedRuleSetData.horizontalSpan = 2;
		rbSimplyfiedRuleSetData.horizontalAlignment = SWT.FILL;
		rbSimplyfiedRuleSet.setLayoutData(rbSimplyfiedRuleSetData);
		rbSimplyfiedRuleSet.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				selectRuleSetButton(rbSimplyfiedRuleSet);
			}
		});
		// sync subdirectories
		syncSubsButton = new Button(c, SWT.CHECK | SWT.LEFT);
		syncSubsButton.setText(Messages.getString("ProfileDetails.Sync_SubDirs")); //$NON-NLS-1$
		syncSubsButton.setToolTipText(Messages.getString("ProfileDetails.Rucurre")); //$NON-NLS-1$
		syncSubsButton.setSelection(true);
		GridData syncSubsButtonData = new GridData();
		syncSubsButtonData.horizontalSpan = 2;
		syncSubsButton.setLayoutData(syncSubsButtonData);
		// use file filter
		buttonUseFileFilter = new Button(c, SWT.CHECK | SWT.LEFT);
		GridData buttonUseFileFilterData = new GridData();
		buttonUseFileFilterData.horizontalSpan = 2;
		buttonUseFileFilter.setLayoutData(buttonUseFileFilterData);
		buttonUseFileFilter.setText("Use file filter");
		buttonUseFileFilter.setSelection(false);
		buttonUseFileFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				enableFilterControls(buttonUseFileFilter.getSelection());
			}
		});

		labelFilesFilter = new Label(c, SWT.NONE);
		labelFilesFilter.setText("Files Filter: ");
		buttonFileFilter = new Button(c, SWT.PUSH | SWT.CENTER);
		buttonFileFilter.setText("Set Filter...");
		GridData buttonFileFilterData = new GridData();
		buttonFileFilterData.grabExcessHorizontalSpace = true;
		buttonFileFilterData.widthHint = UISettings.BUTTON_WIDTH;
		buttonFileFilter.setLayoutData(buttonFileFilterData);
		buttonFileFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				try {
					WizardDialog dialog = new WizardDialog(m_parent.getShell(), SWT.APPLICATION_MODAL | SWT.RESIZE);
					FileFilterPage page = new FileFilterPage(dialog, filter);
					dialog.show();
					FileFilter newfilter = page.getFileFilter();
					if (newfilter != null) {
						filter = newfilter;
						textFilterDescription.setText(filter.toString());
					}
				}
				catch (Exception e) {
					ExceptionHandler.reportException(e);
				}
			}
		});

		textFilterDescription = new Text(c, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData labelFilterDescriptionData = new GridData();
		labelFilterDescriptionData.horizontalSpan = 2;
		labelFilterDescriptionData.horizontalAlignment = SWT.FILL;
		labelFilterDescriptionData.heightHint = 120;
		labelFilterDescriptionData.grabExcessHorizontalSpace = true;
		textFilterDescription.setLayoutData(labelFilterDescriptionData);
		textFilterDescription.setText("");
		textFilterDescription.setEditable(false);

		rbAdvancedRuleSet = new Button(c, SWT.RADIO | SWT.LEFT);
		rbAdvancedRuleSet.setText(Messages.getString("ProfileDetails.Advanced_Rule_Set")); //$NON-NLS-1$
		GridData rbAdvancedRuleSetData = new GridData();
		rbAdvancedRuleSetData.grabExcessHorizontalSpace = true;
		rbAdvancedRuleSetData.horizontalSpan = 2;
		rbAdvancedRuleSetData.horizontalAlignment = SWT.FILL;
		rbAdvancedRuleSet.setLayoutData(rbAdvancedRuleSetData);
		rbAdvancedRuleSet.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				selectRuleSetButton(rbAdvancedRuleSet);
			}
		});

		labelRuleName = new Label(c, SWT.NONE);
		GridData label4LData = new GridData();
		labelRuleName.setEnabled(false);
		labelRuleName.setLayoutData(label4LData);
		labelRuleName.setText(Messages.getString("ProfileDetails.RuleSet_2")); //$NON-NLS-1$
		textRuleSet = new Text(c, SWT.BORDER);
		GridData textRuleSetData = new GridData(SWT.FILL);
		textRuleSet.setEnabled(false);
		textRuleSetData.heightHint = 13;
		textRuleSetData.grabExcessHorizontalSpace = true;
		textRuleSetData.horizontalAlignment = SWT.FILL;
		textRuleSet.setLayoutData(textRuleSetData);

		enableFilterControls(false);

		rbAdvancedRuleSet.setVisible(false); //FIXME: [RULESETS] remove to restore advanced rule set support
		labelRuleName.setVisible(false); //FIXME: [RULESETS] remove to restore advanced rule set support
		textRuleSet.setVisible(false); //FIXME: [RULESETS] remove to restore advanced rule set support
		return c;
	}

	/**
	 * initSubDirsTab creates all controls of the fourth tab.
	 *
	 * @param parent
	 *            parent element for the control
	 * @return composite to be placed inside the subdirectories tab
	 */
	private Composite initSubDirsTab(final Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));

		// tree
		directoryTree = new Tree(c, SWT.BORDER | SWT.SINGLE);
		GridData directoryTreeData = new GridData();
		directoryTreeData.grabExcessHorizontalSpace = true;
		directoryTreeData.grabExcessVerticalSpace = true;
		directoryTreeData.horizontalAlignment = SWT.FILL;
		directoryTreeData.verticalAlignment = SWT.FILL;
		directoryTree.setLayoutData(directoryTreeData);
		directoryTree.addTreeListener(new TreeAdapter() {
			@Override
			public void treeExpanded(final TreeEvent evt) {
				TreeItem item = (TreeItem) evt.item;
				TreeItem[] childrens = item.getItems();
				for (TreeItem children : childrens) {
					if (children.getData(EXPANDED_KEY) == null) {
						File file = (File) children.getData();
						try {
							addChildren(file, children);
						}
						catch (IOException e) {
							ExceptionHandler.reportException(e);
						}
						children.setData(EXPANDED_KEY, new Object());
					}
				}
			}
		});
		directoryTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				buttonSetFilter.setEnabled(true);
				TreeItem item = (TreeItem) evt.item;
				FileFilter currentItemFilter = (FileFilter) item.getData(FILTER_KEY);
				if (currentItemFilter != null) {
					buttonRemoveFilter.setEnabled(true);
				}
				else {
					buttonRemoveFilter.setEnabled(false);
				}
			}
		});
		// buttons next to the tree
		Composite compositeButtons = new Composite(c, SWT.NONE);
		GridLayout compositeButtonsLayout = new GridLayout();
		compositeButtonsLayout.makeColumnsEqualWidth = true;
		GridData compositeButtonsData = new GridData();
		compositeButtonsData.grabExcessVerticalSpace = true;
		compositeButtonsData.verticalAlignment = SWT.FILL;
		compositeButtons.setLayoutData(compositeButtonsData);
		compositeButtons.setLayout(compositeButtonsLayout);

		// add filter button
		buttonSetFilter = new Button(compositeButtons, SWT.PUSH | SWT.CENTER);
		GridData buttonSetFilterData = new GridData();
		buttonSetFilterData.widthHint = UISettings.BUTTON_WIDTH;
		buttonSetFilter.setLayoutData(buttonSetFilterData);
		buttonSetFilter.setText("Set Filter...");
		buttonSetFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				TreeItem[] selectedItems = directoryTree.getSelection();
				if (selectedItems.length > 0) {
					TreeItem selectedItem = selectedItems[0];
					FileFilter currentItemFilter = (FileFilter) selectedItem.getData(FILTER_KEY);
					WizardDialog dialog = new WizardDialog(m_parent.getShell(), SWT.APPLICATION_MODAL | SWT.RESIZE);
					FileFilterPage page = new FileFilterPage(dialog, currentItemFilter);
					dialog.show();
					FileFilter newfilter = page.getFileFilter();
					if (newfilter != null) {
						selectedItem.setData(FILTER_KEY, newfilter);
						treeItemsWithFilter.add(selectedItem);
						File file = (File) selectedItem.getData();
						itemsMap.put(file.getPath(), newfilter);
						markItem(selectedItem);
						buttonRemoveFilter.setEnabled(true);
					}
				}
			}
		});

		// remove filter button
		buttonRemoveFilter = new Button(compositeButtons, SWT.PUSH | SWT.CENTER);
		GridData buttonRemoveFilterData = new GridData();
		buttonRemoveFilterData.widthHint = UISettings.BUTTON_WIDTH;
		buttonRemoveFilter.setLayoutData(buttonRemoveFilterData);
		buttonRemoveFilter.setText("Remove Filter");
		buttonRemoveFilter.setEnabled(false);
		buttonRemoveFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				TreeItem[] selectedItems = directoryTree.getSelection();
				if (selectedItems.length > 0) {
					TreeItem selectedItem = selectedItems[0];
					treeItemsWithFilter.remove(selectedItem);
					File file = (File) selectedItem.getData();
					itemsMap.remove(file.getPath());
					unmarkItem(selectedItem);
				}
			}
		});

		return c;
	}

	public void setProfileManager(final ProfileManager manager) {
		this.profileManager = manager;
	}

	public void setProfileName(final String name) {
		this.profileName = name;

		comboType.select(0);

		if (this.profileName == null) {
			return;
		}

		Profile p = profileManager.getProfile(profileName);
		if (p == null) {
			throw new IllegalArgumentException(Messages.getString("ProfileDetails.profile_does_not_exist")); //$NON-NLS-1$
		}

		textProfileName.setText(p.getName());
		textProfileDescription.setText(p.getDescription());

		srcConnectionConfiguration.setConnectionDescription(p.getSource());
		if (null != srcConnectionConfiguration) {
			srcConnectionConfiguration.setBuffered("syncfiles".equals(p.getSource().getParameter("bufferStrategy"))); //$NON-NLS-1$
		}

		dstConnectionConfiguration.setConnectionDescription(p.getDestination());
		if (null != dstConnectionConfiguration) {
			dstConnectionConfiguration.setBuffered("syncfiles".equals(p.getDestination().getParameter("bufferStrategy"))); //$NON-NLS-1$
		}

		if ((p.getSynchronizationType() != null) && (p.getSynchronizationType().length() > 0)) {
			comboType.setText(p.getSynchronizationType());
		}

		buttonScheduling.setData(p.getSchedule());
		buttonEnabled.setSelection(p.isEnabled());

		RuleSetDescriptor ruleSetDescriptor = p.getRuleSet();
		filter = null;

		if (ruleSetDescriptor instanceof SimplyfiedRuleSetDescriptor) {
			selectRuleSetButton(rbSimplyfiedRuleSet);
			rbSimplyfiedRuleSet.setSelection(true);
			rbAdvancedRuleSet.setSelection(false);
			SimplyfiedRuleSetDescriptor simpleDesc = (SimplyfiedRuleSetDescriptor) ruleSetDescriptor;
			syncSubsButton.setSelection(simpleDesc.isSyncSubDirs());
			FileFilter fileFilter = simpleDesc.getFileFilter();
			filter = fileFilter;
			if (fileFilter != null) {
				textFilterDescription.setText(fileFilter.toString());
			}
			else {
				textFilterDescription.setText("");
			}
			boolean useFilter = simpleDesc.isUseFilter();
			buttonUseFileFilter.setSelection(useFilter);
			enableFilterControls(useFilter);
			FileFilterTree fileFilterTree = simpleDesc.getFileFilterTree();
			if (fileFilterTree != null) {
				itemsMap = fileFilterTree.getItemsMap();
			}
		}
		else {
			selectRuleSetButton(rbAdvancedRuleSet);
			rbSimplyfiedRuleSet.setSelection(false);
			rbAdvancedRuleSet.setSelection(true);
			AdvancedRuleSetDescriptor advDesc = (AdvancedRuleSetDescriptor) ruleSetDescriptor;
			textRuleSet.setText(advDesc.getRuleSetName());
		}
	}

	private void enableFilterControls(final boolean enable) {
		labelFilesFilter.setEnabled(enable);
		buttonFileFilter.setEnabled(enable);
		textFilterDescription.setEnabled(enable);
	}

	private void drawDirectoryTree() {
		directoryTree.setRedraw(false);
		directoryTree.removeAll();
		try {
			File rootFile = sourceSite.getRoot();
			for (File file : rootFile.getChildren()) {
				if (file.isDirectory()) {
					TreeItem item = new TreeItem(directoryTree, SWT.NULL);
					item.setText(file.getName());
					item.setImage(GuiController.getInstance().getImage("Node_Directory.png"));
					item.setData(file);
					if (itemsMap.containsKey(file.getPath())) {
						markItem(item);
						treeItemsWithFilter.add(item);
						item.setData(FILTER_KEY, itemsMap.get(file.getPath()));
					}
					addChildren(file, item);
					item.setData(EXPANDED_KEY, new Object());
				}
			}
		}
		catch (IOException e) {
			ExceptionHandler.reportException(e);
		}
		directoryTree.setRedraw(true);
	}

	private void addChildren(File rootFile, TreeItem item) throws IOException {
		for (File file : rootFile.getChildren()) {
			if (file.isDirectory()) {
				TreeItem childrenItem = new TreeItem(item, SWT.NULL);
				childrenItem.setText(file.getName());
				childrenItem.setImage(GuiController.getInstance().getImage("Node_Directory.png"));
				childrenItem.setData(file);
				if (itemsMap.containsKey(file.getPath())) {
					markItem(childrenItem);
					treeItemsWithFilter.add(childrenItem);
					childrenItem.setData(FILTER_KEY, itemsMap.get(file.getPath()));
				}
			}
		}
	}

	private void markItem(TreeItem item) {
		String text = item.getText();
		if (text.charAt(0) != '*') {
			item.setText('*' + text);
		}
	}

	private void unmarkItem(TreeItem item) {
		String text = item.getText();
		if (text.charAt(0) == '*') {
			item.setText(text.substring(1));
		}
	}

	public void apply() {
		closeSourceSite();

		ConnectionDescription src, dst;
		try {
			src = getConnectionDescription(srcConnectionConfiguration);
			dst = getConnectionDescription(dstConnectionConfiguration);
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
			return;
		}

		if ((profileName == null) || !textProfileName.getText().equals(profileName)) {
			Profile pr = profileManager.getProfile(textProfileName.getText());
			if (pr != null) {
				MessageBox mb = new MessageBox(m_parent.getShell(), SWT.ICON_ERROR);
				mb.setText(Messages.getString("ProfileDetails.Duplicate_Entry")); //$NON-NLS-1$
				mb.setMessage(Messages.getString("ProfileDetails.Profile_already_exists")); //$NON-NLS-1$
				mb.open();
				return;
			}
		}
		Profile p;
		RuleSetDescriptor ruleSetDescriptor = null;
		if (rbSimplyfiedRuleSet.getSelection()) {
			ruleSetDescriptor = new SimplyfiedRuleSetDescriptor(syncSubsButton.getSelection(), filter, buttonUseFileFilter.getSelection(),
					getFileFilterTree());
		}
		if (rbAdvancedRuleSet.getSelection()) {
			String ruleSetName = textRuleSet.getText();
			ruleSetDescriptor = new AdvancedRuleSetDescriptor(ruleSetName);
		}

		if (profileName == null) {
			p = new Profile(textProfileName.getText(), src, dst, ruleSetDescriptor);
			p.setSynchronizationType(comboType.getText());
			p.setDescription(textProfileDescription.getText());
			p.setSchedule((Schedule) buttonScheduling.getData());
			p.setEnabled(buttonEnabled.getSelection());
			if (buttonResetError.getSelection()) {
				p.setLastError(0, null);
			}
			profileManager.addProfile(p);
		}
		else {
			p = profileManager.getProfile(profileName);
			p.beginUpdate();
			p.setName(textProfileName.getText());
			p.setDescription(textProfileDescription.getText());
			p.setSynchronizationType(comboType.getText());
			p.setSource(src);
			p.setDestination(dst);
			p.setSchedule((Schedule) buttonScheduling.getData());
			p.setEnabled(buttonEnabled.getSelection());

			p.setRuleSet(ruleSetDescriptor);
			if (buttonResetError.getSelection()) {
				p.setLastError(0, null);
			}
			p.endUpdate();
		}
		profileManager.save();
	}

	private ConnectionDescription getConnectionDescription(final ConnectionConfiguration cfg) {
		ConnectionDescription dst = null;
		try {
			dst = cfg.getConnectionDescription();
			if (cfg.getBuffered()) {
				dst.setParameter("bufferStrategy", "syncfiles"); //$NON-NLS-1$
			}
		}
		catch (URISyntaxException e) {
		}
		return dst;
	}

	protected void buttonCancelWidgetSelected(SelectionEvent evt) {
		closeSourceSite();
		m_parent.getShell().dispose();
	}

	protected void selectRuleSetButton(Button button) {
		if (button.equals(rbSimplyfiedRuleSet)) {
			labelRuleName.setEnabled(false);
			textRuleSet.setEnabled(false);
			syncSubsButton.setEnabled(true);
			buttonUseFileFilter.setEnabled(true);
			if (buttonUseFileFilter.getSelection()) {
				enableFilterControls(true);
			}
		}
		else {
			labelRuleName.setEnabled(true);
			textRuleSet.setEnabled(true);
			syncSubsButton.setEnabled(false);
			buttonUseFileFilter.setEnabled(false);
			enableFilterControls(false);
		}

	}

	private void treeTabsWidgetSelected(SelectionEvent evt) {
		if (evt.item == tabSubDirs) {
			final ConnectionDescription src = getConnectionDescription(srcConnectionConfiguration);
			if ((sourceSite == null) || (src == null) || !src.getUri().toString().equals(lastSourceLoaded)) {
				directoryTree.removeAll();
				TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
				loadingIem.setText("Loading source dir...");
				loadingIem.setImage(GuiController.getInstance().getImage("Node_Directory.png"));

				Display display = Display.getCurrent();
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							if (null != src) {
								closeSourceSite();
								src.setParameter("bufferStrategy", ""); // the subdirs tab should bypass the buffer imo
								src.setParameter("interactive", "true");
								sourceSite = fsm.createConnection(src);
								drawDirectoryTree();
								lastSourceLoaded = src.getUri().toString();
							}
							else {
								TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
								loadingIem.setText("Unable to load source dir");
								loadingIem.setImage(GuiController.getInstance().getImage("Error.png"));
							}
						}
						catch (FileSystemException e) {
							ExceptionHandler.reportException(e);
							directoryTree.removeAll();
							TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
							loadingIem.setText("Unable to load source dir");
							loadingIem.setImage(GuiController.getInstance().getImage("Error.png"));
						}
						catch (IOException e) {
							ExceptionHandler.reportException(e);
							directoryTree.removeAll();
							TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
							loadingIem.setText("Unable to load source dir");
							loadingIem.setImage(GuiController.getInstance().getImage("Error.png"));
						}
						catch (URISyntaxException e) {
							ExceptionHandler.reportException(e);
							directoryTree.removeAll();
							TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
							loadingIem.setText("Unable to load source dir");
							loadingIem.setImage(GuiController.getInstance().getImage("Error.png"));
						}
						catch (net.sourceforge.fullsync.FileSystemException e) {
							// FIXME Jan can you check this? I had to add it after an update. I have probably
							// made a mess with the merge.
							ExceptionHandler.reportException(e);
							directoryTree.removeAll();
							TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
							loadingIem.setText("Unable to load source dir");
							loadingIem.setImage(GuiController.getInstance().getImage("Error.png"));
						}
						catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				});
			}
		}
	}

	private FileFilterTree getFileFilterTree() {
		FileFilterTree fileFilterTree = new FileFilterTree();
		for (TreeItem item : treeItemsWithFilter) {
			FileFilter itemFilter = (FileFilter) item.getData(FILTER_KEY);
			File itemFile = (File) item.getData();
			fileFilterTree.addFileFilter(itemFile.getPath(), itemFilter);
		}
		return fileFilterTree;
	}

	private void closeSourceSite() {
		if (sourceSite != null) {
			try {
				sourceSite.close();
				sourceSite = null;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public final void widgetDisposed(final DisposeEvent e) {
		closeSourceSite();
	}
}
