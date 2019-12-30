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
package net.sourceforge.fullsync;

import net.sourceforge.fullsync.buffer.BlockBuffer;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.impl.FillBufferTaskExecutor;

public class TaskTree {
	private final Site source;
	private final Site destination;
	private final Task root;

	public TaskTree(Site source, Site destination, Task root) {
		this.source = source;
		this.destination = destination;
		this.root = root;
	}

	public Task getRoot() {
		return root;
	}

	public Site getDestination() {
		return destination;
	}

	public Site getSource() {
		return source;
	}

	public int getTaskCount() {
		return root.getTaskCount();
	}

	public IoStatistics getIoStatistics() {
		// FIXME HACK omg, that's not the way io stats are intended to be generated / used
		BlockBuffer buffer = new BlockBuffer(null);
		TaskExecutor queue = new FillBufferTaskExecutor(buffer);
		return queue.createStatistics(this);
	}
}
