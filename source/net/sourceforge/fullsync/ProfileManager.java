package net.sourceforge.fullsync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.fullsync.remote.RemoteManager;
import net.sourceforge.fullsync.schedule.CrontabSchedule;
import net.sourceforge.fullsync.schedule.IntervalSchedule;
import net.sourceforge.fullsync.schedule.Schedule;
import net.sourceforge.fullsync.schedule.ScheduleTask;
import net.sourceforge.fullsync.schedule.ScheduleTaskSource;
import net.sourceforge.fullsync.schedule.Scheduler;
import net.sourceforge.fullsync.schedule.SchedulerChangeListener;
import net.sourceforge.fullsync.schedule.SchedulerImpl;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


// TODO remove schedulerChangeListener
/**
 * A ProfileManager handles persistence of Profiles and provides
 * a scheduler for creating events when a Profile should be executed.
 *  
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ProfileManager 
	implements ProfileChangeListener, ScheduleTaskSource, SchedulerChangeListener
{
	class ProfileManagerSchedulerTask implements ScheduleTask
	{
		private Profile profile;
		private long executionTime;
		public ProfileManagerSchedulerTask( Profile profile, long executionTime ) 
		{
			this.profile = profile;
			this.executionTime = executionTime;
		}
		public void run() 
		{
			Thread worker = new Thread( new Runnable() {
		        public void run()
	            {
		        	fireProfileSchedulerEvent( profile );
	            }
			} );
			worker.start();
			profile.getSchedule().setLastOccurrence( System.currentTimeMillis() );
			Thread.yield();
		}
		public long getExecutionTime()
        {
            return executionTime;
        }
		public String toString()
		{
		    return "Scheduled execution of "+profile.getName();
		}
	}
	class ProfileComparator implements Comparator
	{
	    public int compare( Object o1, Object o2 )
        {
            Profile p1 = (Profile)o1;
            Profile p2 = (Profile)o2;
            return p1.getName().compareTo( p2.getName() );
        }
	}
	
    private String configFile;
    protected Vector profiles;
    private Vector changeListeners;
    private Vector scheduleListeners;

    // FIXME this list is only needed because we need to give feedback from
    //		 the local scheduler and a remote scheduler.
    private Vector schedulerChangeListeners;

    // TODO  the scheduler shouldn't reside within the profile manager
    //       but just use it as task source
    private Scheduler scheduler;

    // FIXME omg, a profilemanager having a remoteprofilemanager?
    //       please make a dao of me, with save/load and that's it
    //       don't forget calling profilesChangeEvent if dao is changed
    private RemoteManager remoteManager;
    private ProfileListChangeListener remoteListener;
    
    protected ProfileManager() 
    {
        this.profiles = new Vector();
        this.changeListeners = new Vector();
        this.scheduleListeners = new Vector();
        this.schedulerChangeListeners = new Vector();
        this.scheduler = new SchedulerImpl( this );
        this.scheduler.addSchedulerChangeListener( this );
    }
    
    public ProfileManager( String configFile ) throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError
    {
        this();
        this.configFile = configFile;

        loadProfiles();
        Collections.sort( profiles, new ProfileComparator() );
        /*
        Digester dig = new Digester();
        dig.push( this );
        dig.addObjectCreate ( "Profiles/Profile", Profile.class );
        dig.addSetProperties( "Profiles/Profile" );
        dig.addObjectCreate ( "Profiles/Profile/Source", ConnectionDescription.class );
        dig.addSetProperties( "Profiles/Profile/Source" );
        dig.addCallMethod   ( "Profiles/Profile/Source/Param", "setParameter" );
        dig.addCallParam    ( "Profiles/Profile/Source/Param", 0, "name" );
        dig.addCallParam    ( "Profiles/Profile/Source/Param", 1 );
        dig.addSetNext      ( "Profiles/Profile/Destination", "setSource" );
        dig.addObjectCreate ( "Profiles/Profile/Destination", ConnectionDescription.class );
        dig.addSetProperties( "Profiles/Profile/Destination" );
        dig.addCallMethod   ( "Profiles/Profile/Destination/Param", "setParameter" );
        dig.addCallParam    ( "Profiles/Profile/Destination/Param", 0, "name" );
        dig.addCallParam    ( "Profiles/Profile/Destination/Param", 1 );
        dig.addSetNext      ( "Profiles/Profile/Destination", "setDestination" );
        dig.addSetNext      ( "Profiles/Profile", "addProfile" );
        try {
            dig.parse( configFile );
        } catch( IOException e ) {
            ExceptionHandler.reportException( e );
        } catch( SAXException e ) {
            e.getException().printStackTrace();
        }
        */
    }
    
    public void setRemoteConnection(RemoteManager remoteManager) 
    	throws MalformedURLException, RemoteException, NotBoundException
	{
    	this.remoteManager = remoteManager;
    	
    	remoteListener = new ProfileListChangeListener() {
			public void profileListChanged() {
				updateRemoteProfiles();
			}

			public void profileChanged(Profile p) {
//				ProfileManager.this.profileChanged(p);
				updateRemoteProfiles();
			}
    	};
    	remoteManager.addProfileListChangeListener(remoteListener);
    	remoteManager.addSchedulerChangeListener(this);
    	updateRemoteProfiles();
    	fireSchedulerChangedEvent();
    }
    
    private void updateRemoteProfiles() {
    	this.profiles = new Vector();
		
    	Profile[] remoteprofiles = remoteManager.getProfiles();
		for (int i = 0; i < remoteprofiles.length; i++) {
			this.profiles.add(remoteprofiles[i]);
			remoteprofiles[i].addProfileChangeListener(this);
		}

		fireProfilesChangeEvent();    	
    }	
    
    public void disconnectRemote() {

    	if (remoteManager != null) {
	    	try {
				remoteManager.removeProfileListChangeListener(remoteListener);
				remoteManager.removeSchedulerChangeListener(this);
			} catch (RemoteException e) {
				ExceptionHandler.reportException( e );
			}
	    	remoteManager = null;

	        this.profiles = new Vector();
	        
	        try {
				loadProfiles();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ExceptionHandler.reportException( e );
			}
			fireProfilesChangeEvent();
    	}
    }
    
    public boolean isConnected() {
    	return (remoteManager != null);
    }
    
    private void loadProfiles() throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError 
    {
        File file = new File( configFile );
        if( file.exists() )
        {
	        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = builder.parse( file );
	        
	        NodeList list = doc.getDocumentElement().getChildNodes();
	        for( int i = 0; i < list.getLength(); i++ )
	        {
	            Node n = list.item( i );
	            if( n.getNodeType() == Node.ELEMENT_NODE ) 
	            {
	            	Profile profile = unserializeProfile( (Element)n );
	            	profiles.add( profile );
	                profile.addProfileChangeListener( this );
	            }
	        }
        }

    }
    
    public void addProfile( Profile profile )
    {
        profiles.add( profile );
        profile.addProfileChangeListener( this );
        fireProfilesChangeEvent();
    }
    public void addProfile( String name, ConnectionDescription source, ConnectionDescription destination, RuleSetDescriptor ruleSet, String lastUpdate )
    {
        Date date;
        try {
            date = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT ).parse( lastUpdate );
        } catch( ParseException e ) {
            date = new Date();
        }
        addProfile( new Profile( name, source, destination, ruleSet, date ) );
    }
    public void removeProfile( Profile profile )
    {
        profile.removeProfileChangeListener( this );
        profiles.remove( profile );
        fireProfilesChangeEvent();
    }
    public Enumeration getProfiles()
    {
        return profiles.elements();
    }
    public Profile getProfile( String name )
    {
        for( int i = 0; i < profiles.size(); i++ )
        {
            Profile p = (Profile)profiles.get( i );
            if( p.getName().equals( name ) )
                return p;
        }
        return null;
    }
    public void startScheduler()
    {
    	if (remoteManager != null) 
    	{
    		remoteManager.startTimer();
    	} else {
    		scheduler.start();
    	}
    }
    public void stopScheduler()
    {
    	if (remoteManager != null) 
    	{
    		remoteManager.stopTimer();
    	} else {
    		scheduler.stop();
    	}
    }
    public boolean isSchedulerEnabled()
    {
        if( remoteManager != null )
        {
            return remoteManager.isSchedulerEnabled();
        } else {
            return scheduler.isEnabled();
        }
    }
    public ScheduleTask getNextScheduleTask()
    {
        long now = System.currentTimeMillis();
    	long nextTime = Long.MAX_VALUE;
    	Profile nextProfile = null;
    	
    	Enumeration e = profiles.elements();
    	while( e.hasMoreElements() )
    	{
    		Profile p = (Profile)e.nextElement();
    		Schedule s = p.getSchedule();
    		if( p.isEnabled() && s != null )
    		{
    			long o = s.getNextOccurrence( now );
    			if( nextTime > o )
    			{
    				nextTime = o;
    				nextProfile = p;
    			}
    		}
    	}
    	
    	if( nextProfile != null )
    	{
    		return new ProfileManagerSchedulerTask( nextProfile, nextTime );
    	} else {
    	    return null;
    	}
    }
    public void addProfilesChangeListener( ProfileListChangeListener listener )
    {
        changeListeners.add( listener );
    }
    public void removeProfilesChangeListener( ProfileListChangeListener listener )
    {
        changeListeners.remove( listener );
    }
    protected void fireProfilesChangeEvent()
    {
        for( int i = 0; i < changeListeners.size(); i++ )
            ((ProfileListChangeListener)changeListeners.get( i )).profileListChanged();
    }
    public void profileChanged( Profile profile )
    {
        scheduler.refresh();
        for( int i = 0; i < changeListeners.size(); i++ )
            ((ProfileListChangeListener)changeListeners.get( i )).profileChanged( profile );
    }
    public void addSchedulerListener( ProfileSchedulerListener listener )
    {
        scheduleListeners.add( listener );
    }
    public void removeSchedulerListener( ProfileSchedulerListener listener )
    {
        scheduleListeners.remove( listener );
    }    
    protected void fireProfileSchedulerEvent( Profile profile  )
    {
        for( int i = 0; i < scheduleListeners.size(); i++ )
            ((ProfileSchedulerListener)scheduleListeners.get( i )).profileExecutionScheduled( profile );
    }
    /*
    public void addSchedulerChangeListener(SchedulerChangeListener listener) {
    	scheduler.addSchedulerChangeListener(listener);
    }
    public void removeSchedulerChangeListener(SchedulerChangeListener listener) {
    	scheduler.removeSchedulerChangeListener(listener);
    }
    */
    public void schedulerStatusChanged( boolean status )
    {
        fireSchedulerChangedEvent();
    }
    public void addSchedulerChangeListener(SchedulerChangeListener listener) 
	{
    	schedulerChangeListeners.add(listener);
    }
    public void removeSchedulerChangeListener(SchedulerChangeListener listener) 
    {
    	schedulerChangeListeners.remove(listener);
    }
    protected void fireSchedulerChangedEvent() 
    {
        boolean enabled = isSchedulerEnabled();
    	for (int i = 0; i < schedulerChangeListeners.size(); i++) {
    		((SchedulerChangeListener)schedulerChangeListeners.get(i)).schedulerStatusChanged(enabled);
    	}
    }
    protected ConnectionDescription unserializeConnectionDescription( Element element )
    {
        ConnectionDescription desc = new ConnectionDescription();
        desc.setUri( element.getAttribute( "uri" ) );
        String value; 
        value = element.getAttribute( "buffer" );
        if( value.length() > 0 )
            desc.setBufferStrategy( value );
        value = element.getAttribute( "username" );
        if( value.length() > 0 )
            desc.setUsername( value );
        value = element.getAttribute( "password" );
        if( value.length() > 0 )
            desc.setCryptedPassword( value );
        
        NodeList list = element.getChildNodes();
        for( int i = 0; i < list.getLength(); i++ )
        {
            Node n = list.item( i );
            if( n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals( "Param" ) )
            {
                Element e = (Element)n;
                desc.setParameter( e.getAttribute( "name" ), e.getAttribute( "value" ) );
            }
        }
        
        return desc;
    }
    protected Schedule unserializeSchedule( Element element )
    {
    	if( element == null )
    		return null;
    	Schedule schedule = null;
    	String type = element.getAttribute( "type" );
    	if( type.equals( "interval" ) )
    	{
    		long firstSpan = element.hasAttribute( "firstinterval" )?Long.parseLong( element.getAttribute("firstinterval") ):0;
    		long span = Long.parseLong( element.getAttribute( "interval" ) );
    		schedule = new IntervalSchedule( firstSpan, span );
    	} else if( type.equals( "crontab" ) ) {
    	    try {
    	        schedule = new CrontabSchedule( element.getAttribute("pattern" ) );
    	    } catch( Exception ex ) {
    	        ExceptionHandler.reportException( ex );
    	    }
    	}
    	return schedule;
    }
    
    protected RuleSetDescriptor unserializeRuleSetDescriptor(Element element) {
    	RuleSetDescriptor descriptor = RuleSetDescriptor.unserialize(element);
    	return descriptor;
    }
    
    protected Profile unserializeProfile( Element element )
    {
        Profile p = new Profile();
        p.setName( element.getAttribute( "name" ) );
        p.setDescription( element.getAttribute( "description" ) );
        p.setSynchronizationType( element.getAttribute( "type" ) );
        if( element.hasAttribute( "enabled" ) ) 
            p.setEnabled( Boolean.valueOf( element.getAttribute( "enabled" ) ).booleanValue() );
        if( element.hasAttribute( "lastErrorLevel" ) )
            p.setLastError( 
                    Integer.parseInt( element.getAttribute( "lastErrorLevel" ) ), 
                    element.getAttribute("lastErrorString") );
        
        
        try {
            p.setLastUpdate( DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT ).parse( element.getAttribute( "lastUpdate" ) ) );
        } catch( ParseException e ) {
            p.setLastUpdate( new Date() );
        }
        
        p.setRuleSet( unserializeRuleSetDescriptor( (Element)element.getElementsByTagName("RuleSetDescriptor").item(0) ) );
        p.setSchedule( unserializeSchedule( (Element)element.getElementsByTagName( "Schedule" ).item(0) ) );
        p.setSource( unserializeConnectionDescription( (Element)element.getElementsByTagName( "Source" ).item(0) ) );
        p.setDestination( unserializeConnectionDescription( (Element)element.getElementsByTagName( "Destination" ).item(0) ) );
        return p;
    }
    protected Element serialize( ConnectionDescription desc, String name, Document doc )
    {
        Element elem = doc.createElement( name );
        elem.setAttribute( "uri", desc.getUri().toString() );
        if( desc.getBufferStrategy() != null )
            elem.setAttribute( "buffer", desc.getBufferStrategy() );
        if( desc.getUsername() != null )
            elem.setAttribute( "username", desc.getUsername() );
        if( desc.getPassword() != null )
            elem.setAttribute( "password", desc.getCryptedPassword() );
            
        Dictionary params = desc.getParameters();
        Enumeration e = params.keys();
        while( e.hasMoreElements() )
        {
            String key = (String)e.nextElement();
            Element p = doc.createElement( "Param" );
            p.setAttribute( "name", key );
            p.setAttribute( "value", (String)params.get( key ) );
            elem.appendChild( p );
        }
        
        return elem;
    }
    protected Element serialize( Schedule schedule, String name, Document doc )
    {
        Element element = doc.createElement( name );

    	if( schedule instanceof IntervalSchedule )
    	{
    	    IntervalSchedule is = (IntervalSchedule)schedule;
            element.setAttribute( "type", "interval" );
            element.setAttribute( "firstinterval", String.valueOf( is.getFirstInterval() ) );
            element.setAttribute( "interval", String.valueOf( is.getInterval() ) );
    	} else if( schedule instanceof CrontabSchedule ) {
    	    CrontabSchedule cs = (CrontabSchedule)schedule;
    	    element.setAttribute( "type", "crontab" );
            element.setAttribute( "pattern", cs.getPattern() );
    	}
    	return element;
    }
    protected Element serialize( RuleSetDescriptor desc, String name, Document doc ) 
    {
    	Element elem = doc.createElement( name );

    	elem.setAttribute("type", desc.getType());
    	Element ruleDescriptorElement = desc.serialize(doc);
		elem.appendChild(ruleDescriptorElement);
    	
    	return elem;
    }
    protected Element serialize( Profile p, Document doc)
    {
        Element elem = doc.createElement( "Profile" );
        Element e;
        
        elem.setAttribute( "name", p.getName() );
        elem.setAttribute( "description", p.getDescription() );
        elem.setAttribute( "type", p.getSynchronizationType() );
        elem.setAttribute( "enabled", String.valueOf( p.isEnabled() ) );
        elem.setAttribute( "lastErrorLevel", String.valueOf( p.getLastErrorLevel() ) );
        elem.setAttribute( "lastErrorString", p.getLastErrorString() );
        elem.setAttribute( "lastUpdate", DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT ).format( p.getLastUpdate() ) );
        
        elem.appendChild( serialize( p.getRuleSet(), "RuleSetDescriptor", doc) );
        elem.appendChild( serialize( p.getSchedule(), "Schedule", doc ) );
        elem.appendChild( serialize( p.getSource(), "Source", doc ) );
        elem.appendChild( serialize( p.getDestination(), "Destination", doc ) );
        
        return elem;
    }
    public void save()// throws ParserConfigurationException, FactoryConfigurationError, IOException
    {
    	if (remoteManager != null) {
    		try {
        		remoteManager.removeProfileListChangeListener(remoteListener);
        		remoteManager.save((Profile[]) profiles.toArray(new Profile[0]));
        		remoteManager.addProfileListChangeListener(remoteListener);
    		} catch (RemoteException e) {
    			ExceptionHandler.reportException( e );
    		}
    	}
    	else {
    		try {
    			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    			Document doc = docBuilder.newDocument();
	    			
    			Element e = doc.createElement( "Profiles" );
    			e.setAttribute("version", "1.0");
    			Enumeration en = profiles.elements();
    			while( en.hasMoreElements() )
    			{
    				e.appendChild( serialize( (Profile)en.nextElement(), doc ) );
    			}
    			doc.appendChild( e );
    			
    			OutputStream out = new FileOutputStream( configFile );
    			
    			OutputFormat format = new OutputFormat( doc, "UTF-8", true );
    			XMLSerializer serializer = new XMLSerializer ( out, format);
    			serializer.asDOMSerializer();
    			serializer.serialize(doc);
	        
    			out.close();
    		} catch( Exception e ) {
    			// TODO messagebox ?
    			ExceptionHandler.reportException( e );
    		}
    	}
    }
}
