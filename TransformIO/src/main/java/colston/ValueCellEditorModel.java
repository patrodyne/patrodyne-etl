package colston;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.TableCellEditor;

public class ValueCellEditorModel
{
	private Map<Integer, TableCellEditor> tableCellEditors;
	
	public ValueCellEditorModel()
	{
		tableCellEditors = new HashMap<Integer, TableCellEditor>();
	}
	
	public void addEditorForCellValue(int row, TableCellEditor tableCellEditor)
	{
		tableCellEditors.put(new Integer(row), tableCellEditor);
	}
	
	public void removeEditorForCellValue(int row)
	{
		tableCellEditors.remove(row);
	}
	
	public TableCellEditor getEditor(int row)
	{
		return tableCellEditors.get(row);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
