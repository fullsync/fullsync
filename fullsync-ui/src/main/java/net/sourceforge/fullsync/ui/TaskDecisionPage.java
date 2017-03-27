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
import org.eclipse.swt.widgets.Shell;
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

	private TaskDecisionList list;
	private Combo comboFilter;
	private Label labelProgress;

	public TaskDecisionPage(Shell parent, GuiController guiController, TaskTree taskTree) {
		super(parent);
		this.guiController = guiController;
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
		return Messages.getString("TaskDecisionPage.Source") + ": " + taskTree.getSource().getConnectionDescription().getDisplayPath() + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ Messages.getString("TaskDecisionPage.Destination") + ": " + taskTree.getDestination().getConnectionDescription().getDisplayPath(); //$NON-NLS-1$ //$NON-NLS-2$
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
		Synchronizer synchronizer = GuiController.getInstance().getSynchronizer();
		IoStatistics stats = synchronizer.getIoStatistics(taskTree);
		labelProgress.setText("Totals: " + stats.getCountActions() + " tasks, " + UISettings.formatSize(stats.getBytesTransferred()));

		list = new TaskDecisionList(content, SWT.NULL);
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
			catch (IOException ioe) {
				ExceptionHandler.reportException(ioe);
			}
			try {
				taskTree.getDestination().close();
			}
			catch (IOException ioe) {
				ExceptionHandler.reportException(ioe);
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
		Thread worker = new Thread(() -> {
			guiController.showBusyCursor(true);
			final Display display = getDisplay();
			try {
				processing = true;
				list.setChangeAllowed(false);

				Synchronizer synchronizer = GuiController.getInstance().getSynchronizer();
				IoStatistics stats = synchronizer.getIoStatistics(taskTree);
				tasksTotal = stats.getCountActions();
				tasksFinished = 0;

				final Color colorFinishedSuccessful = new Color(null, 150, 255, 150);
				final Color colorFinishedUnsuccessful = new Color(null, 255, 150, 150);

				display.syncExec(() -> setOkButtonEnabled(false));

				final GUIUpdateQueue<TaskFinishedEvent> updateQueue = new GUIUpdateQueue<>(display, (display1, items) -> {
					TableItem item = null;
					System.err.println("GUIUpdateQueue<TaskFinishedEvent>::doUpdate: " + items.size());
					for (TaskFinishedEvent event : items) {
						tasksFinished++;
						// TODO: move this into one translatable string with arguments
						labelProgress
							.setText(tasksFinished
									+ " " + Messages.getString("TaskDecisionPage.of") + " " + tasksTotal + " " + Messages.getString("TaskDecisionPage.tasksFinished")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
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
					if (null != item) {
						list.showItem(item);
					}
				});

				synchronizer.performActions(taskTree, e -> updateQueue.add(e));

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
		}, "ActionPerformer"); //$NON-NLS-1$
		worker.start();
	}
}
