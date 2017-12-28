// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import java.awt.Color;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.patrodyne.etl.transformio.Transformer;

/**
 * A NameValueTable for editing source record properties.
 * 
 * @author Rick O'Sullivan
 */
@SuppressWarnings("serial")
public class SourceTable
	extends NameValueTable
{
	private static final String[] CHARSET_ENCODINGS = new String[]
	{
	 "UTF-8", "UTF-16BE", "UTF-16LE",
	 "US-ASCII", "ISO-8859-1", "WINDOWS-1252"
	};
	
	private static final String[] BUFFER_SIZES =
		new String[] { "256", "512", "1024", "2048", "4096", "8192" };
	
	private static int VALUE_COL_INDEX = 1;

	/**
	 * Default constructor, normally used for GUI design.
	 */
	public SourceTable()
	{
		this(new SourceTableModel(Transformer.newBatch().getSource()));
	}
	
	/**
	 * Construct with a SourceTableModel instance.
	 * @param model A TableModel with source properties to edit.
	 */
	public SourceTable(SourceTableModel model)
	{
		super(model);
		// Table Cell Editor: Charset Encoding
		int charsetEncodingRowIndex = model.getRowIndex(SourceTableModel.NAME_CHARSET_ENCODING);
		setCellEditor(charsetEncodingRowIndex, VALUE_COL_INDEX, getCharsetEncodingEditor());

		// Table Cell Editor: Buffer Size
		int bufferSizeRowIndex = model.getRowIndex(SourceTableModel.NAME_BUFFER_SIZE);
		setCellEditor(bufferSizeRowIndex, VALUE_COL_INDEX, getBufferSizeEditor());
		
		// Table Cell Editor: Locator
		int locatorRowIndex = model.getRowIndex(SourceTableModel.NAME_LOCATOR);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TableCellEditor getBufferSizeEditor()
	{
		JComboBox comboBox = new JComboBox(BUFFER_SIZES);
		comboBox.setEditable(true);
		return new DefaultCellEditor(comboBox);
	}

	private TableCellEditor getLocatorEditor(int locatorRowIndex)
	{
		TableCellEditor tce = newTextCellEditor();
		return new LocatorCellEditor(tce, this, locatorRowIndex, VALUE_COL_INDEX);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
