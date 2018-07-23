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

import java.util.StringTokenizer;

import net.sourceforge.fullsync.DataParseException;

/**
 * TODO the eights day of week should be wrapped to the first! (in cron: 0 == 7)
 * if( daysOfWeek.bArray[8] )
 * daysOfWeek.bArray[1] = true;
 */
public class CrontabPart {
	public static final CrontabPart MINUTES = new CrontabPart(0, 59, 0);
	public static final CrontabPart HOURS = new CrontabPart(0, 23, 0);
	public static final CrontabPart DAYSOFMONTH = new CrontabPart(1, 31, 0);
	public static final CrontabPart MONTHS = new CrontabPart(1, 12, -1);
	public static final CrontabPart DAYSOFWEEK = new CrontabPart(0, 7, +1);

	public class Instance {
		public final String pattern;
		public final boolean[] bArray;
		public final boolean all;

		public Instance(String pattern) throws DataParseException {
			this.pattern = pattern;
			this.bArray = new boolean[high + 1 + offset];
			this.all = parseToken(pattern);
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
			StringBuilder p = new StringBuilder();
			for (int i = low; i <= high; i++) {
				if (this.bArray[i + offset]) {
					p.append(String.valueOf(i)).append(',');
				}
			}
			if (p.length() == 0) {
				return "0"; //$NON-NLS-1$
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
				index = token.indexOf('/');

				if (index > 0) {
					each = Integer.parseInt(token.substring(index + 1));
					if (each == 0) {
						throw new DataParseException("CrontabPart.NeverUseExpressions"); // FIXME: Messages.getString("CrontabPart.NeverUseExpressions"));
					}

					token = token.substring(0, index);
				}

				if ("*".equals(token)) { //$NON-NLS-1$
					for (i = low; i < (bArray.length - offset); i += each) {
						bArray[i + offset] = true;
					}
					return each == 1;
				}

				index = token.indexOf(',');
				if (index > 0) {
					StringTokenizer tokenizer = new StringTokenizer(token, ","); //$NON-NLS-1$
					while (tokenizer.hasMoreElements()) {
						parseToken(tokenizer.nextToken());
					}
					return false;
				}

				index = token.indexOf('-');
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
				throw new DataParseException("CrontabPart.SomethingWasWrong" + token, e); // FIXME: translation Messages.getString("CrontabPart.SomethingWasWrong") + token
			}
		}
	}

	public final int low;
	public final int offset;
	public final int high;

	public CrontabPart(int low, int high, int offset) {
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
