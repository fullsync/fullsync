/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.Serializable;

import net.sourceforge.fullsync.fs.File;

/**
 * @author Michele Aiello
 */
public class FileFilter implements Serializable {
	
	public static final int MATCH_ALL = 0;
	public static final int MATCH_ANY = 1;

	public static final int INCLUDE = 0;
	public static final int EXCLUDE = 1;

	private int match_type;
	private int filter_type;
	
	private boolean appliesToDir;
	
	private FileFilterRule[] rules;
	
	public FileFilter() {
		match_type = 0;
		filter_type = 0;
		appliesToDir = true;
		rules = new FileFilterRule[0];
	}
	
	public void setMatchType(int match_type) {
		this.match_type = match_type;
	}
	
	public int getMatchType() {
		return match_type;
	}
	
	public void setFilterType(int filter_type) {
		this.filter_type = filter_type;
	}
	
	public int getFilterType() {
		return this.filter_type;
	}
	
	public void setFileFilterRules(FileFilterRule[] rules) {
		this.rules = rules;
	}
	
	public void setAppliesToDirectories(boolean appliesToDir) {
		this.appliesToDir = appliesToDir;
	}
	
	public boolean appliesToDirectories() {
		return appliesToDir;
	}
	
	public FileFilterRule[] getFileFiltersRules() {
		return this.rules;
	}

	public boolean match(File file) {
		boolean result = _match(file);
		return (filter_type == INCLUDE)?result:!result;
	}
	
	private boolean _match(File file) {
		if (rules.length == 0) {
			return true;
		}
				
		switch (match_type) {
			case MATCH_ALL: {
				for (int i = 0; i < rules.length; i++) {
					if ((!appliesToDir) && (file.isDirectory())) {
						continue;
					}
					try {
						boolean res = rules[i].match(file);
						if (!res) {
							return false;
						}
					} catch (FilterRuleNotAppliableException e) {
					}
				}
				return true;
			}
		
			case MATCH_ANY: {
				int applyedRules = 0;

				for (int i = 0; i < rules.length; i++) {
					if ((!appliesToDir) && (file.isDirectory())) {
						continue;
					}
					try {
						boolean res = rules[i].match(file);
						if (res) {
							return true;
						}
						applyedRules++;
					} catch (FilterRuleNotAppliableException e) {
					}
				}
				return (applyedRules == 0);
			}
			default:
				return true;
		}
	}
	
	public String toString() {
		if (rules.length == 0) {
			return "Empty filter";
		}
		StringBuffer buff = new StringBuffer(25+30*rules.length);
		
		switch (filter_type) {
			case INCLUDE:
				buff.append("Include");
				break;
			case EXCLUDE:
				buff.append("Exclude");
				break;
		}
		
		buff.append(" any file where ");
		
		for (int i = 0; i < rules.length-1; i++) {
			buff.append(rules[i].toString());
			switch (match_type) {
				case MATCH_ALL:
					buff.append(" and ");
					break;
				case MATCH_ANY:
					buff.append(" or ");
					break;
			}
		}
		
		if (rules.length > 0) {
			buff.append(rules[rules.length-1].toString());
		}
		
		return buff.toString();
	}
	
}
