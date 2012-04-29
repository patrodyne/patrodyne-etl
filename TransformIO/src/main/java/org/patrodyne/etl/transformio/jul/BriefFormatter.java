// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.jul;

import java.text.MessageFormat;
import java.util.logging.LogRecord;

/**
 * A JUL formatter for single line logging.
 * 
 * The layout is level and message.
 * When required, the stack trace is logged after the message.
 * 
 * @author Rick O'Sullivan
 */
public class BriefFormatter
	extends AbstractFormatter
{
	private static final MessageFormat messageFormat =
		new MessageFormat("{0} {1}\n{2}");

	@Override
	public String format(LogRecord record)
	{
		Object[] arguments = new Object[3];
		arguments[0] = (record.getLevel()+"       ").substring(0, 7);
		arguments[1] = record.getMessage();
		arguments[2] = handle(record.getThrown());
		return messageFormat.format(arguments);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
