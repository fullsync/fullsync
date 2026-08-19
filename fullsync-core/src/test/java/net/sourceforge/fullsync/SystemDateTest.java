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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class SystemDateTest {
	@AfterEach
	public void tearDown() {
		SystemDate.getInstance().setUseSystemTime();
	}

	@Test
	public void getInstanceReturnsNonNull() {
		assertNotNull(SystemDate.getInstance());
	}

	@Test
	public void getInstanceReturnsSameInstance() {
		assertSame(SystemDate.getInstance(), SystemDate.getInstance());
	}

	@Test
	public void setCurrentFixesTime() {
		var fixedTime = System.currentTimeMillis() + 60_000L;
		SystemDate.getInstance().setCurrent(fixedTime);
		var reported = SystemDate.getInstance().currentTimeMillis();
		assertTrue(reported >= fixedTime && reported < fixedTime + 1_000L,
			"reported " + reported + " should be within [" + fixedTime + ", " + (fixedTime + 1_000L) + ")");
	}

	@Test
	public void setUseSystemTimeReverts() {
		SystemDate.getInstance().setCurrent(0L);
		SystemDate.getInstance().setUseSystemTime();
		var before = System.currentTimeMillis();
		var reported = SystemDate.getInstance().currentTimeMillis();
		var after = System.currentTimeMillis();
		assertTrue(reported >= before && reported <= after + 10);
	}

	@Test
	public void setTimeSpeedAcceleratesTime() throws Exception {
		var base = System.currentTimeMillis();
		SystemDate.getInstance().setCurrent(base);
		SystemDate.getInstance().setTimeSpeed(100);
		var wallStart = System.currentTimeMillis();
		Thread.sleep(20);
		var wallElapsed = System.currentTimeMillis() - wallStart;
		var reported = SystemDate.getInstance().currentTimeMillis();
		var reportedElapsed = reported - base;
		assertTrue(reportedElapsed >= wallElapsed * 50,
			"reported elapsed " + reportedElapsed + " should be >> wall elapsed " + wallElapsed);
	}
}
