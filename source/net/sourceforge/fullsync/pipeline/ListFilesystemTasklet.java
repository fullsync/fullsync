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
package net.sourceforge.fullsync.pipeline;

import java.util.Collection;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileFilterChain;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.util.SmartQueue;

public class ListFilesystemTasklet extends SyncTasklet<File, File> {
	private final ConnectionDescription location;
	private final SmartQueue<File> backlog;
	private Site site;
	private FileFilterChain filterChain;

	public ListFilesystemTasklet(TaskletWorkNotificationTarget _workNotificationTarget, ConnectionDescription _location, FileFilterChain _filterChain) {
		super(_workNotificationTarget, new SmartQueue<File>());
		backlog = getInput();
		location = _location;
		filterChain = _filterChain;
	}

	@Override
	protected void prepare(SmartQueue<File> queue) throws Exception {
		super.prepare(queue);
		site = FullSync.getFileSystemManager().createConnection(location);
		//FIXME: queue a site.close() for execution on async task cleanup (end of the entire sync)
		queue.offer(site.getRoot());
	}

	@Override
	protected File getNextItem(final SmartQueue<File> queue) {
		File f = null;
		if (!queue.isEmpty()) {
			f = queue.take();
		}
		return f;
	}

	@Override
	public void pause() {
		backlog.pause();
	}

	@Override
	public void resume() {
		backlog.resume();
	}

	@Override
	public void cancel() {
		backlog.cancel();
	}

	@Override
	protected void processItem(final File item) throws Exception {
		if (filterChain.accept(item)) {
			if (item.isDirectory()) {
				Collection<File> children = item.getChildren();
				for (File c : children) {
					backlog.offer(c);
				}
			}
			//TODO: does File contain the needed info? (buildNode()?)
			getOutput().offer(item);
		}
	}
}
