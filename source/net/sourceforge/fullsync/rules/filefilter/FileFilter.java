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
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.Serializable;

import net.sourceforge.fullsync.fs.File;

/**
 * @author Michele Aiello
 */
public class FileFilter implements Serializable {

	private static final long serialVersionUID = 2L;
	public static final int MATCH_ALL = 0;
	public static final int MATCH_ANY = 1;

	public static final int INCLUDE = 0;
	public static final int EXCLUDE = 1;

	private int match_type;
	private int filter_type;

	private boolean appliesToDir;

	private FileFilterRule[] rules;

	public FileFilter() {
		match_type = 0;
		filter_type = 0;
		appliesToDir = true;
		rules = new FileFilterRule[0];
	}

	public void setMatchType(int match_type) {
		this.match_type = match_type;
	}

	public int getMatchType() {
		return match_type;
	}

	public void setFilterType(int filter_type) {
		this.filter_type = filter_type;
	}

	public int getFilterType() {
		return this.filter_type;
	}

	public void setFileFilterRules(FileFilterRule[] rules) {
		this.rules = rules;
	}

	public void setAppliesToDirectories(boolean appliesToDir) {
		this.appliesToDir = appliesToDir;
	}

	public boolean appliesToDirectories() {
		return appliesToDir;
	}

	public FileFilterRule[] getFileFiltersRules() {
		return this.rules;
	}

	public boolean match(final File file) {
		boolean result = doMmatch(file);
		return (filter_type == INCLUDE) ? result : !result;
	}

	private boolean doMmatch(final File file) {
		if (rules.length == 0) {
			return true;
		}

		switch (match_type) {
			case MATCH_ALL:
				for (FileFilterRule rule : rules) {
					if ((!appliesToDir) && (file.isDirectory())) {
						continue;
					}
					try {
						boolean res = rule.match(file);
						if (!res) {
							return false;
						}
					}
					catch (FilterRuleNotAppliableException e) {
					}
				}
				return true;
			case MATCH_ANY: {
				int applyedRules = 0;

				for (FileFilterRule rule : rules) {
					if ((!appliesToDir) && (file.isDirectory())) {
						continue;
					}
					try {
						boolean res = rule.match(file);
						if (res) {
							return true;
						}
						applyedRules++;
					}
					catch (FilterRuleNotAppliableException e) {
					}
				}
				return (applyedRules == 0);
			}
			default:
				return true;
		}
	}

	@Override
	public String toString() {
		if (rules.length == 0) {
			return "Empty filter";
		}
		StringBuffer buff = new StringBuffer(25 + (30 * rules.length));

		switch (filter_type) {
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
			switch (match_type) {
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

}
