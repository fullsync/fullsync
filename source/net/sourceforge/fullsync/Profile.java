package net.sourceforge.fullsync;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import net.sourceforge.fullsync.schedule.Schedule;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Profile
{
    private String name;
    private String description;
    private String synchronizationType;
    private ConnectionDescription source;
    private ConnectionDescription destination;
    private RuleSetDescriptor ruleSet;
    private Date lastUpdate;
    private Schedule schedule;
    
    private ArrayList listeners;
    
    public Profile()
    {
        this.listeners = new ArrayList();
    }
    public Profile( String name, ConnectionDescription source, ConnectionDescription destination, RuleSetDescriptor ruleSet )
    {
        this( name, source, destination, ruleSet, new Date() );
    }
    public Profile( String name, ConnectionDescription source, ConnectionDescription destination, RuleSetDescriptor ruleSet, Date lastUpdate )
    {
        this.name = name;
        this.description = "";
        this.source = source;
        this.destination = destination;
        this.ruleSet = ruleSet;
        this.lastUpdate = lastUpdate;
        this.listeners = new ArrayList();
    }
    public String getName()
    {
        return name;
    }
    public void setName( String name )
    {
        this.name = name;
        notifyProfileChangeListeners();
    }
    public String getSynchronizationType()
    {
        return synchronizationType;
    }
    public void setSynchronizationType( String synchronizationType )
    {
        this.synchronizationType = synchronizationType;
        notifyProfileChangeListeners();
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription( String description )
    {
        this.description = description;
        notifyProfileChangeListeners();
    }
    public ConnectionDescription getDestination()
    {
        return destination;
    }
    public void setDestination( ConnectionDescription destination )
    {
        this.destination = destination;
        notifyProfileChangeListeners();
    }
    public ConnectionDescription getSource()
    {
        return source;
    }
    public void setSource( ConnectionDescription source )
    {
        this.source = source;
        notifyProfileChangeListeners();
    }
    public Date getLastUpdate()
    {
        return lastUpdate;
    }
    public void setLastUpdate( Date lastUpdate )
    {
        this.lastUpdate = lastUpdate;
        notifyProfileChangeListeners();
    }
    public RuleSetDescriptor getRuleSet()
    {
        return ruleSet;
    }
    public void setRuleSet( RuleSetDescriptor ruleSet )
    {
        this.ruleSet = ruleSet;
        notifyProfileChangeListeners();
    }

    public Schedule getSchedule() 
	{
		return schedule;
	}
	public void setSchedule(Schedule schedule) 
	{
		this.schedule = schedule;
		notifyProfileChangeListeners();
	}
	public void addProfileChangeListener( ProfileChangeListener listener )
	{
	    listeners.add( listener );
	}
	public void removeProfileChangeListener( ProfileChangeListener listener )
	{
	    listeners.remove( listener );
	}
	protected void notifyProfileChangeListeners()
	{
	    Iterator i = listeners.iterator();
	    while( i.hasNext() )
	    {
	        ((ProfileChangeListener)i.next()).profileChanged( this );
	    }
	}
}
