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

/**
 * this Class holds default information about UI elements.
 */
public abstract class UISettings {
	private UISettings() {
	}

	/**
	 * preferred button width.
	 */
	public static final int BUTTON_WIDTH = 100;
	/**
	 * preferred button height.
	 */
	public static final int BUTTON_HEIGHT = 25;
	private static final int K = 1024;
	private static final String[] UNITS = {
		"B", //$NON-NLS-1$
		"KiB", //$NON-NLS-1$
		"MiB", //$NON-NLS-1$
		"GiB", //$NON-NLS-1$
		"TiB" //$NON-NLS-1$
	};

	public static String formatSize(long size) { // NO_UCD (use default)
		var i = 1;
		for (; i < UNITS.length; ++i) {
			if (size < Math.pow(K, i)) {
				break;
			}
		}
		--i;
		var result = ""; //$NON-NLS-1$
		if (-1 != size) {
			var scaledSize = (int) Math.ceil(size / Math.pow(K, i));
			result = String.format("%d %s", scaledSize, UNITS[i]); //$NON-NLS-1$
		}
		return result;
	}
}
