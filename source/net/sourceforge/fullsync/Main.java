package net.sourceforge.fullsync;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Main
{
    public static void main( String[] args )
    {
        /* /
        ActionDeciderDebugger debug = new ActionDeciderDebugger();
        debug.debugActionDecider( new PublishActionDecider(), false, true );
        
        /* /
        Provider[] p = Security.getProviders();
        for( int i = 0; i < p.length; i++ )
            System.out.println( p[i].getName() );
        
        System.out.println( Security.getAlgorithms("KeyAgreement") );//, "DH" ) );
        KeyPairGenerator dhKeyPairGen = KeyPairGenerator.getInstance("DH");
        KeyAgreement dhKeyAgreement = KeyAgreement.getInstance("DH");
        /* /
    	
    	FullSync fs = new FullSync(true);
    	fs.start();
    	
    	/* */
        
        CommandLineInterpreter.parse( args );
        
        /* /
        FileSystemManager fsm = new FileSystemManager();
        Directory d1 = fsm.resolveUri( new URI( "file:/E:/Java/WebsiteSynchronizer/_testing/Source" ) );
        Directory d2 = fsm.resolveUri( new URI( "buffered:syncfiles:file:/C:/Temp/test2" ) );
        
        SyncRules rules = new SyncRules("UPLOAD");
		rules.setJustLogging( false );

		Task task = new Task();
		task.setSource( d1 );
		task.setDestination( d2 );
		task.setRules( rules );
		
		Buffer buffer = new BlockBuffer();
		buffer.load();
        ProcessorImpl c = new ProcessorImpl(  );
        c.setActionQueue( new FillBufferActionQueue( buffer ) );
        c.execute( task );
        buffer.unload();
        /* */ 
    }
}
