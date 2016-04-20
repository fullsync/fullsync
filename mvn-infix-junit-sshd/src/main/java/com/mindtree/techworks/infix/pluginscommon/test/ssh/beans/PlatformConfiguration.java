/*
 * $HeadURL$
 * 
 * Copyright (c) 2010 MindTree Ltd. 
 * 
 * This file is part of Infix Maven Plugins
 * 
 * Infix Maven Plugins is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * Infix Maven Plugins is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * Infix Maven Plugins. If not, see <http://www.gnu.org/licenses/>.
 */
package com.mindtree.techworks.infix.pluginscommon.test.ssh.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The platform configuration for the SSH server
 * 
 * @author Bindul Bhowmik
 * @version $Revision$ $Date$
 */
@XmlRootElement ( name = "PlatformConfiguration" )
@XmlAccessorType(XmlAccessType.FIELD)
public class PlatformConfiguration {

	@XmlElement ( name = "NativeProcessProvider", nillable = false, required = true, defaultValue = "com.sshtools.daemon.platform.UnsupportedShellProcessProvider" )
	private String nativeProcessProvider;
	
	@XmlElement ( name = "NativeAuthenticationProvider", nillable = false, required = true)
	private String nativeAuthenticationProvider;
	
	@XmlElement ( name = "NativeFileSystemProvider", nillable = false, required = true, defaultValue = "com.sshtools.daemon.vfs.VirtualFileSystem" )
	private String nativeFileSystemProvider;
	
	// Add support for Native Setting later
	
	@XmlElement ( name = "VFSRoot", required = false)
	private PlatformConfiguration.VFSRoot vfsRoot;

	@XmlElement (name = "VFSMount")
	private List<PlatformConfiguration.VFSMount> vfsMounts;
	// Add support for VFSMount later
	
	/**
	 * @return the nativeProcessProvider
	 */
	public String getNativeProcessProvider () {
		return nativeProcessProvider;
	}
	
	/**
	 * @param nativeProcessProvider the nativeProcessProvider to set
	 */
	public void setNativeProcessProvider (String nativeProcessProvider) {
		this.nativeProcessProvider = nativeProcessProvider;
	}
	
	/**
	 * @return the nativeAuthenticationProvider
	 */
	public String getNativeAuthenticationProvider () {
		return nativeAuthenticationProvider;
	}
	
	/**
	 * @param nativeAuthenticationProvider the nativeAuthenticationProvider to set
	 */
	public void setNativeAuthenticationProvider (String nativeAuthenticationProvider) {
		this.nativeAuthenticationProvider = nativeAuthenticationProvider;
	}
	
	/**
	 * @return the nativeFileSystemProvider
	 */
	public String getNativeFileSystemProvider () {
		return nativeFileSystemProvider;
	}
	
	/**
	 * @param nativeFileSystemProvider the nativeFileSystemProvider to set
	 */
	public void setNativeFileSystemProvider (String nativeFileSystemProvider) {
		this.nativeFileSystemProvider = nativeFileSystemProvider;
	}
	
	/**
	 * @return the vfsRoot
	 */
	public PlatformConfiguration.VFSRoot getVfsRoot () {
		return vfsRoot;
	}
	
	/**
	 * @param vfsRoot the vfsRoot to set
	 */
	public void setVfsRoot (PlatformConfiguration.VFSRoot vfsRoot) {
		this.vfsRoot = vfsRoot;
	}
	
	public List<PlatformConfiguration.VFSMount> getVfsMounts () {
		return vfsMounts;
	}
	
	public void setVfsMounts (List<PlatformConfiguration.VFSMount> vfsMounts) {
		this.vfsMounts = vfsMounts;
	}
	
	public void addVfsMount (PlatformConfiguration.VFSMount vfsMount) {
		if (null == this.vfsMounts) {
			this.vfsMounts = new ArrayList<PlatformConfiguration.VFSMount>();
		}
		this.vfsMounts.add(vfsMount);
	}

	@XmlAccessorType (XmlAccessType.FIELD)
	@XmlType (name = "")
	public static class VFSMount {
		@XmlAttribute (name = "path")
		private String path;
		@XmlAttribute (name = "mount")
		private String mount;
		@XmlAttribute (name = "permissions")
		private String permissions;
		@XmlElement (name = "VFSPermission")
		private PlatformConfiguration.VFSPermission vfsPermission;
		
		public String getPath () {
			return path;
		}
		
		public void setPath (String path) {
			this.path = path;
		}
		
		public String getMount () {
			return mount;
		}
		
		public void setMount (String mount) {
			this.mount = mount;
		}
		
		public String getPermissions () {
			return permissions;
		}
		
		public void setPermissions (String permissions) {
			this.permissions = permissions;
		}
		
		public PlatformConfiguration.VFSPermission getVfsPermission () {
			return vfsPermission;
		}
		
		public void setVfsPermission (PlatformConfiguration.VFSPermission vfsPermission) {
			this.vfsPermission = vfsPermission;
		}
	}
	
	@XmlAccessorType (XmlAccessType.FIELD)
	@XmlType (name = "")
	public static class VFSPermission {
		@XmlAttribute (name = "name")
		private String name;
		@XmlAttribute (name = "permissions")
		private String permissions;
		
		public String getName () {
			return name;
		}
		
		public void setName (String name) {
			this.name = name;
		}
		
		public String getPermissions () {
			return permissions;
		}
		
		public void setPermissions (String permissions) {
			this.permissions = permissions;
		}
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType (name = "")
	public static class VFSRoot {

		@XmlAttribute (name = "path", required = true)
		private String path;
		@XmlAttribute (name = "permissions", required = false)
		private String permissions;

		public String getPath () {
			return path;
		}

		public void setPath (String path) {
			this.path = path;
		}
		
		public String getPermissions () {
			return permissions;
		}

		public void setPermissions (String permissions) {
			this.permissions = permissions;
		}
	}
}
