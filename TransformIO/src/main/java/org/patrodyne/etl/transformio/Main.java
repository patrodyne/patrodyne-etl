// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.patrodyne.etl.transformio.cli.CommandLI;
import org.patrodyne.etl.transformio.gui.GraphicalUI;
import org.patrodyne.etl.transformio.tui.TextualUI;

/**
 * <p>
 * Launch the Main application to transform an input source into an output
 * target.
 * </p>
 * 
 * <p>
 * By design, the Main class limits imports to the Java Class Library before
 * transferring control to one of the user interface classes: GraphicalUI,
 * TextualUI or CommandLI.
 * </p>
 * 
 * <p>
 * More specifically, this class does not import the logging library
 * <tt>org.slf4j</tt> because the choice of a specific logger is a runtime
 * option. At runtime, the logging package is loaded using the Java Extension
 * Mechanism for optional packages. In the event that the optional packages are
 * not where they should be, as specified by <tt>-Djava.ext.dirs</tt>, any class
 * that imports one will also fail. It is the job of the Main class to catch
 * this sort of failure or any other exceptions and inform the user.
 * </p>
 * 
 * <p>
 * In summary, this class starts the application and other <em>TransformIO</em>
 * classes import optional packages using the standard extension mechanism. The
 * extension path is specified using a system property, <tt>java.ext.dirs</tt>.
 * </p>
 * 
 * @author Rick O'Sullivan
 */
public class Main
{
	/** Represents the command line options as a dictionary of properties. */
	protected static Properties options = new Properties();
	
	/**
	 * Launch the application using command line options to choose the
	 * user interface mode and provide an optional batch configuration.
	 * Throwables are trapped for error handling.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args)
	{
		try
		{
			// Parse CLI arguments into a set of property options.
			for (int i=0; i < args.length; ++i)
			{
				String[] nvp = args[i].split("=", 2);
				if ( nvp.length == 2 )
					options.put(nvp[0].toLowerCase(), nvp[1]);
				else if ( nvp.length == 1 )
					options.put(nvp[0].toLowerCase(), "");
				else
					throw new IllegalArgumentException("invalid option at position "+(i+1)+": "+args[i]);
			}

			// Validate options, start or complain.
			if ( validateOptions(options) )
			{
//				CommandLI.showOptions(options);
				switch ( getMode() )
				{
					case GUI: GraphicalUI.start(options); break;
					case TUI: TextualUI.start(options); break;
					case CLI: CommandLI.start(options); break;
				}
			}
			else
				CommandLI.showUsage();
		}
		catch (Throwable t)
		{
			errorHandler(t);
		}
	}

	/**
	 * Validate command line options.
	 * 
	 * <p>Conditions that return false:</p>
	 * 
	 * <ul>
	 * <li>no options</li>
	 * <li>help option</li>
	 * <li>option key is not help, mode or batch.</li>
	 * <li>mode option when value is not GUI, TUI or CLI.</li>
	 * <li>batch option when value is empty.</li>
	 * <li>mode is CLI and batch is not present.</li>
	 * </ul>
	 * 
	 * @param options A dictionary of properties.
	 * 
	 * @return True, when the properties are valid; otherwise, false.
	 */
	protected static boolean validateOptions(Properties options)
	{
		String mode=null, batch=null;
		if ( options.isEmpty() || options.containsKey("help") )
			return false;
		Iterator<Object> keys = options.keySet().iterator();
		while ( keys.hasNext() )
		{
			String key = (String) keys.next();
			if ( !(key.equalsIgnoreCase("mode") || key.equalsIgnoreCase("batch")) )
				return false;
		}
		if ( options.containsKey("mode") )
		{
			mode = options.getProperty("mode");
			if ( !("cli".equalsIgnoreCase(mode) || "gui".equalsIgnoreCase(mode) || "tui".equalsIgnoreCase(mode)) )
				return false;
		}
		else
			options.put("mode", mode="cli");
		if ( options.containsKey("batch") )
		{
			batch = options.getProperty("batch");
			if ( (batch == null) || batch.isEmpty() )
				return false;
		}
		if ( "cli".equalsIgnoreCase(mode) && (batch == null) )
			return false;
		// AOK
		return true;
	}

	/**
	 * Handle throwable exceptions by 1) printing the stack trace to the
	 * standard error stream and 2) in a windowing system, display the stack
	 * trace in a message dialog.
	 * 
	 * @param t A throwable exception
	 */
	protected static void errorHandler(Throwable t)
	{
		// Print stack trace to console.
		t.printStackTrace();
		
		// Display stack trace to the desktop, when available.
		if ( !GraphicsEnvironment.isHeadless() )
		{
			// Create and configure a text area - fill it with exception text.
			final JTextArea textArea = new JTextArea();
			textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
			textArea.setEditable(false);
			StringWriter writer = new StringWriter();
			t.printStackTrace(new PrintWriter(writer));
			textArea.setText(writer.toString());
			
			// Stuff it in a scrollpane with a controlled size.
			JScrollPane scrollPane = new JScrollPane(textArea);
			scrollPane.setPreferredSize(new Dimension(600, 360));
			
			// Pass the scrollpane to the joptionpane.
			String title = Main.class.getSimpleName() + " Exception";
			JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.ERROR_MESSAGE);
		}
	}

	private static ModeType mode;
	/**
	 * Get the user interface mode: GUI, TUI or CLI. The default
	 * mode is CLI.
	 * 
	 * @return The user interface mode.
	 */
	protected static ModeType getMode()
	{
		if ( mode == null )
		{
			String modeOption = options.getProperty("mode");
			if ( "gui".equalsIgnoreCase(modeOption) )
				mode = ModeType.GUI;
			else if ( "tui".equalsIgnoreCase(modeOption) )
				mode = ModeType.TUI;
			else
				mode = ModeType.CLI;
		}
		return mode;
	}
	/**
	 * Set the user interface mode.
	 * 
	 * @param mode The user interface mode: GUI, TUI or CLI.
	 */
	protected static void setMode(ModeType mode)
	{
		Main.mode = mode;
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
