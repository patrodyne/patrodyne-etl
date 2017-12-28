package colston;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.border.*;

@SuppressWarnings("serial")
public class PropertyPanelMain
	extends JPanel
{
	private JComboBox<?> comboBox;
	private NameValueTable nameValueTable;
	private DefaultTableModel nameValueTableModel;
	private String[] col_names = { "Name", "Value" };
	private String[] anchor_values = 
		{ 
		  "CENTER", "NORTH", "NORTHEAST", "EAST", 
		  "SOUTHEAST", "SOUTH", "SOUTHWEST", "WEST", "NORTHWEST"
		};
	private String[] fill_values = { "NONE", "HORIZONTAL", "VERTICAL", "BOTH" };

	private void createGUI()
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		comboBox = new JComboBox<Object>();
		nameValueTableModel = new DefaultTableModel(col_names, 12)
		{
			public String[] prop_names = 
				{ 
				 	"Name", "Anchor", "Fill", "GridHeight", "GridWidth", "GridX", "GridY",
				 	"Insets", "Ipadx", "Ipady", "WeightX", "WeightY"
				};

			public Object getValueAt(int row, int col)
			{
				if (col == 0)
					return prop_names[row];
				return super.getValueAt(row, col);
			}

			public boolean isCellEditable(int row, int col)
			{
				return (col != 0);
			}
		};
		nameValueTable = new NameValueTable(nameValueTableModel);
		nameValueTable.setRowSelectionAllowed(false);
		nameValueTable.setColumnSelectionAllowed(false);
		
		// create a ValueCellEditorModel... this is used to hold the extra
		// information that is needed to deal with row specific editors
		ValueCellEditorModel valueCellEditorModel = new ValueCellEditorModel();
		
		// tell the NameValueTable which ValueCellEditorModel we are using
		nameValueTable.setValueCellEditorModel(valueCellEditorModel);
		
		// create a new JComboBox and DefaultCellEditor to use in the
		// NameValueTable column
		JComboBox<?> anchorComboBox = new JComboBox<Object>(anchor_values);
		DefaultCellEditor anchorCellEditor = new DefaultCellEditor(anchorComboBox);
		// tell the ValueCellEditorModel to use ed for row 1
		valueCellEditorModel.addEditorForCellValue(1, anchorCellEditor);
		
		// create a new JComboBox and editor for a different row
		JComboBox<?> fillComboBox = new JComboBox<Object>(fill_values);
		DefaultCellEditor fillCellEditor = new DefaultCellEditor(fillComboBox);
		// inform the ValueCellEditorModel of the situation
		valueCellEditorModel.addEditorForCellValue(2, fillCellEditor);
		
		add(comboBox, BorderLayout.NORTH);
		add(nameValueTable, BorderLayout.CENTER);
	}

	public PropertyPanelMain()
	{
		createGUI();
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("test");
		f.setSize(300, 350);
		f.getContentPane().add(new PropertyPanelMain(), BorderLayout.CENTER);
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		f.setVisible(true);
	}
}
