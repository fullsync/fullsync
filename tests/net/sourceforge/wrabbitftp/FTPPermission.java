package net.sourceforge.wrabbitftp;
/**
*	The FTPPermission class is intended to abstract various user permissions
*	and make it possible to quickly compare them. Most of its features have
*	not yet been implemented, since so far permission handling has been more or
*	less rudimentary.
*	<br><br>
*	The level of a permission is used to determine which permissions override which
*	others.	0 is the lowest level, 5 is the highest.
*	<br><br>
*	If againstObject is null then the permission applies to all files or users,
*	depending on the type of permission.
*	If againstObject is non-null then the permission applies only to a single user or
*	file.
*	Permissions with an againstObject should always override permissions without an
*	againstObject, for a given object.
*/

public class FTPPermission {
	private String thisPermission = "Generic FTP Permission";
	private int level = 0;
	private String againstObject = null;

	public FTPPermission(String perm, String against, int l) {
		thisPermission = perm;
		againstObject = against;
		level = l;
	}

	public boolean equals(Object another) {
		FTPPermission otherPermission;

		//If we are comparing this object to anything that isn't an instance of an FTPPermission class then
		//we say they are not equal

		if (!(another instanceof FTPPermission)) {
			return false;
		}

		//We are sure we can do this at this point because of the previous test

		otherPermission = (FTPPermission)another;

		//System.out.println("?== " + againstObject + "..." + otherPermission.getAgainstObject());

		//If this permission is against an object and the other is not, or if the other is and this one is not
		//then the two permissions are not equal.

		if ((againstObject == null) && (otherPermission.getAgainstObject() != null) ||
			(againstObject != null) && (otherPermission.getAgainstObject() == null)) {
			return false;
		}

		//If the toString method of this permission matches the toString of the other permission these
		//two permission MAY be equal

		if (otherPermission.toString().equals(toString())) {

			//If both this againstObject and the other againstObject are null these are equals permissions

			if ((otherPermission.getAgainstObject() == null) && againstObject == null) {
				return true;
			}

			//If they aren't null then the toString methods of the two againstObjects must be equal in order
			//for these two permissions to be equal

			if (otherPermission.getAgainstObject().toString().equals(againstObject.toString())) {
				return true;
			} else {
				return false;
			}
		}

		//If we get here, we are sure the two permissions are not equal

		return false;
	}

	public String toString() {
		return thisPermission;
	}

	public String completePermission() {
		if (againstObject != null) {
			return (toString() + ":" + againstObject.toString());
		} else {
			return (toString());
		}
	}

	public boolean overrides(FTPPermission other) {
		return !other.overrides(this);
	}

	public boolean overrides(int otherLevel) {
		if (level > otherLevel) {
			return true;
		}
		return false;
	}

	public void setAgainstObject(String object) {
		againstObject = object;
	}

	public Object getAgainstObject() {
		return againstObject;
	}
}
