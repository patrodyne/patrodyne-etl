// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.jul;

import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.LogRecord;

/**
 * A JUL formatter for single line logging.
 * 
 * The layout is date, level, logger and message.
 * When required, the stack trace is logged after the message.
 * 
 * @author Rick O'Sullivan
 */
public class MediumFormatter
	extends AbstractFormatter
{
	private static final MessageFormat messageFormat = 
		new MessageFormat("{0,date,yyyy-MM-dd hh:mm:ss.SSS}\t{1}\t{2}\t{3}\n{4}");

	@Override
	public String format(LogRecord record)
	{
		Object[] arguments = new Object[5];
		arguments[0] = new Date(record.getMillis());
		arguments[1] = record.getLevel();
		arguments[2] = record.getLoggerName();
		arguments[3] = record.getMessage();
		arguments[4] = handle(record.getThrown());
		return messageFormat.format(arguments);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
