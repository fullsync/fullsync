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
import java.util.stream.Stream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;
import net.sourceforge.fullsync.rules.filefilter.values.DateValue;
import net.sourceforge.fullsync.rules.filefilter.values.FilterValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;
import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;

public class FileFilterManager {
	private static final String ELEMENT_NESTED_FILE_FILTER_RULE = "NestedFileFilterRule"; //$NON-NLS-1$
	private static final String ELEMENT_NESTED_FILE_FILTER = "NestedFileFilter"; //$NON-NLS-1$
	private static final String ATTRIBUTE_AGE = "age"; //$NON-NLS-1$
	private static final String ATTRIBUTE_MODIFICATIONDATE = "modificationdate"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SIZE = "size"; //$NON-NLS-1$
	private static final String ATTRIBUTE_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTRIBUTE_PATTERN = "pattern"; //$NON-NLS-1$
	private static final String ATTRIBUTE_OP = "op"; //$NON-NLS-1$
	private static final String ATTRIBUTE_RULETYPE = "ruletype"; //$NON-NLS-1$
	private static final String ATTRIBUTE_APPLIESTODIR = "appliestodir"; //$NON-NLS-1$
	private static final String ATTRIBUTE_FILTERTYPE = "filtertype"; //$NON-NLS-1$
	private static final String ATTRIBUTE_MATCHTYPE = "matchtype"; //$NON-NLS-1$

	public Element serializeFileFilter(FileFilter fileFilter, Document document, String elementName, String ruleElementName) {
		var filterElement = document.createElement(elementName);

		filterElement.setAttribute(ATTRIBUTE_MATCHTYPE, String.valueOf(fileFilter.matchType()));
		filterElement.setAttribute(ATTRIBUTE_FILTERTYPE, String.valueOf(fileFilter.filterType()));
		filterElement.setAttribute(ATTRIBUTE_APPLIESTODIR, String.valueOf(fileFilter.appliesToDirectories()));
		Stream<FileFilterRule> fileFilterStream = Arrays.stream(fileFilter.rules());
		fileFilterStream.map(rule -> serializeRule(rule, document, ruleElementName)).forEachOrdered(filterElement::appendChild);
		return filterElement;
	}

	public Element serializeRule(FileFilterRule fileFilterRule, Document document, String elementName) {
		var ruleElement = document.createElement(elementName);

		var ruleType = null != fileFilterRule ? fileFilterRule.getRuleType() : null;

		ruleElement.setAttribute(ATTRIBUTE_RULETYPE, ruleType);
		serializeRuleAttributes(fileFilterRule, ruleElement);

		return ruleElement;
	}

	public FileFilter unserializeFileFilter(Element fileFilterElement, String ruleElementName) throws DataParseException {
		var matchType = FileFilter.MATCH_ALL;

		try {
			matchType = Integer.parseInt(fileFilterElement.getAttribute(ATTRIBUTE_MATCHTYPE));
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}

		var filterType = FileFilter.INCLUDE;
		try {
			filterType = Integer.parseInt(fileFilterElement.getAttribute(ATTRIBUTE_FILTERTYPE));
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}

		var applies = Boolean.parseBoolean(fileFilterElement.getAttribute(ATTRIBUTE_APPLIESTODIR));

		var ruleList = fileFilterElement.getElementsByTagName(ruleElementName);
		var numOfRules = ruleList.getLength();
		var rules = new FileFilterRule[numOfRules];

		for (var i = 0; i < rules.length; i++) {
			rules[i] = unserializeFileFilterRule((Element) ruleList.item(i));
		}
		return new FileFilter(matchType, filterType, applies, rules);
	}

	private void serializeRuleAttributes(FileFilterRule fileFilterRule, Element ruleElement) {
		if (fileFilterRule instanceof FileNameFileFilterRule rule) {
			ruleElement.setAttribute(ATTRIBUTE_OP, String.valueOf(rule.getOperator()));
			ruleElement.setAttribute(ATTRIBUTE_PATTERN, rule.getValue().toString());
		}

		if (fileFilterRule instanceof FilePathFileFilterRule rule) {
			ruleElement.setAttribute(ATTRIBUTE_OP, String.valueOf(rule.getOperator()));
			ruleElement.setAttribute(ATTRIBUTE_PATTERN, rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileTypeFileFilterRule rule) {
			ruleElement.setAttribute(ATTRIBUTE_OP, String.valueOf(rule.getOperator()));
			ruleElement.setAttribute(ATTRIBUTE_TYPE, rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileSizeFileFilterRule rule) {
			ruleElement.setAttribute(ATTRIBUTE_OP, String.valueOf(rule.getOperator()));
			ruleElement.setAttribute(ATTRIBUTE_SIZE, rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileModificationDateFileFilterRule rule) {
			ruleElement.setAttribute(ATTRIBUTE_OP, String.valueOf(rule.getOperator()));
			ruleElement.setAttribute(ATTRIBUTE_MODIFICATIONDATE, rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileAgeFileFilterRule rule) {
			ruleElement.setAttribute(ATTRIBUTE_OP, String.valueOf(rule.getOperator()));
			ruleElement.setAttribute(ATTRIBUTE_AGE, rule.getValue().toString());
		}

		if (fileFilterRule instanceof SubfilterFileFilerRule rule) {
			var subfilter = ((FilterValue) rule.getValue()).value();
			var doc = ruleElement.getOwnerDocument();
			var subfilterElement = serializeFileFilter(subfilter, doc, ELEMENT_NESTED_FILE_FILTER, ELEMENT_NESTED_FILE_FILTER_RULE);
			ruleElement.appendChild(subfilterElement);
		}
	}

	public FileFilterRule unserializeFileFilterRule(Element fileFilterRuleElement) throws DataParseException {
		FileFilterRule rule = null;
		var ruleType = fileFilterRuleElement.getAttribute(ATTRIBUTE_RULETYPE);

		if (FileNameFileFilterRule.TYPE_NAME.equals(ruleType)) {
			var op = Integer.parseInt(fileFilterRuleElement.getAttribute(ATTRIBUTE_OP));
			var pattern = fileFilterRuleElement.getAttribute(ATTRIBUTE_PATTERN);
			rule = new FileNameFileFilterRule(new TextValue(pattern), op);
		}

		if (FilePathFileFilterRule.TYPE_NAME.equals(ruleType)) {
			var op = Integer.parseInt(fileFilterRuleElement.getAttribute(ATTRIBUTE_OP));
			var pattern = fileFilterRuleElement.getAttribute(ATTRIBUTE_PATTERN);
			rule = new FilePathFileFilterRule(new TextValue(pattern), op);
		}

		if (FileTypeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			var op = Integer.parseInt(fileFilterRuleElement.getAttribute(ATTRIBUTE_OP));
			var type = fileFilterRuleElement.getAttribute(ATTRIBUTE_TYPE);
			rule = new FileTypeFileFilterRule(new TypeValue(type), op);
		}

		if (FileSizeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			var op = Integer.parseInt(fileFilterRuleElement.getAttribute(ATTRIBUTE_OP));
			var size = fileFilterRuleElement.getAttribute(ATTRIBUTE_SIZE);
			rule = new FileSizeFileFilterRule(new SizeValue(size), op);
		}

		if (FileModificationDateFileFilterRule.TYPE_NAME.equals(ruleType)) {
			var op = Integer.parseInt(fileFilterRuleElement.getAttribute(ATTRIBUTE_OP));
			var date = fileFilterRuleElement.getAttribute(ATTRIBUTE_MODIFICATIONDATE);
			rule = new FileModificationDateFileFilterRule(new DateValue(date), op);
		}

		if (FileAgeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			var op = Integer.parseInt(fileFilterRuleElement.getAttribute(ATTRIBUTE_OP));
			var age = fileFilterRuleElement.getAttribute(ATTRIBUTE_AGE);
			rule = new FileAgeFileFilterRule(new AgeValue(age), op);
		}

		if (SubfilterFileFilerRule.TYPE_NAME.equals(ruleType)) {
			var filterList = fileFilterRuleElement.getElementsByTagName(ELEMENT_NESTED_FILE_FILTER);
			var subfileFilerElement = (Element) filterList.item(0);
			var fileFiler = unserializeFileFilter(subfileFilerElement, ELEMENT_NESTED_FILE_FILTER_RULE);
			rule = new SubfilterFileFilerRule(fileFiler);
		}

		return rule;
	}

	public FileFilterRule createFileFilterRule(String ruleType, int op, OperandValue value) throws DataParseException {
		FileFilterRule rule = null;

		if (FileNameFileFilterRule.TYPE_NAME.equals(ruleType)) {
			var textValue = (TextValue) value;
			rule = new FileNameFileFilterRule(textValue, op);
		}

		if (FilePathFileFilterRule.TYPE_NAME.equals(ruleType)) {
			var textValue = (TextValue) value;
			rule = new FilePathFileFilterRule(textValue, op);
		}

		if (FileTypeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			var fileTypeValue = (TypeValue) value;
			rule = new FileTypeFileFilterRule(fileTypeValue, op);
		}

		if (FileSizeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			var size = (SizeValue) value;
			rule = new FileSizeFileFilterRule(size, op);
		}

		if (FileModificationDateFileFilterRule.TYPE_NAME.equals(ruleType)) {
			var date = (DateValue) value;
			rule = new FileModificationDateFileFilterRule(date, op);
		}

		if (FileAgeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			var age = (AgeValue) value;
			rule = new FileAgeFileFilterRule(age, op);
		}

		if (SubfilterFileFilerRule.TYPE_NAME.equals(ruleType)) {
			var filterValue = (FilterValue) value;
			rule = new SubfilterFileFilerRule(filterValue.value());
		}

		return rule;
	}

	public String[] getOperatorsForRuleType(String ruleType) {
		if (FileNameFileFilterRule.TYPE_NAME.equals(ruleType)) {
			return FileNameFileFilterRule.getAllOperators();
		}

		if (FilePathFileFilterRule.TYPE_NAME.equals(ruleType)) {
			return FilePathFileFilterRule.getAllOperators();
		}

		if (FileTypeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			return FileTypeFileFilterRule.getAllOperators();
		}

		if (FileSizeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			return FileSizeFileFilterRule.getAllOperators();
		}

		if (FileModificationDateFileFilterRule.TYPE_NAME.equals(ruleType)) {
			return FileModificationDateFileFilterRule.getAllOperators();
		}

		if (FileAgeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			return FileAgeFileFilterRule.getAllOperators();
		}

		if (SubfilterFileFilerRule.TYPE_NAME.equals(ruleType)) {
			return new String[0];
		}

		return new String[] {
			"N/A"
		};
	}
}
