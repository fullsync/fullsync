/*
 * $HeadURL$
 * 
 * Copyright (c) 2010 MindTree Ltd. 
 * 
 * This file is part of Infix Maven Plugins
 * 
 * Infix Maven Plugins is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * Infix Maven Plugins is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * Infix Maven Plugins. If not, see <http://www.gnu.org/licenses/>.
 */
package com.mindtree.techworks.infix.pluginscommon.test.ssh;

import java.io.File;
import java.io.IOException;

import com.sshtools.daemon.configuration.PlatformConfiguration;
import com.sshtools.daemon.platform.NativeAuthenticationProvider;
import com.sshtools.daemon.platform.PasswordChangeException;
import com.sshtools.daemon.vfs.VFSMount;
import com.sshtools.j2ssh.configuration.ConfigurationLoader;


/**
 * @author Bindul Bhowmik
 * @version $Revision$ $Date$
 *
 */
public class JunitDummyAuthenticationProvider extends NativeAuthenticationProvider {
	@Override
	public String getHomeDirectory (String username) throws IOException {
		VFSMount vfsroot = ((PlatformConfiguration) ConfigurationLoader.getConfiguration(PlatformConfiguration.class)).getVFSRoot();
		String base = vfsroot.getPath();
		File homeDir = new File (base + "/home/" + username);
		return homeDir.getAbsolutePath().replace('\\', '/');
	}

	@Override
	public boolean changePassword(String arg0, String arg1, String arg2) {
		return false;
	}

	@Override
	public void logoffUser() throws IOException {
	}

	@Override
	public boolean logonUser(String arg0) throws IOException {
		return true;
	}

	@Override
	public boolean logonUser(String arg0, String arg1) throws PasswordChangeException, IOException {
		return true;
	}
}
