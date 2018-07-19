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
package net.sourceforge.fullsync.schedule;

import java.util.Calendar;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import net.sourceforge.fullsync.DataParseException;

public class CrontabSchedule extends Schedule {
	public static final String SCHEDULE_TYPE = "crontab"; //$NON-NLS-1$
	private static final String ATTRIBUTE_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTRIBUTE_PATTERN = "pattern"; //$NON-NLS-1$

	private final String origPattern;
	private final CrontabPart.Instance minutes;
	private final CrontabPart.Instance hours;
	private final CrontabPart.Instance daysOfMonth;
	private final CrontabPart.Instance months;
	private final CrontabPart.Instance daysOfWeek;

	private static String getPatternFromElement(Element element) {
		String pattern = "* * * * *"; //$NON-NLS-1$
		if (element.hasAttribute(ATTRIBUTE_PATTERN)) {
			pattern = element.getAttribute(ATTRIBUTE_PATTERN);
		}
		return pattern;
	}

	public CrontabSchedule(final Element element) throws DataParseException {
		this(getPatternFromElement(element));
	}

	@Override
	public Element serialize(final Element element) {
		element.setAttribute(ATTRIBUTE_TYPE, SCHEDULE_TYPE);
		element.setAttribute(ATTRIBUTE_PATTERN, getPattern());
		return element;
	}

	public CrontabSchedule() throws DataParseException {
		this("* * * * *"); //$NON-NLS-1$
	}

	/**
	 * Reads a crontab schedule as specified in the crontab man document:
	 *
	 * The time and date fields are:
	 * field allowed values
	 * ----- --------------
	 * minute 0-59
	 * hour 0-23
	 * day of month 1-31
	 * month 1-12 (or names, see below)
	 * day of week 0-7 (0 or 7 is Sun, or use names)
	 * A field may be an asterisk (*), which always stands for ``first-last''.
	 * Ranges of numbers are allowed. Ranges are two numbers separated with a
	 * hyphen. The specified range is inclusive. For example, 8-11 for an
	 * 'hours' entry specifies execution at hours 8, 9, 10 and 11.
	 *
	 * Lists are allowed. A list is a set of numbers (or ranges) separated by
	 * commas. Examples: '1,2,5,9', '0-4,8-12'.
	 *
	 * Step values can be used in conjunction with ranges. Following a range
	 * with '/<number>' specifies skips of the number's value through the
	 * range. For example, '0-23/2' can be used in the hours field to spec-
	 * ify command execution every other hour (the alternative in the V7 stan-
	 * dard is '0,2,4,6,8,10,12,14,16,18,20,22'). Steps are also permitted
	 * after an asterisk, so if you want to say 'every two hours', just use
	 * '* /2'.
	 **/
	public CrontabSchedule(String pattern) throws DataParseException {
		origPattern = pattern;

		StringTokenizer tokenizer = new StringTokenizer(pattern);
		minutes = CrontabPart.MINUTES.createInstance(tokenizer.nextToken());
		hours = CrontabPart.HOURS.createInstance(tokenizer.nextToken());
		daysOfMonth = CrontabPart.DAYSOFMONTH.createInstance(tokenizer.nextToken());
		months = CrontabPart.MONTHS.createInstance(tokenizer.nextToken());
		daysOfWeek = CrontabPart.DAYSOFWEEK.createInstance(tokenizer.nextToken());

		if (daysOfWeek.bArray[8]) {
			daysOfWeek.bArray[1] = true;
		}
	}

	public CrontabSchedule(CrontabPart.Instance minutes, CrontabPart.Instance hours, CrontabPart.Instance daysOfMonth,
		CrontabPart.Instance months, CrontabPart.Instance daysOfWeek) {
		this.minutes = minutes;
		this.hours = hours;
		this.daysOfMonth = daysOfMonth;
		this.months = months;
		this.daysOfWeek = daysOfWeek;

		if (daysOfWeek.bArray[8]) {
			daysOfWeek.bArray[1] = true;
		}

		StringBuilder buff = new StringBuilder();
		buff.append(minutes.pattern).append(' ');
		buff.append(hours.pattern).append(' ');
		buff.append(daysOfMonth.pattern).append(' ');
		buff.append(months.pattern).append(' ');
		buff.append(daysOfWeek.pattern);
		origPattern = buff.toString();
	}

	public String getPattern() {
		// TODO this should be generated
		return origPattern;
	}

	@Override
	public long getNextOccurrence(long lastOccurence, long now) {
		if (now == lastOccurence) {
			now += 1000;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(now);

		// TODO if we have a trigger at minute 0, and hours changes,
		// min will go to 1 and cycle at least once
		gotoNextOrStay(months.bArray, cal, Calendar.MONTH);

		if (months.all && daysOfMonth.all && !daysOfWeek.all) {
			gotoNextOrStay(daysOfWeek.bArray, cal, Calendar.DAY_OF_WEEK);
		}
		else {
			gotoNextOrStay(daysOfMonth.bArray, cal, Calendar.DAY_OF_MONTH);
			// TODO currently we miss out the doublecase
			// !allDaysOfWeek + !allDaysOfMonth
		}

		gotoNextOrStay(hours.bArray, cal, Calendar.HOUR_OF_DAY);
		gotoNextOrStay(minutes.bArray, cal, Calendar.MINUTE);
		if ((cal.get(Calendar.SECOND) != 0) || (cal.get(Calendar.MILLISECOND) != 0)) {
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			gotoNext(minutes.bArray, cal, Calendar.MINUTE);
		}
		return cal.getTimeInMillis();
	}

	private void gotoNextOrStay(boolean[] bArray, Calendar cal, int field) {
		if (!bArray[cal.get(field)]) {
			gotoNext(bArray, cal, field);
		}
	}

	private void gotoNext(boolean[] bArray, Calendar cal, int field) {
		// FIXME we assume that there is a true in the array,
		// but we should avoid a deadloop anyways.

		int orig = cal.get(field);
		int now = orig + 1;
		int max = cal.getActualMaximum(field);
		int min = cal.getActualMinimum(field);

		while ((now > max) || !bArray[now]) {
			now++;
			if (now > max) {
				switch (field) {
					case Calendar.MONTH:
						cal.add(Calendar.YEAR, 1);
						break;
					case Calendar.DAY_OF_MONTH:
						gotoNext(months.bArray, cal, Calendar.MONTH);
						break;
					case Calendar.DAY_OF_WEEK:
						cal.add(Calendar.DAY_OF_MONTH, now - orig);
						// TODO we ignore a formal gotoNext(month)
						// as dayOfWeek is only available if all
						// months are allowed
						orig = now;
						break;
					case Calendar.HOUR_OF_DAY:
						if (months.all && daysOfMonth.all && !daysOfWeek.all) {
							gotoNext(daysOfWeek.bArray, cal, Calendar.DAY_OF_WEEK);
						}
						else if (!daysOfMonth.all) {
							gotoNext(daysOfMonth.bArray, cal, Calendar.DAY_OF_MONTH);
						}
						else {
							gotoNext(daysOfMonth.bArray, cal, Calendar.DAY_OF_MONTH);
						}
						// TODO currently we miss out the doublecase
						// !allDaysOfWeek + !allDaysOfMonth
						break;
					case Calendar.MINUTE:
						gotoNext(hours.bArray, cal, Calendar.HOUR_OF_DAY);
						break;
				}
				now = min;
			}
		}

		if (now != orig) {
			switch (field) {
				case Calendar.MONTH:
					cal.set(Calendar.DAY_OF_MONTH, 1);
				case Calendar.DAY_OF_MONTH:
				case Calendar.DAY_OF_WEEK:
					cal.set(Calendar.HOUR_OF_DAY, 0);
				case Calendar.HOUR_OF_DAY:
					cal.set(Calendar.MINUTE, 0);
				case Calendar.MINUTE:
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
			}
		}
		cal.set(field, now);
	}

	public CrontabPart.Instance[] getParts() {
		return new CrontabPart.Instance[] { minutes, hours, daysOfMonth, months, daysOfWeek };
	}

	@Override
	public String toString() {
		return "Crontab: " + origPattern;
	}
}
