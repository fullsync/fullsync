package net.sourceforge.fullsync.pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.util.SmartQueue;

public class TransferTaskExecutor extends SyncTasklet<Task, TransferTaskResult> {
	private byte[] buffer;

	public TransferTaskExecutor(TaskletWorkNotificationTarget _workNotificationTarget, SmartQueue<Task> _inputQueue) {
		super(_workNotificationTarget, _inputQueue);
		buffer = new byte[8 * 1024];
	}

	@Override
	protected void processItem(Task item) throws Exception {
		Action action = item.getCurrentAction();
		File source = item.getSource();
		File destination = item.getDestination();

		switch (action.getType()) {
			case Action.Add:
			case Action.Update:
				if (action.getLocation() == Location.Destination) {
					if (source.isDirectory()) {
						destination.makeDirectory();
					}
					else {
						copyFile(source, destination);
					}
				}
				else if (action.getLocation() == Location.Source) {
					if (destination.isDirectory()) {
						source.makeDirectory();
					}
					else {
						copyFile(destination, source);
					}
				}
				break;
			case Action.Delete:
				if (action.getLocation() == Location.Destination) {
					destination.delete();
				}
				else if (action.getLocation() == Location.Source) {
					source.delete();
				}
				break;
			default:
				break;
		}
		getOutput().offer(new TransferTaskResult(item));
	}

	private void copyFile(File source, File destination) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = source.getInputStream();
			os = destination.getOutputStream();
			copyStream(is, os);
		}
		finally {
			try {
				if (null != is) {
					is.close();
				}
			}
			finally {
				if (null != os) {
					try {
						os.flush();
					}
					finally {
						os.close();
					}
				}
			}
		}
		destination.setLastModified(source.getLastModified());
		destination.writeFileAttributes();
	}

	private void copyStream(InputStream is, OutputStream os) throws IOException {
		int bytesRead = 0;
		while (-1 != bytesRead) {
			bytesRead = is.read(buffer);
			if (bytesRead > 0) {
				os.write(buffer, 0, bytesRead);
			}
		}
	}



	@Override
	protected void processingFailed(Task item, Throwable t) {
		TransferTaskResult result = new TransferTaskResult(item);
		result.setError(t);
		getOutput().offer(result);
	}

	@Override
	public void pause() {
		getInput().pause();
	}

	@Override
	public void resume() {
		getInput().resume();
	}

	@Override
	public void cancel() {
		getInput().cancel();
	}

}
