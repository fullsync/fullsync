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
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.fs.File;

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
public class SftpConnection implements FileSystemConnection
{
    private ConnectionDescription desc;
    private SshClient sshClient;
    private SftpSubsystemClient sftpClient;
    private String basePath;
    private AbstractFile root;

    public SftpConnection( ConnectionDescription desc )
    	throws FileSystemException, IOException, URISyntaxException
    {
        this.desc = desc;
        URI uri = new URI( desc.getUri() );
        sshClient = new SshClient();
        SshConnectionProperties prop = new SshConnectionProperties();
        prop.setHost( uri.getHost() );
        
        // REVISIT not really fine
        sshClient.connect( prop, new DialogKnownHostsKeyVerification( FullSync.getInstance().getMainWindow().getShell() ) );
        
        PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
        pwd.setUsername( desc.getUsername() );
        pwd.setPassword( desc.getPassword() );
        
        int result = sshClient.authenticate( pwd );
        if( result == AuthenticationProtocolState.COMPLETE )
        {
            sftpClient = sshClient.openSftpChannel();
            //sftpClient.cd( path );
            basePath = sftpClient.getDefaultDirectory()+uri.getPath();
            if( basePath.endsWith("/") )
                basePath = basePath.substring( 0, basePath.length()-1 );
            
        } else {
            throw new FileSystemException( "Could not connect" );
        }
        this.root = new AbstractFile( this, ".", ".", null, true, true );
        /* 
         * sftpClient.quit();
         * sshClient.disconnect();
         */
    }

    public File getRoot()
    {
        return root;
    }

    public File createChild( File parent, String name, boolean directory )
    {
        return new AbstractFile( this, name, parent.getPath()+"/"+name, parent, directory, false );
    }

    public File buildNode( File parent, SftpFile file )
    {
        String name = file.getFilename();
        String path = parent.getPath()+"/"+name;

        File n = new AbstractFile( this, name, path, parent, file.isDirectory(), true );
        
        FileAttributes att = file.getAttributes();
        if( file.isFile() )
            n.setFileAttributes( new net.sourceforge.fullsync.fs.FileAttributes( att.getSize().longValue(), att.getModifiedTime().longValue() ) );
        
        return n;
    }
    
    public Hashtable getChildren( File dir )
    {
        try {
	        SftpFile f = sftpClient.openDirectory( basePath+"/"+dir.getPath() );
	        ArrayList files = new ArrayList();
	        sftpClient.listChildren( f, files );
	        
	        Hashtable table = new Hashtable();
	        for( Iterator i = files.iterator(); i.hasNext(); )
	        {
	            SftpFile file = (SftpFile)i.next();
	            table.put( file.getFilename(), buildNode( dir, file ) );
	        }
	        
	        return table;
        } catch( IOException ioe ) {
            //ioe.printStackTrace();
            return new Hashtable();
        }
    }
    

    public boolean makeDirectory( File dir )
    {
        try {
            sftpClient.makeDirectory( basePath+"/"+dir.getPath() );
            return true;
        } catch( IOException e ) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setFileAttributes( File file, net.sourceforge.fullsync.fs.FileAttributes att )
    {
        return false;
    }
    
    public InputStream readFile( File file )
    {
        try {
            return new SftpFileInputStream( sftpClient.openFile( basePath+"/"+file.getPath(), SftpSubsystemClient.OPEN_READ ) );
        } catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public OutputStream writeFile( File file )
    {
        try {
            FileAttributes attrs = new FileAttributes();
            attrs.setPermissions("rw-rw----");
            SftpFile f = sftpClient.openFile( basePath+"/"+file.getPath(), SftpSubsystemClient.OPEN_CREATE | SftpSubsystemClient.OPEN_WRITE, attrs );
            return new SftpFileOutputStream( f );
        } catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean delete( File node )
    {
        try {
		    if( node.isDirectory() )
		         sftpClient.removeDirectory( basePath+"/"+node.getPath() );
		    else sftpClient.removeFile( basePath+"/"+node.getPath() );
		    return true;
        } catch( IOException e ) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void flush() throws IOException
    {
        
    }
    public void close() throws IOException
    {
        sftpClient.close();
        sshClient.disconnect();
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
