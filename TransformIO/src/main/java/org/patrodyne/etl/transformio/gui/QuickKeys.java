// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;

/**
 * Pop up to show the available quick keys.
 *
 * @author Rick O'Sullivan
 */
public class QuickKeys
	extends JDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Create the dialog.
	 */
	@SuppressWarnings("serial")
	public QuickKeys(Frame frame)
	{
		super(frame, "Quick Keys");
		setResizable(false);
		setAlwaysOnTop(true);
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			String[] headers = {"Action", "Key"};
			Object[][] keys = 
			{
			 	{"Debug Batch"	,"CTRL_G"},
			 	{"Run Batch"	,"CTRL_R"},
			 	{"Save Batch"	,"CTRL_S"},
			 	{"Cut Text"		,"CTRL_X"},
			 	{"Copy Text"	,"CTRL_C"},
			 	{"Paste Text"	,"CTRL_V"},
			 	{"Undo Changes"	,"CTRL_Z"},
			 	{"Redo Changes"	,"CTRL_Y"},
			 	{"Blank Target"	,"CTRL_B"},
			 	{"Blank Console","CTRL_K"},
			 	{"Engine List"	,"CTRL_E"}
			};
			JTable table = new JTable
			(
	        	new DefaultTableModel(keys,headers)
	        	{
		            @Override
		            public boolean isCellEditable(int row, int column)
		            {
		               return false;
		            }
		        }
			);
			table.setPreferredScrollableViewportSize(new Dimension(200, 176));
	        table.setFillsViewportHeight(true);
			table.setFocusable(false);
			table.setRowSelectionAllowed(false);
			JScrollPane scrollPane = new JScrollPane(table);
			contentPanel.add(scrollPane);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener
				(
					new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							setVisible(false);
						}
					}
				);
			}
		}
		pack();
		setLocationRelativeTo(frame);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
