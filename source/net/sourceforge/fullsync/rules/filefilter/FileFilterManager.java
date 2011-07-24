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
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;
import net.sourceforge.fullsync.rules.filefilter.values.DateValue;
import net.sourceforge.fullsync.rules.filefilter.values.FilterValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;
import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Michele Aiello
 */
public class FileFilterManager {

	public Element serializeFileFilter(FileFilter fileFilter, Document document, String elementName, String ruleElementName) {
		Element filterElement = document.createElement(elementName);

		filterElement.setAttribute("matchtype", String.valueOf(fileFilter.getMatchType()));
		filterElement.setAttribute("filtertype", String.valueOf(fileFilter.getFilterType()));
		filterElement.setAttribute("appliestodir", String.valueOf(fileFilter.appliesToDirectories()));

		FileFilterRule[] rules = fileFilter.getFileFiltersRules();
		for (FileFilterRule rule : rules) {
			Element ruleElement = serializeRule(rule, document, ruleElementName);
			filterElement.appendChild(ruleElement);
		}

		return filterElement;
	}

	public Element serializeRule(FileFilterRule fileFilterRule, Document document, String elementName) {
		Element ruleElement = document.createElement(elementName);
		String ruleType = getRuleType(fileFilterRule);

		ruleElement.setAttribute("ruletype", ruleType);
		serializeRuleAttributes(fileFilterRule, ruleElement);

		return ruleElement;
	}

	public FileFilter unserializeFileFilter(Element fileFilterElement, String ruleElementName) {
		FileFilter fileFilter = new FileFilter();
		int match_type = 0;

		try {
			match_type = Integer.parseInt(fileFilterElement.getAttribute("matchtype"));
		}
		catch (NumberFormatException e) {
		}
		fileFilter.setMatchType(match_type);

		int filter_type = 0;
		try {
			filter_type = Integer.parseInt(fileFilterElement.getAttribute("filtertype"));
		}
		catch (NumberFormatException e) {
		}
		fileFilter.setFilterType(filter_type);

		boolean applies = Boolean.valueOf(fileFilterElement.getAttribute("appliestodir")).booleanValue();
		fileFilter.setAppliesToDirectories(applies);

		NodeList ruleList = fileFilterElement.getElementsByTagName(ruleElementName);
		int numOfRules = ruleList.getLength();
		FileFilterRule[] rules = new FileFilterRule[numOfRules];

		for (int i = 0; i < rules.length; i++) {
			rules[i] = unserializeFileFilterRule((Element) ruleList.item(i));
		}

		fileFilter.setFileFilterRules(rules);

		return fileFilter;
	}

	private void serializeRuleAttributes(FileFilterRule fileFilterRule, Element ruleElement) {
		if (fileFilterRule instanceof FileNameFileFilterRule) {
			FileNameFileFilterRule rule = (FileNameFileFilterRule) fileFilterRule;
			ruleElement.setAttribute("op", String.valueOf(rule.getOperator()));
			ruleElement.setAttribute("pattern", rule.getValue().toString());
		}

		if (fileFilterRule instanceof FilePathFileFilterRule) {
			FilePathFileFilterRule rule = (FilePathFileFilterRule) fileFilterRule;
			ruleElement.setAttribute("op", String.valueOf(rule.getOperator()));
			ruleElement.setAttribute("pattern", rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileTypeFileFilterRule) {
			FileTypeFileFilterRule rule = (FileTypeFileFilterRule) fileFilterRule;
			ruleElement.setAttribute("op", String.valueOf(rule.getOperator()));
			ruleElement.setAttribute("type", rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileSizeFileFilterRule) {
			FileSizeFileFilterRule rule = (FileSizeFileFilterRule) fileFilterRule;
			ruleElement.setAttribute("op", String.valueOf(rule.getOperator()));
			ruleElement.setAttribute("size", rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileModificationDateFileFilterRule) {
			FileModificationDateFileFilterRule rule = (FileModificationDateFileFilterRule) fileFilterRule;
			ruleElement.setAttribute("op", String.valueOf(rule.getOperator()));
			ruleElement.setAttribute("modificationdate", rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileAgeFileFilterRule) {
			FileAgeFileFilterRule rule = (FileAgeFileFilterRule) fileFilterRule;
			ruleElement.setAttribute("op", String.valueOf(rule.getOperator()));
			ruleElement.setAttribute("age", rule.getValue().toString());
		}

		if (fileFilterRule instanceof SubfilterFileFilerRule) {
			SubfilterFileFilerRule rule = (SubfilterFileFilerRule) fileFilterRule;
			FileFilter subfilter = ((FilterValue) rule.getValue()).getValue();
			Element subfilterElement = serializeFileFilter(subfilter, ruleElement.getOwnerDocument(), "NestedFileFilter",
					"NestedFileFilterRule");
			ruleElement.appendChild(subfilterElement);
		}
	}

	public FileFilterRule unserializeFileFilterRule(Element fileFilterRuleElement) {
		FileFilterRule rule = null;
		String ruleType = fileFilterRuleElement.getAttribute("ruletype");

		if (ruleType.equals(FileNameFileFilterRule.typeName)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String pattern = fileFilterRuleElement.getAttribute("pattern");
			rule = new FileNameFileFilterRule(new TextValue(pattern), op);
		}

		if (ruleType.equals(FilePathFileFilterRule.typeName)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String pattern = fileFilterRuleElement.getAttribute("pattern");
			rule = new FilePathFileFilterRule(new TextValue(pattern), op);
		}

		if (ruleType.equals(FileTypeFileFilterRule.typeName)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String type = fileFilterRuleElement.getAttribute("type");
			rule = new FileTypeFileFilterRule(new TypeValue(type), op);
		}

		if (ruleType.equals(FileSizeFileFilterRule.typeName)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String size = fileFilterRuleElement.getAttribute("size");
			rule = new FileSizeFileFilterRule(new SizeValue(size), op);
		}

		if (ruleType.equals(FileModificationDateFileFilterRule.typeName)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String date = fileFilterRuleElement.getAttribute("modificationdate");
			rule = new FileModificationDateFileFilterRule(new DateValue(date), op);
		}

		if (ruleType.equals(FileAgeFileFilterRule.typeName)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String age = fileFilterRuleElement.getAttribute("age");
			rule = new FileAgeFileFilterRule(new AgeValue(age), op);
		}

		if (ruleType.equals(SubfilterFileFilerRule.typeName)) {
			NodeList filterList = fileFilterRuleElement.getElementsByTagName("NestedFileFilter");
			Element subfileFilerElement = (Element) filterList.item(0);
			FileFilter fileFiler = unserializeFileFilter(subfileFilerElement, "NestedFileFilterRule");
			rule = new SubfilterFileFilerRule(fileFiler);
		}

		return rule;
	}

	public FileFilterRule createFileFilterRule(String ruleType, int op, OperandValue value) {
		FileFilterRule rule = null;

		if (ruleType.equals(FileNameFileFilterRule.typeName)) {
			TextValue textValue = (TextValue) value;
			rule = new FileNameFileFilterRule(textValue, op);
		}

		if (ruleType.equals(FilePathFileFilterRule.typeName)) {
			TextValue textValue = (TextValue) value;
			rule = new FilePathFileFilterRule(textValue, op);
		}

		if (ruleType.equals(FileTypeFileFilterRule.typeName)) {
			TypeValue fileTypeValue = (TypeValue) value;
			rule = new FileTypeFileFilterRule(fileTypeValue, op);
		}

		if (ruleType.equals(FileSizeFileFilterRule.typeName)) {
			SizeValue size = (SizeValue) value;
			rule = new FileSizeFileFilterRule(size, op);
		}

		if (ruleType.equals(FileModificationDateFileFilterRule.typeName)) {
			DateValue date = (DateValue) value;
			rule = new FileModificationDateFileFilterRule(date, op);
		}

		if (ruleType.equals(FileAgeFileFilterRule.typeName)) {
			AgeValue age = (AgeValue) value;
			rule = new FileAgeFileFilterRule(age, op);
		}

		if (ruleType.equals(SubfilterFileFilerRule.typeName)) {
			FilterValue filterValue = (FilterValue) value;
			rule = new SubfilterFileFilerRule(filterValue.getValue());
		}

		return rule;
	}

	public String[] getOperatorsForRuleType(String ruleType) {
		if (ruleType.equals(FileNameFileFilterRule.typeName)) {
			return FileNameFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FilePathFileFilterRule.typeName)) {
			return FilePathFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FileTypeFileFilterRule.typeName)) {
			return FileTypeFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FileSizeFileFilterRule.typeName)) {
			return FileSizeFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FileModificationDateFileFilterRule.typeName)) {
			return FileModificationDateFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FileAgeFileFilterRule.typeName)) {
			return FileAgeFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(SubfilterFileFilerRule.typeName)) {
			return new String[0];
		}

		return new String[] { "N/A" };
	}

	private String getRuleType(FileFilterRule fileFilterRule) {
		if (fileFilterRule instanceof FileNameFileFilterRule) {
			return FileNameFileFilterRule.typeName;
		}

		if (fileFilterRule instanceof FilePathFileFilterRule) {
			return FilePathFileFilterRule.typeName;
		}

		if (fileFilterRule instanceof FileTypeFileFilterRule) {
			return FileTypeFileFilterRule.typeName;
		}

		if (fileFilterRule instanceof FileSizeFileFilterRule) {
			return FileSizeFileFilterRule.typeName;
		}

		if (fileFilterRule instanceof FileModificationDateFileFilterRule) {
			return FileModificationDateFileFilterRule.typeName;
		}

		if (fileFilterRule instanceof FileAgeFileFilterRule) {
			return FileAgeFileFilterRule.typeName;
		}

		if (fileFilterRule instanceof SubfilterFileFilerRule) {
			return SubfilterFileFilerRule.typeName;
		}

		return null;
	}

}
