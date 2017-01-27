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

public class SizeValue implements OperandValue {
	public enum Unit {
		BYTES,
		KBYTES,
		MBYTES,
		GBYTES
	}

	private static final long serialVersionUID = 2L;

	private double value;
	private Unit unit;

	public SizeValue() {
		this.value = 0;
		this.unit = Unit.BYTES;
	}

	public SizeValue(String size) {
		this();
		fromString(size);
	}

	public long getBytes() {
		return (long) Math.floor(value * Math.pow(1024, unit.ordinal()));
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void setValue(String bytes) {
		try {
			this.value = Double.parseDouble(bytes);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
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

	private Unit getUnitFromString(String unitName) {
		for (Unit u : Unit.values()) {
			if (u.name().equalsIgnoreCase(unitName)) {
				return u;
			}
		}
		return Unit.BYTES;
	}

	@Override
	public String toString() {
		return String.valueOf(value) + " " + unit.toString();
	}
}
