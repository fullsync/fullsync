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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.eventbus.EventBus;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.RuleSetDescriptor;
import net.sourceforge.fullsync.event.ProfileChanged;
import net.sourceforge.fullsync.schedule.Schedule;

class ProfileImpl implements Profile {
	private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	private static final String ATTRIBUTE_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTRIBUTE_ENABLED = "enabled"; //$NON-NLS-1$
	private static final String ATTRIBUTE_DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String ATTRIBUTE_LAST_UPDATE = "lastUpdate"; //$NON-NLS-1$
	private static final String ATTRIBUTE_LAST_ERROR_LEVEL = "lastErrorLevel"; //$NON-NLS-1$
	private static final String ATTRIBUTE_LAST_ERROR_STRING = "lastErrorString"; //$NON-NLS-1$
	private final EventBus eventBus;
	private final String id;
	private final String name;
	private final String description;
	private final String synchronizationType;
	private final ConnectionDescription source;
	private final ConnectionDescription destination;
	private final RuleSetDescriptor ruleSet;
	private final boolean schedulingEnabled;
	private final Schedule schedule;
	private Date lastUpdate;
	private int lastErrorLevel;
	private String lastErrorString;
	private long lastScheduleTime;

	public static Profile unserialize(EventBus eventBus, Element element) throws DataParseException {
		var profileId = element.getAttribute(ATTRIBUTE_ID);
		var profileName = element.getAttribute(ATTRIBUTE_NAME);
		var src = ConnectionDescription.unserialize((Element) element.getElementsByTagName("Source").item(0)); //$NON-NLS-1$
		var dst = ConnectionDescription.unserialize((Element) element.getElementsByTagName("Destination").item(0)); //$NON-NLS-1$
		var deserializedRuleset = RuleSetDescriptor
			.unserialize((Element) element.getElementsByTagName("RuleSetDescriptor").item(0)); //$NON-NLS-1$
		var usedRuleset = null != deserializedRuleset
			? deserializedRuleset
			: new SimplifiedRuleSetDescriptor(true, null, false, null);
		var description = element.getAttribute(ATTRIBUTE_DESCRIPTION);
		var synchronizationType = element.getAttribute(ATTRIBUTE_TYPE);
		var schedulingEnabled = true;
		if (element.hasAttribute(ATTRIBUTE_ENABLED)) {
			schedulingEnabled = Boolean.parseBoolean(element.getAttribute(ATTRIBUTE_ENABLED));
		}
		var schedule = Schedule.unserialize((Element) element.getElementsByTagName(Schedule.ELEMENT_NAME).item(0));

		Date lastUpdate = null;
		var lastErrorLevel = 0;
		String lastErrorString = null;
		var lastScheduleTime = 0L;

		if (element.hasAttribute(ATTRIBUTE_LAST_ERROR_LEVEL)) {
			lastErrorLevel = Integer.parseInt(element.getAttribute(ATTRIBUTE_LAST_ERROR_LEVEL));
			lastErrorString = element.getAttribute(ATTRIBUTE_LAST_ERROR_STRING);
		}

		try {
			var c = Calendar.getInstance(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
			c.setTimeInMillis(Integer.parseInt(element.getAttribute(ATTRIBUTE_LAST_UPDATE)));
			lastUpdate = c.getTime();
		}
		catch (NumberFormatException e) {
			// TODO: log a warning
		}

		return new ProfileImpl(eventBus, profileId, profileName, description, synchronizationType, src, dst, usedRuleset, schedulingEnabled,
			schedule, lastUpdate, lastErrorLevel, lastErrorString, lastScheduleTime);
	}

	public ProfileImpl(EventBus eventBus, String id, String name, String description, String synchronizationType,
		ConnectionDescription source, ConnectionDescription destination, RuleSetDescriptor ruleSet, boolean schedulingEnabled,
		Schedule schedule, Date lastUpdate, int lastErrorLevel, String lastErrorString, long lastScheduleTime) {
		this.eventBus = eventBus;
		this.id = id;
		this.name = name;
		this.description = description;
		this.synchronizationType = synchronizationType;
		this.source = source;
		this.destination = destination;
		this.ruleSet = ruleSet;
		this.schedulingEnabled = schedulingEnabled;
		this.schedule = schedule;
		this.lastUpdate = lastUpdate;
		this.lastErrorLevel = lastErrorLevel;
		this.lastErrorString = lastErrorString;
		this.lastScheduleTime = lastScheduleTime;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSynchronizationType() {
		return synchronizationType;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public ConnectionDescription getDestination() {
		return destination;
	}

	@Override
	public ConnectionDescription getSource() {
		return source;
	}

	@Override
	public Date getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
		eventBus.post(new ProfileChanged(this));
	}

	@Override
	public String getLastUpdateText() {
		// TODO this doesnt belong here (l18n)
		if (null == lastUpdate) {
			return "never";
		}
		else {
			return DateFormat.getDateTimeInstance().format(lastUpdate);
		}
	}

	// FIXME: this needs updates!
	@Override
	public String getNextUpdateText() {
		// TODO this doesnt belong here (l18n)
		if (null == schedule) {
			return "not scheduled";
		}
		else if (!schedulingEnabled) {
			return "not enabled";
		}
		else {
			return DateFormat.getDateTimeInstance().format(new Date(schedule.getNextOccurrence(lastScheduleTime, new Date().getTime())));
		}
	}

	@Override
	public RuleSetDescriptor getRuleSet() {
		return ruleSet;
	}

	@Override
	public boolean isSchedulingEnabled() {
		return schedulingEnabled;
	}

	@Override
	public Schedule getSchedule() {
		return schedule;
	}

	@Override
	public void setLastError(int lastErrorLevel, String lastErrorString) {
		this.lastErrorLevel = lastErrorLevel;
		this.lastErrorString = lastErrorString;
		eventBus.post(new ProfileChanged(this));
	}

	@Override
	public long getLastScheduleTime() {
		return lastScheduleTime;
	}

	@Override
	public void setLastScheduleTime(long lastScheduleTime) {
		this.lastScheduleTime = lastScheduleTime;
		eventBus.post(new ProfileChanged(this));
	}

	@Override
	public int getLastErrorLevel() {
		return lastErrorLevel;
	}

	@Override
	public String getLastErrorString() {
		return lastErrorString;
	}

	public Element serialize(final Document doc) {
		var elem = doc.createElement("Profile"); //$NON-NLS-1$
		elem.setAttribute(ATTRIBUTE_ID, id);
		elem.setAttribute(ATTRIBUTE_NAME, name);
		elem.setAttribute(ATTRIBUTE_DESCRIPTION, description);
		elem.setAttribute(ATTRIBUTE_TYPE, synchronizationType);
		elem.setAttribute(ATTRIBUTE_ENABLED, String.valueOf(schedulingEnabled));
		if (0 != lastErrorLevel) {
			elem.setAttribute(ATTRIBUTE_LAST_ERROR_LEVEL, String.valueOf(lastErrorLevel));
			elem.setAttribute(ATTRIBUTE_LAST_ERROR_STRING, lastErrorString);
		}
		if (null != lastUpdate) {
			elem.setAttribute(ATTRIBUTE_LAST_UPDATE, String.valueOf(lastUpdate.getTime()));
		}

		elem.appendChild(RuleSetDescriptor.serialize(ruleSet, doc));
		if (null != schedule) {
			elem.appendChild(Schedule.serialize(schedule, doc));
		}
		if (null != source) {
			elem.appendChild(source.serialize("Source", doc)); //$NON-NLS-1$
		}
		if (null != destination) {
			elem.appendChild(destination.serialize("Destination", doc)); //$NON-NLS-1$
		}

		return elem;
	}
}
