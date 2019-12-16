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

public class SystemDate {
	private static SystemDate instance;
	private long baseTime = -1;
	private long timeOfBaseTime = -1;
	private int speed = 1;

	private SystemDate() {
	}

	public static SystemDate getInstance() {
		if (null == instance) {
			instance = new SystemDate();
		}
		return instance;
	}

	public void setCurrent(long millis) {
		this.baseTime = millis;
		this.timeOfBaseTime = System.currentTimeMillis();
	}

	public void setUseSystemTime() {
		this.baseTime = -1;
		this.timeOfBaseTime = -1;
		this.speed = 1;
	}

	public void setTimeSpeed(int speed) {
		this.speed = speed;
	}

	public long currentTimeMillis() {
		if (baseTime >= 0) {
			return baseTime + ((System.currentTimeMillis() - timeOfBaseTime) * speed);
		}
		else {
			return System.currentTimeMillis();
		}
	}
}
