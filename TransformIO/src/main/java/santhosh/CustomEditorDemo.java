package santhosh;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class CustomEditorDemo
	extends JFrame
{
	public CustomEditorDemo()
	{
		super("CustomEditor - santhosh@in.fiorano.com");
		JTable table = new JTable
		(
			new Object[][] 
			{
			 	{ "JavaTutorial", "This is sun's java tutorial.\nthis is best for beginners" },
				{ "JavaSwing", "This is book dedicated to swing.\nIt covers in-depth knowledge of swing" },
				{ "", "" }
			},
			new String[] { "Title of Book", "Description of Book" }
		);
		JTextField textField = new JTextField();
		textField.setBorder(BorderFactory.createEmptyBorder());
		DefaultCellEditor editor = new DefaultCellEditor(textField);
		editor.setClickCountToStart(1);
		table.getColumn(table.getColumnName(0)).setCellEditor(editor);
		table.getColumn(table.getColumnName(1)).setCellEditor(new StringActionTableCellEditor(editor));
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		getContentPane().add(scroll);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		new CustomEditorDemo().setVisible(true);
	}
}