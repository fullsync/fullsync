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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Display;

public class GUIUpdateQueue<Item> {
	public static interface GUIUpdateTask<Item> {
		void doUpdate(Display display, LinkedList<Item> items);
	}

	private Display m_display;
	private ConcurrentLinkedQueue<Item> m_queue;
	private AtomicBoolean m_updateScheduled;
	private GUIUpdateTask<Item> m_updateTask;

	public GUIUpdateQueue(Display display, GUIUpdateTask<Item> guiUpdateTask) {
		m_display = display;
		m_queue = new ConcurrentLinkedQueue<Item>();
		m_updateScheduled = new AtomicBoolean(false);
		m_updateTask = guiUpdateTask;
	}

	public synchronized void add(Item item) {
		m_queue.add(item);
		if (!m_updateScheduled.get()) {
			m_updateScheduled.set(true);
			m_display.asyncExec(() -> {
				LinkedList<Item> items = new LinkedList<Item>();
				getItems(items);
				if (!items.isEmpty()) {
					m_updateTask.doUpdate(m_display, items);
				}
			});
		}
	}

	private synchronized void getItems(LinkedList<Item> items) {
		Item item;
		while (null != (item = m_queue.poll())) {
			items.add(item);
		}
		m_updateScheduled.set(false);
	}
}
