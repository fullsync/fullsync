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
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.vfs2.FileObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;
import net.sourceforge.fullsync.utils.XmlUtils;

public class SyncFileBufferedConnection implements BufferedConnection {
	private static final String BUFFER_FILENAME = ".syncfiles"; //$NON-NLS-1$
	private static final String ELEMENT_SYNC_FILES = "SyncFiles"; //$NON-NLS-1$
	private static final String ELEMENT_FILE = "File"; //$NON-NLS-1$
	private static final String ELEMENT_DIRECTORY = "Directory"; //$NON-NLS-1$
	private static final String ATTRIBUTE_FILE_SYSTEM_LAST_MODIFIED = "FileSystemLastModified"; //$NON-NLS-1$
	private static final String ATTRIBUTE_FILE_SYSTEM_LENGTH = "FileSystemLength"; //$NON-NLS-1$
	private static final String ATTRIBUTE_BUFFERED_LAST_MODIFIED = "BufferedLastModified"; //$NON-NLS-1$
	private static final String ATTRIBUTE_BUFFERED_LENGTH = "BufferedLength"; //$NON-NLS-1$
	private static final String ATTRIBUTE_NAME = "Name"; //$NON-NLS-1$

	private static class SyncFileDefaultHandler extends DefaultHandler {
		private final BufferedConnection bufferedConnection;
		private AbstractBufferedFile current;

		SyncFileDefaultHandler(SyncFileBufferedConnection bc) {
			bufferedConnection = bc;
			current = (AbstractBufferedFile) bc.getRoot();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			String name = attributes.getValue(ATTRIBUTE_NAME);

			if (ELEMENT_DIRECTORY.equals(qName)) {
				if ("/".equals(name) || ".".equals(name)) { //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
				AbstractBufferedFile newDir = new AbstractBufferedFile(bufferedConnection, name, current, true, true);
				current.addChild(newDir);
				current = newDir;
			}
			else if (ELEMENT_FILE.equals(qName)) {
				AbstractBufferedFile newFile = new AbstractBufferedFile(bufferedConnection, name, current, false, true);
				newFile.setSize(Long.parseLong(attributes.getValue(ATTRIBUTE_BUFFERED_LENGTH)));
				newFile.setLastModified(Long.parseLong(attributes.getValue(ATTRIBUTE_BUFFERED_LAST_MODIFIED)));

				newFile.setFsSize(Long.parseLong(attributes.getValue(ATTRIBUTE_FILE_SYSTEM_LENGTH)));
				newFile.setFsLastModified(Long.parseLong(attributes.getValue(ATTRIBUTE_FILE_SYSTEM_LAST_MODIFIED)));
				current.addChild(newFile);
			}
			super.startElement(uri, localName, qName, attributes);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (ELEMENT_DIRECTORY.equals(qName)) {
				/*
				 * Source Buffer needs to load fs files after buffer info /
				 * Collection fsChildren = current.getUnbuffered().getChildren();
				 * for( Iterator i = fsChildren.iterator(); i.hasNext(); )
				 * {
				 * File f = (File)i.next();
				 * if(null == current.getChild( f.getName() ))
				 * current.addChild( new AbstractBufferedFile( bc, f, current, f.isDirectory(), false ) );
				 * }
				 * /*
				 */
				current = (AbstractBufferedFile) current.getParent();
			}
			super.endElement(uri, localName, qName);
		}
	}

	private final Site fs;
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
		if (null == n) {
			n = dir.getUnbuffered().createChild(name, directory);
		}
		return new AbstractBufferedFile(this, n, dir, directory, false);
	}

	@Override
	public boolean delete(File node) throws IOException {
		node.getUnbuffered().delete();
		((BufferedFile) node.getParent()).removeChild(node.getName());
		return true;
	}

	@Override
	public Map<String, File> getChildren(File dir) {
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
			if (null == bf) {
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
		File f = fsRoot.getChild(BUFFER_FILENAME);

		root = new AbstractBufferedFile(this, fsRoot, null, true, true);
		if ((null == f) || !f.exists() || f.isDirectory()) {
			if (isMonitoringFileSystem()) {
				updateFromFileSystem(root);
			}
			return;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream((int) f.getSize());
		try (InputStream in = new GZIPInputStream(f.getInputStream())) {
			int i;
			byte[] block = new byte[4096];
			while ((i = in.read(block)) > 0) {
				out.write(block, 0, i);
			}
		}
		try {
			XmlUtils.newSaxParser().parse(new ByteArrayInputStream(out.toByteArray()), new SyncFileDefaultHandler(this));
		}
		catch (SAXParseException spe) {
			StringBuilder sb = new StringBuilder(spe.toString());
			sb.append("\n Line number: " + spe.getLineNumber()); //$NON-NLS-1$
			sb.append("\n Column number: " + spe.getColumnNumber()); //$NON-NLS-1$
			sb.append("\n Public ID: " + spe.getPublicId()); //$NON-NLS-1$
			sb.append("\n System ID: " + spe.getSystemId() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			System.err.println(sb.toString());
		}
		catch (IOException | SAXException | ParserConfigurationException | FactoryConfigurationError e) {
			ExceptionHandler.reportException(e);
		}

		if (isMonitoringFileSystem()) {
			updateFromFileSystem(root);
		}
	}

	protected Element serializeFile(BufferedFile file, Document doc) throws IOException {
		Element elem = doc.createElement(file.isDirectory() ? ELEMENT_DIRECTORY : ELEMENT_FILE);
		elem.setAttribute(ATTRIBUTE_NAME, file.getName());
		if (file.isDirectory()) {
			for (File n : file.getChildren()) {
				if (n.exists()) {
					elem.appendChild(serializeFile((BufferedFile) n, doc));
				}
			}
		}
		else {
			elem.setAttribute(ATTRIBUTE_BUFFERED_LENGTH, String.valueOf(file.getSize()));
			elem.setAttribute(ATTRIBUTE_BUFFERED_LAST_MODIFIED, String.valueOf(file.getLastModified()));
			elem.setAttribute(ATTRIBUTE_FILE_SYSTEM_LENGTH, String.valueOf(file.getFsSize()));
			elem.setAttribute(ATTRIBUTE_FILE_SYSTEM_LAST_MODIFIED, String.valueOf(file.getFsLastModified()));
		}
		return elem;
	}

	public void saveToBuffer() throws IOException {
		File fsRoot = fs.getRoot();
		File node = fsRoot.getChild(BUFFER_FILENAME);
		if ((null == node) || !node.exists()) {
			node = root.createChild(BUFFER_FILENAME, false);
		}
		try {
			Document doc = XmlUtils.newDocumentBuilder().newDocument();
			Element e = doc.createElement(ELEMENT_SYNC_FILES);
			e.appendChild(serializeFile(root, doc));
			doc.appendChild(e);
			Transformer tf = XmlUtils.newTransformer();
			try (OutputStreamWriter osw = new OutputStreamWriter(new GZIPOutputStream(node.getOutputStream()), StandardCharsets.UTF_8)) {
				tf.transform(new DOMSource(doc), new StreamResult(osw));
				osw.flush();
			}
		}
		catch (IOException | ParserConfigurationException | FactoryConfigurationError | TransformerException e) {
			ExceptionHandler.reportException(e);
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
	public void close() throws Exception {
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
