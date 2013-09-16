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

import com.adamtaft.eb.EventBus;
import com.adamtaft.eb.EventBusService;

import net.sourceforge.fullsync.impl.ConfigurationPreferences;

public class FullSync {
	private static FullSync m_instance;

	public static FullSync inst() {
		return m_instance;
	}

	private final Preferences m_prefs;
	private final EventBus m_bus;

	public FullSync(ConfigurationPreferences preferences) {
		m_instance = this;
		m_prefs = preferences;
		m_bus = EventBusService.getInstance();
	}

	public static Preferences prefs() {
		return inst().m_prefs;
	}

	public static void subscribe(Object subscriber) {
		inst().m_bus.subscribe(subscriber);
	}

	public static void unsubscribe(Object subscriber) {
		inst().m_bus.unsubscribe(subscriber);
	}

	public static void publish(Object event) {
		inst().m_bus.publish(event);
	}

	public static boolean hasPendingEvents() {
		return inst().m_bus.hasPendingEvents();
	}
}
