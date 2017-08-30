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
package net.sourceforge.fullsync.rules.filefilter;

import java.util.Arrays;

import net.sourceforge.fullsync.DataParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;
import net.sourceforge.fullsync.rules.filefilter.values.DateValue;
import net.sourceforge.fullsync.rules.filefilter.values.FilterValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;
import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;

public class FileFilterManager {

	public Element serializeFileFilter(FileFilter fileFilter, Document document, String elementName, String ruleElementName) {
		Element filterElement = document.createElement(elementName);

		filterElement.setAttribute("matchtype", String.valueOf(fileFilter.getMatchType()));
		filterElement.setAttribute("filtertype", String.valueOf(fileFilter.getFilterType()));
		filterElement.setAttribute("appliestodir", String.valueOf(fileFilter.appliesToDirectories()));
		Arrays.stream(fileFilter.getFileFiltersRules())
			.map(rule -> serializeRule(rule, document, ruleElementName))
			.forEachOrdered(filterElement::appendChild);
		return filterElement;
	}

	public Element serializeRule(FileFilterRule fileFilterRule, Document document, String elementName) {
		Element ruleElement = document.createElement(elementName);

		String ruleType = (null != fileFilterRule) ? fileFilterRule.getRuleType() : null;

		ruleElement.setAttribute("ruletype", ruleType);
		serializeRuleAttributes(fileFilterRule, ruleElement);

		return ruleElement;
	}

	public FileFilter unserializeFileFilter(Element fileFilterElement, String ruleElementName) throws DataParseException {
		FileFilter fileFilter = new FileFilter();
		int matchType = 0;

		try {
			matchType = Integer.parseInt(fileFilterElement.getAttribute("matchtype"));
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		fileFilter.setMatchType(matchType);

		int filterType = 0;
		try {
			filterType = Integer.parseInt(fileFilterElement.getAttribute("filtertype"));
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		fileFilter.setFilterType(filterType);

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
			Document doc = ruleElement.getOwnerDocument();
			Element subfilterElement = serializeFileFilter(subfilter, doc, "NestedFileFilter", "NestedFileFilterRule");
			ruleElement.appendChild(subfilterElement);
		}
	}

	public FileFilterRule unserializeFileFilterRule(Element fileFilterRuleElement) throws DataParseException {
		FileFilterRule rule = null;
		String ruleType = fileFilterRuleElement.getAttribute("ruletype");

		if (ruleType.equals(FileNameFileFilterRule.TYPE_NAME)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String pattern = fileFilterRuleElement.getAttribute("pattern");
			rule = new FileNameFileFilterRule(new TextValue(pattern), op);
		}

		if (ruleType.equals(FilePathFileFilterRule.TYPE_NAME)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String pattern = fileFilterRuleElement.getAttribute("pattern");
			rule = new FilePathFileFilterRule(new TextValue(pattern), op);
		}

		if (ruleType.equals(FileTypeFileFilterRule.TYPE_NAME)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String type = fileFilterRuleElement.getAttribute("type");
			rule = new FileTypeFileFilterRule(new TypeValue(type), op);
		}

		if (ruleType.equals(FileSizeFileFilterRule.TYPE_NAME)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String size = fileFilterRuleElement.getAttribute("size");
			rule = new FileSizeFileFilterRule(new SizeValue(size), op);
		}

		if (ruleType.equals(FileModificationDateFileFilterRule.TYPE_NAME)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String date = fileFilterRuleElement.getAttribute("modificationdate");
			rule = new FileModificationDateFileFilterRule(new DateValue(date), op);
		}

		if (ruleType.equals(FileAgeFileFilterRule.TYPE_NAME)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute("op"));
			String age = fileFilterRuleElement.getAttribute("age");
			rule = new FileAgeFileFilterRule(new AgeValue(age), op);
		}

		if (ruleType.equals(SubfilterFileFilerRule.TYPE_NAME)) {
			NodeList filterList = fileFilterRuleElement.getElementsByTagName("NestedFileFilter");
			Element subfileFilerElement = (Element) filterList.item(0);
			FileFilter fileFiler = unserializeFileFilter(subfileFilerElement, "NestedFileFilterRule");
			rule = new SubfilterFileFilerRule(fileFiler);
		}

		return rule;
	}

	public FileFilterRule createFileFilterRule(String ruleType, int op, OperandValue value) throws DataParseException {
		FileFilterRule rule = null;

		if (ruleType.equals(FileNameFileFilterRule.TYPE_NAME)) {
			TextValue textValue = (TextValue) value;
			rule = new FileNameFileFilterRule(textValue, op);
		}

		if (ruleType.equals(FilePathFileFilterRule.TYPE_NAME)) {
			TextValue textValue = (TextValue) value;
			rule = new FilePathFileFilterRule(textValue, op);
		}

		if (ruleType.equals(FileTypeFileFilterRule.TYPE_NAME)) {
			TypeValue fileTypeValue = (TypeValue) value;
			rule = new FileTypeFileFilterRule(fileTypeValue, op);
		}

		if (ruleType.equals(FileSizeFileFilterRule.TYPE_NAME)) {
			SizeValue size = (SizeValue) value;
			rule = new FileSizeFileFilterRule(size, op);
		}

		if (ruleType.equals(FileModificationDateFileFilterRule.TYPE_NAME)) {
			DateValue date = (DateValue) value;
			rule = new FileModificationDateFileFilterRule(date, op);
		}

		if (ruleType.equals(FileAgeFileFilterRule.TYPE_NAME)) {
			AgeValue age = (AgeValue) value;
			rule = new FileAgeFileFilterRule(age, op);
		}

		if (ruleType.equals(SubfilterFileFilerRule.TYPE_NAME)) {
			FilterValue filterValue = (FilterValue) value;
			rule = new SubfilterFileFilerRule(filterValue.getValue());
		}

		return rule;
	}

	public String[] getOperatorsForRuleType(String ruleType) {
		if (ruleType.equals(FileNameFileFilterRule.TYPE_NAME)) {
			return FileNameFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FilePathFileFilterRule.TYPE_NAME)) {
			return FilePathFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FileTypeFileFilterRule.TYPE_NAME)) {
			return FileTypeFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FileSizeFileFilterRule.TYPE_NAME)) {
			return FileSizeFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FileModificationDateFileFilterRule.TYPE_NAME)) {
			return FileModificationDateFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(FileAgeFileFilterRule.TYPE_NAME)) {
			return FileAgeFileFilterRule.getAllOperators();
		}

		if (ruleType.equals(SubfilterFileFilerRule.TYPE_NAME)) {
			return new String[0];
		}

		return new String[] { "N/A" };
	}
}
