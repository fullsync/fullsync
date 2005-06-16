package net.full.fullsync.sync.files;

import net.full.fullsync.sync.files.RulesProvider;
import net.sourceforge.fullsync.RuleSet;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SimpleFileSyncRulesProvider implements RulesProvider
{
    private RuleSet ruleSet;
    
    public SimpleFileSyncRulesProvider( RuleSet ruleSet )
    {
        this.ruleSet = ruleSet;
    }
    
    public RuleSet getRuleSet( String relativePath )
    {
         return ruleSet;
    }
}