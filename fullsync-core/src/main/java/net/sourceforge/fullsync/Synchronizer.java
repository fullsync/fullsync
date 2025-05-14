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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fullsync.buffer.BlockBuffer;
import net.sourceforge.fullsync.impl.FillBufferTaskExecutor;

@Singleton
public record Synchronizer(TaskGenerator taskGenerator) {
	private static final Logger logger = LoggerFactory.getLogger(Synchronizer.class);

	@Inject
	public Synchronizer {}

	public synchronized TaskTree executeProfile(Profile profile, boolean interactive) {
		try {
			return taskGenerator.execute(profile, interactive);
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
		return null;
	}

	public int performActions(TaskTree taskTree) {
		return performActions(taskTree, null);
	}

	public int performActions(TaskTree taskTree, TaskFinishedListener listener) {
		try {
			logger.info("Synchronization started");
			logger.info("  source:      " + taskTree.source().getConnectionDescription().getDisplayPath());
			logger.info("  destination: " + taskTree.destination().getConnectionDescription().getDisplayPath());
			TaskExecutor queue = new FillBufferTaskExecutor(new BlockBuffer(logger));
			if (null != listener) {
				queue.addTaskFinishedListener(listener);
			}
			queue.enqueue(taskTree);
			queue.flush();
			taskTree.source().flush();
			taskTree.destination().flush();
			taskTree.source().close();
			taskTree.destination().close();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
			logger.error("An Exception occured while performing actions", e);
			logger.info("synchronization failed");
			logger.info("------------------------------------------------------------");
			return 1;
		}
		logger.info("synchronization successful"); // TODO ...with x errors and y warnings
		logger.info("------------------------------------------------------------");
		return 0;
	}
}
