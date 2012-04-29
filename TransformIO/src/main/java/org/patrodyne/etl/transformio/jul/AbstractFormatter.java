// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.jul;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;

/**
 * Base class for JUL formatters.
 *
 * @author Rick O'Sullivan
 */
public abstract class AbstractFormatter
	extends Formatter
{
	/**
	 * Handle a throwable by returning a stack trace
	 * or blank when the the throwable is null.
	 * 
	 * @param thrown A possibly null throwable.
	 * 
	 * @return A stack trace or blank string.
	 */
	protected String handle(Throwable thrown)
	{
		if (thrown != null)
	    {
	        StringWriter writer = new StringWriter();
	        thrown.printStackTrace(new PrintWriter(writer));
	        return writer.toString();
	    }
	    else
	        return "";
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
