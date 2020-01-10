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
package net.sourceforge.fullsync.rules.filefilter;

import java.util.Arrays;
import java.util.Objects;

import net.sourceforge.fullsync.FSFile;

public class FileFilter {
	public static final int MATCH_ALL = 0;
	public static final int MATCH_ANY = 1;
	public static final int INCLUDE = 0;
	public static final int EXCLUDE = 1;
	private final int matchType;
	private final int filterType;
	private final boolean appliesToDir;
	private final FileFilterRule[] rules;

	public FileFilter(int matchType, int filterType, boolean appliesToDir, FileFilterRule... rules) {
		this.matchType = matchType;
		this.filterType = filterType;
		this.appliesToDir = appliesToDir;
		this.rules = rules;
	}

	public int getMatchType() {
		return matchType;
	}

	public int getFilterType() {
		return this.filterType;
	}

	public boolean appliesToDirectories() {
		return appliesToDir;
	}

	public FileFilterRule[] getFileFiltersRules() {
		return this.rules;
	}

	public boolean match(final FSFile file) {
		boolean result = doMmatch(file);
		return filterType == INCLUDE ? result : !result;
	}

	private boolean doMmatch(final FSFile file) {
		if (rules.length == 0) {
			return true;
		}

		switch (matchType) {
			case MATCH_ALL:
				return doMatchAll(file);
			case MATCH_ANY:
				return doMatchAny(file);
			default:
				return true;
		}
	}

	private boolean doMatchAll(final FSFile file) {
		for (FileFilterRule rule : rules) {
			if (!appliesToDir && file.isDirectory()) {
				continue;
			}
			try {
				boolean res = rule.match(file);
				if (!res) {
					return false;
				}
			}
			catch (FilterRuleNotAppliableException e) {
				// fine
			}
		}
		return true;
	}

	private boolean doMatchAny(final FSFile file) {
		int appliedRules = 0;

		for (FileFilterRule rule : rules) {
			if (!appliesToDir && file.isDirectory()) {
				continue;
			}
			try {
				boolean res = rule.match(file);
				if (res) {
					return true;
				}
				appliedRules++;
			}
			catch (FilterRuleNotAppliableException e) {
				// fine
			}
		}
		return 0 == appliedRules;
	}

	@Override
	public String toString() {
		if (rules.length == 0) {
			return "Empty filter";
		}
		StringBuilder buff = new StringBuilder(25 + (30 * rules.length));

		switch (filterType) {
			case INCLUDE:
				buff.append("Include");
				break;
			case EXCLUDE:
				buff.append("Exclude");
				break;
		}

		buff.append(" any file where ");

		for (int i = 0; i < (rules.length - 1); i++) {
			buff.append(rules[i].toString());
			switch (matchType) {
				case MATCH_ALL:
					buff.append(" and ");
					break;
				case MATCH_ANY:
					buff.append(" or ");
					break;
			}
		}

		if (rules.length > 0) {
			buff.append(rules[rules.length - 1].toString());
		}

		return buff.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		FileFilter that = (FileFilter) o;
		return (matchType == that.matchType)
			&& (filterType == that.filterType)
			&& (appliesToDir == that.appliesToDir)
			&& Arrays.equals(rules, that.rules);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(matchType, filterType, appliesToDir);
		result = (31 * result) + Arrays.hashCode(rules);
		return result;
	}
}
