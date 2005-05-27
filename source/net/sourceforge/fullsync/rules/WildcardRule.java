/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules;

import java.util.regex.Pattern;

import net.sourceforge.fullsync.fs.File;

/**
 * @author Michele Aiello
 */
public class WildcardRule implements Rule {

	private Pattern pattern;
	
    public WildcardRule(String pattern)
    {
        this.pattern = Pattern.compile(toRegExp(pattern));
    }
	
	public boolean accepts(File node) {
	    return pattern.matcher( node.getName() ).matches();
	}

	private String toRegExp(String pattern) {
		StringBuffer buff = new StringBuffer(pattern.length() * 2);
		
		for (int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			switch (c) {
				case '*':
				case '?':
					buff.append('.');
					buff.append(c);
					break;
				case '.':
				case '/':
				case '\\':
					buff.append('\\');
					buff.append(c);
					break;
				default:
					buff.append(c);
			}
		}
		return buff.toString();
	}
	
}
