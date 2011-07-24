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
 * Created on 6-feb-2006
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.FilterValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

/**
 * @author Michele Aiello
 */
public class SubfilterFileFilerRule extends FileFilterRule {

	public static String typeName = "Nested Filter";

	private FileFilter fileFilter;

	public SubfilterFileFilerRule(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public String getRuleType() {
		return typeName;
	}

	public int getOperator() {
		return 0;
	}

	public String getOperatorName() {
		return "";
	}

	public OperandValue getValue() {
		return new FilterValue(fileFilter);
	}

	public boolean match(File file) throws FilterRuleNotAppliableException {
		return fileFilter.match(file);
	}

	public String toString() {
		return "(" + fileFilter.toString() + ")";
	}

}
