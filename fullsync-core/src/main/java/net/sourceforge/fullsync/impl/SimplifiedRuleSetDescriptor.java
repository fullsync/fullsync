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

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.RuleSetDescriptor;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.FileFilterManager;
import net.sourceforge.fullsync.rules.filefilter.FileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FileNameFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.filefiltertree.FileFilterTree;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

public class SimplifiedRuleSetDescriptor extends RuleSetDescriptor {
	public static final String RULESET_TYPE = "simple"; //$NON-NLS-1$
	private static final String ELEMENT_SIMPLE_RULE_SET = "SimpleRuleSet"; //$NON-NLS-1$
	private static final String ELEMENT_FILE_FILTER_RULE = "FileFilterRule"; //$NON-NLS-1$
	private static final String ELEMENT_FILE_FILTER = "FileFilter"; //$NON-NLS-1$
	private static final String ELEMENT_SUBDIRECTORY_FILE_FILTER = "SubdirectoryFileFilter"; //$NON-NLS-1$
	private static final String ATTRIBUTE_PATH = "path"; //$NON-NLS-1$
	private static final String ATTRIBUTE_USE_FILTER = "useFilter"; //$NON-NLS-1$
	private static final String ATTRIBUTE_TAKE_PATTERN = "takePattern"; //$NON-NLS-1$
	private static final String ATTRIBUTE_IGNORE_PATTERN = "ignorePattern"; //$NON-NLS-1$
	private static final String ATTRIBUTE_PATTERNS_TYPE = "patternsType"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SYNC_SUBS = "syncSubs"; //$NON-NLS-1$
	private static final String PATTERN_TYPE_REG_EXP = "RegExp"; //$NON-NLS-1$
	private final boolean syncSubDirs;
	private final String ignorePattern;
	private final String takePattern;
	private final String patternsType;
	private final FileFilter fileFilter;
	private final boolean useFilter;
	private final FileFilterTree fileFilterTree;

	public SimplifiedRuleSetDescriptor(boolean syncSubDirs, FileFilter fileFilter, boolean useFilter, FileFilterTree fileFilterTree) {
		this.syncSubDirs = syncSubDirs;
		this.ignorePattern = ""; //$NON-NLS-1$
		this.takePattern = ""; //$NON-NLS-1$
		this.patternsType = ""; //$NON-NLS-1$
		this.fileFilter = fileFilter;
		this.useFilter = useFilter;
		this.fileFilterTree = fileFilterTree;
	}

	@Override
	public String getType() {
		return RULESET_TYPE;
	}

	@Override
	public Element serializeDescriptor(Document document) {
		var simpleRuleSetElement = document.createElement(ELEMENT_SIMPLE_RULE_SET);
		simpleRuleSetElement.setAttribute(ATTRIBUTE_SYNC_SUBS, String.valueOf(isSyncSubDirs()));
		simpleRuleSetElement.setAttribute(ATTRIBUTE_PATTERNS_TYPE, getPatternsType());
		simpleRuleSetElement.setAttribute(ATTRIBUTE_IGNORE_PATTERN, getIgnorePattern());
		simpleRuleSetElement.setAttribute(ATTRIBUTE_TAKE_PATTERN, getTakePattern());
		simpleRuleSetElement.setAttribute(ATTRIBUTE_USE_FILTER, String.valueOf(isUseFilter()));

		var fm = new FileFilterManager();
		if (null != fileFilter) {
			var fileFilterElement = fm.serializeFileFilter(getFileFilter(), document, ELEMENT_FILE_FILTER, ELEMENT_FILE_FILTER_RULE);
			simpleRuleSetElement.appendChild(fileFilterElement);
		}

		if (null != fileFilterTree) {
			fileFilterTree.stream().map(e -> {
				var subdirFilterElement = document.createElement(ELEMENT_SUBDIRECTORY_FILE_FILTER);
				subdirFilterElement.setAttribute(ATTRIBUTE_PATH, e.getKey());
				var fileFilterElement = fm.serializeFileFilter(e.getValue(), document, ELEMENT_FILE_FILTER, ELEMENT_FILE_FILTER_RULE);
				subdirFilterElement.appendChild(fileFilterElement);
				return subdirFilterElement;
			}).forEachOrdered(simpleRuleSetElement::appendChild);
		}
		return simpleRuleSetElement;
	}

	public SimplifiedRuleSetDescriptor(Element element) throws DataParseException {
		var ruleSetConfigNodeList = element.getElementsByTagName(ELEMENT_SIMPLE_RULE_SET);

		if (ruleSetConfigNodeList.getLength() == 0) {
			syncSubDirs = true;
			useFilter = false;
			ignorePattern = ""; //$NON-NLS-1$
			takePattern = ""; //$NON-NLS-1$
			patternsType = PATTERN_TYPE_REG_EXP;
			fileFilter = null;
			fileFilterTree = null;
		}
		else {
			var simpleRuleSetConfigElement = (Element) ruleSetConfigNodeList.item(0);
			syncSubDirs = Boolean.parseBoolean(simpleRuleSetConfigElement.getAttribute(ATTRIBUTE_SYNC_SUBS));
			patternsType = simpleRuleSetConfigElement.getAttribute(ATTRIBUTE_PATTERNS_TYPE);
			ignorePattern = simpleRuleSetConfigElement.getAttribute(ATTRIBUTE_IGNORE_PATTERN);
			takePattern = simpleRuleSetConfigElement.getAttribute(ATTRIBUTE_TAKE_PATTERN);
			var useFilterAttr = Boolean.parseBoolean(simpleRuleSetConfigElement.getAttribute(ATTRIBUTE_USE_FILTER));
			var fileFilterNodeList = simpleRuleSetConfigElement.getElementsByTagName(ELEMENT_FILE_FILTER);
			if (fileFilterNodeList.getLength() > 0) {
				useFilter = useFilterAttr;
				var filterManager = new FileFilterManager();
				var fileFilterElement = (Element) fileFilterNodeList.item(0);
				fileFilter = filterManager.unserializeFileFilter(fileFilterElement, ELEMENT_FILE_FILTER_RULE);

				var subdirFiltersNodeList = simpleRuleSetConfigElement.getElementsByTagName(ELEMENT_SUBDIRECTORY_FILE_FILTER);
				var numOfDirs = subdirFiltersNodeList.getLength();
				Map<String, FileFilter> filters = new TreeMap<>();
				for (var i = 0; i < numOfDirs; i++) {
					var subDirElement = (Element) subdirFiltersNodeList.item(i);
					var path = subDirElement.getAttribute(ATTRIBUTE_PATH);
					fileFilterNodeList = subDirElement.getElementsByTagName(ELEMENT_FILE_FILTER);
					if (fileFilterNodeList.getLength() > 0) {
						var subDirFileFilterElement = (Element) fileFilterNodeList.item(0);
						var subDirFileFilter = filterManager.unserializeFileFilter(subDirFileFilterElement,
							ELEMENT_FILE_FILTER_RULE);
						filters.put(path, subDirFileFilter);
					}
				}
				fileFilterTree = new FileFilterTree(filters);
			}
			else {
				fileFilterTree = null;
				var isRegExpFilter = PATTERN_TYPE_REG_EXP.equals(patternsType);
				useFilter = ignorePattern.isEmpty() && takePattern.isEmpty() ? useFilterAttr : isRegExpFilter;
				if (isRegExpFilter) {
					if (!ignorePattern.isEmpty() && takePattern.isEmpty()) {
						var op = FileNameFileFilterRule.OP_MATCHES_REGEXP;
						var ignoreRule = new FileNameFileFilterRule(new TextValue(ignorePattern), op);
						fileFilter = new FileFilter(FileFilter.MATCH_ALL, FileFilter.EXCLUDE, true, ignoreRule);
					}
					else if (ignorePattern.isEmpty() && !takePattern.isEmpty()) {
						var op = FileNameFileFilterRule.OP_MATCHES_REGEXP;
						var takeRule = new FileNameFileFilterRule(new TextValue(takePattern), op);
						fileFilter = new FileFilter(FileFilter.MATCH_ALL, FileFilter.INCLUDE, true, takeRule);
					}
					else if (!ignorePattern.isEmpty() && !takePattern.isEmpty()) {
						var ignoreOp = FileNameFileFilterRule.OP_MATCHES_REGEXP;
						FileFilterRule ignoreRule = new FileNameFileFilterRule(new TextValue(ignorePattern), ignoreOp);
						var takeOp = FileNameFileFilterRule.OP_DOESNT_MATCHES_REGEXP;
						FileFilterRule takeRule = new FileNameFileFilterRule(new TextValue(takePattern), takeOp);
						fileFilter = new FileFilter(FileFilter.MATCH_ALL, FileFilter.EXCLUDE, true, ignoreRule, takeRule);
					}
					else {
						fileFilter = null;
					}
				}
				else {
					fileFilter = null;
				}
			}
		}
	}

	public boolean isSyncSubDirs() {
		return syncSubDirs;
	}

	public String getPatternsType() {
		return patternsType;
	}

	public String getTakePattern() {
		return takePattern;
	}

	public String getIgnorePattern() {
		return ignorePattern;
	}

	public FileFilter getFileFilter() {
		return fileFilter;
	}

	public boolean isUseFilter() {
		return useFilter;
	}

	public FileFilterTree getFileFilterTree() {
		return fileFilterTree;
	}

	@Override
	public RuleSet createRuleSet() {
		var ruleSet = new SimplifiedSyncRules();
		ruleSet.setUsingRecursion(syncSubDirs);

		if ((null != patternsType) && !patternsType.isEmpty()) {
			ruleSet.setPatternsType(patternsType);
		}
		else {
			ruleSet.setPatternsType(PATTERN_TYPE_REG_EXP);
		}
		ruleSet.setIgnorePattern(ignorePattern);
		ruleSet.setTakePattern(takePattern);
		ruleSet.setFileFilter(fileFilter);
		ruleSet.setUseFilter(useFilter);
		ruleSet.setFileFilterTree(fileFilterTree);
		return ruleSet;
	}

	@Override
	public String toString() {
		return "SimplyfiedRuleSetDescriptor{"
			+ "syncSubDirs="
			+ syncSubDirs
			+ ", ignorePattern='"
			+ ignorePattern
			+ '\''
			+ ", takePattern='"
			+ takePattern
			+ '\''
			+ ", patternsType='"
			+ patternsType
			+ '\''
			+ ", fileFilter="
			+ fileFilter
			+ ", useFilter="
			+ useFilter
			+ ", fileFilterTree="
			+ fileFilterTree
			+ '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		var that = (SimplifiedRuleSetDescriptor) o;
		return (syncSubDirs == that.syncSubDirs)
			&& (useFilter == that.useFilter)
			&& Objects.equals(ignorePattern, that.ignorePattern)
			&& Objects.equals(takePattern, that.takePattern)
			&& Objects.equals(patternsType, that.patternsType)
			&& Objects.equals(fileFilter, that.fileFilter)
			&& Objects.equals(fileFilterTree, that.fileFilterTree);
	}

	@Override
	public int hashCode() {
		return Objects.hash(syncSubDirs, ignorePattern, takePattern, patternsType, fileFilter, useFilter, fileFilterTree);
	}
}
