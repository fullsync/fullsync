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
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.rules.filefilter.values.DateValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

/**
 * @author Michele Aiello
 */
public class FileModificationDateFileFilterRule extends FileFilterRule {

	public static String typeName = "File modification date";

	public static final int OP_IS = 0;
	public static final int OP_ISNT = 1;
	public static final int OP_IS_BEFORE = 2;
	public static final int OP_IS_AFTER = 3;

	private static final String[] allOperators = new String[] { "is", "isn't", "is before", "is after" };

	private DateValue date;
	private int op;

	public String getRuleType() {
		return typeName;
	}

	public static String[] getAllOperators() {
		return allOperators;
	}

	public FileModificationDateFileFilterRule(DateValue date, int operator) {
		this.date = date;
		this.op = operator;
	}

	public int getOperator() {
		return op;
	}

	public String getOperatorName() {
		return allOperators[op];
	}

	public OperandValue getValue() {
		return date;
	}

	public boolean match(File file) throws FilterRuleNotAppliableException {
		FileAttributes attrs = file.getFileAttributes();
		if (attrs == null) {
			throw new FilterRuleNotAppliableException("The file doesn't have any size attribute");
		}

		long lastModified = attrs.getLastModified();
		switch (op) {
			case OP_IS:
				return date.equals(lastModified);

			case OP_ISNT:
				return !date.equals(lastModified);

			case OP_IS_BEFORE:
				return date.isBefore(lastModified);

			case OP_IS_AFTER:
				return date.isAfter(lastModified);
		}
		return false;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer(30);

		buff.append("file modification date ");
		buff.append(allOperators[op]);
		buff.append(" '");
		buff.append(date);
		buff.append("'");
		return buff.toString();
	}

}
