package net.sourceforge.fullsync;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import net.sourceforge.fullsync.schedule.Schedule;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Profile implements Serializable
{
    private String name;
    private String description;
    private String synchronizationType;
    private ConnectionDescription source;
    private ConnectionDescription destination;
    private RuleSetDescriptor ruleSet;
    private Date lastUpdate;
    private Schedule schedule;
    
    private boolean enabled;
    private int lastErrorLevel;
    private String lastErrorString;
    //private int status;

    private transient ArrayList listeners;
    
    public Profile()
    {
        this.listeners = new ArrayList();
        this.enabled = true;
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
        this.enabled = true;
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
    public String getNextUpdate()
    {
        if( schedule == null ) {
            return "not scheduled";
        } else if( !enabled ) {
            return "not enabled";
        } else {
            //if( lastUpdate == null )
                 return new Date( schedule.getNextOccurrence(new Date().getTime()) ).toString();
            //else return new Date( schedule.getNextOccurrence(lastUpdate.getTime()) ).toString();
        }
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
    public boolean isEnabled()
    {
        return enabled;
    }
    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
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
	public void setLastError( int lastErrorLevel, String lastErrorString )
    {
        this.lastErrorLevel = lastErrorLevel;
        this.lastErrorString = lastErrorString;
        notifyProfileChangeListeners();
    }
	public int getLastErrorLevel()
    {
        return lastErrorLevel;
    }
	public String getLastErrorString()
    {
        return lastErrorString;
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
	
	private void writeObject(java.io.ObjectOutputStream out)
		throws IOException
	{
		out.writeObject(name);
	    out.writeObject(description);
	    out.writeObject(synchronizationType);
	    out.writeObject(source);
	    out.writeObject(destination);
	    out.writeObject(ruleSet);
	    out.writeObject(lastUpdate);
	    out.writeObject(schedule);
	    out.writeBoolean(enabled);
	    out.writeInt(lastErrorLevel);
	    out.writeObject(lastErrorString);
	}

	private void readObject(java.io.ObjectInputStream in)
    	throws IOException, ClassNotFoundException
	{
		name = (String) in.readObject();
		description = (String) in.readObject();
		synchronizationType = (String) in.readObject();
		source = (ConnectionDescription) in.readObject();
		destination = (ConnectionDescription) in.readObject();
		ruleSet = (RuleSetDescriptor) in.readObject();
		lastUpdate = (Date) in.readObject();
		schedule = (Schedule) in.readObject();
	    enabled = in.readBoolean();
	    lastErrorLevel = in.readInt();
	    lastErrorString = (String) in.readObject();
		
        this.listeners = new ArrayList();
	}
}
