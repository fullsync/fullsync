/*
 * Created on Nov 5, 2004
 */
package net.sourceforge.fullsync.impl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.RuleSetDescriptor;

/**
 * @author Michele Aiello
 */
public class SimplyfiedRuleSetDescriptor extends RuleSetDescriptor {

	private boolean syncSubDirs = false;
	private boolean deleteOnDestination = false;
	private String ignorePattern;
	private String takePattern;
	
	public SimplyfiedRuleSetDescriptor() {
		
	}
	
	public SimplyfiedRuleSetDescriptor(boolean syncSubDirs, boolean deleteOnDestination, 
			String ignorePatter, String acceptPatter) 
	{
		this.syncSubDirs = syncSubDirs;
		this.deleteOnDestination = deleteOnDestination;
		this.ignorePattern = ignorePatter;
		this.takePattern = acceptPatter;
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
		simpleRuleSetElement.setAttribute("deleteOnDestination", String.valueOf(isDeleteOnDestination()));
		simpleRuleSetElement.setAttribute("ignorePattern", getIgnorePattern());
		simpleRuleSetElement.setAttribute("takePattern", getTakePattern());

		return simpleRuleSetElement;
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#unserializeDescriptor(org.w3c.dom.Element)
	 */
	protected void unserializeDescriptor(Element element) {
		NodeList ruleSetConfigNodeList = element.getElementsByTagName("SimpleRuleSet");
		
		if (ruleSetConfigNodeList.getLength() == 0) {
			syncSubDirs = true;
			deleteOnDestination = false;
			ignorePattern = "";
			takePattern = "";
		}
		else {
			Element simpleRuleSetConfigElement = (Element)ruleSetConfigNodeList.item(0);
			syncSubDirs = Boolean.valueOf(simpleRuleSetConfigElement.getAttribute("syncSubs")).booleanValue();
			deleteOnDestination = Boolean.valueOf(simpleRuleSetConfigElement.getAttribute("deleteOnDestination")).booleanValue();
			ignorePattern = simpleRuleSetConfigElement.getAttribute("ignorePattern");
			takePattern = simpleRuleSetConfigElement.getAttribute("takePattern");
		}
	}
	
	/**
	 * @return Returns the deleteOnDestination.
	 */
	public boolean isDeleteOnDestination() {
		return deleteOnDestination;
	}
	
	/**
	 * @param deleteOnDestination The deleteOnDestination to set.
	 */
	public void setDeleteOnDestination(boolean deleteOnDestination) {
		this.deleteOnDestination = deleteOnDestination;
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
	
	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#createRuleSet()
	 */
	public RuleSet createRuleSet() {
		SimplyfiedSyncRules ruleSet = new SimplyfiedSyncRules();
		ruleSet.setUsingRecursion(syncSubDirs);
		if (deleteOnDestination) {
			ruleSet.setApplyingDeletion(Location.Destination);
		}
		else {
			ruleSet.setApplyingDeletion(Location.None);
		}
		
		ruleSet.setIgnorePattern(ignorePattern);
		ruleSet.setTakePattern(takePattern);
		
		return ruleSet;
	}

}
