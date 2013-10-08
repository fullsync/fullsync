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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.fullsync.BackgroundTask;
import net.sourceforge.fullsync.BackgroundTaskState;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.util.DebouncedHysteresisEmitter;
import net.sourceforge.fullsync.util.HysteresisReceiver;

public class ProfileSyncTask implements BackgroundTask, HysteresisReceiver {
	private BackgroundTaskState state;
	private final Profile profile;
	private final AtomicInteger workingTasks;
	private final ArrayList<SyncTasklet<? extends Object, ? extends Object>> subTasks;
	private final DebouncedHysteresisEmitter debouncer;

	public ProfileSyncTask(final Profile p, final boolean interactive) {
		state = BackgroundTaskState.Initializing;
		profile = p;
		workingTasks = new AtomicInteger();
		subTasks = new ArrayList<SyncTasklet<? extends Object, ? extends Object>>(5);
		debouncer = new DebouncedHysteresisEmitter(this, 0, 300);
		restart();
	}

	@Override
	public BackgroundTaskState getState() {
		return state;
	}

	@Override
	public synchronized void pause() {
		for (SyncTasklet<? extends Object, ? extends Object> task : subTasks) {
			task.pause();
		}
	}

	@Override
	public synchronized void cancel() {
		for (SyncTasklet<? extends Object, ? extends Object> task : subTasks) {
			task.cancel();
		}
		debouncer.cancel();
	}

	@Override
	public synchronized void resume() {
		for (SyncTasklet<? extends Object, ? extends Object> task : subTasks) {
			task.resume();
		}
	}

	@Override
	public synchronized void restart() {
		cancel();
		state = BackgroundTaskState.Running;
		subTasks.clear();
		ListFilesystemTasklet src = new ListFilesystemTasklet(this, profile.getSource());
		ListFilesystemTasklet dst = new ListFilesystemTasklet(this, profile.getDestination());
		subTasks.add(src);
		subTasks.add(dst);
		//TODO: decide if src and target use the same location and thus should avoid multiple connections
		DebugPrintQueue<File> srcDebugPrinter = new DebugPrintQueue<File>(this, src.getOutput(), "SRC");
		DebugPrintQueue<File> dstDebugPrinter = new DebugPrintQueue<File>(this, dst.getOutput(), "DST");
		subTasks.add(srcDebugPrinter);
		subTasks.add(dstDebugPrinter);
		for (SyncTasklet<? extends Object, ? extends Object> task : subTasks) {
			FullSync.submit(task);
		}
	}

	@Override
	public void showUI() {
		// TODO Auto-generated method stub

	}

	void startWork() {
		int working = workingTasks.incrementAndGet();
		if (1 == working) {
			debouncer.up();
		}
	}

	void endWork() {
		int working = workingTasks.decrementAndGet();
		if (0 == working) {
			debouncer.down();
		}
	}

	@Override
	public synchronized void up() {
		FullSync.publish(this); // FIXME: publish BackgroundTaskWorking() or something
	}

	@Override
	public synchronized void down() {
		FullSync.publish(this); // FIXME: publish BackgroundTaskIdle() or something
	}

}
