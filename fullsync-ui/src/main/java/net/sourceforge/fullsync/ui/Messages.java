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
package net.sourceforge.fullsync.ui;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fullsync.ExceptionHandler;

public class Messages {
	private static final String BUNDLE_NAME = "net.sourceforge.fullsync.ui.messages";//$NON-NLS-1$
	private static Messages instance;
	private static final Logger logger = LoggerFactory.getLogger(Messages.class);

	private ResourceBundle bundle;

	private Messages() {
		String code = GuiController.getInstance().getPreferences().getLanguageCode();
		Locale langLocale = new Locale(code);
		try {
			Locale.setDefault(langLocale);
			bundle = ResourceBundle.getBundle(BUNDLE_NAME, langLocale);
		}
		catch (MissingResourceException e) {
			ExceptionHandler.reportException("Unable to find locale for language " + code, e);
			bundle = ResourceBundle.getBundle(BUNDLE_NAME);
		}
	}

	public static String getString(final String key) {
		if (null == instance) {
			instance = new Messages();
		}

		try {
			String value = instance.bundle.getString(key);
			if ((null != value) && (!value.isEmpty())) {
				return value;
			}
			else {
				logger.warn("Missing translation for key " + key, new Exception("key exists but is empty"));
			}
		}
		catch (Exception e) {
			logger.warn("Missing translation for key " + key, e);
		}
		return '!' + key + '!';
	}

	public static String getString(final String key, final String value) {
		String msg = getString(key);
		return MessageFormat.format(msg, value);
	}
}
