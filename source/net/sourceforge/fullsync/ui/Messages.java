/*
 * Created on Dec 22, 2004
 */
package net.sourceforge.fullsync.ui;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sourceforge.fullsync.ExceptionHandler;


/**
 * @author Michele Aiello
 */
public class Messages {
	private static final String BUNDLE_NAME = "net.sourceforge.fullsync.ui.messages";//$NON-NLS-1$
	private ResourceBundle RESOURCE_BUNDLE;
	
	private static Messages _instance;
	
	private Messages() {
		String code = GuiController.getInstance().getPreferences().getLanguageCode();
		Locale langLocale = new Locale(code);
		try {
            Locale.setDefault(langLocale);
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, langLocale);
		} catch (Throwable e) {
			ExceptionHandler.reportException( "Unable to find locale for language "+code, e);
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
		}
	}

	public static String getString(String key) 
    {
		if (_instance == null) {
			_instance = new Messages();
		}
		
		try {
			return _instance.RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			e.printStackTrace();
			return '!' + key + '!';
		}
	}
    
    public static String getString( String key, String value )
    {
        String msg = getString( key );
        return MessageFormat.format( msg, new Object[] { value } );
    }
}