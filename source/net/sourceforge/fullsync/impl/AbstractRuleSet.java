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
import java.util.StringTokenizer;
import java.util.Vector;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.Rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides informations and rules about how to handle specific<br>
 * files and whether system files should be used or not.<br>
 * TODO refine commands to a lot of SETs and add RESET commands
 *
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public abstract class AbstractRuleSet implements RuleSet, Cloneable {
	private static final Logger logger = LoggerFactory.getLogger(AbstractRuleSet.class.getSimpleName());
	String name;
	boolean usingRecursion;
	boolean usingRecursionOnIgnore;
	int usingSyncRulesFile;

	String syncRulesFilename;

	int checkingBufferAlways;
	int checkingBufferOnReplace;

	Vector<Rule> takeRules;
	Vector<Rule> ignoreRules;
	Vector<String> syncRules;

	boolean applyingTakeRules;
	boolean applyingIgnoreRules;
	boolean applyingSyncRules;
	int applyingDeletion;
	/**
	 * ignore all files by default; just Take rules will allow actions.
	 */
	boolean ignoreAll;

	/**
	 * Indicates allowed directions:
	 * <ul>
	 * <li>0 - none (do nothing)
	 * <li>1 - to_local
	 * <li>2 - to_remote
	 * </ul>
	 */
	int direction;

	String ruleSet;
	private boolean justLogging;

	public AbstractRuleSet() {
		reset();
		justLogging = true;
	}

	/**
	 * Constructor AbstractRuleSet.
	 *
	 * @param name
	 */
	public AbstractRuleSet(String name) {
		this();
		this.name = name;
	}

	protected void reset() {
		usingSyncRulesFile = Location.Both;
		syncRulesFilename = ".syncrules";
		// useSyncBufferFile = RuleSet.NONE;
		// syncBufferFilename = ".syncfiles";
		// useSyncCommandsFile = RuleSet.NONE;
		// syncCommandsFilename= ".synccmd";

		checkingBufferAlways = Location.None;
		checkingBufferOnReplace = Location.None;

		if (takeRules == null) {
			takeRules = new Vector<Rule>();
		}
		else {
			takeRules.clear();
		}

		if (ignoreRules == null) {
			ignoreRules = new Vector<Rule>();
		}
		else {
			ignoreRules.clear();
		}

		if (syncRules == null) {
			syncRules = new Vector<String>();
		}
		else {
			syncRules.clear();
		}

		applyingTakeRules = false;
		applyingIgnoreRules = false;
		applyingSyncRules = false;
		applyingDeletion = Location.None;

		ignoreAll = false;

		direction = 0;

		ruleSet = null;
	}

	/**
	 * @return Returns true if the file should be taken, false if ignored
	 */
	@Override
	public boolean isNodeIgnored(File node) {
		boolean take = !ignoreAll;

		if (take) {
			for (Rule rule : ignoreRules) {
				if (rule.accepts(node)) {
					take = false;
					break;
				}
			}
		}

		if (!take) {
			for (Rule rule : takeRules) {
				if (rule.accepts(node)) {
					take = true;
					break;
				}
			}
		}

		return !take;
	}

	protected long evalRealValue(File f, String exp) throws DataParseException {
		if (exp.equalsIgnoreCase("length")) {
			return f.getSize();
		}
		else if (exp.equalsIgnoreCase("date")) {
			return (int) Math.floor(f.getLastModified() / 1000.0);
		}
		else {
			throw new DataParseException("Error while parsing SyncRule: '" + exp + "' is unknown", 0);
		}
	}

	protected int eval(long srcValue, String operator, long dstValue) throws DataParseException {
		if (operator.equals("!=")) {
			return (srcValue != dstValue) ? -100 : 0;
		}
		else if (operator.equals("==")) {
			return (srcValue == dstValue) ? 0 : -100;
		}
		else if (operator.equals(">")) {
			return (srcValue > dstValue) ? -1 : 1;
		}
		else if (operator.equals("<")) {
			return (srcValue < dstValue) ? 1 : -1;
		}
		else {
			throw new DataParseException("Error while parsing SyncRule: '" + operator + "' is unknown operator", 0);
		}
	}

	private static final State inSyncBoth = new State(State.NodeInSync, Location.Both);
	private static final State fileChgDst = new State(State.FileChange, Location.Destination);
	private static final State fileChgSrc = new State(State.FileChange, Location.Source);
	private static final State fileChgNone = new State(State.FileChange, Location.None);

	@Override
	public State compareFiles(File src, File dst) throws DataParseException {
		// TODO verify functionality of this method
		//FIXME: optimize rule processing
		//FIXME: add debug logs to every decision
		//FIXME: "compile" syncRules into a tree or so to allow resolution without string tokenization,...
		boolean isEqual = true;
		State st = null;
		int val = 0, res;
		for (String rule : syncRules) {
			StringTokenizer t = new StringTokenizer(rule, " ");
			String srcValue = t.nextToken();
			String operator = t.nextToken();
			String dstValue = t.nextToken();
			long srcV, dstV;
			srcV = evalRealValue(src, srcValue);
			dstV = evalRealValue(dst, dstValue);
			res = eval(srcV, operator, dstV);
			logger.debug(srcValue + " " + operator + " " + dstValue + ": " + srcV + operator + dstV + " = " + res);
			val += res;
			if (val < -50) {
				val += 100;
				isEqual = false;
			}

			for (; t.hasMoreTokens();) {
				String bind = t.nextToken();
				if (bind.equals(",")) {
					//FIXME: uhm if , then nothing??
				}

				srcValue = t.nextToken();
				operator = t.nextToken();
				dstValue = t.nextToken();
				srcV = evalRealValue(src, srcValue);
				dstV = evalRealValue(dst, dstValue);
				res = eval(srcV, operator, dstV);
				val += res;
				logger.debug(srcValue + " " + operator + " " + dstValue + ": " + srcV + operator + dstV + " = " + res);
				if (val < -50) {
					val += 100;
					isEqual = false;
				}
			}
		}
		if ((val == 0) && isEqual) {
			st = inSyncBoth;
		}
		else if (val > 0) {
			st = fileChgDst;
		}
		else if (val < 0) {
			st = fileChgSrc;
		}
		else {
			st = fileChgNone;
		}
		logger.debug("compareFiles = " + st);
		return st;
	}

	@Override
	public RuleSet createChild(File src, File dst) throws DataParseException, IOException {
		try {
			AbstractRuleSet rules = (AbstractRuleSet) this.clone();
			if (rules.isUsingRulesFile(SyncTokenizer.LOCAL)) {
				rules.processRules(src);
			}
			if (rules.isUsingRulesFile(SyncTokenizer.REMOTE)) {
				rules.processRules(dst);
			}
			return rules;
		}
		catch (CloneNotSupportedException cnse) {
			ExceptionHandler.reportException(cnse);
			return null;
		}
	}

	public void processRules(File dir) throws DataParseException, IOException {
		// TODO really unbuffered ?
		File node = (dir.getUnbuffered()).getChild(syncRulesFilename);
		if ((node != null) && !node.isDirectory()) {
			InputStream in = (node).getInputStream();
			processRules(in, (node).getPath());
			in.close();
		}
	}

	public abstract void processRules(InputStream in, String filename) throws IOException, DataParseException;

	/**
	 *
	 * @return boolean true if rules should be processed, false if not; it does not depend on the active direction
	 */
	public boolean isUsingRulesFile(int where) {
		/*
		 * switch( useSyncRulesFile )
		 * {
		 * case SyncTokenizer.NONE:
		 * return false;
		 * case SyncTokenizer.LOCAL:
		 * return (where == SyncTokenizer.LOCAL);
		 * case SyncTokenizer.REMOTE:
		 * return (where == SyncTokenizer.REMOTE);
		 * case SyncTokenizer.BOTH:
		 * return true;
		 * }
		 */
		return (usingSyncRulesFile & where) > 0;
		// return false;
	}

	@Override
	public boolean isUsingRecursion() {
		return usingRecursion;
	}

	@Override
	public boolean isUsingRecursionOnIgnore() {
		return usingRecursionOnIgnore;
	}

	/**
	 * Returns the ruleSet.
	 *
	 * @return String
	 */
	public String getRuleSet() {
		return ruleSet;
	}

	/**
	 * Sets the ruleSet.
	 *
	 * @param ruleSet
	 *            The ruleSet to set
	 */
	public void setRuleSet(String ruleSet) {
		this.ruleSet = ruleSet;
	}

	/**
	 * Returns the direction.
	 *
	 * @return int
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * Returns the justLogging.
	 *
	 * @return boolean
	 */
	@Override
	public boolean isJustLogging() {
		return justLogging;
	}

	/**
	 * Sets the justLogging.
	 *
	 * @param justLogging
	 *            The justLogging to set
	 */
	public void setJustLogging(boolean justLogging) {
		this.justLogging = justLogging;
	}

	/**
	 * Returns the applyDeletion.
	 *
	 * @return boolean
	 */
	@Override
	public boolean isApplyingDeletion(int location) {
		return (applyingDeletion & location) > 0;
	}

	public boolean isApplyingIgnoreRules() {
		return applyingIgnoreRules;
	}

	public void setApplyingIgnoreRules(boolean applyingIgnoreRules) {
		this.applyingIgnoreRules = applyingIgnoreRules;
	}

	public boolean isApplyingSyncRules() {
		return applyingSyncRules;
	}

	public void setApplyingSyncRules(boolean applyingSyncRules) {
		this.applyingSyncRules = applyingSyncRules;
	}

	public boolean isApplyingTakeRules() {
		return applyingTakeRules;
	}

	public void setApplyingTakeRules(boolean applyingTakeRules) {
		this.applyingTakeRules = applyingTakeRules;
	}

	@Override
	public boolean isCheckingBufferAlways(int location) {
		return (checkingBufferAlways & location) > 0;
	}

	@Override
	public boolean isCheckingBufferOnReplace(int location) {
		return (checkingBufferOnReplace & location) > 0;
	}

	public boolean isIgnoreAll() {
		return ignoreAll;
	}

	public void setIgnoreAll(boolean ignoreAll) {
		this.ignoreAll = ignoreAll;
	}

	public Vector<Rule> getIgnoreRules() {
		return ignoreRules;
	}

	public void setIgnoreRules(Vector<Rule> ignoreRules) {
		this.ignoreRules = ignoreRules;
	}

	public void addIgnoreRule(Rule rule) {
		this.ignoreRules.add(rule);
	}

	public Vector<String> getSyncRules() {
		return syncRules;
	}

	public void setSyncRules(Vector<String> syncRules) {
		this.syncRules = syncRules;
	}

	public String getSyncRulesFilename() {
		return syncRulesFilename;
	}

	public void setSyncRulesFilename(String syncRulesFilename) {
		this.syncRulesFilename = syncRulesFilename;
	}

	public Vector<Rule> getTakeRules() {
		return takeRules;
	}

	public void setTakeRules(Vector<Rule> takeRules) {
		this.takeRules = takeRules;
	}

	public void addTakeRule(Rule rule) {
		this.takeRules.add(rule);
	}

	public int getUsingSyncRulesFile() {
		return usingSyncRulesFile;
	}

	public void setUsingSyncRulesFile(int usingSyncRulesFile) {
		this.usingSyncRulesFile = usingSyncRulesFile;
	}

	public void setApplyingDeletion(int applyingDeletion) {
		this.applyingDeletion = applyingDeletion;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setUsingRecursion(boolean usingRecursion) {
		this.usingRecursion = usingRecursion;
	}

	public void setUsingRecursionOnIgnore(boolean usingRecursionOnIgnore) {
		this.usingRecursionOnIgnore = usingRecursionOnIgnore;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
