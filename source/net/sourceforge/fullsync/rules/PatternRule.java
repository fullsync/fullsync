package net.sourceforge.fullsync.rules;


import java.util.regex.Pattern;

import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class PatternRule implements Rule
{
    private Pattern pattern;
    
    public PatternRule( String pattern )
    {
        this.pattern = Pattern.compile( pattern );
    }
    
	public boolean accepts( Node node )
	{
	    return pattern.matcher( node.getName() ).matches();
	}
}
