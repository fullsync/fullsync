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
import java.io.Serializable;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.IoStatistics;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskFinishedEvent;
import net.sourceforge.fullsync.TaskFinishedListener;
import net.sourceforge.fullsync.TaskTree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
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
/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class TaskDecisionPage implements WizardPage, Serializable {
	private static final long serialVersionUID = 2L;
	private transient WizardDialog dialog;
	private transient GuiController guiController;
	private transient Profile profile;
	private transient TaskTree taskTree;
	private transient boolean processing;
	private transient int tasksFinished;
	private transient int tasksTotal;

	private transient TaskDecisionList list;
	private transient Combo comboFilter;
	private transient Label labelProgress;

	public TaskDecisionPage(WizardDialog dialog, GuiController guiController, Profile profile, TaskTree taskTree) {
		this.dialog = dialog;
		this.guiController = guiController;
		this.profile = profile;
		this.taskTree = taskTree;

		dialog.setPage(this);
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
		return Messages.getString("TaskDecisionPage.Source") + ": " + taskTree.getSource().getUri() + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ Messages.getString("TaskDecisionPage.Destination") + ": " + taskTree.getDestination().getUri(); //$NON-NLS-1$ //$NON-NLS-2$
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
	public void createContent(Composite content) {
		list = new TaskDecisionList(content, SWT.NULL);
		list.setTaskTree(taskTree);
		list.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		content.setLayout(new GridLayout());

		dialog.getShell().addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent event) {
				if (processing) {
					MessageBox mb = new MessageBox(dialog.getShell(), SWT.ICON_ERROR | SWT.OK);
					mb.setText(Messages.getString("TaskDecisionPage.Error")); //$NON-NLS-1$
					mb.setMessage(Messages.getString("TaskDecisionPage.SyncWindowCantBeClosed")); //$NON-NLS-1$
					mb.open();

					event.doit = false;
				}
				else {
					try {
						taskTree.getSource().close();
						taskTree.getDestination().close();
					}
					catch (IOException ioe) {
						ExceptionHandler.reportException(ioe);
					}
				}
			}
		});
		
		Composite compositeBottom = new Composite(content, SWT.FILL);
		GridLayout compositeBottomLayout = new GridLayout();
		GridData compositeBottomLData = new GridData();
		compositeBottomLData.grabExcessHorizontalSpace = true;
		compositeBottomLData.horizontalAlignment = GridData.FILL;
		compositeBottom.setLayoutData(compositeBottomLData);
		compositeBottomLayout.numColumns = 3;
		compositeBottom.setLayout(compositeBottomLayout);
		{
			comboFilter = new Combo(compositeBottom, SWT.DROP_DOWN | SWT.READ_ONLY);
			GridData comboFilterLData = new GridData();
			comboFilterLData.widthHint = 90;
			comboFilterLData.heightHint = 21;
			comboFilter.setLayoutData(comboFilterLData);
			comboFilter.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent evt) {
					if (!processing) {
						list.setOnlyChanges(comboFilter.getSelectionIndex() == 1);
						if (taskTree != null) {
							list.rebuildActionList();
						}
					}
				}
			});
		}
		{
			labelProgress = new Label(compositeBottom, SWT.NONE);
			GridData labelProgressLData = new GridData();
			labelProgressLData.horizontalAlignment = GridData.FILL;
			labelProgressLData.heightHint = 13;
			labelProgressLData.horizontalIndent = 5;
			labelProgressLData.grabExcessHorizontalSpace = true;
			labelProgress.setLayoutData(labelProgressLData);
			labelProgress.setSize(new org.eclipse.swt.graphics.Point(42, 13));
			Synchronizer synchronizer = GuiController.getInstance().getSynchronizer();
			IoStatistics stats = synchronizer.getIoStatistics(taskTree);
			labelProgress.setText("Totals: " + stats.getCountActions() + " tasks, " + stats.getBytesTransferred() + " bytes");
		}

		comboFilter.add(Messages.getString("TaskDecisionPage.Everything")); //$NON-NLS-1$
		comboFilter.add(Messages.getString("TaskDecisionPage.ChangesOnly")); //$NON-NLS-1$
		comboFilter.select(1);

	}

	@Override
	public boolean apply() {
		if (!processing) {
			dialog.setCancelButtonEnabled(false);
			performActions();
		}
		return false;
	}
	
	@Override
	public boolean cancel() {
		if (!processing) {
			return true;
		}
		//FIXME: tell the user this can't be canceled currently, or implement that
		return false;
	}
	
	void performActions() {
		Thread worker = new Thread(new Runnable() {
			@Override
			public void run() {
				guiController.showBusyCursor(true);
				try {
					final Display display = dialog.getDisplay();
					processing = true;
					list.setChangeAllowed(false);

					Synchronizer synchronizer = GuiController.getInstance().getSynchronizer();
					IoStatistics stats = synchronizer.getIoStatistics(taskTree);
					tasksTotal = stats.getCountActions();
					tasksFinished = 0;

					final Color colorFinishedSuccessful = new Color(null, 150, 255, 150);
					final Color colorFinishedUnsuccessful = new Color(null, 255, 150, 150);

					display.syncExec(new Runnable() {
						@Override
						public void run() {
							dialog.setOkButtonEnabled(false);
						}
					});

					synchronizer.performActions(taskTree, new TaskFinishedListener() {
						@Override
						public void taskFinished(final TaskFinishedEvent event) {
							display.asyncExec(new Runnable() {
								@Override
								public void run() {
									tasksFinished++;
									labelProgress
											.setText(tasksFinished
													+ " " + Messages.getString("TaskDecisionPage.of") + " " + tasksTotal + " " + Messages.getString("TaskDecisionPage.tasksFinished")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
									Task task = event.getTask();
									TableItem item = list.getTableItemForTask(task);
									// TODO This doesn't seams to work. Even if there is an exception in the sync of one item
									// the item is colored with the "successful" color.
									if (item != null) {
										if (event.isSuccessful()) {
											item.setBackground(colorFinishedSuccessful);
										}
										else {
											item.setBackground(colorFinishedUnsuccessful);
										}
										list.showItem(item);
									}
								}
							});
						}
					});

					dialog.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							// Notification Window.
							MessageBox mb = new MessageBox(dialog.getShell(), SWT.ICON_INFORMATION | SWT.OK);
							mb.setText(Messages.getString("TaskDecisionPage.Finished")); //$NON-NLS-1$
							mb.setMessage(Messages.getString("TaskDecisionPage.ProfileFinished")); //$NON-NLS-1$
							mb.open();
							// dialog.getShell().dispose();
						}
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
		}, "ActionPerformer"); //$NON-NLS-1$
		worker.start();
	}
}
