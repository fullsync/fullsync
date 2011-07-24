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
package net.full.fs.ui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import net.sourceforge.fullsync.ConnectionDescription;

public class LocationDescription {
	private URI uri;
	private Properties properties;

	public LocationDescription(URI uri) {
		this.uri = uri;
		this.properties = new Properties();
	}

	public LocationDescription(ConnectionDescription conn) throws URISyntaxException {
		this(new URI(conn.getUri()));

		if (conn.getUsername() != null) {
			this.properties.setProperty("username", conn.getUsername());
			this.properties.setProperty("password", conn.getPassword());
		}
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public URI getUri() {
		return uri;
	}

	public String getProperty(String name) {
		return properties.getProperty(name);
	}

	public void setProperty(String name, String value) {
		properties.setProperty(name, value);
	}

	public ConnectionDescription toConnectionDescription() {
		ConnectionDescription desc;
		desc = new ConnectionDescription(uri.toString(), ""); //$NON-NLS-1$

		if (getProperty("username") != null) {
			desc.setUsername(getProperty("username"));
			desc.setPassword(getProperty("password"));
		}
		return desc;
	}
}
