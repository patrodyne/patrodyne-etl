// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.cli;

import java.io.FileReader;
import java.util.Map.Entry;
import java.util.Properties;

import org.patrodyne.etl.transformio.Transformer;
import org.patrodyne.etl.transformio.UserInterface;
import org.patrodyne.etl.transformio.xml.Batch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command Line Interface for TransformIO. This class provides
 * methods to start a {@link Batch} job and show text the console.
 * 
 * @author Rick O'Sullivan
 */
public class CommandLI extends Transformer implements UserInterface
{
	/** Represents a SLF4J logger interface. */
	private static Logger log = LoggerFactory.getLogger(CommandLI.class);
	/** Implementation of abstract method from {@link Transformer}. */
	@Override
	protected Logger log() { return log; }
	
	/**
	 * <p>Starts the command line user interface. Unmarshals a batch file to
	 * a {@link Batch} instance then  invokes {@link #execute(Batch)} to
	 * perform an extract, transform and load operation.</p>
	 * 
	 * <p>Exceptions are trapped and handled by {@link #notification(Exception)}.</p>
	 * 
	 * @param options A dictionary of command line arguments.
	 */
	public static void start(Properties options)
	{
		show("options: "+about());
		showOptions(options);
		CommandLI commandLI = new CommandLI();
		try
		{
			FileReader batchReader = new FileReader(options.getProperty("batch"));
			Batch batch = unmarshalBatch(batchReader);
			commandLI.execute(batch, true);
		}
		catch (Exception e)
		{
			commandLI.notification(e);
		}
	}

	/**
	 * <p>Print the options for this application. Options are echoed to
	 * confirm the invocation plan.</p>
	 * 
	 * @param options A dictionary of command line arguments.
	 * 
	 * @see #show(Object)
	 */
	public static void showOptions(Properties options)
	{
		for ( Entry<Object, Object> option : options.entrySet())
		{
			if ( option.getValue() != null )
				show(option.getKey()+"="+option.getValue());
			else
				show(option.getKey());
		}
	}
	
	/**
	 * <p>Show the usage for this program. Provides a brief explanation
	 * of the command line interface.</p>
	 * 
	 * @see #show(String)
	 */
	public static void showUsage()
	{
		show("Transform input data into output data.");
		show();
		show("usage: " + PROGRAM_NAME + " [options]");
		show();
		show("options:");
        show("  help - show this information.");
        show("  mode=gui|tui|cli");
        show("  batch=filename");
	}

	/**
	 * <p>Show an object to the console. Converts an object to its
	 * standard string representation an invokes {@link CommandLI#show(String)}.</p>
	 * 
	 * <p>Null objects are ignored.</p>
	 * 
	 * @param obj The object to show.
	 */
	public static void show(Object obj)
	{
		if ( obj != null )
			show(obj.toString());
	}
	
	/** Show empty text. This usually results with a blank line. */
	public static void show() { show(""); }

	/**
	 * <p>Show text to the console. Uses {@link log} to output a message at
	 * the INFO level. Typically, the logger is configured to send informational
	 * messages to the console but may be configured to append messages to a log
	 * file for scheduled jobs.</p>
	 * 
	 * @param text The string to output.
	 */
	public static void show(String text)
	{
		log.info(text);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
