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
	
	public void fromString(String value) {
		try {
			Date date = dateFormat.parse(value);
			millis = date.getTime();
		} catch (ParseException e) {
			this.millis = 0;			
		}
	}

	public String toString() {
		return dateFormat.format(new Date(millis));
	}
	
	public boolean equals(long cmp) {
		Date compDate = new Date(cmp);
		Date date = new Date(this.millis);
		return (date.getYear() == compDate.getYear()) && 
		(date.getMonth() == compDate.getMonth()) && 
		(date.getDay() == compDate.getDay());
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
