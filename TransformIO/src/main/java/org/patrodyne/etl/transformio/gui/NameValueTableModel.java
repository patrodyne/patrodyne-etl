// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

/**
 * An abstract table model for name-value pairs.
 * 
 * This model exposes selected properties as a small 
 * table of name-value pairs.
 * 
 * @author Rick O'Sullivan
 */
@SuppressWarnings("serial")
abstract public class NameValueTableModel
	extends AbstractTableModel
{
	private static final String[] DEFAULT_COLUMN_NAMES = { "Name", "Value" };
	private String[] colNames = DEFAULT_COLUMN_NAMES;
	
	/**
	 * Construct with default column names.
	 */
	public NameValueTableModel()
	{
		this(DEFAULT_COLUMN_NAMES);
	}

	/**
	 * Construct with an array of column names.
	 * @param colNames an array of column names.
	 */
	public NameValueTableModel(String[] colNames)
	{
		super();
		this.colNames = (colNames != null) ? colNames : DEFAULT_COLUMN_NAMES;
	}

	/** @see javax.swing.table.TableModel#getColumnCount() */
	@Override
	public int getColumnCount()
	{
		return colNames.length;
	}
	
	/** 
	 * A name for the column using name-value pair conventions.
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int col)
	{
		return col < colNames.length ? colNames[col] : "";
	}
	
	/** @see javax.swing.table.AbstractTableModel#isCellEditable(int, int) */
	@Override
	public boolean isCellEditable(int row, int col)
	{
		return (col > 0);
	}
	
	/**
	 * The row name for each property in the table.
	 * Subclasses provide their custom property names by
	 * implementing this abstract accessor.
	 *  
	 * @param row The row index, zero based.
	 * @return The property name for the row.
	 */
	abstract public String getRowName(int row);
	
	private Map<String, Integer> rowIndexes;
	/**
	 * The row index for each property in the table.
	 * Subclasses provide their custom property 
	 * indexes by overriding accessor.
	 *  
	 * @param name The property name.
	 * @return The row index for the given name.
	 */
	public int getRowIndex(String name)
	{
		if (rowIndexes == null)
		{
			rowIndexes = new HashMap<String, Integer>(getRowCount());
			for(int rowIndex=0; rowIndex < getRowCount(); ++rowIndex)
				rowIndexes.put(getRowName(rowIndex), rowIndex);
		}
		return rowIndexes.get(name);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
