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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.fullsync.DataParseException;

public class DateValue implements OperandValue {
	// TODO format for UI different form the one used to serialize.
	// The UI format should depend on the locale or should be chosen by the user
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private final long millis;

	public DateValue(Date date) {
		this.millis = date.getTime();
	}

	public DateValue(String date) throws DataParseException {
		try {
			var d = dateFormat.parse(date);
			millis = d.getTime();
		}
		catch (ParseException ex) {
			throw new DataParseException(String.format("'%s' is not a valid date", date), ex);
		}
	}

	public Date getDate() {
		return new Date(millis);
	}

	public long getTime() {
		return millis;
	}

	@Override
	public String toString() {
		return dateFormat.format(new Date(millis));
	}

	public boolean isEqualTo(long cmp) {
		var compDate = new Date(cmp);
		var date = new Date(this.millis);
		return (date.getYear() == compDate.getYear()) && (date.getMonth() == compDate.getMonth()) && (date.getDay() == compDate.getDay());
	}

	public boolean isBefore(long cmp) {
		var compDate = new Date(cmp);
		var date = new Date(this.millis);
		if (date.getYear() < compDate.getYear()) {
			return true;
		}
		else if (date.getYear() == compDate.getYear()) {
			if (date.getMonth() < compDate.getMonth()) {
				return true;
			}
			else if (date.getMonth() == compDate.getMonth()) {
				return date.getDay() < compDate.getDay();
			}
		}
		return false;
	}

	public boolean isAfter(long cmp) {
		var compDate = new Date(cmp);
		var date = new Date(this.millis);
		if (date.getYear() > compDate.getYear()) {
			return true;
		}
		else if (date.getYear() == compDate.getYear()) {
			if (date.getMonth() > compDate.getMonth()) {
				return true;
			}
			else if (date.getMonth() == compDate.getMonth()) {
				return date.getDay() > compDate.getDay();
			}
		}
		return false;
	}
}
