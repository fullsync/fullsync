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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.eclipse.swt.widgets.Display;

public class GUIUpdateQueue<ITEM> { // NO_UCD (use default)
	private final Display display;
	private final Queue<ITEM> queue = new ConcurrentLinkedQueue<>();
	private final AtomicBoolean updateScheduled = new AtomicBoolean(false);
	private final Consumer<List<ITEM>> updateTask;

	public GUIUpdateQueue(Display d, Consumer<List<ITEM>> task) { // NO_UCD (use default)
		display = d;
		updateTask = task;
	}

	public synchronized void add(ITEM item) { // NO_UCD (use default)
		queue.add(item);
		if (!updateScheduled.get()) {
			updateScheduled.set(true);
			display.asyncExec(() -> {
				var items = getItems();
				if (!items.isEmpty()) {
					updateTask.accept(items);
				}
			});
		}
	}

	private synchronized List<ITEM> getItems() {
		List<ITEM> items = new LinkedList<>();
		ITEM item;
		while (null != (item = queue.poll())) {
			items.add(item);
		}
		updateScheduled.set(false);
		return items;
	}
}
