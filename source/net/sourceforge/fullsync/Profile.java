package net.sourceforge.fullsync;

import java.net.URI;
import java.util.Date;

/**
 * @author codewright
 */
public class Profile
{
    private String name;
    private URI source;
    private URI destination;
    private String ruleSet;
    private Date lastUpdate;
    
    public Profile( String name, URI source, URI destination, String ruleSet, Date lastUpdate )
    {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.ruleSet = ruleSet;
        this.lastUpdate = lastUpdate;
    }
    
    public URI getDestination()
    {
        return destination;
    }
    public void setDestination( URI destination )
    {
        this.destination = destination;
    }
    public Date getLastUpdate()
    {
        return lastUpdate;
    }
    public void setLastUpdate( Date lastUpdate )
    {
        this.lastUpdate = lastUpdate;
    }
    public String getName()
    {
        return name;
    }
    public void setName( String name )
    {
        this.name = name;
    }
    public String getRuleSet()
    {
        return ruleSet;
    }
    public void setRuleSet( String ruleSet )
    {
        this.ruleSet = ruleSet;
    }
    public URI getSource()
    {
        return source;
    }
    public void setSource( URI source )
    {
        this.source = source;
    }
}
