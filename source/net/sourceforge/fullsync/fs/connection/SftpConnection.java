package net.sourceforge.fullsync.fs.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.ui.GuiController;

import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.configuration.SshConnectionProperties;
import com.sshtools.j2ssh.sftp.FileAttributes;
import com.sshtools.j2ssh.sftp.SftpFile;
import com.sshtools.j2ssh.sftp.SftpFileInputStream;
import com.sshtools.j2ssh.sftp.SftpFileOutputStream;
import com.sshtools.j2ssh.sftp.SftpSubsystemClient;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SftpConnection extends InstableConnection
{
    private ConnectionDescription desc;
    private URI connectionUri;
    private SshClient sshClient;
    private SftpSubsystemClient sftpClient;
    private String basePath;
    private AbstractFile root;

    public SftpConnection( ConnectionDescription desc )
    	throws IOException, URISyntaxException
    {
        this.desc = desc;
        this.root = new AbstractFile( this, ".", ".", null, true, true );
        this.connectionUri = new URI( desc.getUri() );
        
        connect();
    }
    public void connect()
    	throws IOException
    {
        SshConnectionProperties prop = new SshConnectionProperties();
        prop.setHost( connectionUri.getHost() );
        if( connectionUri.getPort()!=-1 )
            prop.setPort( connectionUri.getPort() );
        
        sshClient = new SshClient();
        // REVISIT not really fine (the static method call)
        sshClient.connect( prop, new DialogKnownHostsKeyVerification( GuiController.getInstance().getMainShell() ) );
        
        PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
        pwd.setUsername( desc.getUsername() );
        pwd.setPassword( desc.getPassword() );
        
        int result = sshClient.authenticate( pwd );
        if( result == AuthenticationProtocolState.COMPLETE )
        {
            sftpClient = sshClient.openSftpChannel();
            //sftpClient.cd( path );
            basePath = sftpClient.getDefaultDirectory()+connectionUri.getPath();
            if( basePath.endsWith("/") )
                basePath = basePath.substring( 0, basePath.length()-1 );
            
        } else {
            throw new IOException( "Could not connect" );
        }
        this.root = new AbstractFile( this, ".", ".", null, true, true );
    }
    public void reconnect() throws IOException
    {
        if( sshClient != null )
            sshClient.disconnect();
        connect();
    }
    public void close() throws IOException
    {
        sftpClient.close();
        sshClient.disconnect();
    }
    public void flush() throws IOException
    {
        
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

    public File getRoot()
    {
        return root;
    }

    
    
    
    public File _createChild( File parent, String name, boolean directory )
    {
        return new AbstractFile( this, name, null, parent, directory, false );
    }

    public File buildNode( File parent, SftpFile file )
    {
        String name = file.getFilename();
        //String path = parent.getPath()+"/"+name;

        File n = new AbstractFile( this, name, null, parent, file.isDirectory(), true );
        
        FileAttributes att = file.getAttributes();
        if( file.isFile() )
            n.setFileAttributes( new net.sourceforge.fullsync.fs.FileAttributes( att.getSize().longValue(), att.getModifiedTime().longValue() ) );
        
        return n;
    }
    
    public Hashtable _getChildren( File dir ) throws IOException
    {
        SftpFile f = null;
        try {
            f = sftpClient.openDirectory( basePath+"/"+dir.getPath() );
        } catch( IOException ioe ) {
            if( ioe.getMessage().equals( "No such file") )
                 return new Hashtable(0);
            else throw ioe;
        }
        ArrayList files = new ArrayList();
        sftpClient.listChildren( f, files );
        
        Hashtable table = new Hashtable();
        for( Iterator i = files.iterator(); i.hasNext(); )
        {
            SftpFile file = (SftpFile)i.next();
            if( !file.getFilename().equals(".") && !file.getFilename().equals("..") )
                table.put( file.getFilename(), buildNode( dir, file ) );
        }
        
        return table;
    }
    

    public boolean _makeDirectory( File dir ) throws IOException
    {
        sftpClient.makeDirectory( basePath+"/"+dir.getPath() );
        return true;
    }

    public boolean _writeFileAttributes( File file, net.sourceforge.fullsync.fs.FileAttributes att )
    {
        return false;
    }
    
    public InputStream _readFile( File file ) throws IOException
    {
        return new SftpFileInputStream( sftpClient.openFile( basePath+"/"+file.getPath(), SftpSubsystemClient.OPEN_READ ) );
    }

    public OutputStream _writeFile( File file ) throws IOException
    {
        FileAttributes attrs = new FileAttributes();
        attrs.setPermissions("rw-rw----");
        SftpFile f = sftpClient.openFile( basePath+"/"+file.getPath(), SftpSubsystemClient.OPEN_CREATE | SftpSubsystemClient.OPEN_WRITE | SftpSubsystemClient.OPEN_TRUNCATE, attrs );
        return new SftpFileOutputStream( f );
    }

    public boolean _delete( File node ) throws IOException
    {
	    if( node.isDirectory() )
	         sftpClient.removeDirectory( basePath+"/"+node.getPath() );
	    else sftpClient.removeFile( basePath+"/"+node.getPath() );
	    return true;
    }
}
