package net.sourceforge.fullsync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Hashtable;

import junit.framework.TestCase;
import net.sourceforge.fullsync.impl.AdvancedRuleSetDescriptor;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class PublishUpdateTest extends TestCase
{
    private File testingDir;
    private File testingSource;
    private File testingDestination;
    
    private Synchronizer synchronizer;
    private Profile profile;

    protected void setUp() throws Exception
    {
        testingDir = new File( "testing" );
        testingSource = new File( testingDir, "source" );
        testingDestination = new File( testingDir, "destination" );
        
        testingDir.mkdirs();
        testingSource.mkdir();
        testingDestination.mkdir();
        
        synchronizer = new Synchronizer();
        profile = new Profile();
        profile.setName( "TestProfile" );
        profile.setSource( new ConnectionDescription( testingSource.toURI().toString(), "" ) );
        profile.setDestination( new ConnectionDescription( testingDestination.toURI().toString(), "syncfiles" ) );
        profile.setRuleSet( new AdvancedRuleSetDescriptor( "UPLOAD" ) );
        profile.setSynchronizationType( "Publish/Update" );
        
        clearUp();

        super.setUp();
    }
    protected void tearDown() throws Exception
    {
        clearUp();
        testingSource.delete();
        testingDestination.delete();
        testingDir.delete();
        
        super.tearDown();
    }
    
    protected void clearDirectory( File dir )
    {
        File[] files = dir.listFiles();
        for( int i = 0; i < files.length; i++ )
        {
            if( files[i].isDirectory() )
                clearDirectory( files[i] );
            files[i].delete();
        }
    }
    protected void clearUp()
    	throws IOException
    {
        clearDirectory( testingSource );
        clearDirectory( testingDestination );
    }
    protected void createRuleFile()
    	throws IOException
    {
        createNewFileWithContents( 
                testingSource, 
                ".syncrules", 
                new Date().getTime(),
                 "START RULESET UPLOAD\n"
            	+"	USE RULEFILES SOURCE\n"
            	+"	USE DIRECTION DESTINATION\n"
            	+"	USE RECURSION YES\n"
            	+"	USE RECURSIONONIGNORE YES\n"
                +"\n"
            	+"	APPLY IGNORERULES YES\n"
            	+"	APPLY TAKERULES YES\n"
            	+"	APPLY DELETION DESTINATION\n"
                +"\n"
            	+"	DEFINE IGNORE \"^[.].+\"\n"
            	+"	DEFINE SYNC \"length != length\"\n"
            	+"	DEFINE SYNC \"date != date\"\n"
            	+"END RULESET UPLOAD" );
    }
    protected void createNewDir( File dir, String dirname, long lastModified )
    {
        File d = new File( dir, dirname );
        d.mkdir();
        d.setLastModified( lastModified );
    }
    protected PrintStream createNewFile( File dir, String filename )
		throws IOException
	{
	    File file = new File( dir, filename );
	    file.createNewFile();
	    PrintStream out = new PrintStream( new FileOutputStream( file ) );
	    return out;
	}
	protected void createNewFileWithContents( File dir, String filename, long lm, String content )
		throws IOException
	{
	    PrintStream out = createNewFile( dir, filename );
	    out.print( content );
	    out.close();
	    
	    new File( dir, filename ).setLastModified( lm );
	}
	protected TaskTree assertPhaseOneActions( final Hashtable expectation )
		throws Exception
	{
	    TaskGenerationListener list = new TaskGenerationListener() {
	        public void taskGenerationFinished( Task task )
	        {
	            Object ex = expectation.get( task.getSource().getName() );
	            assertNotNull( "Unexpected generated Task for file: "+task.getSource().getName(), ex );
	            assertTrue( "Action was "+task.getCurrentAction()+", expected: "+ex+" for File "+task.getSource().getName(), 
	                    task.getCurrentAction().equalsExceptExplanation((Action)ex) );
	        }
	        public void taskGenerationStarted(
	                net.sourceforge.fullsync.fs.File source,
	                net.sourceforge.fullsync.fs.File destination )
	        {}
	        public void taskTreeFinished( TaskTree tree ) {}
	        public void taskTreeStarted( TaskTree tree ) {}
	    };
	    
	    TaskGenerator processor = synchronizer.getTaskGenerator();
	    processor.addTaskGenerationListener( list ); 
	    TaskTree tree = processor.execute( profile );
	    processor.removeTaskGenerationListener( list );
	    
	    return tree;
	}
    
    protected void assertFilesEqual( File srcDir, File dstDir, String filename )
    {
        File src = new File( srcDir, filename );
        File dst = new File( dstDir, filename );
        
        assertEquals( src.length(), dst.length() );
        assertEquals( src.lastModified(), dst.lastModified() );
    }
    
    public void testBasicSynchronization()
    	throws Exception
    {
        createRuleFile();
        long lm = new Date().getTime();
        Hashtable expectation = new Hashtable();
        TaskTree tree;
        
        // Creating files and dirs
        createNewFileWithContents( testingSource, "inSync.txt", lm, "This file will stay in sync" );
        createNewFileWithContents( testingSource, "changeSource.txt", lm, "This file will be changed in source." );
        createNewFileWithContents( testingSource, "changeDestination.txt", lm, "This file will be changed in destination." );
        createNewFileWithContents( testingSource, "changeBoth.txt", lm, "This file will be changed in source and destination." );
        createNewFileWithContents( testingSource, "deleteSource.txt", lm, "This file will be deleted in source." );
        createNewFileWithContents( testingSource, "deleteDestination.txt", lm, "This file will be deleted in source." );
        createNewFileWithContents( testingSource, "deleteBoth.txt", lm, "This file will be deleted in source." );
        createNewFileWithContents( testingSource, "fileToDirSource.txt", lm, "This file will get a dir in source." );
        createNewFileWithContents( testingSource, "fileToDirDestination.txt", lm, "This file will get a dir in destination." );
        createNewFileWithContents( testingSource, "fileToDirBoth.txt", lm, "This file will get a dir in source and destination." );
        createNewDir( testingSource, "inSync", lm ); // This dir will stay in sync.
        createNewDir( testingSource, "changeSource", lm ); // "This file will be changed in source." );
        createNewDir( testingSource, "changeDestination", lm ); // "This file will be changed in destination." );
        createNewDir( testingSource, "changeBoth", lm ); // "This file will be changed in source and destination." );
        createNewDir( testingSource, "deleteSource", lm ); // This dir will get a file." );
        createNewDir( testingSource, "deleteDestination", lm ); // This dir will get a file." );
        createNewDir( testingSource, "deleteBoth", lm ); // This dir will get a file." );
        createNewDir( testingSource, "dirToFileSource", lm ); // This dir will get a file." );
        createNewDir( testingSource, "dirToFileDestination", lm ); // This dir will get a file." );
        createNewDir( testingSource, "dirToFileBoth", lm ); // This dir will get a file." );
        
        // Setting expectations for initial synchronization
        expectation.clear();
        expectation.put( "inSync.txt", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "changeSource.txt", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "changeDestination.txt", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "changeBoth.txt", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "deleteSource.txt", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "deleteDestination.txt", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "deleteBoth.txt", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "fileToDirSource.txt", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "fileToDirDestination.txt", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "fileToDirBoth.txt", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        
        expectation.put( "inSync", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "changeSource", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "changeDestination", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "changeBoth", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "deleteSource", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "deleteDestination", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "deleteBoth", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "dirToFileSource", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "dirToFileDestination", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "dirToFileBoth", new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "" ) );
        
        /* Phase One: */ tree = assertPhaseOneActions( expectation );
        /* Phase Two: */ synchronizer.performActions( tree ); // TODO assert task finished events ?
        
        // Now changing files and dirs and setting expectations
        expectation.clear();
        expectation.put( "inSync.txt", new Action( Action.Nothing, Location.None, BufferUpdate.None, "" ) );
        
        setLastModified( testingSource, "changeSource.txt", lm+10000 );
        setLastModified( testingDestination, "changeDestination.txt", lm+10000 );
        setLastModified( testingSource, "changeBoth.txt", lm+10000 );
        setLastModified( testingDestination, "changeBoth.txt", lm+10000 );
        expectation.put( "changeSource.txt", new Action( Action.Update, Location.Destination, BufferUpdate.Destination, "" ) );
        expectation.put( "changeDestination.txt", new Action( Action.Nothing, Location.None, BufferUpdate.None, "" ) );
        expectation.put( "changeBoth.txt", new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, "" ) );
        
        delete( testingSource, "deleteSource.txt" );
        delete( testingDestination, "deleteDestination.txt" );
        delete( testingSource, "deleteBoth.txt" );
        delete( testingDestination, "deleteBoth.txt" );
        expectation.put( "deleteSource.txt", new Action( Action.Delete, Location.Destination, BufferUpdate.Destination, "", false ) );
        expectation.put( "deleteDestination.txt", new Action( Action.Nothing, Location.None, BufferUpdate.None, "" ) );
        expectation.put( "deleteBoth.txt", new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, "" ) );
        
        fileToDir( testingSource, "fileToDirSource.txt" );
        fileToDir( testingDestination, "fileToDirDestination.txt" );
        fileToDir( testingSource, "fileToDirBoth.txt" );
        fileToDir( testingDestination, "fileToDirBoth.txt" );
        expectation.put( "fileToDirSource.txt", new Action( Action.DirHereFileThereError, Location.Source, BufferUpdate.None, "" ) );
        expectation.put( "fileToDirDestination.txt", new Action( Action.Nothing, Location.None, BufferUpdate.None, "" ) );
        expectation.put( "fileToDirBoth.txt", new Action( Action.Nothing, Location.None, BufferUpdate.None, "" ) );
        
        expectation.put( "inSync", new Action( Action.Nothing, Location.None, BufferUpdate.None, "" ) );
        
        setLastModified( testingSource, "changeSource", lm+10000 );
        setLastModified( testingDestination, "changeDestination", lm+10000 );
        setLastModified( testingSource, "changeBoth", lm+10000 );
        setLastModified( testingDestination, "changeBoth", lm+10000 );
        expectation.put( "changeSource", new Action( Action.Nothing, Location.None, BufferUpdate.None, "" ) );
        expectation.put( "changeDestination", new Action( Action.Nothing, Location.None, BufferUpdate.None, "" ) );
        expectation.put( "changeBoth", new Action( Action.Nothing, Location.None, BufferUpdate.None, "" ) );
        
        delete( testingSource, "deleteSource" );
        delete( testingDestination, "deleteDestination" );
        delete( testingSource, "deleteBoth" );
        delete( testingDestination, "deleteBoth" );
        expectation.put( "deleteSource", new Action( Action.Delete, Location.Destination, BufferUpdate.Destination, "", false ) );
        expectation.put( "deleteDestination", new Action( Action.Nothing, Location.None, BufferUpdate.None, "" ) );
        expectation.put( "deleteBoth", new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, "" ) );
        
        dirToFile( testingSource, "dirToFileSource" );
        dirToFile( testingDestination, "dirToFileDestination" );
        dirToFile( testingSource, "dirToFileBoth" );
        dirToFile( testingDestination, "dirToFileBoth" );
        expectation.put( "dirToFileSource", new Action( Action.DirHereFileThereError, Location.Destination, BufferUpdate.None, "" ) );
        expectation.put( "dirToFileDestination", new Action( Action.Nothing, Location.None, BufferUpdate.None, "" ) );
        expectation.put( "dirToFileBoth", new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, "" ) );
        
        /* Phase One: */ tree = assertPhaseOneActions( expectation );
        /* Phase Two: */ synchronizer.performActions( tree ); // TODO assert task finished events ?
    }
    
    protected void setLastModified( File dir, String filename, long lm )
    {
        File file = new File( dir, filename );
        file.setLastModified( lm );
    }
    protected void delete( File dir, String filename )
    {
        File file = new File( dir, filename );
        file.delete();
    }
    protected void fileToDir( File dir, String filename )
    {
        File file = new File( dir, filename );
        long lm = file.lastModified();
        file.delete();
        file.mkdir();
        file.setLastModified( lm );
    }
    protected void dirToFile( File dir, String filename )
    	throws IOException
    {
        File file = new File( dir, filename );
        long lm = file.lastModified();
        file.delete();
        file.createNewFile();
        file.setLastModified( lm );
    }


}
