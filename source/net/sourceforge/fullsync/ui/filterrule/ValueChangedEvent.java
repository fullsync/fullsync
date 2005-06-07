/*
 * Created on Jun 6, 2005
 */
package net.sourceforge.fullsync.ui.filterrule;

import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

/**
 * @author Michele Aiello
 */
public class ValueChangedEvent {

	private OperandValue value;
	
	public ValueChangedEvent(OperandValue value) {
		this.value = value;
	}
	
	public OperandValue getValue() {
		return value;
	}
}
