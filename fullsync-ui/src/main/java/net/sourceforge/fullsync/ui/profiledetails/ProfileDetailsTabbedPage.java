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
package net.sourceforge.fullsync.ui.profiledetails;

import static org.eclipse.swt.events.SelectionListener.widgetDefaultSelectedAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileBuilder;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuleSetDescriptor;
import net.sourceforge.fullsync.fs.FSFile;
import net.sourceforge.fullsync.fs.connection.FileSystemConnection;
import net.sourceforge.fullsync.impl.SimplifiedRuleSetDescriptor;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.filefiltertree.FileFilterTree;
import net.sourceforge.fullsync.schedule.Schedule;
import net.sourceforge.fullsync.ui.FileFilterPage;
import net.sourceforge.fullsync.ui.Messages;
import net.sourceforge.fullsync.ui.UISettings;
import net.sourceforge.fullsync.ui.WizardDialog;

public class ProfileDetailsTabbedPage extends WizardDialog {
	private static final String EXPANDED_KEY = "Expanded"; //$NON-NLS-1$
	private static final String FILTER_KEY = "Filter"; //$NON-NLS-1$
	private final ProfileManager profileManager;
	private final Provider<FileFilterPage> fileFilterPageProvider;
	private final Provider<ConnectionConfiguration> connectionConfigurationProvider;
	private final Provider<ScheduleSelectionDialog> scheduleSelectionDialogProvider;
	private final Provider<FileSystemManager> fileSystemManagerProvider;
	private Text textProfileName;
	private Text textProfileDescription;
	private Label labelFilesFilter;
	private Button buttonFileFilter;
	private Button buttonResetError;
	private Button buttonEnabled;
	private Button buttonScheduling;
	private Label labelTypeDescription;
	private Combo comboType;
	private ConnectionConfiguration dstConnectionConfiguration;
	private ConnectionConfiguration srcConnectionConfiguration;
	private Button buttonUseFileFilter;
	private Text textFilterDescription;
	private Button syncSubsButton;
	private TabItem tabSubDirs;
	private Button buttonSetFilter;
	private Button buttonRemoveFilter;
	private Tree directoryTree;
	private final List<TreeItem> treeItemsWithFilter = new ArrayList<>();
	private Map<String, FileFilter> itemsMap = new HashMap<>();
	private FileSystemConnection sourceConnection;
	private Profile profile;
	private FileFilter filter;
	private Composite m_parent;
	private ConnectionDescription lastSourceLoaded;

	@Inject
	public ProfileDetailsTabbedPage(Shell shell, ProfileManager profileManager, Provider<FileFilterPage> fileFilterPageProvider,
		Provider<ConnectionConfiguration> connectionConfigurationProvider,
		Provider<ScheduleSelectionDialog> scheduleSelectionDialogProvider, Provider<FileSystemManager> fileSystemManagerProvider) {
		super(shell);
		this.profileManager = profileManager;
		this.fileFilterPageProvider = fileFilterPageProvider;
		this.connectionConfigurationProvider = connectionConfigurationProvider;
		this.scheduleSelectionDialogProvider = scheduleSelectionDialogProvider;
		this.fileSystemManagerProvider = fileSystemManagerProvider;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@Override
	public String getTitle() {
		String title = Messages.getString("ProfileDetailsPage.Profile"); //$NON-NLS-1$
		if (null != profile) {
			title = title + " " + profile.getName(); //$NON-NLS-1$
		}
		return title;
	}

	@Override
	public String getCaption() {
		return Messages.getString("ProfileDetailsPage.ProfileDetails"); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return ""; //$NON-NLS-1$
	}

	@Override
	public String getIconName() {
		return "Profile_Default.png"; //$NON-NLS-1$
	}

	@Override
	public String getImageName() {
		return "Profile_Wizard.png"; //$NON-NLS-1$
	}

	@Override
	public void createContent(Composite content) {
		m_parent = content;
		content.addDisposeListener(e -> closeSourceSite());
		try {
			TabFolder tabs = new TabFolder(content, SWT.NULL);
			GridData tabsData = new GridData(SWT.FILL, SWT.FILL, true, true);
			tabs.setLayoutData(tabsData);
			TabItem tabGeneral = new TabItem(tabs, SWT.NULL);
			tabGeneral.setText(Messages.getString("ProfileDetailsTabbedPage.General")); //$NON-NLS-1$
			tabGeneral.setControl(initGeneralTab(tabs));

			TabItem tabSource = new TabItem(tabs, SWT.NULL);
			tabSource.setText(Messages.getString("ProfileDetails.Source.Label")); //$NON-NLS-1$
			tabSource.setControl(initSourceTab(tabs));

			TabItem tabDestination = new TabItem(tabs, SWT.NULL);
			tabDestination.setText(Messages.getString("ProfileDetails.Destination.Label")); //$NON-NLS-1$
			tabDestination.setControl(initDestinationTab(tabs));

			TabItem tabFilters = new TabItem(tabs, SWT.NULL);
			tabFilters.setText(Messages.getString("ProfileDetailsTabbedPage.Filters")); //$NON-NLS-1$
			tabFilters.setControl(initFiltersTab(tabs));

			tabSubDirs = new TabItem(tabs, SWT.NULL);
			tabSubDirs.setText(Messages.getString("ProfileDetailsTabbedPage.Subdirectories")); //$NON-NLS-1$
			tabSubDirs.setControl(initSubDirsTab(tabs));

			tabs.addSelectionListener(widgetDefaultSelectedAdapter(this::treeTabsWidgetSelected));
			tabs.addSelectionListener(widgetSelectedAdapter(this::treeTabsWidgetSelected));
			comboType.select(0);

			if (null == this.profile) {
				return;
			}

			textProfileName.setText(profile.getName());
			textProfileDescription.setText(profile.getDescription());

			srcConnectionConfiguration.setConnectionDescription(profile.getSource());
			if (null != profile.getSource()) {
				String bufferStrategy = profile.getSource().getBufferStrategy().orElse(""); //$NON-NLS-1$
				srcConnectionConfiguration.setBuffered(FileSystemManager.BUFFER_STRATEGY_SYNCFILES.equals(bufferStrategy));
			}

			dstConnectionConfiguration.setConnectionDescription(profile.getDestination());
			if (null != profile.getDestination()) {
				String bufferStrategy = profile.getDestination().getBufferStrategy().orElse(""); //$NON-NLS-1$
				dstConnectionConfiguration.setBuffered(FileSystemManager.BUFFER_STRATEGY_SYNCFILES.equals(bufferStrategy));
			}

			if ((null != profile.getSynchronizationType()) && (profile.getSynchronizationType().length() > 0)) {
				comboType.setText(profile.getSynchronizationType());
			}

			buttonScheduling.setData(profile.getSchedule());
			buttonEnabled.setSelection(profile.isSchedulingEnabled());

			RuleSetDescriptor ruleSetDescriptor = profile.getRuleSet();

			SimplifiedRuleSetDescriptor simpleDesc = (SimplifiedRuleSetDescriptor) ruleSetDescriptor;
			syncSubsButton.setSelection(simpleDesc.isSyncSubDirs());
			filter = simpleDesc.getFileFilter();
			textFilterDescription.setText(null != filter ? filter.toString() : ""); //$NON-NLS-1$
			boolean useFilter = simpleDesc.isUseFilter();
			buttonUseFileFilter.setSelection(useFilter);
			enableFilterControls(useFilter);
			FileFilterTree fileFilterTree = simpleDesc.getFileFilterTree();
			if (null != fileFilterTree) {
				itemsMap = fileFilterTree.stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
			}
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
		nameLabel.setText(Messages.getString("ProfileDetails.Name.Label")); //$NON-NLS-1$
		GridData textNameData = new GridData();
		textNameData.grabExcessHorizontalSpace = true;
		textNameData.horizontalAlignment = SWT.FILL;
		textProfileName = new Text(c, SWT.BORDER);
		textProfileName.setLayoutData(textNameData);
		textProfileName.setToolTipText(Messages.getString("ProfileDetails.Name.ToolTip")); //$NON-NLS-1$
		// profile description
		Label descriptionLabel = new Label(c, SWT.NONE);
		descriptionLabel.setText(Messages.getString("ProfileDetails.Description.Label")); //$NON-NLS-1$
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
		comboType.addModifyListener(evt -> {
			srcConnectionConfiguration.setBuffered(false);
			dstConnectionConfiguration.setBuffered(false);
			if (comboType.getText().equals("Publish/Update")) { //$NON-NLS-1$
				labelTypeDescription.setText(Messages.getString("ProfileDetails.ProfileDescription.Publish")); //$NON-NLS-1$
				srcConnectionConfiguration.setBuffered(true);
			}
			else if (comboType.getText().equals("Backup Copy")) { //$NON-NLS-1$
				labelTypeDescription.setText(Messages.getString("ProfileDetails.ProfileDescription.BackupCopy")); //$NON-NLS-1$
			}
			else if (comboType.getText().equals("Exact Copy")) { //$NON-NLS-1$
				labelTypeDescription.setText(Messages.getString("ProfileDetails.ProfileDescription.ExactCopy")); //$NON-NLS-1$
			}
			else if (comboType.getText().equals("Two Way Sync")) { //$NON-NLS-1$
				labelTypeDescription.setText(Messages.getString("ProfileDetails.ProfileDescription.TwoWaySync")); //$NON-NLS-1$
			}
		});
		comboType.add("Publish/Update");
		comboType.add("Backup Copy");
		comboType.add("Exact Copy");
		comboType.add("Two Way Sync");

		new Label(c, SWT.NONE); // area below the type label should be empty
		GridData labelTypeDescriptionData = new GridData(SWT.FILL, SWT.TOP, true, false);
		labelTypeDescription = new Label(c, SWT.WRAP);
		labelTypeDescription.setLayoutData(labelTypeDescriptionData);
		labelTypeDescription.setText(Messages.getString("ProfileDetails.Description.Label")); //$NON-NLS-1$
		// automated execution
		Label labelAutomatedExecution = new Label(c, SWT.NONE);
		labelAutomatedExecution.setText(Messages.getString("ProfileDetailsTabbedPage.AutomatedExecution")); //$NON-NLS-1$
		buttonEnabled = new Button(c, SWT.CHECK | SWT.RIGHT);
		buttonEnabled.setText(Messages.getString("ProfileDetails.Enabled")); //$NON-NLS-1$
		new Label(c, SWT.NONE); // area below the automated execution label should be empty
		buttonScheduling = new Button(c, SWT.PUSH | SWT.CENTER);
		buttonScheduling.setText(Messages.getString("ProfileDetails.Edit_Scheduling")); //$NON-NLS-1$
		buttonScheduling.addListener(SWT.Selection, e -> {
			ScheduleSelectionDialog dialog = scheduleSelectionDialogProvider.get();
			dialog.setSchedule((Schedule) buttonScheduling.getData());
			dialog.open(m_parent.getShell());
			buttonScheduling.setData(dialog.getSchedule()); // FIXME: if cancelled??
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

		ConnectionDescription src = null != profile ? profile.getSource() : null;
		srcConnectionConfiguration = connectionConfigurationProvider.get();
		srcConnectionConfiguration.render(c, src);
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

		ConnectionDescription dst = null != profile ? profile.getDestination() : null;
		dstConnectionConfiguration = connectionConfigurationProvider.get();
		dstConnectionConfiguration.render(c, dst);
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
		buttonUseFileFilter.setText(Messages.getString("ProfileDetailsTabbedPage.EnableFileFilter")); //$NON-NLS-1$
		buttonUseFileFilter.setSelection(false);
		buttonUseFileFilter.addListener(SWT.Selection, e -> enableFilterControls(buttonUseFileFilter.getSelection()));

		labelFilesFilter = new Label(c, SWT.NONE);
		labelFilesFilter.setText(Messages.getString("ProfileDetailsTabbedPage.FileFilter")); //$NON-NLS-1$
		buttonFileFilter = new Button(c, SWT.PUSH | SWT.CENTER);
		buttonFileFilter.setText(Messages.getString("ProfileDetailsTabbedPage.SetFileFilter")); //$NON-NLS-1$
		GridData buttonFileFilterData = new GridData();
		buttonFileFilterData.grabExcessHorizontalSpace = true;
		buttonFileFilterData.widthHint = UISettings.BUTTON_WIDTH;
		buttonFileFilter.setLayoutData(buttonFileFilterData);
		buttonFileFilter.addListener(SWT.Selection, e -> {
			try {
				FileFilterPage dialog = fileFilterPageProvider.get();
				dialog.setFileFilter(filter);
				dialog.show();
				FileFilter newfilter = dialog.getFileFilter();
				if (null != newfilter) {
					filter = newfilter;
					textFilterDescription.setText(filter.toString());
				}
			}
			catch (Exception ex) {
				ExceptionHandler.reportException(ex);
			}
		});

		textFilterDescription = new Text(c, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData labelFilterDescriptionData = new GridData();
		labelFilterDescriptionData.horizontalSpan = 2;
		labelFilterDescriptionData.horizontalAlignment = SWT.FILL;
		labelFilterDescriptionData.heightHint = 120;
		labelFilterDescriptionData.grabExcessHorizontalSpace = true;
		textFilterDescription.setLayoutData(labelFilterDescriptionData);
		textFilterDescription.setText(""); //$NON-NLS-1$
		textFilterDescription.setEditable(false);

		enableFilterControls(false);
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
					if (null == children.getData(EXPANDED_KEY)) {
						FSFile file = (FSFile) children.getData();
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
		directoryTree.addListener(SWT.Selection, e -> {
			buttonSetFilter.setEnabled(true);
			TreeItem item = (TreeItem) e.item;
			FileFilter currentItemFilter = (FileFilter) item.getData(FILTER_KEY);
			buttonRemoveFilter.setEnabled(null != currentItemFilter);
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
		buttonSetFilter.setText(Messages.getString("ProfileDetailsTabbedPage.SetFileFilter")); //$NON-NLS-1$
		buttonSetFilter.addListener(SWT.Selection, e -> {
			TreeItem[] selectedItems = directoryTree.getSelection();
			if (selectedItems.length > 0) {
				TreeItem selectedItem = selectedItems[0];
				FileFilter currentItemFilter = (FileFilter) selectedItem.getData(FILTER_KEY);
				FileFilterPage dialog = fileFilterPageProvider.get();
				dialog.setFileFilter(currentItemFilter);
				dialog.show();
				FileFilter newfilter = dialog.getFileFilter();
				if (null != newfilter) {
					selectedItem.setData(FILTER_KEY, newfilter);
					treeItemsWithFilter.add(selectedItem);
					FSFile file = (FSFile) selectedItem.getData();
					itemsMap.put(file.getPath(), newfilter);
					markItem(selectedItem);
					buttonRemoveFilter.setEnabled(true);
				}
			}
		});

		// remove filter button
		buttonRemoveFilter = new Button(compositeButtons, SWT.PUSH | SWT.CENTER);
		GridData buttonRemoveFilterData = new GridData();
		buttonRemoveFilterData.widthHint = UISettings.BUTTON_WIDTH;
		buttonRemoveFilter.setLayoutData(buttonRemoveFilterData);
		buttonRemoveFilter.setText(Messages.getString("ProfileDetailsTabbedPage.RemoveFilter")); //$NON-NLS-1$
		buttonRemoveFilter.setEnabled(false);
		buttonRemoveFilter.addListener(SWT.Selection, e -> {
			TreeItem[] selectedItems = directoryTree.getSelection();
			if (selectedItems.length > 0) {
				TreeItem selectedItem = selectedItems[0];
				treeItemsWithFilter.remove(selectedItem);
				FSFile file = (FSFile) selectedItem.getData();
				itemsMap.remove(file.getPath());
				unmarkItem(selectedItem);
			}
		});
		return c;
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
			for (FSFile file : getOrderedChildren(sourceConnection.getRoot())) {
				if (file.isDirectory()) {
					TreeItem item = new TreeItem(directoryTree, SWT.NULL);
					item.setText(file.getName());
					item.setImage(imageRepository.getImage("Node_Directory.png")); //$NON-NLS-1$
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

	private void addChildren(FSFile rootFile, TreeItem item) throws IOException {
		for (FSFile file : getOrderedChildren(rootFile)) {
			if (file.isDirectory()) {
				TreeItem childrenItem = new TreeItem(item, SWT.NULL);
				childrenItem.setText(file.getName());
				childrenItem.setImage(imageRepository.getImage("Node_Directory.png")); //$NON-NLS-1$
				childrenItem.setData(file);
				if (itemsMap.containsKey(file.getPath())) {
					markItem(childrenItem);
					treeItemsWithFilter.add(childrenItem);
					childrenItem.setData(FILTER_KEY, itemsMap.get(file.getPath()));
				}
			}
		}
	}

	private List<FSFile> getOrderedChildren(FSFile rootFile) throws IOException {
		List<FSFile> children = new ArrayList<>(rootFile.getChildren());
		Collections.sort(children, (o1, o2) -> o1.getName().compareTo(o2.getName()));
		return children;
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

	@Override
	public boolean apply() {
		closeSourceSite();

		ConnectionDescription src = null;
		ConnectionDescription dst = null;
		try {
			src = getConnectionDescription(srcConnectionConfiguration).build();
			dst = getConnectionDescription(dstConnectionConfiguration).build();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
			return false;
		}
		boolean isNewProfile = null == profile;

		// TODO: no longer necessary, keep or remove?
		if (isNewProfile || !textProfileName.getText().equals(profile.getName())) {
			Profile pr = profileManager.getProfileByName(textProfileName.getText());
			if (null != pr) {
				MessageBox mb = new MessageBox(m_parent.getShell(), SWT.ICON_ERROR);
				mb.setText(Messages.getString("ProfileDetails.Duplicate_Entry")); //$NON-NLS-1$
				mb.setMessage(Messages.getString("ProfileDetails.Profile_already_exists")); //$NON-NLS-1$
				mb.open();
				return false;
			}
		}
		Profile oldProfile = profile;
		ProfileBuilder builder = isNewProfile ? profileManager.getProfileBuilder() : profileManager.getProfileBuilder(profile);

		RuleSetDescriptor ruleSetDescriptor = new SimplifiedRuleSetDescriptor(syncSubsButton.getSelection(), filter,
			buttonUseFileFilter.getSelection(), getFileFilterTree());

		builder.setName(textProfileName.getText())
			.setDescription(textProfileDescription.getText())
			.setSynchronizationType(comboType.getText())
			.setSchedulingEnabled(buttonEnabled.getSelection())
			.setSchedule((Schedule) buttonScheduling.getData())
			.setSource(src)
			.setDestination(dst)
			.setRuleSet(ruleSetDescriptor);
		if (buttonResetError.getSelection()) {
			builder.setLastError(0, null);
		}
		if (isNewProfile) {
			profileManager.addProfile(builder.build());
		}
		else {
			profileManager.updateProfile(oldProfile, builder.build());
		}
		profileManager.save();
		return true; // FIXME: return false if failed
	}

	private ConnectionDescription.Builder getConnectionDescription(final ConnectionConfiguration cfg) {
		ConnectionDescription.Builder builder = null;
		try {
			builder = cfg.getConnectionDescription();
			if (cfg.getBuffered()) {
				builder.setBufferStrategy(FileSystemManager.BUFFER_STRATEGY_SYNCFILES);
			}
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return builder;
	}

	private void treeTabsWidgetSelected(SelectionEvent evt) {
		if (evt.item == tabSubDirs) {
			final ConnectionDescription src = getConnectionDescription(srcConnectionConfiguration).build();
			if ((null == sourceConnection) || (null == src) || (null == lastSourceLoaded) || !lastSourceLoaded.equals(src)) {
				directoryTree.removeAll();
				TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
				loadingIem.setText(Messages.getString("ProfileDetailsTabbedPage.Loading")); //$NON-NLS-1$
				loadingIem.setImage(imageRepository.getImage("Node_Directory.png")); //$NON-NLS-1$

				Display display = Display.getCurrent();
				display.asyncExec(() -> {
					try {
						if (null != src) {
							closeSourceSite();
							ConnectionDescription.Builder builder = new ConnectionDescription.Builder(src);
							builder.setBufferStrategy(null);
							lastSourceLoaded = builder.build();
							sourceConnection = fileSystemManagerProvider.get().createConnection(lastSourceLoaded, true);
							drawDirectoryTree();
						}
						else {
							TreeItem loadingIem1 = new TreeItem(directoryTree, SWT.NULL);
							loadingIem1.setText(Messages.getString("ProfileDetailsTabbedPage.LoadingFailed")); //$NON-NLS-1$
							loadingIem1.setImage(imageRepository.getImage("Error.png")); //$NON-NLS-1$
						}
					}
					catch (IOException | FileSystemException ex) {
						ExceptionHandler.reportException(ex);
						directoryTree.removeAll();
						TreeItem loadingIem2 = new TreeItem(directoryTree, SWT.NULL);
						loadingIem2.setText(Messages.getString("ProfileDetailsTabbedPage.LoadingFailed")); //$NON-NLS-1$
						loadingIem2.setImage(imageRepository.getImage("Error.png")); //$NON-NLS-1$
					}
				});
			}
		}
	}

	private FileFilterTree getFileFilterTree() {
		Map<String, FileFilter> filters = new TreeMap<>();
		for (TreeItem item : treeItemsWithFilter) {
			FileFilter itemFilter = (FileFilter) item.getData(FILTER_KEY);
			FSFile itemFile = (FSFile) item.getData();
			filters.put(itemFile.getPath(), itemFilter);
		}
		return new FileFilterTree(filters);
	}

	private void closeSourceSite() {
		if (null != sourceConnection) {
			try {
				sourceConnection.close();
				sourceConnection = null;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean cancel() {
		return true;
	}
}
