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

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.RuleSetDescriptor;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.FileFilterManager;
import net.sourceforge.fullsync.rules.filefilter.FileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FileNameFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.filefiltertree.FileFilterTree;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SimplyfiedRuleSetDescriptor extends RuleSetDescriptor {
	public static final String RULESET_TYPE = "simple";

	private static final long serialVersionUID = 2L;
	private boolean syncSubDirs = false;
	private String ignorePattern;
	private String takePattern;
	private String patternsType;
	private FileFilter fileFilter;
	private boolean useFilter;
	private FileFilterTree fileFilterTree;

	public SimplyfiedRuleSetDescriptor() {

	}

	public SimplyfiedRuleSetDescriptor(boolean syncSubDirs, FileFilter fileFilter, boolean useFilter, FileFilterTree fileFilterTree) {
		this.syncSubDirs = syncSubDirs;
		this.ignorePattern = "";
		this.takePattern = "";
		this.patternsType = "";
		this.fileFilter = fileFilter;
		this.useFilter = useFilter;
		this.fileFilterTree = fileFilterTree;
	}

	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#getType()
	 */
	@Override
	public String getType() {
		return RULESET_TYPE;
	}

	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#serializeDescriptor(org.w3c.dom.Document)
	 */
	@Override
	public Element serializeDescriptor(Document document) {
		Element simpleRuleSetElement = document.createElement("SimpleRuleSet");

		simpleRuleSetElement.setAttribute("syncSubs", String.valueOf(isSyncSubDirs()));
		simpleRuleSetElement.setAttribute("patternsType", getPatternsType());
		simpleRuleSetElement.setAttribute("ignorePattern", getIgnorePattern());
		simpleRuleSetElement.setAttribute("takePattern", getTakePattern());
		simpleRuleSetElement.setAttribute("useFilter", String.valueOf(isUseFilter()));

		FileFilterManager filterManager = new FileFilterManager();

		if (fileFilter != null) {
			Element fileFilterElement = filterManager.serializeFileFilter(getFileFilter(), document, "FileFilter", "FileFilterRule");
			simpleRuleSetElement.appendChild(fileFilterElement);
		}

		if (fileFilterTree != null) {
			HashMap<String, FileFilter> itemsMap = fileFilterTree.getItemsMap();
			Set<Entry<String, FileFilter>> entrySet = itemsMap.entrySet();
			for (Entry<String, FileFilter> entry : entrySet) {
				String path = entry.getKey();
				FileFilter filter = entry.getValue();
				Element subdirFilterElement = document.createElement("SubdirectoryFileFilter");
				subdirFilterElement.setAttribute("path", path);
				Element fileFilterElement = filterManager.serializeFileFilter(filter, document, "FileFilter", "FileFilterRule");
				subdirFilterElement.appendChild(fileFilterElement);
				simpleRuleSetElement.appendChild(subdirFilterElement);
			}
		}

		return simpleRuleSetElement;
	}

	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#unserializeDescriptor(org.w3c.dom.Element)
	 */
	@Override
	protected void unserializeDescriptor(Element element) {
		NodeList ruleSetConfigNodeList = element.getElementsByTagName("SimpleRuleSet");

		if (ruleSetConfigNodeList.getLength() == 0) {
			syncSubDirs = true;
			useFilter = false;
			ignorePattern = "";
			takePattern = "";
			fileFilter = null;
			fileFilterTree = null;
		}
		else {
			Element simpleRuleSetConfigElement = (Element) ruleSetConfigNodeList.item(0);
			syncSubDirs = Boolean.valueOf(simpleRuleSetConfigElement.getAttribute("syncSubs")).booleanValue();
			patternsType = simpleRuleSetConfigElement.getAttribute("patternsType");
			ignorePattern = simpleRuleSetConfigElement.getAttribute("ignorePattern");
			takePattern = simpleRuleSetConfigElement.getAttribute("takePattern");
			String useFilterStr = simpleRuleSetConfigElement.getAttribute("useFilter");
			if ((useFilterStr != null) && (!useFilterStr.equals(""))) {
				useFilter = Boolean.valueOf(useFilterStr).booleanValue();
			}
			NodeList fileFilterNodeList = simpleRuleSetConfigElement.getElementsByTagName("FileFilter");
			if (fileFilterNodeList.getLength() > 0) {
				FileFilterManager filterManager = new FileFilterManager();
				Element fileFilterElement = (Element) fileFilterNodeList.item(0);
				fileFilter = filterManager.unserializeFileFilter(fileFilterElement, "FileFilterRule");

				NodeList subdirFiltersNodeList = simpleRuleSetConfigElement.getElementsByTagName("SubdirectoryFileFilter");
				int numOfDirs = subdirFiltersNodeList.getLength();
				fileFilterTree = new FileFilterTree();
				for (int i = 0; i < numOfDirs; i++) {
					Element subDirElement = (Element) subdirFiltersNodeList.item(i);
					String path = subDirElement.getAttribute("path");
					fileFilterNodeList = subDirElement.getElementsByTagName("FileFilter");
					if (fileFilterNodeList.getLength() > 0) {
						Element subDirFileFilterElement = (Element) fileFilterNodeList.item(0);
						FileFilter subDirFileFilter = filterManager.unserializeFileFilter(subDirFileFilterElement, "FileFilterRule");

						fileFilterTree.addFileFilter(path, subDirFileFilter);
					}
				}
			}
			else {
				fileFilter = null;
				if (patternsType.equals("RegExp")) {
					if (!ignorePattern.equals("") && (takePattern.equals(""))) {
						fileFilter = new FileFilter();
						fileFilter.setMatchType(FileFilter.MATCH_ALL);
						fileFilter.setFilterType(FileFilter.EXCLUDE);
						FileFilterRule[] rules = new FileFilterRule[] {
								new FileNameFileFilterRule(new TextValue(ignorePattern), FileNameFileFilterRule.OP_MATCHES_REGEXP) };
						fileFilter.setFileFilterRules(rules);
						useFilter = true;
					}
					if (ignorePattern.equals("") && (!takePattern.equals(""))) {
						fileFilter = new FileFilter();
						fileFilter.setMatchType(FileFilter.MATCH_ALL);
						fileFilter.setFilterType(FileFilter.INCLUDE);
						FileFilterRule[] rules = new FileFilterRule[] {
								new FileNameFileFilterRule(new TextValue(takePattern), FileNameFileFilterRule.OP_MATCHES_REGEXP) };
						fileFilter.setFileFilterRules(rules);
						useFilter = true;
					}
					if (!ignorePattern.equals("") && (!takePattern.equals(""))) {
						fileFilter = new FileFilter();
						fileFilter.setMatchType(FileFilter.MATCH_ALL);
						fileFilter.setFilterType(FileFilter.EXCLUDE);
						FileFilterRule[] rules = new FileFilterRule[] {
							new FileNameFileFilterRule(new TextValue(ignorePattern), FileNameFileFilterRule.OP_MATCHES_REGEXP),
							new FileNameFileFilterRule(new TextValue(takePattern), FileNameFileFilterRule.OP_DOESNT_MATCHES_REGEXP),
						};
						fileFilter.setFileFilterRules(rules);
						useFilter = true;
					}
				}
				else {
					// TODO Wildcard translation
				}
			}
		}
	}

	/**
	 * @return Returns the syncSubDirs.
	 */
	public boolean isSyncSubDirs() {
		return syncSubDirs;
	}

	/**
	 * @param syncSubDirs
	 *            The syncSubDirs to set.
	 */
	public void setSyncSubDirs(boolean syncSubDirs) {
		this.syncSubDirs = syncSubDirs;
	}

	/**
	 * @return Returns the patternsType.
	 */
	public String getPatternsType() {
		return patternsType;
	}

	/**
	 * @param type
	 *            type The patternsType to set.
	 */
	public void setPatternsType(String type) {
		this.patternsType = type;
	}

	/**
	 * @return Returns the takePattern.
	 */
	public String getTakePattern() {
		return takePattern;
	}

	/**
	 * @param takePattern
	 *            The takePattern to set.
	 */
	public void setTakePattern(String takePattern) {
		this.takePattern = takePattern;
	}

	/**
	 * @return Returns the ignorePattern.
	 */
	public String getIgnorePattern() {
		return ignorePattern;
	}

	/**
	 * @param ignorePattern
	 *            The ignorePattern to set.
	 */
	public void setIgnorePattern(String ignorePattern) {
		this.ignorePattern = ignorePattern;
	}

	public FileFilter getFileFilter() {
		return fileFilter;
	}

	public void setFileFilter(FileFilter filter) {
		this.fileFilter = filter;
	}

	public boolean isUseFilter() {
		return useFilter;
	}

	public FileFilterTree getFileFilterTree() {
		return fileFilterTree;
	}

	public void setFileFilterTree(FileFilterTree fileFilterTree) {
		this.fileFilterTree = fileFilterTree;
	}

	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#createRuleSet()
	 */
	@Override
	public RuleSet createRuleSet() {
		SimplyfiedSyncRules ruleSet = new SimplyfiedSyncRules();
		ruleSet.setUsingRecursion(syncSubDirs);

		if ((patternsType != null) && (!patternsType.equals(""))) {
			ruleSet.setPatternsType(patternsType);
		}
		else {
			ruleSet.setPatternsType("RegExp");
		}
		ruleSet.setIgnorePattern(ignorePattern);
		ruleSet.setTakePattern(takePattern);
		ruleSet.setFileFilter(fileFilter);
		ruleSet.setUseFilter(useFilter);
		ruleSet.setFileFilterTree(fileFilterTree);

		return ruleSet;
	}

}
