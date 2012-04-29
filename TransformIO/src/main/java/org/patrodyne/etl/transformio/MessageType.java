// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio;

/**
 * <p>Message Types identify how information will be annotated or appended.</p>
 * 
 * <ul>
 * <li>When used in a graphical user interface, the MessageType can be used to
 * select the type of dialog and its corresponding icon.</li>
 * <li>When used in a logging context, the MessageType can be used to select
 * the logging level and corresponding appenders.</li>
 * </ul>
 * 
 * @author Rick O'Sullivan
 */
public enum MessageType
{
	/** An informational note.*/
	INFO,
	/** A warning message about a forgivable issue. */
	WARN,
	/** An recoverable error has occurred. */
	ERROR, 
	/** The application has failed and cannot recover. */
	FATAL;
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
