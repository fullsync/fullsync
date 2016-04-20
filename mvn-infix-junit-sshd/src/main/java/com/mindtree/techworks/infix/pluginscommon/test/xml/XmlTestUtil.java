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
package com.mindtree.techworks.infix.pluginscommon.test.xml;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Util class for handling xml unit tests.
 *
 * Parts of this class are copied from net.sf.xmlunit.util.Nodes part of
 * unreleased code in <a href="http://xmlunit.sourceforge.net">XmlUnit</a>.
 *
 * @author Bindul Bhowmik
 * @version $Revision$ $Date$
 */
public class XmlTestUtil {

	private XmlTestUtil () {}

	public static Document getNormalizedDocument (Reader input)
		throws ParserConfigurationException, SAXException, IOException {

		InputSource is = new InputSource(input);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();

		Document d = db.parse(is);
		return d;
//		return (Document) Nodes.normalizeWhitespace(d); // FIXME: is this required??
	}

	public static Document getNormalizedDocument (String fileLocation)
		throws ParserConfigurationException, SAXException, IOException {
		return getNormalizedDocument(new FileReader(fileLocation));
	}
}
