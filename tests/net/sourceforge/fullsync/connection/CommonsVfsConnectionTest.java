package net.sourceforge.fullsync.connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import junit.framework.TestCase;
import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.fs.connection.CommonsVfsConnection;

public class CommonsVfsConnectionTest extends TestCase
{
    public void testLocal()
        throws Exception
    {
        String testSample = "Hello,\nthis is some nice text sample!";
        
        File testingDir = new File( "testing" );
        testingDir.mkdirs();
        File sample = new File( testingDir, "sample" );
        FileWriter writer = new FileWriter( sample );
        writer.write( testSample );
        writer.close();
        
        
        ConnectionDescription connectionDescription = new ConnectionDescription(testingDir.toURI().toString(),"");
        CommonsVfsConnection conn = new CommonsVfsConnection( connectionDescription );
        net.sourceforge.fullsync.fs.File root = conn.getRoot();
        assertTrue( root.exists() );
        
        Collection children = root.getChildren();
        assertEquals( 1, children.size() );
        
        net.sourceforge.fullsync.fs.File sampleFile = (net.sourceforge.fullsync.fs.File)children.iterator().next();
        assertNotNull( sampleFile );
        
        assertTrue( sampleFile.exists() );
        
        assertEquals( "sample", sampleFile.getName() );
        
        InputStream in = sampleFile.getInputStream();
        BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
        
        assertEquals( "Hello,", reader.readLine() );
        assertEquals( "this is some nice text sample!", reader.readLine() );
        
        reader.close();
        
        sample.delete();
        testingDir.delete();
    }
}
