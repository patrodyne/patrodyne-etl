// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import org.patrodyne.etl.transformio.MessageType;
import org.patrodyne.etl.transformio.Transformer;
import org.patrodyne.etl.transformio.xml.Batch;
import org.patrodyne.etl.transformio.xml.Record;
import org.patrodyne.etl.transformio.xml.Record.Field;

/**
 * A table model that delegates to a Batch.Source and Batch.Target instances
 * to edit field attributes.
 * 
 * This model exposes selected properties of Batch.Source and Batch.Target as
 * a small table of name-value tuples.
 * 
 * @author Rick O'Sullivan
 */
@SuppressWarnings("serial")
public class FieldTableModel
	extends NameValueTableModel
{
	private static final String[] ColNames =
	{
	 	"Attribute",
	 	"Source",
	 	"Target"
	};
	public static final String ATTRIBUTE_NAME = "Name";
	public static final String ATTRIBUTE_GET = "Get";
	public static final String ATTRIBUTE_SET = "Set";
	private static final String[] RowNames =
	{
	 	ATTRIBUTE_NAME,
	 	ATTRIBUTE_GET,
	 	ATTRIBUTE_SET
	};
	private Batch.Source source;
	private Batch.Target target;
	
	private String fieldName;
	/**
	 * Get the current field name.
	 * @return the fieldName
	 */
	public String getFieldName()
	{
		return fieldName;
	}
	/**
	 * Set the current field name.
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
	}

	/** Construct with a Batch instance. */
	public FieldTableModel(Batch batch)
	{
		super(ColNames);
		this.source = batch.getSource(); 
		this.target = batch.getTarget(); 
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

	private Field getField(Record record)
	{
		Field aField = null;
		if ( getFieldName() != null )
		{
			for (Field field : record.getFields())
			{
				if (field.getName().equals(getFieldName()))
				{
					aField = field;
					break;
				}
			}
		}
		return aField;
	}
	
	private Field getSourceField()
	{
		return getField(source.getRecord());
	}
	
	private Field getTargetField()
	{
		return getField(target.getRecord());
	}
	
	/** @see javax.swing.table.TableModel#getValueAt(int, int) */
	@Override
	public Object getValueAt(int row, int col)
	{
		if ( col == 0 )
			return getRowName(row);
		else if ( col == 1 )
		{
			Field sourceField = getSourceField();
			if ( sourceField != null )
			{
				switch (row)
				{
					case 0: return sourceField.getName();
					case 1: return sourceField.getGet();
					case 2: return sourceField.getSet();
					default: return null;
				}
			}
		}
		else if ( col == 2 )
		{
			Field targetField = getTargetField();
			if ( targetField != null )
			{
				switch (row)
				{
					case 0: return targetField.getName();
					case 1: return targetField.getGet();
					case 2: return targetField.getSet();
					default: return null;
				}
			}
		}
		return null;
	}

	/** @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int) */
	public void setValueAt(Object value, int row, int col)
	{
		try
		{
			if (col == 1)
			{
				Field sourceField = getSourceField();
				if ( sourceField != null )
				{
					switch (row)
					{
						case 0: sourceField.setName((String)value); break;
						case 1: sourceField.setGet((String)value); break;
						case 2: sourceField.setSet((String)value); break;
					}
					fireTableCellUpdated(row, col);
				}
			}
			else if (col == 2)
			{
				Field targetField = getTargetField();
				if ( targetField != null )
				{
					switch (row)
					{
						case 0: targetField.setName((String)value); break;
						case 1: targetField.setGet((String)value); break;
						case 2: targetField.setSet((String)value); break;
					}
					fireTableCellUpdated(row, col);
				}
			}
		}
		catch (Exception ex)
		{
			Transformer.notification(MessageType.WARN, ex);
		}
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
