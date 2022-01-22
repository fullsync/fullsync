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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

@Singleton
public class ImageRepository {
	private final Display display;
	private final Map<String, Image> cache = new HashMap<>();

	@Inject
	public ImageRepository(Display display) {
		this.display = display;
		display.addListener(SWT.Dispose, this::dispose);
	}

	public Image getImage(String imageName) {
		Image img = null;
		if (null != imageName) {
			img = cache.computeIfAbsent(imageName, n -> new Image(display, new FullSyncImageDataProvider(n)));
		}
		return img;
	}

	private void dispose(Event e) {
		for (Image i : cache.values()) {
			if (!i.isDisposed()) {
				i.dispose();
			}
		}
		cache.clear();
	}
}
