/*
 * Created on Nov 5, 2004
 */
package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.RuleSetDescriptor;

/**
 * @author Michele Aiello
 */
public class SimplyfiedRuleSetDescriptor implements RuleSetDescriptor {

	//TODO add all parameters.
	
	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#createRuleSet()
	 */
	public RuleSet createRuleSet() {
		return new SimplyfiedSyncRules();
	}

}
