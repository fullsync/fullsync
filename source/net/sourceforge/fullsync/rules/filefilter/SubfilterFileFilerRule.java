/*
 * Created on 6-feb-2006
 *
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.FilterValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

/**
 * @author Michele Aiello
 */
public class SubfilterFileFilerRule extends FileFilterRule {

	public static String typeName = "Nested Filter";

	private FileFilter fileFilter;
	
	public SubfilterFileFilerRule(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public String getRuleType() {
		return typeName;
	}

	public int getOperator() {
		return 0;
	}

	public String getOperatorName() {
		return "";
	}

	public OperandValue getValue() {
		return new FilterValue(fileFilter);
	}

	public boolean match(File file) throws FilterRuleNotAppliableException {
		return fileFilter.match(file);
	}
	
	public String toString() {
		return "("+fileFilter.toString()+")";
	}

}
