package net.sourceforge.fullsync;

import java.util.Date;

import net.sourceforge.fullsync.schedule.Schedule;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Profile
{
    private String name;
    private ConnectionDescription source;
    private ConnectionDescription destination;
    private String ruleSet;
    private Date lastUpdate;
    private Schedule schedule;
    
    public Profile()
    {
    }
    public Profile( String name, ConnectionDescription source, ConnectionDescription destination, String ruleSet )
    {
        this( name, source, destination, ruleSet, new Date() );
    }
    public Profile( String name, ConnectionDescription source, ConnectionDescription destination, String ruleSet, Date lastUpdate )
    {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.ruleSet = ruleSet;
        this.lastUpdate = lastUpdate;
    }
    
    public ConnectionDescription getDestination()
    {
        return destination;
    }
    public void setDestination( ConnectionDescription destination )
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
    public ConnectionDescription getSource()
    {
        return source;
    }
    public void setSource( ConnectionDescription source )
    {
        this.source = source;
    }
    public Schedule getSchedule() 
	{
		return schedule;
	}
	public void setSchedule(Schedule schedule) 
	{
		this.schedule = schedule;
	}
}
