package net.sourceforge.fullsync;

import java.io.IOException;

import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface RuleSet extends IgnoreDecider, FileComparer
{
    public String getName();
	
    public boolean isUsingRecursion();
    public boolean isUsingRecursionOnIgnore();
    public boolean isJustLogging();
    
    public boolean isApplyingDeletion( int location );
    public boolean isCheckingBufferAlways( int location );
    public boolean isCheckingBufferOnReplace( int location );
    
    public RuleSet createChild( File src, File dst ) throws IOException, DataParseException;
    
    // public boolean isUsingRulesFile( int location ); 
    // public boolean isBuffering( int where );
    // public String getSyncBufferFilename();
    // public String getRulesFilename();
}
