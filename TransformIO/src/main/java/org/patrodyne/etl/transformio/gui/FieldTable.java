// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.patrodyne.etl.transformio.Transformer;

/**
 * A NameValueTable for editing source and target record field properties.
 * 
 * @author Rick O'Sullivan
 */
@SuppressWarnings("serial")
public class FieldTable
	extends NameValueTable
{
	private static int SOURCE_COL_INDEX = 1;
	private static int TARGET_COL_INDEX = 2;

	/**
	 * Default constructor, normally used for GUI design.
	 */
	public FieldTable()
	{
		this(new FieldTableModel(Transformer.newBatch()));
	}
	
	/**
	 * Construct with a FieldTableModel instance.
	 * @param model A TableModel with source and target properties to edit.
	 */
	public FieldTable(FieldTableModel model)
	{
		super(model);
		// Set renderer style for source  and target values.
		for (int rowIndex=0; rowIndex < getRowCount(); ++rowIndex)
		{
			setRendererStyle(rowIndex, SOURCE_COL_INDEX);
			setRendererStyle(rowIndex, TARGET_COL_INDEX);
		}
	}

	/**
	 * Set renderer foreground color for values.
	 * @param rowIndex The row index of the cell renderer.
	 * @param colIndex The column index of the cell renderer.
	 */
	protected void setRendererStyle(int rowIndex, int colIndex)
	{
		TableCellRenderer tcr = getCellRenderer(rowIndex, colIndex);
		if ( tcr instanceof DefaultTableCellRenderer )
		{
			DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) tcr;
			renderer.setForeground(Color.BLUE);
		}
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
