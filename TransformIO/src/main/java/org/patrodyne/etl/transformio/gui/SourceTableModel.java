// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import java.math.BigInteger;

import org.patrodyne.etl.transformio.MessageType;
import org.patrodyne.etl.transformio.Transformer;
import org.patrodyne.etl.transformio.xml.Batch;
import org.patrodyne.etl.transformio.xml.Locator;

/**
 * A table model that delegates to a Batch.Source instance.
 * 
 * This model exposes selected properties of Batch.Source as
 * a small table of name-value pairs.
 * 
 * @author Rick O'Sullivan
 */
@SuppressWarnings("serial")
public class SourceTableModel
	extends NameValueTableModel
{
	public static final String NAME_CHARSET_ENCODING = "Charset Encoding";
	public static final String NAME_BUFFER_SIZE = "Buffer Size";
	public static final String NAME_LOCATOR = "Locator";
	private static final String[] RowNames =
	{
	 	NAME_CHARSET_ENCODING,
	 	NAME_BUFFER_SIZE,
	 	NAME_LOCATOR
	};
	private Batch.Source source;
	
	/** Construct with a Batch.Source instance. */
	public SourceTableModel(Batch.Source source)
	{
		this.source = source; 
	}

	/** @see org.patrodyne.etl.transformio.gui.NameValueTableModel#getRowName(int) */
	public String getRowName(int row)
	{
		return RowNames[row];
	}
	
	/** @see javax.swing.table.TableModel#getRowCount() */
	@Override
	public int getRowCount()
	{
		return RowNames.length;
	}

	/** @see javax.swing.table.TableModel#getValueAt(int, int) */
	@Override
	public Object getValueAt(int row, int col)
	{
		switch (row)
		{
			case 0: return (col == 0) ? getRowName(0) : source.getCharset();
			case 1: return (col == 0) ? getRowName(1) : source.getBufferSize().toString();
			case 2: return (col == 0) ? getRowName(2) : source.getLocator();
			default: return null;
		}
	}

	/** @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int) */
	public void setValueAt(Object value, int row, int col)
	{
		if (col == 1)
		{
			try
			{
				switch (row)
				{
					case 0: source.setCharset((String)value); break;
					case 1: source.setBufferSize(new BigInteger((String)value)); break;
					case 2:
						if ( value instanceof Locator )
							source.setLocator((Locator)value);
						else
						{
							Locator locator = new Locator();
							locator.setUrl((String)value);
							source.setLocator(locator);
						}
						break;
				}
				fireTableCellUpdated(row, col);
			}
			catch (Exception ex)
			{
				Transformer.notification(MessageType.WARN, ex);
			}
		}
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
