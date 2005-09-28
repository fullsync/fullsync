package net.sourceforge.fullsync.fs.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Hashtable;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.sftp.TrustEveryoneUserInfo;

import com.jcraft.jsch.UserInfo;

public class CommonsVfsConnection implements FileSystemConnection
{
    private ConnectionDescription desc;
    private FileObject base;
    private FileSystem fileSystem;
    private File root;

    public CommonsVfsConnection( ConnectionDescription desc )
        throws net.sourceforge.fullsync.FileSystemException
    {
        try {
            this.desc = desc;

            FileSystemOptions options = new FileSystemOptions();
            UserInfo userInfo = new TrustEveryoneUserInfo();
            SftpFileSystemConfigBuilder.getInstance().setUserInfo( options, userInfo );
            
            String uriString;
            String userinfo = desc.getUsername();
            if( userinfo != null )
            {
                String[] parts = userinfo.split("@");
                
                if( parts.length == 2 )
                {
                    userinfo = parts[0];
                    String publickeyfile = parts[1];
                    SftpFileSystemConfigBuilder.getInstance().setIdentities( options, new java.io.File[]{ new java.io.File(publickeyfile) } );
                }
                
                userinfo = URLEncoder.encode( userinfo ); 
                if( desc.getPassword() != null )
                    userinfo += ":" + URLEncoder.encode( desc.getPassword() );
                
                uriString = desc.getUri().replaceFirst( "//", "//"+userinfo+"@" );
            } else {
                uriString = desc.getUri();
            }

            URI uri = new URI( uriString );
            String baseUri = uriString.substring( 0, uriString.length()-(uri.getPath().length()));
            base = VFS.getManager().resolveFile( baseUri, options );
            base = base.resolveFile( uri.getPath() );
            fileSystem = base.getFileSystem();
            root = new AbstractFile( this, ".", ".", null, true, base.exists() );
        } catch( FileSystemException e ) {
            throw new net.sourceforge.fullsync.FileSystemException( e );
        } catch( URISyntaxException e ) {
            throw new net.sourceforge.fullsync.FileSystemException( e );
        }
    }
    
    public File createChild( File parent, String name, boolean directory )
            throws IOException
    {
        return new AbstractFile( this, name, null, parent, directory, false );
        
    }
    
    public File buildNode( File parent, FileObject file )
        throws FileSystemException
    {
        String name = file.getName().getBaseName();
        //String path = parent.getPath()+"/"+name;
        
        File n = new AbstractFile( this, name, null, parent, file.getType() == FileType.FOLDER, true );
        if( file.getType() == FileType.FILE ) {
            FileContent content = file.getContent();
            n.setFileAttributes( new FileAttributes( content.getSize(), content.getLastModifiedTime() ) );
        }
        return n;
    }

    public Hashtable getChildren( File dir ) throws IOException
    {
        try {
            Hashtable children = new Hashtable();
            
            FileObject obj = base.resolveFile( dir.getPath() );
            if( obj.exists() && obj.getType() == FileType.FOLDER )
            {
                FileObject[] list = obj.getChildren();
                for( int i = 0; i < list.length; i++ )
                    children.put( list[i].getName().getBaseName(), buildNode( dir, list[i] ) );
            }
            return children;
        } catch( FileSystemException fse ) {
            throw new IOException( fse.getMessage() );
        }
    }

    public boolean makeDirectory( File dir ) throws IOException
    {
        FileObject obj = base.resolveFile( dir.getPath() );
        obj.createFolder();
        return true;
    }

    public boolean writeFileAttributes( File file, FileAttributes att )
            throws IOException
    {
        FileObject obj = base.resolveFile( file.getPath() );
        FileContent content = obj.getContent();
        content.setLastModifiedTime( att.getLastModified() );
        return true;
    }

    public InputStream readFile( File file ) throws IOException
    {
        FileObject obj = base.resolveFile( file.getPath() );
        return obj.getContent().getInputStream();
    }

    public OutputStream writeFile( File file ) throws IOException
    {
        FileObject obj = base.resolveFile( file.getPath() );
        return obj.getContent().getOutputStream();
    }

    public boolean delete( File node ) throws IOException
    {
        FileObject obj = base.resolveFile( node.getPath() );
        return obj.delete();
    }

    public File getRoot()
    {
        return root;
    }

    public void flush() throws IOException
    {
        
    }

    public void close() throws IOException
    {
    }

    public String getUri()
    {
        return desc.getUri();
    }

    public boolean isCaseSensitive()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isAvailable()
    {
        // TODO Auto-generated method stub
        return true;
    }

}
