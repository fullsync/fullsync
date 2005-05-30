/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.fs.File;

/**
 * @author Michele Aiello
 */
public class FileFilter {
	
	public static final int MATCH_ALL = 0;
	public static final int MATCH_ANY = 1;
		
	private int match_type;
	private FileFilterRule[] rules;
	
	public FileFilter() {
		match_type = -1;
		rules = new FileFilterRule[0];
	}
	
	public void setMatchType(int match_type) {
		this.match_type = match_type;
	}
	
	public int getMatchType() {
		return match_type;
	}
	
	public void setFileFilterRules(FileFilterRule[] rules) {
		this.rules = rules;
	}
	
	public FileFilterRule[] getFileFiltersRules() {
		return this.rules;
	}
	
	public boolean match(File file) {
		if (rules.length == 0) {
			return true;
		}

		switch (match_type) {
			case MATCH_ALL: {
				for (int i = 0; i < rules.length; i++) {
					boolean res = rules[i].match(file);
					if (!res) {
						return false;
					}
				}
				return true;
			}
		
			case MATCH_ANY: {
				boolean b = false;
				for (int i = 0; i < rules.length; i++) {
					boolean res = rules[i].match(file);
					if (res) {
						return true;
					}
				}
				return false;
			}
			default:
				return true;
		}
	}
	
	public String toString() {
		if (rules.length == 0) {
			return "Empty filter";
		}
		StringBuffer buff = new StringBuffer(30*rules.length);
		
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
