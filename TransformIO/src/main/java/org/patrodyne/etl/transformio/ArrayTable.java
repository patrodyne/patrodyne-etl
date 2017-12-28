// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio;

import java.util.ArrayList;

/**
 * A two-dimensional dynamic array.
 * 
 * @author Rick O'Sullivan
 */
public class ArrayTable<T>
{
	private ArrayList<ArrayList<T>> table;

	/**
	 * Constructs an empty table with an initial capacity of ten.
	 */
	public ArrayTable()
	{
		this(10,10);
	}

	/**
	 * Constructs a table with the given row and column capacity.
	 * @param rowCapacity the row capacity.
	 * @param colCapacity the column capacity.
	 */
	public ArrayTable(int rowCapacity, int colCapacity)
	{
		table = new ArrayList<ArrayList<T>>(rowCapacity);
		for ( int row=0; row < rowCapacity; ++row)
			table.add(new ArrayList<T>(colCapacity));
	}
	
	public T get(int row, int col)
	{
		return table.get(row).get(col);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
