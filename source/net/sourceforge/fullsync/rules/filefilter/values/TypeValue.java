/*
 * Created on Jun 6, 2005
 */
package net.sourceforge.fullsync.rules.filefilter.values;

/**
 * @author Michele Aiello
 */
public class TypeValue implements OperandValue {
	
	public static final int FILE_TYPE = 0;
	public static final int DIRECTORY_TYPE = 1;
	
	private static final String[] valueNames = new String[] {
			"file",
			"directory"
	};

	private int type;
	
	public TypeValue() {
		this.type = FILE_TYPE;
	}
	
	public TypeValue(int type) {
		this.type = type;
	}
	
	public TypeValue(String type) {
		fromString(type);
	}
	
	public void setType(int fileType) {
		if ((fileType < FILE_TYPE) || (fileType > DIRECTORY_TYPE)) {
			//TODO exception?
		}
		this.type = fileType;
	}
	
	public int getType() {
		return this.type;
	}
	
	public void fromString(String str) {
		for (int i = 0; i < valueNames.length; i++) {
			if (valueNames[i].equalsIgnoreCase(str)) {
				this.type = i;
				return;
			}
		}
	}
	
	public String toString() {
		return valueNames[type];
	}
	
	public boolean isFile() {
		return (type == FILE_TYPE);
	}

	public boolean isDirectory() {
		return (type == DIRECTORY_TYPE);
	}

	public static String[] getAllTypes() {
		return valueNames;
	}
	
}
