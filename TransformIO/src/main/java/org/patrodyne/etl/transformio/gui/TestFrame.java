package org.patrodyne.etl.transformio.gui;

import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class TestFrame
	extends JFrame
{
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					TestFrame frame = new TestFrame();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TestFrame()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow][grow]", "[grow][]"));
		
		JButton btnB1 = new JButton("B1");
		contentPane.add(btnB1, "cell 0 0, grow");
		
		JButton btnB2 = new JButton("B2");
		contentPane.add(btnB2, "cell 1 0, grow");
		
		JTextArea textArea = new JTextArea();
		contentPane.add(textArea, "cell 0 1 2 1,grow");
	}
}

// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
