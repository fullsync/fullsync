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

import java.io.IOException;

import javax.inject.Inject;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import net.sourceforge.fullsync.fs.FileSystemConnectionFactory;
import net.sourceforge.fullsync.fs.buffering.BufferingProviderFactory;
import net.sourceforge.fullsync.fs.connection.FileSystemConnection;

public class FileSystemManagerImpl implements FileSystemManager {
	private final Injector injector;

	@Inject
	public FileSystemManagerImpl(Injector injector) {
		this.injector = injector;
	}

	private FileSystemConnectionFactory getFilesystem(final String scheme) throws FileSystemException {
		try {
			Key<FileSystemConnectionFactory> key = Key.get(FileSystemConnectionFactory.class, Names.named(scheme));
			return injector.getInstance(key);
		}
		catch (ConfigurationException configurationException) {
			throw new FileSystemException("Unknown scheme: " + scheme, configurationException); //$NON-NLS-1$
		}
	}

	@Override
	public final FileSystemConnection createConnection(final ConnectionDescription connectionDescription, boolean interactive)
		throws FileSystemException, IOException {
		String scheme = connectionDescription.getScheme();
		FileSystemConnectionFactory fs = getFilesystem(scheme);
		FileSystemConnection fileSystemConnection = null;
		if (null != fs) {
			fileSystemConnection = fs.createConnection(connectionDescription, interactive);
			/* FIXME: [BUFFERING] uncomment to reenable buffering
			if (connectionDescription.getBufferStrategy().isPresent()) {
				fileSystemConnection = resolveBuffering(fileSystemConnection, connectionDescription.getBufferStrategy().get());
			}
			 */
		}
		return fileSystemConnection;
	}

	public FileSystemConnection resolveBuffering(final FileSystemConnection fileSystemConnection, final String bufferStrategy)
		throws FileSystemException, IOException {
		try {
			Key<BufferingProviderFactory> key = Key.get(BufferingProviderFactory.class, Names.named(bufferStrategy));
			BufferingProviderFactory bufferingProviderFactory = injector.getInstance(key);
			return bufferingProviderFactory.createBufferedConnection(fileSystemConnection);
		}
		catch (ConfigurationException configurationException) {
			throw new FileSystemException("BufferStrategy '" + bufferStrategy + "' not found", configurationException); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
