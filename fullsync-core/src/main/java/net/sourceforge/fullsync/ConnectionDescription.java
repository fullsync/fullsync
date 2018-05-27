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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConnectionDescription {
	private static final String ELEMENT_SECRET_PARAM = "SecretParam"; //$NON-NLS-1$
	private static final String ELEMENT_PARAM = "Param"; //$NON-NLS-1$
	private static final String PARAMETER_PUBLIC_KEY_AUTH = "publicKeyAuth";
	private static final String PUBLIC_KEY_AUTH_ENABLED = "enabled"; //$NON-NLS-1$
	private static final String PUBLIC_KEY_AUTH_DISABLED = "disabled"; //$NON-NLS-1$
	private static final String PARAMETER_KEY_PASSPHRASE = "keyPassphrase";
	private static final String ATTRIBUTE_URI = "uri"; //$NON-NLS-1$
	private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	private static final String ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$
	private static final String ATTRIBUTE_BUFFER_STRATEGY = "buffer"; //$NON-NLS-1$
	private static final String ATTRIBUTE_USERNAME = "username"; //$NON-NLS-1$
	private static final String ATTRIBUTE_PASSWORD = "password"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(ConnectionDescription.class);

	private final URI uri;
	private final String username;
	private final String password;
	private final Optional<String> bufferStrategy;
	private final Optional<Boolean> publicKeyAuth;
	private final Optional<String> keyPassphrase;

	public Element serialize(String name, Document doc) {
		Element elem = doc.createElement(name);
		elem.setAttribute(ATTRIBUTE_URI, uri.toString());
		elem.setAttribute(ATTRIBUTE_USERNAME, username);
		elem.setAttribute(ATTRIBUTE_PASSWORD, Obfuscator.obfuscate(password));
		if (bufferStrategy.isPresent()) {
			elem.setAttribute(ATTRIBUTE_BUFFER_STRATEGY, bufferStrategy.get());
		}
		if (publicKeyAuth.isPresent()) {
			Element p = doc.createElement(ELEMENT_PARAM);
			p.setAttribute(ATTRIBUTE_NAME, PARAMETER_PUBLIC_KEY_AUTH);
			p.setAttribute(ATTRIBUTE_VALUE, publicKeyAuth.get().booleanValue() ? PUBLIC_KEY_AUTH_ENABLED : PUBLIC_KEY_AUTH_DISABLED);
			elem.appendChild(p);
		}
		if (keyPassphrase.isPresent()) {
			Element p = doc.createElement(ELEMENT_SECRET_PARAM);
			p.setAttribute(ATTRIBUTE_NAME, PARAMETER_KEY_PASSPHRASE);
			p.setAttribute(ATTRIBUTE_VALUE, Obfuscator.obfuscate(keyPassphrase.get()));
			elem.appendChild(p);
		}
		return elem;
	}

	public static ConnectionDescription unserialize(Element element) {
		Builder builder = new Builder();
		String uriAttribute = element.getAttribute(ATTRIBUTE_URI);
		URI uri = null;
		try {
			uri = new URI(uriAttribute);
		}
		catch (URISyntaxException ex) {
			logger.warn("could not parse '" + uriAttribute + "'", ex);
		}
		builder.setUri(uri);
		builder.setUsername(element.getAttribute(ATTRIBUTE_USERNAME));
		builder.setPassword(Obfuscator.deobfuscate(element.getAttribute(ATTRIBUTE_PASSWORD)));
		builder.setBufferStrategy(element.getAttribute(ATTRIBUTE_BUFFER_STRATEGY));

		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				if (ELEMENT_PARAM.equals(n.getNodeName())) {
					String attribute = e.getAttribute(ATTRIBUTE_NAME);
					if (PARAMETER_PUBLIC_KEY_AUTH.equals(attribute)) {
						builder.setPublicKeyAuth(PUBLIC_KEY_AUTH_ENABLED.equals(e.getAttribute(ATTRIBUTE_VALUE)));
					}
				}
				if (ELEMENT_SECRET_PARAM.equals(n.getNodeName())) {
					String attribute = e.getAttribute(ATTRIBUTE_NAME);
					if (PARAMETER_KEY_PASSPHRASE.equals(attribute)) {
						builder.setKeyPassphrase(Obfuscator.deobfuscate(e.getAttribute(ATTRIBUTE_VALUE)));
					}
				}
			}
		}
		return builder.build();
	}

	public ConnectionDescription(URI uri, String username, String password, Optional<String> bufferStrategy,
		Optional<Boolean> publicKeyAuthEnabled, Optional<String> keyPassphrase) {
		this.uri = uri;
		this.username = username;
		this.password = password;
		this.publicKeyAuth = publicKeyAuthEnabled;
		this.keyPassphrase = keyPassphrase;
		this.bufferStrategy = bufferStrategy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + bufferStrategy.hashCode();
		result = (prime * result) + keyPassphrase.hashCode();
		result = (prime * result) + ((password == null) ? 0 : password.hashCode());
		result = (prime * result) + publicKeyAuth.hashCode();
		result = (prime * result) + ((uri == null) ? 0 : uri.hashCode());
		result = (prime * result) + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ConnectionDescription other = (ConnectionDescription) obj;
		if (!bufferStrategy.isPresent()) {
			if (other.bufferStrategy.isPresent()) {
				return false;
			}
		}
		else if (!bufferStrategy.equals(other.bufferStrategy)) {
			return false;
		}
		if (!keyPassphrase.isPresent()) {
			if (other.keyPassphrase.isPresent()) {
				return false;
			}
		}
		else if (!keyPassphrase.equals(other.keyPassphrase)) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		}
		else if (!password.equals(other.password)) {
			return false;
		}
		if (!publicKeyAuth.isPresent()) {
			if (other.publicKeyAuth.isPresent()) {
				return false;
			}
		}
		else if (!publicKeyAuth.equals(other.publicKeyAuth)) {
			return false;
		}
		if (uri == null) {
			if (other.uri != null) {
				return false;
			}
		}
		else if (!uri.equals(other.uri)) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		}
		else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Optional<String> getBufferStrategy() {
		return bufferStrategy;
	}

	public Optional<Boolean> getPublicKeyAuth() {
		return publicKeyAuth;
	}

	public Optional<String> getKeyPassphrase() {
		return keyPassphrase;
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

	public static class Builder {
		private URI uri;
		private String username;
		private String password;
		private Optional<String> bufferStrategy = Optional.empty();
		private Optional<Boolean> publicKeyAuth = Optional.empty();
		private Optional<String> keyPassphrase = Optional.empty();

		public Builder() {
		}

		public Builder(ConnectionDescription desc) {
			uri = desc.getUri();
			username = desc.getUsername();
			password = desc.getPassword();
			bufferStrategy = desc.getBufferStrategy();
			publicKeyAuth = desc.getPublicKeyAuth();
			keyPassphrase = desc.getKeyPassphrase();
		}

		public ConnectionDescription build() {
			return new ConnectionDescription(uri, username, password, bufferStrategy, publicKeyAuth, keyPassphrase);
		}

		public void setUri(URI uri) {
			this.uri = uri;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public void setBufferStrategy(String bufferStrategy) {
			this.bufferStrategy = Optional.ofNullable(bufferStrategy);
		}

		public void setPublicKeyAuth(boolean publicKeyAuth) {
			this.publicKeyAuth = Optional.ofNullable(publicKeyAuth);
		}

		public void setKeyPassphrase(String keyPassphrase) {
			this.keyPassphrase = Optional.ofNullable(keyPassphrase);
		}
	}
}
