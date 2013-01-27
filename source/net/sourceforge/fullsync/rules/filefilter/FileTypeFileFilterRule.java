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

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;

/**
 * @author Michele Aiello
 */
public class FileTypeFileFilterRule extends FileFilterRule {

	private static final long serialVersionUID = 2L;

	public static String typeName = "File type";

	public static final int OP_IS = 0;
	public static final int OP_ISNT = 1;

	private static final String[] allOperators = new String[] { "is", "isn't" };

	private TypeValue type;
	private int op;

	@Override
	public String getRuleType() {
		return typeName;
	}

	public static String[] getAllOperators() {
		return allOperators;
	}

	public static String[] getAllOperands() {
		return TypeValue.getAllTypes();
	}

	public FileTypeFileFilterRule(TypeValue type, int operator) {
		this.type = type;
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
		return type;
	}

	@Override
	public boolean match(File file) {
		switch (op) {
			case OP_IS:
				return (((type.isFile()) && file.isFile()) || ((type.isDirectory()) && file.isDirectory()));

			case OP_ISNT:
				return !(((type.isFile()) && file.isFile()) || ((type.isDirectory()) && file.isDirectory()));

			default:
				return false;
		}
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer(30);

		buff.append("file type ");
		buff.append(allOperators[op]);
		buff.append(" '");
		buff.append(type.toString());
		buff.append('\'');

		return buff.toString();
	}
}
