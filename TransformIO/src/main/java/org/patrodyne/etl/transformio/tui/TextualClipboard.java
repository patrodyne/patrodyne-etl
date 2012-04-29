// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.tui;

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
	protected String getClip() { return clip; }
	/**
	 * Sets the clip.
	 *
	 * @param clip the new clip
	 */
	protected void setClip(String clip) { this.clip = clip;	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
