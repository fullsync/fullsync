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
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

public class FileNameFileFilterRule extends FileFilterRule {

	private static final long serialVersionUID = 2L;

	public static String typeName = "File name";

	public static final int OP_IS = 0;
	public static final int OP_ISNT = 1;
	public static final int OP_CONTAINS = 2;
	public static final int OP_DOESNT_CONTAINS = 3;
	public static final int OP_BEGINS_WITH = 4;
	public static final int OP_DOESNT_BEGINS_WITH = 5;
	public static final int OP_ENDS_WITH = 6;
	public static final int OP_DOESNT_ENDS_WITH = 7;
	public static final int OP_MATCHES_REGEXP = 8;
	public static final int OP_DOESNT_MATCHES_REGEXP = 9;

	private static final String[] allOperators = new String[] { "is", "isn't", "contains", "doesn't contains", "begins with",
			"doesn't begins with", "ends with", "doesn't ends with", "matches regexp", "doesn't matches regexp" };

	private TextValue pattern;
	private int op;

	private Pattern regexppattern;

	@Override
	public String getRuleType() {
		return typeName;
	}

	public static String[] getAllOperators() {
		return allOperators;
	}

	public FileNameFileFilterRule(TextValue pattern, int operator) {
		this.pattern = pattern;
		this.op = operator;

		if ((operator == OP_MATCHES_REGEXP) || (operator == OP_DOESNT_MATCHES_REGEXP)) {
			try {
				this.regexppattern = Pattern.compile(this.pattern.getValue());
			}
			catch (PatternSyntaxException e) {
				this.pattern.setValue("");
				ExceptionHandler.reportException(e);
			}
		}
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
		return pattern;
	}

	@Override
	public boolean match(File file) {
		String name = file.getName();

		switch (op) {
			case OP_IS:
				return name.equals(pattern.getValue());

			case OP_ISNT:
				return !name.equals(pattern.getValue());

			case OP_CONTAINS:
				return (name.indexOf(pattern.getValue()) >= 0);

			case OP_DOESNT_CONTAINS:
				return (name.indexOf(pattern.getValue()) < 0);

			case OP_BEGINS_WITH:
				return name.startsWith(pattern.getValue());

			case OP_DOESNT_BEGINS_WITH:
				return !name.startsWith(pattern.getValue());

			case OP_ENDS_WITH:
				return name.endsWith(pattern.getValue());

			case OP_DOESNT_ENDS_WITH:
				return !name.endsWith(pattern.getValue());

			case OP_MATCHES_REGEXP:
				return regexppattern.matcher(name).matches();

			case OP_DOESNT_MATCHES_REGEXP:
				return !regexppattern.matcher(name).matches();

			default:
				return false;
		}
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer(30);

		buff.append("file name ");
		buff.append(allOperators[op]);
		buff.append(" '");
		buff.append(pattern.toString());
		buff.append('\'');

		return buff.toString();
	}
}
