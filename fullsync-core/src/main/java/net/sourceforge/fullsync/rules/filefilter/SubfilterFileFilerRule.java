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

import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.rules.filefilter.values.FilterValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

public class SubfilterFileFilerRule implements FileFilterRule {
	public static final String TYPE_NAME = "Nested Filter";
	private final FileFilter fileFilter;

	public SubfilterFileFilerRule(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	@Override
	public String getRuleType() {
		return TYPE_NAME;
	}

	@Override
	public int getOperator() {
		return 0;
	}

	@Override
	public String getOperatorName() {
		return "";
	}

	@Override
	public OperandValue getValue() {
		return new FilterValue(fileFilter);
	}

	@Override
	public boolean match(FSFile file) throws FilterRuleNotAppliableException {
		return fileFilter.match(file);
	}

	@Override
	public String toString() {
		return "(" + fileFilter.toString() + ")";
	}
}
