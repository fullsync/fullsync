/*
 * Created on Nov 5, 2004
 */
package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.RuleSetDescriptor;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.FileFilterManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Michele Aiello
 */
public class SimplyfiedRuleSetDescriptor extends RuleSetDescriptor {

	private boolean syncSubDirs = false;
	private String ignorePattern;
	private String takePattern;
	private String patternsType;
	private FileFilter fileFilter;
	private boolean filterSelectsFiles;
	private boolean useFilter;
	
	public SimplyfiedRuleSetDescriptor() {
		
	}
	
	public SimplyfiedRuleSetDescriptor(boolean syncSubDirs, String ignorePatter, String acceptPatter, 
			String patternsType, FileFilter fileFilter, 
			boolean filterSelectsFiles, boolean useFilter) 
	{
		this.syncSubDirs = syncSubDirs;
		this.ignorePattern = ignorePatter;
		this.takePattern = acceptPatter;
		this.patternsType = patternsType;
		this.fileFilter = fileFilter;
		this.filterSelectsFiles = filterSelectsFiles;
		this.useFilter = useFilter;
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#getType()
	 */
	public String getType() {
		return "simple";
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#serialize(org.w3c.dom.Document)
	 */
	public Element serialize(Document document) {
		Element simpleRuleSetElement = document.createElement("SimpleRuleSet");

		simpleRuleSetElement.setAttribute("syncSubs", String.valueOf(isSyncSubDirs()));		
		simpleRuleSetElement.setAttribute("patternsType", getPatternsType());
		simpleRuleSetElement.setAttribute("ignorePattern", getIgnorePattern());
		simpleRuleSetElement.setAttribute("takePattern", getTakePattern());
		simpleRuleSetElement.setAttribute("filterSelectsFiles", String.valueOf(isFilterSelectsFiles()));
		simpleRuleSetElement.setAttribute("useFilter", String.valueOf(isUseFilter()));

		if (fileFilter != null) {
			FileFilterManager filterManager = new FileFilterManager();
			Element fileFilterElement = filterManager.serializeFileFilter(getFileFilter(), document, "FileFilter");
			simpleRuleSetElement.appendChild(fileFilterElement);
		}
		
		return simpleRuleSetElement;
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#unserializeDescriptor(org.w3c.dom.Element)
	 */
	protected void unserializeDescriptor(Element element) {
		NodeList ruleSetConfigNodeList = element.getElementsByTagName("SimpleRuleSet");
		
		if (ruleSetConfigNodeList.getLength() == 0) {
			syncSubDirs = true;
			ignorePattern = "";
			takePattern = "";
		}
		else {
			Element simpleRuleSetConfigElement = (Element)ruleSetConfigNodeList.item(0);
			syncSubDirs = Boolean.valueOf(simpleRuleSetConfigElement.getAttribute("syncSubs")).booleanValue();
			patternsType = simpleRuleSetConfigElement.getAttribute("patternsType");
			ignorePattern = simpleRuleSetConfigElement.getAttribute("ignorePattern");
			takePattern = simpleRuleSetConfigElement.getAttribute("takePattern");
			String filterSelectsStr = simpleRuleSetConfigElement.getAttribute("filterSelectsFiles");
			if ((filterSelectsStr != null) && (!filterSelectsStr.equals(""))) {
				filterSelectsFiles = Boolean.valueOf(filterSelectsStr).booleanValue();
			}
			String useFilterStr = simpleRuleSetConfigElement.getAttribute("useFilter");
			if ((useFilterStr != null) && (!useFilterStr.equals(""))) {
				useFilter = Boolean.valueOf(useFilterStr).booleanValue();
			}
			NodeList fileFilterNodeList = simpleRuleSetConfigElement.getElementsByTagName("FileFilter");
			if (fileFilterNodeList.getLength() > 0) {
				FileFilterManager filterManager = new FileFilterManager();
				fileFilter = filterManager.unserializeFileFilter((Element)fileFilterNodeList.item(0));
			}
			else {
				fileFilter = null;
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
	 * @param syncSubDirs The syncSubDirs to set.
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
	 * @param type type The patternsType to set.
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
	 * @param takePattern The takePattern to set.
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
	 * @param ignorePattern The ignorePattern to set.
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
	
	public boolean isFilterSelectsFiles() {
		return filterSelectsFiles;
	}

	public boolean isUseFilter() {
		return useFilter;
	}

	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#createRuleSet()
	 */
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
		ruleSet.setFilterSelectsFiles(filterSelectsFiles);
		ruleSet.setUseFilter(useFilter);
		
		return ruleSet;
	}

}
