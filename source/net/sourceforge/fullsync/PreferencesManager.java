
package net.sourceforge.fullsync;

import java.io.File;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author Michele Aiello
 *
 */
public class PreferencesManager {

	/**
	 * <FullSyncPreferences>
	 *   <General>
	 * 	   <CloseMinimizesToSystemTray>true</CloseMinimizesToSystemTray>
	 * 	   <MinimizeMinimizesToSystemTray>false</CloseMinimizesToSystemTray>
	 * 	   <ConfirmExit>true</ConfirmExit>
	 *     <EnableSystemTray>true</EnableSystemTray>
	 *   </General>
	 * </FullSyncPreferences>
	 */
	
    private PropertiesConfiguration config;
    private String configFile;
    
    private boolean closeMinimizesToSystemTray = true;
    private boolean minimizeMimimizesToSystemTray = false;
    private boolean confirmExit = true;
    private boolean systemTrayEnabled = true;
    
    
    public PreferencesManager( String configFile ) 
    {
        this.configFile = configFile;        
        this.config = new PropertiesConfiguration();
        
        try {
            File file = new File( configFile );
            config.setFile( file );
            if( file.exists() )
            {
                /*
    			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    			Document doc = builder.parse( file );
    			
    			Element rootElement = doc.getDocumentElement();
    			loadPreferences(rootElement);
    			*/
                config.load();
            } else {
                /*
                config.setProperty( "Interface.closeMinimizesToSystemTray", new Boolean( true ) );
                config.setProperty( "Interface.MinimizeMinimizesToSystemTray", new Boolean( false ) );
                config.setProperty( "Interface.ConfirmExit", new Boolean( true ) );
                config.setProperty( "Interface.SystemTray.Enabled", new Boolean( true ) );
                config.save();
                */
            }
            
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
        
    }
    
    public void save()
    {
        try {
            config.save();
        } catch( ConfigurationException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
/*
    public void loadPreferences(Element rootElement) 
    {
    	NodeList generalNodeList = rootElement.getElementsByTagName("General");
    	if (generalNodeList.getLength() > 0) 
    	{
    		Element generalElement = (Element) generalNodeList.item(0);
    		NodeList closeButtonPreferencesNodeList = generalPreferencesElement.getElementsByTagName("CloseButtonBehaviour");
    		if (closeButtonPreferencesNodeList.getLength() > 0) {
    			Element closeButtonPreferencesElement = (Element) closeButtonPreferencesNodeList.item(0);
    			askOnClosing = Boolean.valueOf(closeButtonPreferencesElement.getAttribute("ask")).booleanValue();
    			closingButtonMinimizes = Boolean.valueOf(closeButtonPreferencesElement.getAttribute("minimize")).booleanValue();
    		}
    	}
    }
    
	public void savePreferences() {
        try {
	        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = docBuilder.newDocument();
	
	        Element rootElement = doc.createElement( "FullSyncPreferences" );
	        doc.appendChild(rootElement);
	        
	        Element generalPreferencesElement = doc.createElement("General");
	        rootElement.appendChild(generalPreferencesElement);
	        
	        Element closeButtonPreferencesElement = doc.createElement("CloseButtonBehaviour");
	        closeButtonPreferencesElement.setAttribute("ask", String.valueOf(askOnClosing));
	        closeButtonPreferencesElement.setAttribute("minimize", String.valueOf(closingButtonMinimizes));
	        generalPreferencesElement.appendChild(closeButtonPreferencesElement);
	        
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
*/
	public boolean confirmExit() 
	{
	    return config.getBoolean("Interface.ConfirmExit", true);
	}
	public void setConfirmExit( boolean bool )
	{
	    config.setProperty("Interface.ConfirmExit", new Boolean( bool ));
	}
	
	public boolean closeMinimizesToSystemTray() 
	{
		return config.getBoolean("Interface.CloseMinimizesToSystemTray", true);
	}
	public void setCloseMinimizesToSystemTray( boolean bool )
	{
	    config.setProperty("Interface.CloseMinimizesToSystemTray", new Boolean( bool ));
	}
	
	public boolean minimizeMinimizesToSystemTray()
	{
	    return config.getBoolean("Interface.MinimizeMinimizesToSystemTray", false);
	}
	public void setMinimizeMinimizesToSystemTray( boolean bool )
	{
	    config.setProperty("Interface.MinimizeMinimizesToSystemTray", new Boolean( bool ));
	}
	
	public boolean systemTrayEnabled()
	{
	    return config.getBoolean("Interface.SystemTray.Enabled", true);
	}
	public void setSystemTrayEnabled( boolean bool )
	{
	    config.setProperty("Interface.SystemTray.Enabled", new Boolean( bool ));
	}
}
