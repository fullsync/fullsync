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
package net.sourceforge.fullsync.fs.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;

import org.apache.commons.vfs2.FileObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SyncFileBufferedConnection implements BufferedConnection {
	private static final long serialVersionUID = 2L;

	class SyncFileDefaultHandler extends DefaultHandler {
		BufferedConnection bc;
		AbstractBufferedFile current;

		public SyncFileDefaultHandler(SyncFileBufferedConnection bc) {
			this.bc = bc;
			current = (AbstractBufferedFile) bc.getRoot();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			String name = attributes.getValue("Name");

			if ("Directory".equals(qName)) {
				if ("/".equals(name) || ".".equals(name)) {
					return;
				}
				// File n = current.getUnbuffered().getChild( name );
				// if( n == null )
				// n = current.getUnbuffered().createChild( name, true );
				AbstractBufferedFile newDir = new AbstractBufferedFile(bc, name, current, true, true);
				current.addChild(newDir);
				current = newDir;
			}
			else if ("File".equals(qName)) {
				// File n = current.getUnbuffered().getChild( name );
				// if( n == null )
				// n = current.getUnbuffered().createChild( name, false );
				AbstractBufferedFile newFile = new AbstractBufferedFile(bc, name, current, false, true);
				newFile.setSize(Long.parseLong(attributes.getValue("BufferedLength")));
				newFile.setLastModified(Long.parseLong(attributes.getValue("BufferedLastModified")));

				newFile.setFsSize(Long.parseLong(attributes.getValue("FileSystemLength")));
				newFile.setFsLastModified(Long.parseLong(attributes.getValue("FileSystemLastModified")));
				current.addChild(newFile);
			}
			super.startElement(uri, localName, qName, attributes);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ("Directory".equals(qName)) {
				/*
				 * Source Buffer needs to load fs files after buffer info /
				 * Collection fsChildren = current.getUnbuffered().getChildren();
				 * for( Iterator i = fsChildren.iterator(); i.hasNext(); )
				 * {
				 * File f = (File)i.next();
				 * if( current.getChild( f.getName() ) == null )
				 * current.addChild( new AbstractBufferedFile( bc, f, current, f.isDirectory(), false ) );
				 * }
				 * /*
				 */
				current = (AbstractBufferedFile) current.getParent();
			}
			super.endElement(uri, localName, qName);
		}
	}

	private Site fs;
	private BufferedFile root;
	private boolean monitoringFileSystem;

	public SyncFileBufferedConnection(final Site fs) throws IOException {
		this.fs = fs;
		this.monitoringFileSystem = false;
		loadFromBuffer();
	}

	@Override
	public boolean isAvailable() {
		return fs.isAvailable();
	}

	@Override
	public File createChild(File dir, String name, boolean directory) throws IOException {
		File n = dir.getUnbuffered().getChild(name);
		if (n == null) {
			n = dir.getUnbuffered().createChild(name, directory);
		}
		BufferedFile bf = new AbstractBufferedFile(this, n, dir, directory, false);
		return bf;
	}

	@Override
	public boolean delete(File node) throws IOException {
		node.getUnbuffered().delete();
		((BufferedFile) node.getParent()).removeChild(node.getName());
		return true;
	}

	@Override
	public Hashtable<String, File> getChildren(File dir) {
		return null;
	}

	@Override
	public File getRoot() {
		return root;
	}

	@Override
	public boolean makeDirectory(File dir) {
		return false;
	}

	@Override
	public InputStream readFile(File file) {
		return null;
	}

	@Override
	public OutputStream writeFile(File file) {
		return null;
	}

	@Override
	public boolean writeFileAttributes(File file) {
		return false;
	}

	protected void updateFromFileSystem(BufferedFile buffered) throws IOException {
		// load fs entries if wanted
		Collection<File> fsChildren = buffered.getUnbuffered().getChildren();
		for (File uf : fsChildren) {
			BufferedFile bf = (BufferedFile) buffered.getChild(uf.getName());
			if (bf == null) {
				bf = new AbstractBufferedFile(this, uf, root, uf.isDirectory(), false);
				buffered.addChild(bf);
			}
			if (bf.isDirectory()) {
				updateFromFileSystem(bf);
			}
		}
	}

	protected void loadFromBuffer() throws IOException {
		File fsRoot = fs.getRoot();
		File f = fsRoot.getChild(".syncfiles");

		root = new AbstractBufferedFile(this, fsRoot, null, true, true);
		if ((f == null) || !f.exists() || f.isDirectory()) {
			if (isMonitoringFileSystem()) {
				updateFromFileSystem(root);
			}
			return;
		}
		ByteArrayOutputStream out;
		InputStream reader = null;
		try {
			out = new ByteArrayOutputStream((int) f.getSize());

			InputStream in = new GZIPInputStream(f.getInputStream());
			int i;
			byte[] block = new byte[1024];
			while ((i = in.read(block)) > 0) {
				out.write(block, 0, i);
			}
			in.close();
			out.close();

			reader = new ByteArrayInputStream(out.toByteArray());
			SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
			sax.parse(reader, new SyncFileDefaultHandler(this));

		}
		catch (IOException ioe) {
			ExceptionHandler.reportException(ioe);
		}
		catch (SAXParseException spe) {
			StringBuffer sb = new StringBuffer(spe.toString());
			sb.append("\n Line number: " + spe.getLineNumber());
			sb.append("\n Column number: " + spe.getColumnNumber());
			sb.append("\n Public ID: " + spe.getPublicId());
			sb.append("\n System ID: " + spe.getSystemId() + "\n");
			System.err.println(sb.toString());
		}
		catch (SAXException e) {
			ExceptionHandler.reportException(e);
		}
		catch (ParserConfigurationException e) {
			ExceptionHandler.reportException(e);
		}
		catch (FactoryConfigurationError e) {
			ExceptionHandler.reportException(e);
		}
		finally {
			try {
				if (reader != null) {
					reader.close();
				}
			}
			catch (IOException e1) {
				ExceptionHandler.reportException(e1);
			}
		}

		if (isMonitoringFileSystem()) {
			updateFromFileSystem(root);
		}
	}

	protected Element serializeFile(BufferedFile file, Document doc) throws IOException {
		Element elem = doc.createElement(file.isDirectory() ? "Directory" : "File");
		elem.setAttribute("Name", file.getName());
		if (file.isDirectory()) {
			for (File n : file.getChildren()) {
				if (!n.exists()) {
					continue;
				}

				elem.appendChild(serializeFile((BufferedFile) n, doc));
			}
		}
		else {
			elem.setAttribute("BufferedLength", String.valueOf(file.getSize()));
			elem.setAttribute("BufferedLastModified", String.valueOf(file.getLastModified()));
			elem.setAttribute("FileSystemLength", String.valueOf(file.getFsSize()));
			elem.setAttribute("FileSystemLastModified", String.valueOf(file.getFsLastModified()));
		}
		return elem;
	}

	public void saveToBuffer() throws IOException {
		File fsRoot = fs.getRoot();
		File node = fsRoot.getChild(".syncfiles");
		if ((node == null) || !node.exists()) {
			node = root.createChild(".syncfiles", false);
		}
		OutputStream out = null;

		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element e = doc.createElement("SyncFiles");
			e.appendChild(serializeFile(root, doc));
			doc.appendChild(e);

			out = new GZIPOutputStream(node.getOutputStream());

			TransformerFactory fac = TransformerFactory.newInstance();
			fac.setAttribute("indent-number", 2);
			Transformer tf = fac.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new OutputStreamWriter(out, "UTF-8"));

			tf.setOutputProperty(OutputKeys.METHOD, "xml");
			tf.setOutputProperty(OutputKeys.VERSION, "1.0");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.setOutputProperty(OutputKeys.STANDALONE, "no");
			tf.transform(source, result);
		}
		catch (IOException ioe) {
			ExceptionHandler.reportException(ioe);
		}
		catch (ParserConfigurationException e) {
			ExceptionHandler.reportException(e);
		}
		catch (FactoryConfigurationError e) {
			ExceptionHandler.reportException(e);
		} catch (TransformerConfigurationException e) {
			ExceptionHandler.reportException(e);
		} catch (TransformerException e) {
			ExceptionHandler.reportException(e);
		}
		finally {
			if (null != out) {
				out.close();
			}
		}
	}

	@Override
	public boolean isMonitoringFileSystem() {
		return monitoringFileSystem;
	}

	@Override
	public void flush() throws IOException {
		saveToBuffer();
		fs.flush();
	}

	@Override
	public void close() throws IOException {
		fs.close();
	}

	@Override
	public boolean isCaseSensitive() {
		return fs.isCaseSensitive();
	}

	@Override
	public FileObject getBase() {
		return fs.getBase();
	}

	@Override
	public ConnectionDescription getConnectionDescription() {
		return fs.getConnectionDescription();
	}

}
