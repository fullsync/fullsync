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
 * Created on Nov 5, 2004
 */
package net.sourceforge.fullsync;

import java.io.Serializable;
import java.util.Hashtable;

import net.sourceforge.fullsync.impl.AdvancedRuleSetDescriptor;
import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Michele Aiello
 */
public abstract class RuleSetDescriptor implements Serializable {

	private static final long serialVersionUID = 1;

	private static Hashtable descriptorRegister;

	// TODO [Michele] change this!
	static {
		descriptorRegister = new Hashtable(2);
		descriptorRegister.put("simple", SimplyfiedRuleSetDescriptor.class);
		descriptorRegister.put("advanced", AdvancedRuleSetDescriptor.class);
	}

	public abstract RuleSet createRuleSet();

	public abstract String getType();

	public abstract Element serialize(Document document);

	protected abstract void unserializeDescriptor(Element element);

	public static final RuleSetDescriptor unserialize(Element element) {
		if (element == null) {
			return null;
		}
		String ruleSetType = element.getAttribute("type");
		Class ruleSetDesctiptorClass = (Class) descriptorRegister.get(ruleSetType);

		if (ruleSetDesctiptorClass == null) {
			return null;
		}
		else {
			RuleSetDescriptor desc = null;

			try {
				desc = (RuleSetDescriptor) ruleSetDesctiptorClass.newInstance();
			}
			catch (InstantiationException e) {
				ExceptionHandler.reportException(e);
				return null;
			}
			catch (IllegalAccessException e) {
				ExceptionHandler.reportException(e);
				return null;
			}

			desc.unserializeDescriptor(element);

			return desc;
		}

	}

}
