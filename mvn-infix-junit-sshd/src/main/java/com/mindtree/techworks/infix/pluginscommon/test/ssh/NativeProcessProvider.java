package com.mindtree.techworks.infix.pluginscommon.test.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class NativeProcessProvider extends com.sshtools.daemon.platform.NativeProcessProvider {
	@Override
	public boolean allocatePseudoTerminal(String arg0, int arg1, int arg2, int arg3, int arg4, String arg5) {
		return false;
	}

	@Override
	public boolean createProcess(String arg0, Map arg1) throws IOException {
		return false;
	}

	@Override
	public String getDefaultTerminalProvider() {
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public InputStream getStderrInputStream() throws IOException {
		return null;
	}

	@Override
	public void kill() {
	}

	@Override
	public void start() throws IOException {
	}

	@Override
	public boolean stillActive() {
		return false;
	}

	@Override
	public boolean supportsPseudoTerminal(String arg0) {
		return false;
	}

	@Override
	public int waitForExitCode() {
		return -1;
	}
}
