/*
 * Created on Nov 5, 2004
 */
package net.sourceforge.fullsync;

import java.io.Serializable;
import java.util.Hashtable;

import net.sourceforge.fullsync.impl.AdvancedRuleSetDescriptor;
import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Michele Aiello
 */
public abstract class RuleSetDescriptor implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	private static Hashtable descriptorRegister;
	
	// TODO [Michele] change this!
	static {
		descriptorRegister = new Hashtable(2);
		descriptorRegister.put("simple", SimplyfiedRuleSetDescriptor.class);
		descriptorRegister.put("advanced", AdvancedRuleSetDescriptor.class);
	}
	
	public abstract RuleSet createRuleSet();
	
	public abstract String getType();
	
	public abstract Element serialize(Document document);
	
	protected abstract void unserializeDescriptor(Element element);
	
	public static final RuleSetDescriptor unserialize(Element element) {
		if (element == null) {
			return null;
		}
    	String ruleSetType = element.getAttribute("type");
    	Class ruleSetDesctiptorClass = (Class) descriptorRegister.get(ruleSetType);
    	
    	if (ruleSetDesctiptorClass == null) {
    		return null;
    	}
    	else {
    		RuleSetDescriptor desc = null;
    		
    		try {
				desc = (RuleSetDescriptor) ruleSetDesctiptorClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
			
			desc.unserializeDescriptor(element);
    		
			return desc;
    	}
    	
	}
	
}
