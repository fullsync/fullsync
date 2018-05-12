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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConnectionDescription {
	private static final String ELEMENT_SECRET_PARAM = "SecretParam"; //$NON-NLS-1$
	private static final String ELEMENT_PARAM = "Param"; //$NON-NLS-1$
	public static final String PARAMETER_BUFFER_STRATEGY = "bufferStrategy"; //$NON-NLS-1$
	public static final String PARAMETER_USERNAME = "username"; //$NON-NLS-1$
	public static final String PARAMETER_PASSWORD = "password"; //$NON-NLS-1$
	public static final String PARAMETER_INTERACTIVE = "interactive"; //$NON-NLS-1$
	private static final String ATTRIBUTE_URI = "uri"; //$NON-NLS-1$
	private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	private static final String ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$
	private static final String ATTRIBUTE_BUFFER_STRATEGY = "buffer"; //$NON-NLS-1$
	private static final String ATTRIBUTE_USERNAME = PARAMETER_USERNAME;
	private static final String ATTRIBUTE_PASSWORD = PARAMETER_PASSWORD;

	private static final Logger logger = LoggerFactory.getLogger(ConnectionDescription.class);

	private final URI uri;
	private final Map<String, String> parameters = new HashMap<>();
	private final Map<String, String> secretParameters = new HashMap<>();

	public Element serialize(String name, Document doc) {
		Element elem = doc.createElement(name);
		elem.setAttribute(ATTRIBUTE_URI, uri.toString());
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			switch (entry.getKey()) {
				case PARAMETER_USERNAME:
					elem.setAttribute(ATTRIBUTE_USERNAME, entry.getValue());
					break;
				case PARAMETER_BUFFER_STRATEGY:
					elem.setAttribute(ATTRIBUTE_BUFFER_STRATEGY, entry.getValue());
					break;
				default:
					Element p = doc.createElement(ELEMENT_PARAM);
					p.setAttribute(ATTRIBUTE_NAME, entry.getKey());
					p.setAttribute(ATTRIBUTE_VALUE, entry.getValue());
					elem.appendChild(p);
					break;
			}
		}
		for (Map.Entry<String, String> entry : secretParameters.entrySet()) {
			switch (entry.getKey()) {
				case PARAMETER_PASSWORD:
					elem.setAttribute(ATTRIBUTE_PASSWORD, entry.getValue());
					break;
				default:
					Element p = doc.createElement(ELEMENT_SECRET_PARAM);
					p.setAttribute(ATTRIBUTE_NAME, entry.getKey());
					p.setAttribute(ATTRIBUTE_VALUE, entry.getValue());
					elem.appendChild(p);
					break;
			}
		}
		return elem;
	}

	public static ConnectionDescription unserialize(Element element) {
		String uriAttribute = element.getAttribute(ATTRIBUTE_URI);
		URI uri = null;
		try {
			uri = new URI(uriAttribute);
		}
		catch (URISyntaxException ex) {
			logger.warn("could not parse '" + uriAttribute + "'", ex);
		}
		ConnectionDescription desc = new ConnectionDescription(uri);
		desc.parameters.put(PARAMETER_BUFFER_STRATEGY, element.getAttribute(ATTRIBUTE_BUFFER_STRATEGY));
		desc.parameters.put(PARAMETER_USERNAME, element.getAttribute(ATTRIBUTE_USERNAME));
		desc.secretParameters.put(PARAMETER_PASSWORD, element.getAttribute(ATTRIBUTE_PASSWORD));

		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				if (ELEMENT_PARAM.equals(n.getNodeName())) {
					desc.parameters.put(e.getAttribute(ATTRIBUTE_NAME), e.getAttribute(ATTRIBUTE_VALUE));
				}
				if (ELEMENT_SECRET_PARAM.equals(n.getNodeName())) {
					desc.secretParameters.put(e.getAttribute(ATTRIBUTE_NAME), e.getAttribute(ATTRIBUTE_VALUE));
				}
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
		return Obfuscator.deobfuscate(secretParameters.get(name));
	}

	public final void setSecretParameter(final String name, final String value) {
		secretParameters.put(name, Obfuscator.obfuscate(value));
	}

	@Override
	public String toString() {
		return uri.toString();
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
			catch (IOException ex) {
				logger.debug("failed to canonicalize file path", ex);
			}
		}
		return uri.toString();
	}
}
