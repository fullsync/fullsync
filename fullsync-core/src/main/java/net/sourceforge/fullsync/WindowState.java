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

public class WindowState {
	private int x;
	private int y;
	private int width;
	private int height;
	private boolean maximized;
	private boolean minimized;

	private boolean isPointOutside(int _x, int _y) {
		return (_x <= x) || (_y <= y) || (_x >= (x + width)) || (_y >= (y + height));
	}

	public boolean isInsideOf(int _x, int _y, int _width, int _height) {
		return isPointOutside(_x, _y) && isPointOutside(_x + _width, _y + _height);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append('{');
		if (!maximized) {
			sb.append(String.format("%d, %d, %d, %d", x, y, width, height));
		}
		else {
			sb.append("maximized");
		}
		if (minimized) {
			sb.append(", minimized");
		}
		sb.append('}');
		return sb.toString();
	}

	public boolean isValid() {
		return (width > 0) && (height > 0);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isMaximized() {
		return maximized;
	}

	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}

	public boolean isMinimized() {
		return minimized;
	}

	public void setMinimized(boolean minimized) {
		this.minimized = minimized;
	}
}
