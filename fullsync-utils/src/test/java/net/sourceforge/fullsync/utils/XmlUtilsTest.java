/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */
package net.sourceforge.fullsync.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.xml.sax.helpers.DefaultHandler;

public class XmlUtilsTest {
	private static final String SIMPLE_XML = "<root><child1/><child2/></root>";

	@Test
	public void newDocumentBuilderReturnsUsableBuilder() throws Exception {
		var builder = XmlUtils.newDocumentBuilder();
		assertNotNull(builder);
		var doc = builder.parse(new ByteArrayInputStream(SIMPLE_XML.getBytes(StandardCharsets.UTF_8)));
		assertNotNull(doc);
		assertEquals("root", doc.getDocumentElement().getTagName());
	}

	@Test
	public void newSaxParserReturnsUsableParser() throws Exception {
		var parser = XmlUtils.newSaxParser();
		assertNotNull(parser);
		var input = new ByteArrayInputStream(SIMPLE_XML.getBytes(StandardCharsets.UTF_8));
		parser.parse(input, new DefaultHandler());
	}

	@Test
	public void newTransformerHasCorrectOutputProperties() throws Exception {
		var transformer = XmlUtils.newTransformer();
		assertNotNull(transformer);
		assertEquals("xml", transformer.getOutputProperty(OutputKeys.METHOD));
		assertEquals("1.0", transformer.getOutputProperty(OutputKeys.VERSION));
		assertEquals("yes", transformer.getOutputProperty(OutputKeys.INDENT));
		assertEquals("no", transformer.getOutputProperty(OutputKeys.STANDALONE));
	}

	@Test
	public void newTransformerCanTransformXml() throws Exception {
		var transformer = XmlUtils.newTransformer();
		var sw = new StringWriter();
		transformer.transform(new StreamSource(new ByteArrayInputStream(SIMPLE_XML.getBytes(StandardCharsets.UTF_8))),
			new StreamResult(sw));
		assertNotNull(sw.toString());
	}

	@Test
	public void forEachChildElementVisitsOnlyElements() throws Exception {
		var doc = XmlUtils.newDocumentBuilder().parse(new ByteArrayInputStream(SIMPLE_XML.getBytes(StandardCharsets.UTF_8)));
		var root = doc.getDocumentElement();
		List<String> visited = new ArrayList<>();
		XmlUtils.forEachChildElement(root, el -> visited.add(el.getTagName()));
		assertEquals(List.of("child1", "child2"), visited);
	}

	@Test
	public void forEachChildElementSkipsTextNodes() throws Exception {
		var xml = "<root>text<child/>more text</root>";
		var doc = XmlUtils.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
		List<String> visited = new ArrayList<>();
		XmlUtils.forEachChildElement(doc.getDocumentElement(), el -> visited.add(el.getTagName()));
		assertEquals(List.of("child"), visited);
	}

	@Test
	public void forEachChildElementOnLeafNodeVisitsNothing() throws Exception {
		var xml = "<root><leaf/></root>";
		var doc = XmlUtils.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
		var leaf = (Node) doc.getDocumentElement().getFirstChild();
		List<String> visited = new ArrayList<>();
		XmlUtils.forEachChildElement(leaf, el -> visited.add(el.getTagName()));
		assertEquals(0, visited.size());
	}
}
