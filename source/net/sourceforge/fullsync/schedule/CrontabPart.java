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
package net.sourceforge.fullsync.schedule;

import java.io.Serializable;
import java.util.StringTokenizer;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.ui.Messages;

/**
 * TODO the eights day of week should be wrapped to the first! (in cron: 0 == 7)
 * if( daysOfWeek.bArray[8] )
 * daysOfWeek.bArray[1] = true;
 */
public class CrontabPart implements Serializable {
	// REVISIT we can't localize those names as we don't have an guicontroller at this point of time
	// atm the name isn't used anywhere, but if it is used, we need to think of some nice trick
	public static final CrontabPart MINUTES = new CrontabPart("minutes", 0, 59, 0); //$NON-NLS-1$
	public static final CrontabPart HOURS = new CrontabPart("hours", 0, 23, 0); //$NON-NLS-1$
	public static final CrontabPart DAYSOFMONTH = new CrontabPart("daysOfMonth", 1, 31, 0); //$NON-NLS-1$
	public static final CrontabPart MONTHS = new CrontabPart("months", 1, 12, -1); //$NON-NLS-1$
	public static final CrontabPart DAYSOFWEEK = new CrontabPart("daysOfWeek", 0, 7, +1); //$NON-NLS-1$
	public static final CrontabPart[] ALL_PARTS = new CrontabPart[] { MINUTES, HOURS, DAYSOFMONTH, MONTHS, DAYSOFWEEK };

	private static final long serialVersionUID = 2L;

	public class Instance implements Serializable {
		private static final long serialVersionUID = 2L;
		public final String pattern;
		public final boolean[] bArray;
		public final boolean all;

		public Instance(String pattern) throws DataParseException {
			this.pattern = pattern;
			this.bArray = new boolean[high + 1 + offset];
			this.all = parseToken(pattern);
		}

		public Instance(boolean[] bArray) {
			this.bArray = new boolean[high + 1 + offset];
			this.pattern = generatePattern();
			this.all = false;
		}

		public Instance(int[] intArray, int intOffset) {
			bArray = new boolean[high + 1 + offset];
			setIntArray(intArray, intOffset);
			this.pattern = generatePattern();
			this.all = false;
		}

		private void setIntArray(int[] intArray, int intOffset) {
			for (int i = 0; i < intArray.length; i++) {
				bArray[(intArray[i] - intOffset) + offset] = true;
			}
		}

		public int[] getIntArray(int intOffset) {
			int[] a = new int[high + 1];
			int aPos = 0;
			for (int i = low; i <= high; i++) {
				if (bArray[i + offset]) {
					a[aPos++] = i + intOffset;
				}
			}
			int[] res;
			if (aPos == (high + 1)) {
				res = a;
			}
			else {
				res = new int[aPos];
				for (int i = 0; i < aPos; i++) {
					res[i] = a[i];
				}
			}
			return res;
		}

		private String generatePattern() {
			StringBuffer p = new StringBuffer();
			for (int i = low; i <= high; i++) {
				if (this.bArray[i + offset]) {
					p.append(String.valueOf(i)).append(',');
				}
			}
			if (p.length() == 0) {
				return "0";
			}
			else {
				return p.substring(0, p.length() - 1);
			}
		}

		private boolean parseToken(String token) throws DataParseException {
			int i;
			int index;
			int each = 1;

			try {
				// Look for step first
				index = token.indexOf("/");

				if (index > 0) {
					each = Integer.parseInt(token.substring(index + 1));
					if (each == 0) {
						throw new DataParseException(Messages.getString("CrontabPart.NeverUseExpressions")); //$NON-NLS-1$
					}

					token = token.substring(0, index);
				}

				if (token.equals("*")) {
					for (i = low; i < (bArray.length - offset); i += each) {
						bArray[i + offset] = true;
					}
					return each == 1;
				}

				index = token.indexOf(",");
				if (index > 0) {
					StringTokenizer tokenizer = new StringTokenizer(token, ",");
					while (tokenizer.hasMoreElements()) {
						parseToken(tokenizer.nextToken());
					}
					return false;
				}

				index = token.indexOf("-");
				if (index > 0) {
					int start = Integer.parseInt(token.substring(0, index));
					int end = Integer.parseInt(token.substring(index + 1));

					for (int j = start; j <= end; j += each) {
						bArray[j + offset] = true;
					}
					return false;
				}

				int iValue = Integer.parseInt(token);
				bArray[iValue + offset] = true;
				return false;
			}
			catch (Exception e) {
				throw new DataParseException(Messages.getString("CrontabPart.SomethingWasWrong") + token, e); //$NON-NLS-1$
			}
		}
	}

	public final String name;

	public final int low;
	public final int offset;
	public final int high;

	public CrontabPart(String name, int low, int high, int offset) {
		this.name = name;
		this.low = low;
		this.high = high;
		this.offset = offset;
	}

	public Instance createInstance(String pattern) throws DataParseException {
		return new Instance(pattern);
	}

	public Instance createInstance(int[] intArray, int intOffset) {
		return new Instance(intArray, intOffset);
	}
}
