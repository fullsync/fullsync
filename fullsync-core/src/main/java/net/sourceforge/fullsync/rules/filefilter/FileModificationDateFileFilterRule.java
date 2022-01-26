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
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.DateValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

public class FileModificationDateFileFilterRule implements FileFilterRule {
	public static final String TYPE_NAME = "File modification date";
	public static final int OP_IS = 0;
	public static final int OP_ISNT = 1;
	public static final int OP_IS_BEFORE = 2;
	public static final int OP_IS_AFTER = 3;
	private static final String[] allOperators = {
		"is",
		"isn't",
		"is before",
		"is after"
	};
	private final DateValue date;
	private final int op;

	@Override
	public String getRuleType() {
		return TYPE_NAME;
	}

	public static String[] getAllOperators() {
		return allOperators;
	}

	public FileModificationDateFileFilterRule(DateValue date, int operator) {
		this.date = date;
		this.op = operator;
	}

	@Override
	public int getOperator() {
		return op;
	}

	@Override
	public String getOperatorName() {
		return allOperators[op];
	}

	@Override
	public OperandValue getValue() {
		return date;
	}

	@Override
	public boolean match(File file) throws FilterRuleNotAppliableException {
		var lastModified = file.getLastModified();
		if (-1 == lastModified) {
			throw new FilterRuleNotAppliableException("The file or directory doesn't have any modification date");
		}
		return switch (op) {
			case OP_IS -> date.isEqualTo(lastModified);
			case OP_ISNT -> !date.isEqualTo(lastModified);
			case OP_IS_BEFORE -> date.isBefore(lastModified);
			case OP_IS_AFTER -> date.isAfter(lastModified);
			default -> false;
		};
	}

	@Override
	public String toString() {
		return "file modification date " + allOperators[op] + " '" + date + "'";
	}
}
