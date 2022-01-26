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
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

@Singleton
public class FontRepository { // NO_UCD (use default)
	private static class Key {
		private final String name;
		private final int height;
		private final int style;
		private final int hash;

		Key(String _name, int _height, int _style) {
			name = _name;
			height = _height;
			style = _style;
			hash = (name + "#" + height + "#" + style).hashCode(); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Key k) {
				return (height == k.height) && (style == k.style) && (0 == name.compareTo(k.name));
			}
			return false;
		}
	}

	private final Device device;
	private final Map<FontRepository.Key, Font> cache = new HashMap<>(5);

	@Inject
	public FontRepository(Display display) {
		this.device = display;
		display.addListener(SWT.Dispose, this::dispose);
	}

	public Font getFont(String name, int height, int style) { // NO_UCD (use default)
		var key = new Key(name, height, style);
		return cache.computeIfAbsent(key, k -> new Font(device, k.name, k.height, k.style));
	}

	private void dispose(Event e) {
		for (Font f : cache.values()) {
			f.dispose();
		}
		cache.clear();
	}
}
