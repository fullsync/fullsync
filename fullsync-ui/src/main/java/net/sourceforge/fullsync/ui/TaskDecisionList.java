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

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskTree;

class TaskDecisionList extends Composite {
	private Table tableLogLines;
	private int tableLogLinesFillIndex;
	private int tableLogLinesFillCount;
	private final Map<ActionType, Image> actionImages = new EnumMap<>(ActionType.class);
	private final Map<Integer, Image> taskImages = new HashMap<>();
	private Image locationSource;
	private Image locationDestination;
	private Image locationBoth;
	private Image nodeFile;
	private Image nodeDirectory;
	private Image nodeUndefined;
	private TaskTree taskTree;
	private final Map<Task, TableItem> taskItemMap = new HashMap<>();
	private boolean onlyChanges;
	private boolean changeAllowed;

	TaskDecisionList(Composite parent, ImageRepository imageRepository) {
		super(parent, SWT.NULL);
		try {
			this.setSize(550, 500);

			tableLogLines = new Table(this, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
			var tableLogLinesLData = new GridData();
			tableLogLinesLData.verticalAlignment = SWT.FILL;
			tableLogLinesLData.horizontalAlignment = SWT.FILL;
			tableLogLinesLData.horizontalIndent = 0;
			tableLogLinesLData.horizontalSpan = 3;
			tableLogLinesLData.grabExcessHorizontalSpace = true;
			tableLogLinesLData.grabExcessVerticalSpace = true;
			tableLogLines.setLayoutData(tableLogLinesLData);
			tableLogLines.setHeaderVisible(true);
			tableLogLines.setLinesVisible(true);

			var tableColumnFilename = new TableColumn(tableLogLines, SWT.NONE);
			tableColumnFilename.setText(Messages.getString("TaskDecisionList.Filename")); //$NON-NLS-1$
			tableColumnFilename.setWidth(240);

			var tableColumnSourceSize = new TableColumn(tableLogLines, SWT.NONE);
			tableColumnSourceSize.setText(Messages.getString("TaskDecisionList.Size")); //$NON-NLS-1$
			tableColumnSourceSize.setAlignment(SWT.RIGHT);
			tableColumnSourceSize.setWidth(90);

			var tableColumnAction = new TableColumn(tableLogLines, SWT.NONE);
			tableColumnAction.setResizable(false);
			tableColumnAction.setText(Messages.getString("TaskDecisionList.Action")); //$NON-NLS-1$
			tableColumnAction.setWidth(70);

			var tableColumnExplanation = new TableColumn(tableLogLines, SWT.NONE);
			tableColumnExplanation.setText(Messages.getString("TaskDecisionList.Explanation")); //$NON-NLS-1$
			tableColumnExplanation.setWidth(170);

			tableLogLines.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent evt) {
					tableLogLinesMouseUp(evt);
				}
			});

			var thisLayout = new GridLayout();
			this.setLayout(thisLayout);
			this.layout();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
		initializeImages(imageRepository);
		onlyChanges = true;
		changeAllowed = true;
	}

	public void setTaskTree(TaskTree task) {
		this.taskTree = task;
	}

	private void initializeImages(ImageRepository imageRepository) {
		nodeFile = imageRepository.getImage("Node_File.png"); //$NON-NLS-1$
		nodeDirectory = imageRepository.getImage("Node_Directory.png"); //$NON-NLS-1$
		nodeUndefined = imageRepository.getImage("Node_Undefined.png"); //$NON-NLS-1$
		locationSource = imageRepository.getImage("Location_Source.png"); //$NON-NLS-1$
		locationDestination = imageRepository.getImage("Location_Destination.png"); //$NON-NLS-1$
		locationBoth = imageRepository.getImage("Location_Both.png"); //$NON-NLS-1$

		for (ActionType action : ActionType.values()) {
			actionImages.put(action, imageRepository.getImage(getActionTypeImage(action)));
		}
	}

	private String getActionTypeImage(ActionType actionType) {
		return switch (actionType) {
			case NOTHING -> "Action_Nothing.png"; //$NON-NLS-1$
			case ADD -> "Action_Add.png"; //$NON-NLS-1$
			case UPDATE -> "Action_Update.png"; //$NON-NLS-1$
			case DELETE -> "Action_Delete.png"; //$NON-NLS-1$
			case NOT_DECIDABLE_ERROR -> "Action_NotDecidableError.png"; //$NON-NLS-1$
			case UNEXPECTED_CHANGE_ERROR -> "Action_UnexpectedChangeError.png"; //$NON-NLS-1$
			case DIR_HERE_FILE_THERE_ERROR -> "Action_DirHereFileThereError.png"; //$NON-NLS-1$
		};
	}

	private void drawSide(GC g, Task t, Action a, Location location) {
		FSFile n;
		if (null == t) {
			n = null;
		}
		else if (location == Location.SOURCE) {
			n = t.getSource();
		}
		else {
			n = t.getDestination();
		}

		var x = location == Location.SOURCE ? 2 : (2 * 16) + 2;

		if (null == n) {
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

		if ((a.getLocation() == location) || (a.getLocation() == Location.BOTH)) {
			var actionImage = actionImages.get(a.getType());
			if (null != actionImage) {
				g.drawImage(actionImage, x, 0);
			}
			if (location == Location.SOURCE) {
				g.drawImage(locationSource, x + 16, 0);
			}
			else {
				g.drawImage(locationDestination, x - 16, 0);
			}
		}
	}

	// FIXME: implement using ImageDataProvider?
	private void drawLocation(GC g, Action a) {
		switch (a.getLocation()) {
			case SOURCE:
				g.drawImage(locationSource, 16 + 2, 0);
				break;
			case DESTINATION:
				g.drawImage(locationDestination, 16 + 2, 0);
				break;
			case BOTH:
				g.drawImage(locationBoth, 16 + 2, 0);
				break;
			case BUFFER:
			case NONE:
				break;
		}
	}

	private Integer calcTaskImageHash(Task t, Action a) {
		var hash = 0;

		// using 5 bits for files
		if (null == t) {
			hash |= 1;
		}
		else {
			var src = t.getSource();
			var dst = t.getDestination();
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
		hash |= a.getLocation().ordinal() << 6;
		hash |= a.getType().ordinal() << 8;

		return hash;
	}

	private Image buildTaskImage(Task t, Action a) {
		var data = new ImageData((16 * 3) + 2, 16, 8, new PaletteData(255, 255, 255));
		data.transparentPixel = data.palette.getPixel(new RGB(0, 0, 0));

		var image = new Image(null, data);
		var g = new GC(image);
		try {
			drawSide(g, t, a, Location.SOURCE);
			drawSide(g, t, a, Location.DESTINATION);
			drawLocation(g, a);
		}
		finally {
			g.dispose();
		}
		addDisposeListener(e -> image.dispose());
		return image;
	}

	private Image getTaskImage(Task t, Action a) {
		return taskImages.computeIfAbsent(calcTaskImageHash(t, a), key -> buildTaskImage(t, a));
	}

	private void addTaskChildren(Task task) {
		for (Task t : task.getChildren()) {
			addTask(t);
		}
	}

	private void addTask(Task t) {
		if (!onlyChanges || (t.getCurrentAction().getType() != ActionType.NOTHING)) {
			var image = getTaskImage(t, t.getCurrentAction());

			TableItem item;
			if (tableLogLinesFillIndex < tableLogLinesFillCount) {
				item = tableLogLines.getItem(tableLogLinesFillIndex);
			}
			else {
				item = new TableItem(tableLogLines, SWT.NULL);
				++tableLogLinesFillCount;
			}
			++tableLogLinesFillIndex;
			item.setImage(2, image);
			var text = new String[4];
			text[0] = t.getSource().getDisplayPath();
			text[1] = formatSize(t);
			text[2] = ""; //$NON-NLS-1$
			text[3] = t.getCurrentAction().getExplanation();
			item.setText(text);
			item.setData(t);

			taskItemMap.put(t, item);
		}
		addTaskChildren(t);
	}

	private String formatSize(final Task t) {
		long size = -1;
		final var action = t.getCurrentAction();
		if ((action.getType() == ActionType.ADD) || (action.getType() == ActionType.UPDATE)) {
			switch (action.getLocation()) {
				case SOURCE -> size = t.getDestination().getSize();
				case DESTINATION -> size = t.getSource().getSize();
				default -> {
				}
			}
		}
		return UISettings.formatSize(size);
	}

	private void updateTask(TableItem item) {
		var t = (Task) item.getData();
		var image = getTaskImage(t, t.getCurrentAction());
		item.setImage(2, image);
		item.setText(3, t.getCurrentAction().getExplanation());
	}

	void rebuildActionList() {
		tableLogLinesFillIndex = 0;
		tableLogLinesFillCount = tableLogLines.getItemCount();

		setRedraw(false);
		addTaskChildren(taskTree.root());
		setRedraw(true);

		// index is always pointing at the next free slot
		if (tableLogLinesFillIndex < tableLogLinesFillCount) {
			tableLogLines.setItemCount(tableLogLinesFillIndex);
			tableLogLinesFillCount = tableLogLines.getItemCount();
		}
	}

	private boolean isSameAction(Action a, Action b) {
		var same = a.getType() == b.getType();
		same = same && (a.getLocation() == b.getLocation());
		return same && a.getExplanation().equals(b.getExplanation());
	}

	private void actionSelected(Event e) {
		var tableItemList = tableLogLines.getSelection();
		var targetAction = (Action) e.widget.getData();
		for (TableItem item : tableItemList) {
			var task = (Task) item.getData();
			var actions = task.getActions();

			for (var i = 0; i < actions.length; ++i) {
				if (isSameAction(actions[i], targetAction)) {
					task.setCurrentAction(i);
					break;
				}
			}

			updateTask(item);
		}
	}

	private void showPopup(int x, int y) {
		final var tableItemList = tableLogLines.getSelection();
		// TODO impl some kind of ActionList supporting "containsAction"
		// and "indexOfAction" using own comparison rules
		if (tableItemList.length == 0) {
			return;
		}

		List<Task> taskList = Arrays.stream(tableItemList).map(i -> (Task) i.getData()).toList();

		// load initial actions of first task
		var possibleActions = taskList.getFirst().getActions().clone();

		for (Task task : taskList) {
			// invalidate all possible actions we don't find in this actionlist
			var actions = task.getActions();

			for (var iPosAction = 0; iPosAction < possibleActions.length; iPosAction++) {
				var action = possibleActions[iPosAction];
				var found = false;

				if (null != action) {
					// check whether action is also supported by this task
					for (Action a : actions) {
						if (isSameAction(a, action)) {
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
		}

		var referenceTask = taskList.size() == 1 ? taskList.getFirst() : null;

		var m = new Menu(this);
		for (Action action : possibleActions) {
			if (null != action) {
				var image = getTaskImage(referenceTask, action);
				var mi = new MenuItem(m, SWT.NULL);
				mi.setImage(image);
				mi.setText(action.getType().toString() + " - " + action.getExplanation()); //$NON-NLS-1$
				mi.setData(action);
				mi.addListener(SWT.Selection, this::actionSelected);
			}
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

	void showItem(TableItem item) {
		tableLogLines.showItem(item);
	}

	private void tableLogLinesMouseUp(MouseEvent evt) {
		if (changeAllowed && (evt.button == 3)) {
			showPopup(evt.x, evt.y);
		}
	}

	TableItem getTableItemForTask(Task task) {
		return taskItemMap.get(task);
	}
}
