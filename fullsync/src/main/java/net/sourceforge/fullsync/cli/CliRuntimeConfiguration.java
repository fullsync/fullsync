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
package net.sourceforge.fullsync.cli;

import java.net.InetSocketAddress;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;

import net.sourceforge.fullsync.RuntimeConfiguration;

public class CliRuntimeConfiguration implements RuntimeConfiguration {
	private final Optional<String> profileToRun;
	private final Optional<String> remotePassword;
	private final Optional<InetSocketAddress> listenSocketAddress;
	private final Optional<Boolean> daemon;
	private final Optional<Boolean> startMinimized;

	public CliRuntimeConfiguration(CommandLine args) {
		profileToRun = parseProfileToRun(args);
		remotePassword = parseRemotePassword(args);
		listenSocketAddress = parseListenSocketAddress(args);
		daemon = Optional.of(args.hasOption("d"));
		startMinimized = Optional.of(args.hasOption('m'));
	}

	private Optional<String> parseProfileToRun(CommandLine args) {
		if (args.hasOption("r")) {
			return Optional.of(args.getOptionValue("r"));
		}
		return Optional.empty();
	}

	private Optional<String> parseRemotePassword(CommandLine args) {
		if (args.hasOption("a")) {
			return Optional.of(args.getOptionValue("a", "admin"));
		}
		else if (args.hasOption("p")) {
			return Optional.of("admin");
		}
		return Optional.empty();
	}

	private Optional<InetSocketAddress> parseListenSocketAddress(CommandLine args) {
		if (args.hasOption("p")) {
			String portStr = args.getOptionValue("p", "10000");
			return Optional.of(new InetSocketAddress(Integer.parseInt(portStr)));
		}
		return Optional.empty();
	}

	@Override
	public Optional<String> getProfileToRun() {
		return profileToRun;
	}

	@Override
	public Optional<String> getRemotePassword() {
		return remotePassword;
	}

	@Override
	public Optional<InetSocketAddress> getListenSocketAddress() {
		return listenSocketAddress;
	}

	@Override
	public Optional<Boolean> isDaemon() {
		return daemon;
	}

	@Override
	public Optional<Boolean> isStartMinimized() {
		return startMinimized;
	}
}
