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
package net.sourceforge.fullsync;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ConnectionDescription implements Serializable {
	private static final long serialVersionUID = 2L;

	private URI uri = null;
	private Hashtable<String, String> parameters = new Hashtable<String, String>();
	private Hashtable<String, String> secretParameters = new Hashtable<String, String>();


	public Element serialize(String name, Document doc) {
		Element elem = doc.createElement(name);
		elem.setAttribute("uri", uri.toString());
		for (String key : parameters.keySet()) {
			if ("username".equals(key)) {
				elem.setAttribute("username", parameters.get(key));
			}
			else if ("bufferStrategy".equals(key)) {
				elem.setAttribute("buffer", parameters.get(key));
			}
			else {
				Element p = doc.createElement("Param");
				p.setAttribute("name", key);
				p.setAttribute("value", parameters.get(key));
				elem.appendChild(p);
			}
		}
		for (String key : secretParameters.keySet()) {
			if ("password".equals(key)) {
				elem.setAttribute("password", secretParameters.get(key));
			}
			else {
				Element p = doc.createElement("SecretParam");
				p.setAttribute("name", key);
				p.setAttribute("value", secretParameters.get(key));
				elem.appendChild(p);
			}
		}
		return elem;
	}

	public static ConnectionDescription unserialize(Element element) {
		ConnectionDescription desc = new ConnectionDescription(null);
		try {
			desc.setUri(new URI(element.getAttribute("uri")));
		}
		catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		desc.parameters.put("bufferStrategy", element.getAttribute("buffer"));
		desc.parameters.put("username", element.getAttribute("username"));
		desc.secretParameters.put("password", element.getAttribute("password"));

		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if ((n.getNodeType() == Node.ELEMENT_NODE) && "Param".equals(n.getNodeName())) {
				Element e = (Element) n;
				desc.parameters.put(e.getAttribute("name"), e.getAttribute("value"));
			}
			if ((n.getNodeType() == Node.ELEMENT_NODE) && "SecretParam".equals(n.getNodeName())) {
				Element e = (Element) n;
				desc.secretParameters.put(e.getAttribute("name"), e.getAttribute("value"));
			}
		}

		return desc;
	}

	public ConnectionDescription(URI uri) {
		this.uri = uri;
	}

	public String getParameter(final String name) {
		return parameters.get(name);
	}

	public void setParameter(final String name, final String value) {
		parameters.put(name, value);
	}

	public String getSecretParameter(final String name) {
		return Crypt.decrypt(secretParameters.get(name));
	}

	public final void setSecretParameter(final String name, final String value) {
		secretParameters.put(name, Crypt.encrypt(value));
	}

	@Override
	public String toString() {
		return uri.toString();
	}

	public void setUri(final URI uri) {
		this.uri = uri;
	}

	public URI getUri() {
		return uri;
	}

	public String getDisplayPath() {
		if ("file".equals(uri.getScheme())) {
			File f = new File(uri);
			try {
				return f.getCanonicalPath();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return uri.toString();
	}
}
