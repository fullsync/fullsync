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
package org.slf4j.impl;

import java.io.Serial;

import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * A simple (and direct) implementation that logs messages of level TRACE or
 * higher on the console (<code>System.err</code>).
 *
 * <p>
 * The output includes the relative time in milliseconds, thread name, the level, logger name, and the message followed by the line
 * separator for the host. In log4j terms it amounts to the "%r [%t] %level %logger - %m%n" pattern.
 * </p>
 *
 * <p>
 * Sample output follows.
 * </p>
 *
 * <pre>
 * 176 [main] INFO examples.Sort - Populating an array of 2 elements in reverse order.
 * 225 [main] INFO examples.SortAlgo - Entered the sort method.
 * 304 [main] INFO examples.SortAlgo - Dump of integer array:
 * 317 [main] INFO examples.SortAlgo - Element [0] = 0
 * 331 [main] INFO examples.SortAlgo - Element [1] = 1
 * 343 [main] INFO examples.Sort - The next log statement should be an error message.
 * 346 [main] ERROR examples.SortAlgo - Tried to dump an uninitialized array.
 *         at org.log4j.examples.SortAlgo.dump(SortAlgo.java:58)
 *         at org.log4j.examples.Sort.main(Sort.java:64)
 * 467 [main] INFO  examples.Sort - Exiting main method.
 * </pre>
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class FullSyncLogger extends MarkerIgnoringBase {
	@Serial
	private static final long serialVersionUID = -1504986506259137575L;
	/**
	 * Mark the time when this class gets loaded into memory.
	 */
	private static final long startTime = System.currentTimeMillis();
	public static final String LINE_SEPARATOR = System.lineSeparator();
	private static final String TRACE_STR = "TRACE";
	private static final String DEBUG_STR = "DEBUG";
	private static final String INFO_STR = "INFO";
	private static final String WARN_STR = "WARN";
	private static final String ERROR_STR = "ERROR";

	/**
	 * Package access allows only {@link FullSyncLoggerFactory} to instantiate
	 * SimpleLogger instances.
	 */
	FullSyncLogger(final String name) {
		this.name = name;
	}

	/**
	 * Always returns true.
	 *
	 * @return always true
	 */
	@Override
	public boolean isTraceEnabled() {
		return true;
	}

	/**
	 * A simple implementation which always logs messages of level TRACE according
	 * to the format outlined above.
	 */
	@Override
	public void trace(final String msg) {
		log(TRACE_STR, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * TRACE according to the format outlined above.
	 */
	@Override
	public void trace(final String format, final Object arg) {
		formatAndLog(TRACE_STR, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * TRACE according to the format outlined above.
	 */
	@Override
	public void trace(final String format, final Object arg1, final Object arg2) {
		formatAndLog(TRACE_STR, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * TRACE according to the format outlined above.
	 */
	@Override
	public void trace(final String format, final Object... argArray) {
		formatAndLog(TRACE_STR, format, argArray);
	}

	/**
	 * Log a message of level TRACE, including an exception.
	 */
	@Override
	public void trace(final String msg, final Throwable t) {
		log(TRACE_STR, msg, t);
	}

	/**
	 * Always returns true.
	 *
	 * @return always true
	 */
	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	/**
	 * A simple implementation which always logs messages of level DEBUG according
	 * to the format outlined above.
	 */
	@Override
	public void debug(final String msg) {
		log(DEBUG_STR, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * DEBUG according to the format outlined above.
	 */
	@Override
	public void debug(final String format, final Object arg1) {
		formatAndLog(DEBUG_STR, format, arg1, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * DEBUG according to the format outlined above.
	 */
	@Override
	public void debug(final String format, final Object arg1, final Object arg2) {
		formatAndLog(DEBUG_STR, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * DEBUG according to the format outlined above.
	 */
	@Override
	public void debug(final String format, final Object... argArray) {
		formatAndLog(DEBUG_STR, format, argArray);
	}

	/**
	 * Log a message of level DEBUG, including an exception.
	 */
	@Override
	public void debug(final String msg, final Throwable t) {
		log(DEBUG_STR, msg, t);
	}

	/**
	 * This is our internal implementation for logging regular (non-parameterized)
	 * log messages.
	 *
	 * @param level
	 * @param message
	 * @param t
	 */
	private void log(final String level, final String message, final Throwable t) {
		final var buf = new StringBuilder();

		final var millis = System.currentTimeMillis();
		buf.append(millis - startTime);

		buf.append(" [");
		buf.append(Thread.currentThread().getName());
		buf.append("] ");

		buf.append(level);
		buf.append(" ");

		buf.append(name);
		buf.append(" - ");

		buf.append(message);

		buf.append(LINE_SEPARATOR);

		System.err.print(buf);
		if (null != t) {
			t.printStackTrace(System.err);
		}
	}

	/**
	 * For formatted messages, first substitute arguments and then log.
	 *
	 * @param level
	 * @param format
	 * @param arg1
	 * @param arg2
	 */
	private void formatAndLog(final String level, final String format, final Object arg1, final Object arg2) {
		final var tp = MessageFormatter.format(format, arg1, arg2);
		log(level, tp.getMessage(), tp.getThrowable());
	}

	/**
	 * For formatted messages, first substitute arguments and then log.
	 *
	 * @param level
	 * @param format
	 * @param argArray
	 */
	private void formatAndLog(final String level, final String format, final Object[] argArray) {
		final var tp = MessageFormatter.arrayFormat(format, argArray);
		log(level, tp.getMessage(), tp.getThrowable());
	}

	/**
	 * Always returns true.
	 */
	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	/**
	 * A simple implementation which always logs messages of level INFO according
	 * to the format outlined above.
	 */
	@Override
	public void info(final String msg) {
		log(INFO_STR, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * INFO according to the format outlined above.
	 */
	@Override
	public void info(final String format, final Object arg) {
		formatAndLog(INFO_STR, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * INFO according to the format outlined above.
	 */
	@Override
	public void info(final String format, final Object arg1, final Object arg2) {
		formatAndLog(INFO_STR, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * INFO according to the format outlined above.
	 */
	@Override
	public void info(final String format, final Object... argArray) {
		formatAndLog(INFO_STR, format, argArray);
	}

	/**
	 * Log a message of level INFO, including an exception.
	 */
	@Override
	public void info(final String msg, final Throwable t) {
		log(INFO_STR, msg, t);
	}

	/**
	 * Always returns true.
	 */
	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	/**
	 * A simple implementation which always logs messages of level WARN according
	 * to the format outlined above.
	 */
	@Override
	public void warn(final String msg) {
		log(WARN_STR, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * WARN according to the format outlined above.
	 */
	@Override
	public void warn(final String format, final Object arg) {
		formatAndLog(WARN_STR, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * WARN according to the format outlined above.
	 */
	@Override
	public void warn(final String format, final Object arg1, final Object arg2) {
		formatAndLog(WARN_STR, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * WARN according to the format outlined above.
	 */
	@Override
	public void warn(final String format, final Object... argArray) {
		formatAndLog(WARN_STR, format, argArray);
	}

	/**
	 * Log a message of level WARN, including an exception.
	 */
	@Override
	public void warn(final String msg, final Throwable t) {
		log(WARN_STR, msg, t);
	}

	/**
	 * Always returns true.
	 */
	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	/**
	 * A simple implementation which always logs messages of level ERROR according
	 * to the format outlined above.
	 */
	@Override
	public void error(final String msg) {
		log(ERROR_STR, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	@Override
	public void error(final String format, final Object arg) {
		formatAndLog(ERROR_STR, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	@Override
	public void error(final String format, final Object arg1, final Object arg2) {
		formatAndLog(ERROR_STR, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	@Override
	public void error(final String format, final Object... argArray) {
		formatAndLog(ERROR_STR, format, argArray);
	}

	/**
	 * Log a message of level ERROR, including an exception.
	 */
	@Override
	public void error(final String msg, final Throwable t) {
		log(ERROR_STR, msg, t);
	}
}
