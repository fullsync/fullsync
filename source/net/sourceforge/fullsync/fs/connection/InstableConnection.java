package net.sourceforge.fullsync.fs.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public abstract class InstableConnection implements FileSystemConnection
{
    public File createChild( File parent, String name, boolean directory )
    	throws IOException
    {
        try {
            return _createChild( parent, name, directory );
        } catch( IOException ioe ) {
            handleConnectionBroken();
            return _createChild( parent, name, directory );
        }
    }

    public Hashtable getChildren( File dir )
    	throws IOException
    {
        try {
            return _getChildren( dir );
        } catch( IOException ioe ) {
            handleConnectionBroken();
            return _getChildren( dir );
        }
    }

    public boolean makeDirectory( File dir ) throws IOException
    {
        try {
            return _makeDirectory( dir );
        } catch( IOException ioe ) {
            handleConnectionBroken();
            return _makeDirectory( dir );
        }    
     }

    public boolean writeFileAttributes( File file, FileAttributes att ) throws IOException
    {
        try {
            return _writeFileAttributes( file, att );
        } catch( IOException ioe ) {
            handleConnectionBroken();
            return _writeFileAttributes( file, att );
        }
    }

    public InputStream readFile( File file ) throws IOException
    {
        try {
            return _readFile( file );
        } catch( IOException ioe ) {
            handleConnectionBroken();
            return _readFile( file );
        }
    }

    public OutputStream writeFile( File file ) throws IOException
    {
        try {
            return _writeFile( file );
        } catch( IOException ioe ) {
            handleConnectionBroken();
            return _writeFile( file );
        }
    }

    public boolean delete( File node ) throws IOException
    {
        try {
            return _delete( node );
        } catch( IOException ioe ) {
            handleConnectionBroken();
            return _delete( node );
        }
    }
    
    public void handleConnectionBroken() throws IOException
    {
        reconnect();
    }

    public abstract File _createChild( File parent, String name, boolean directory ) throws IOException;
    public abstract Hashtable _getChildren( File dir ) throws IOException;;
    public abstract boolean _makeDirectory( File dir ) throws IOException;
    public abstract boolean _writeFileAttributes( File file, FileAttributes att )  throws IOException;
    public abstract InputStream _readFile( File file ) throws IOException;
    public abstract OutputStream _writeFile( File file ) throws IOException;
    public abstract boolean _delete( File node ) throws IOException;
    
    public abstract File getRoot();
    public abstract String getUri();
    public abstract boolean isCaseSensitive(); 

    public abstract void connect() throws IOException;
    public abstract void reconnect() throws IOException;
    public abstract void close() throws IOException;
    public abstract void flush() throws IOException;
}
