package net.sourceforge.wrabbitftp;
import java.util.*;

/**
*	The FTPSecurityManager is a helper class which validates permissions (since they
*	are specified as keywords in the configuration files, and subject to typos) and
*	checks permissions against files and users to determine who can view specific
*	files.
*/
public abstract class FTPSecurityManager {
	private static FTPPermission genericHiddenFilePermission = new FTPPermission("HiddenFilePermission", null, 1);
	private static FTPPermission genericViewHiddenFilesPermission = new FTPPermission("ViewHiddenFilesPermission", null, 2);

	private static FTPPermission genericProtectedFilePermission = new FTPPermission("ProtectedFilePermission", null, 3);
	private static FTPPermission genericViewProtectedFilesPermission = new FTPPermission("ViewProtectedFilesPermission", null, 4);

	private static FTPPermission genericPrivateFilePermission = new FTPPermission("PrivateFilePermission", null, 5);

	private static FTPPermission genericDownloadFilePermission = new FTPPermission("DownloadFilePermission", null, 1);
	private static FTPPermission genericUploadFilePermission = new FTPPermission("UploadFilePermission", null, 1);
	private static FTPPermission genericDeleteFilePermission = new FTPPermission("DeleteFilePermission", null, 1);

	private static Vector knownPermissions = null;

	public static void initManager() {
		knownPermissions = new Vector();
		knownPermissions.add("HiddenFilePermission");
		knownPermissions.add("ViewHiddenFilesPermission");
		knownPermissions.add("ProtectedFilePermission");
		knownPermissions.add("ViewProtectedFilesPermission");
		knownPermissions.add("PrivateFilePermission");

		knownPermissions.add("DownloadFilePermission");
		knownPermissions.add("UploadFilePermission");
		knownPermissions.add("DeleteFilePermission");
	}

	/**
	* Decide if this is a known permission or not.
	*/

	public static boolean knownPermission(String perm) {
		if (knownPermissions == null) { FTPSecurityManager.initManager(); }
		return knownPermissions.contains(perm);
	}

	/**
	* Determine if a given object can be viewed by a given user.
	*/

	public static boolean viewable(FTPObject obj, FTPUser usr) {
		Vector objectPermissions = obj.getPermissions();
		Vector userPermissions = usr.getPermissions();

		//If the object has a private file permission then it is not viewable. Period.

		if (objectPermissions.contains(genericPrivateFilePermission)) {
			return false;
		}

		//If this object is hidden from this user specifically, then we deny viewing.
		//A user permission against this object allowing it to be viewed DOES NOT override a permission against the
		//user preventing the file from being viewed.

		if (objectPermissions.contains(new FTPPermission("HiddenFilePermission", usr.getUsername(), 1))) {
			System.out.println("Forbidden for this user (" + obj.toString() + ")");
			return false;
		}

		//If the object is generally hidden we do not allow the user to see it,
		//unless the user may see general hidden files, or they are allowed to see this file specifically.

		if (objectPermissions.contains(genericHiddenFilePermission)) {
			if (userPermissions.contains(genericViewHiddenFilesPermission) ||
			    userPermissions.contains(new FTPPermission("ViewHiddenFilesPermission", obj.completeObjectPath(), 2))) {
				return true;
			} else {
				return false;
			}
		}

		return true;
	}
}
