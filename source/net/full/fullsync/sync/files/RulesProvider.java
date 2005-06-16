package net.full.fullsync.sync.files;

import net.sourceforge.fullsync.RuleSet;

public interface RulesProvider
{
    public RuleSet getRuleSet( String relativePath );
}