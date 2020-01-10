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

import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.filefiltertree.FileFilterTree;

public class SimplifiedSyncRules implements RuleSet {
	private boolean isUsingRecursion = true;
	private String patternsType;
	private String ignorePattern;
	private String takePattern;
	private FileFilter fileFilter;
	private FileFilterTree fileFilterTree;
	private boolean useFilter;

	@Override
	public boolean isUsingRecursion() {
		return isUsingRecursion;
	}

	public void setUsingRecursion(boolean usingRecursion) {
		this.isUsingRecursion = usingRecursion;
	}

	public void setPatternsType(String type) {
		this.patternsType = type;
	}

	public String getPatternsType() {
		return patternsType;
	}

	public void setIgnorePattern(String pattern) {
		this.ignorePattern = pattern;
	}

	public void setTakePattern(String pattern) {
		this.takePattern = pattern;
	}

	public String getIgnorePattern() {
		return ignorePattern;
	}

	public String getTakePattern() {
		return takePattern;
	}

	public FileFilter getFileFilter() {
		return fileFilter;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public void setUseFilter(boolean bool) {
		this.useFilter = bool;
	}

	public void setFileFilterTree(FileFilterTree fileFilterTree) {
		this.fileFilterTree = fileFilterTree;
	}

	public FileFilterTree getFileFilterTree() {
		return fileFilterTree;
	}

	@Override
	public boolean isNodeIgnored(final FSFile node) {
		if (useFilter) {
			FileFilter filterToUse = fileFilter;
			if (null != fileFilterTree) {
				FileFilter subFilter = fileFilterTree.getFilter(node.getPath());
				if (null != subFilter) {
					filterToUse = subFilter;
				}
			}
			boolean take = true;
			if (null != filterToUse) {
				take = filterToUse.match(node);
			}
			return !take;
		}
		return false;
	}

	@Override
	public State compareFiles(final FSFile src, final FSFile dst) {
		if (Math.floor(src.getLastModified() / 1000.0) > Math.floor(dst.getLastModified() / 1000.0)) {
			return State.FILE_CHANGE_SOURCE;
		}
		else if (Math.floor(src.getLastModified() / 1000.0) < Math.floor(dst.getLastModified() / 1000.0)) {
			return State.FILE_CHANGE_DESTINATION;
		}
		if (src.getSize() != dst.getSize()) {
			return State.FILE_CHANGE_UNKNOWN;
		}
		return State.IN_SYNC;
	}

	@Override
	public RuleSet createChild(final FSFile src, final FSFile dst) {
		// TODO even simple sync rules should allow override rules
		return this;
	}
}
