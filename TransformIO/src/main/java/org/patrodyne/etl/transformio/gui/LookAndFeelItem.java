// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Wrap a LookAndFeelInfo and use its name as this toString.
 * 
 * @author Rick O'Sullivan
 */
public class LookAndFeelItem
{
	private LookAndFeelInfo lookAndFeelInfo;
	
	/**
	 * Get a little information about the installed LookAndFeel.
	 *
	 * @return the look and feel info
	 */
	protected LookAndFeelInfo getLookAndFeelInfo()
	{
		return lookAndFeelInfo;
	}
	private void setLookAndFeelInfo(LookAndFeelInfo lookAndFeelInfo)
	{
		this.lookAndFeelInfo = lookAndFeelInfo;
	}
	
	/**
	 * Represent this item using look and feel name.
	 *
	 * @return the string
	 */
	@Override
	public String toString()
	{
		return getLookAndFeelInfo().getName();
	}
	
	/**
	 * Construct with a LookAndFeelInfo.
	 * 
	 * @param lookAndFeelInfo A look and feel info object.
	 */
	public LookAndFeelItem(LookAndFeelInfo lookAndFeelInfo)
	{
		super();
		setLookAndFeelInfo(lookAndFeelInfo);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
