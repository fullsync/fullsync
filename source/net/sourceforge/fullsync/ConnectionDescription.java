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

import java.io.Serializable;
import java.util.Hashtable;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ConnectionDescription implements Serializable {
	private static final long serialVersionUID = 2L;

	private String uri;
	private String bufferStrategy;
	private String username;
	private String cryptedPassword;
	private Hashtable<String, String> parameters;

	public ConnectionDescription() {
		this.uri = null;
		this.bufferStrategy = null;
		this.parameters = new Hashtable<String, String>();
	}

	public ConnectionDescription(String url, String bufferStrategy) {
		// TODO we should throw an exception if the url is bad
		this.uri = url;
		this.bufferStrategy = bufferStrategy;
		this.parameters = new Hashtable<String, String>();
	}

	public String getBufferStrategy() {
		return bufferStrategy;
	}

	public void setBufferStrategy(String bufferStrategy) {
		this.bufferStrategy = bufferStrategy;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Hashtable<String, String> getParameters() {
		return parameters;
	}

	public String getParameter(String name) {
		return parameters.get(name);
	}

	public void setParameter(String name, String value) {
		this.parameters.put(name, value);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCryptedPassword() {
		return cryptedPassword;
	}

	public void setCryptedPassword(String cryptedPassword) {
		this.cryptedPassword = cryptedPassword;
	}

	public String getPassword() {
		return Crypt.decrypt(cryptedPassword);
	}

	public void setPassword(String password) {
		this.cryptedPassword = Crypt.encrypt(password);
	}

	@Override
	public String toString() {
		return uri;
	}
}
