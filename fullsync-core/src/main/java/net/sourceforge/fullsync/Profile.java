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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;
import net.sourceforge.fullsync.schedule.Schedule;

public class Profile implements Serializable, Comparable<Profile> {
	private static final long serialVersionUID = 3L;

	private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	private static final String ATTRIBUTE_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTRIBUTE_ENABLED = "enabled"; //$NON-NLS-1$
	private static final String ATTRIBUTE_DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String ATTRIBUTE_LAST_UPDATE = "lastUpdate"; //$NON-NLS-1$
	private static final String ATTRIBUTE_LAST_ERROR_LEVEL = "lastErrorLevel"; //$NON-NLS-1$
	private static final String ATTRIBUTE_LAST_ERROR_STRING = "lastErrorString"; //$NON-NLS-1$

	private String name;
	private String description;
	private String synchronizationType;
	private ConnectionDescription source;
	private ConnectionDescription destination;
	private RuleSetDescriptor ruleSet;
	private Date lastUpdate;
	private Schedule schedule;

	private boolean enabled;
	private int lastErrorLevel;
	private String lastErrorString;

	private transient boolean eventsAllowed;
	private transient List<ProfileChangeListener> listeners = new ArrayList<>();

	static Profile unserialize(Element element) throws DataParseException {
		String profileName = element.getAttribute(ATTRIBUTE_NAME);
		ConnectionDescription src = ConnectionDescription.unserialize((Element) element.getElementsByTagName("Source").item(0));
		ConnectionDescription dst = ConnectionDescription.unserialize((Element) element.getElementsByTagName("Destination").item(0));
		RuleSetDescriptor ruleset = RuleSetDescriptor.unserialize((Element) element.getElementsByTagName("RuleSetDescriptor").item(0));
		Profile p = new Profile(profileName, src, dst, ruleset);
		// this may happen with profiles that used advanced rule sets
		if (null == p.getRuleSet()) {
			p.setLastError(-1, "Error: the Filters of this Profile are broken");
			p.setRuleSet(new SimplyfiedRuleSetDescriptor(true, null, false, null));
		}
		p.setDescription(element.getAttribute(ATTRIBUTE_DESCRIPTION));
		p.setSynchronizationType(element.getAttribute(ATTRIBUTE_TYPE));
		if (element.hasAttribute(ATTRIBUTE_ENABLED)) {
			p.setEnabled(Boolean.valueOf(element.getAttribute(ATTRIBUTE_ENABLED)));
		}
		if (element.hasAttribute(ATTRIBUTE_LAST_ERROR_LEVEL)) {
			int errorLevel = Integer.parseInt(element.getAttribute(ATTRIBUTE_LAST_ERROR_LEVEL));
			p.setLastError(errorLevel, element.getAttribute(ATTRIBUTE_LAST_ERROR_STRING));
		}

		try {
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			c.setTimeInMillis(Integer.parseInt(element.getAttribute(ATTRIBUTE_LAST_UPDATE)));
			p.setLastUpdate(c.getTime());
		}
		catch (NumberFormatException e) {
			p.setLastUpdate(null);
		}

		p.setSchedule(Schedule.unserialize((Element) element.getElementsByTagName("Schedule").item(0)));
		return p;
	}

	public Profile(String name, ConnectionDescription source, ConnectionDescription destination, RuleSetDescriptor ruleSet) {
		this.name = name;
		this.description = "";
		this.source = source;
		this.destination = destination;
		this.ruleSet = ruleSet;
		this.lastUpdate = new Date();
		this.enabled = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		notifyProfileChangeListeners();
	}

	public String getSynchronizationType() {
		return synchronizationType;
	}

	public void setSynchronizationType(String synchronizationType) {
		this.synchronizationType = synchronizationType;
		notifyProfileChangeListeners();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		notifyProfileChangeListeners();
	}

	public ConnectionDescription getDestination() {
		return destination;
	}

	public void setDestination(ConnectionDescription destination) {
		this.destination = destination;
		notifyProfileChangeListeners();
	}

	public ConnectionDescription getSource() {
		return source;
	}

	public void setSource(ConnectionDescription source) {
		this.source = source;
		notifyProfileChangeListeners();
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
		notifyProfileChangeListeners();
	}

	public String getLastUpdateText() {
		// TODO this doesnt belong here (l18n)
		if (null == lastUpdate) {
			return "never";
		}
		else {
			return DateFormat.getDateTimeInstance().format(lastUpdate);
		}
	}

	//FIXME: this needs updates!
	public String getNextUpdateText() {
		// TODO this doesnt belong here (l18n)
		if (null == schedule) {
			return "not scheduled";
		}
		else if (!enabled) {
			return "not enabled";
		}
		else {
			return DateFormat.getDateTimeInstance().format(new Date(schedule.getNextOccurrence(new Date().getTime())));
		}
	}

	public RuleSetDescriptor getRuleSet() {
		return ruleSet;
	}

	public void setRuleSet(RuleSetDescriptor ruleSet) {
		this.ruleSet = ruleSet;
		notifyProfileChangeListeners();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		notifyProfileChangeListeners();
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
		notifyProfileChangeListeners();
	}

	public void setLastError(int lastErrorLevel, String lastErrorString) {
		this.lastErrorLevel = lastErrorLevel;
		this.lastErrorString = lastErrorString;
		notifyProfileChangeListeners();
	}

	public int getLastErrorLevel() {
		return lastErrorLevel;
	}

	public String getLastErrorString() {
		return lastErrorString;
	}

	public void addProfileChangeListener(ProfileChangeListener listener) {
		listeners.add(listener);
	}

	public void removeProfileChangeListener(ProfileChangeListener listener) {
		listeners.remove(listener);
	}

	protected void notifyProfileChangeListeners() {
		if (eventsAllowed) {
			for (ProfileChangeListener listener : listeners) {
				listener.profileChanged(this);
			}
		}
	}

	public void beginUpdate() {
		this.eventsAllowed = false;
	}

	public void endUpdate() {
		this.eventsAllowed = true;
		notifyProfileChangeListeners();
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(name);
		out.writeObject(description);
		out.writeObject(synchronizationType);
		out.writeObject(source);
		out.writeObject(destination);
		out.writeObject(ruleSet);
		out.writeObject(lastUpdate);
		out.writeObject(schedule);
		out.writeBoolean(enabled);
		out.writeInt(lastErrorLevel);
		out.writeObject(lastErrorString);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		name = (String) in.readObject();
		description = (String) in.readObject();
		synchronizationType = (String) in.readObject();
		source = (ConnectionDescription) in.readObject();
		destination = (ConnectionDescription) in.readObject();
		ruleSet = (RuleSetDescriptor) in.readObject();
		lastUpdate = (Date) in.readObject();
		schedule = (Schedule) in.readObject();
		enabled = in.readBoolean();
		lastErrorLevel = in.readInt();
		lastErrorString = (String) in.readObject();
	}

	public Element serialize(final Document doc) {
		Element elem = doc.createElement("Profile");
		elem.setAttribute(ATTRIBUTE_NAME, name);
		elem.setAttribute(ATTRIBUTE_DESCRIPTION, description);
		elem.setAttribute(ATTRIBUTE_TYPE, synchronizationType);
		elem.setAttribute(ATTRIBUTE_ENABLED, String.valueOf(enabled));
		elem.setAttribute(ATTRIBUTE_LAST_ERROR_LEVEL, String.valueOf(lastErrorLevel));
		elem.setAttribute(ATTRIBUTE_LAST_ERROR_STRING, lastErrorString);
		if (null != lastUpdate) {
			elem.setAttribute(ATTRIBUTE_LAST_UPDATE, String.valueOf(lastUpdate.getTime()));
		}

		elem.appendChild(RuleSetDescriptor.serialize(ruleSet, doc));
		if (null != schedule) {
			elem.appendChild(Schedule.serialize(schedule, doc));
		}
		if (null != source) {
			elem.appendChild(source.serialize("Source", doc));
		}
		if (null != destination) {
			elem.appendChild(destination.serialize("Destination", doc));
		}

		return elem;
	}

	@Override
	public int compareTo(Profile o) {
		return ("" + name).compareTo(o.getName());
	}
}
