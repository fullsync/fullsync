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

import java.util.Date;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class PhaseOneTest extends BaseConnectionTest {

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		ConnectionDescription dst = new ConnectionDescription(testingDst.toURI());
		dst.setParameter("bufferStrategy", "");
		profile.setDestination(dst);
	}

	@Override
	@Test
	public void testSingleInSync() throws Exception {
		createRuleFile();
		long lm = new Date().getTime();

		createNewFileWithContents(testingSrc, "sourceFile1.txt", lm, "this is a test\ncontent1");
		createNewFileWithContents(testingDst, "sourceFile1.txt", lm, "this is a test\ncontent1");

		Hashtable<String, Action> expectation = new Hashtable<String, Action>();
		expectation.put("sourceFile1.txt", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));
		assertPhaseOneActions(expectation);
	}

	@Override
	@Test
	public void testSingleFileChange() throws Exception {
		super.testSingleFileChange();
	}
}
