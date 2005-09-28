package net.sourceforge.wrabbitftp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import sun.io.Converters;

/**
*	The FTPConnection class maintains settings and provides methods to manipulate
*	a single connection to the server. There is a single static instance of the
*	FTPParser class shared between all FTPConnection objects. Each FTPConnection
*	is responsible for creating a single FTPUser object to represent the user
*	currently logged in through the connection.
*/
public class FTPConnection implements Runnable {
	private static final FTPParser parser = new FTPParser();

	private final FTPUser user = new FTPUser();

	private Socket incoming;
	private boolean running = false;
	private boolean inBinaryMode = false;
	private boolean debugMode = true;
	private BufferedReader inflow;
	private PrintWriter outflow;

	private String addressString;
	private String renameFrom;

	private ServerSocket passiveSocket;
	private Socket passiveIncoming;

	private Socket clientSocket;
	private String clientAddress;
	private int clientPort;

	private String currentDirectory;
	private FTPObject currentObject;
	private FTPObject rootObject;
	private FTPServer parent;
	private int resumePosition;

	private String outflowCharset;

	public FTPConnection(FTPServer p, Socket i, FTPObject startingObject, FTPObject root) {
		parent = p;

		incoming = i;
		clientPort = 0;

		currentObject = startingObject;
		rootObject = root;

		currentDirectory = currentObject.path();

		resumePosition = 0;

		outflowCharset = Converters.getDefaultEncodingName();
	}

	public void setBinaryMode(boolean tf) {
		inBinaryMode = tf;
	}

	public boolean getBinaryMode() {
		return inBinaryMode;
	}

	public String getCurrentDirectory() {
		return currentObject.completeObjectPath();
	}

	public String getPhysicalDirectory() {
		return currentObject.physicalObjectPath();
	}

	/**
	*	Try to change the working directory...
	*	First, attempt to change to a child directory of the current directory,
	*	if that fails, attempt to change to a child of the root object,
	*	if that fails, check for the presence of '/root/' at the start of
	*	the path, remove it, and attempt the change again.
	*/
	public boolean setDirectory(String newDir) {
		FTPObject attempt = currentObject.childAsDirectory(newDir);

		System.out.println("[C] setDirectory on " + this);

		if (attempt != null) {
			System.out.println("[C] Relative change directory attempt succeeded");
			currentObject = attempt;
			currentDirectory = currentObject.path();
			return true;
		} else {
			System.out.println("[C] Relative change directory attempt failed");
			attempt = rootObject.nestedDirectory(newDir);
			if (attempt != null) {
				System.out.println("[C] Nested change directory attempt succeeded");
				currentObject = attempt;
				currentDirectory = currentObject.path();
				return true;
			} else {
				System.out.println("[C] Nested change directory attempt failed");
				if (newDir.startsWith("/root/")) {
					newDir = newDir.substring(new String("/root/").length(), newDir.length());
					System.out.println("[C] Reduced directory to " + newDir);
					return setDirectory(newDir);
				}
				return false;
			}
		}
	}

	public void parentDirectory() {
		currentObject = currentObject.parentAsDirectory();
		currentDirectory = currentObject.path();
	}

	public void rebuildFlows(Socket i) {
		try {
			outflow = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream(), outflowCharset), true);
			inflow = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
		} catch (Exception e) {
			System.out.println("Error rebuilding flows");
			System.out.println(e);
			return;
		}
	}

	public void start() {
		running = true;
		try {
			rebuildFlows(incoming);
		} catch (Exception e) {
			System.out.println("Error creating flows");
			System.out.println(e);
			return;
		}
		new Thread(this).start();
	}

	public void stop() {
		running = false;
		try { passiveIncoming.close(); }	catch (Exception e) { }
		try { passiveSocket.close(); }		catch (Exception e) { }
		try { clientSocket.close(); } 	 	catch (Exception e) { }
		try { inflow.close(); } 		 	catch (Exception e) { }
		try { outflow.close(); } 	 	 	catch (Exception e) { }
		try { incoming.close();	} 		 	catch (Exception e) { }

		parent.signalConnectionTerminated(this);
	}

	public void run() {
		String incomingString = null;

		try {
            output("220 AinGEN/FTP Server Alpha5");
		} catch (Exception e) {
			System.out.println("Error with client outflow");
			System.out.println(e);
			stop();
		}

		while (running) {
			try {
				if (inflow.ready()) {
					incomingString = inflow.readLine();
					if (incomingString == null) {
						throw new IOException("Client inflow interrupted during standard processing");
					} else {
						parser.parse(incomingString, this);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				stop();
			}
		}

		System.out.println("Client terminated connection normally");
		stop();
	}

	public void output(String out) {
		try {
			if (debugMode) System.out.println("[C] " + user.getUsername() + "> " + out);
			outflow.println(out);
		} catch (Exception e) {
			System.out.println("Error printing to outflow");
		}
	}

	public void endSession() {
		running = false;
	}

	public FTPUser getUser() {
		return user;
	}

	public ServerSocket getPassiveSocket() {
		if (passiveSocket == null) {
			return createPassiveSocket();
		}
		return passiveSocket;
	}

	public String getPassiveSocketAddress() {
		if (passiveSocket != null) {
			try {
				passiveIncoming.close();
				passiveSocket.close();
				passiveSocket = null;
			} catch (Exception e) { }
		}
		createPassiveSocket();
		return addressString;
	}

	public ServerSocket createPassiveSocket() {
		int localPort, highPort, lowPort;

		if (passiveSocket != null) {
			return passiveSocket;
		}

		try {
		    passiveSocket = new ServerSocket(0, 1, InetAddress.getLocalHost());
		} catch (Exception e) {
			System.out.println("Error creating passiveSocket");
			System.out.println(e);
			return null;
		}
		localPort = passiveSocket.getLocalPort();
		highPort = localPort >> 8;
		lowPort = localPort & 0xff;
		addressString = passiveSocket.getInetAddress().getHostAddress().replace('.', ',');
		addressString = addressString + "," + highPort + "," + lowPort;

		System.out.println("Passive socket created (" + addressString + ")");

		return passiveSocket;
	}

	public void enterPassiveMode() {
		try {
			passiveIncoming = passiveSocket.accept();
		} catch (Exception e) {
			System.out.println("Error while awaiting connection for passiveSocket");
			System.out.println(e);
			return;
		}

		rebuildFlows(passiveIncoming);
	}

	public void setClientPort(String portString) {
		StringTokenizer st = new StringTokenizer(portString, ",");
		int portHi, portLo;

		clientAddress = new String(st.nextToken() + "." + st.nextToken() + "." + st.nextToken() + "." + st.nextToken());

		try {
			portHi = new Integer(st.nextToken()).intValue();
			portLo = new Integer(st.nextToken()).intValue();
			clientPort = (portHi << 8) | portLo;
		} catch (Exception e) {
			System.out.println("PORT command failed, could not interperate port value");
		}
	}

	public Socket getDataConnection() {
		if (passiveSocket != null && passiveIncoming != null) {
			return passiveIncoming;
		} else {
			if (clientPort > 0) {
				try {
					clientSocket = new Socket(clientAddress, clientPort);
				} catch (Exception e) {
					return null;
				}
				return clientSocket;
			}
		}
		return null;
	}

	public FTPObject getCurrentObject() {
		return currentObject;
	}

	public void rebuildCurrentChildList() {
		currentObject.buildChildList();
	}

	/**
	*	Sets the position in a file at which transfers begin.
	*/
	public void setResumePosition(int r) {
		resumePosition = r;
	}

	/**
	*	Returns the position in a file at which a transfer should begin.
	*/
	public int getResumePosition() {
		return resumePosition;
	}

	/**
	*	Returns a usefull string representation of this FTPConnection object.
	*/
	public String toString() {
		return "Connection for " + user.toString() + " in " + currentObject.toString();
	}

	/**
	*	Returns the setting of this connections debugMode parameter.
	*/
	public boolean inDebugMode() {
		return debugMode;
	}

	/**
	*	Set the source name for a rename command
	*/
	public void setRenameFrom(String from) {
		renameFrom = from;
	}

	/**
	*	This function locates the file specified by the renameFrom variable and
	*	renames it to the argument passed. It then clears the renameFrom variable.
	*/
	public void renameTo(String to) {
		currentObject.renameChild(renameFrom, to);
		renameFrom = new String("");
	}

	/**
	*	Change the charset for the output and rebuild the input reader & output writer
	*	for the socket.
	*/
	public void setOutflowCharset(String cs) {
		outflowCharset = cs;
		rebuildFlows(incoming);
	}
}

