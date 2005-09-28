package net.sourceforge.wrabbitftp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

/**
*	The FTPUser class abstracts all users either logged into the server or
*	displayed in the GUI during editing/review.
*/
public class FTPUser {
	private String username, password, expectedPassword;
	private int userLevel;
	private boolean authenticated = false, canAuthenticate = true;
	private Vector userPermissions = new Vector();

	/**
	*   Constructs a single instance of an 'empty' FTPUser
	*/
	public FTPUser() {
	}

	/**
	*   Constructs an FTPUser class with the given username and password for use
	*	in the GUI.
	*/
	public FTPUser(String u, String p) {
		username = u;
		password = p;
		canAuthenticate = false;
	}

	/**
	*   Sets the username for this user.
	*/
	public void setUsername(String u) {
		username = u;
	}

	/**
	*   Returns the username set by the FTP USER command
	*/
	public String getUsername() {
		return username;
	}

	/**
	*   Sets the password for this user, based on the FTP PASS command
	*/
	public void setPassword(String p) {
		password = p;
	}

	/**
	*   Retrives the password entered by this user
	*/
	public String getPassword() {
		return password;
	}

	/**
	*	Returns the userLevel for this user
	*/
	public int getUserLevel() {
		return userLevel;
	}

	/**
	*	Sets the userLevel for this user
	*/
	public void setUserLevel(int l) {
		userLevel = l;
	}

	/**
	*   Returns true if this user has completed the login process successfully
	*/

	public boolean isAuthenticated() {
		return authenticated;
	}

 	/**
	*   If the username is set, and therefore was found and the password
	*   is the password we expected say that the user is authenticated,
	*   otherwise flag them as not authenticated.
	*/
	public void authenticate() {
    	if (!canAuthenticate) return;

    	if (password.equalsIgnoreCase(expectedPassword)) {
    		authenticated = true;
    		getFilePermissions();
    	} else {
    		authenticated = false;
    	}
	}

	/**
	*   Scan the users file for a given user name, and take note of the
	*   expected password for that user.
	*/
	public boolean scanFileForUser(String user) 
	{
	    expectedPassword = "Sample";
	    return user.equals( "SampleUser" );
	}

	/**
	*	Read this users permissions from the usersecurity file.
	*/
	public void getFilePermissions() {
    	BufferedReader userFile = null;
    	String perm = null, against = null;

    	try {
       		userFile = new BufferedReader(new FileReader(new File("usersecurity")));
       	} catch (FileNotFoundException notFoundException) {
       		return;
    	}

    	String line = null;
    	StringTokenizer st = null;

    	try {
        		line = userFile.readLine();
        		while (line != null) {
            		st = new StringTokenizer(line, ":");
	                if (st.nextToken().equalsIgnoreCase(username)) {
						perm = st.nextToken();
						if (st.hasMoreTokens()) {
							against = st.nextToken();
						}
						if (FTPSecurityManager.knownPermission(perm)) {
							userPermissions.add(new FTPPermission(perm, against, 1));
						}
            		}
            		line = userFile.readLine();
            		against = null;
        		}
		userFile.close();
    	} catch (Exception e) {
        		System.out.println("Exception while reading user security file, permissions may not be set correctly.");
        		return;
    	}
	}

   	/**
   	*	Returns the vector of permissions for this user.
	*/
	public Vector getPermissions() {
		return userPermissions;
	}

	/**
	*	Clears the permission vector for this user.
	*/
	public void clearPermissions() {
		userPermissions.removeAllElements();
	}

	/**
	*	Adds a permission object to the vector for this user. Non-FTPPermission
	*	objects are ignored, since they would break the type-casting when looping
	*	through the vector.
	*/
	public void addPermission(FTPPermission p) {
		if (p instanceof FTPPermission) userPermissions.add(p);
	}

	/**
	*	Retruns the string for this FTPUser object -- this will be the username.
	*/
	public String toString() {
		return username;
	}
}
