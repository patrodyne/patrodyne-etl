// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.tui;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * A textual clipboard.
 * 
 * @author Rick O'Sullivan
 */
public class TextualClipboard
{
	private String clip = "";
	
	/**
	 * Gets the clip.
	 *
	 * @return the clip
	 */
	protected String getClip() 
	{
		if ( GraphicsEnvironment.isHeadless() )
			return clip; 
		else
		{
			try
			{
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable contents = clipboard.getContents(null);
				return (String) contents.getTransferData(DataFlavor.stringFlavor);
			}
			catch (Exception ex)
			{
				return "";
			}
		}
	}
	/**
	 * Sets the clip.
	 *
	 * @param clip the new clip
	 */
	protected void setClip(String clip) 
	{ 
		if ( GraphicsEnvironment.isHeadless() )
			this.clip = clip;
		else
		{
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(clip), null);
		}
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
