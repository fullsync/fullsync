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
package net.sourceforge.fullsync.rules.filefilter.values;

import net.sourceforge.fullsync.DataParseException;

public class SizeValue implements OperandValue {
	public enum Unit {
		BYTES,
		KBYTES,
		MBYTES,
		GBYTES,
	}

	private final double value;
	private final Unit unit;

	public SizeValue(double value, Unit unit) {
		this.value = value;
		this.unit = unit;
	}

	public SizeValue(String size) throws DataParseException {
		var sep = size.indexOf(' ');
		if (sep > 0) {
			try {
				this.value = Double.parseDouble(size.substring(0, sep));
			}
			catch (NumberFormatException ex) {
				throw new DataParseException(String.format("'%s' is not a valid number", size), ex);
			}
			var u = size.substring(sep + 1);
			this.unit = getUnitFromString(u);
		}
		else {
			throw new DataParseException(String.format("'%s' is not a valid Size value", size));
		}
	}

	public long getBytes() {
		return (long) Math.floor(value * Math.pow(1024, unit.ordinal()));
	}

	public double getValue() {
		return value;
	}

	public Unit getUnit() {
		return unit;
	}

	private Unit getUnitFromString(String unitName) throws DataParseException {
		for (Unit u : Unit.values()) {
			if (u.name().equalsIgnoreCase(unitName)) {
				return u;
			}
		}
		throw new DataParseException(String.format("'%s' is not a valid size unit", unitName));
	}

	@Override
	public String toString() {
		return value + " " + unit.toString();
	}
}
