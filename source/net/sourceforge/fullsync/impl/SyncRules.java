package net.sourceforge.fullsync.impl;

import java.io.InputStream;
import java.io.InputStreamReader;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.rules.PatternRule;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SyncRules extends AbstractRuleSet
{
	private boolean	processAllowed;

	public SyncRules( String name )
	{
	    super( name );
	    this.setRuleSet( name );
	}
	
    protected void processUseCommand( SyncTokenizer t ) throws DataParseException
	{
		String what = t.nextWord();
				
		if( what.equalsIgnoreCase( "rulefiles" ) ) usingSyncRulesFile = t.nextLocation();
		//else if( what.equalsIgnoreCase( "syncbuffer" ) ) usingSyncBufferFile = t.nextLocation();
		//else if( what.equalsIgnoreCase( "commands" ) ) usingSyncBufferFile = t.nextLocation();
		else if( what.equalsIgnoreCase( "direction" ) ) direction = t.nextLocation();
		
		else if( what.equalsIgnoreCase( "recursion" ) ) setUsingRecursion( t.nextBoolean() );
		else if( what.equalsIgnoreCase( "recursiononignore" ) ) setUsingRecursionOnIgnore( t.nextBoolean( ) );
		else if( what.equalsIgnoreCase( "ignoreall" ) ) ignoreAll = t.nextBoolean();
		else throw new DataParseException( "Unknown Identifier \""+what+"\" in \""+t.getSourceName()+"\" in line "+t.lineno() );
	}
	
	protected void processSetCommand( SyncTokenizer t ) throws DataParseException
	{
		String what = t.nextWord();
		String val = t.nextString();
		
		if( what.equalsIgnoreCase( "rulesfile" ) ) syncRulesFilename = val;
		//else if( what.equalsIgnoreCase( "syncbufferfile" ) ) syncBufferFilename = val;
		//else if( what.equalsIgnoreCase( "commandsfile" ) ) syncCommandsFilename = val;
		else if( what.equalsIgnoreCase( "ruleset" ) ) ruleSet = val;
		else throw new DataParseException( "Unknown Identifier \""+what+"\" in \""+t.getSourceName()+"\" in line "+t.lineno() );
	}
	
	protected void processApplyCommand( SyncTokenizer t ) throws DataParseException
	{
		String what = t.nextWord();
		
		if( what.equalsIgnoreCase( "ignorerules" ) ) applyingIgnoreRules = t.nextBoolean();
		else if( what.equalsIgnoreCase( "takerules" ) ) applyingTakeRules = t.nextBoolean();
		else if( what.equalsIgnoreCase( "syncrules" ) ) applyingSyncRules = t.nextBoolean();
		else if( what.equalsIgnoreCase( "deletion" ) ) applyingDeletion = t.nextLocation();
		else throw new DataParseException( "Unknown Identifier \""+what+"\" in \""+t.getSourceName()+"\" in line "+t.lineno() );
	}
	
	protected void processDefineCommand( SyncTokenizer t ) throws DataParseException
	{
		String what = t.nextWord();
		String val  = t.nextString();
		
		if( what.equalsIgnoreCase( "ignore" ) ) ignoreRules.add( new PatternRule( val ) );
		else if( what.equalsIgnoreCase( "take" ) ) takeRules.add( new PatternRule( val ) );
		else if( what.equalsIgnoreCase( "sync" ) ) syncRules.add( val );
		else throw new DataParseException( "Unknown Identifier \""+what+"\" in \""+t.getSourceName()+"\" in line "+t.lineno() );
	}
	
	protected void processResetCommand( SyncTokenizer t ) throws DataParseException
	{
		String what = t.nextWord();
		
		if( what.equalsIgnoreCase( "all" ) ) reset();
		else if( what.equalsIgnoreCase( "rules" ) ) {
			String which = t.nextWord();
			if( which.equalsIgnoreCase( "ignore" ) ) ignoreRules.clear();
			else if( which.equalsIgnoreCase( "take" ) ) takeRules.clear();
			else if( which.equalsIgnoreCase( "sync" ) ) syncRules.clear();
			else if( which.equalsIgnoreCase( "all" ) ) { ignoreRules.clear(); takeRules.clear(); syncRules.clear(); }
			else throw new DataParseException( "Unknown Identifier \""+which+"\" in \""+t.getSourceName()+"\" in line "+t.lineno() );
		}
		else throw new DataParseException( "Unknown Identifier \""+what+"\" in \""+t.getSourceName()+"\" in line "+t.lineno() );
	}

	protected void processSyncRule( String cmd, SyncTokenizer t ) throws DataParseException
	{
		if( cmd.equalsIgnoreCase( "use" ) ) processUseCommand( t );
		else if( cmd.equalsIgnoreCase( "set" ) ) processSetCommand( t );
		else if( cmd.equalsIgnoreCase( "apply" ) ) processApplyCommand( t );
		else if( cmd.equalsIgnoreCase( "define" ) ) processDefineCommand( t );
		else if( cmd.equalsIgnoreCase( "reset" ) ) processResetCommand( t );
		else throw new DataParseException( "Unknown Command \""+cmd+"\" in \""+t.getSourceName()+"\" in line "+t.lineno() );
	}

	protected void processSyncRules( SyncTokenizer t ) throws FileSystemException, DataParseException
	{
		t.eolIsSignificant(false);
		String cmd;
		processAllowed = true;
		while( (cmd = t.nextWord()) != null )
		{
			if( cmd.equals( "start" ) ) {
				if( t.nextWord().equals( "ruleset" ) )
				{
					String name = t.nextString();
					if( (ruleSet == null) || !name.equals( ruleSet.toLowerCase() ) )
						processAllowed = false;
				}
			} else if( cmd.equalsIgnoreCase( "end" ) ) {
				if( t.nextWord().equals( "ruleset" ) )
				{
					String name = t.nextString();
					if( (ruleSet == null) || !name.equals( ruleSet ) )
						processAllowed = true;
				}
			} else {
				if( processAllowed )
				{
					processSyncRule( cmd, t );
				}
			}
			t.finishStatement();
		}
			
	}

    public void processRules( InputStream in, String filename ) 
    	throws FileSystemException, DataParseException
    {
        processSyncRules( new SyncTokenizer( new InputStreamReader( in ), filename ) );
    }

    
}
