package net.sourceforge.wrabbitftp;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
*	This class creates the initial physical and virtual root objects for the
*	FTP service, reads and parses the general configuration file, and does some
*	basic/background management for the GUI. The FTPServer object also accepts
*	new client connections and creates FTPConnection objects for them.
*/
public class FTPServer extends Thread {
	//Static, final, identifying variables used to display the startup banner
	public static final String TITLE = "Wrabbit FTP Server";
	public static final String VERSION = "Version Alpha 6";
	public static final String COPYRIGHT = "Authors : Charles R Berube & Philippe Bouvart";
	public static final String INFORMATION1 = "http://www.orderlychaos.com";
	public static final String INFORMATION2 = "http://sourceforge.net/projects/wrabbitftp/";

	//Connection and network related variables
	private ServerSocket server;
	private Socket incoming;
	private Vector activeConnections;

	//Internal server status and file system information
	private boolean running = false;
	private FTPObject rootObject;

	//Server settings and configuration files
	private int port = 21;

	/**
	*	Construct an instance of the FTPServer object.
	*/
	public FTPServer( int port ) 
	{
	    this.port = port;
		activeConnections = new Vector();
		//setRoot( "root", "root" );
		
		initialize();
	}

	/**
	*	Complete the creation and setup of the FTPServer object.
	*	Needs to be seperate to support command line parameters well.
	*/
	private void initialize() {
		try {
			server = new ServerSocket(port, 5);
			server.setSoTimeout(100);			
		} catch (Exception e) {
			System.out.println("[S] startup> Server creation failed.");
			System.out.println("    java error> " + e);
			System.exit(1);
		}
	}
	
	public void setRoot( String ftpPath, File realPath )
	{
	    rootObject = new FTPObject(ftpPath, realPath, null);
	}

	/**
	*	Parse the virtualroots file and try to attach a virtual root to
	*	each path listed in it. Prints a notice, but does not crash out
	*	if a virtual root cannot be established.
	*/
	public void addVirtualRoot( String ftpPath, String realPath ) 
	{
	    rootObject.addChild(new FTPObject(ftpPath, new File(realPath), rootObject));
	}

	/**
	*	Waits for and accepts client connections.
	*/
	public void run() 
	{
		FTPConnection newConnection;
		byte[] connectionCheck = new byte[7];

		running = true;

		System.out.println("[S] startup> Port: " + server.getLocalPort());
		
		while (running) 
		{
			try {
                incoming = server.accept();
                newConnection = new FTPConnection(this, incoming, rootObject, rootObject);
                newConnection.start();
                activeConnections.add(newConnection);
            } catch( InterruptedIOException e1 ) {
                yield();
            } catch( IOException e1 ) {
                e1.printStackTrace();
            }
		}
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("IOException while closiong FTP server : "+e);
		}
		System.out.println("[S] FTP Server ended on port :"+server.getLocalPort());
		//try {sleep(5000); } catch (InterruptedException e) {}
	}

	/**
	*	Returns true if the server thread is running
	*/
	public boolean isRunning() {
		return running;
	}

	/**
	*	Return the server's root object (the top of the directory
	*	tree).
	*/
	public FTPObject getRoot() {
		return rootObject;
	}

	/**
	*	Return the vector of active connections.
	*/
	public Vector getActiveConnections() {
		return activeConnections;
	}

	/**
	*	Called by an active FTPConnection object just as it becomes
	*	inactive, normally, or due to an error.
	*/
	public void signalConnectionTerminated(FTPConnection connection) {
		activeConnections.remove(connection);
	}

	/**
	*	Stop the thread by setting running to false
	*/
	public void stopRunning() {
		running = false;
	}
}