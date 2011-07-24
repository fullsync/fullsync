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
/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.schedule;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class IntervalSchedule implements Schedule {
	private static final long serialVersionUID = 1;

	long firstInterval;
	long interval;
	long next;

	public IntervalSchedule(long firstInterval, long interval) {
		this.firstInterval = firstInterval;
		this.interval = interval;

		this.next = System.currentTimeMillis() + firstInterval;
	}

	public long getNextOccurrence(long now) {
		return next > now ? next : now;
	}

	public void setLastOccurrence(long now) {
		this.next = now + interval;
	}

	public long getFirstInterval() {
		return firstInterval;
	}

	public long getInterval() {
		return interval;
	}
}
