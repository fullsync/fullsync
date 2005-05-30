/*
 * Created on Nov 4, 2004
 */
package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.rules.PatternRule;
import net.sourceforge.fullsync.rules.Rule;
import net.sourceforge.fullsync.rules.WildcardRule;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;

/**
 * @author Michele Aiello
 */
public class SimplyfiedSyncRules implements RuleSet {
	
	private String name;
		
	private boolean isUsingRecursion = true;
	
	private int applyingDeletion = Location.None;
	
	private String patternsType;
	
	private String ignorePattern;
	private Rule ignoreRule;
	
	private String takePattern;
	private Rule takeRule;
	
	private FileFilter fileFilter;
	private boolean filterSelectsFiles;
	
	private boolean useFilter;
	
	/**
	 * Default Constructor
	 */
	public SimplyfiedSyncRules() {
	}

	/**
	 * Constructor
	 */
	public SimplyfiedSyncRules(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSet#isUsingRecursion()
	 */
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
		
		if ((ignorePattern == null) || (ignorePattern.equals(""))) {
			this.ignoreRule = null;
		}
		else {
			ignoreRule = createRuleFromPattern(ignorePattern);
		}
	}
	
	public void setTakePattern(String pattern) {
		this.takePattern = pattern;
		
		if ((takePattern == null) || (takePattern.equals(""))) {
			this.takeRule = null;
		}
		else {
			takeRule = createRuleFromPattern(takePattern);
		}		
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
	
	public boolean getFilterSelectsFiles() {
		return filterSelectsFiles;
	}
	
	public void setFilterSelectsFiles(boolean bool) {
		this.filterSelectsFiles = bool;
	}
	
	public void setUseFilter(boolean bool) {
		this.useFilter = bool;
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSet#isUsingRecursionOnIgnore()
	 */
	public boolean isUsingRecursionOnIgnore() {
		return false;
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSet#isJustLogging()
	 */
	public boolean isJustLogging() {
		return false;
	}
	
	/**
	 * @see net.sourceforge.fullsync.IgnoreDecider#isNodeIgnored(net.sourceforge.fullsync.fs.File)
	 */
	public boolean isNodeIgnored(File node) {
		if (!useFilter) {
			return false;
		}
		
		boolean take = true;
		
		if (fileFilter != null) {
			take = fileFilter.match(node);
		}
		if (!filterSelectsFiles) {
			take = !take;
		}
		
		return !take;
//		boolean take = true;
//		
//		if (take) {
//			if (ignoreRule != null) {
//				take = !ignoreRule.accepts(node);
//			}
//			else {
//				if (takeRule != null)
//					take = false;
//			}
//		}
//		
//		if (!take) {
//			if (takeRule != null) {
//				take = takeRule.accepts(node);
//			}
//		}
//		
//		return !take;
	}
	
	/**
	 * @see net.sourceforge.fullsync.FileComparer#compareFiles(net.sourceforge.fullsync.fs.FileAttributes, net.sourceforge.fullsync.fs.FileAttributes)
	 */
	public State compareFiles(FileAttributes src, FileAttributes dst)
			throws DataParseException 
	{
        if (Math.floor(src.getLastModified()/1000.0) > Math.floor(dst.getLastModified()/1000.0)) {
            return new State(State.FileChange, Location.Source);
        } else if (Math.floor(src.getLastModified()/1000.0) < Math.floor(dst.getLastModified()/1000.0)){
            return new State(State.FileChange, Location.Destination);
        }
		if (src.getLength() != dst.getLength()) {
			return new State(State.FileChange, Location.None);
		}
		return new State(State.NodeInSync, Location.Both);
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSet#createChild(net.sourceforge.fullsync.fs.File, net.sourceforge.fullsync.fs.File)
	 */
	public RuleSet createChild(File src, File dst) 
	{
	    // TODO even simple sync rules should allow override rules
		return this;
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSet#isApplyingDeletion(int)
	 */
	public boolean isApplyingDeletion(int location) {
		return (applyingDeletion & location) > 0;
	}
	
	/**
	 * @param applyingDeletion The applyingDeletion to set.
	 */
	public void setApplyingDeletion(int applyingDeletion) {
		this.applyingDeletion = applyingDeletion;
	}
	
	/**
	 * @return Returns the applyingDeletion.
	 */
	public int getApplyingDeletion() {
		return applyingDeletion;
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSet#isCheckingBufferAlways(int)
	 */
	public boolean isCheckingBufferAlways(int location) {
		return false;
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSet#isCheckingBufferOnReplace(int)
	 */
	public boolean isCheckingBufferOnReplace(int location) {
		return false;
	}
	
	private Rule createRuleFromPattern(String pattern) {
		if (patternsType.equals("Wildcard")) {
			return new WildcardRule(pattern);
		}
		
		if (patternsType.equals("RegExp")) {
			return new PatternRule(pattern);
		}
		
		return null;
	}
	
}
