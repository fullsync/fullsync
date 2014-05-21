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

import java.util.HashMap;
import java.util.Hashtable;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.fs.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TaskDecisionList extends Composite {
	private TableColumn tableColumnExplanation;
	private TableColumn tableColumnFilename;
	private TableColumn tableColumnAction;
	private TableColumn tableColumnSourceSize;
	private Table tableLogLines;
	private int tableLogLinesFillIndex;
	private int tableLogLinesFillCount;

	private Hashtable<Integer, Image> actionImages;
	private Hashtable<Integer, Image> taskImages;
	private Image locationSource;
	private Image locationDestination;
	private Image locationBoth;
	private Image nodeFile;
	private Image nodeDirectory;
	private Image nodeUndefined;

	private TaskTree taskTree;
	private final HashMap<Task, TableItem> taskItemMap;

	private boolean onlyChanges;
	private boolean changeAllowed;

	public TaskDecisionList(Composite parent, int style) {
		super(parent, style);

		this.taskItemMap = new HashMap<Task, TableItem>();
		try {
			this.setSize(550, 500);

			tableLogLines = new Table(this, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
			GridData tableLogLinesLData = new GridData();
			tableLogLinesLData.verticalAlignment = SWT.FILL;
			tableLogLinesLData.horizontalAlignment = SWT.FILL;
			tableLogLinesLData.horizontalIndent = 0;
			tableLogLinesLData.horizontalSpan = 3;
			tableLogLinesLData.grabExcessHorizontalSpace = true;
			tableLogLinesLData.grabExcessVerticalSpace = true;
			tableLogLines.setLayoutData(tableLogLinesLData);
			tableLogLines.setHeaderVisible(true);
			tableLogLines.setLinesVisible(true);

			tableColumnFilename = new TableColumn(tableLogLines, SWT.NONE);
			tableColumnFilename.setText(Messages.getString("TaskDecisionList.Filename")); //$NON-NLS-1$
			tableColumnFilename.setWidth(240);

			tableColumnSourceSize = new TableColumn(tableLogLines, SWT.NONE);
			tableColumnSourceSize.setText(Messages.getString("TaskDecisionList.Size")); //$NON-NLS-1$
			tableColumnSourceSize.setAlignment(SWT.RIGHT);
			tableColumnSourceSize.setWidth(90);

			tableColumnAction = new TableColumn(tableLogLines, SWT.NONE);
			tableColumnAction.setResizable(false);
			tableColumnAction.setText(Messages.getString("TaskDecisionList.Action")); //$NON-NLS-1$
			tableColumnAction.setWidth(70);

			tableColumnExplanation = new TableColumn(tableLogLines, SWT.NONE);
			tableColumnExplanation.setText(Messages.getString("TaskDecisionList.Explanation")); //$NON-NLS-1$
			tableColumnExplanation.setWidth(170);

			tableLogLines.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent evt) {
					tableLogLinesMouseUp(evt);
				}
			});

			GridLayout thisLayout = new GridLayout();
			this.setLayout(thisLayout);
			this.layout();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
		initializeImages();
		onlyChanges = true;
		changeAllowed = true;
	}

	public static void show(final GuiController guiController, final Profile profile, final TaskTree task, final boolean interactive) {
		final Display display = Display.getDefault();
		display.asyncExec(() -> {
			try {
				final TaskDecisionPage dialog = new TaskDecisionPage(guiController.getMainShell(), guiController, task);
				if (!interactive) {
					dialog.addWizardDialogListener(() -> dialog.performActions());
				}
				dialog.show();
			}
			catch (Exception ex) {
				ExceptionHandler.reportException(ex);
			}
		});
	}

	public void setTaskTree(TaskTree task) {
		this.taskTree = task;
	}

	public void initializeImages() {
		GuiController gui = GuiController.getInstance();
		nodeFile = gui.getImage("Node_File.png"); //$NON-NLS-1$
		nodeDirectory = gui.getImage("Node_Directory.png"); //$NON-NLS-1$
		nodeUndefined = gui.getImage("Node_Undefined.png"); //$NON-NLS-1$
		locationSource = gui.getImage("Location_Source.png"); //$NON-NLS-1$
		locationDestination = gui.getImage("Location_Destination.png"); //$NON-NLS-1$
		locationBoth = gui.getImage("Location_Both.png"); //$NON-NLS-1$

		actionImages = new Hashtable<Integer, Image>();
		for (ActionType action : ActionType.values()) {
			actionImages.put(Integer.valueOf(action.ordinal()), gui.getImage("Action_" + action.name() + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		taskImages = new Hashtable<Integer, Image>();
	}

	@Override
	public void dispose() {
		nodeFile.dispose();
		nodeDirectory.dispose();
		nodeUndefined.dispose();
		locationSource.dispose();
		locationDestination.dispose();
		locationBoth.dispose();
		for (Image i : actionImages.values()) {
			i.dispose();
		}
		for (Image i : taskImages.values()) {
			i.dispose();
		}
		super.dispose();
	}

	protected void drawSide(GC g, Task t, Action a, Location location) {
		File n;
		if (t == null) {
			n = null;
		}
		else if (location == Location.Source) {
			n = t.getSource();
		}
		else {
			n = t.getDestination();
		}

		int x = location == Location.Source ? 2 : (2 * 16) + 2;

		if (n == null) {
			g.drawImage(nodeUndefined, x, 0);
		}
		else if (n.exists()) {
			if (n.isDirectory()) {
				g.drawImage(nodeDirectory, x, 0);
			}
			else {
				g.drawImage(nodeFile, x, 0);
			}
		}
		// TODO draw some not-existing image ?

		if (a.getLocation() != Location.None) {
			Image actionImage = actionImages.get(Integer.valueOf(a.getType().ordinal()));
			if (actionImage != null) {
				g.drawImage(actionImage, x, 0);
			}
			if (location == Location.Source) {
				g.drawImage(locationSource, x + 16, 0);
			}
			else {
				g.drawImage(locationDestination, x - 16, 0);
			}
		}
	}

	protected void drawLocation(GC g, Action a) {
		switch (a.getLocation()) {
			case Source:
				g.drawImage(locationSource, 16 + 2, 0);
				break;
			case Destination:
				g.drawImage(locationDestination, 16 + 2, 0);
				break;
			case Both:
				g.drawImage(locationBoth, 16 + 2, 0);
				break;
		}
	}

	protected Integer calcTaskImageHash(Task t, Action a) {
		int hash = 0;

		// using 5 bits for files
		if (t == null) {
			hash |= 1;
		}
		else {
			File src = t.getSource();
			File dst = t.getDestination();
			if (src.exists()) {
				hash |= 2;
				if (src.isDirectory()) {
					hash |= 4;
				}
			}
			if (dst.exists()) {
				hash |= 8;
				if (dst.isDirectory()) {
					hash |= 16;
				}
			}
		}

		// using 2+ bits for action
		hash |= (a.getLocation().ordinal() << 6);
		hash |= (a.getType().ordinal() << 8);

		return Integer.valueOf(hash);
	}

	protected Image buildTaskImage(Task t, Action a) {
		ImageData data = new ImageData((16 * 3) + 2, 16, 8, new PaletteData(255, 255, 255));
		data.transparentPixel = data.palette.getPixel(new RGB(0, 0, 0));

		Image image = new Image(null, data);
		GC g = new GC(image);
		drawSide(g, t, a, Location.Source);
		drawSide(g, t, a, Location.Destination);
		drawLocation(g, a);
		g.dispose();
		return image;
	}

	protected Image getTaskImage(Task t, Action a) {
		Image image;
		Integer key = calcTaskImageHash(t, a);
		image = taskImages.get(key);
		if (image == null) {
			image = buildTaskImage(t, a);
			taskImages.put(key, image);
		}
		return image;
	}

	protected Image getTaskImage(Action a) {
		return getTaskImage(null, a);
	}

	protected Image getTaskImage(Task t) {
		return getTaskImage(t, t.getCurrentAction());
	}

	protected void addTaskChildren(Task task) {
		for (Task t : task.getChildren()) {
			addTask(t);
		}
	}

	protected void addTask(Task t) {
		if (!onlyChanges || (t.getCurrentAction().getType() != ActionType.Nothing)) {
			Image image = getTaskImage(t);

			TableItem item;
			if (tableLogLinesFillIndex < tableLogLinesFillCount) {
				item = tableLogLines.getItem(tableLogLinesFillIndex);
				tableLogLinesFillIndex++;
			}
			else {
				item = new TableItem(tableLogLines, SWT.NULL);
				tableLogLinesFillIndex++;
				tableLogLinesFillCount++;
			}
			item.setImage(2, image);
			item.setText(new String[] { t.getSource().getPath(), formatSize(t), "", //$NON-NLS-1$
					t.getCurrentAction().getExplanation() });
			item.setData(t);

			taskItemMap.put(t, item);
		}
		addTaskChildren(t);
	}

	private String formatSize(final Task t) {
		long size = -1;
		final Action action = t.getCurrentAction();
		if ((action.getType() == ActionType.Add) || (action.getType() == ActionType.Update)) {
			switch (action.getLocation()) {
				case Source:
					size = t.getDestination().getSize();
					break;
				case Destination:
					size = t.getSource().getSize();
					break;
				default:
					break;
			}
		}
		return UISettings.formatSize(size);
	}

	protected void updateTask(TableItem item) {
		Task t = (Task) item.getData();
		Image image = getTaskImage(t);
		item.setImage(2, image);
		item.setText(3, t.getCurrentAction().getExplanation());
	}

	public void rebuildActionList() {
		tableLogLinesFillIndex = 0;
		tableLogLinesFillCount = tableLogLines.getItemCount();

		setRedraw(false);
		addTaskChildren(taskTree.getRoot());
		setRedraw(true);

		// index is always pointing at the next free slot
		if (tableLogLinesFillIndex < tableLogLinesFillCount) {
			tableLogLines.setItemCount(tableLogLinesFillIndex);
			tableLogLinesFillCount = tableLogLines.getItemCount();
		}
	}

	protected void showPopup(int x, int y) {
		final TableItem[] tableItemList = tableLogLines.getSelection();
		// TODO impl some kind of ActionList supporting "containsAction"
		// and "indexOfAction" using own comparison rules
		if (tableItemList.length == 0) {
			return;
		}

		Listener selListener = e -> {
			Action targetAction = (Action) e.widget.getData();

			for (TableItem item : tableItemList) {
				Task task = (Task) item.getData();
				Action[] actions = task.getActions();

				for (int iAction = 0; iAction < actions.length; iAction++) {
					Action a = actions[iAction];
					if ((a.getType() == targetAction.getType()) && (a.getLocation() == targetAction.getLocation())
							&& a.getExplanation().equals(targetAction.getExplanation())) {
						task.setCurrentAction(iAction);
						break;
					}
				}

				updateTask(item);
			}
		};

		Task[] taskList = new Task[tableItemList.length];
		for (int i = 0; i < tableItemList.length; i++) {
			taskList[i] = (Task) tableItemList[i].getData();
		}

		Menu m = new Menu(this);
		MenuItem mi;

		// load initial actions of first task
		Action[] possibleActions = taskList[0].getActions().clone();

		for (int iTask = 1; iTask < taskList.length; iTask++) {
			// invalidate all possible actions we dont find in this actionlist
			Action[] actions = taskList[iTask].getActions();

			for (int iPosAction = 0; iPosAction < possibleActions.length; iPosAction++) {
				Action action = possibleActions[iPosAction];
				boolean found = false;

				if (action == null) {
					continue;
				}

				// check whether action is also supported by this task
				for (Action a : actions) {
					if ((a.getType() == action.getType()) && (a.getLocation() == action.getLocation())
							&& a.getExplanation().equals(action.getExplanation())) {
						// the action exists
						found = true;
						break;
					}
				}

				if (!found) {
					// invalidate action that is not supported by all selected tasks
					possibleActions[iPosAction] = null;
				}
			}
		}

		Task referenceTask = taskList.length == 1 ? taskList[0] : null;
		for (Action action : possibleActions) {
			if (action == null) {
				continue;
			}

			Image image = getTaskImage(referenceTask, action);
			mi = new MenuItem(m, SWT.NULL);
			mi.setImage(image);
			mi.setText(action.getType().toString() + " - " + action.getExplanation()); //$NON-NLS-1$
			mi.setData(action);
			mi.addListener(SWT.Selection, selListener);
		}

		m.setLocation(tableLogLines.toDisplay(x, y));
		m.setVisible(true);
	}

	public void setOnlyChanges(boolean onlyChanges) {
		this.onlyChanges = onlyChanges;
	}

	public boolean isChangeAllowed() {
		return changeAllowed;
	}

	public void setChangeAllowed(boolean changeAllowed) {
		this.changeAllowed = changeAllowed;
	}

	public void showItem(TableItem item) {
		tableLogLines.showItem(item);
	}

	protected void tableLogLinesMouseUp(MouseEvent evt) {
		if (changeAllowed && (evt.button == 3)) {
			showPopup(evt.x, evt.y);
		}
	}

	public TableItem getTableItemForTask(Task task) {
		return taskItemMap.get(task);
	}
}
