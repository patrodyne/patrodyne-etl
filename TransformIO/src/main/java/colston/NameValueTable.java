package colston;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class NameValueTable
	extends JTable
{
	protected ValueCellEditorModel valueCellEditorModel;
	
	public NameValueTable()
	{
		valueCellEditorModel = null;
	}

	public NameValueTable(TableModel dm)
	{
		super(dm);
		valueCellEditorModel = null;
	}

	public NameValueTable(TableModel dm, TableColumnModel cm)
	{
		super(dm, cm);
		valueCellEditorModel = null;
	}

	public NameValueTable(int numRows, int numColumns)
	{
		super(numRows, numColumns);
		valueCellEditorModel = null;
	}

	@SuppressWarnings("rawtypes")
	public NameValueTable(Vector rowData, Vector columnNames)
	{
		super(rowData, columnNames);
		valueCellEditorModel = null;
	}

	public NameValueTable(Object[][] rowData, Object[] columnNames)
	{
		super(rowData, columnNames);
		valueCellEditorModel = null;
	}

	public NameValueTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm)
	{
		super(dm, cm, sm);
		valueCellEditorModel = null;
	}

	// new constructor
	public NameValueTable(TableModel tm, ValueCellEditorModel valueEditorModel)
	{
		super(tm,null,null);
		this.valueCellEditorModel = valueEditorModel;
	}

	public ValueCellEditorModel getValueCellEditorModel()
	{
		return valueCellEditorModel;
	}

	public void setValueCellEditorModel(ValueCellEditorModel valueEditorModel)
	{
		this.valueCellEditorModel = valueEditorModel;
	}

	public TableCellEditor getCellEditor(int row, int col)
	{
		TableCellEditor tmpEditor = null;
		
		if (valueCellEditorModel!=null)
			tmpEditor = valueCellEditorModel.getEditor(row);
		
		if (tmpEditor!=null)
			return tmpEditor;
		
		return super.getCellEditor(row,col);
	}
}

// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
