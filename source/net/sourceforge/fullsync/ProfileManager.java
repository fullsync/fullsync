package net.sourceforge.fullsync;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ProfileManager
{
    private Hashtable profiles;
    
    public ProfileManager( String configFile )
    {
        profiles = new Hashtable();
        
        Digester dig = new Digester();
        
        dig.push( this );
        
        dig.addCallMethod( "Profiles/Profile", "addProfile", 5 );
        dig.addCallParam( "Profiles/Profile/Name", 0 );
        dig.addCallParam( "Profiles/Profile/Source", 1 );
        dig.addCallParam( "Profiles/Profile/Destination", 2 );
        dig.addCallParam( "Profiles/Profile/RuleSet", 3 );
        dig.addCallParam( "Profiles/Profile/LastUpdate", 4 );
        
        try {
            dig.parse( configFile );
        } catch( IOException e ) {
            e.printStackTrace();
        } catch( SAXException e ) {
            e.printStackTrace();
        }
    }
    public void addProfile( String name, String source, String destination, String ruleSet, String lastUpdate )
    {
        try {
            Profile p = new Profile( name, new URI( source ), new URI( destination ), ruleSet, DateFormat.getInstance().parse( lastUpdate ) );
            profiles.put( name, p );
        } catch( URISyntaxException e ) {
            e.printStackTrace();
        } catch( ParseException e ) {
            e.printStackTrace();
        }
    }
    public Enumeration getProfiles()
    {
        return profiles.elements();
    }
    public Profile getProfile( String name )
    {
        return (Profile)profiles.get( name );
    }
}
