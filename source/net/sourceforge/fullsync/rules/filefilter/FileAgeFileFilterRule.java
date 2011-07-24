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

import net.sourceforge.fullsync.SystemDate;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

/**
 * @author Michele Aiello
 */
public class FileAgeFileFilterRule extends FileFilterRule {

	private static final long serialVersionUID = 2L;

	public static String typeName = "File age";

	public static final int OP_IS = 0;
	public static final int OP_ISNT = 1;
	public static final int OP_IS_GREATER_THAN = 2;
	public static final int OP_IS_LESS_THAN = 3;

	private static final String[] allOperators = new String[] { "is", "isn't", "is greater than", "is less than" };

	private AgeValue age;
	private int op;

	@Override
	public String getRuleType() {
		return typeName;
	}

	public static String[] getAllOperators() {
		return allOperators;
	}

	public FileAgeFileFilterRule(AgeValue age, int operator) {
		this.age = age;
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
		return age;
	}

	@Override
	public boolean match(File file) throws FilterRuleNotAppliableException {
		FileAttributes attrs = file.getFileAttributes();
		if (attrs == null) {
			throw new FilterRuleNotAppliableException("The file doesn't have any size attribute");
		}

		long lastModified = attrs.getLastModified();
		long now = SystemDate.getInstance().currentTimeMillis();
		double delta = (Math.floor(now / 1000.0) - Math.floor(lastModified / 1000.0));
		switch (op) {
			case OP_IS:
				return delta == age.getSeconds();

			case OP_ISNT:
				return delta != age.getSeconds();

			case OP_IS_GREATER_THAN:
				return delta > age.getSeconds();

			case OP_IS_LESS_THAN:
				return delta < age.getSeconds();
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer(30);

		buff.append("file age ");
		buff.append(allOperators[op]);
		buff.append(" '");
		buff.append(age);
		buff.append("'");
		return buff.toString();
	}

}
