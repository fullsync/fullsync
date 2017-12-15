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

import java.util.List;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableItem;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.IoStatistics;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskFinishedEvent;
import net.sourceforge.fullsync.TaskTree;

public class TaskDecisionPage extends WizardDialog {
	private GuiController guiController;
	private TaskTree taskTree;
	private boolean processing;
	private int tasksFinished;
	private int tasksTotal;

	private final Synchronizer synchronizer;
	private final Display display;
	private final Color colorFinishedSuccessful;
	private final Color colorFinishedUnsuccessful;

	private TaskDecisionList list;
	private Combo comboFilter;
	private Label labelProgress;

	@Inject
	public TaskDecisionPage(MainWindow mainWindow, Display display, Synchronizer synchronizer, GuiController guiController) {
		super(mainWindow.getShell());
		this.display = display;
		this.synchronizer = synchronizer;
		this.guiController = guiController;
		colorFinishedSuccessful = new Color(display, 150, 255, 150);
		colorFinishedUnsuccessful = new Color(display, 255, 150, 150);
		mainWindow.getShell().addDisposeListener(e -> {
			colorFinishedSuccessful.dispose();
			colorFinishedUnsuccessful.dispose();
		});
	}

	public void setTaskTree(TaskTree taskTree) {
		this.taskTree = taskTree;
	}

	@Override
	public String getTitle() {
		return Messages.getString("TaskDecisionPage.TaskDecision"); //$NON-NLS-1$
	}

	@Override
	public String getCaption() {
		return Messages.getString("TaskDecisionPage.ChooseTheActions"); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		String source = Messages.getString("TaskDecisionPage.Source"); //$NON-NLS-1$
		String destination = Messages.getString("TaskDecisionPage.Destination"); //$NON-NLS-1$
		String sourcePath = taskTree.getSource().getConnectionDescription().getDisplayPath();
		String destinationPath = taskTree.getDestination().getConnectionDescription().getDisplayPath();
		return String.format("%s: %s%n%s: %s", source, sourcePath, destination, destinationPath); //$NON-NLS-1$
	}

	@Override
	public Image getIcon() {
		return GuiController.getInstance().getImage("Tasklist_Icon.png"); //$NON-NLS-1$
	}

	@Override
	public Image getImage() {
		return GuiController.getInstance().getImage("Tasklist_Wizard.png"); //$NON-NLS-1$
	}

	@Override
	public void createContent(final Composite content) {
		content.setLayout(new GridLayout(2, false));

		// filter combo
		comboFilter = new Combo(content, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboFilter.add(Messages.getString("TaskDecisionPage.Everything")); //$NON-NLS-1$
		comboFilter.add(Messages.getString("TaskDecisionPage.ChangesOnly")); //$NON-NLS-1$
		comboFilter.select(1);
		comboFilter.addModifyListener(e -> {
			if (!processing) {
				list.setOnlyChanges(comboFilter.getSelectionIndex() == 1);
				if (null != taskTree) {
					list.rebuildActionList();
				}
			}
		});

		// progress label
		labelProgress = new Label(content, SWT.NONE);
		GridData labelProgressLData = new GridData();
		labelProgressLData.horizontalAlignment = SWT.FILL;
		labelProgressLData.horizontalIndent = 5;
		labelProgressLData.grabExcessHorizontalSpace = true;
		labelProgress.setLayoutData(labelProgressLData);
		IoStatistics stats = synchronizer.getIoStatistics(taskTree);
		labelProgress.setText("Totals: " + stats.getCountActions() + " tasks, " + UISettings.formatSize(stats.getBytesTransferred()));

		list = new TaskDecisionList(content);
		list.setTaskTree(taskTree);
		GridData listLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		listLayoutData.horizontalSpan = 2;
		list.setLayoutData(listLayoutData);

		list.setOnlyChanges(true);
		if (null != taskTree) {
			list.rebuildActionList();
		}
	}

	@Override
	public boolean apply() {
		if (!processing) {
			setCancelButtonEnabled(false);
			performActions();
		}
		return false;
	}

	@Override
	public boolean cancel() {
		if (!processing) {
			try {
				taskTree.getSource().close();
			}
			catch (Exception ex) {
				ExceptionHandler.reportException(ex);
			}
			try {
				taskTree.getDestination().close();
			}
			catch (Exception ex) {
				ExceptionHandler.reportException(ex);
			}
			return true;
		}
		MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
		mb.setText(Messages.getString("TaskDecisionPage.Error")); //$NON-NLS-1$
		mb.setMessage(Messages.getString("TaskDecisionPage.SyncWindowCantBeClosed")); //$NON-NLS-1$
		mb.open();
		//TODO: implement canceling a running profile
		return false;
	}

	void performActions() {
		Thread worker = new Thread(this::doPerformActions, "ActionPerformer"); //$NON-NLS-1$
		worker.start();
	}

	private void doPerformActions() {
		guiController.showBusyCursor(true);
		try {
			processing = true;
			list.setChangeAllowed(false);

			IoStatistics stats = synchronizer.getIoStatistics(taskTree);
			tasksTotal = stats.getCountActions();
			tasksFinished = 0;
			display.syncExec(() -> setOkButtonEnabled(false));

			GUIUpdateQueue<TaskFinishedEvent> updateQueue = new GUIUpdateQueue<>(display, this::updateTaskStatus);

			synchronizer.performActions(taskTree, updateQueue::add);

			display.asyncExec(() -> {
				// Notification Window.
				MessageBox mb = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
				mb.setText(Messages.getString("TaskDecisionPage.Finished")); //$NON-NLS-1$
				mb.setMessage(Messages.getString("TaskDecisionPage.ProfileFinished")); //$NON-NLS-1$
				mb.open();
				checkAndCancel();
			});
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
		finally {
			guiController.showBusyCursor(false);
			processing = false;
			list.setChangeAllowed(true);
		}
	}

	private void updateTaskStatus(List<TaskFinishedEvent> items) {
		TableItem item = null;
		tasksFinished += items.size();
		for (TaskFinishedEvent event : items) {
			Task task = event.getTask();
			item = list.getTableItemForTask(task);
			// FIXME This doesn't seams to work. Even if there is an exception in the sync of one item
			// the item is colored with the "successful" color.
			if (null != item) {
				if (event.isSuccessful()) {
					item.setBackground(colorFinishedSuccessful);
				}
				else {
					item.setBackground(colorFinishedUnsuccessful);
				}
			}
		}
		String of = Messages.getString("TaskDecisionPage.of"); //$NON-NLS-1$
		String finished = Messages.getString("TaskDecisionPage.tasksFinished"); //$NON-NLS-1$
		// TODO: move this into one translatable string with arguments
		labelProgress.setText(String.format("%d %s %d %s", tasksFinished, of, tasksTotal, finished)); //$NON-NLS-1$
		if (null != item) {
			list.showItem(item);
		}
	}
}
