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
package net.sourceforge.fullsync.ui;

import java.util.Arrays;

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class NiceListView extends Composite implements Listener {
	private NiceListViewItem selected;

	public NiceListView(Composite parent, int style) {
		super(parent, style);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				try {
					Control[] children = getChildren();
					int index = Arrays.asList(children).indexOf(selected);
					if (e.keyCode == SWT.ARROW_UP) {
						setSelected((NiceListViewItem) children[index - 1]);
					}
					else if (e.keyCode == SWT.ARROW_DOWN) {
						setSelected((NiceListViewItem) children[index + 1]);
					}
				}
				catch (ClassCastException ex) {
					ExceptionHandler.reportException(ex);
				}
			}
		});
		initGUI();
	}

	@Override
	public void handleEvent(final Event event) {
		switch (event.type) {
			case SWT.KeyDown:
				switch (event.keyCode) {
					case SWT.ARROW_UP:
					case SWT.ARROW_DOWN:
						Control[] children = getChildren();
						int index = Arrays.asList(children).indexOf(selected);
						if ((event.keyCode == SWT.ARROW_UP) && (index > 0)) {
							setSelected((NiceListViewItem) children[index - 1]);
						}
						else if ((event.keyCode == SWT.ARROW_DOWN) && (index + 1 < children.length)) {
							setSelected((NiceListViewItem) children[index + 1]);
						}
						break;
					default:
						break;
				}
			default:
				break;
		}
	}

	private void initGUI() {
		try {
			GridLayout thisLayout = new GridLayout();
			thisLayout.horizontalSpacing = 2;
			thisLayout.verticalSpacing = 0;
			this.setLayout(thisLayout);
			this.setBackground(UISettings.COLOR_WHITE);
			this.layout();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	public Composite getSelectedContent() {
		if (selected == null) {
			return null;
		}
		return selected.getContent();
	}

	public void setSelected(NiceListViewItem item) {
		if (item == selected) {
			selected.forceFocus();
			return;
		}
		Control[] children = this.getChildren();
		for (Control element : children) {
			NiceListViewItem a = (NiceListViewItem) element;
			a.setSelected(false);
		}
		item.setSelected(true);
		selected = item;
		this.setSize(this.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		this.layout();
	}

	@Override
	public boolean setFocus() {
		if (selected != null) {
			selected.forceFocus();
			return true;
		}
		Control[] cs = getChildren();
		if (cs.length > 0) {
			cs[0].forceFocus();
			return true;
		}
		return false;
	}

	public void clear() {
		Control[] children = this.getChildren();
		for (Control element : children) {
			element.dispose();
		}
	}
}
