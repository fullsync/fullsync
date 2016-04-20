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
package com.mindtree.techworks.infix.pluginscommon.test.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses and holds a text file, can be used to compare and test parts of the
 * file
 * 
 * @author Bindul Bhowmik
 * @version $Revision$ $Date$
 */
public class ParsedTextFile {

	/**
	 * Option to trim lines when reading the text
	 */
	public static final int TRIM_LINES = 1;
	
	/**
	 * Option to skip empty lines while parsing
	 */
	public static final int SKIP_EMPTY_LINES = 2;
	
	/**
	 * The parsed lines
	 */
	private final String[] text;
	
	public ParsedTextFile (File file, int options) throws IOException {
		this (new FileReader(file), options);
	}
	
	public ParsedTextFile (Reader reader, int options) throws IOException {
		boolean trimLines = ((options & TRIM_LINES) == TRIM_LINES);
		boolean skipEmpty = ((options & SKIP_EMPTY_LINES) == SKIP_EMPTY_LINES);
		
		BufferedReader bufReader = new BufferedReader(reader);
		List<String> lines = new ArrayList<String>();
		
		String lineRead = null;
		while (null != (lineRead = bufReader.readLine())) {
			if (trimLines) {
				lineRead = lineRead.trim();
			}
			if (skipEmpty && lineRead.trim().length() == 0) {
				continue;
			}
			lines.add(lineRead);
		}
		
		text = lines.toArray(new String[lines.size()]);
	}
	
	public int countLines (String needle, boolean isRegex) {
		
		int counter = 0;
		if (isRegex) {
			Pattern pattern = Pattern.compile(needle);
			Matcher matcher = null;

			for (String line : text) {
				if (null == matcher) {
					matcher = pattern.matcher(line);
				} else {
					matcher.reset(line);
				}
				
				if (matcher.matches()) {
					counter ++;
				}
			}
		} else {
			for (String line : text) {
				if (line.equals(needle)) {
					counter ++;
				}
			}
		}
		
		return counter;
	}
	
	public boolean hasLine (String needle, boolean isRegex) {
		return (countLines(needle, isRegex) >= 1);
	}
}
