/*
 * Created on Nov 5, 2004
 */
package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.RuleSetDescriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Michele Aiello
 */
public class AdvancedRuleSetDescriptor extends RuleSetDescriptor {

	private String ruleSetName;
	
	public AdvancedRuleSetDescriptor() {
		
	}
	
	public AdvancedRuleSetDescriptor(String ruleSetName) {
		this.ruleSetName = ruleSetName;
	}
	
	/** (non-Javadoc)
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#getType()
	 */
	public String getType() {
		return "advanced";
	}
	
	/** (non-Javadoc)
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#serialize(org.w3c.dom.Document)
	 */
	public Element serialize(Document document) {
		Element advancedRuleSetElement = document.createElement("AdvancedRuleSet");
		advancedRuleSetElement.setAttribute("name", getRuleSetName());
		return advancedRuleSetElement;
	}
	
	/** (non-Javadoc)
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#unserializeDescriptor(org.w3c.dom.Element)
	 */
	protected void unserializeDescriptor(Element element) {
		Element ruleSetNameElement = (Element)element.getElementsByTagName("AdvancedRuleSet").item(0);
		ruleSetName = ruleSetNameElement.getAttribute("name");
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
