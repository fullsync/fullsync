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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.rules.PatternRule;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SyncRules extends AbstractRuleSet {
	private boolean processAllowed;

	public SyncRules(String name) {
		super(name);
		this.setRuleSet(name);
	}

	protected void processUseCommand(SyncTokenizer t) throws DataParseException {
		String what = t.nextWord();

		if (what.equalsIgnoreCase("rulefiles")) {
			usingSyncRulesFile = t.nextLocation();
		}
		else if (what.equalsIgnoreCase("direction")) {
			direction = t.nextLocation();
		}
		else if (what.equalsIgnoreCase("recursion")) {
			setUsingRecursion(t.nextBoolean());
		}
		else if (what.equalsIgnoreCase("recursiononignore")) {
			setUsingRecursionOnIgnore(t.nextBoolean());
		}
		else if (what.equalsIgnoreCase("ignoreall")) {
			ignoreAll = t.nextBoolean();
		}
		else {
			throw new DataParseException("Unknown Identifier \"" + what + "\" in \"" + t.getSourceName() + "\" in line " + t.lineno());
		}
	}

	protected void processSetCommand(SyncTokenizer t) throws DataParseException {
		String what = t.nextWord();
		String val = t.nextString();

		if (what.equalsIgnoreCase("rulesfile")) {
			syncRulesFilename = val;
		}
		else if (what.equalsIgnoreCase("ruleset")) {
			ruleSet = val;
		}
		else {
			throw new DataParseException("Unknown Identifier \"" + what + "\" in \"" + t.getSourceName() + "\" in line " + t.lineno());
		}
	}

	protected void processApplyCommand(SyncTokenizer t) throws DataParseException {
		String what = t.nextWord();

		if (what.equalsIgnoreCase("ignorerules")) {
			applyingIgnoreRules = t.nextBoolean();
		}
		else if (what.equalsIgnoreCase("takerules")) {
			applyingTakeRules = t.nextBoolean();
		}
		else if (what.equalsIgnoreCase("syncrules")) {
			applyingSyncRules = t.nextBoolean();
		}
		else if (what.equalsIgnoreCase("deletion")) {
			applyingDeletion = t.nextLocation();
		}
		else {
			throw new DataParseException("Unknown Identifier \"" + what + "\" in \"" + t.getSourceName() + "\" in line " + t.lineno());
		}
	}

	protected void processDefineCommand(SyncTokenizer t) throws DataParseException {
		String what = t.nextWord();
		String val = t.nextString();

		if (what.equalsIgnoreCase("ignore")) {
			ignoreRules.add(new PatternRule(val));
		}
		else if (what.equalsIgnoreCase("take")) {
			takeRules.add(new PatternRule(val));
		}
		else if (what.equalsIgnoreCase("sync")) {
			syncRules.add(val);
		}
		else {
			throw new DataParseException("Unknown Identifier \"" + what + "\" in \"" + t.getSourceName() + "\" in line " + t.lineno());
		}
	}

	protected void processResetCommand(SyncTokenizer t) throws DataParseException {
		String what = t.nextWord();

		if (what.equalsIgnoreCase("all")) {
			reset();
		}
		else if (what.equalsIgnoreCase("rules")) {
			String which = t.nextWord();
			if (which.equalsIgnoreCase("ignore")) {
				ignoreRules.clear();
			}
			else if (which.equalsIgnoreCase("take")) {
				takeRules.clear();
			}
			else if (which.equalsIgnoreCase("sync")) {
				syncRules.clear();
			}
			else if (which.equalsIgnoreCase("all")) {
				ignoreRules.clear();
				takeRules.clear();
				syncRules.clear();
			}
			else {
				throw new DataParseException("Unknown Identifier \"" + which + "\" in \"" + t.getSourceName() + "\" in line " + t.lineno());
			}
		}
		else {
			throw new DataParseException("Unknown Identifier \"" + what + "\" in \"" + t.getSourceName() + "\" in line " + t.lineno());
		}
	}

	protected void processSyncRule(String cmd, SyncTokenizer t) throws DataParseException {
		if (cmd.equalsIgnoreCase("use")) {
			processUseCommand(t);
		}
		else if (cmd.equalsIgnoreCase("set")) {
			processSetCommand(t);
		}
		else if (cmd.equalsIgnoreCase("apply")) {
			processApplyCommand(t);
		}
		else if (cmd.equalsIgnoreCase("define")) {
			processDefineCommand(t);
		}
		else if (cmd.equalsIgnoreCase("reset")) {
			processResetCommand(t);
		}
		else {
			throw new DataParseException("Unknown Command \"" + cmd + "\" in \"" + t.getSourceName() + "\" in line " + t.lineno());
		}
	}

	protected void processSyncRules(SyncTokenizer t) throws IOException, DataParseException {
		t.eolIsSignificant(false);
		String cmd;
		processAllowed = true;
		while ((cmd = t.nextWord()) != null) {
			if (cmd.equals("start")) {
				if (t.nextWord().equals("ruleset")) {
					String name = t.nextString();
					if ((ruleSet == null) || !name.equals(ruleSet.toLowerCase())) {
						processAllowed = false;
					}
				}
			}
			else if (cmd.equalsIgnoreCase("end")) {
				if (t.nextWord().equals("ruleset")) {
					String name = t.nextString();
					if ((ruleSet == null) || !name.equals(ruleSet)) {
						processAllowed = true;
					}
				}
			}
			else {
				if (processAllowed) {
					processSyncRule(cmd, t);
				}
			}
			t.finishStatement();
		}

	}

	@Override
	public void processRules(InputStream in, String filename) throws IOException, DataParseException {
		processSyncRules(new SyncTokenizer(new InputStreamReader(in), filename));
	}

}
