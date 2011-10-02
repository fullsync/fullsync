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
/*
 * Created on Dec 22, 2004
 */
package net.sourceforge.fullsync.ui;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import net.sourceforge.fullsync.ExceptionHandler;

/**
 * @author Michele Aiello
 */
public class Messages {
	private static final String BUNDLE_NAME = "net.sourceforge.fullsync.ui.messages";//$NON-NLS-1$
	private ResourceBundle RESOURCE_BUNDLE;

	private static Messages instance;

	private Messages() {
		String code = GuiController.getInstance().getPreferences().getLanguageCode();
		Locale langLocale = new Locale(code);
		try {
			Locale.setDefault(langLocale);
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, langLocale);
		}
		catch (Throwable e) {
			ExceptionHandler.reportException("Unable to find locale for language " + code, e);
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
		}
	}

	public static String getString(final String key) {
		String value = '!' + key + '!';
		if (instance == null) {
			instance = new Messages();
		}

		try {
			value = instance.RESOURCE_BUNDLE.getString(key);
			if ((null != value) && (value.length() > 0)) {
				return value;
			}
			else {
				value = '!' + key + '!';
				throw new Exception("WARNING: Translation for message '" + key + "' missing!"); //$NON-NLS-1$
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public static String getString(final String key, final String value) {
		String msg = getString(key);
		return MessageFormat.format(msg, new Object[] { value });
	}
}
