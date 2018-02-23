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

public class AgeValue implements OperandValue {
	public enum Unit {
		SECONDS,
		MINUTES,
		HOURS,
		DAYS
	}

	private static final long SECONDS_PER_DAY = 60L * 60L * 24L;

	private double value;
	private Unit unit;

	public AgeValue() {
		this.value = 0;
		this.unit = Unit.SECONDS;
	}

	public AgeValue(String age) {
		fromString(age);
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

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public long getSeconds() {
		if (Unit.DAYS.ordinal() > unit.ordinal()) {
			return (long) Math.floor(value * Math.pow(60, unit.ordinal()));
		}
		return (long) Math.floor(value * SECONDS_PER_DAY);
	}

	@Override
	public void fromString(String value) {
		int sep = value.indexOf(' ');
		if (sep > 0) {
			this.value = Double.parseDouble(value.substring(0, sep));
			String u = value.substring(sep + 1, value.length());
			this.unit = getUnitFromString(u);
		}
	}

	public Unit getUnitFromString(String unitName) {
		for (Unit u : Unit.values()) {
			if (u.name().equalsIgnoreCase(unitName)) {
				return u;
			}
		}
		return Unit.SECONDS;
	}

	@Override
	public String toString() {
		return String.valueOf(value) + " " + unit.toString();
	}
}
