package net.sourceforge.wrabbitftp;
import java.util.*;
import java.net.*;
import java.io.*;

public class FTPParser {
	public FTPParser() {
	}

	public void parse(String command, FTPConnection parent) {
		StringTokenizer st = new StringTokenizer(command);
		String ftpCommand = null, commandArgs = null;

		ftpCommand = st.nextToken();

		System.out.println("[P] parse> " + command);

		if (ftpCommand.equalsIgnoreCase("USER")) {
			try {
				user(st.nextToken(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		if (ftpCommand.equalsIgnoreCase("PASS")) {
			try {
				pass(st.nextToken(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		//Check for user authentication. Any commands parsed before this point
		//are allowed even for unauthenticated users.
		if (!parent.getUser().isAuthenticated()) {
			parent.output("530 Login incorrect");
			return;
		}

		if (ftpCommand.equalsIgnoreCase("PWD")) {
			pwd(parent);
			return;
		}

		if (ftpCommand.equalsIgnoreCase("SYST")) {
			syst(parent);
			return;
		}

		if (ftpCommand.equalsIgnoreCase("PASV")) {
			pasv(parent);
			return;
		}

		if (ftpCommand.equalsIgnoreCase("QUIT")) {
			quit(parent);
			return;
		}

		if (ftpCommand.equalsIgnoreCase("LIST")) {
			list(parent, false);
			return;
		}

		if (ftpCommand.equalsIgnoreCase("NLST")) {
			list(parent, true);
			return;
		}

		if (ftpCommand.equalsIgnoreCase("CDUP")) {
			cdup(parent);
			return;
		}

		if (ftpCommand.equalsIgnoreCase("CWD")) {
			try {
				cwd(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		if (ftpCommand.equalsIgnoreCase("RETR")) {
			try {
				retr(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		if (ftpCommand.equalsIgnoreCase("TYPE")) {
			try {
				type(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

	    if (ftpCommand.equalsIgnoreCase("STRU")) {
			try {
		    	stru(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		if (ftpCommand.equalsIgnoreCase("MODE")) {
			try {
	        	mode(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

	    if (ftpCommand.equalsIgnoreCase("STOR")) {
			try {
			    stor(allRemainingTokens(st).trim(), parent, false);
			} catch (NoSuchElementException nse) {}
			return;
		}

	    if (ftpCommand.equalsIgnoreCase("APPE")) {
			try {
			    stor(allRemainingTokens(st).trim(), parent, true);
			} catch (NoSuchElementException nse) {}
			return;
		}

	    if (ftpCommand.equalsIgnoreCase("MDTM")) {
			try {
			    mdtm(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		if (ftpCommand.equalsIgnoreCase("PORT")) {
			try {
			    port(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

	    if (ftpCommand.equalsIgnoreCase("DELE")) {
			try {
			    dele(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		if (ftpCommand.equalsIgnoreCase("NOOP")) {
			noop(parent);
			return;
		}

		if (ftpCommand.equalsIgnoreCase("REST")) {
			try {
			    rest(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		if (ftpCommand.equalsIgnoreCase("RNFR")) {
			try {
			    rnfr(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		if (ftpCommand.equalsIgnoreCase("RNTO")) {
			try {
			    rnto(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		if (ftpCommand.equalsIgnoreCase("MKD")) {
			try {
			    mkd(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		if (ftpCommand.equalsIgnoreCase("RMD")) {
			try {
			    rmd(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		if (ftpCommand.equalsIgnoreCase("OPTS")) {
			try {
				opts(allRemainingTokens(st).trim(), parent);
			} catch (NoSuchElementException nse) {}
			return;
		}

		parent.output("500 Command '" + ftpCommand + "' not recognized or not supported");
	}

	/**
	*	Creates a single string that contains all the tokens that have not
	*	yet been parsed out of a given StringTokenizer.
	*/
	private String allRemainingTokens(StringTokenizer st) {
		String r = new String();

		while (st.hasMoreTokens()) {
			r = r + st.nextToken() + " ";
		}

		return r;
	}

	public void user(String commandArgs, FTPConnection parent) {
	    if (parent.getUser().scanFileForUser(commandArgs)) {
		    parent.output("331 Password required for " + commandArgs);
			parent.getUser().setUsername(commandArgs);
		} else {
			parent.output("530 Login incorrect");
		}
	}

	public void pass(String commandArgs, FTPConnection parent) {
		parent.getUser().setPassword(commandArgs);
		parent.getUser().authenticate();
		if (parent.getUser().isAuthenticated()) {
			parent.output("230 " + parent.getUser().getUsername() + " logged in");
		} else {
			parent.output("530 Login incorrect");
		}
	}

	public void pwd(FTPConnection parent) {
		parent.output("257 \"" + parent.getCurrentDirectory() + "\" is current directory");
	}

	public void syst(FTPConnection parent) {
		parent.output("215 UNIX");
	}

	public void pasv(FTPConnection parent) {
		parent.output("227 Entering Passive Mode (" + parent.getPassiveSocketAddress() + ")");
		parent.enterPassiveMode();
	}

	public void quit(FTPConnection parent) {
		parent.output("221 Goodbye!");
		parent.endSession();
	}

	public void cwd(String commandArgs, FTPConnection parent) {
		System.out.println("Directory request: " + commandArgs);

		if (parent.setDirectory(commandArgs)) {
			parent.output("250 CWD command successfull");
		} else {
			parent.output("550 Directory not found");
		}
	}

	public void cdup(FTPConnection parent) {
		parent.parentDirectory();
		parent.output("250 CDUP command successfull");
	}

	/**
	*	This outputs a list of filenames to the data connection.
	*	It will send details if the shortForm parameter is false, otherwise
	*	it sends just a stripped down list of file names.
	*/
	public void list(FTPConnection parent, boolean shortForm) {
		FTPObject thisChild;
		Vector children;
		int f;

		Socket listSocket = parent.getDataConnection();
		parent.output("150 Opening ASCII mode data connection for LIST command");

		try {
			OutputStream out = listSocket.getOutputStream();
        	PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));

			children = parent.getCurrentObject().getChildren();

			for(f=0;f<children.size();f++) {
				thisChild = (FTPObject)children.elementAt(f);
				if (FTPSecurityManager.viewable(thisChild, parent.getUser())) {
					if (shortForm) {
						if (parent.inDebugMode()) System.out.println("[L] " + parent.getUser().getUsername() + "> " + thisChild.toString());
						writer.println(thisChild.toString());
					} else {
						if (parent.inDebugMode()) System.out.println("[L] " + parent.getUser().getUsername() + "> " + formatObjectList(thisChild));
						writer.println(formatObjectList(thisChild));
					}
				}
			}

        	writer.close();
    		listSocket.close();
		} catch (Exception e) {
			System.out.println("[P] reply> Error LISTing");
			System.out.println(e);
			return;
		}

		parent.output("226 ASCII transfer complete");
	}

	/**
	*	Call the list method with a default of 'false' for the shortForm
	*	parameter, causing full details of the files to be returned.
	*/
	public void list(FTPConnection parent) {
		list(parent, false);
	}

	/**
	*	Put information about a particular object in a UNIX-ish listing
	*	suitable for return by the LIST command.
	*/
	private String formatObjectList(FTPObject obj) {
		String fs = new String();

		if (obj.isDirectory()) {
			fs = "dr-xr-xr-x 1 owner group 0";
		} else {
			fs = "-r-xr-xr-x 1 owner group " + obj.size();
		}

		fs = fs + " " + obj.getFormattedDate();
		fs = fs + " " + obj.toString();

		return fs;
	}

	public void retr(String commandArgs, FTPConnection parent) {
		if (parent.getBinaryMode()) {
			retrI(commandArgs, parent);
		} else {
			retrA(commandArgs, parent);
		}
	}

	public void type(String commandArgs, FTPConnection parent) {
		if (commandArgs.equalsIgnoreCase("I")) {
			parent.setBinaryMode(true);
			parent.output("200 Type set to I");
		} else {
			parent.setBinaryMode(false);
			parent.output("200 Type set to A");
		}
	}

	public void stru(String commandArgs, FTPConnection parent) {
		if (commandArgs.equalsIgnoreCase("F")) {
      		parent.output("200 Structure set to F");
       	} else if (commandArgs.equalsIgnoreCase("R")) {
	        parent.output("504 Structure cannot be set to R");
       	} else if (commandArgs.equalsIgnoreCase("P")) {
            parent.output("504 Structure cannot be set to P");
       	} else {
            parent.output("504 Structure cannot be set to " + commandArgs);
       	}
    }

	public void mode(String commandArgs, FTPConnection parent) {
		if (commandArgs.equalsIgnoreCase("S")) {
    	    parent.output("200 Mode set to S (Stream)");
      	} else if (commandArgs.equalsIgnoreCase("B")) {
    	    	parent.output("504 Mode cannot be set to B (Block)");
    	} else if (commandArgs.equalsIgnoreCase("C")) {
        	parent.output("504 Mode cannot be set to C (Compressed)");
    	} else {
        	parent.output("504 Mode cannot be set to " + commandArgs);
    	}
	}

	public void mdtm(String commandArgs, FTPConnection parent) {
    	parent.output("502 MDTM command not currently supported");
	}

	public void noop(FTPConnection parent) {
    	parent.output("200 NOOP succeeded");
	}

	/**
	*	This will STORe a file on the server. If the append argument is true
	*	this method will APPEnd to the existing file.
	*/
    public void stor(String commandArgs, FTPConnection parent, boolean append) {
       	if (parent.getBinaryMode()) {
    		storI(commandArgs, parent, append);
		} else {
        	storA(commandArgs, parent, append);
        }
	}

	public void storI(String commandArgs, FTPConnection parent, boolean append) {
		BufferedInputStream incomingData = null;
		BufferedOutputStream diskFile = null;
		int byt = -1;

		File saveFile = new File(parent.getPhysicalDirectory() + System.getProperty("file.separator") + commandArgs);
    	System.out.println("Attempt to retrieve for STOR the file (in binary): " + saveFile.toString());

		Socket listSocket = parent.getDataConnection();
    	parent.output("150 Opening BINARY mode data connection to receive " + commandArgs);

    	try {
    		incomingData = new BufferedInputStream(listSocket.getInputStream());
    		diskFile = new BufferedOutputStream(new FileOutputStream(saveFile, append));

    		byt = incomingData.read();
			while (byt >= 0) {
				if (byt >= 0) {
                	diskFile.write(byt);
				}
            byt = incomingData.read();
			}

         diskFile.flush();

         diskFile.close();
         incomingData.close();

    		parent.rebuildCurrentChildList();
    	} catch (Exception e) {
    		System.out.println("Error receiving file in BINARY format for STOR");
			System.out.println(e);
			return;
    	}

    	parent.output("226 BINARY transfer complete");
	}

	public void storA(String commandArgs, FTPConnection parent, boolean append) {
		BufferedReader incomingData = null;
    	PrintWriter diskFile = null;
    	String line = null;

		File saveFile = new File(parent.getPhysicalDirectory() + System.getProperty("file.separator") + commandArgs);
    	System.out.println("Attempt to retrieve for STOR the file: " + saveFile.toString());

		Socket listSocket = parent.getDataConnection();
    	parent.output("150 Opening ASCII mode data connection to receive " + commandArgs);

    	try {
        	incomingData = new BufferedReader(new InputStreamReader(listSocket.getInputStream()));
    		diskFile = new PrintWriter(new FileOutputStream(saveFile, append));

    		line = incomingData.readLine();
    		while (line != null) {
        		if (line != null) {
        			diskFile.println(line);
				}
        		line = incomingData.readLine();
			}

			diskFile.flush();

    		diskFile.close();
    		incomingData.close();

    		parent.rebuildCurrentChildList();
    	} catch (Exception e) {
    		System.out.println("Error receiving file in ASCII format for STOR");
			System.out.println(e);
			return;
    	}

    	parent.output("226 ASCII transfer complete");
   	}

   	public void port(String commandArgs, FTPConnection parent) {
		parent.setClientPort(commandArgs);
    	parent.output("200 PORT command succeded");
	}

	/**
	*	This method deletes the file named by the commandArgs string. The
	*	file must be in the current directory (i.e. a child of the current object).
	*/
	public void dele(String commandArgs, FTPConnection parent) {
		parent.getCurrentObject().delete(commandArgs);
     	parent.output("250 DELE command succeded, " + commandArgs + "deleted.");
	}

	/**
	*	This sets the offset from the beginning of the file at which transfers
	*	will begin.
	*/
	public void rest(String commandArgs, FTPConnection parent) {
		int position;

		try {
			position = Integer.parseInt(commandArgs);
		} catch (Exception e) {
  			parent.output("504 Resume position cannot be set to " + commandArgs);
  			return;
  		}

  		parent.setResumePosition(position);
    	parent.output("350 REST command succeded, transfers will begin at " + position);
	}

	/**
	*	Set the filename from which we will be renaming. This is the first
	*	half of the complete rename command.
	*/
	public void rnfr(String commandArgs, FTPConnection parent) {
		if (parent.getCurrentObject().childExists(commandArgs)) {
			parent.setRenameFrom(commandArgs);
			parent.output("350 RNFR command succeded, send RNTO command to proceed");
		} else {
			parent.output("550 File specified by RNFR does not exist");
		}
	}

	/**
	*	Set the new name of the file we are about to rename, then actually
	*	rename it. This is the second half of the rename command.
	*/
	public void rnto(String commandArgs, FTPConnection parent) {
		parent.renameTo(commandArgs);
		parent.output("250 RNTO command succeded");
	}

	/**
	*	Create a directory below the currently active directory.
	*/
	public void mkd(String commandArgs, FTPConnection parent) {
		parent.getCurrentObject().makeDirectory(commandArgs);
		parent.output("257 Directory created");
	}

	/**
	*	Delete a directory below the currently active directory.
	*	This method will trim the path to its last element before
	*	passing it to the delete method of the object.
	*/
	public void rmd(String commandArgs, FTPConnection parent) {
		String fixedName = commandArgs;

		if (commandArgs.indexOf('\\') > 0) {
			fixedName = commandArgs.substring(commandArgs.lastIndexOf('\\') + 1);
		} else if (commandArgs.indexOf('/') > 0) {
			fixedName = commandArgs.substring(commandArgs.lastIndexOf('/') + 1);
		}

		parent.getCurrentObject().delete(fixedName);
		parent.output("250 Directory '" + fixedName + "' removed");
	}

	/**
	*	Handle the multi-function OPTS commaned, since IE uses it to
	*	force UTF-8 encoding.
	*	OPTS currently supported:
	*		UTF-8
	*/
	public void opts(String commandArgs, FTPConnection parent) {
		String opt = null;
		StringTokenizer st = new StringTokenizer(commandArgs, " ");

		try {
			opt = st.nextToken();

			if (opt.equalsIgnoreCase("UTF8")) {
				parent.setOutflowCharset("UTF-8");
				parent.output("200 Output format forced to UTF-8");
				return;
			}

			throw new Exception("Bad OPTS command exception in FTPParser::opts");
		} catch (Exception ex) {
			parent.output("501 OPTS command '" + opt + "' not recognized or supported");
		}
	}
	
	
	
	public void retrA(String commandArgs, FTPConnection parent) {
		FTPObject sendableObject = null;
		PrintWriter writer = null;
		BufferedReader dataStream = null;
		String line = null;

		dataStream = new BufferedReader(parent.getCurrentObject().getReaderFromObject(commandArgs));

		if (dataStream == null) {
			System.out.println("Error sending file in ASCII format for RETR, file not found, or object not in child list");
			return;
		}

		Socket listSocket = parent.getDataConnection();
		parent.output("150 Opening ASCII mode data connection for " + commandArgs);

		try {
			if (parent.getResumePosition() > 0) dataStream.skip(parent.getResumePosition());

			writer = new PrintWriter(listSocket.getOutputStream());

			line = dataStream.readLine();
			while (line != null) {
				if (line != null) {
					writer.println(line);
					writer.flush();
				}
				line = dataStream.readLine();
			}

	      writer.close();
		   listSocket.close();
	    	dataStream.close();
		} catch (Exception e) {
			System.out.println("Error sending file in ASCII format for RETR");
			System.out.println(e);
			return;
		}

		parent.output("226 ASCII transfer complete");
	}

	public void retrI(String commandArgs, FTPConnection parent) {
		FTPObject sendableObject = null;
		BufferedOutputStream writer = null;
		BufferedInputStream dataStream = null;
		int byt = -1;

		dataStream = new BufferedInputStream(parent.getCurrentObject().getInputFromObject(commandArgs));

		if (dataStream == null) {
			System.out.println("Error sending file in BINARY format for RETR, file not found, or object not in child list");
			return;
		}

		Socket listSocket = parent.getDataConnection();
		parent.output("150 Opening BINARY mode data connection for " + commandArgs);

		try {
			if (parent.getResumePosition() > 0) dataStream.skip(parent.getResumePosition());
	
			writer = new BufferedOutputStream(listSocket.getOutputStream(), 1);

			byt = dataStream.read();
			while (byt >= 0) {
				if (byt >= 0) {
					writer.write(byt);
					writer.flush();
				}
				byt = dataStream.read();
			}

	      writer.close();
		   listSocket.close();
	    	dataStream.close();
		} catch (Exception e) {
			System.out.println("Error sending file in BINARY format for RETR");
			System.out.println(e);
			return;
		}

		parent.output("226 BINARY transfer complete");
	}
}
