// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.patrodyne.etl.transformio.xml.Locator;

/**
 * Implementation of ActionTableCellEditor to edit a Locator (URL).
 * 
 * @author Rick O'Sullivan
 */
public class LocatorCellEditor
	extends ActionTableCellEditor
{
	/**
	 * Construct with a cell editor, table, row index and column index.
	 * 
	 * @param editor The backing cell editor.
	 * @param table The table containing the cell to edit.
	 * @param row The row index of the cell to edit.
	 * @param column The column index of the cell to edit.
	 */
	public LocatorCellEditor(TableCellEditor editor, JTable table, int row, int column)
	{
		super(editor, table, row, column);
	}

	/** @see org.patrodyne.etl.transformio.gui.ActionTableCellEditor#editCell(javax.swing.JTable, int, int) */
	@Override
	protected void editCell(JTable table, int row, int column)
	{
		LocatorPanel locatorPanel = 
			new LocatorPanel((Locator) table.getValueAt(row, column));
		int result = JOptionPane.showOptionDialog
		(
			table, 
			locatorPanel,
			"Locator", 
			JOptionPane.OK_CANCEL_OPTION, 
			JOptionPane.PLAIN_MESSAGE, 
			null, null, null
		);
		if (result == JOptionPane.OK_OPTION)
			table.setValueAt(locatorPanel.getLocator(), row, column);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
