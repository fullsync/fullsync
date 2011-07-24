/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */
package net.sourceforge.fullsync;

import net.sourceforge.fullsync.cli.CommandLineInterpreter;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Main {
	public static void main(String[] args) {
		/*
		 * /
		 * ActionDeciderDebugger debug = new ActionDeciderDebugger();
		 * debug.debugActionDecider( new PublishActionDecider(), false, true );
		 *
		 * /* /
		 * Provider[] p = Security.getProviders();
		 * for( int i = 0; i < p.length; i++ )
		 * System.out.println( p[i].getName() );
		 *
		 * System.out.println( Security.getAlgorithms("KeyAgreement") );//, "DH" ) );
		 * KeyPairGenerator dhKeyPairGen = KeyPairGenerator.getInstance("DH");
		 * KeyAgreement dhKeyAgreement = KeyAgreement.getInstance("DH");
		 * /* /
		 *
		 * FullSync fs = new FullSync(true);
		 * fs.start();
		 *
		 * /*
		 */
		CommandLineInterpreter.parse(args);

		/*
		 * /
		 *
		 * Profile pr;
		 * Profile p = new Profile(
		 * "Test",
		 * new ConnectionDescription("file:/E:/testing/source", ""),
		 * new ConnectionDescription("file:/E:/testing/destination", ""),
		 * new SimplyfiedRuleSetDescriptor( true, "", "" ) );
		 *
		 * try {
		 * PipedOutputStream pipeOut = new PipedOutputStream();
		 * PipedInputStream pipeIn = new PipedInputStream( pipeOut );
		 * ObjectOutputStream out = new ObjectOutputStream( pipeOut );
		 * ObjectInputStream in = new ObjectInputStream( pipeIn );
		 *
		 * out.writeObject( p );
		 * pr = (Profile)in.readObject();
		 *
		 * out.close();
		 * in.close();
		 * } catch( Exception ex ) { ExceptionHandler.reportException( ex ); }
		 *
		 * /* /
		 * FileSystemManager fsm = new FileSystemManager();
		 * Directory d1 = fsm.resolveUri( new URI( "file:/E:/Java/WebsiteSynchronizer/_testing/Source" ) );
		 * Directory d2 = fsm.resolveUri( new URI( "buffered:syncfiles:file:/C:/Temp/test2" ) );
		 *
		 * SyncRules rules = new SyncRules("UPLOAD");
		 * rules.setJustLogging( false );
		 *
		 * Task task = new Task();
		 * task.setSource( d1 );
		 * task.setDestination( d2 );
		 * task.setRules( rules );
		 *
		 * Buffer buffer = new BlockBuffer();
		 * buffer.load();
		 * TaskGeneratorImpl c = new TaskGeneratorImpl( );
		 * c.setTaskExecutor( new FillBufferTaskExecutor( buffer ) );
		 * c.execute( task );
		 * buffer.unload();
		 * /*
		 */
	}
}
