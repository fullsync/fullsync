/*
 * Created on 20.07.2004
 */
package net.sourceforge.fullsync.fs.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FtpFile implements File
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
    
    
    private FTPClient client;
    private String path;
    private String name;
    private FTPFile file;
    private FtpFile parent;
    private boolean directory;
    private boolean filtered;
    
    private Hashtable children;

    
    public FtpFile( FTPClient client, String name, FtpFile parent, boolean directory )
    {
        this.client = client;
        this.path = parent.getPath()+"/"+name;
        this.name = name;
        this.file = null;
        this.parent = parent;
        this.directory = directory;
    }
    public FtpFile( FTPClient client, FTPFile file, FtpFile parent )
    {
        this.client = client;
        this.path = parent.getPath()+"/"+file.getName();
        this.name = file.getName();
        this.file = file;
        this.parent = parent;
        this.directory = file.isDirectory();
    }

    public File getParent()
    {
        return parent;
    }

    public void initialize()
    {
        try {
            children = new Hashtable();
            if( !client.changeWorkingDirectory( path ) )
                return;

            FTPFile[] files = client.listFiles();
            for( int c = 0; c < files.length; c++ )
            {
                if( files[c].isDirectory() )
                     children.put( files[c].getName(), new FtpFile( client, files[c].getName(), this, true ) );
                else children.put( files[c].getName(), new FtpFile( client, files[c], this ) );
            }
            
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
    public long getLength()
    {
        return file.getSize();
    }

    public long getLastModified()
    {
        return file.getTimestamp().getTimeInMillis();
    }

    public void setLastModified( long lm )
    {
        return;
    }

    public Collection getChildren()
    {
        if( children == null )
            initialize();
        
        return children.values();
    }

    public File getChild( String name )
    {
        if( children == null )
            initialize();
        return (File)children.get( name );
    }

    public File createChild( String name, boolean directory )
    {
        // TODO check existing
        FtpFile dir = new FtpFile( client, name, this, directory );
        children.put( name, dir );
        return dir;
    }
    public InputStream getInputStream() throws IOException
    {
        client.changeWorkingDirectory( parent.getPath() );
        return new FtpFileInputStream( client.retrieveFileStream( name ), client );
    }
    
    public OutputStream getOutputStream() throws IOException
    {
        client.changeWorkingDirectory( parent.getPath() );
        return new FtpFileOutputStream( client.storeFileStream( name ), client );
    }
    
    public FileAttributes getFileAttributes()
    {
        return new FileAttributes( file.getSize(), file.getTimestamp().getTimeInMillis() );
    }
    public void setFileAttributes( FileAttributes att )
    {
    }
	public void writeFileAttributes() throws IOException
	{

	}
    public boolean isFile()
    {
        return !directory;
    }
    public boolean isFiltered()
    {
        return filtered;
    }
    public void setFiltered( boolean filtered )
    {
        this.filtered = filtered;
    }
    public boolean makeDirectory() throws IOException
    {
        return client.makeDirectory( path );
    }
    
    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public boolean isDirectory()
    {
        return false;
    }

    public boolean exists()
    {
        return (file != null);
    }

    public boolean isBuffered()
    {
        return false;
    }

    public File getUnbuffered()
    {
        return this;
    }
    
    public boolean delete() throws IOException
    {
        if( isDirectory() )
        {
	        if( children == null )
	            initialize();
	        if( children.isEmpty() )
	        {
	            try {
	                return client.removeDirectory( getPath() );
	            } catch( IOException ioe ) {
	                return false;
	            }
	        } else {
	            return false;
	        }
        } else {
	        client.changeWorkingDirectory( parent.getPath() );
	        return client.deleteFile( name );
        }
    }

    public void refresh()
    {
        try {
            if( isDirectory() )
            {
	            if( children == null )
	                initialize();
	            
	            for( Enumeration e = children.elements(); e.hasMoreElements(); )
	            {
	                File n = (File)e.nextElement();
	                if( n.isDirectory() == false )
	                    n.refresh();
	            }
            } else {
                client.changeWorkingDirectory( parent.getPath() );
		        FTPFile[] a = client.listFiles( name );
		        if( a.length == 0 )
		             file = null;
		        else file = a[0];
            }
        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    public void refreshBuffer()
    {
        
    }

}
