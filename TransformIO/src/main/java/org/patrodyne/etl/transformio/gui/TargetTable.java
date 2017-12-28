// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.patrodyne.etl.transformio.Transformer;

/**
 * A NameValueTable for editing target record properties.
 * 
 * @author Rick O'Sullivan
 */
@SuppressWarnings("serial")
public class TargetTable
	extends NameValueTable
{
	private static final String[] CHARSET_ENCODINGS = new String[]
	{
	 "UTF-8", "UTF-16BE", "UTF-16LE",
	 "US-ASCII", "ISO-8859-1", "WINDOWS-1252"
	};

	private static int VALUE_COL_INDEX = 1;
	
	/**
	 * Default constructor, normally used for GUI design.
	 */
	public TargetTable()
	{
		this(new TargetTableModel(Transformer.newBatch().getTarget()));
	}
	
	/**
	 * Construct with a TargetTableModel instance.
	 * @param model A TableModel with target properties to edit.
	 */
	public TargetTable(TargetTableModel model)
	{
		super(model);
		// Charset Encoding Table Cell Editor
		int charsetEncodingRowIndex = model.getRowIndex(TargetTableModel.NAME_CHARSET_ENCODING);
		setCellEditor(charsetEncodingRowIndex, VALUE_COL_INDEX, getCharsetEncodingEditor());

		// Buffer Size Table Cell Editor
		int bufferSizeRowIndex = model.getRowIndex(TargetTableModel.NAME_BYTE_ORDER_MARK);
		final JCheckBox checkBox = new JCheckBox();
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
		checkBox.setBackground(Color.white);
		setCellRenderer(bufferSizeRowIndex, VALUE_COL_INDEX, getByteOrderMarkRenderer(checkBox));
		setCellEditor(bufferSizeRowIndex, VALUE_COL_INDEX, getByteOrderMarkEditor(checkBox));
		
		// Table Cell Editor: Locator
		int locatorRowIndex = model.getRowIndex(TargetTableModel.NAME_LOCATOR);
		setCellEditor(locatorRowIndex, VALUE_COL_INDEX, getLocatorEditor(locatorRowIndex));

		// Set renderer foreground color for values.
		for (int rowIndex=0; rowIndex < getRowCount(); ++rowIndex)
		{
			TableCellRenderer tcr = getCellRenderer(rowIndex, VALUE_COL_INDEX);
			if ( tcr instanceof DefaultTableCellRenderer )
			{
				DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) tcr;
				renderer.setForeground(Color.BLUE);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TableCellEditor getCharsetEncodingEditor()
	{
		JComboBox comboBox = new JComboBox(CHARSET_ENCODINGS);
		comboBox.setEditable(true);
		return new DefaultCellEditor(comboBox);
	}

	private TableCellRenderer getByteOrderMarkRenderer(final JCheckBox checkBox)
	{
		return new DefaultTableCellRenderer()
		{
		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		    {
		    	checkBox.setSelected(((Boolean)value).booleanValue()) ;
		    	return checkBox;
		    }
		};
	}

	private TableCellEditor getByteOrderMarkEditor(final JCheckBox checkBox)
	{
		return new DefaultCellEditor(checkBox);
	}

	private TableCellEditor getLocatorEditor(int locatorRowIndex)
	{
		TableCellEditor tce = newTextCellEditor();
		return new LocatorCellEditor(tce, this, locatorRowIndex, VALUE_COL_INDEX);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
