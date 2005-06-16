package net.full.fullsync.sync.files;

import net.full.fullsync.sync.Element;
import net.sourceforge.fullsync.fs.Site;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FileSyncInputElement implements Element
{
    private Site source;
    private Site destination;
    private RulesProvider rulesProvider;

    public FileSyncInputElement( Site source, Site destination, RulesProvider rulesProvider )
    {
        this.source = source;
        this.destination = destination;
        this.rulesProvider = rulesProvider;
    }
    
    public Site getSource()
    {
        return source;
    }
    public Site getDestination()
    {
        return destination;
    }
    public RulesProvider getRulesProvider()
    {
        return rulesProvider;
    }
}
