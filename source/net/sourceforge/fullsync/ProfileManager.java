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
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.fullsync.remote.RemoteManager;
import net.sourceforge.fullsync.schedule.CrontabSchedule;
import net.sourceforge.fullsync.schedule.IntervalSchedule;
import net.sourceforge.fullsync.schedule.Schedule;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A ProfileManager handles persistence of Profiles and provides
 * a scheduler for creating events when a Profile should be executed.
 *  
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ProfileManager implements ProfileChangeListener
{
	class ProfileManagerTimerTask extends TimerTask
	{
		private Profile profile;
		public ProfileManagerTimerTask( Profile p ) 
		{
			this.profile = p;
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
			profile.getSchedule().update();
			updateTimer();
		}
	}
	
	
    private String configFile;
    protected Vector profiles;
    private Vector changeListeners;
    private Vector scheduleListeners;
    private Vector timerListeners;
    private Timer timer;
    private boolean timerActive;

    // FIXME omg, a profilemanager having a remoteprofilemanager?
    //       please make a dao of me, with save/load and that's it
    //       don't forget calling profilesChangeEvent if dao is changed
    private RemoteManager remoteManager;
    private ProfileListChangeListener remoteListener;
    private Timer updateTimer;
    
    protected ProfileManager() {
        this.changeListeners = new Vector();
        this.scheduleListeners = new Vector();
        this.timerListeners = new Vector();
        this.timerActive = false;
    }
    
    public ProfileManager( String configFile ) throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError
    {
        this.configFile = configFile;
        this.profiles = new Vector();
        this.changeListeners = new Vector();
        this.scheduleListeners = new Vector();
        this.timerListeners = new Vector();
        this.timerActive = false;
        
        loadProfiles();
        
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
    	updateRemoteProfiles();
    	
//    	updateTimer = new Timer(true);
//    	updateTimer.scheduleAtFixedRate(new TimerTask() {
//			public void run() {
//				updateRemoteProfiles();
//			}
//    	}, 0, 1000);
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
    	try {
			remoteManager.removeProfileListChangeListener(remoteListener);
		} catch (RemoteException e) {
			ExceptionHandler.reportException( e );
		}
    	
    	remoteManager = null;
    	
    	if (updateTimer != null) {
    		updateTimer.cancel();
    		updateTimer = null;
    	}
    	
        this.profiles = new Vector();
        
        try {
			loadProfiles();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			ExceptionHandler.reportException( e );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ExceptionHandler.reportException( e );
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			ExceptionHandler.reportException( e );
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			ExceptionHandler.reportException( e );
		}
		
        fireProfilesChangeEvent();
    }
    
    public boolean isConnected() {
    	return (remoteManager != null);
    }
    
    private void loadProfiles() throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
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
	            	Profile p = unserializeProfile( (Element)n );
	                addProfile( p );
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
    public void startTimer()
    {
    	if (remoteManager != null) {
    		remoteManager.startTimer();
    		timerActive = true;
    	}
    	else {
    		timerActive = true;
    		timer = new Timer( true);
    		updateTimer();
    	}
    	fireTimerChangedEvent(timerActive);
    }
    void updateTimer()
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
    		timer.schedule( 
    				new ProfileManagerTimerTask( nextProfile ),
					new Date( nextTime ) );
    	}
    }
    public boolean isTimerEnabled()
    {
        return timerActive;
    }
    public void stopTimer()
    {
    	if (remoteManager != null) {
    		remoteManager.stopTimer();
			timerActive = false;
    	}
    	else {
    		if( timerActive )
    		{
    			timerActive = false;
    			timer.cancel();
    		}
    	}
    	fireTimerChangedEvent(timerActive);
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
        if( isTimerEnabled() )
        {
            stopTimer();
            startTimer();
        }
        
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
    public void addTimerListener(TimerUpdateListener listener) {
    	timerListeners.add(listener);
    }
    public void removeTimerListener(TimerUpdateListener listener) {
    	timerListeners.remove(listener);
    }
    protected void fireTimerChangedEvent(boolean status) {
    	for (int i = 0; i < timerListeners.size(); i++) {
    		((TimerUpdateListener)timerListeners.get(i)).timerStatusChanged(status);
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
