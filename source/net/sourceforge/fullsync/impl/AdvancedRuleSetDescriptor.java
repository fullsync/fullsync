/*
 * Created on Nov 5, 2004
 */
package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.RuleSetDescriptor;

/**
 * @author Michele Aiello
 */
public class AdvancedRuleSetDescriptor implements RuleSetDescriptor {

	private String ruleSetName;
	
	public AdvancedRuleSetDescriptor(String ruleSetName) {
		this.ruleSetName = ruleSetName;
	}
	
	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#createRuleSet()
	 */
	public RuleSet createRuleSet() {
		SyncRules rules = new SyncRules(ruleSetName);
		rules.setJustLogging(false);
		
		return rules;
	}
	
	/**
	 * @return Returns the ruleSetName.
	 */
	public String getRuleSetName() {
		return ruleSetName;
	}

}
