import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class CustomTableEditor
	extends JFrame
{
	DefaultTableModel model = new DefaultTableModel
	(
		new Object[][] 
		{
	 		{ "some", "text" }, { "any", "text" },
	 		{ "even", "more" }, { "text", "strings" },
	 		{ "and", "other" }, { "text", "values" }
		},
		new Object[] { "Column 1", "Column 2" }
	);

	public CustomTableEditor()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTable table = new JTable(model);
		
		table.setDefaultEditor(Object.class, new MyEditor());
		
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		pack();
	}

	public static void main(String arg[])
	{
		new CustomTableEditor().setVisible(true);
	}
}

@SuppressWarnings("serial")
class MyEditor
	extends DefaultCellEditor
{
	public MyEditor()
	{
		super(new JTextField());
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, 
		int row, int column)
	{
		JTextField editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, 
			row, column);
		
		if (value != null)
			editor.setText(value.toString());
		
		if (column == 0)
		{
			editor.setHorizontalAlignment(SwingConstants.CENTER);
			editor.setFont(new Font("Serif", Font.BOLD, 14));
		}
		else
		{
			editor.setHorizontalAlignment(SwingConstants.RIGHT);
			editor.setFont(new Font("Serif", Font.ITALIC, 12));
		}
		
		return editor;
	}
}
