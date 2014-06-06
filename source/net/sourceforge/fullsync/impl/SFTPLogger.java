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
package net.sourceforge.fullsync.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SFTPLogger class implementing the com.jcraft.jsch.Logger interface
 * that enables the logging of the SSH library below the
 * org.apache.commons.vfs2 SFTP implementation.
 */
public class SFTPLogger implements com.jcraft.jsch.Logger {
	/**
	 * reference to the FullSync logger.
	 */
	private Logger logger;

	/**
	 * Constructor of the SFTPLogger implementation.
	 */
	public SFTPLogger() {
		logger = LoggerFactory.getLogger("SFTP");
	}

	@Override
	public final boolean isEnabled(final int level) {
		return true;
	}

	@Override
	public final void log(final int level, final String message) {
		switch (level) {
			case DEBUG:
				logger.debug(message);
				break;
			case INFO:
				logger.info(message);
				break;
			case WARN:
				logger.warn(message);
				break;
			case ERROR:
				logger.error(message);
				break;
			case FATAL:
				logger.error(message);
				break;
			default:
				logger.debug(message);
				break;
		}
	}

}
