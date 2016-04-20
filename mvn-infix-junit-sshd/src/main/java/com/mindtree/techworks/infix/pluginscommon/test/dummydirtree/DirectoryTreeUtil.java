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
package com.mindtree.techworks.infix.pluginscommon.test.dummydirtree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXB;

import com.mindtree.techworks.infix.pluginscommon.test.dummydirtree.model.DirectoryTree;
import com.mindtree.techworks.infix.pluginscommon.test.dummydirtree.model.DirectoryTree.Directory;


/**
 * @author Bindul Bhowmik
 * @version $Revision$ $Date$
 *
 */
public final class DirectoryTreeUtil {

	private DirectoryTreeUtil () {}
	
	public static void generateDirectoryTree ( File rootDirectory, URL dirTreeDescriptor ) throws IOException {
		
		// Get the directory tree
		DirectoryTree tree = JAXB.unmarshal(dirTreeDescriptor, DirectoryTree.class);
		
		for (Directory directory : tree.getDirectory()) {
			createDirectory(rootDirectory, directory);
		}
	}
	
	private static void createDirectory ( File parent, Directory directory) throws IOException {
		File dir = new File (parent, directory.getName());
		if (!dir.exists()) {
			dir.mkdir();
		} else if (!dir.isDirectory()) {
			throw new IOException("Directory : " + dir.getAbsolutePath() 
				+ " already exists and is not a directory");
		}
		
		// Handle child directories
		if (null != directory.getDirectory()) {
			for (Directory childDir : directory.getDirectory()) {
				createDirectory(dir, childDir);
			}
		}
		
		// Create files
		if (null != directory.getFile()) {
			for (DirectoryTree.File file : directory.getFile()) {
				File fil = new File (dir, file.getName());
				if (null != file.getSize() && file.getSize().doubleValue() > 0) {
					// Dump content
					// TODO Implement
					FileWriter writer = new FileWriter(fil);
					writer.append("Test content");
					writer.flush();
					writer.close();
				} else {
					FileWriter writer = new FileWriter(fil);
					writer.append("Test content");
					writer.flush();
					writer.close();
				}
			}
		}
	}
}
