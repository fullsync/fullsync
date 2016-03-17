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
 * Created on Nov 4, 2004
 */
package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.filefiltertree.FileFilterTree;

public class SimplyfiedSyncRules implements RuleSet {

	private String name;

	private boolean isUsingRecursion = true;

	private String patternsType;

	private String ignorePattern;

	private String takePattern;

	private FileFilter fileFilter;
	private FileFilterTree fileFilterTree;

	private boolean useFilter;

	/**
	 * Default Constructor.
	 */
	public SimplyfiedSyncRules() {
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see net.sourceforge.fullsync.RuleSet#isUsingRecursion()
	 */
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

	/**
	 * @return Returns the ignorePattern.
	 */
	public String getIgnorePattern() {
		return ignorePattern;
	}

	/**
	 * @return Returns the takePattern.
	 */
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

	/**
	 * @see net.sourceforge.fullsync.RuleSet#isJustLogging()
	 */
	@Override
	public boolean isJustLogging() {
		return false;
	}

	/**
	 * @see net.sourceforge.fullsync.IgnoreDecider#isNodeIgnored(net.sourceforge.fullsync.fs.File)
	 */
	@Override
	public boolean isNodeIgnored(final File node) {
		if (useFilter) {
			FileFilter filterToUse = fileFilter;
			if (fileFilterTree != null) {
				FileFilter subFilter = fileFilterTree.getFilter(node.getPath());
				if (subFilter != null) {
					filterToUse = subFilter;
				}
			}
			boolean take = true;
			if (filterToUse != null) {
				take = filterToUse.match(node);
			}
			return !take;
		}
		return false;
	}

	/**
	 * @see net.sourceforge.fullsync.FileComparer#compareFiles(net.sourceforge.fullsync.fs.FileAttributes,
	 *      net.sourceforge.fullsync.fs.FileAttributes)
	 */
	@Override
	public State compareFiles(final File src, final File dst) throws DataParseException {
		if (Math.floor(src.getLastModified() / 1000.0) > Math.floor(dst.getLastModified() / 1000.0)) {
			return new State(State.FileChange, Location.Source);
		}
		else if (Math.floor(src.getLastModified() / 1000.0) < Math.floor(dst.getLastModified() / 1000.0)) {
			return new State(State.FileChange, Location.Destination);
		}
		if (src.getSize() != dst.getSize()) {
			return new State(State.FileChange, Location.None);
		}
		return new State(State.NodeInSync, Location.Both);
	}

	/**
	 * @see net.sourceforge.fullsync.RuleSet#createChild(net.sourceforge.fullsync.fs.File, net.sourceforge.fullsync.fs.File)
	 */
	@Override
	public RuleSet createChild(final File src, final File dst) {
		// TODO even simple sync rules should allow override rules
		return this;
	}
}
