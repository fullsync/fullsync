package net.sourceforge.fullsync.fs.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Hashtable;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SmbConnection extends InstableConnection
{
	private static final long serialVersionUID = 1;
	
    private ConnectionDescription desc;

    private SmbFile base;
    private AbstractFile root;
    
    public SmbConnection( ConnectionDescription desc )
        throws IOException
    {
        this.desc = desc;
        this.root = new AbstractFile( this, ".", ".", null, true, true ); 
        connect();
    }
    public void connect() throws IOException
    {
        String domain = null, username, password;
        String[] user = desc.getUsername().split("@");
        username = user[0];
        if( user.length > 1 ) domain = user[1];
        password = desc.getPassword();
        
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication( domain, username, password );
        base = new SmbFile( desc.getUri()+"/", auth );
    }
    public void reconnect() throws IOException
    {
    }
    public void flush() throws IOException
    {
        
    }
    public void close() throws IOException
    {
    }
    
    public File getRoot()
    {
        return root;
    }
    
    public boolean isAvailable()
    {
        return true;
    }

    public File _createChild( File parent, String name, boolean directory )
    {
        return new AbstractFile( this, name, null, parent, directory, false );
    }

    public File buildNode( File parent, SmbFile file )
        throws SmbException
    {
        String name = file.getName();
        if( name.endsWith( "/" ) )
            name = name.substring( 0, name.length()-1 );
        //String path = parent.getPath()+"/"+name;
        
        File n = new AbstractFile( this, name, null, parent, file.isDirectory(), true );
        if( file.isFile() ) {
            n.setFileAttributes( new FileAttributes( file.length(), file.lastModified() ) );
        }
        return n;
    }
    
    public Hashtable _getChildren( File dir )
        throws SmbException, MalformedURLException, UnknownHostException
    {
        if( dir.exists() )
        {
	        SmbFile f = new SmbFile( base, dir.getPath()+"/" );
	        SmbFile[] files = f.listFiles();
	        
	        if( files == null )
	            return new Hashtable();
	        
	        Hashtable table = new Hashtable( files.length );
	        if( files != null )
	        for( int i = 0; i < files.length; i++ )
	        {
                SmbFile file = files[i]; 

                String name = file.getName();
                if( name.endsWith( "/" ) )
                    name = name.substring( 0, name.length()-1 );

	            if( file.isFile() || file.isDirectory() )
	                table.put( name, buildNode( dir, file ) );
	        }
	        return table;
        } else {
            return new Hashtable();
        }
    }

    public boolean _makeDirectory( File dir )
        throws MalformedURLException, SmbException, UnknownHostException
    {
        SmbFile f = new SmbFile( base, dir.getPath() );
        f.mkdirs();
        return f.exists();
    }

    public boolean _writeFileAttributes( File file, FileAttributes att )
        throws MalformedURLException, SmbException, UnknownHostException
    {
        SmbFile f = new SmbFile( base, file.getPath() );
        f.setLastModified( att.getLastModified() );
        return true;
    }

    public InputStream _readFile( File file ) 
        throws MalformedURLException, UnknownHostException, IOException
    {
        SmbFile f = new SmbFile( base, file.getPath() );
        return f.getInputStream();
    }

    public OutputStream _writeFile( File file )
        throws MalformedURLException, UnknownHostException, IOException
    {
        SmbFile f = new SmbFile( base, file.getPath() );
        return f.getOutputStream();
    }

    public boolean _delete( File node ) throws MalformedURLException, UnknownHostException, SmbException
    {
        SmbFile f = new SmbFile( base, node.getPath() );
        f.delete();
        return !f.exists();
    }
    
    public String getUri()
    {
        return desc.getUri();
    }

    public boolean isCaseSensitive()
    {
    	// TODO find out whether current fs is case sensitive
    	return false;
    }
}
