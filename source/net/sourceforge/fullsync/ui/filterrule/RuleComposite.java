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
