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
import java.util.NoSuchElementException;
import java.util.Objects;
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
	private static final String PARAMETER_PUBLIC_KEY_AUTH = "publicKeyAuth"; //$NON-NLS-1$
	private static final String PUBLIC_KEY_AUTH_ENABLED = "enabled"; //$NON-NLS-1$
	private static final String PUBLIC_KEY_AUTH_DISABLED = "disabled"; //$NON-NLS-1$
	private static final String PARAMETER_KEY_PASSPHRASE = "keyPassphrase"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SCHEME = "scheme"; //$NON-NLS-1$
	private static final String ATTRIBUTE_HOST = "host"; //$NON-NLS-1$
	private static final String ATTRIBUTE_PORT = "port"; //$NON-NLS-1$
	private static final String ATTRIBUTE_PATH = "path"; //$NON-NLS-1$
	private static final String ATTRIBUTE_URI = "uri"; //$NON-NLS-1$
	private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	private static final String ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$
	private static final String ATTRIBUTE_BUFFER_STRATEGY = "buffer"; //$NON-NLS-1$
	private static final String ATTRIBUTE_USERNAME = "username"; //$NON-NLS-1$
	private static final String ATTRIBUTE_PASSWORD = "password"; //$NON-NLS-1$
	private static final String ATTRIBUTE_USER_DIR_IS_ROOT = "userDirIsRoot"; //$NON-NLS-1$
	private static final Logger logger = LoggerFactory.getLogger(ConnectionDescription.class);

	private final String scheme;
	private final Optional<String> host;
	private final Optional<Integer> port;
	private final String path;
	private final Optional<String> username;
	private final Optional<String> password;
	private final Optional<String> bufferStrategy;
	private final Optional<Boolean> publicKeyAuth;
	private final Optional<String> keyPassphrase;
	private final boolean userDirIsRoot;

	public Element serialize(String name, Document doc) {
		Element elem = doc.createElement(name);
		elem.setAttribute(ATTRIBUTE_SCHEME, scheme);
		host.ifPresent(s -> elem.setAttribute(ATTRIBUTE_HOST, s));
		port.ifPresent(integer -> elem.setAttribute(ATTRIBUTE_PORT, integer.toString()));
		elem.setAttribute(ATTRIBUTE_PATH, path);
		username.ifPresent(s -> elem.setAttribute(ATTRIBUTE_USERNAME, s));
		password.ifPresent(s -> elem.setAttribute(ATTRIBUTE_PASSWORD, Obfuscator.obfuscate(s)));
		bufferStrategy.ifPresent(s -> elem.setAttribute(ATTRIBUTE_BUFFER_STRATEGY, s));
		publicKeyAuth.ifPresent(aBoolean -> {
			Element p = doc.createElement(ELEMENT_PARAM);
			p.setAttribute(ATTRIBUTE_NAME, PARAMETER_PUBLIC_KEY_AUTH);
			p.setAttribute(ATTRIBUTE_VALUE, aBoolean.booleanValue() ? PUBLIC_KEY_AUTH_ENABLED : PUBLIC_KEY_AUTH_DISABLED);
			elem.appendChild(p);
		});
		keyPassphrase.ifPresent(s -> {
			Element p = doc.createElement(ELEMENT_SECRET_PARAM);
			p.setAttribute(ATTRIBUTE_NAME, PARAMETER_KEY_PASSPHRASE);
			p.setAttribute(ATTRIBUTE_VALUE, Obfuscator.obfuscate(s));
			elem.appendChild(p);
		});
		elem.setAttribute(ATTRIBUTE_USER_DIR_IS_ROOT, Boolean.toString(userDirIsRoot));
		return elem;
	}

	private static String getAttributeOrNull(Element element, String attribute) {
		String value = null;
		if (element.hasAttribute(attribute)) {
			value = element.getAttribute(attribute);
		}
		return value;
	}

	public static ConnectionDescription unserialize(Element element) {
		Builder builder = new Builder();
		if (element.hasAttribute(ATTRIBUTE_URI)) {
			String uriAttribute = element.getAttribute(ATTRIBUTE_URI);
			try {
				URI uri = new URI(uriAttribute);
				builder.setScheme(uri.getScheme());
				builder.setHost(uri.getHost());
				builder.setPort(uri.getPort());
				builder.setPath(uri.getPath());
				builder.setUserDirIsRoot(true);
			}
			catch (URISyntaxException ex) {
				logger.warn("could not parse '" + uriAttribute + "'", ex); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		else {
			builder.setScheme(element.getAttribute(ATTRIBUTE_SCHEME));
			builder.setHost(getAttributeOrNull(element, ATTRIBUTE_HOST));
			if (element.hasAttribute(ATTRIBUTE_PORT)) {
				builder.setPort(Integer.valueOf(element.getAttribute(ATTRIBUTE_PORT)));
			}
			builder.setPath(element.getAttribute(ATTRIBUTE_PATH));
			builder.setUserDirIsRoot(Boolean.valueOf(element.getAttribute(ATTRIBUTE_USER_DIR_IS_ROOT)));
		}
		builder.setUsername(getAttributeOrNull(element, ATTRIBUTE_USERNAME));
		builder.setPassword(Obfuscator.deobfuscate(getAttributeOrNull(element, ATTRIBUTE_PASSWORD)));
		builder.setBufferStrategy(getAttributeOrNull(element, ATTRIBUTE_BUFFER_STRATEGY));

		NodeList list = element.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				switch (n.getNodeName()) {
					case ELEMENT_PARAM:
						if (PARAMETER_PUBLIC_KEY_AUTH.equals(e.getAttribute(ATTRIBUTE_NAME))) {
							builder.setPublicKeyAuth(PUBLIC_KEY_AUTH_ENABLED.equals(e.getAttribute(ATTRIBUTE_VALUE)));
						}
						break;
					case ELEMENT_SECRET_PARAM:
						if (PARAMETER_KEY_PASSPHRASE.equals(e.getAttribute(ATTRIBUTE_NAME))) {
							builder.setKeyPassphrase(Obfuscator.deobfuscate(e.getAttribute(ATTRIBUTE_VALUE)));
						}
						break;
				}
			}
		}
		return builder.build();
	}

	public ConnectionDescription(String scheme, Optional<String> host, Optional<Integer> port, String path, Optional<String> username,
		Optional<String> password, Optional<String> bufferStrategy, Optional<Boolean> publicKeyAuthEnabled, Optional<String> keyPassphrase,
		boolean userDirIsRoot) {
		Objects.requireNonNull(scheme, "Scheme must be provided"); //$NON-NLS-1$
		Objects.requireNonNull(path, "Path must be provided"); //$NON-NLS-1$
		this.scheme = scheme;
		this.host = host;
		this.port = port;
		this.path = path;
		this.username = username;
		this.password = password;
		this.publicKeyAuth = publicKeyAuthEnabled;
		this.keyPassphrase = keyPassphrase;
		this.bufferStrategy = bufferStrategy;
		this.userDirIsRoot = userDirIsRoot;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		ConnectionDescription that = (ConnectionDescription) o;
		return (userDirIsRoot == that.userDirIsRoot)
			&& Objects.equals(scheme, that.scheme)
			&& Objects.equals(host, that.host)
			&& Objects.equals(port, that.port)
			&& Objects.equals(path, that.path)
			&& Objects.equals(username, that.username)
			&& Objects.equals(password, that.password)
			&& Objects.equals(bufferStrategy, that.bufferStrategy)
			&& Objects.equals(publicKeyAuth, that.publicKeyAuth)
			&& Objects.equals(keyPassphrase, that.keyPassphrase);
	}

	@Override
	public int hashCode() {
		return Objects.hash(scheme, host, port, path, username, password, bufferStrategy, publicKeyAuth, keyPassphrase, userDirIsRoot);
	}

	public String getScheme() {
		return scheme;
	}

	public Optional<String> getHost() {
		return host;
	}

	public Optional<Integer> getPort() {
		return port;
	}

	public String getPath() {
		return path;
	}

	public Optional<String> getUsername() {
		return username;
	}

	public Optional<String> getPassword() {
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

	public boolean isUserDirIsRoot() {
		return userDirIsRoot;
	}

	public String getDisplayPath() {
		if ("file".equals(getScheme())) { //$NON-NLS-1$
			File f = new File(getPath());
			try {
				return f.getCanonicalPath();
			}
			catch (IOException ex) {
				logger.debug("failed to canonicalize file path", ex); //$NON-NLS-1$
			}
		}
		try {
			String portSuffix = port.map(integer -> ":" + integer).orElse(""); //$NON-NLS-1$ //$NON-NLS-2$
			if ("sftp".equals(scheme) && (22 == port.orElse(-1))) { //$NON-NLS-1$
				String userPrefix = username.map(s -> s + "@").orElse(""); //$NON-NLS-1$ //$NON-NLS-2$
				String displayPath = userDirIsRoot ? path : '/' + path;
				return String.format("%s%s:%s", userPrefix, host.get(), displayPath); //$NON-NLS-1$
			}
			else {
				return String.format("%s://%s%s%s", getScheme(), host.get(), portSuffix, path); //$NON-NLS-1$
			}
		}
		catch (NoSuchElementException ex) {
			// NoSuchElementException should never happen as file is the only protocol without host and port
			logger.debug("failed to construct display URL", ex); //$NON-NLS-1$
		}
		return "<error>"; //$NON-NLS-1$
	}

	public static class Builder {
		private String scheme;
		private Optional<String> host = Optional.empty();
		private Optional<Integer> port = Optional.empty();
		private String path;
		private Optional<String> username = Optional.empty();
		private Optional<String> password = Optional.empty();
		private Optional<String> bufferStrategy = Optional.empty();
		private Optional<Boolean> publicKeyAuth = Optional.empty();
		private Optional<String> keyPassphrase = Optional.empty();
		private boolean userDirIsRoot = false;

		public Builder() {
		}

		public Builder(ConnectionDescription desc) {
			scheme = desc.getScheme();
			host = desc.getHost();
			port = desc.getPort();
			path = desc.getPath();
			username = desc.getUsername();
			password = desc.getPassword();
			bufferStrategy = desc.getBufferStrategy();
			publicKeyAuth = desc.getPublicKeyAuth();
			keyPassphrase = desc.getKeyPassphrase();
			userDirIsRoot = desc.isUserDirIsRoot();
		}

		public ConnectionDescription build() {
			return new ConnectionDescription(scheme, host, port, path, username, password, bufferStrategy, publicKeyAuth, keyPassphrase,
				userDirIsRoot);
		}

		public void setScheme(String scheme) {
			this.scheme = scheme;
		}

		public void setHost(String host) {
			this.host = Optional.ofNullable(host);
		}

		public void setPort(int port) {
			this.port = port > 0 ? Optional.of(port) : Optional.empty();
		}

		public void setPath(String path) {
			this.path = path;
		}

		public void setUsername(String username) {
			this.username = Optional.ofNullable(username);
		}

		public void setPassword(String password) {
			this.password = Optional.ofNullable(password);
		}

		public void setBufferStrategy(String bufferStrategy) {
			this.bufferStrategy = Optional.ofNullable(bufferStrategy);
		}

		public void setPublicKeyAuth(boolean publicKeyAuth) {
			this.publicKeyAuth = Optional.of(publicKeyAuth);
		}

		public void setKeyPassphrase(String keyPassphrase) {
			this.keyPassphrase = Optional.ofNullable(keyPassphrase);
		}

		public void setUserDirIsRoot(boolean userDirIsRoot) {
			this.userDirIsRoot = userDirIsRoot;
		}
	}
}
