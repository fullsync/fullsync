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
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Node;
import net.sourceforge.fullsync.fs.buffering.BufferedDirectory;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SyncFilesBufferedDirectory extends SyncFilesBufferedNode implements BufferedDirectory
{
    private Hashtable children;
    private String syncBufferFilename;
    
    public SyncFilesBufferedDirectory( String name, Directory directory, String syncBufferFilename )
    {
    	super( name, directory );
    	this.children = new Hashtable();
    	this.syncBufferFilename = syncBufferFilename;
        
    	loadFromBuffer();
    }
    public SyncFilesBufferedDirectory( String name, Directory directory, String syncBufferFilename, SyncFilesBufferedDirectory parent )
    {
    	super( name, directory, parent );
    	this.children = new Hashtable();
    	this.syncBufferFilename = syncBufferFilename;

        loadFromBuffer();
    }
    
    protected void loadFromBuffer()
    {
        Node node = ((Directory)unbuff).getChild( syncBufferFilename );
        if( node == null || node.isDirectory() )
            return; // TODO clear children list ?
        
        try {
	        String line;
	        File f = (File)node;
	        ByteArrayOutputStream out = new ByteArrayOutputStream((int)f.getLength());
	        
	        InputStream in = f.getInputStream();
	        int i;
	        while( (i = in.read()) > 0 )
	            out.write( i );
	        in.close();
	        out.close();
	        
	        BufferedReader reader = new BufferedReader( new InputStreamReader( new ByteArrayInputStream( out.toByteArray() ) ) );
	        while( (line = reader.readLine()) != null )
	        {
	            String[] parts = line.split("\t");
	            Node n = ((Directory)unbuff).getChild( parts[1] );
	            if( n == null )
	            {
	                if( parts[0].equals( "F" ) )
		                 n = ((Directory)unbuff).createFile( parts[1] );
		            else n = ((Directory)unbuff).createDirectory( parts[1] );
	            }
	            if( parts[0].equals( "F" ) )
	                 children.put( parts[1], new SyncFilesBufferedFile( parts[1], (File)n, this, parts ) );
	            else children.put( parts[1], new SyncFilesBufferedDirectory( parts[1], (Directory)n, syncBufferFilename, this ) );
	        }
	        reader.close();
        } catch( IOException ioe ){
            ioe.printStackTrace();
        }
    }
    public void saveToBuffer()
    {
        Node node = ((Directory)unbuff).getChild( syncBufferFilename );
        
        if( node == null )
        {
            node = ((Directory)unbuff).createFile( syncBufferFilename );
        } else if( node.isDirectory() ) {
            return;  // TODO throw exception, log error, whatever
        }
        // TODO avoid writing empty files
        try {
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
            Object n = e.nextElement();
            if( n instanceof BufferedDirectory )
            {
                BufferedDirectory dir = ((BufferedDirectory)n);
                if( dir.exists() )
                    dir.flushDirty();
            }
        }
    }

    public Directory createDirectory( String name )
    {
        markDirty();
        Directory d = ((Directory)unbuff).createDirectory( name );
        SyncFilesBufferedDirectory bd = new SyncFilesBufferedDirectory( name, d, syncBufferFilename, this );
        this.children.put( name, bd );
        return bd;
    }
    public File createFile( String name )
    {
        markDirty();
        File f = ((Directory)unbuff).createFile( name );
        SyncFilesBufferedFile bf = new SyncFilesBufferedFile( name, f, this );
        this.children.put( name, bf );
        return bf;
    }
    public void makeDirectory()
    {
        markDirty();
        parent.markDirty();
    	((Directory)unbuff).makeDirectory();
    }
    public Node getChild( String name )
    {
        Object obj = children.get( name );
        if( obj == null )
             return null;
        else return (Node)obj;
    }
    public Collection getChildren()
    {
        return children.values();
        /*
        Collection ch = children.values();
        Node[] nodes = new Node[ch.size()];
        ch.toArray( nodes );
        Arrays.sort( nodes, new Comparator() {
            public int compare( Object arg0, Object arg1 )
	        {
                Node n1 = (Node)arg0;
                Node n2 = (Node)arg1;

                return n1.getName().compareTo(n2.getName());
	        } 
        });
        return nodes;
        */
    }
    public Directory getParent()
    {
        return parent;
    }
    public void removeChild( String name )
    {
        markDirty();
        children.remove( name );
    }
   
    public boolean delete()
    {
        /* Non-Recursive deletion
        String name = unbuff.getName();
        Node[] ch = getChildren();
        for( int c = 0; c < ch.length; c++ )
            ch[c].delete();
            */
        if( getChildren().size() == 0 )
        {
	        Node file = ((Directory)unbuff).getChild( syncBufferFilename );
	        file.delete();
	        unbuff.delete();
	        parent.removeChild( name );
	        return true;
        } else {
            return false;
        }
    }
    public void refresh()
    {
        // TODO reload
    }
    public boolean isInSync()
    {
        return true;
    }
    public String toBufferLine()
    {
        return "D\t"+getName();
    }
    public boolean isDirectory()
    {
        return true;
    }
    
}
