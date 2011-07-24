package net.sourceforge.fullsync;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * various utilities.
 * @author cobexer
 */
public abstract class Util {
	/**
	 * used for all I/O buffers.
	 */
	private static final int IOBUFFERSIZE = 0x1000;

	/**
	 * readStreamAsString reads everything from the given stream to a string using UTF-8 as encoding.
	 * @param is the InputStream to convert to a string
	 * @return resulting String
	 */
	public static String readStreamAsString(final InputStream is) {
		if (null != is) {
			final char[] buffer = new char[IOBUFFERSIZE];
			StringBuilder out = new StringBuilder();
			Reader in;
			try {
				in = new InputStreamReader(is, "UTF-8");
				int read;
				do {
					read = in.read(buffer, 0, buffer.length);
					if (read > 0) {
						out.append(buffer, 0, read);
					}
				} while (read >= 0);
				return out.toString();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}
}
