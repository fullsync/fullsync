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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FullSyncImageDataProvider implements ImageDataProvider {
	private static final Logger logger = LoggerFactory.getLogger(FullSyncImageDataProvider.class.getSimpleName());
	private String name;

	public FullSyncImageDataProvider(String imageName) {
		name = imageName;
	}

	@Override
	public ImageData getImageData(int zoom) {
		ImageData data = null;
		try (InputStream is = getImageStream(zoom)) {
			if (null != is) {
				data = new ImageData(is);
			}
		}
		catch (IOException e) {
			logger.debug("Failed to close image stream", e); //$NON-NLS-1$
		}
		return data;
	}

	private InputStream getImageStream(int zoom) {
		return getClass().getClassLoader().getResourceAsStream("net/sourceforge/fullsync/images/zoom" + zoom + "/" + name); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
