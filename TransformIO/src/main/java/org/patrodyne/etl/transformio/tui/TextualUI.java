// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.tui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import charva.awt.*;
import charva.awt.event.*;
import charvax.swing.*;
import charvax.swing.border.*;

//import java.awt.*;
//import java.awt.event.*;
//import javax.swing.*;
//import javax.swing.border.*;

import java.io.*;

import org.patrodyne.etl.transformio.xml.Batch;
import org.patrodyne.etl.transformio.xml.Locator;
import org.patrodyne.etl.transformio.MessageType;
import org.patrodyne.etl.transformio.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TextualUI.
 * 
 * @author Rick O'Sullivan
 */
//@SuppressWarnings("serial")
public class TextualUI extends Transformer implements Runnable
{
	@SuppressWarnings("unused")
	private static final Font DEFAULT_FONT = new Font("Courier New", Font.PLAIN, 15);
	private static final CharSequence EXPAND_TAB = "    ";
	private static TextualClipboard clipboard = new TextualClipboard();
	private static JFrame frame;
	private static final int CTRL_B=2, CTRL_E=5, CTRL_G=7, CTRL_K=11, CTRL_S=19, CTRL_R=18;
	
	private static transient Logger log = LoggerFactory.getLogger(TextualUI.class);
	
	/**
	 * Represents the logger.
	 * @see org.patrodyne.etl.transformio.Transformer#log()
	 */
	protected Logger log() { return log; }

	/**
	 * Launch the application.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args)
	{
		Properties properties = new Properties();
		start(properties);
	}
	
	/**
	 * Create the application with startup options.
	 *
	 * @param options the options
	 */
	public TextualUI(Properties options)
	{
		initialize(options);
	}

	/**
	 * Start the TUI with command line options.
	 * 
	 * @param options Command line options.
	 */
	public static void start(final Properties options)
	{
		try
		{
			TextualUI textualUI = new TextualUI(options);
			// EventQueue.invokeLater(textualUI);
			// Thread tuiThread = new Thread(textualUI);
			// tuiThread.start();
			textualUI.run();
		}
		catch ( UnsatisfiedLinkError ule)
		{
			stderr(ule.getMessage());
			stderr("Please build 'CHARVA: A Java Windowing Toolkit for Text Terminals'");
			stderr("from http://www.pitman.co.za/projects/charva/index.html");
			stderr("and copy libTerminal.so or Terminal.dll to:");
			stderr(System.getProperty("java.library.path"));
		}
		catch (Exception e)
		{
			log.error("start failed", e);
		}
	}
	
	private static void stderr(String message)
	{
		System.err.println(message);
	}

	/**
	 * Run this application in the AWT event dispatching thread, for thread
	 * safety.
	 */
	public void run()
	{
		frame.setVisible(true);
	}
	
	private void stop()
	{
		try
		{
			frame.setVisible(false);
			System.exit(0);
		}
		catch (Exception e)
		{
			log.warn("stop failed", e);
		}
	}
	
	/**
	 * Initialize preference manager.
	 */
	protected static void initializePreferenceManager()
	{
		String prefPath = TextualUI.class.getName().replace('.', '/');
		setPreferencesManager(new PreferencesManager(frame, prefPath));
	}

	private static PreferencesManager preferencesManager;
	
	/**
	 * Gets the preferences manager.
	 *
	 * @return the preferences manager
	 */
	protected static PreferencesManager getPreferencesManager()
	{
		if ( preferencesManager == null )
			initializePreferenceManager();
		return preferencesManager;
	}
	/**
	 * Sets the preferences manager.
	 *
	 * @param preferencesManager the new preferences manager
	 */
	protected static void setPreferencesManager(PreferencesManager preferencesManager)
	{
		TextualUI.preferencesManager = preferencesManager;
	}
	
	/**
	 * Gets the preferred console history size.
	 *
	 * @return the preferred console history size
	 */
	protected int getPreferredConsoleHistorySize()
	{
		return PreferencesManager.getPreferredConsoleHistorySize();
	}
	
	/**
	 * Gets the preferred initial source edit mode.
	 *
	 * @return the preferred initial source edit mode
	 */
	protected Boolean getPreferredInitialSourceEditMode()
	{
		return PreferencesManager.getPreferredInitialSourceEditMode();
	}
	
	/**
	 * Gets the preferred initial wrap source data.
	 *
	 * @return the preferred initial wrap source data
	 */
	protected Boolean getPreferredInitialWrapSourceData()
	{
		return PreferencesManager.getPreferredInitialWrapSourceData();
	}
	
	/**
	 * Gets the preferred initial wrap target data.
	 *
	 * @return the preferred initial wrap target data
	 */
	protected Boolean getPreferredInitialWrapTargetData()
	{
		return PreferencesManager.getPreferredInitialWrapTargetData();
	}
	
	/**
	 * Gets the preferred initial wrap console data.
	 *
	 * @return the preferred initial wrap console data
	 */
	protected Boolean getPreferredInitialWrapConsoleData()
	{
		return PreferencesManager.getPreferredInitialWrapConsoleData();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @wbp.parser.entryPoint
	 */
	private void initialize(Properties options)
	{
		frame = new JFrame();
		initializePreferenceManager();

//		frame.setLocation(0, 0);
//		frame.setForeground(Color.white);
//		frame.setBackground(Color.blue);

		// Java.awt
//		frame.setFont(DEFAULT_FONT);
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		int windowWidth = screenSize.width/2;
//		int windowHeight = screenSize.height/2; 
//		int screeenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
//		int fontPoints = frame.getFont().getSize();
//		WindowSize windowSize = 
//			calculateWindowSize(windowWidth, windowHeight, screeenResolution, fontPoints, 2);
//		frame.setBounds(50, 50, windowWidth, windowHeight);

		// Charva.awt
		int windowCols = Toolkit.getDefaultToolkit().getScreenColumns();
		int windowRows = Toolkit.getDefaultToolkit().getScreenRows();
		WindowSize windowSize = new WindowSize(windowCols, windowRows);
		frame.setSize(windowSize.getColumns(), windowSize.getRows());
		
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		final JTabbedPane tabbedPane = new JTabbedPane();
		contentPane.add(tabbedPane);

		int editorCols = windowSize.getColumns()-4-2;
		int editorRows = windowSize.getRows()-6-2;
		
		JScrollPane batchPane = new JScrollPane();
		batchPane.setViewportBorder(new TitledBorder("Editor"));
		final JTextArea batchTextArea = new JTextArea("", editorRows, editorCols);
		batchTextArea.setEditable(true);
		batchTextArea.setLineWrap(false);
		batchTextArea.setWrapStyleWord(false);
		// batchTextArea.setTabSize(DEFAULT_TAB_SIZE);
		batchPane.setViewportView(batchTextArea);
		tabbedPane.addTab("Batch",  null, batchPane, "");
		final TextualKeyListener batchKeyListener = new TextualKeyListener(batchTextArea, clipboard);
		batchTextArea.addKeyListener(batchKeyListener);
		
		JScrollPane sourcePane = new JScrollPane();
		sourcePane.setViewportBorder(new TitledBorder("Viewer"));
		final JTextArea sourceTextArea = new JTextArea("", editorRows, editorCols);
		sourceTextArea.setEditable(getPreferredInitialWrapSourceData());
		sourceTextArea.setLineWrap(false);
		sourceTextArea.setWrapStyleWord(false);
		// sourceTextArea.setTabSize(DEFAULT_TAB_SIZE);
		sourcePane.setViewportView(sourceTextArea);
		tabbedPane.addTab("Source", null, sourcePane, "");
		final TextualKeyListener sourceKeyListener = new TextualKeyListener(sourceTextArea, clipboard);
		sourceTextArea.addKeyListener(sourceKeyListener);
		
		JScrollPane targetPane = new JScrollPane();
		targetPane.setViewportBorder(new TitledBorder("Viewer"));
		final JTextArea targetTextArea = new JTextArea("", editorRows, editorCols);
		targetTextArea.setEditable(false);
		targetTextArea.setLineWrap(getPreferredInitialWrapTargetData());
		targetTextArea.setWrapStyleWord(false);
		// targetTextArea.setTabSize(DEFAULT_TAB_SIZE);
		targetPane.setViewportView(targetTextArea);
		tabbedPane.addTab("Target", null, targetPane, "");
		
		JScrollPane consolePane = new JScrollPane();
		consolePane.setViewportBorder(new TitledBorder("Viewer"));
		final JTextArea consoleTextArea = new JTextArea("", editorRows, editorCols);
		consoleTextArea.setEditable(false);
		consoleTextArea.setLineWrap(getPreferredInitialWrapConsoleData());
		consoleTextArea.setWrapStyleWord(false);
		// consoleTextArea.setTabSize(DEFAULT_TAB_SIZE);
		consolePane.setViewportView(consoleTextArea);
		tabbedPane.addTab("Console", null, consolePane, "");
		
		// Optionally, load batch and source.
		String batchFilename = options.containsKey("batch") ? options.getProperty("batch") : null;
		if ( batchFilename != null )
		{
			try
			{
				setCurrentBatchFile(read(batchTextArea, batchFilename));
				setCurrentBatchHash(batchTextArea.getText().hashCode());
				expandTab(batchTextArea);
				Batch batch = unmarshalBatch(batchTextArea.getText());
				if ( batch != null )
				{
					openURL(sourceTextArea, batch.getSource().getLocator());
					setCurrentSourceHash(sourceTextArea.getText().hashCode());
				}
			}
			catch (Exception ex)
			{
				notification(ex);
			}
		}

		final JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		final JMenu mnFile = new JMenu("File", 'F');
		menuBar.add(mnFile);

		JMenuItem mntmNew = new JMenuItem("New", 'N');
		mnFile.add(mntmNew);
		
		JMenuItem mntmOpen = new JMenuItem("Open...", 'O');
		mnFile.add(mntmOpen);
		
		JMenuItem mntmClose = new JMenuItem("Close", 'C');
		mnFile.add(mntmClose);
		
		JMenuItem mntmSave = new JMenuItem("Save (^S)", 'S');
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As...", 'A');
		mnFile.add(mntmSaveAs);
		
		JMenuItem mntmExit = new JMenuItem("Exit", 'x');
		mnFile.add(mntmExit);

		JMenu mnEdit = new JMenu("Edit", 'E');
		menuBar.add(mnEdit);
		
		JMenuItem mntmMark = new JMenuItem("Mark (^A)", 'M');
		mnEdit.add(mntmMark);
		
		mnEdit.addSeparator();
		
		JMenuItem mntmCut = new JMenuItem("Cut (^X)", 't');
		mnEdit.add(mntmCut);
		
		JMenuItem mntmCopy = new JMenuItem("Copy (^C)" , 'C');
		mnEdit.add(mntmCopy);
		
		JMenuItem mntmPaste = new JMenuItem("Paste (^V)", 'P');
		mnEdit.add(mntmPaste);
		
		mnEdit.addSeparator();
		
		JMenuItem mntmPreferences = new JMenuItem("Options", 'O');
		mnEdit.add(mntmPreferences);

		JMenu mnTransform = new JMenu("Transform", 'T');
		menuBar.add(mnTransform);
		
		JMenuItem mntmDebug = new JMenuItem("Debug (^G)", 'D');
		mnTransform.add(mntmDebug);
		
		JMenuItem mntmRun = new JMenuItem("Run (^R)", 'R');
		mnTransform.add(mntmRun);
		
		final JMenu mnSource = new JMenu("Source", 'S');
		menuBar.add(mnSource);
		
		JCheckBoxMenuItem chckbxmntmWrapSource = new JCheckBoxMenuItem("Wrap", 'W');
		chckbxmntmWrapSource.setSelected(getPreferredInitialWrapSourceData());
		mnSource.add(chckbxmntmWrapSource);
		
		final JCheckBoxMenuItem chckbxmntmEditSource = new JCheckBoxMenuItem("Edit", 'E');
		chckbxmntmEditSource.setSelected(getPreferredInitialSourceEditMode());
		mnSource.add(chckbxmntmEditSource);

		final JMenuItem mntmSaveSource = new JMenuItem("Save", 'S');
		mntmSaveSource.setEnabled(false);
		mnSource.add(mntmSaveSource);
		
		JMenuItem mntmRefreshSource = new JMenuItem("Refresh", 'R');
		mnSource.add(mntmRefreshSource);
		
		JMenu mnTarget = new JMenu("Target", 'r');
		menuBar.add(mnTarget);
		
		JCheckBoxMenuItem chckbxmntmWrapTarget = new JCheckBoxMenuItem("Wrap", 'W');
		chckbxmntmWrapTarget.setSelected(getPreferredInitialWrapTargetData());
		mnTarget.add(chckbxmntmWrapTarget);
		
		JMenuItem mntmBlankTarget = new JMenuItem("Blank (^B)", 'B');
		mnTarget.add(mntmBlankTarget);
		
		JMenu mnConsole = new JMenu("Console", 'C');
		menuBar.add(mnConsole);
		
		JCheckBoxMenuItem chckbxmntmWrapConsole = new JCheckBoxMenuItem("Wrap", 'W');
		chckbxmntmWrapConsole.setSelected(getPreferredInitialWrapConsoleData());
		mnConsole.add(chckbxmntmWrapConsole);
		
		JMenuItem mntmBlankConsole = new JMenuItem("Blank (^K)", 'B');
		mnConsole.add(mntmBlankConsole);
		
		JMenuItem mntmListEngines = new JMenuItem("Engines (^E)", 'E');
		mnConsole.add(mntmListEngines);
		
		JMenu mnHelp = new JMenu("Help", 'H');
		menuBar.add(mnHelp);
		
		JMenuItem mntmOnline = new JMenuItem("Online", 'O');
		mnHelp.add(mntmOnline);
		
		JMenuItem mntmDonate = new JMenuItem("Donate", 'D');
		mnHelp.add(mntmDonate);
		
		JMenuItem mntmKeys = new JMenuItem("Keys", 'K');
		mnHelp.add(mntmKeys);
		
		JMenuItem mntmAbout = new JMenuItem("About", 'A');
		mnHelp.add(mntmAbout);
		
		frame.addKeyListener
		(
			new KeyListener()
			{
				@Override
				public void keyTyped(KeyEvent keyEvent)	{ }
				
				@Override
				public void keyReleased(KeyEvent keyEvent) { }
				
				@Override
				public void keyPressed(KeyEvent keyEvent)
				{
					switch ( keyEvent.getKeyCode() )
					{
						case CTRL_G: debug(batchTextArea, sourceTextArea, targetTextArea); break;
						case CTRL_R: run(batchTextArea, sourceTextArea, targetTextArea); break;
						case CTRL_S: saveBatch(batchTextArea); break;
						case CTRL_B: blankOrNotify(targetTextArea); break;
						case CTRL_K: blankOrNotify(consoleTextArea); break;
						case CTRL_E: logScriptEngines(); break;
						default: break;
					}
				}
			}
		);
		
		tabbedPane.addKeyListener
		(
			new KeyListener()
			{
				@Override
				public void keyTyped(KeyEvent keyEvent) { }
				
				@Override
				public void keyReleased(KeyEvent keyEvent) {	}
				
				@Override
				public void keyPressed(KeyEvent keyEvent)
				{
					if ( keyEvent.getSource() instanceof JButton )
					{
						int key = keyEvent.getKeyCode();
						if (key == ' ')
						{
							mnFile.setSelected(true);
							menuBar.setFocus(mnFile);
						}
						else if (key == 'b' || key == 'B')
						{
							tabbedPane.setSelectedIndex(0);
							tabbedPane.setFocus(tabbedPane.getComponent(0));
						}
						else if (key == 's' || key == 'S')
						{
							tabbedPane.setSelectedIndex(1);
							tabbedPane.setFocus(tabbedPane.getComponent(1));
						}
						else if (key == 't' || key == 'T')
						{
							tabbedPane.setSelectedIndex(2);
							tabbedPane.setFocus(tabbedPane.getComponent(2));
						}
						else if (key == 'c' || key == 'C')
						{
							tabbedPane.setSelectedIndex(3);
							tabbedPane.setFocus(tabbedPane.getComponent(3));
						}
					}
				}
			}
		);
		
		mntmNew.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						if ( confirmClose(batchTextArea, sourceTextArea, targetTextArea, chckbxmntmEditSource, mntmSaveSource) )
						{
							batchTextArea.setText(marshalNewBatch());
							expandTab(batchTextArea);
							batchTextArea.setCaretPosition(0);
							setCurrentBatchHash(batchTextArea.getText().hashCode());
							setCurrentSourceHash(sourceTextArea.getText().hashCode());
						}
					}
					catch ( Exception ex)
					{
						notification(ex);
					}
				}
			}
		);
		
		mntmOpen.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						if ( confirmClose(batchTextArea, sourceTextArea, targetTextArea, chckbxmntmEditSource, mntmSaveSource) )
						{
							setCurrentBatchFile(open(batchTextArea, getCurrentBatchFile()));
							setCurrentBatchHash(batchTextArea.getText().hashCode());
							expandTab(batchTextArea);
							Batch batch = unmarshalBatch(batchTextArea.getText());
							if ( batch != null )
							{
								openURL(sourceTextArea, batch.getSource().getLocator());
								setCurrentSourceHash(sourceTextArea.getText().hashCode());
								blank(targetTextArea);
							}
						}
					}
					catch (Exception ex)
					{
						notification(ex);
					}
				}
			}
		);
		
		mntmClose.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						confirmClose(batchTextArea, sourceTextArea, targetTextArea, chckbxmntmEditSource, mntmSaveSource);
					}
					catch (Exception ex)
					{
						notification(ex);
					}
				}
			}
		);
		
		mntmSave.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					saveBatch(batchTextArea);
				}
			}
		);
		
		mntmSaveAs.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						setCurrentBatchFile(saveAs(batchTextArea, getCurrentBatchFile()));
						setCurrentBatchHash(batchTextArea.getText().hashCode());
					}
					catch (Exception ex)
					{
						notification(ex);
					}
				}
			}
		);
		
		mntmExit.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent ae_)
				{
					String actionCommand = ae_.getActionCommand();
					if (actionCommand.equals("Exit"))
					{
						try
						{
							if ( confirmClose(batchTextArea, sourceTextArea, targetTextArea, chckbxmntmEditSource, mntmSaveSource) )
							    stop();
						}
						catch (Exception ex)
						{
							notification(ex);
						}
					}
					else
					{
						JOptionPane.showMessageDialog(frame, 
							"Menu item '" + actionCommand + "' not implemented", 
							"Error", JOptionPane.PLAIN_MESSAGE);
					}
				}
			}
		);
		
		mntmMark.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if ( batchTextArea.isDisplayed() )
						batchKeyListener.mark();
					else if ( sourceTextArea.isDisplayed() )
						sourceKeyListener.mark();
				}
			}
		);
		
		mntmCut.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if ( batchTextArea.isDisplayed() )
						batchKeyListener.cut();
					else if ( sourceTextArea.isDisplayed() )
						sourceKeyListener.cut();
				}
			}
		);
		
		mntmCopy.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if ( batchTextArea.isDisplayed() )
						batchKeyListener.copy();
					else if ( sourceTextArea.isDisplayed() )
						sourceKeyListener.copy();
				}
			}
		);
		
		mntmPaste.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if ( batchTextArea.isDisplayed() )
						batchKeyListener.paste();
					else if ( sourceTextArea.isDisplayed() )
						sourceKeyListener.paste();
				}
			}
		);
		
		mntmPreferences.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					setPreferencesManager(null);
					getPreferencesManager().setVisible(true);
				}
			}
		);
		
		mntmDebug.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					debug(batchTextArea, sourceTextArea, targetTextArea);
				}
			}
		);
		
		mntmRun.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					run(batchTextArea, sourceTextArea, targetTextArea);
				}
			}
		);
		
		chckbxmntmWrapSource.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						lineWrap(sourceTextArea, ((JCheckBoxMenuItem) e.getSource()).isSelected());
					}
					catch (Exception ex)
					{
						notification(ex);
					}
				}
			}
		);
		
		chckbxmntmEditSource.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					boolean isSelected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
					sourceTextArea.setEditable(isSelected);
					mntmSaveSource.setEnabled(isSelected);
					mnSource.repaint();
				}
			}
		);
		
		mntmSaveSource.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						Batch batch = unmarshalBatch(batchTextArea.getText());
						if ( batch != null )
						{
							saveURL(sourceTextArea, batch.getSource().getLocator());
							setCurrentSourceHash(sourceTextArea.getText().hashCode());
						}
					}
					catch (Exception ex)
					{
						notification(ex);
					}
				}
			}
		);
		
		mntmRefreshSource.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						Batch batch = unmarshalBatch(batchTextArea.getText());
						if ( batch != null )
							refreshSource(sourceTextArea, batch);
					}
					catch (Exception ex)
					{
						notification(ex);
					}
				}
			}
		);
		
		chckbxmntmWrapTarget.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						lineWrap(targetTextArea, ((JCheckBoxMenuItem) e.getSource()).isSelected());
					}
					catch (Exception ex)
					{
						notification(ex);
					}
				}
			}
		);
		
		mntmBlankTarget.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					blankOrNotify(targetTextArea);
				}
			}
		);

		chckbxmntmWrapConsole.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						lineWrap(consoleTextArea, ((JCheckBoxMenuItem) e.getSource()).isSelected());
					}
					catch (Exception ex)
					{
						notification(ex);
					}
				}
			}
		);
		
		mntmBlankConsole.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					blankOrNotify(consoleTextArea);
				}
			}
		);
		
		mntmListEngines.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					logScriptEngines();
				}
			}
		);
		
		mntmOnline.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						notification(MessageType.INFO, LINK_HOME);
					}
					catch (Exception ex)
					{
						notification(ex);
					}
				}
			}
		);
		
		mntmDonate.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						notification(MessageType.INFO, LINK_DONATE);
					}
					catch (Exception ex)
					{
						notification(ex);
					}
				}
			}
		);
		
		mntmKeys.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					JDialog keysDialog = new QuickKeys(frame);
					keysDialog.setVisible(true);
				}
			}
		);
		
		mntmAbout.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						notification(MessageType.INFO, about());
					}
					catch (Exception ex)
					{
						notification(ex);
					}
				}
			}
		);
		
		// Start the console.
		ConsoleThread consoleThread = new ConsoleThread(consoleTextArea);
		consoleThread.start();
		frame.validate();
	}

	/**
	 * Expand tab and set caret position to the 0th position.
	 *
	 * @param textArea the text area
	 */
	protected void expandTab(JTextArea textArea)
	{
		textArea.setText(textArea.getText().replace("\t", EXPAND_TAB));
		textArea.setCaretPosition(0);
	}

	private void lineWrap(JTextArea textArea, boolean wrap)
	{
		textArea.setLineWrap(wrap);
		textArea.validate();
	}
	
	private void blank(JTextArea txtrArea)
	{
		txtrArea.setText("");
		txtrArea.repaint();
	}
	
	private void transformDebug(JTextArea txtrBatch, JTextArea txtrSource, JTextArea txtrTarget)
		throws Exception
	{
		Batch batch = unmarshalBatch(txtrBatch.getText());
		if ( batch != null )
		{
			// Use current edits, source is optional.
			// refreshSource(txtrSource, batch);
			txtrTarget.setText(transform(batch, txtrSource.getText()));
			txtrTarget.repaint();
		}
	}

	private void transformRun(JTextArea txtrBatch, JTextArea txtrSource, JTextArea txtrTarget)
		throws Exception
	{
		Batch batch = unmarshalBatch(txtrBatch.getText());
		if ( batch != null )
		{
			refreshSource(txtrSource, batch);
			blank(txtrTarget);
			execute(batch, false);
			txtrTarget.setText("Target: "+resolve(batch.getTarget().getLocator()));
		}
	}

	private void refreshSource(JTextArea txtrSource, Batch batch)
		throws IOException
	{
		if ( (batch.getSource() == null) || batch.getSource().getLocator() == null )
			blank(txtrSource);
		else
			openURL(txtrSource, batch.getSource().getLocator());
	}

	// Approved, file != null; otherwise, file = null.
	private File saveAs(final JTextArea textArea, File currentFile) throws IOException
	{
		File file = null;
		JFileChooser chooser = new JFileChooser();
		if ( currentFile != null )
			chooser.setCurrentDirectory(currentFile.getParentFile());
		else
			chooser.setCurrentDirectory(getCurrentWorkingDirectory());
		if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
		{
			if ((file = chooser.getSelectedFile()) != null)
				save(textArea, file);
			else
			{
				file = currentFile;
				notification(MessageType.INFO, "Save aborted, no file selected.");
			}
		}
		else
			notification(MessageType.INFO, "Save canceled by user.");
		return file;
	}

	private void save(final JTextArea textArea, File file) throws IOException
	{
		save(textArea.getText(), file);
		notification(MessageType.INFO, file.getName()+" saved.");
	}

	private File open(final JTextArea textArea, File currentFile) throws IOException
	{
		File newFile = currentFile;
		JFileChooser chooser = new JFileChooser();
		if ( newFile != null )
			chooser.setCurrentDirectory(newFile.getParentFile());
		else
			chooser.setCurrentDirectory(getCurrentWorkingDirectory());
		int returnVal = chooser.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			if (file != null)
			{
				newFile = file;
				read(textArea, file);
			}
		}
		else
			notification(MessageType.INFO, "Open canceled by user.");
		return newFile;
	}

	private boolean confirmClose(final JTextArea txtrBatch, final JTextArea txtrSource, final JTextArea txtrTarget,
		final JCheckBoxMenuItem chckbxmntmEdit, final JMenuItem mntmSaveSource) throws Exception
	{
		boolean confirmClose = false;
		boolean batchChanged = (txtrBatch.getText().hashCode() != getCurrentBatchHash());
		boolean sourceChanged = (txtrSource.getText().hashCode() != getCurrentSourceHash());
		if ( batchChanged || sourceChanged )
		{
			int response = JOptionPane.showConfirmDialog(frame, "Save changes?", "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);
			switch ( response )
			{
				case JOptionPane.YES_OPTION:
					if (getCurrentBatchFile() == null)
						setCurrentBatchFile(saveAs(txtrBatch, getCurrentBatchFile()));
					// If not null, either _was_ not null or saveAsBatch was approved.
					if (getCurrentBatchFile() != null)
					{
						if ( batchChanged )
							save(txtrBatch, getCurrentBatchFile());
						if ( sourceChanged )
						{
							Batch batch = unmarshalBatch(txtrBatch.getText());
							if ( batch != null )
								saveURL(txtrSource, batch.getSource().getLocator());
						}
						close(txtrBatch, txtrSource, txtrTarget, chckbxmntmEdit, mntmSaveSource);
						confirmClose = true;
					}
					break;
				case JOptionPane.NO_OPTION: 
					close(txtrBatch, txtrSource, txtrTarget, chckbxmntmEdit, mntmSaveSource);
					confirmClose = true;
					break;
				case JOptionPane.CANCEL_OPTION: 
					break;
			}
		}
		else
		{
			close(txtrBatch, txtrSource, txtrTarget, chckbxmntmEdit, mntmSaveSource);
			confirmClose = true;
		}
		if ( confirmClose )
			setCurrentBatchFile(null);
		return confirmClose;
	}
	
	private void close(final JTextArea txtrBatch, final JTextArea txtrSource, final JTextArea txtrTarget,
		final JCheckBoxMenuItem chckbxmntmEdit, final JMenuItem mntmSaveSource)
	{
		txtrBatch.setText("");
		txtrSource.setText("");
		txtrTarget.setText("");
		txtrSource.setEditable(false);
		chckbxmntmEdit.setSelected(false);
		mntmSaveSource.setEnabled(false);
		setCurrentBatchHash(txtrBatch.getText().hashCode());
		setCurrentSourceHash(txtrSource.getText().hashCode());
	}
	
	private void openURL(JTextArea textArea, Locator locator) throws IOException
	{
		textArea.setText(openURL(locator));
		//textArea.setTabSize(DEFAULT_TAB_SIZE);
	}

	private void saveURL(JTextArea textArea, Locator locator) throws IOException
	{
		saveURL(locator, textArea.getText());
	}

	private File read(final JTextArea textArea, String filename)
		throws FileNotFoundException, IOException
	{
		File file = new File(filename);
		read(textArea, new File(filename));
		return file;
	}

	private void read(final JTextArea textArea, File file)
		throws FileNotFoundException, IOException
	{
		textArea.setText(read(file));
		textArea.setCaretPosition(0);
		//textArea.setTabSize(DEFAULT_TAB_SIZE);
	}

	private void saveBatch(final JTextArea batchTextArea)
	{
		try
		{
			if (getCurrentBatchFile() != null)
				save(batchTextArea, getCurrentBatchFile());
			else
				setCurrentBatchFile(saveAs(batchTextArea, getCurrentBatchFile()));
			setCurrentBatchHash(batchTextArea.getText().hashCode());
		}
		catch (Exception ex)
		{
			notification(ex);
		}
	}

	private void debug(final JTextArea batchTextArea, final JTextArea sourceTextArea, final JTextArea targetTextArea)
	{
		try
		{
			transformDebug(batchTextArea, sourceTextArea, targetTextArea);
		}
		catch (Exception ex)
		{
			notification(ex);
		}
	}

	private void run(final JTextArea batchTextArea, final JTextArea sourceTextArea, final JTextArea targetTextArea)
	{
		try
		{
			transformRun(batchTextArea, sourceTextArea, targetTextArea);
		}
		catch (Exception ex)
		{
			notification(ex);
		}
	}

	private void blankOrNotify(final JTextArea targetTextArea)
	{
		try
		{
			blank(targetTextArea);
		}
		catch (Exception ex)
		{
			notification(ex);
		}
	}

	/**
	 * TUI message dialog.
	 *
	 * @param type the type of information
	 * @param message the message to notify
	 */
	public static void tuiMessageDialog(MessageType type, String message)
	{
		switch ( type )
		{
			case INFO:
				JOptionPane.showMessageDialog(frame, message, "Information", JOptionPane.INFORMATION_MESSAGE);
				log.info(message);
				break;
			case WARN: 
				JOptionPane.showMessageDialog(frame, message, "Warning", JOptionPane.WARNING_MESSAGE);
				log.warn(message);
				break;
			case ERROR: 
				JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
				log.error(message);
				break;
			case FATAL: 
				JOptionPane.showMessageDialog(frame, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
				log.error(message);
				break;
		}
	}

	private class ConsoleThread extends Thread
	{
		private JTextArea textArea;
		protected JTextArea getTextArea()
		{
			return textArea;
		}
		protected void setTextArea(JTextArea textArea)
		{
			this.textArea = textArea;
		}
		
		private ConsoleThread(JTextArea textArea)
		{
			super(ConsoleThread.class.getSimpleName());
			setTextArea(textArea);
		}

		public void run()
		{
			console(about());
			try
			{
				List<Integer> lineHistory = new ArrayList<Integer>();
				LineNumberReader consoleReader = consoleReader();
				String readLine;
				while ( (readLine = consoleReader.readLine()) != null )
				{
					if ( getTextArea().getText().isEmpty() )
						lineHistory.clear();
					String line = readLine+"\n";
					getTextArea().append(line);
					lineHistory.add(line.length());
					if ( lineHistory.size() > getPreferredConsoleHistorySize() )
					{
						getTextArea().replaceRange("", 0, lineHistory.get(0));
						lineHistory.remove(0);
					}
					getTextArea().setCaretPosition(getTextArea().getText().length());
				}
			}
			catch (IOException ex)
			{
				// Use this instead of notification(ex) to
				// avoid logging a message back to this console!
				JOptionPane.showMessageDialog(frame, message(ex), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
