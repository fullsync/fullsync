package net.sourceforge.fullsync.ui;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.joda.time.DateTimeZone;

public class TimeZoneCombo {
	private Combo combo;
	private String timeZone;
	private String[] timeZoneIds;
	public TimeZoneCombo(Composite parent) {
		Label label = new Label(parent, SWT.NULL);
		label.setText(Messages.getString("TimeZone"));
		combo = new Combo(parent, SWT.NULL);
		GridData comboData = new GridData();
		comboData.horizontalSpan = 2;
		comboData.horizontalAlignment = SWT.FILL;
		combo.setLayoutData(comboData);
		Set<String> ids = DateTimeZone.getAvailableIDs();
		timeZoneIds = ids.toArray(new String[ids.size()]);
		combo.setItems(timeZoneIds);
		setTimeZone(DateTimeZone.getDefault().getID());
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String tz) {
		timeZone = tz;
		int index = Arrays.binarySearch(timeZoneIds, tz);
		if (index >= 0) {
			combo.setText(timeZoneIds[index]);
		}
	}
}
