/*
 * Created on Nov 4, 2004
 */
package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author Michele Aiello
 */
public class SimplyfiedSyncRules implements RuleSet {
	
	private String name;
		
	private boolean isUsingRecursion = true;
	
	private int applyingDeletion = Location.Destination;
	
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
	
	/**
	 * @see net.sourceforge.fullsync.RuleSet#isUsingRecursionOnIgnore()
	 */
	public boolean isUsingRecursionOnIgnore() {
	    // REVISIT this is not a discussion board ! ;)
		// [Michele] I made this equals to isUsingRecursion for the moment.
		// I might change it later.
	    // [Jan] i don't think it's good to default to recurse on ignore
	    // as it is pretty senseless if overwriting is not allowed
	    // so i made it return false
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
		// MICHELE I have to add patterns on file name.
		return false;
	}
	
	/**
	 * @see net.sourceforge.fullsync.FileComparer#compareFiles(net.sourceforge.fullsync.fs.FileAttributes, net.sourceforge.fullsync.fs.FileAttributes)
	 */
	public State compareFiles(FileAttributes src, FileAttributes dst)
			throws DataParseException 
	{
		if (src.getLength() != dst.getLength()) {
			return new State(State.FileChange, Location.None);
		}

		if (src.getLastModified() > dst.getLastModified()) {
			return new State(State.FileChange, Location.Source);
		}

		return new State(State.NodeInSync, Location.Both);
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSet#createChild(net.sourceforge.fullsync.fs.File, net.sourceforge.fullsync.fs.File)
	 */
	public RuleSet createChild(File src, File dst) throws FileSystemException,
			DataParseException 
	{
	    // TODO even simple sync rules should allow overwrite rules
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
}
