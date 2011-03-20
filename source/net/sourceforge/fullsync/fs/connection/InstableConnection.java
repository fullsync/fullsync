/**
 *	@license
 *	This program is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation; either version 2
 *	of the License, or (at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *	Boston, MA  02110-1301, USA.
 *
 *	---
 *	@copyright Copyright (C) 2005, Jan Kopcsek <codewright@gmx.net>
 *	@copyright Copyright (C) 2011, Obexer Christoph <cobexer@gmail.com>
 */
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
	private static final long serialVersionUID = 1;
	
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

    public Hashtable<String, File> getChildren( File dir )
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
    public abstract Hashtable<String, File> _getChildren( File dir ) throws IOException;;
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
