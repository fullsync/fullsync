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
package net.sourceforge.fullsync.buffer;

public class Entry {
	private int start;
	private int length;
	private int internalSegment;
	private EntryDescriptor descriptor;

	public Entry(int start, int length) {
		this.start = start;
		this.length = length;
		this.internalSegment = Segment.ONLY;
		this.descriptor = null;
	}

	public boolean isLastSegment() {
		return (internalSegment & Segment.LAST) > 0;
	}

	@Override
	public String toString() {
		return String.format("%10d-%10d: %s", start, (start + length) - 1, descriptor.toString()); //$NON-NLS-1$
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getInternalSegment() {
		return internalSegment;
	}

	public void setInternalSegment(int internalSegment) {
		this.internalSegment = internalSegment;
	}

	public EntryDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(EntryDescriptor descriptor) {
		this.descriptor = descriptor;
	}
}
