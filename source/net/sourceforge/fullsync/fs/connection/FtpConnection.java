package net.sourceforge.fullsync.fs.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FtpConnection implements FileSystemConnection
{
    class FtpFileInputStream extends InputStream
    {
        private InputStream in;
        private FTPClient client;
        
        public FtpFileInputStream( InputStream in, FTPClient client )
        {
            this.in = in;
            this.client = client;
        }        
        public int available() throws IOException
        {
            return in.available();
        }
        public void close() throws IOException
        {
            in.close();
            client.completePendingCommand();
        }
        public int read( byte[] b, int off, int len ) throws IOException
        {
            return in.read( b, off, len );
        }
        public int read( byte[] b ) throws IOException
        {
            return in.read( b );
        }
        public synchronized void reset() throws IOException
        {
            in.reset();
        }
        public int read() throws IOException
        {
            return in.read();
        }        
        public synchronized void mark( int readlimit )
        {
            in.mark( readlimit );
        }
        public boolean markSupported()
        {
            return in.markSupported();
        }
        public long skip( long n ) throws IOException
        {
            return in.skip( n );
        }
    }
    class FtpFileOutputStream extends OutputStream
    {
        private OutputStream out;
        private FTPClient client;
        
        public FtpFileOutputStream( OutputStream out, FTPClient client )
        {
            this.out = out;
            this.client = client;
        }
        public void close() throws IOException
        {
            out.close();
            client.completePendingCommand();
        }
        public void flush() throws IOException
        {
            out.flush();
        }
        public void write( byte[] b, int off, int len ) throws IOException
        {
            out.write( b, off, len );
        }
        public void write( byte[] b ) throws IOException
        {
            out.write( b );
        }
        public void write( int b ) throws IOException
        {
            out.write( b );
        }
    }
    
    private ConnectionDescription desc;
    private FTPClient client;
    private String basePath;
    private File root;
    
    public FtpConnection( ConnectionDescription desc )
    	throws FileSystemException, URISyntaxException
    {
        try {
            this.desc = desc;
	        client = new FTPClient();
	        URI uri = new URI( desc.getUri() );
	        client.connect( uri.getHost(), uri.getPort()==-1?21:uri.getPort() );
	        client.login( desc.getUsername(), desc.getPassword() );
	        client.setFileType( FTP.BINARY_FILE_TYPE );
	        basePath = client.printWorkingDirectory()+uri.getPath();
	        
	        if( !client.changeWorkingDirectory( basePath ) )
	        {
	            client.quit();
	            throw new FileSystemException( "Could not set working dir" );
	        }
	        
	        if( basePath.endsWith("/") )
                basePath = basePath.substring( 0, basePath.length()-1 );
	        
	        root = new AbstractFile( this, ".", ".", null, true, true ); 
        } catch( IOException ioe ) {
            throw new FileSystemException(ioe);
        }
    }

    public File getRoot()
    {
        return root;
    }

    public File createChild( File parent, String name, boolean directory )
    {
        return new AbstractFile( this, name, parent.getPath()+"/"+name, parent, directory, false );
    }

    public File buildNode( File parent, FTPFile file )
    {
        String name = file.getName();
        String path = parent.getPath()+"/"+name;
        
        File n = new AbstractFile( this, name, path, parent, file.isDirectory(), true );
        if( !file.isDirectory() )
            n.setFileAttributes( new FileAttributes( file.getSize(), file.getTimestamp().getTimeInMillis()) );
        return n;
    }
    
    public Hashtable getChildren( File dir )
    {
        try {
	        //client.changeWorkingDirectory( basePath+dir.getPath() );
	        FTPFile[] files = client.listFiles( "-a "+basePath+"/"+dir.getPath() );
	        
	        Hashtable table = new Hashtable();
	        for( int i = 0; i < files.length; i++ )
	        {
	            String name = files[i].getName();
	            if( !name.equals( "." ) && !name.equals( ".." ) )
	                table.put( name, buildNode( dir, files[i] ) );
	        }
	        
	        return table;
        } catch( IOException ioe ) {
            return null;
        }
    }
    

    public boolean makeDirectory( File dir ) throws IOException
    {
        return client.makeDirectory( basePath +"/"+ dir.getPath() );
    }

    public boolean setFileAttributes( File file, FileAttributes attr )
    {
        return false;
    }

    public InputStream readFile( File file ) throws IOException
    {
        //client.changeWorkingDirectory( basePath+file.getParent().getPath() );
        return new FtpFileInputStream( client.retrieveFileStream( basePath+"/"+file.getPath() ), client );
    }

    public OutputStream writeFile( File file ) throws IOException
    {
        //client.changeWorkingDirectory( basePath+file.getParent().getPath() );
        return new FtpFileOutputStream( client.storeFileStream( basePath+"/"+file.getPath() ), client );
    }

    public boolean delete( File file ) throws IOException
    {
	    if( file.isDirectory() )
	         return client.removeDirectory( basePath+"/"+file.getPath() );
	    else return client.deleteFile( basePath+"/"+file.getPath() );
    }
    
    public void flush() throws IOException
    {
        
    }
    
    public void close() throws IOException
    {
        client.disconnect();
    }
    
    public String getUri()
    {
        return desc.getUri();
    }

    
}
