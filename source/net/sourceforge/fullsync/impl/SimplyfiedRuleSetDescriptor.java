/*
 * Created on Nov 5, 2004
 */
package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.RuleSetDescriptor;

/**
 * @author Michele Aiello
 */
public class SimplyfiedRuleSetDescriptor implements RuleSetDescriptor {

	private boolean syncSubDirs = false;
	private boolean deleteOnDestination = false;
	private String ignorePattern;
	private String takePattern;
	
	public SimplyfiedRuleSetDescriptor(boolean syncSubDirs, boolean deleteOnDestination, 
			String ignorePatter, String acceptPatter) 
	{
		this.syncSubDirs = syncSubDirs;
		this.deleteOnDestination = deleteOnDestination;
		this.ignorePattern = ignorePatter;
		this.takePattern = acceptPatter;
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
