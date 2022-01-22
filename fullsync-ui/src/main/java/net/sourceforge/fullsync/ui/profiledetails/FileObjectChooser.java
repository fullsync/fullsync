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

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.ui.ImageRepository;
import net.sourceforge.fullsync.ui.Messages;
import net.sourceforge.fullsync.ui.ShellStateHandler;
import net.sourceforge.fullsync.ui.UISettings;

class FileObjectChooser {
	private final Preferences preferences;
	private final ImageRepository imageRepository;
	private Shell dialogShell;
	private Label labelBaseUrl;
	private Text textUrlExtension;
	private Text textFilename;
	private Table tableItems;
	private boolean result;
	private FileObject rootFileObject;
	private FileObject activeFileObject;
	private FileObject selectedFileObject;

	@Inject
	public FileObjectChooser(Preferences preferences, ImageRepository imageRepository) {
		this.preferences = preferences;
		this.imageRepository = imageRepository;
	}

	public boolean open(Shell parent, FileObject base) {
		dialogShell = new Shell(parent, SWT.BORDER | SWT.TITLE | SWT.RESIZE | SWT.PRIMARY_MODAL);
		dialogShell.setText(Messages.getString("FileObjectChooser.Title")); //$NON-NLS-1$

		dialogShell.setLayout(new GridLayout());
		dialogShell.layout();
		dialogShell.pack();
		dialogShell.setSize(550, 400);

		// top area
		var compositeTop = new Composite(dialogShell, SWT.NONE);
		var composite1Layout = new GridLayout(4, false);
		var compositeTopLData = new GridData();
		compositeTopLData.horizontalAlignment = SWT.FILL;
		compositeTop.setLayoutData(compositeTopLData);
		compositeTop.setLayout(composite1Layout);

		var labelUrl = new Label(compositeTop, SWT.NONE);
		labelUrl.setText(Messages.getString("FileObjectChooser.URL")); //$NON-NLS-1$

		labelBaseUrl = new Label(compositeTop, SWT.NONE);

		textUrlExtension = new Text(compositeTop, SWT.BORDER);
		var textUrlExtensionLData = new GridData();
		textUrlExtensionLData.grabExcessHorizontalSpace = true;
		textUrlExtensionLData.horizontalAlignment = SWT.FILL;
		textUrlExtension.setLayoutData(textUrlExtensionLData);

		// toolbar: folder up, create new folder
		var toolBarActions = new ToolBar(compositeTop, SWT.NONE);
		// folder up
		var toolItemParent = new ToolItem(toolBarActions, SWT.NONE);
		toolItemParent.setImage(imageRepository.getImage("FS_LevelUp.gif")); //$NON-NLS-1$
		toolItemParent.addListener(SWT.Selection, e -> toolItemParentWidgetSelected());
		// new folder
		var toolItemNewFolder = new ToolItem(toolBarActions, SWT.NONE);
		toolItemNewFolder.setImage(imageRepository.getImage("FS_Folder_New.gif")); //$NON-NLS-1$
		toolItemNewFolder.setDisabledImage(imageRepository.getImage("FS_Folder_New_disabled.gif")); //$NON-NLS-1$
		toolItemNewFolder.addListener(SWT.Selection, e -> toolItemNewFolderWidgetSelected());
		toolItemNewFolder.setEnabled(false);

		// table showing files and folders
		var tableItemsLData = new GridData();
		tableItemsLData.grabExcessHorizontalSpace = true;
		tableItemsLData.grabExcessVerticalSpace = true;
		tableItemsLData.horizontalAlignment = SWT.FILL;
		tableItemsLData.verticalAlignment = SWT.FILL;
		tableItems = new Table(dialogShell, SWT.SINGLE | SWT.BORDER);
		tableItems.setHeaderVisible(true);
		tableItems.setLayoutData(tableItemsLData);
		tableItems.addListener(SWT.MouseDown, this::tableItemSelected);
		tableItems.addListener(SWT.MouseDoubleClick, this::tableItemDoubleClicked);

		var tableColumnName = new TableColumn(tableItems, SWT.NONE);
		tableColumnName.setText(Messages.getString("FileObjectChooser.TableHeaderFileName")); //$NON-NLS-1$
		tableColumnName.setWidth(200);

		var tableColumnSize = new TableColumn(tableItems, SWT.NONE);
		tableColumnSize.setText(Messages.getString("FileObjectChooser.TableHeaderSize")); //$NON-NLS-1$
		tableColumnSize.setWidth(60);

		var tableColumnType = new TableColumn(tableItems, SWT.NONE);
		tableColumnType.setText(Messages.getString("FileObjectChooser.TableHeaderType")); //$NON-NLS-1$
		tableColumnType.setWidth(100);

		var tableColumnDateModified = new TableColumn(tableItems, SWT.NONE);
		tableColumnDateModified.setText(Messages.getString("FileObjectChooser.TableHeaderDateModified")); //$NON-NLS-1$
		tableColumnDateModified.setWidth(145);

		// bottom area
		var compositeBottom = new Composite(dialogShell, SWT.NONE);
		var compositeBottomLayout = new GridLayout();
		compositeBottomLayout.numColumns = 3;
		var compositeBottomLData = new GridData();
		compositeBottomLData.horizontalAlignment = SWT.FILL;
		compositeBottom.setLayoutData(compositeBottomLData);
		compositeBottom.setLayout(compositeBottomLayout);

		var labelFilename = new Label(compositeBottom, SWT.NONE);
		labelFilename.setText(Messages.getString("FileObjectChooser.SelectedFileName")); //$NON-NLS-1$

		var textFilenameLData = new GridData();
		textFilenameLData.horizontalAlignment = SWT.FILL;
		textFilenameLData.grabExcessHorizontalSpace = true;
		textFilename = new Text(compositeBottom, SWT.BORDER);
		textFilename.setLayoutData(textFilenameLData);

		var buttonOk = new Button(compositeBottom, SWT.PUSH | SWT.CENTER);
		var buttonOkLData = new GridData();
		buttonOkLData.horizontalAlignment = GridData.CENTER;
		buttonOkLData.widthHint = UISettings.BUTTON_WIDTH;
		buttonOkLData.heightHint = UISettings.BUTTON_HEIGHT;
		buttonOk.setLayoutData(buttonOkLData);
		buttonOk.setText(Messages.getString("FileObjectChooser.Open")); //$NON-NLS-1$
		buttonOk.addListener(SWT.Selection, this::okSelected);
		// file filter
		var labelFileFilter = new Label(compositeBottom, SWT.NONE);
		labelFileFilter.setText(Messages.getString("FileObjectChooser.FileTypeFilter")); //$NON-NLS-1$

		var comboFileFilter = new Combo(compositeBottom, SWT.NONE);
		var comboFileFilterLData = new GridData();
		comboFileFilterLData.horizontalAlignment = SWT.FILL;
		comboFileFilter.setLayoutData(comboFileFilterLData);
		comboFileFilter.setText(Messages.getString("FileObjectChooser.FileTypeFilter.AllFiles")); //$NON-NLS-1$
		comboFileFilter.setEnabled(false);

		var buttonCancel = new Button(compositeBottom, SWT.PUSH | SWT.CENTER);
		var buttonCancelLData = new GridData();
		buttonCancelLData.widthHint = UISettings.BUTTON_WIDTH;
		buttonCancelLData.heightHint = UISettings.BUTTON_HEIGHT;
		buttonCancel.setLayoutData(buttonCancelLData);
		buttonCancel.setText(Messages.getString("FileObjectChooser.Cancel")); //$NON-NLS-1$
		buttonCancel.addListener(SWT.Selection, this::cancelSelected);

		setBaseFileObject(base);

		try {
			result = false;
			dialogShell.open();
			ShellStateHandler.apply(preferences, dialogShell, FileObjectChooser.class);
			var display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void okSelected(Event event) {
		result = true;
		dialogShell.getDisplay().asyncExec(dialogShell::dispose);
	}

	private void cancelSelected(Event event) {
		result = false;
		dialogShell.getDisplay().asyncExec(dialogShell::dispose);
	}

	private void tableItemDoubleClicked(Event event) {
		var items = tableItems.getSelection();
		if (items.length > 0) {
			try {
				var item = items[0];
				var file = (FileObject) item.getData();
				if (file.getType().hasChildren()) {
					setActiveFileObject(file);
				}
			}
			catch (FileSystemException fse) {
				fse.printStackTrace();
			}
		}
	}

	private void tableItemSelected(Event event) {
		var items = tableItems.getSelection();
		if (items.length > 0) {
			var item = items[0];
			var file = (FileObject) item.getData();
			// FIXME if we are looking for files, just take files, otherwise just take dirs
			setSelectedFileObject(file);
		}
	}

	private void populateList() throws FileSystemException {
		var children = activeFileObject.getChildren();
		Arrays.sort(children, (o1, o2) -> {
			try {
				if ((o1.getType() == FileType.FOLDER) && (o2.getType() == FileType.FILE)) {
					return -1;
				}
				else if ((o1.getType() == FileType.FILE) && (o2.getType() == FileType.FOLDER)) {
					return 1;
				}
				return o1.getName().getBaseName().compareTo(o2.getName().getBaseName());
			}
			catch (FileSystemException fse) {
				fse.printStackTrace();
				return 0;
			}
		});

		var df = DateFormat.getDateTimeInstance();

		for (var i = 0; i < children.length; i++) {
			var data = children[i];

			TableItem item;
			if (tableItems.getItemCount() <= i) {
				item = new TableItem(tableItems, SWT.NULL);
			}
			else {
				item = tableItems.getItem(i);
			}

			item.setText(0, data.getName().getBaseName());
			var type = new StringBuilder().append(data.getType().getName()); // FIXME: translate type name {file,folder}

			if (data.getType().hasContent()) {
				var content = data.getContent();
				var contentType = content.getContentInfo().getContentType();
				if (null != contentType) {
					type.append(" (").append(contentType).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				item.setText(1, String.valueOf(content.getSize()));
				item.setText(3, df.format(new Date(content.getLastModifiedTime())));
			}
			else {
				item.setText(1, ""); //$NON-NLS-1$
				item.setText(3, ""); //$NON-NLS-1$
			}
			item.setText(2, type.toString());

			if (data.getType() == FileType.FOLDER) {
				item.setImage(imageRepository.getImage("FS_Folder_Collapsed.gif")); //$NON-NLS-1$
			}
			else {
				item.setImage(imageRepository.getImage("FS_File_text_plain.gif")); //$NON-NLS-1$
			}

			item.setData(data);
		}
		tableItems.setItemCount(children.length);
	}

	private void updateActiveFileObject() throws FileSystemException {
		tableItems.deselectAll();
		populateList();
		textUrlExtension.setText(rootFileObject.getName().getRelativeName(activeFileObject.getName()));
	}

	private void updateSelectedFileObject() {
		if (null != selectedFileObject) {
			textFilename.setText(selectedFileObject.getName().getBaseName());
		}
		else {
			textFilename.setText(""); //$NON-NLS-1$
		}
	}

	public void setBaseFileObject(final FileObject base) {
		if (null == activeFileObject) {
			activeFileObject = base;
		}
		try {
			rootFileObject = base.resolveFile("/"); //$NON-NLS-1$
			labelBaseUrl.setText(rootFileObject.getName().toString());
			setActiveFileObject(activeFileObject);
		}
		catch (FileSystemException e) {
			ExceptionHandler.reportException(e);
		}
	}

	private void setActiveFileObject(final FileObject active) throws FileSystemException {
		activeFileObject = active;
		if (null != dialogShell) {
			updateActiveFileObject();
		}
	}

	public FileObject getActiveFileObject() {
		return activeFileObject;
	}

	public void setSelectedFileObject(final FileObject selected) {
		this.selectedFileObject = selected;
		if (null != dialogShell) {
			updateSelectedFileObject();
		}
	}

	private void toolItemParentWidgetSelected() {
		try {
			var parent = activeFileObject.getParent();
			if (null != parent) {
				setActiveFileObject(parent);
			}
		}
		catch (FileSystemException e) {
			e.printStackTrace();
		}
	}

	private void toolItemNewFolderWidgetSelected() {
		// FIXME: create new folder
	}
}
