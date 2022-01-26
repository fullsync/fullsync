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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Display;

import net.sourceforge.fullsync.ThrowingSupplier;

public class BackgroundExecutor {
	private final ScheduledExecutorService scheduledExecutorService;
	private final Display display;

	@Inject
	public BackgroundExecutor(ScheduledExecutorService scheduledExecutorService, Display display) {
		this.scheduledExecutorService = scheduledExecutorService;
		this.display = display;
	}

	public <R> void runAsync(ThrowingSupplier<R> supplier, Consumer<R> success, Consumer<Exception> error) { // NO_UCD (use default)
		CompletableFuture<R> future = CompletableFuture.supplyAsync(unchecked(supplier), scheduledExecutorService);
		future.thenRun(new UIUpdateTask<>(display, future, success, error));
	}

	// from http://4comprehension.com/sneakily-throwing-exceptions-in-lambda-expressions-in-java/
	static <R> Supplier<R> unchecked(ThrowingSupplier<R> f) { // NO_UCD (use private)
		return () -> {
			try {
				return f.get();
			}
			catch (Exception ex) {
				return sneakyThrow(ex);
			}
		};
	}

	// from http://4comprehension.com/sneakily-throwing-exceptions-in-lambda-expressions-in-java/
	@SuppressWarnings("unchecked")
	static <T extends Exception, R> R sneakyThrow(Exception t) throws T { // NO_UCD (use private)
		throw (T) t; // ( ͡° ͜ʖ ͡°)
	}

	private static class UIUpdateTask<T> implements Runnable {
		private final Display display;
		private final CompletableFuture<T> future;
		private final Consumer<T> success;
		private final Consumer<Exception> error;

		public UIUpdateTask(Display display, CompletableFuture<T> future, Consumer<T> success, Consumer<Exception> error) {
			this.display = display;
			this.future = future;
			this.success = success;
			this.error = error;
		}

		@Override
		public void run() {
			display.asyncExec(this::notifyOnUiThread);
		}

		private void notifyOnUiThread() {
			try {
				success.accept(future.get());
			}
			catch (Exception ex) {
				error.accept(ex);
			}
		}
	}
}
