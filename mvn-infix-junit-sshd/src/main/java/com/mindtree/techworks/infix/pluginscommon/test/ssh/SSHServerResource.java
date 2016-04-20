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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXB;

import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

import com.mindtree.techworks.infix.pluginscommon.test.ssh.beans.PlatformConfiguration;
import com.mindtree.techworks.infix.pluginscommon.test.ssh.beans.ServerConfiguration;
import com.sshtools.common.configuration.XmlConfigurationContext;
import com.sshtools.daemon.SshServer;
import com.sshtools.daemon.configuration.XmlServerConfigurationContext;
import com.sshtools.daemon.forwarding.ForwardingServer;
import com.sshtools.daemon.session.SessionChannelFactory;
import com.sshtools.j2ssh.configuration.ConfigurationException;
import com.sshtools.j2ssh.configuration.ConfigurationLoader;
import com.sshtools.j2ssh.connection.ConnectionProtocol;


/**
 * @author Bindul Bhowmik
 * @version $Revision$ $Date$
 *
 */
public class SSHServerResource extends ExternalResource {

	private static XmlServerConfigurationContext serverPlatformConfiguration;

	private final String userId;
	private final int port;
	private final String bindAddress;

	private TemporaryFolder baseDir = new TemporaryFolder();
	private TemporaryFolder configDir = new TemporaryFolder();

	public SSHServerResource (String userId, int port, String bindAddress) {
		this.userId = userId;
		this.port = port;
		this.bindAddress = bindAddress;
	}

	/* (non-Javadoc)
	 * @see org.junit.rules.ExternalResource#before()
	 */
	@Override
	protected void before () throws Throwable {
		// Setup the temporary folder and copy configuration files
		baseDir.create();
		configDir.create();
		setupConfiguration();

		// Run it in a separate thread
		Executors.newSingleThreadExecutor().submit(new Callable<Object>() {

			@Override
			public Object call () throws Exception {
				start();
				return null;
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.junit.rules.ExternalResource#after()
	 */
	@Override
	protected void after () {

		try {
			stop();
		} catch (Throwable e) { /* Ignore */ }

		// Delete stuff
		try {
			configDir.delete();
		} catch (Throwable e) { /* Ignore */ }

		try {
			baseDir.delete();
		} catch (Throwable e) { /* Ignore */ }
	}

	public TemporaryFolder getRootDirectory () {
		return baseDir;
	}

	public File getUserHome () {
		return new File (baseDir.getRoot().getAbsolutePath().replace('\\', '/') + "/home/" + userId);
	}

	public String getUserId () {
		return userId;
	}

	public int getPort () {
		return port;
	}

	public String getBindAddress () {
		return bindAddress;
	}

	private void setupConfiguration () throws IOException {
		createPlatformConfig ();
		createServerConfig ();
		copyResources ();
		setupHomeDir ();

		configureServer ();
	}

	private void start () throws IOException {

		SshServer server = new SshServer() {

			@Override
			public void configureServices (ConnectionProtocol connection) throws IOException {
				connection.addChannelFactory(SessionChannelFactory.SESSION_CHANNEL,
					new SessionChannelFactory());

				if (ConfigurationLoader.isConfigurationAvailable(ServerConfiguration.class)) {
					com.sshtools.daemon.configuration.ServerConfiguration cfg = (com.sshtools.daemon.configuration.ServerConfiguration) ConfigurationLoader.getConfiguration(com.sshtools.daemon.configuration.ServerConfiguration.class);
					if (cfg.getAllowTcpForwarding()) {
//						ForwardingServer forwarding =
						new ForwardingServer(connection);
					}
				}
			}

			@Override
			public void shutdown (String msg) {
				// Disconnect all sessions
			}
		};

		server.startServer();
	}

	private void stop () throws ConfigurationException, UnknownHostException, IOException {
		com.sshtools.daemon.configuration.ServerConfiguration cfg = (com.sshtools.daemon.configuration.ServerConfiguration) ConfigurationLoader.getConfiguration(com.sshtools.daemon.configuration.ServerConfiguration.class);
		Socket socket = new Socket(InetAddress.getLocalHost(), cfg.getCommandPort());

		// Write the command id
		socket.getOutputStream().write(0x3a);

		// Write the length of the message (max 255)
		String msg = "bye";
		int len = (msg.length() <= 255) ? msg.length() : 255;
		socket.getOutputStream().write(len);

		// Write the message
		if (len > 0) {
			socket.getOutputStream().write(msg.substring(0, len).getBytes());
		}

		socket.close();
	}

	private void configureServer () throws ConfigurationException {

		String configBase = configDir.getRoot().getAbsolutePath().replace('\\', '/') + '/';

		// We store serverPlatformConfiguration as a static variable so we can
		// reinitialize it. This is required if the server is started and stopped
		// from multiple test executions - the design of J2SSH daemon does not
		// allow re-configuration (configs stored in static variables).
		if (null == serverPlatformConfiguration) {
			serverPlatformConfiguration = new XmlServerConfigurationContext();
			serverPlatformConfiguration.setServerConfigurationResource(ConfigurationLoader
				.checkAndGetProperty("sshtools.server", configBase + "server.xml"));
			serverPlatformConfiguration.setPlatformConfigurationResource(System.getProperty(
				"sshtools.platform", configBase + "platform.xml"));
			ConfigurationLoader.initialize(false, serverPlatformConfiguration);
		} else {
			serverPlatformConfiguration.setServerConfigurationResource(ConfigurationLoader
				.checkAndGetProperty("sshtools.server", configBase + "server.xml"));
			serverPlatformConfiguration.setPlatformConfigurationResource(System.getProperty(
				"sshtools.platform", configBase + "platform.xml"));
			serverPlatformConfiguration.initialize();
		}

		XmlConfigurationContext context2 = new XmlConfigurationContext();
		context2.setFailOnError(false);
		context2.setAPIConfigurationResource(ConfigurationLoader.checkAndGetProperty(
			"sshtools.config", configBase + "sshtools.xml"));
		context2.setAutomationConfigurationResource(ConfigurationLoader.checkAndGetProperty(
			"sshtools.automate", configBase + "automation.xml"));
		ConfigurationLoader.initialize(false, context2);
	}

	private void setupHomeDir () throws IOException {
		File homeBase = baseDir.newFolder("home");
		File userHome = new File(homeBase, userId);
		userHome.mkdirs();
	}

	private void copyResources () throws IOException {
		byte[] buffer = new byte[0x1000];
		int len = 0;
		for ( String file : Arrays.asList("sshtools.xml", "automation.xml", "test-dsa.key")) {
			URL fileUrl = ClassLoader.getSystemResource("sshd-config/" + file);
			File dst = new File (configDir.getRoot(), file);
			File src = new File (fileUrl.getPath());
			InputStream istr = new FileInputStream(src);
			OutputStream ostr = new FileOutputStream(dst);
			while (-1 != (len = istr.read(buffer))) {
				ostr.write(buffer, 0, len);
			}
			istr.close();
			ostr.flush();
			ostr.close();
		}
	}

	private void createPlatformConfig () throws IOException {
		PlatformConfiguration platformConfig = new PlatformConfiguration();
		platformConfig.setNativeProcessProvider(NativeProcessProvider.class.getName());
//		platformConfig.setNativeAuthenticationProvider("com.sshtools.daemon.platform.DummyAuthenticationProvider");
		platformConfig.setNativeAuthenticationProvider(com.mindtree.techworks.infix.pluginscommon.test.ssh.JunitDummyAuthenticationProvider.class.getName());
		platformConfig.setNativeFileSystemProvider(com.sshtools.daemon.vfs.VirtualFileSystem.class.getName());
		PlatformConfiguration.VFSRoot root = new PlatformConfiguration.VFSRoot();
		root.setPath(baseDir.getRoot().getAbsolutePath().replace('\\', '/'));
		platformConfig.setVfsRoot(root);

		marshall(platformConfig, "platform.xml");
	}

	private void createServerConfig () throws IOException {
		ServerConfiguration serverConfig = new ServerConfiguration();

		ServerConfiguration.ServerHostKey hostKey = new ServerConfiguration.ServerHostKey();
		hostKey.setPrivateKeyFile(configDir.getRoot().getAbsolutePath().replace('\\', '/') + "/test-dsa.key");
		serverConfig.addServerHostKey(hostKey);

		serverConfig.setPort(port);
		serverConfig.setListenAddress(bindAddress);
		serverConfig.setMaxConnections(3);
		serverConfig.addAllowedAuthentication("password");
		serverConfig.addAllowedAuthentication("keyboard-interactive");

		ServerConfiguration.Subsystem subsystem = new ServerConfiguration.Subsystem();
		subsystem.setName("sftp");
		subsystem.setType("class");
		subsystem.setProvider(com.sshtools.daemon.sftp.SftpSubsystemServer.class.getName());
		serverConfig.addSubsystem(subsystem);

		marshall(serverConfig, "server.xml");
	}

	private void marshall (Object source, String fileName) throws IOException {
		File outputFile = configDir.newFile(fileName);
		JAXB.marshal(source, outputFile);
	}
}
