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
import java.util.TimeZone;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;
import net.sourceforge.fullsync.schedule.Schedule;

public class Profile implements Serializable, Comparable<Profile> {
	private static final long serialVersionUID = 3L;

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
	private transient ArrayList<ProfileChangeListener> listeners;

	static Profile unserialize(Element element) {
		Profile p = new Profile(element.getAttribute("name"),
				ConnectionDescription.unserialize((Element) element.getElementsByTagName("Source").item(0)),
				ConnectionDescription.unserialize((Element) element.getElementsByTagName("Destination").item(0)),
				RuleSetDescriptor.unserialize((Element) element.getElementsByTagName("RuleSetDescriptor").item(0)));
		// this may happen with profiles that used advanced rule sets
		if (null == p.getRuleSet()) {
			p.setLastError(-1, "Error: the Filters of this Profile are broken");
			p.setRuleSet(new SimplyfiedRuleSetDescriptor(true, null, false, null));
		}
		p.setDescription(element.getAttribute("description"));
		p.setSynchronizationType(element.getAttribute("type"));
		if (element.hasAttribute("enabled")) {
			p.setEnabled(Boolean.valueOf(element.getAttribute("enabled")).booleanValue());
		}
		if (element.hasAttribute("lastErrorLevel")) {
			p.setLastError(Integer.parseInt(element.getAttribute("lastErrorLevel")), element.getAttribute("lastErrorString"));
		}

		try {
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			c.setTimeInMillis(Integer.parseInt(element.getAttribute("lastUpdate")));
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
		this.listeners = new ArrayList<>();
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
		if (lastUpdate == null) {
			return "never";
		}
		else {
			return DateFormat.getDateTimeInstance().format(lastUpdate);
		}
	}

	//FIXME: this needs updates!
	public String getNextUpdateText() {
		// TODO this doesnt belong here (l18n)
		if (schedule == null) {
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

		this.listeners = new ArrayList<>();
	}

	public Element serialize(final Document doc) {
		Element elem = doc.createElement("Profile");
		elem.setAttribute("name", name);
		elem.setAttribute("description", description);
		elem.setAttribute("type", synchronizationType);
		elem.setAttribute("enabled", String.valueOf(enabled));
		elem.setAttribute("lastErrorLevel", String.valueOf(lastErrorLevel));
		elem.setAttribute("lastErrorString", lastErrorString);
		if (null != lastUpdate) {
			elem.setAttribute("lastUpdate", Long.valueOf(lastUpdate.getTime()).toString());
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
