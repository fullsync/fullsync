/*
 * Created on Jun 5, 2005
 */
package net.sourceforge.fullsync.ui.filterrule;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 * @author Michele Aiello
 */
public class SWTCalendarDialog {
    private Shell shell;
    private SWTCalendar swtcal;
    private Display display;

    public SWTCalendarDialog(Display display) {
        this.display = display;
        shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.CLOSE);
        shell.setText("Choose Date...");
        shell.setLayout(new RowLayout());
        swtcal = new SWTCalendar(shell, SWT.NONE | SWTCalendar.RED_WEEKEND);
    }

    public void open() {
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
    }

    public Calendar getCalendar() {
        return swtcal.getCalendar();
    }

    public void setDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        swtcal.setCalendar(calendar);
    }
    
    public void addDateChangedListener(SWTCalendarListener listener) {
        swtcal.addSWTCalendarListener(listener);
    }

}