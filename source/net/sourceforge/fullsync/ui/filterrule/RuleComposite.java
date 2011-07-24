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
package net.sourceforge.fullsync.ui.filterrule;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;

/**
 * @author Michele Aiello
 */
public abstract class RuleComposite extends Composite {

	private Vector listeners = new Vector();

	protected RuleComposite(Composite parent, int style) {
		super(parent, style);
	}

	public void addValueChangedListener(ValueChangedListener listener) {
		if (listeners.contains(listener)) {
			return;
		}
		listeners.add(listener);
	}

	public void removeValueChangedListener(ValueChangedListener listener) {
		listeners.remove(listener);
	}

	protected void valueChanged(ValueChangedEvent evt) {
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			ValueChangedListener listener = (ValueChangedListener) it.next();
			listener.onValueChanged(evt);
		}
	}

}
