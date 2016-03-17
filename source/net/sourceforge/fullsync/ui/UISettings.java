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

	/**
	 * preferred button width.
	 */
	public static final int BUTTON_WIDTH = 100;

	/**
	 * preferred button height.
	 */
	public static final int BUTTON_HEIGHT = 25;

	public static String formatSize(long size) {
		if (size == -1) {
			return "";
		}
		if (size > (1024 * 1024 * 1024)) {
			return ((long)(Math.ceil(size / (1024 * 1024 * 1024)))) + " GiB";
		}
		if (size > (1024 * 1024)) {
			return ((long)(Math.ceil(size / (1024 * 1024)))) + " MiB";
		}
		if (size > 1024) {
			return ((long)(Math.ceil(size / 1024))) + " KiB";
		}
		return size + " B";
	}
}
