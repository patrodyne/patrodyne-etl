// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

/**
 * Unit tests for TranformIO's Main application.
 * 
 * @author Rick O'Sullivan
 */
public class MainTest
{
	/**
	 * Test method for {@link org.patrodyne.etl.transformio.Main#validateOptions(java.util.Properties)}.
	 */
	@Test
	public void testValidateOptions()
	{
		Properties options = new Properties();
		// No options
		assertFalse(Main.validateOptions(options));
		// Help option
		options.put("help", "");
		assertFalse(Main.validateOptions(options));
		// Mode: GUI
		options.clear();
		options.put("mode", "GUI");
		assertTrue(Main.validateOptions(options));
		options.put("mode", "gui");
		assertTrue(Main.validateOptions(options));
		// Mode: TUI
		options.clear();
		options.put("mode", "TUI");
		assertTrue(Main.validateOptions(options));
		options.put("mode", "tui");
		assertTrue(Main.validateOptions(options));
		// Mode: CLI, no Batch
		options.clear();
		options.put("mode", "CLI");
		assertFalse(Main.validateOptions(options));
		options.put("mode", "cli");
		assertFalse(Main.validateOptions(options));
		// Mode: CLI with empty batch URL
		options.clear();
		options.put("batch", "");
		options.put("mode", "CLI");
		assertFalse(Main.validateOptions(options));
		options.put("mode", "cli");
		assertFalse(Main.validateOptions(options));
		// Mode: CLI with non-empty batch URL
		options.clear();
		options.put("batch", "file:example.tui");
		options.put("mode", "CLI");
		assertTrue(Main.validateOptions(options));
		options.put("mode", "cli");
		assertTrue(Main.validateOptions(options));
		// Mode: CLI with non-empty non-batch URL
		options.clear();
		options.put("file:example.tui", "");
		options.put("mode", "CLI");
		assertFalse(Main.validateOptions(options));
		options.put("mode", "cli");
		assertFalse(Main.validateOptions(options));
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
