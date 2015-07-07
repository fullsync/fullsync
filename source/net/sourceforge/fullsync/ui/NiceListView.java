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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class NiceListView extends Composite implements Listener {
	private NiceListViewItem selected;

	public NiceListView(Composite parent, int style) {
		super(parent, style);
		addListener(SWT.KeyDown, this);
		initGUI();
	}

	@Override
	public void handleEvent(final Event event) {
		switch (event.type) {
			case SWT.KeyDown:
				Control[] children = getChildren();
				int index = Arrays.asList(children).indexOf(selected);
				switch (event.keyCode) {
					case SWT.ARROW_UP:
						if (index > 0) {
							index -= 1;
						}
						break;
					case SWT.ARROW_DOWN:
						if (index + 1 < children.length) {
							index += 1;
						}
						break;
					case SWT.HOME:
						if (children.length > 0) {
							index = 0;
						}
						break;
					case SWT.END:
						if (children.length > 0) {
							index = children.length -1;
						}
						break;
					default:
						index = -1;
						break;
				}
				if (index > -1) {
					setSelected((NiceListViewItem) children[index]);
					Composite parent = this.getParent();
					if (parent instanceof ScrolledComposite) {
						ScrolledComposite sc = (ScrolledComposite)parent;
						sc.showControl(children[index]);
					}
				}
				break;
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
