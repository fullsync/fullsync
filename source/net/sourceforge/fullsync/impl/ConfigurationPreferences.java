
package net.sourceforge.fullsync.impl;

import java.io.File;

import javax.xml.parsers.FactoryConfigurationError;

import net.sourceforge.fullsync.Crypt;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Preferences;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author Michele Aiello
 *
 */
public class ConfigurationPreferences implements Preferences 
{
    private PropertiesConfiguration config;
    private String configFile;
    
    public ConfigurationPreferences( String configFile ) 
    {
        this.configFile = configFile;        
        this.config = new PropertiesConfiguration();
        
        try {
            File file = new File( configFile );
            config.setFile( file );
            if( file.exists() )
            {
                config.load();
            } 
		} catch (ConfigurationException e) {
			ExceptionHandler.reportException( e );
		} catch (FactoryConfigurationError e) {
			ExceptionHandler.reportException( e );
		}
        
    }
    
    public void save()
    {
        try {
            config.save();
        } catch( ConfigurationException e ) {
            // TODO Auto-generated catch block
            ExceptionHandler.reportException( e );
        }
    }

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
	public String getProfileListStyle()
    {
	    return config.getString("Interface.ProfileList.Style", "NiceListView");
    }
	public void setProfileListStyle( String profileListStyle )
    {
	    config.setProperty("Interface.ProfileList.Style", profileListStyle);
    }
	
	public boolean listeningForRemoteConnections() {
		return config.getBoolean("RemoteConnection.active", false);
	}
	public void setListeningForRemoteConnections(boolean bool) {
		config.setProperty("RemoteConnection.active", new Boolean(bool));
	}
	public int getRemoteConnectionsPort() {
		return config.getInt("RemoteConnection.port", 10000);
	}
	public void setRemoteConnectionsPort(int port) {
		config.setProperty("RemoteConnection.port", new Integer(port));
	}	
	public String getRemoteConnectionsPassword() {
		String passwd = config.getString("RemoteConnection.password", "admin");
		String decryptedPassword = Crypt.decrypt(passwd);
		return decryptedPassword;
	}
	public void setRemoteConnectionsPassword(String password) {
		String encryptedPasswd = Crypt.encrypt(password);
		config.setProperty("RemoteConnection.password", encryptedPasswd);
	}
	public boolean showSplashScreen() {
		return config.getBoolean("Interface.ShowSplashScreen", true);
	}
	public void setShowSplashScreen(boolean bool) {
		config.setProperty("Interface.ShowSplashScreen", new Boolean(bool));
	}
	public boolean autostartScheduler() {
		return config.getBoolean("Interface.AutostartScheduler", false);
	}
	public void setAutostartScheduler(boolean bool) {
		config.setProperty("Interface.AutostartScheduler", new Boolean(bool));
	}
    public String getLanguageCode() {
    	return config.getString("Interface.LanguageCode", new String("en"));
    }
    public void setLanguageCode(String code) {
    	config.setProperty("Interface.LanguageCode", code);
    }

}
