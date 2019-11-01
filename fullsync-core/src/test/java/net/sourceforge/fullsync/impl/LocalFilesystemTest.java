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
package net.sourceforge.fullsync.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.fullsync.ConnectionDescription;

public class LocalFilesystemTest extends FilesystemTestBase {
	@Override
	@BeforeEach
	public void setUpEach() throws Exception {
		super.setUpEach();
	}

	@Override
	@AfterEach
	public void tearDownEach() {
		super.tearDownEach();
	}

	@Override
	protected ConnectionDescription getDestinationConnectionDescription() {
		ConnectionDescription.Builder dstBuilder = new ConnectionDescription.Builder();
		dstBuilder.setScheme("file");
		dstBuilder.setBufferStrategy("");
		dstBuilder.setPath(testingDst.getAbsolutePath());
		return dstBuilder.build();
	}

	@Override
	@Test
	public void testPublishUpdate() throws Exception {
		super.testPublishUpdate();
	}
}
