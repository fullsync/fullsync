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
import org.w3c.dom.NodeList;

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
		Element filterElement = document.createElement(elementName);

		filterElement.setAttribute(ATTRIBUTE_MATCHTYPE, String.valueOf(fileFilter.getMatchType()));
		filterElement.setAttribute(ATTRIBUTE_FILTERTYPE, String.valueOf(fileFilter.getFilterType()));
		filterElement.setAttribute(ATTRIBUTE_APPLIESTODIR, String.valueOf(fileFilter.appliesToDirectories()));
		Stream<FileFilterRule> fileFilterStream = Arrays.stream(fileFilter.getFileFiltersRules());
		fileFilterStream.map(rule -> serializeRule(rule, document, ruleElementName)).forEachOrdered(filterElement::appendChild);
		return filterElement;
	}

	public Element serializeRule(FileFilterRule fileFilterRule, Document document, String elementName) {
		Element ruleElement = document.createElement(elementName);

		String ruleType = (null != fileFilterRule) ? fileFilterRule.getRuleType() : null;

		ruleElement.setAttribute(ATTRIBUTE_RULETYPE, ruleType);
		serializeRuleAttributes(fileFilterRule, ruleElement);

		return ruleElement;
	}

	public FileFilter unserializeFileFilter(Element fileFilterElement, String ruleElementName) throws DataParseException {
		FileFilter fileFilter = new FileFilter();
		int matchType = 0;

		try {
			matchType = Integer.parseInt(fileFilterElement.getAttribute(ATTRIBUTE_MATCHTYPE));
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		fileFilter.setMatchType(matchType);

		int filterType = 0;
		try {
			filterType = Integer.parseInt(fileFilterElement.getAttribute(ATTRIBUTE_FILTERTYPE));
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		fileFilter.setFilterType(filterType);

		boolean applies = Boolean.parseBoolean(fileFilterElement.getAttribute(ATTRIBUTE_APPLIESTODIR));
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
			ruleElement.setAttribute(ATTRIBUTE_OP, String.valueOf(rule.getOperator()));
			ruleElement.setAttribute(ATTRIBUTE_PATTERN, rule.getValue().toString());
		}

		if (fileFilterRule instanceof FilePathFileFilterRule) {
			FilePathFileFilterRule rule = (FilePathFileFilterRule) fileFilterRule;
			ruleElement.setAttribute(ATTRIBUTE_OP, String.valueOf(rule.getOperator()));
			ruleElement.setAttribute(ATTRIBUTE_PATTERN, rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileTypeFileFilterRule) {
			FileTypeFileFilterRule rule = (FileTypeFileFilterRule) fileFilterRule;
			ruleElement.setAttribute(ATTRIBUTE_OP, String.valueOf(rule.getOperator()));
			ruleElement.setAttribute(ATTRIBUTE_TYPE, rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileSizeFileFilterRule) {
			FileSizeFileFilterRule rule = (FileSizeFileFilterRule) fileFilterRule;
			ruleElement.setAttribute(ATTRIBUTE_OP, String.valueOf(rule.getOperator()));
			ruleElement.setAttribute(ATTRIBUTE_SIZE, rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileModificationDateFileFilterRule) {
			FileModificationDateFileFilterRule rule = (FileModificationDateFileFilterRule) fileFilterRule;
			ruleElement.setAttribute(ATTRIBUTE_OP, String.valueOf(rule.getOperator()));
			ruleElement.setAttribute(ATTRIBUTE_MODIFICATIONDATE, rule.getValue().toString());
		}

		if (fileFilterRule instanceof FileAgeFileFilterRule) {
			FileAgeFileFilterRule rule = (FileAgeFileFilterRule) fileFilterRule;
			ruleElement.setAttribute(ATTRIBUTE_OP, String.valueOf(rule.getOperator()));
			ruleElement.setAttribute(ATTRIBUTE_AGE, rule.getValue().toString());
		}

		if (fileFilterRule instanceof SubfilterFileFilerRule) {
			SubfilterFileFilerRule rule = (SubfilterFileFilerRule) fileFilterRule;
			FileFilter subfilter = ((FilterValue) rule.getValue()).getValue();
			Document doc = ruleElement.getOwnerDocument();
			Element subfilterElement = serializeFileFilter(subfilter, doc, ELEMENT_NESTED_FILE_FILTER, ELEMENT_NESTED_FILE_FILTER_RULE);
			ruleElement.appendChild(subfilterElement);
		}
	}

	public FileFilterRule unserializeFileFilterRule(Element fileFilterRuleElement) throws DataParseException {
		FileFilterRule rule = null;
		String ruleType = fileFilterRuleElement.getAttribute(ATTRIBUTE_RULETYPE);

		if (FileNameFileFilterRule.TYPE_NAME.equals(ruleType)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute(ATTRIBUTE_OP));
			String pattern = fileFilterRuleElement.getAttribute(ATTRIBUTE_PATTERN);
			rule = new FileNameFileFilterRule(new TextValue(pattern), op);
		}

		if (FilePathFileFilterRule.TYPE_NAME.equals(ruleType)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute(ATTRIBUTE_OP));
			String pattern = fileFilterRuleElement.getAttribute(ATTRIBUTE_PATTERN);
			rule = new FilePathFileFilterRule(new TextValue(pattern), op);
		}

		if (FileTypeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute(ATTRIBUTE_OP));
			String type = fileFilterRuleElement.getAttribute(ATTRIBUTE_TYPE);
			rule = new FileTypeFileFilterRule(new TypeValue(type), op);
		}

		if (FileSizeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute(ATTRIBUTE_OP));
			String size = fileFilterRuleElement.getAttribute(ATTRIBUTE_SIZE);
			rule = new FileSizeFileFilterRule(new SizeValue(size), op);
		}

		if (FileModificationDateFileFilterRule.TYPE_NAME.equals(ruleType)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute(ATTRIBUTE_OP));
			String date = fileFilterRuleElement.getAttribute(ATTRIBUTE_MODIFICATIONDATE);
			rule = new FileModificationDateFileFilterRule(new DateValue(date), op);
		}

		if (FileAgeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			int op = Integer.parseInt(fileFilterRuleElement.getAttribute(ATTRIBUTE_OP));
			String age = fileFilterRuleElement.getAttribute(ATTRIBUTE_AGE);
			rule = new FileAgeFileFilterRule(new AgeValue(age), op);
		}

		if (SubfilterFileFilerRule.TYPE_NAME.equals(ruleType)) {
			NodeList filterList = fileFilterRuleElement.getElementsByTagName(ELEMENT_NESTED_FILE_FILTER);
			Element subfileFilerElement = (Element) filterList.item(0);
			FileFilter fileFiler = unserializeFileFilter(subfileFilerElement, ELEMENT_NESTED_FILE_FILTER_RULE);
			rule = new SubfilterFileFilerRule(fileFiler);
		}

		return rule;
	}

	public FileFilterRule createFileFilterRule(String ruleType, int op, OperandValue value) throws DataParseException {
		FileFilterRule rule = null;

		if (FileNameFileFilterRule.TYPE_NAME.equals(ruleType)) {
			TextValue textValue = (TextValue) value;
			rule = new FileNameFileFilterRule(textValue, op);
		}

		if (FilePathFileFilterRule.TYPE_NAME.equals(ruleType)) {
			TextValue textValue = (TextValue) value;
			rule = new FilePathFileFilterRule(textValue, op);
		}

		if (FileTypeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			TypeValue fileTypeValue = (TypeValue) value;
			rule = new FileTypeFileFilterRule(fileTypeValue, op);
		}

		if (FileSizeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			SizeValue size = (SizeValue) value;
			rule = new FileSizeFileFilterRule(size, op);
		}

		if (FileModificationDateFileFilterRule.TYPE_NAME.equals(ruleType)) {
			DateValue date = (DateValue) value;
			rule = new FileModificationDateFileFilterRule(date, op);
		}

		if (FileAgeFileFilterRule.TYPE_NAME.equals(ruleType)) {
			AgeValue age = (AgeValue) value;
			rule = new FileAgeFileFilterRule(age, op);
		}

		if (SubfilterFileFilerRule.TYPE_NAME.equals(ruleType)) {
			FilterValue filterValue = (FilterValue) value;
			rule = new SubfilterFileFilerRule(filterValue.getValue());
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

		return new String[] { "N/A" };
	}
}
