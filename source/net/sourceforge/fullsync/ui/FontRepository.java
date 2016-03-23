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

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;

public class FontRepository {
	private static class Key {
		private String name;
		private int height;
		private int style;
		private int hash;
		Key(String _name, int _height, int _style) {
			name = _name;
			height = _height;
			style = _style;
			hash = (name + "#" + height + "#" + style).hashCode();
		}
		@Override
		public int hashCode() {
			return hash;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Key) {
				Key k = (Key)obj;
				return (height == k.height) && (style == k.style) && (0 == name.compareTo(k.name));
			}
			return false;
		}
	}

	private Device dev;
	private HashMap<FontRepository.Key, Font> cache;

	public FontRepository(Device _device) {
		dev = _device;
		cache = new HashMap<FontRepository.Key, Font>(5);
	}

	public Font getFont(String name, int height, int style) {
		Key key = new Key(name, height, style);
		Font font = cache.get(key);
		if (null == font) {
			font = new Font(dev, name, height, style);
		}
		return font;
	}

	public void dispose() {
		for (Font f : cache.values()) {
			f.dispose();
		}
		cache.clear();
	}
}
