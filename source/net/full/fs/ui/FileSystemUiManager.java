package net.full.fs.ui;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.smb.SmbFileSystemConfigBuilder;
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
        
        FileSystemOptions fileSystemOptions = new FileSystemOptions();
        
        if( uri.startsWith( "ftp" ) )
        {
            String username = location.getProperty( "username" );
            String password = location.getProperty( "password" );
            
            FtpFileSystemConfigBuilder.getInstance().setUsername( fileSystemOptions, username );
            FtpFileSystemConfigBuilder.getInstance().setPassword( fileSystemOptions, password );
        } else if( uri.startsWith( "sftp" ) ) {
            final String username = location.getProperty( "username" );
            final String password = location.getProperty( "password" );
            
            SftpFileSystemConfigBuilder.getInstance().setUsername( fileSystemOptions, username );
            SftpFileSystemConfigBuilder.getInstance().setPassword( fileSystemOptions, password );
        } else if( uri.startsWith( "smb" ) ) {
            final String username = location.getProperty( "username" );
            final String password = location.getProperty( "password" );
            
            SmbFileSystemConfigBuilder.getInstance().setUsername( fileSystemOptions, username );
            SmbFileSystemConfigBuilder.getInstance().setPassword( fileSystemOptions, password );
        }

        return VFS.getManager().resolveFile( uri, fileSystemOptions );
    }
}
