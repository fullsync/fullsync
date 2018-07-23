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
package net.sourceforge.fullsync.ui.filterrule;

import javax.inject.Provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.values.FilterValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.ui.FileFilterPage;
import net.sourceforge.fullsync.ui.Messages;

class SubfilterRuleComposite extends RuleComposite {
	private FileFilter value = new FileFilter(FileFilter.MATCH_ALL, FileFilter.INCLUDE, true);
	private Provider<FileFilterPage> fileFilterPageProvider;

	SubfilterRuleComposite(Provider<FileFilterPage> fileFilterPageProvider, Composite parent, final FilterValue initialValue) {
		super(parent);
		this.fileFilterPageProvider = fileFilterPageProvider;
		if (null != initialValue) {
			value = initialValue.getValue();
		}
		render();
	}

	private void render() {
		this.setLayout(new GridLayout(3, true));

		textValue = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData textValueData = new GridData(SWT.FILL, SWT.FILL, true, true);
		textValueData.heightHint = textValue.getLineHeight() * 3;
		textValueData.horizontalSpan = 2;
		textValue.setLayoutData(textValueData);
		if (null != value) {
			textValue.setText(value.toString());
		}
		textValue.setEditable(false);

		Button buttonFilter = new Button(this, SWT.PUSH);
		buttonFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		buttonFilter.setText(Messages.getString("SubfilterRuleComposite.SetFilter")); //$NON-NLS-1$
		buttonFilter.addListener(SWT.Selection, this::onEditSubfilter);
	}

	private void onEditSubfilter(Event e) {
		try {
			FileFilterPage dialog = fileFilterPageProvider.get();
			dialog.setParent(getShell());
			dialog.setFileFilter(value);
			dialog.show();
			FileFilter newfilter = dialog.getFileFilter();
			if (null != newfilter) {
				value = newfilter;
				textValue.setText(value.toString());
				textValue.setToolTipText(value.toString());
			}
		}
		catch (Exception ex) {
			ExceptionHandler.reportException(ex);
		}
	}

	@Override
	public OperandValue getValue() {
		return new FilterValue(value);
	}
}
