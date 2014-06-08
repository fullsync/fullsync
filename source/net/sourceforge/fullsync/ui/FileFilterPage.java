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
package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.rules.filefilter.FileFilter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class FileFilterPage implements WizardPage {

	private FileFilterDetails details;
	private FileFilter filter;

	public FileFilterPage(WizardDialog dialog, FileFilter filter) {
		dialog.setPage(this);
		this.filter = filter;
	}

	@Override
	public String getTitle() {
		return "File Filter";
	}

	@Override
	public String getCaption() {
		return "Edit the file filter";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public Image getIcon() {
		return GuiController.getInstance().getImage("FileFilter_Default.png"); //$NON-NLS-1$
	}

	@Override
	public Image getImage() {
		return GuiController.getInstance().getImage("FileFilter_Wizard.png"); //$NON-NLS-1$
	}

	@Override
	public void createContent(Composite content) {
		details = new FileFilterDetails(content, SWT.NULL, filter);
	}

	public FileFilter getFileFilter() {
		return filter;
	}

	@Override
	public boolean apply() {
		filter = details.getFileFilter();
		return true; //FIXME: return false if failed
	}

	@Override
	public boolean cancel() {
		filter = null;
		return true;
	}

}
