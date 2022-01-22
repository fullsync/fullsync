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

public class AgeValue implements OperandValue {
	public enum Unit {
		SECONDS,
		MINUTES,
		HOURS,
		DAYS,
	}

	private static final long SECONDS_PER_DAY = 60L * 60L * 24L;
	private final double value;
	private final Unit unit;

	public AgeValue(String age) throws DataParseException {
		var sep = age.indexOf(' ');
		if (sep > 0) {
			try {
				this.value = Double.parseDouble(age.substring(0, sep));
			}
			catch (NumberFormatException ex) {
				throw new DataParseException("", ex);
			}
			var u = age.substring(sep + 1);
			this.unit = getUnitFromString(u);
		}
		else {
			throw new DataParseException(String.format("'%s' is not a valid Age value", age));
		}
	}

	public AgeValue(double value, Unit unit) {
		this.value = value;
		this.unit = unit;
	}

	public double getValue() {
		return value;
	}

	public Unit getUnit() {
		return unit;
	}

	public long getSeconds() {
		if (Unit.DAYS.ordinal() > unit.ordinal()) {
			return (long) Math.floor(value * Math.pow(60, unit.ordinal()));
		}
		return (long) Math.floor(value * SECONDS_PER_DAY);
	}

	public Unit getUnitFromString(String unitName) throws DataParseException {
		for (Unit u : Unit.values()) {
			if (u.name().equalsIgnoreCase(unitName)) {
				return u;
			}
		}
		throw new DataParseException(String.format("'%s' is not a valid unit name", unitName));
	}

	@Override
	public String toString() {
		return value + " " + unit.toString();
	}
}
