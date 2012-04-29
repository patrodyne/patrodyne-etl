// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.jul;

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;

import org.patrodyne.etl.transformio.Transformer;

/**
 * A Java Utility Logging (JUL) console handler for TransformIO.
 * 
 * @author Rick O'Sullivan
 */
public class PrintConsoleHandler
	extends ConsoleHandler
{
	protected void setOutputStream(OutputStream out)
		throws SecurityException
	{
		super.setOutputStream(Transformer.getConsoleOutputStream(out));
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
