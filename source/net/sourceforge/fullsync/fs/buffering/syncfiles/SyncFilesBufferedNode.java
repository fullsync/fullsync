/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs.buffering.syncfiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SyncFilesBufferedNode implements BufferedFile
{
    protected BufferedFile parent;
    protected File unbuff;
    protected boolean dirty;
    protected String name;
    protected Hashtable children;
    protected String syncBufferFilename;
    protected boolean directory;
    protected boolean exists;
    protected boolean filtered;
    protected FileAttributes fsAttributes;
    protected FileAttributes attributes;

    public SyncFilesBufferedNode( String name, File unbuff, String syncBufferFilename, BufferedFile parent, boolean directory, boolean exists )
    {
        this.parent = parent;
        this.unbuff = unbuff;
        this.name = name;
        this.syncBufferFilename = syncBufferFilename;
        this.directory = directory;
        this.exists = exists;
    }
    
    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        if( unbuff == null )
             return parent.getPath()+"/"+name;
        else return unbuff.getPath();
    }
    public File getParent()
    {
        return parent;
    }
    
    public boolean isBuffered()
    {
        return true;
    }
    
    public boolean isDirty()
    {
        return dirty;
    }

    public File getUnbuffered()
    {
        return unbuff;
    }
    
    public void markDirty()
    {
        dirty = true;
    }
    
    public String toString()
    {
        return "Buffered: "+unbuff.toString();
    }
    
    public boolean exists()
    {
        return exists;
    }
    
    public String toBufferLine()
    {
        if( isDirectory() )
        {
            return "D\t"+getName();
        } else {
            return "F\t"+getName()
            		+"\t"+getFileAttributes().getLength()+"\t"+getFileAttributes().getLastModified()
            		+"\t"+getFsFileAttributes().getLength()+"\t"+getFsFileAttributes().getLastModified();
        }
    }
    protected void loadFromBuffer() throws IOException
    {
        try {
            File node = unbuff.getChild( syncBufferFilename );
            if( node == null || !node.exists() || node.isDirectory() )
                return; // TODO clear children list ?
            
	        String line;
	        File f = (File)node;
	        ByteArrayOutputStream out = new ByteArrayOutputStream((int)f.getFileAttributes().getLength());
	        
	        InputStream in = f.getInputStream();
	        int i; byte[] block = new byte[1024];
	        while( (i = in.read(block)) > 0 )
	            out.write( block );
	        in.close();
	        out.close();
	        
	        BufferedReader reader = new BufferedReader( new InputStreamReader( new ByteArrayInputStream( out.toByteArray() ) ) );
	        while( (line = reader.readLine()) != null )
	        {
	            String[] parts = line.split("\t");
	            if( parts.length < 2 )
	                continue;
	            File n = unbuff.getChild( parts[1] );
	            if( n == null )
	            {
	                n = unbuff.createChild( parts[1], parts[0].equals( "D" ) );
	            }
	            SyncFilesBufferedNode syncnode = new SyncFilesBufferedNode( parts[1], n, syncBufferFilename, this, parts[0].equals( "D" ), true );
	            if( parts[0].equals( "F" ) )
	            {
	                syncnode.setFileAttributes(   new FileAttributes( Long.parseLong( parts[2] ), Long.parseLong( parts[3] ) ) );
	                syncnode.setFsFileAttributes( new FileAttributes( Long.parseLong( parts[4] ), Long.parseLong( parts[5] ) ) );
	            }
	            children.put( parts[1], syncnode );
	        }
	        reader.close();
        } catch( IOException ioe ){
            ioe.printStackTrace();
        }
    }
    public void saveToBuffer()
    {
        try {
            File node = unbuff.getChild( syncBufferFilename );
            
            if( node == null )
            {
                node = unbuff.createChild( syncBufferFilename, false );
            } else if( node.isDirectory() ) {
                return;  // TODO throw exception, log error, whatever
            }
            // TODO avoid writing empty files

            String line;
	        File f = (File)node;
	        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( f.getOutputStream() ) );
	        Collection items = getChildren();
	        for( Iterator i = items.iterator(); i.hasNext(); )
	        {
	            SyncFilesBufferedNode n = (SyncFilesBufferedNode)i.next();
	            if( n.exists() )
	            {
	                writer.write( n.toBufferLine() );
	                writer.write( '\n' );
	            }
	        }
	        writer.close();
        } catch( IOException ioe ){
            ioe.printStackTrace();
        }
    }
    public void flushDirty()
    {
        if( isDirty() )
        {
            saveToBuffer();
        }
        for( Enumeration e = children.elements(); e.hasMoreElements(); )
        {
            BufferedFile n = (BufferedFile)e.nextElement();
            if( n.exists() );
                //n.flushDirty();
        }
    }

    public File createChild( String name, boolean directory ) throws IOException
    {
        markDirty();
        File n = unbuff.getChild( name );
        if( n == null )
            n = unbuff.createChild( name, directory );
        SyncFilesBufferedNode bn = new SyncFilesBufferedNode( name, n, syncBufferFilename, this, directory, false );
        children.put( name, bn );
        return bn;
    }
    
    public InputStream getInputStream() throws IOException
    {
        return unbuff.getInputStream();
    }
    public OutputStream getOutputStream() throws IOException
    {
        markDirty();
        return unbuff.getOutputStream();
    }
    
    
    public FileAttributes getFileAttributes()
    {
        return attributes;
    }
    public void writeFileAttributes() throws IOException
    {
        
    }
    public void setFileAttributes( FileAttributes attributes )
    {
        this.attributes = attributes;
    }
    public FileAttributes getFsFileAttributes()
    {
        return fsAttributes;
    }
    public void setFsFileAttributes( FileAttributes fsAttributes )
    {
        this.fsAttributes = fsAttributes;
    }
    public boolean isDirectory()
    {
        return directory;
    }
    public boolean makeDirectory() throws IOException
    {
        markDirty();
        //parent.markDirty();
    	return unbuff.makeDirectory();
    }
    public File getChild( String name )
    {
        Object obj = children.get( name );
        if( obj == null )
             return null;
        else return (File)obj;
    }
    public Collection getChildren()
    {
        return children.values();
    }
    
    
    public void addChild( File node )
    {
        children.put( node.getName(), node );
    }
    public void refreshReference() throws IOException
    {
        unbuff = getParent().getUnbuffered().getChild( getName() );
    }
    public void removeChild( String name )
    {
        children.remove( name );
    }
    public boolean delete() throws IOException
    {
        markDirty();
        return unbuff.delete();
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
    public void refresh()
    {
        // TODO refresh()
    }
    public void refreshBuffer()
    {
        // TODO refreshBuffer()
    }
}
