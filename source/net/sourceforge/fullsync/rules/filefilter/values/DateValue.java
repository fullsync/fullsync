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
 * Created on Jun 6, 2005
 */
package net.sourceforge.fullsync.rules.filefilter.values;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Michele Aiello
 */
public class DateValue implements OperandValue {

	private static final long serialVersionUID = 2L;

	// TODO format for UI different form the one used to serialize.
	// The UI format should depend on the locale or should be choosen by the user
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private long millis;

	public DateValue() {
		this.millis = 0;
	}

	public DateValue(long millis) {
		this.millis = millis;
	}

	public DateValue(Date date) {
		this.millis = date.getTime();
	}

	public DateValue(String date) {
		fromString(date);
	}

	public void setDate(Date date) {
		this.millis = date.getTime();
	}

	public Date getDate() {
		return new Date(millis);
	}

	public void setTime(long millis) {
		this.millis = millis;
	}

	public long getTime() {
		return millis;
	}

	@Override
	public void fromString(String value) {
		try {
			Date date = dateFormat.parse(value);
			millis = date.getTime();
		}
		catch (ParseException e) {
			this.millis = 0;
		}
	}

	@Override
	public String toString() {
		return dateFormat.format(new Date(millis));
	}

	public boolean equals(long cmp) {
		Date compDate = new Date(cmp);
		Date date = new Date(this.millis);
		return (date.getYear() == compDate.getYear()) && (date.getMonth() == compDate.getMonth()) && (date.getDay() == compDate.getDay());
	}

	public boolean isBefore(long cmp) {
		Date compDate = new Date(cmp);
		Date date = new Date(this.millis);
		if ((date.getYear() < compDate.getYear())) {
			return true;
		}
		else if ((date.getYear() == compDate.getYear())) {
			if ((date.getMonth() < compDate.getMonth())) {
				return true;
			}
			else if ((date.getMonth() == compDate.getMonth())) {
				if ((date.getDay() < compDate.getDay())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isAfter(long cmp) {
		Date compDate = new Date(cmp);
		Date date = new Date(this.millis);
		if ((date.getYear() > compDate.getYear())) {
			return true;
		}
		else if ((date.getYear() == compDate.getYear())) {
			if ((date.getMonth() > compDate.getMonth())) {
				return true;
			}
			else if ((date.getMonth() == compDate.getMonth())) {
				if ((date.getDay() > compDate.getDay())) {
					return true;
				}
			}
		}
		return false;
	}

}
