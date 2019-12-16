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

import java.util.Date;
import java.util.function.Supplier;

import com.google.common.eventbus.EventBus;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileBuilder;
import net.sourceforge.fullsync.RuleSetDescriptor;
import net.sourceforge.fullsync.schedule.Schedule;

public class ProfileBuilderImpl implements ProfileBuilder {
	private final EventBus eventBus;
	private String id;
	private String name;
	private String description;
	private String synchronizationType;
	private ConnectionDescription source;
	private ConnectionDescription destination;
	private RuleSetDescriptor ruleSet;
	private boolean schedulingEnabled;
	private Schedule schedule;
	private Date lastUpdate;
	private int lastErrorLevel;
	private String lastErrorString;
	private long lastScheduleTime;

	public ProfileBuilderImpl(EventBus eventBus, Profile profile, Supplier<String> newIdSupplier) {
		this.eventBus = eventBus;
		if (null != profile) {
			id = profile.getId();
			name = profile.getName();
			description = profile.getDescription();
			synchronizationType = profile.getSynchronizationType();
			source = profile.getSource();
			destination = profile.getDestination();
			ruleSet = profile.getRuleSet();
			schedulingEnabled = profile.isSchedulingEnabled();
			schedule = profile.getSchedule();
			lastUpdate = profile.getLastUpdate();
			lastErrorLevel = profile.getLastErrorLevel();
			lastErrorString = profile.getLastErrorString();
			lastScheduleTime = profile.getLastScheduleTime();
		}
		if (null == id) {
			id = newIdSupplier.get();
		}
	}

	@Override
	public ProfileBuilder setId(String id) {
		this.id = id;
		return this;
	}

	@Override
	public ProfileBuilder setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public ProfileBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public ProfileBuilder setSynchronizationType(String synchronizationType) {
		this.synchronizationType = synchronizationType;
		return this;
	}

	@Override
	public ProfileBuilder setSource(ConnectionDescription source) {
		this.source = source;
		return this;
	}

	@Override
	public ProfileBuilder setDestination(ConnectionDescription destination) {
		this.destination = destination;
		return this;
	}

	@Override
	public ProfileBuilder setRuleSet(RuleSetDescriptor ruleSet) {
		this.ruleSet = ruleSet;
		return this;
	}

	@Override
	public ProfileBuilder setSchedulingEnabled(boolean schedulingEnabled) {
		this.schedulingEnabled = schedulingEnabled;
		return this;
	}

	@Override
	public ProfileBuilder setSchedule(Schedule schedule) {
		this.schedule = schedule;
		return this;
	}

	@Override
	public ProfileBuilder setLastError(int lastErrorLevel, String lastErrorString) {
		this.lastErrorLevel = lastErrorLevel;
		this.lastErrorString = lastErrorString;
		return this;
	}

	@Override
	public ProfileBuilder setLastScheduleTime(long lastScheduleTime) {
		this.lastScheduleTime = lastScheduleTime;
		return this;
	}

	@Override
	public ProfileBuilder setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
		return this;
	}

	@Override
	public Profile build() {
		return new ProfileImpl(eventBus, id, name, description, synchronizationType, source, destination, ruleSet, schedulingEnabled,
			schedule, lastUpdate, lastErrorLevel, lastErrorString, lastScheduleTime);
	}
}
