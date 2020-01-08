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

import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.buffering.BufferingProvider;
import net.sourceforge.fullsync.fs.buffering.syncfiles.SyncFilesBufferingProvider;
import net.sourceforge.fullsync.fs.connection.FileSystemConnection;

public class FileSystemManagerImpl implements FileSystemManager {
	private final Injector injector;

	@Inject
	public FileSystemManagerImpl(Injector injector) {
		this.injector = injector;
	}

	private FileSystem getFilesystem(final String scheme) throws FileSystemException {
		try {
			return injector.getInstance(Key.get(FileSystem.class, Names.named(scheme)));
		}
		catch (ConfigurationException configurationException) {
			throw new FileSystemException("Unknown scheme: " + scheme, configurationException); //$NON-NLS-1$
		}
	}

	@Override
	public final FileSystemConnection createConnection(final ConnectionDescription desc, boolean interactive)
		throws FileSystemException, IOException {
		String scheme = desc.getScheme();

		FileSystem fs = getFilesystem(scheme);

		FileSystemConnection s = null;
		if (null != fs) {
			s = fs.createConnection(desc, interactive);
			/* FIXME: [BUFFERING] uncomment to reenable buffering
			String bufferStrategy = desc.getParameter(ConnectionDescription.PARAMETER_BUFFER_STRATEGY);
			if ((null != bufferStrategy) && !"".equals(bufferStrategy)) {
				s = resolveBuffering(s, bufferStrategy);
			}
			 */
		}
		return s;
	}

	public FileSystemConnection resolveBuffering(final FileSystemConnection dir, final String bufferStrategy)
		throws FileSystemException, IOException {
		BufferingProvider p = null;
		if (BUFFER_STRATEGY_SYNCFILES.equals(bufferStrategy)) {
			p = new SyncFilesBufferingProvider();
		}

		if (null == p) {
			throw new FileSystemException("BufferStrategy '" + bufferStrategy + "' not found"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return p.createBufferedSite(dir);
	}
}
