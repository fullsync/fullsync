package net.sourceforge.fullsync;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ConnectionDescription implements Serializable
{
    private String uri;
    private String bufferStrategy;
    private String username;
    private String cryptedPassword;
    private Hashtable parameters;
    
    public ConnectionDescription()
    {
        this.uri = null;
        this.bufferStrategy = null;
        this.parameters = new Hashtable();
    }
    public ConnectionDescription( String url, String bufferStrategy )
    {
        this.uri = url;
        this.bufferStrategy = bufferStrategy;
        this.parameters = new Hashtable();
    }
    
    
    public String getBufferStrategy()
    {
        return bufferStrategy;
    }
    public void setBufferStrategy( String bufferStrategy )
    {
        this.bufferStrategy = bufferStrategy;
    }
    public String getUri()
    {
        return uri;
    }
    public void setUri( String uri )
    {
        this.uri = uri;
    }
    public Dictionary getParameters()
    {
        return parameters;
    }
    public String getParameter( String name )
    {
        return (String)parameters.get( name );
    }
    public void setParameter( String name, String value )
    {
        this.parameters.put( name, value );
    }
    public String getUsername()
    {
        return username;
    }
    public void setUsername( String username )
    {
        this.username = username;
    }
    public String getCryptedPassword()
    {
        return cryptedPassword;
    }
    public void setCryptedPassword( String cryptedPassword )
    {
        this.cryptedPassword = cryptedPassword;
    }
    public String getPassword()
    {
        return Crypt.decrypt( cryptedPassword );
    }
    public void setPassword( String password )
    {
        this.cryptedPassword = Crypt.encrypt( password );
    }
    public String toString()
    {
        return uri;
    }
}
