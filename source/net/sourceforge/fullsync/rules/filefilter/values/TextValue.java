/*
 * Created on Jun 6, 2005
 */
package net.sourceforge.fullsync.rules.filefilter.values;

/**
 * @author Michele Aiello
 */
public class TextValue implements OperandValue {

	private String value;
	
	public TextValue() {
		this.value = "";
	}
	
	public TextValue(String value) {
		this.value = value;
	}
	
	public void fromString(String value) {
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public String toString() {
		return value;
	}
	
}
