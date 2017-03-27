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
package net.sourceforge.fullsync;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;

public abstract class RuleSetDescriptor implements Serializable {

	private static final long serialVersionUID = 2L;

	private static final String ELEMENT_NAME = "RuleSetDescriptor";

	private static Map<String, Class<? extends RuleSetDescriptor>> descriptorRegister;

	static {
		descriptorRegister = new HashMap<>(2);
		descriptorRegister.put(SimplyfiedRuleSetDescriptor.RULESET_TYPE, SimplyfiedRuleSetDescriptor.class);
	}

	public abstract RuleSet createRuleSet();

	public abstract String getType();

	public abstract Element serializeDescriptor(Document document);

	protected abstract void unserializeDescriptor(Element element);

	public static final RuleSetDescriptor unserialize(Element element) {
		RuleSetDescriptor desc = null;
		if (null != element) {
			String ruleSetType = element.getAttribute("type");
			Class<? extends RuleSetDescriptor> ruleSetDesctiptorClass = descriptorRegister.get(ruleSetType);

			if (null != ruleSetDesctiptorClass) {
				try {
					desc = ruleSetDesctiptorClass.newInstance();
					desc.unserializeDescriptor(element);
				}
				catch (InstantiationException | IllegalAccessException e) {
					ExceptionHandler.reportException(e);
				}
			}
		}
		return desc;
	}

	public static final Element serialize(RuleSetDescriptor desc, Document doc) {
		Element elem = doc.createElement(ELEMENT_NAME);
		elem.setAttribute("type", desc.getType());
		Element ruleDescriptorElement = desc.serializeDescriptor(doc);
		elem.appendChild(ruleDescriptorElement);
		return elem;
	}
}
