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
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConnectionDescription implements Serializable {
	public static final String PARAMETER_USERNAME = "username";
	public static final String PARAMETER_PASSWORD = "password";
	public static final String PARAMETER_INTERACTIVE = "interactive";

	private static final long serialVersionUID = 2L;

	private URI uri;
	private Map<String, String> parameters = new HashMap<>();
	private Map<String, String> secretParameters = new HashMap<>();

	public Element serialize(String name, Document doc) {
		Element elem = doc.createElement(name);
		elem.setAttribute("uri", uri.toString());
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			switch (entry.getKey()) {
				case PARAMETER_USERNAME:
					elem.setAttribute(PARAMETER_USERNAME, entry.getValue());
					break;
				case "bufferStrategy":
					elem.setAttribute("buffer", entry.getValue());
					break;
				default:
					Element p = doc.createElement("Param");
					p.setAttribute("name", entry.getKey());
					p.setAttribute("value", entry.getValue());
					elem.appendChild(p);
					break;
			}
		}
		for (Map.Entry<String, String> entry : secretParameters.entrySet()) {
			switch (entry.getKey()) {
				case PARAMETER_PASSWORD:
					elem.setAttribute(PARAMETER_PASSWORD, entry.getValue());
					break;
				default:
					Element p = doc.createElement("SecretParam");
					p.setAttribute("name", entry.getKey());
					p.setAttribute("value", entry.getValue());
					elem.appendChild(p);
					break;
			}
		}
		return elem;
	}

	public static ConnectionDescription unserialize(Element element) {
		ConnectionDescription desc = new ConnectionDescription(null);
		try {
			desc.setUri(new URI(element.getAttribute("uri")));
		}
		catch (URISyntaxException ex) {
			ex.printStackTrace();
		}
		desc.parameters.put("bufferStrategy", element.getAttribute("buffer"));
		desc.parameters.put(PARAMETER_USERNAME, element.getAttribute(PARAMETER_USERNAME));
		desc.secretParameters.put(PARAMETER_PASSWORD, element.getAttribute(PARAMETER_PASSWORD));

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

	public void clearParameter(final String name) {
		parameters.remove(name);
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
