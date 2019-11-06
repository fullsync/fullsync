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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.fullsync.impl.SimplifiedRuleSetDescriptor;

public abstract class RuleSetDescriptor {
	private static final String ELEMENT_NAME = "RuleSetDescriptor"; //$NON-NLS-1$
	private static final String ATTRIBUTE_TYPE = "type"; //$NON-NLS-1$

	public static final RuleSetDescriptor unserialize(Element element) throws DataParseException {
		RuleSetDescriptor desc = null;
		if (null != element) {
			String ruleSetType = element.getAttribute(ATTRIBUTE_TYPE);
			switch (ruleSetType) {
				case SimplifiedRuleSetDescriptor.RULESET_TYPE:
					desc = new SimplifiedRuleSetDescriptor(element);
					break;
			}
		}
		return desc;
	}

	public static final Element serialize(RuleSetDescriptor desc, Document doc) {
		Element elem = doc.createElement(ELEMENT_NAME);
		elem.setAttribute(ATTRIBUTE_TYPE, desc.getType());
		Element ruleDescriptorElement = desc.serializeDescriptor(doc);
		elem.appendChild(ruleDescriptorElement);
		return elem;
	}

	public abstract RuleSet createRuleSet();

	public abstract String getType();

	public abstract Element serializeDescriptor(Document document);
}
