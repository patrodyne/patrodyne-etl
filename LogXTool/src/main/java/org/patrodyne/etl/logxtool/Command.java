// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.logxtool;

import java.util.Properties;

import javax.xml.bind.JAXBException;

/**
 * Interface to execute commands.
 * @author Rick O'Sullivan
 */
public interface Command
{
	public void execute(Properties options) throws JAXBException;
}
