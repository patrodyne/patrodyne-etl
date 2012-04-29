// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.tui;

import charva.awt.Point;
import charva.awt.Toolkit;
import charva.awt.event.ActionEvent;
import charva.awt.event.ActionListener;
import charvax.swing.JMenuItem;

/**
 * A menu item that can be selected or deselected. If selected, the menu item
 * typically appears with a checkmark next to it. If unselected or deselected,
 * the menu item appears without a checkmark.
 * 
 * @author Rick O'Sullivan
 */
public class JCheckBoxMenuItem
	extends JMenuItem
{
	/**
	 * Instantiates a new JCheck box menu item.
	 */
	public JCheckBoxMenuItem()
	{
		super();
		initialize();
	}
	
	/**
	 * Instantiates a new JCheck box menu item.
	 *
	 * @param text the text for the menu item.
	 */
	public JCheckBoxMenuItem(String text)
	{
		super(text);
		initialize();
	}
	
	/**
	 * Instantiates a new JCheck box menu item.
	 *
	 * @param text the text for the menu item.
	 * @param mnemonic the mnemonic for the menu item.
	 */
	public JCheckBoxMenuItem(String text, int mnemonic)
	{
		super(text, mnemonic);
		initialize();
	}

	private void initialize()
	{
		addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent ae_)
				{
					toggleSelected();
				}
			}
		);
	}
	
	private boolean selected;
	
	/**
	 * Is the menu item selected.
	 * @see charvax.swing.AbstractButton#isSelected()
	 */
	public boolean isSelected()
	{
		return selected;
	}
	
	/**
	 * Set selected flage for the menu item. 
	 * @see charvax.swing.AbstractButton#setSelected(boolean)
	 */
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}

	private void toggleSelected()
	{
		if ( isEnabled() )
			setSelected(!isSelected());
		else
			setSelected(false);
	}
	
	/**
	 * Set enabled for the menu item.
	 * @see charva.awt.Component#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean flag)
	{
		super.setEnabled(flag);
		if ( !flag )
			setSelected(false);
	}
	
	/**
	 * Draw the menu item.
	 * @see charvax.swing.JMenuItem#draw()
	 */
	@Override
	public void draw()
	{
		/*
		 * Get the absolute origin of this component.
		 */
		Point origin = getLocationOnScreen();
		Toolkit term = Toolkit.getDefaultToolkit();
		term.setCursor(origin);
		int colorpair = getCursesColor();
		int attribute;
		if (!super.isEnabled())
		{
			attribute = Toolkit.A_NORMAL;
			term.addString("<", attribute, colorpair);
			term.addString(super.getText(), attribute, colorpair);
			term.addString(">", attribute, colorpair);
		}
		else
		{
			attribute = (super.hasFocus()) ? Toolkit.A_BOLD : Toolkit.A_NORMAL;
			String checkmark = isSelected() ? "*" : " ";
			term.addString(checkmark, attribute, colorpair);
			term.addString(super.getText(), attribute, colorpair);
			term.addString(" ", attribute, colorpair);
		}
		if (super.getMnemonic() > 0)
		{
			int mnemonicPos = super.getText().indexOf((char) super.getMnemonic());
			if (mnemonicPos != -1)
			{
				term.setCursor(origin.addOffset(1 + mnemonicPos, 0));
				term.addChar(super.getMnemonic(), attribute | Toolkit.A_UNDERLINE, colorpair);
			}
		}
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
