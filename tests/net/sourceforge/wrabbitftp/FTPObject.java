package net.sourceforge.wrabbitftp;
import java.io.*;
import java.util.*;

/**
*	The FTPObject class abstracts all files, directories, virtual roots, and drives
*	that exist on the server. The class also provides methods to manipulate individual
*	files/directories and to assist in moving from one directory to another and
*	listing child objects. Technically an FTPObject does not need to have a physical
*	file representation, but in practice, they generally do.
*	<br><br>
*	This class could be extended to provide various kinds of special functionality,
*	such as pulling records from a database and treating them as files, or subdividing
*	archieve files for display over the FTP connection.
*/
public class FTPObject {
	private FTPObject parent;

	private File diskFile;

	private Vector children;
	private String objectName;
	private String objectPath;
	private long objectSize;
	private boolean canBeDir;

	private Vector filePermissions;

	public FTPObject(String name, String mypath, long mysize, boolean dir, FTPObject p) {
		children = null;

		diskFile = null;
		objectName = name;
		objectSize = mysize;
		parent = p;
		canBeDir = dir;
		objectPath = mypath;

		getFilePermissions();
	}

	public FTPObject(String name, File realFile, FTPObject p) {
		children = null;

		diskFile = realFile;
		objectName = name;
		objectSize = diskFile.length();
		parent = p;
		canBeDir = diskFile.isDirectory();
		objectPath = diskFile.getAbsolutePath();

		getFilePermissions();
	}

	public FTPObject(File realFile, FTPObject p) {
		children = null;

		diskFile = realFile;
		objectName = diskFile.getName();
		objectSize = diskFile.length();
		parent = p;
		canBeDir = diskFile.isDirectory();
		objectPath = diskFile.getAbsolutePath();

		getFilePermissions();
	}

	public void addChild(FTPObject c) {
		if (children == null) {
			children = new Vector();
		}

		children.add(c);
	}

	public void clearChildren() {
		children.removeAllElements();
		children = null;
	}

	public String path() {
		return objectPath;
	}

	public long size() {
		return objectSize;
	}

	public boolean isDirectory() {
		return canBeDir;
	}

	public void changeDirectoryTo() {
		buildChildList();
	}

	public String toString() {
		return objectName;
	}

	public FTPObject getParent() {
		return parent;
	}

	public String physicalObjectPath() {
		return diskFile.getAbsolutePath();
	}

	public String completeObjectPath() {
		FTPObject working = this;
		String cpath;

		cpath = working.toString();
		working = working.getParent();

		while (working != null) {
			cpath = working.toString() + "\\" + cpath;
			working = working.getParent();
		}

		return cpath;
	}

	public InputStream getInputFromObject() {
		if (isDirectory()) {
			return null;
		}

		try {
			return new FileInputStream(diskFile);
		} catch (Exception e) {
			return null;
		}
	}

	public FileReader getReaderFromObject() {
		if (isDirectory()) {
			return null;
		}

		try {
			return new FileReader(diskFile);
		} catch (Exception e) {
			return null;
		}
	}

	public InputStream getInputFromObject(String child) {
		FTPObject thisChild;
		int c;

		getChildren();

		for(c=0;c<children.size();c++) {
			thisChild = (FTPObject)children.elementAt(c);
			if (thisChild.toString().equalsIgnoreCase(child)) {
				return thisChild.getInputFromObject();
			}
		}

		return null;
	}

	public FileReader getReaderFromObject(String child) {
		FTPObject thisChild;
		int c;

		getChildren();

		for(c=0;c<children.size();c++) {
			thisChild = (FTPObject)children.elementAt(c);
			if (thisChild.toString().equalsIgnoreCase(child)) {
				return thisChild.getReaderFromObject();
			}
		}

		return null;
	}

	public FTPObject childAsDirectory(String name) {
		FTPObject thisChild;
		int c;

		getChildren();

		for(c=0;c<children.size();c++) {
			thisChild = (FTPObject)children.elementAt(c);
			if (thisChild.toString().equalsIgnoreCase(name) && thisChild.isDirectory()) {
				return thisChild;
			}
		}

		return null;
	}

	public File getDiskFile() {
		return diskFile;
	}

	/**
	*	Given a path string, this function attempts to tokenize it, and descend
	*	into the correct directory one level at a time. It works with both / and
	*	\ as path separators.
	**/
	public FTPObject nestedDirectory(String name) {
		FTPObject activeObject = this;
		StringTokenizer st;
		String currentChildDirectory = new String("");

		if (name.startsWith("root\\") || name.startsWith("root/")) {
			name = name.substring(5);
		}

		if (name.indexOf("\\") == -1) {
			st = new StringTokenizer(name, "/", false);
		} else {
			st = new StringTokenizer(name, "\\", false);
		}

		while (st.hasMoreTokens()) {
			currentChildDirectory = st.nextToken();
			activeObject = activeObject.childAsDirectory(currentChildDirectory);
			if (activeObject == null) {
				return null;
			}
		}

		return activeObject;
	}

	public FTPObject parentAsDirectory() {
		if (parent == null) {
			return this;
		} else {
			return parent;
		}
	}

	public void buildChildList() {
		children = new Vector();

		File[] files = null;
		int c;

		if (diskFile.isDirectory()) {
			files = diskFile.listFiles();
		} else {
			return;
		}

		for(c=0;c<files.length;c++) {
			children.add(new FTPObject(files[c], this));
		}
	}

	public Vector getChildren() {
		if (children == null) {
			buildChildList();
		}
		return children;
	}

	public boolean childExists(String name) {
		FTPObject thisChild;
		int c;
		boolean result;

		getChildren();

		for(c=0;c<children.size();c++) {
			thisChild = (FTPObject)children.elementAt(c);
			if (thisChild.toString().equalsIgnoreCase(name)) {
				return true;
			}
		}

		return false;
	}

	/**
	*	Find a child matching the name argument and return it. Returns null if the
	*	child does not exist. This method WILL return children that are directories.
	*/
	public FTPObject findChild(String name) {
		FTPObject thisChild;
		int c;

		getChildren();

		for(c=0;c<children.size();c++) {
			thisChild = (FTPObject)children.elementAt(c);
			if (thisChild.toString().equalsIgnoreCase(name)) {
				return thisChild;
			}
		}

		return null;
	}

	/**
	*	Find a child named by the old argument and rename it to the new argument.
	*/
	public void renameChild(String o, String n) {
		FTPObject targetChild;

		targetChild = findChild(o);
		targetChild.renameSelf(n);
	}

	/**
	*	Renames the current diskFile for this object to the argument passed.
	*	This function will cause the parent to rebuild its child list to reflect
	*	the name change.
	*/
	public void renameSelf(String n) {
		diskFile.renameTo(new File(parent.getDiskFile(), n));
		if (parent != null) parent.buildChildList();
	}

	/**
	*	This method finds a child matching the name given and deletes it.
	*	Directories WILL be deleted by this method. Afterwards the child
	*	list for this object will be rebuilt.
	*/
	public boolean delete(String name) {
		FTPObject thisChild;
		int c;
		boolean result;

		getChildren();

		for(c=0;c<children.size();c++) {
			thisChild = (FTPObject)children.elementAt(c);
			if (thisChild.toString().equalsIgnoreCase(name)) {
				result = thisChild.delete();
				buildChildList();
				return result;
			}
		}

		return false;
	}

	public boolean delete() {
		return diskFile.delete();
	}

	public boolean listingAllowed(FTPUser user) {
		return FTPSecurityManager.viewable(this, user);
	}

	public Vector getPermissions() {
		return filePermissions;
	}

	private void getFilePermissions() {
		File permissionsFile = new File("ftpsecurity");
		BufferedReader in = null;
		String line = null, token = null, against = null;
		StringTokenizer st = null;
		FTPPermission thisPermission;

		//Always create the permissions vector
		filePermissions = new Vector();

		//Attempt to open and read the file
		try {
			in = new BufferedReader(new FileReader(permissionsFile));
		} catch (Exception e) { }

		//If the file exists and is valid, read and parse it for object permissions
		if (in != null) {
			try {
				line = in.readLine();
				while (line != null) {
					st = new StringTokenizer(line, ":", false);
					if (st.nextToken().equalsIgnoreCase(completeObjectPath())) {
						try {
							token = st.nextToken();
							if (st.hasMoreTokens()) {
								against = st.nextToken();
							}
							if (FTPSecurityManager.knownPermission(token)) {
								filePermissions.add(new FTPPermission(token, against, 1));
							} else {
								throw new Exception("Bad permission name");
							}
							against = null;
						} catch (Exception e) {
							System.out.println("Bad permission \"" + token + "\" for " + toString());
						}
					}
					line = in.readLine();
				}
			} catch (Exception e) {
				System.out.println("Error reading permissions file");
			}
		}
	}

	/**
	*	Creates a subdirectory under this object. This function will trim the
	*	name passed to the final name in the path. The child list will also
	*	be rebuilt.
	*/
	public void makeDirectory(String name) {
		File directory;
		String fixedName = name;

		if (name.indexOf('\\') > 0) {
			fixedName = name.substring(name.lastIndexOf('\\') + 1);
		} else if (name.indexOf('/') > 0) {
			fixedName = name.substring(name.lastIndexOf('/') + 1);
		}

		directory = new File(diskFile, fixedName);
		directory.mkdir();

		buildChildList();
	}

	/**
	*	Return this node, plus one for each item in the children vector. This
	*	is the breadth of the FTPObject.
	*/
	public int getBreadth() {
		return 1 + children.size();
	}

	/**
	*	Return the breadth of this node, plus the size of all it's children.
	*	This is the size of the node.
	*/
	public int getSize() {
		int c, nodeSize;

		nodeSize = getBreadth();

		for(c=0;c<children.size();c++) {
			nodeSize += ((FTPObject)(children.elementAt(c))).getSize();
		}

		return nodeSize;
	}

	/**
	*	Return a string with the date this object was last modified, neatly
	*	formatted and ready for display in a LIST command.
	*/
	public String getFormattedDate() {
		Date fileDate = new Date(diskFile.lastModified());
		String tempDate = fileDate.toString(), formattedDate = null;

		formattedDate = tempDate.substring(4, 10);
		formattedDate = formattedDate + " " + tempDate.substring(24);

		return formattedDate;
	}

	/**
	 * This method is needed to use the "constains" method in Vector of FTPObject
	 */  
	public boolean equals(Object o) {
		if (o instanceof FTPObject) {
			return this.toString().equalsIgnoreCase(o.toString());
		}
		return false;
	}
}