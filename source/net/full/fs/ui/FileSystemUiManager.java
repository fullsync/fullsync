package net.full.fs.ui;

import java.net.URLEncoder;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class FileSystemUiManager
{
    private static FileSystemUiManager instance;
    public static FileSystemUiManager getInstance()
    {
        if( instance == null )
            instance = new FileSystemUiManager();
        return instance;
    }
    
    public ProtocolSpecificComposite createProtocolSpecificComposite( Composite parent, int style, String protocol )
    {
        ProtocolSpecificComposite composite = null;
        
        if( protocol.equals( "file" ) )
            composite = new FileSpecificComposite( parent, style );
        else if( protocol.equals( "ftp" ) )
            composite = new UserPasswordSpecificComposite( parent, style );
        else if( protocol.equals( "sftp" ) )
            composite = new UserPasswordSpecificComposite( parent, style );
        else if( protocol.equals( "smb" ) )
            composite = new UserPasswordSpecificComposite( parent, style );
        
        if( composite != null )
        {
            composite.reset( protocol );
        }
        return composite;
    }
    
    public String[] getSchemes()
    {
        return new String[] { "file", "ftp", "sftp", "smb" };
    }
    
    public FileObject resolveFile( LocationDescription location ) throws FileSystemException
    {
        String uri = location.getUri().toString();
        
        String username = location.getProperty( "username" );
        String password = location.getProperty( "password" );
        String userinfo = null;
        
        if( username != null ) {
            userinfo = URLEncoder.encode( username );
            if( password != null )
                userinfo += ":" + URLEncoder.encode( password );
            
            uri = uri.replaceFirst("//", "//"+userinfo+"@");
        }
        return VFS.getManager().resolveFile( uri );
    }
}
