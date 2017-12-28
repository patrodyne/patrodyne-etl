// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * A JTable for name-value pairs (a property table).
 * @author Rick O'Sullivan
 */
@SuppressWarnings("serial")
abstract public class NameValueTable
	extends JTable
{
	/**
	 * Construct with a TableModel for name-value pairs.
	 * @param nameValueTableModel
	 */
	public NameValueTable(NameValueTableModel nameValueTableModel)
	{
		super(nameValueTableModel);
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		// Focus on first value column when name column is selected.
		// This ensures the enter and tab keys move to the next 
		// row in the value column.
		getSelectionModel().addListSelectionListener
		(
			new ListSelectionListener()
			{
				@Override
				public void valueChanged(ListSelectionEvent e)
				{
					NameValueTable nvt = NameValueTable.this;
					if ( nvt.getSelectedColumn() == 0 )
						nvt.changeSelection(nvt.getSelectedRow(), 1, false, false);
				}
			}
		);
	}

	private TableCellEditor[][] cellEditors;
	protected TableCellEditor[][] getCellEditors()
	{
		if ( cellEditors == null )
		{
			cellEditors = new TableCellEditor[getRowCount()][getColumnCount()];
			for (int rowIndex=0; rowIndex < getRowCount(); ++rowIndex)
			{
				for (int colIndex=0; colIndex < getColumnCount(); ++colIndex)
				{
					if ( colIndex == 0 )
						cellEditors[rowIndex][colIndex] = super.getCellEditor(rowIndex,colIndex);
					else
						cellEditors[rowIndex][colIndex] = newTextCellEditor();
				}
			}
		}
		return cellEditors;
	}
	
	/**
	 * Get a custom cell editor for each row (property).
	 * A NameValueTable is a property table with two or more columns, 
	 * Name (col=0) and Value (col>0), where only the Value column(s)
	 * are editable.
	 * @see javax.swing.JTable#getCellEditor(int, int)
	 */
	public TableCellEditor getCellEditor(int row, int col)
	{
		return getCellEditors()[row][col];
	}

	/**
	 * Create a new text cell editor.
	 * @return A new text cell editor.
	 */
	protected DefaultCellEditor newTextCellEditor()
	{
		JTextField textField = new JTextField();
		GraphicalUI.addTextPopupMenu(textField);
		DefaultCellEditor dce = new DefaultCellEditor(textField);
		dce.setClickCountToStart(1);
		return dce;
	}
	
	/**
	 * Set the cell editor at the specified row and column index.
	 * @param rowIndex The row index position.
	 * @param colIndex The column index position.
	 * @param cellEditor The cell editor to set.
	 */
	public void setCellEditor(int rowIndex, int colIndex, TableCellEditor cellEditor)
	{
		getCellEditors()[rowIndex][colIndex] = cellEditor;
	}

	private TableCellRenderer[][] cellRenderers;
	protected TableCellRenderer[][] getCellRenderers()
	{
		if ( cellRenderers == null )
		{
			cellRenderers = new TableCellRenderer[getRowCount()][getColumnCount()];
			for (int rowIndex=0; rowIndex < getRowCount(); ++rowIndex)
			{
				for (int colIndex=0; colIndex < getColumnCount(); ++colIndex)
				{
					if ( colIndex == 0)
						cellRenderers[rowIndex][colIndex] = super.getCellRenderer(rowIndex,colIndex);
					else
						cellRenderers[rowIndex][colIndex] = new DefaultTableCellRenderer();
				}
			}
		}
		return cellRenderers;
	}
	
	/**
	 * Get a custom cell renderer for each row (property).
	 * A NameValueTable is a property table with two or more columns, 
	 * Name (col=0) and Value (col>0), where only the Value column(s)
	 * are mutable.
	 * @see javax.swing.JTable#getCellRenderer(int, int)
	 */
	public TableCellRenderer getCellRenderer(int row, int col)
	{
		return getCellRenderers()[row][col];
	}
	
	/**
	 * Set the cell renderer at the specified row and column index.
	 * @param rowIndex The row index position.
	 * @param colIndex The column index position.
	 * @param cellRenderer The cell renderer to set.
	 */
	public void setCellRenderer(int rowIndex, int colIndex, TableCellRenderer cellRenderer)
	{
		getCellRenderers()[rowIndex][colIndex] = cellRenderer;
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
