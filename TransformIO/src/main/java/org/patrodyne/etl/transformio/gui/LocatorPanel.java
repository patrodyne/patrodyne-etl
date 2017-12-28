// PatroDyne: Patron Supported Dynamic Executables, http://patrodyne.org
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigInteger;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.patrodyne.etl.transformio.MessageType;
import org.patrodyne.etl.transformio.Transformer;
import org.patrodyne.etl.transformio.xml.Locator;

/**
 * A panel with a form for editing Locator properties.
 * 
 * @author Rick O'Sullivan
 */
@SuppressWarnings("serial")
public class LocatorPanel
	extends JPanel
{
	private static final String[] PROTOCOLS = new String[]
	{
	 "file", "ftp", "http", "https" 
//	 ,"sftp" ,"qfile", "qftp", "qsftp", "qgpg"
	};
	private JTextField urlTextField = new JTextField();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox cobProtocol = new JComboBox(PROTOCOLS);
	private JTextField fldHost = new JTextField();
	private JTextField fldPort = new JTextField();
	private JTextField fldUsername = new JTextField();
	private JTextField fldPassword = new JTextField();
	private JTextField fldPath = new JTextField();
	private JTextArea txtQuery = new JTextArea();
	private JTextField fldAnchor = new JTextField();
	private Component[] fields = new Component[]
	{
	 	urlTextField,
	 	cobProtocol,
	 	fldHost,
	 	fldPort,
	 	fldUsername,
	 	fldPassword,
	 	fldPath,
	 	txtQuery,
	 	fldAnchor
	};
	
	private Locator locator;
	public Locator getLocator()
	{
		if ( locator == null )
			locator = new Locator();
		return locator;
	}
	private void setLocator(Locator locator)
	{
		this.locator = new Locator();
		this.locator.setUrl(locator.getUrl());
		this.locator.setProtocol(locator.getProtocol());
		this.locator.setUsername(locator.getUsername());
		this.locator.setPassword(locator.getPassword());
		this.locator.setHost(locator.getHost());
		this.locator.setPort(locator.getPort());
		this.locator.setPath(locator.getPath());
		this.locator.setQuery(locator.getQuery());
		this.locator.setAnchor(locator.getAnchor());
	}

	private void setLocator(String urlText)
	{
		try
		{
			URL url = new URL(urlText);
			locator = new Locator();
			locator.setUrl(urlText);
			locator.setProtocol(url.getProtocol());
			if ( url.getUserInfo() != null )
			{
				String[] userInfo = url.getUserInfo().split(":");
				if (userInfo.length > 0)
					locator.setUsername(userInfo[0]);
				if (userInfo.length > 1)
					locator.setPassword(userInfo[1]);
			}
			locator.setHost(url.getHost());
			if (url.getPort() > -1)
				locator.setPort(BigInteger.valueOf(url.getPort()));
			else
				locator.setPort(BigInteger.ONE.negate());
			locator.setPath(url.getPath());
			locator.setQuery(url.getQuery());
			locator.setAnchor(url.getRef());
		}
		catch (Exception ex)
		{
			notification(MessageType.ERROR, ex);
		}
	}
	
	private void setLocatorURL()
	{
		StringBuilder url = new StringBuilder();
		if ( !isBlank(getLocator().getProtocol()) )
		{
			url.append(getLocator().getProtocol());
			boolean relPath = isBlank(getLocator().getPath()) || 
				!getLocator().getPath().startsWith("/");
			if ( "file".equals(getLocator().getProtocol()))
			{
				url.append(":");
				if ( !(relPath && isBlank(getLocator().getHost())) )
					url.append("//");
			}
			else
				url.append("://");
			if (!isBlank(getLocator().getHost()))
			{
				if (!isBlank(getLocator().getUsername()))
				{
					url.append(getLocator().getUsername());
					if (!isBlank(getLocator().getPassword()))
					{
						url.append(":");
						url.append(getLocator().getPassword());
					}
					url.append("@");
				}
				url.append(getLocator().getHost());
				if (BigInteger.ZERO.compareTo(getLocator().getPort()) <= 0)
				{
					url.append(":");
					url.append(getLocator().getPort());
				}
				if (relPath)
					url.append("/");
			}
			url.append(getLocator().getPath());
			if (!isBlank(getLocator().getQuery()))
			{
				url.append("?");
				url.append(getLocator().getQuery());
			}
			if (!isBlank(getLocator().getAnchor()))
			{
				url.append("#");
				url.append(getLocator().getAnchor());
			}
			getLocator().setUrl(url.toString());
			urlTextField.setText(getLocator().getUrl());
		}
	}
	
	private void setFields()
	{
		cobProtocol.setSelectedItem(getLocator().getProtocol());
		fldUsername.setText(getLocator().getUsername());
		fldPassword.setText(getLocator().getPassword());
		fldHost.setText(getLocator().getHost());
		if ( (getLocator().getPort() == null) || (BigInteger.ZERO.compareTo(getLocator().getPort()) >= 0))
			fldPort.setText("");
		else
			fldPort.setText(String.valueOf(getLocator().getPort()));
		fldPath.setText(getLocator().getPath());
		txtQuery.setText(wrapQuery(getLocator().getQuery()));
		fldAnchor.setText(getLocator().getAnchor());
	}
	
	/** Construct a JPanel with a Locator form. */
	public LocatorPanel(Locator locator)
	{
		setLocator(locator);
		
		if ( isBlank(getLocator().getUrl()) )
			setLocatorURL();
		else
			setLocator(getLocator().getUrl());
		
		setFields();

		setLayout(new MigLayout("", "[][grow][][grow]", "[][][][][][][]"));
		add(new JLabel("URL"), "cell 0 0,alignx trailing");
		
		urlTextField.setText(getLocator().getUrl());
		add(urlTextField, "cell 1 0 3 1,growx");
		urlTextField.setColumns(10);
		
		urlTextField.addFocusListener
		(
			new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)
				{
					setLocator(urlTextField.getText());
					setFields();
				}
				@Override
				public void focusGained(FocusEvent e) {	}
			}
		);

		JSeparator separator = new JSeparator();
		add(separator, "cell 0 1");
		
		JLabel lblProtocol = new JLabel("Protocol");
		add(lblProtocol, "cell 0 2,alignx trailing");
		
		cobProtocol.setEditable(true);
		add(cobProtocol, "cell 1 2,growx");
		
		cobProtocol.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					getLocator().setProtocol((String)cobProtocol.getSelectedItem());
					setLocatorURL();
				}
			}
		);
		
		JLabel lblPath = new JLabel("Path");
		add(lblPath, "cell 2 2,alignx trailing");
		
		add(fldPath, "cell 3 2,growx");
		fldPath.setColumns(20);
		
		fldPath.addFocusListener
		(
			new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)
				{
					getLocator().setPath(fldPath.getText());
					setLocatorURL();
				}
				@Override
				public void focusGained(FocusEvent e) {	}
			}
		);
		
		JLabel lblHost = new JLabel("Host");
		add(lblHost, "cell 0 3,alignx trailing");
		
		add(fldHost, "cell 1 3,growx");
		fldHost.setColumns(10);
		
		fldHost.addFocusListener
		(
			new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)
				{
					getLocator().setHost(fldHost.getText());
					setLocatorURL();
				}
				@Override
				public void focusGained(FocusEvent e) {	}
			}
		);
		
		JLabel lblQuery = new JLabel("Query");
		add(lblQuery, "cell 2 3,alignx trailing");
		
		txtQuery.setColumns(20);
		JScrollPane spnQuery = new JScrollPane();
		add(spnQuery, "cell 3 3 1 3,grow");
		spnQuery.setViewportView(txtQuery);

		txtQuery.addKeyListener
		(
			new KeyAdapter()
			{
				@Override
				public void keyPressed(KeyEvent e)
				{
					if (e.getKeyCode() == KeyEvent.VK_TAB)
					{
						if (e.getModifiers() > 0)
							txtQuery.transferFocusBackward();
						else
							txtQuery.transferFocus();
						e.consume();
					}
				}
			}
		);
		
		txtQuery.addFocusListener
		(
			new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)
				{
					getLocator().setQuery(unwrapQuery(txtQuery.getText()));
					setLocatorURL();
				}
				@Override
				public void focusGained(FocusEvent e) {	}
			}
		);

		JLabel lblPort = new JLabel("Port");
		add(lblPort, "cell 0 4,alignx trailing");
		
		add(fldPort, "cell 1 4,growx");
		fldPort.setColumns(10);
		
		fldPort.addFocusListener
		(
			new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)
				{
					if ( isBlank(fldPort.getText()) )
						getLocator().setPort(BigInteger.ONE.negate());
					else
						getLocator().setPort(new BigInteger(fldPort.getText()));
					setLocatorURL();
				}
				@Override
				public void focusGained(FocusEvent e) {	}
			}
		);
		
		JLabel lblUsername = new JLabel("Username");
		add(lblUsername, "cell 0 5,alignx trailing");
		
		add(fldUsername, "cell 1 5,growx");
		fldUsername.setColumns(10);
		
		fldUsername.addFocusListener
		(
			new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)
				{
					getLocator().setUsername(fldUsername.getText());
					setLocatorURL();
				}
				@Override
				public void focusGained(FocusEvent e) {	}
			}
		);
		
		JLabel lblPassword = new JLabel("Password");
		add(lblPassword, "cell 0 6,alignx trailing");
		
		add(fldPassword, "cell 1 6,growx");
		fldPassword.setColumns(10);
		
		fldPassword.addFocusListener
		(
			new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)
				{
					getLocator().setPassword(fldPassword.getText());
					setLocatorURL();
				}
				@Override
				public void focusGained(FocusEvent e) {	}
			}
		);
		
		JLabel lblAnchor = new JLabel("Anchor");
		add(lblAnchor, "cell 2 6,alignx trailing");
		
		add(fldAnchor, "cell 3 6,growx");
		fldAnchor.setColumns(20);
		
		fldAnchor.addFocusListener
		(
			new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)
				{
					getLocator().setAnchor(fldAnchor.getText());
					setLocatorURL();
				}
				@Override
				public void focusGained(FocusEvent e) {	}
			}
		);
		
		// Configure how the focus moves from one field to another.
		setFocusCycleRoot(true);
		setFocusTraversalPolicy
		(
			new FocusTraversalPolicy()
			{
				@Override
				public Component getDefaultComponent(Container aContainer)
				{
					return fields[0];
				}
				
				@Override
				public Component getFirstComponent(Container aContainer)
				{
					return fields[0];
				}
				
				@Override
				public Component getLastComponent(Container aContainer)
				{
					return fields[fields.length-1];
				}
				
				@Override
				public Component getComponentBefore(Container aContainer, Component aComponent)
				{
					int current = search(aComponent);
					int before = ( current > 0 ) ? current-1 : fields.length-1;
					return fields[before];
				}
				
				@Override
				public Component getComponentAfter(Container aContainer, Component aComponent)
				{
					int current = search(aComponent);
					int after = ( current < fields.length-1 ) ? current+1 : 0;
					return fields[after];
				}

				// search the fields array for the specified component.
				@SuppressWarnings("rawtypes")
				private int search(Component aComponent)
				{
					int current = 0;
					do
					{
						Component field = fields[current];
						if (field instanceof JComboBox)
							field = ((JComboBox) field).getEditor().getEditorComponent();
						if ( field == aComponent )
							break;
					} while ( ++current < fields.length);
					return current;
				}
			}
		);
		
		GraphicalUI.addTextPopupMenu(this);
	}
	
	private String wrapQuery(String query)
	{
		return (query != null) ? query.replaceAll("&", "\n&") : null;
	}
	
	private String unwrapQuery(String query)
	{
		return (query != null) ? query.replaceAll("\n", "") : null;
	}
	
	private static boolean isBlank(String str)
	{
		return str == null || str.trim().length() == 0;
	}
	
	private static void notification(MessageType type, Exception ex)
	{
		Transformer.notification(type, ex);
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
