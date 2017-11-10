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

public enum ActionType {
	NOTHING,
	ADD,
	UPDATE,
	DELETE,
	NOT_DECIDABLE_ERROR,
	UNEXPECTED_CHANGE_ERROR,
	DIR_HERE_FILE_THERE_ERROR;

	public boolean isError() {
		switch (this) {
			case NOTHING:
			case ADD:
			case UPDATE:
			case DELETE:
				return false;
			case NOT_DECIDABLE_ERROR:
			case UNEXPECTED_CHANGE_ERROR:
			case DIR_HERE_FILE_THERE_ERROR:
				return true;
		}
		throw new RuntimeException("Implementation bug in ActionType::isError, ordinal: " + ordinal());
	}
}
