/*
 * Created on Jun 6, 2005
 */
package net.sourceforge.fullsync.rules.filefilter.values;


/**
 * @author Michele Aiello
 */
public class AgeValue implements OperandValue {

	private double value;
	private int unit;
	
	public static final int SECONDS = 0;
	public static final int MINUTES = 1;
	public static final int HOURS = 2;
	public static final int DAYS = 3;
	
	private static final String[] allUnits = new String[] {
			"seconds",
			"minutes",
			"hours",
			"days"
	};

	public static String[] getAllUnits() {
		return allUnits;
	}
	
	public AgeValue() {
		this.value = 0;
		this.unit = 0;
	}
	
	public AgeValue(String age) {
		fromString(age);
	}
	
	public AgeValue(double value, int unit) {
		this.value = value;
		this.unit = unit;
	}
	
	public double getValue() {
		return value;
	}
		
	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public long getSeconds() {
		return (long)Math.floor(value*Math.pow(60, unit));
	}

	public void fromString(String value) {
		int sep = value.indexOf(' ');
		if (sep > 0) {
			this.value = Double.parseDouble(value.substring(0, sep));
			String u = value.substring(sep+1, value.length());
			this.unit = getUnitFromString(u);
		}
	}
	
	public int getUnitFromString(String unitName) {
		for (int i = 0; i < allUnits.length; i++) {
			if (allUnits[i].equalsIgnoreCase(unitName)) {
				return i;
			}
		}
		return -1;
	}
	

	public String toString() {
		return String.valueOf(value) + " " + allUnits[unit];
	}
}
