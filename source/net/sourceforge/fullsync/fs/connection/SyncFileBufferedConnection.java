package net.sourceforge.fullsync.fs.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SyncFileBufferedConnection implements BufferedConnection
{
	private static final long serialVersionUID = 1;
	
    class SyncFileDefaultHandler extends DefaultHandler
    {
        BufferedConnection bc;
        AbstractBufferedFile current;
        
        public SyncFileDefaultHandler( SyncFileBufferedConnection bc )
        {
            this.bc = bc;
            current = (AbstractBufferedFile)bc.getRoot();
        }
        
        public void startDocument() throws SAXException
        {
            
            super.startDocument();
        }
        public void startElement( String uri, String localName, String qName,
                Attributes attributes ) throws SAXException
        {
            String name = attributes.getValue( "Name" );
            
            if( qName.equals( "Directory" ) )
            {
                if( name.equals( "/" ) || name.equals( "." ) )
                    return;
                //File n = current.getUnbuffered().getChild( name );
                //if( n == null )
		        //    n = current.getUnbuffered().createChild( name, true );
                AbstractBufferedFile newDir = new AbstractBufferedFile( bc, name, current.getPath()+"/"+name, current, true, true );
                current.addChild( newDir );
                current = newDir;
            } else if( qName.equals( "File" ) ) {
                //File n = current.getUnbuffered().getChild( name );
                //if( n == null )
                //    n = current.getUnbuffered().createChild( name, false );
                AbstractBufferedFile newFile = 
                    new AbstractBufferedFile( bc, name, current.getPath()+"/"+name, current, false, true );
                newFile.setFileAttributes( 
                    new FileAttributes(
                        Long.parseLong(attributes.getValue( "BufferedLength" ) ),
                        Long.parseLong(attributes.getValue( "BufferedLastModified" ) ) ) );
                newFile.setFsFileAttributes(
                    new FileAttributes(
                        Long.parseLong(attributes.getValue( "FileSystemLength" ) ), 
                        Long.parseLong(attributes.getValue( "FileSystemLastModified" ) ) ) );
                current.addChild( newFile );
            }
            super.startElement( uri, localName, qName, attributes );
        }
        
        public void endElement( String uri, String localName, String qName )
                throws SAXException
        {
            if( qName.equals( "Directory" ) )
            {
                /* Source Buffer needs to load fs files after buffer info /
                Collection fsChildren = current.getUnbuffered().getChildren();
                for( Iterator i = fsChildren.iterator(); i.hasNext(); )
                {
                    File f = (File)i.next();
                    if( current.getChild( f.getName() ) == null )
                        current.addChild( new AbstractBufferedFile( bc, f, current, f.isDirectory(), false ) );
                }
                /* */
                current = (AbstractBufferedFile)current.getParent();
            }
            super.endElement( uri, localName, qName );
        }
        public void endDocument() throws SAXException
        {
            super.endDocument();
        }
    }
    
    private Site fs;
    private BufferedFile root; 
    private boolean dirty;
    private boolean monitoringFileSystem; 
    
    public SyncFileBufferedConnection( Site fs ) throws IOException
    {
        this.fs = fs;
        this.dirty = false;
        this.monitoringFileSystem = false;
        loadFromBuffer();
    }

    public File createChild( File dir, String name, boolean directory ) throws IOException
    {
        dirty = true;
        File n = dir.getUnbuffered().getChild( name );
        if( n == null )
            n = dir.getUnbuffered().createChild( name, directory );
        BufferedFile bf = new AbstractBufferedFile( this, n, dir, directory, false );
        return bf;
    }
    public boolean delete( File node ) throws IOException
    {
        node.getUnbuffered().delete();
        ((BufferedFile)node.getParent()).removeChild( node.getName() );
        return true;
    }
    public Hashtable getChildren( File dir )
    {
        return null;
    }
    public File getRoot()
    {
        return root;
    }
    public boolean makeDirectory( File dir )
    {
        return false;
    }
    public InputStream readFile( File file )
    {
        return null;
    }
    public boolean setLastModifiedDate( File file, long lm )
    {
        return false;
    }
    public OutputStream writeFile( File file )
    {
        return null;
    }
    public void flushDirty() throws IOException
    {
        //if( dirty )
            saveToBuffer();
    }
    
    public boolean writeFileAttributes( File file, FileAttributes att )
    {
        return false;
    }
    protected void updateFromFileSystem( BufferedFile buffered ) throws IOException
    {
    	// load fs entries if wanted
        Collection fsChildren = buffered.getUnbuffered().getChildren();
        for( Iterator i = fsChildren.iterator(); i.hasNext(); )
        {
            File uf = (File)i.next();
            BufferedFile bf = (BufferedFile)buffered.getChild( uf.getName() );
            if( bf == null )
            {
            	bf = new AbstractBufferedFile( this, uf, root, uf.isDirectory(), false );
				buffered.addChild( bf );
            }
            if( bf.isDirectory() )
            	updateFromFileSystem( bf );
        }
    }
    protected void loadFromBuffer() throws IOException
    {
        File fsRoot = fs.getRoot();
        File f = fsRoot.getChild( ".syncfiles" );

        root = new AbstractBufferedFile(this, fsRoot, null, true, true );
        if( f == null || !f.exists() || f.isDirectory() )
        {
        	if( isMonitoringFileSystem() )
        		updateFromFileSystem( root );
            return;
        }
        ByteArrayOutputStream out;
        InputStream reader = null;
        try {
            // TODO if we dont require ftp calls while creating buffer use 
            //		input stream directly
            
	        out = new ByteArrayOutputStream((int)f.getFileAttributes().getLength());
	        
	        InputStream in = new GZIPInputStream( f.getInputStream() );
	        int i; byte[] block = new byte[1024];
	        while( (i = in.read(block)) > 0 )
	            out.write( block, 0, i );
	        in.close();
	        out.close();

	        reader = new ByteArrayInputStream( out.toByteArray() );
	        SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
	        sax.parse( reader, new SyncFileDefaultHandler( this ) );
            
        } catch( IOException ioe ) {
            ioe.printStackTrace();
        } catch( SAXParseException spe ) {
            StringBuffer sb = new StringBuffer( spe.toString() );
            sb.append("\n Line number: " + spe.getLineNumber());
            sb.append("\n Column number: " + spe.getColumnNumber() );
            sb.append("\n Public ID: " + spe.getPublicId() );
            sb.append("\n System ID: " + spe.getSystemId() + "\n");
            System.out.println( sb.toString() ); 
        } catch( SAXException e ) {
            e.printStackTrace();
        } catch( ParserConfigurationException e ) {
            e.printStackTrace();
        } catch( FactoryConfigurationError e ) {
            e.printStackTrace();
        } finally {
            try {
                if( reader != null )
                    reader.close();
            } catch( IOException e1 ) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        
        if( isMonitoringFileSystem() )
        	updateFromFileSystem( root );
    }
    protected Element serializeFile( BufferedFile file, Document doc ) throws IOException
    {
        Element elem = doc.createElement( file.isDirectory()?"Directory":"File" );
        elem.setAttribute( "Name", file.getName() );
        if( file.isDirectory() )
        {
            Collection items = file.getChildren();
            for( Iterator i = items.iterator(); i.hasNext(); )
            {
                File n = (File)i.next();
                if( !n.exists() )
                    continue;

                elem.appendChild( serializeFile( (BufferedFile)n, doc ) );
            }
    	} else {
	        elem.setAttribute( "BufferedLength", String.valueOf( file.getFileAttributes().getLength() ) );
	        elem.setAttribute( "BufferedLastModified", String.valueOf( file.getFileAttributes().getLastModified() ) );
	        elem.setAttribute( "FileSystemLength", String.valueOf( file.getFsFileAttributes().getLength() ) );
	        elem.setAttribute( "FileSystemLastModified", String.valueOf( file.getFsFileAttributes().getLastModified() ) );
        }
        return elem;
    }
    public void saveToBuffer() throws IOException
    {
        File fsRoot = fs.getRoot();
        File node = fsRoot.getChild( ".syncfiles" );
        if( node == null || !node.exists() )
        {
            node = root.createChild( ".syncfiles", false );
        }
        
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element e = doc.createElement( "SyncFiles" );
            e.appendChild( serializeFile( root, doc ) );
            doc.appendChild( e );
            
	        File f = (File)node;
	        OutputStream out = new GZIPOutputStream( f.getOutputStream() );
	        
	        OutputFormat format = new OutputFormat( doc, "UTF-8", true );
	        XMLSerializer serializer = new XMLSerializer ( out, format);
	        serializer.asDOMSerializer();
	        serializer.serialize(doc);
	        
	        out.close();
        } catch( IOException ioe ){
            ioe.printStackTrace();
        } catch( ParserConfigurationException e ) {
            e.printStackTrace();
        } catch( FactoryConfigurationError e ) {
            e.printStackTrace();
        }
    }
    
    public boolean isMonitoringFileSystem() 
	{
		return monitoringFileSystem;
	}
	public void setMonitoringFileSystem(boolean monitor) 
	{
		this.monitoringFileSystem = monitor;
	}
    public void flush() throws IOException
    {
        saveToBuffer();
        fs.flush();
    }
    public void close() throws IOException
    {
        fs.close();
    }
    
    public String getUri()
    {
        return fs.getUri();
    }

    public boolean isCaseSensitive()
	{
		return fs.isCaseSensitive();
	}

}