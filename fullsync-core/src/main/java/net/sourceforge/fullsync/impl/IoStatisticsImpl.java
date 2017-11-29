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
package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.IoStatistics;

public class IoStatisticsImpl implements IoStatistics {
	private int filesCopied;
	private int dirsCreated;
	private int deletions;
	private long bytesTransferred;

	@Override
	public int getFilesCopied() {
		return filesCopied;
	}

	@Override
	public int getDirsCreated() {
		return dirsCreated;
	}

	@Override
	public int getDeletions() {
		return deletions;
	}

	@Override
	public long getBytesTransferred() {
		return bytesTransferred;
	}

	@Override
	public int getCountActions() {
		return getFilesCopied() + getDirsCreated() + getDeletions();
	}

	public void setBytesTransferred(long bytesTransferred) {
		this.bytesTransferred = bytesTransferred;
	}

	public void setDeletions(int deletions) {
		this.deletions = deletions;
	}

	public void setDirsCreated(int dirsCreated) {
		this.dirsCreated = dirsCreated;
	}

	public void setFilesCopied(int filesCopied) {
		this.filesCopied = filesCopied;
	}
}
