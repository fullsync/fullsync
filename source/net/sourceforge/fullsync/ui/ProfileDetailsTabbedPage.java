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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuleSetDescriptor;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.filefiltertree.FileFilterTree;
import net.sourceforge.fullsync.schedule.Schedule;

import org.apache.commons.vfs2.FileSystemException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Image;
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

public class ProfileDetailsTabbedPage extends WizardDialog {
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
	private ConnectionConfiguration dstConnectionConfiguration;
	private ConnectionConfiguration srcConnectionConfiguration;
	private Button buttonUseFileFilter;
	private Text textFilterDescription;
	private Button syncSubsButton;

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
	private String lastSourceLoaded = null;

	public ProfileDetailsTabbedPage(Shell parent, ProfileManager profileManager, String profileName) {
		super(parent);
		this.profileManager = profileManager;
		this.profileName = profileName;
	}

	@Override
	public String getTitle() {
		String title = Messages.getString("ProfileDetailsPage.Profile"); //$NON-NLS-1$
		if (this.profileName != null) {
			title = title + " " + profileName; //$NON-NLS-1$
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
	public Image getIcon() {
		return GuiController.getInstance().getImage("Profile_Default.png"); //$NON-NLS-1$
	}

	@Override
	public Image getImage() {
		return GuiController.getInstance().getImage("Profile_Wizard.png"); //$NON-NLS-1$
	}

	@Override
	public void createContent(Composite content) {
		m_parent = content;
		content.addDisposeListener(e -> closeSourceSite());
		try {
			tabs = new TabFolder(content, SWT.NULL);
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
			if (null != p.getSource()) {
				srcConnectionConfiguration.setBuffered("syncfiles".equals(p.getSource().getParameter("bufferStrategy"))); //$NON-NLS-1$
			}

			dstConnectionConfiguration.setConnectionDescription(p.getDestination());
			if (null != p.getDestination()) {
				dstConnectionConfiguration.setBuffered("syncfiles".equals(p.getDestination().getParameter("bufferStrategy"))); //$NON-NLS-1$
			}

			if ((p.getSynchronizationType() != null) && (p.getSynchronizationType().length() > 0)) {
				comboType.setText(p.getSynchronizationType());
			}

			buttonScheduling.setData(p.getSchedule());
			buttonEnabled.setSelection(p.isEnabled());

			RuleSetDescriptor ruleSetDescriptor = p.getRuleSet();
			filter = null;

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
		comboType.addModifyListener(evt -> {
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
		buttonScheduling.addListener(SWT.Selection, e -> {
			ScheduleSelectionDialog dialog = new ScheduleSelectionDialog(m_parent.getShell(), SWT.NULL);
			dialog.setSchedule((Schedule) buttonScheduling.getData());
			dialog.open();
			buttonScheduling.setData(dialog.getSchedule());
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
		buttonUseFileFilter.addListener(SWT.Selection, e -> enableFilterControls(buttonUseFileFilter.getSelection()));

		labelFilesFilter = new Label(c, SWT.NONE);
		labelFilesFilter.setText("Files Filter: ");
		buttonFileFilter = new Button(c, SWT.PUSH | SWT.CENTER);
		buttonFileFilter.setText("Set Filter...");
		GridData buttonFileFilterData = new GridData();
		buttonFileFilterData.grabExcessHorizontalSpace = true;
		buttonFileFilterData.widthHint = UISettings.BUTTON_WIDTH;
		buttonFileFilter.setLayoutData(buttonFileFilterData);
		buttonFileFilter.addListener(SWT.Selection, e -> {
			try {
				FileFilterPage dialog = new FileFilterPage(m_parent.getShell(), filter);
				dialog.show();
				FileFilter newfilter = dialog.getFileFilter();
				if (newfilter != null) {
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
		textFilterDescription.setText("");
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
		buttonSetFilter.setText("Set Filter...");
		buttonSetFilter.addListener(SWT.Selection, e -> {
			TreeItem[] selectedItems = directoryTree.getSelection();
			if (selectedItems.length > 0) {
				TreeItem selectedItem = selectedItems[0];
				FileFilter currentItemFilter = (FileFilter) selectedItem.getData(FILTER_KEY);
				FileFilterPage dialog = new FileFilterPage(m_parent.getShell(), currentItemFilter);
				dialog.show();
				FileFilter newfilter = dialog.getFileFilter();
				if (newfilter != null) {
					selectedItem.setData(FILTER_KEY, newfilter);
					treeItemsWithFilter.add(selectedItem);
					File file = (File) selectedItem.getData();
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
		buttonRemoveFilter.setText("Remove Filter");
		buttonRemoveFilter.setEnabled(false);
		buttonRemoveFilter.addListener(SWT.Selection, e -> {
			TreeItem[] selectedItems = directoryTree.getSelection();
			if (selectedItems.length > 0) {
				TreeItem selectedItem = selectedItems[0];
				treeItemsWithFilter.remove(selectedItem);
				File file = (File) selectedItem.getData();
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
			for (File file : getOrderedChildren(sourceSite.getRoot())) {
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
		for (File file : getOrderedChildren(rootFile)) {
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

	private List<File> getOrderedChildren(File rootFile) throws IOException {
		ArrayList<File> children = new ArrayList<File>(rootFile.getChildren());
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

		ConnectionDescription src, dst;
		try {
			src = getConnectionDescription(srcConnectionConfiguration);
			dst = getConnectionDescription(dstConnectionConfiguration);
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
			return false;
		}

		if ((profileName == null) || !textProfileName.getText().equals(profileName)) {
			Profile pr = profileManager.getProfile(textProfileName.getText());
			if (pr != null) {
				MessageBox mb = new MessageBox(m_parent.getShell(), SWT.ICON_ERROR);
				mb.setText(Messages.getString("ProfileDetails.Duplicate_Entry")); //$NON-NLS-1$
				mb.setMessage(Messages.getString("ProfileDetails.Profile_already_exists")); //$NON-NLS-1$
				mb.open();
				return false;
			}
		}
		Profile p;
		RuleSetDescriptor ruleSetDescriptor = null;
		ruleSetDescriptor = new SimplyfiedRuleSetDescriptor(syncSubsButton.getSelection(), filter, buttonUseFileFilter.getSelection(),
				getFileFilterTree());

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
		return true; //FIXME: return false if failed
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

	private void treeTabsWidgetSelected(SelectionEvent evt) {
		if (evt.item == tabSubDirs) {
			final ConnectionDescription src = getConnectionDescription(srcConnectionConfiguration);
			if ((sourceSite == null) || (src == null) || !src.getUri().toString().equals(lastSourceLoaded)) {
				directoryTree.removeAll();
				TreeItem loadingIem = new TreeItem(directoryTree, SWT.NULL);
				loadingIem.setText("Loading source dir...");
				loadingIem.setImage(GuiController.getInstance().getImage("Node_Directory.png"));

				Display display = Display.getCurrent();
				display.asyncExec(() -> {
					try {
						if (null != src) {
							closeSourceSite();
							src.setParameter("bufferStrategy", ""); // the subdirs tab should bypass the buffer imo
							src.setParameter(ConnectionDescription.PARAMETER_INTERACTIVE, "true");
							sourceSite = fsm.createConnection(src);
							drawDirectoryTree();
							lastSourceLoaded = src.getUri().toString();
						}
						else {
							TreeItem loadingIem1 = new TreeItem(directoryTree, SWT.NULL);
							loadingIem1.setText("Unable to load source dir");
							loadingIem1.setImage(GuiController.getInstance().getImage("Error.png"));
						}
					}
					catch (FileSystemException e1) {
						ExceptionHandler.reportException(e1);
						directoryTree.removeAll();
						TreeItem loadingIem2 = new TreeItem(directoryTree, SWT.NULL);
						loadingIem2.setText("Unable to load source dir");
						loadingIem2.setImage(GuiController.getInstance().getImage("Error.png"));
					}
					catch (IOException e2) {
						ExceptionHandler.reportException(e2);
						directoryTree.removeAll();
						TreeItem loadingIem3 = new TreeItem(directoryTree, SWT.NULL);
						loadingIem3.setText("Unable to load source dir");
						loadingIem3.setImage(GuiController.getInstance().getImage("Error.png"));
					}
					catch (URISyntaxException e3) {
						ExceptionHandler.reportException(e3);
						directoryTree.removeAll();
						TreeItem loadingIem4 = new TreeItem(directoryTree, SWT.NULL);
						loadingIem4.setText("Unable to load source dir");
						loadingIem4.setImage(GuiController.getInstance().getImage("Error.png"));
					}
					catch (net.sourceforge.fullsync.FileSystemException e4) {
						// FIXME Jan can you check this? I had to add it after an update. I have probably
						// made a mess with the merge.
						ExceptionHandler.reportException(e4);
						directoryTree.removeAll();
						TreeItem loadingIem5 = new TreeItem(directoryTree, SWT.NULL);
						loadingIem5.setText("Unable to load source dir");
						loadingIem5.setImage(GuiController.getInstance().getImage("Error.png"));
					}
					catch (Exception ex) {
						ex.printStackTrace();
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
	public boolean cancel() {
		return true;
	}
}
