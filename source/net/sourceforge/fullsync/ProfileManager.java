package net.sourceforge.fullsync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

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
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ProfileManager
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
			System.out.println( "Running profile: "+profile.getName());
			Thread worker = new Thread( new Runnable() {
		        public void run()
	            {
		        	FullSync.getInstance().executeProfile( profile, false );
	            }
			} );
			worker.start();
			profile.getSchedule().update();
			updateTimer();
		}
	}
	
	
    private String configFile;
    private Hashtable profiles;
    private Vector listeners;
    private Timer timer;
    private boolean timerActive;
    
    public ProfileManager( String configFile ) throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError
    {
        this.configFile = configFile;
        this.profiles = new Hashtable();
        this.listeners = new Vector();
        this.timerActive = false;
        
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse( new File( configFile ) );
        
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
            e.printStackTrace();
        } catch( SAXException e ) {
            e.getException().printStackTrace();
        }
        */
    }
    public void addProfile( Profile p )
    {
        profiles.put( p.getName(), p );
        fireChange();
    }
    public void addProfile( String name, ConnectionDescription source, ConnectionDescription destination, String ruleSet, String lastUpdate )
    {
        Profile p;
        try {
            p = new Profile( name, source, destination, ruleSet, DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT ).parse( lastUpdate ) );
        } catch( ParseException e ) {
            p = new Profile( name, source, destination, ruleSet, new Date() );
            e.printStackTrace();
        }
        profiles.put( name, p );
        fireChange();
    }
    public void removeProfile( String name )
    {
        profiles.remove( name );
        fireChange();
    }
    public Enumeration getProfiles()
    {
        return profiles.elements();
    }
    public Profile getProfile( String name )
    {
        return (Profile)profiles.get( name );
    }
    public void startTimer()
    {
    	timerActive = true;
    	timer = new Timer();
    	updateTimer();
    }
    void updateTimer()
    {
    	long nextTime = Long.MAX_VALUE;
    	Profile nextProfile = null;
    	
    	Enumeration e = profiles.elements();
    	while( e.hasMoreElements() )
    	{
    		Profile p = (Profile)e.nextElement();
    		Schedule s = p.getSchedule();
    		if( s != null )
    		{
    			long o = s.getNextOccurrence();
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
        if( timerActive )
        {
            timerActive = false;
            timer.cancel();
        }
    }
    public void addChangeListener( ProfilesChangeListener list )
    {
        listeners.add( list );
    }
    public void removeChangeListener( ProfilesChangeListener list )
    {
        listeners.remove( list );
    }
    protected void fireChange()
    {
        for( int i = 0; i < listeners.size(); i++ )
            ((ProfilesChangeListener)listeners.get( i )).profilesChanged();
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
    		long firstSpan = element.hasAttribute( "firstspan" )?Long.parseLong( element.getAttribute("firstspan") ):0;
    		long span = Long.parseLong( element.getAttribute( "span" ) );
    		schedule = new IntervalSchedule( firstSpan, span );
    	}
    	return schedule;
    }
    protected Profile unserializeProfile( Element element )
    {
        Profile p = new Profile();
        p.setName( element.getAttribute( "name" ) );
        p.setRuleSet( element.getAttribute( "ruleSet" ) );
        try {
            p.setLastUpdate( DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT ).parse( element.getAttribute( "lastUpdate" ) ) );
        } catch( ParseException e ) {
            p.setLastUpdate( new Date() );
        }
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
    protected Element serialize( Profile p, Document doc)
    {
        Element elem = doc.createElement( "Profile" );
        Element e;
        
        elem.setAttribute( "name", p.getName() );
        elem.setAttribute( "ruleSet", p.getRuleSet() );
        elem.setAttribute( "lastUpdate", DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT ).format( p.getLastUpdate() ) );
        
        elem.appendChild( serialize( p.getSource(), "Source", doc ) );
        elem.appendChild( serialize( p.getDestination(), "Destination", doc ) );
        
        return elem;
    }
    public void save()// throws ParserConfigurationException, FactoryConfigurationError, IOException
    {
        try {
	        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = docBuilder.newDocument();
	
	        Element e = doc.createElement( "Profiles" );
	        Enumeration enum = profiles.elements();
	        while( enum.hasMoreElements() )
	        {
	            e.appendChild( serialize( (Profile)enum.nextElement(), doc ) );
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
            e.printStackTrace();
        }
    }
}
