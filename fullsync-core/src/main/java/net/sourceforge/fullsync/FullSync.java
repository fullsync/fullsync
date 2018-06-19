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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutionException;

import javax.inject.Singleton;

import com.google.common.util.concurrent.Futures;

@Singleton
public class FullSync {
	public static final String PREFERENCES_PROPERTIES = "preferences.properties"; //$NON-NLS-1$
	public static final String PROFILES_XML = "profiles.xml"; //$NON-NLS-1$

	private final Deque<PromptQuestion> questionHandler = new ArrayDeque<>();

	public FullSync() {
		// deny everything by default
		questionHandler.push(question -> Futures.immediateFailedFuture(new ExecutionException("No Question handler present", null)));
	}

	public PromptQuestion getQuestionHandler() {
		return questionHandler.peek();
	}

	public void pushQuestionHandler(PromptQuestion handler) {
		questionHandler.push(handler);
	}

	public void popQuestionHandler() {
		questionHandler.pop();
	}
}
