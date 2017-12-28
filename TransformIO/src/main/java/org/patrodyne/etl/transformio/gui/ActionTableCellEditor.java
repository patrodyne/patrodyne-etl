// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 * An abstract implementation of the ActionListener and TableCellEditor interfaces.
 * Concrete sub-classes implement methods to edit a table cell value in response to
 * an action event (button click).
 * 
 * Typically, concrete implementations of this class are used to edit values of
 * the NameValueTable by providing a special purpose property panel.
 * 
 * This class provides a method to return a table cell editor component
 * containing a table cell editor and a button used to display a value with
 * a button to act on the value.
 * 
 * @author Rick O'Sullivan
 */
abstract public class ActionTableCellEditor
	implements ActionListener, TableCellEditor
{
	private JButton actionButton = new JButton("\u22EF");
	private TableCellEditor editor;
	protected JTable table;
	protected int row, column;
	
	/**
	 * Construct with a cell editor, table, row index and column index.
	 * 
	 * @param editor The backing cell editor.
	 * @param table The table containing the cell to edit.
	 * @param row The row index of the cell to edit.
	 * @param column The column index of the cell to edit.
	 */
	public ActionTableCellEditor(TableCellEditor editor, JTable table, int row, int column)
	{
		this.editor = editor;
		this.table = table;
		this.row = row;
		this.column = column;
		
		actionButton.setFocusable(false);
		actionButton.setFocusPainted(false);
		actionButton.setMargin(new Insets(0, 0, 0, 0));	
		actionButton.addActionListener(this);
	}

	/**
	 * Edit the cell at the given row and column of the given table.
	 * 
	 * @param table The table to edit.
	 * @param row The row to edit.
	 * @param column The column to edit.
	 */
	abstract protected void editCell(JTable table, int row, int column);

	/** @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		editor.cancelCellEditing();
		editCell(table, row, column);
	}

	/**
	 * Get the table cell editor component using the predefined table, 
	 * row and column properties plus the given parameters. 
	 * 
	 * @param value The value to edit.
	 * @param isSelected A flag indicating when the cell is selected.
	 * 
	 * @return the component to display a value and an action button.
	 */
	public Component getTableCellEditorComponent(Object value, boolean isSelected)
	{
		return getTableCellEditorComponent(table, value, isSelected, row, column);
	}
	
	/** @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int) */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		this.table = table;
		this.row = row;
		this.column = column;
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(editor.getTableCellEditorComponent(table, value, isSelected, row, column));
		panel.add(actionButton, BorderLayout.EAST);
		return panel;
	}

	/** @see javax.swing.CellEditor#getCellEditorValue() */
	@Override
	public Object getCellEditorValue()
	{
		return editor.getCellEditorValue();
	}

	/** @see javax.swing.CellEditor#isCellEditable(java.util.EventObject) */
	@Override
	public boolean isCellEditable(EventObject anEvent)
	{
		return editor.isCellEditable(anEvent);
	}

	/** @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject) */
	@Override
	public boolean shouldSelectCell(EventObject anEvent)
	{
		return editor.shouldSelectCell(anEvent);
	}

	/** @see javax.swing.CellEditor#stopCellEditing() */
	@Override
	public boolean stopCellEditing()
	{
		return editor.stopCellEditing();
	}

	/** @see javax.swing.CellEditor#cancelCellEditing() */
	@Override
	public void cancelCellEditing()
	{
		editor.cancelCellEditing();
	}

	/** @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener) */
	@Override
	public void addCellEditorListener(CellEditorListener l)
	{
		editor.addCellEditorListener(l);
	}

	/** @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener) */
	@Override
	public void removeCellEditorListener(CellEditorListener l)
	{
		editor.removeCellEditorListener(l);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
