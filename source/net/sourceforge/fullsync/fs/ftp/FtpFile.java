/*
 * Created on 20.07.2004
 */
package net.sourceforge.fullsync.fs.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Node;

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
    private FtpDirectory parent;
    
    public FtpFile( FTPClient client, String name, FtpDirectory parent )
    {
        this.client = client;
        this.path = parent.getPath()+"/"+name;
        this.name = name;
        this.file = null;
        this.parent = parent;
    }
    public FtpFile( FTPClient client, FTPFile file, FtpDirectory parent )
    {
        this.client = client;
        this.path = parent.getPath()+"/"+file.getName();
        this.name = file.getName();
        this.file = file;
        this.parent = parent;
    }

    public Directory getDirectory()
    {
        return parent;
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

    public Node getUnbuffered()
    {
        return this;
    }

    public boolean delete()
    {
        try {
            client.changeWorkingDirectory( parent.getPath() );
            return client.deleteFile( name );
        } catch( IOException ioe ) {
            return false;
        }
    }

    public void refresh()
    {
        try {
	        client.changeWorkingDirectory( parent.getPath() );
	        FTPFile[] a = client.listFiles( name );
	        if( a.length == 0 )
	             file = null;
	        else file = a[0];
        } catch( IOException ioe ) {
            // TODO why catching all IOs, just put them throu
        }
    }

}
