// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.patrodyne.etl.transformio.MessageType;
import org.patrodyne.etl.transformio.Transformer;
import org.patrodyne.etl.transformio.xml.Batch;
import org.patrodyne.etl.transformio.xml.Locator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GraphicalUI User Interface for TransformIO.
 * 
 * @author Rick O'Sullivan
 */
public class GraphicalUI extends Transformer implements Runnable
{
	private static final double PRIMARY_RESIZE_WEIGHT = 0.75;
	private static final double DEFAULT_RESIZE_WEIGHT = 0.5;
	private static JFrame frame;
	private static JSplitPane splitPaneMain;
	private static JSplitPane splitPaneBatch;
	private static JSplitPane splitPaneRight;
	private UndoableEditListener batchUndoableEditListener = null;
	private JMenuItem mntmUndo = null, mntmRedo = null;
	private ActionListener undoActionListener, redoActionListener;

	private static transient Logger log = LoggerFactory.getLogger(GraphicalUI.class);
	
	/**
	 * @see org.patrodyne.etl.transformio.Transformer#log()
	 */
	protected Logger log() { return log; }

	/**
	 * Create the application with startup options.
	 *
	 * @param options the options
	 */
	public GraphicalUI(Properties options)
	{
		initialize(options);
		// logScriptEngines(); May break piped I/O?
	}

	/**
	 * Start the GUI with command line options.
	 * 
	 * @param options Command line options.
	 */
	public static void start(final Properties options)
	{
		EventQueue.invokeLater(new GraphicalUI(options));
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
		setPreferredExtendedState(frame.getExtendedState());
		if ( frame.getExtendedState() == JFrame.NORMAL )
			setPreferredBounds(frame.getBounds());
		System.exit(0);
	}
	
	/**
	 * Initialize preference manager.
	 */
	protected void initializePreferenceManager()
	{
		String prefPath = getClass().getName().replace('.', '/');
		setPreferencesManager(new PreferencesManager(frame, prefPath));
	}
	
	private PreferencesManager preferencesManager;
	
	/**
	 * Gets the preferences manager.
	 *
	 * @return the preferences manager
	 */
	protected PreferencesManager getPreferencesManager()
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
	protected void setPreferencesManager(PreferencesManager preferencesManager)
	{
		this.preferencesManager = preferencesManager;
	}

	/**
	 * Gets the preferred font.
	 *
	 * @return the preferred font
	 */
	protected Font getPreferredFont()
	{
		return PreferencesManager.getPreferredFont();
	}
	
	/**
	 * Gets the preferred bounds.
	 *
	 * @return the preferred bounds
	 */
	protected Rectangle getPreferredBounds()
	{
		return PreferencesManager.getPreferredBounds();
	}
	/**
	 * Sets the preferred bounds.
	 *
	 * @param preferredBounds the new preferred bounds
	 */
	protected void setPreferredBounds(Rectangle preferredBounds)
	{
		PreferencesManager.setPreferredBounds(preferredBounds);
	}
	
	/**
	 * Gets the preferred extended state.
	 *
	 * @return the preferred extended state
	 */
	protected Integer getPreferredExtendedState()
	{
		return PreferencesManager.getPreferredExtendedState();
	}
	/**
	 * Sets the preferred extended state.
	 *
	 * @param preferredExtendedState the new preferred extended state
	 */
	protected void setPreferredExtendedState(Integer preferredExtendedState)
	{
		PreferencesManager.setPreferredExtendedState(preferredExtendedState);
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
	 * Gets the preferred look and feel.
	 *
	 * @return the preferred look and feel
	 */
	protected String getPreferredLookAndFeel()
	{
		return PreferencesManager.getPreferredLookAndFeel();
	}
	/**
	 * Sets the preferred look and feel.
	 */
	protected void setPreferredLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(getPreferredLookAndFeel());
		}
		catch (Exception e1)
		{
			// Ignore
		}
	}

	/**
	 * Update look and feel.
	 */
	protected void updateLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(getPreferredLookAndFeel());
			SwingUtilities.updateComponentTreeUI(frame);
			SwingUtilities.updateComponentTreeUI(getPreferencesManager());
		}
		catch (Exception e)
		{
			// Use existing L&F.
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(Properties options)
	{
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		initializePreferenceManager();
		setPreferredLookAndFeel();
		
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		splitPaneMain = new JSplitPane();
		splitPaneMain.setResizeWeight(PRIMARY_RESIZE_WEIGHT);
		splitPaneMain.setOneTouchExpandable(true);
		splitPaneMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPaneMain);
		
		splitPaneBatch = new JSplitPane();
		splitPaneBatch.setResizeWeight(DEFAULT_RESIZE_WEIGHT);
		splitPaneBatch.setOneTouchExpandable(true);
		splitPaneMain.setTopComponent(splitPaneBatch);
		
		splitPaneRight = new JSplitPane();
		splitPaneRight.setOneTouchExpandable(true);
		splitPaneRight.setResizeWeight(DEFAULT_RESIZE_WEIGHT);
		splitPaneRight.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneBatch.setRightComponent(splitPaneRight);
		
		final RSyntaxTextArea batchTextArea = new RSyntaxTextArea();
		batchTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		batchTextArea.setTabSize(DEFAULT_TAB_SIZE);
		batchTextArea.setFont(getPreferredFont());
		batchTextArea.setRows(20);
		batchTextArea.setColumns(80);
		JScrollPane batchPane = new JScrollPane(batchTextArea);
		splitPaneBatch.setLeftComponent(batchPane);
		
		final JTextArea sourceTextArea = new JTextArea();
		sourceTextArea.setTabSize(DEFAULT_TAB_SIZE);
		sourceTextArea.setEditable(false);
		sourceTextArea.setFont(getPreferredFont());
		sourceTextArea.setLineWrap(getPreferredInitialWrapSourceData());
		sourceTextArea.setText("");
		sourceTextArea.setColumns(60);
		sourceTextArea.setRows(10);
		JScrollPane sourcePane = new JScrollPane(sourceTextArea);
		splitPaneRight.setLeftComponent(sourcePane);
		
		final JTextArea targetTextArea = new JTextArea();
		targetTextArea.setTabSize(DEFAULT_TAB_SIZE);
		targetTextArea.setEditable(false);
		targetTextArea.setFont(getPreferredFont());
		targetTextArea.setLineWrap(getPreferredInitialWrapTargetData());
		targetTextArea.setText("");
		targetTextArea.setRows(10);
		targetTextArea.setColumns(60);
		JScrollPane targetPane = new JScrollPane(targetTextArea);
		splitPaneRight.setRightComponent(targetPane);
		
		final JTextArea consoleTextArea = new JTextArea();
		consoleTextArea.setTabSize(DEFAULT_TAB_SIZE);
		consoleTextArea.setEditable(false);
		consoleTextArea.setFont(getPreferredFont());
		consoleTextArea.setLineWrap(getPreferredInitialWrapConsoleData());
		
		JScrollPane consolePane = new JScrollPane(consoleTextArea);
		splitPaneMain.setBottomComponent(consolePane);
		
		// Optionally, load batch and source.
		String batchFilename = options.containsKey("batch") ? options.getProperty("batch") : null;
		if ( batchFilename != null )
		{
			try
			{
				setCurrentBatchFile(read(batchTextArea, batchFilename));
				setCurrentBatchHash(batchTextArea.getText().hashCode());
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

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mnFile.add(mntmNew);
		
		JMenuItem mntmOpen = new JMenuItem("Open...");
		mnFile.add(mntmOpen);
		
		JMenuItem mntmClose = new JMenuItem("Close");
		mnFile.add(mntmClose);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As...");
		mnFile.add(mntmSaveAs);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		mntmUndo = new JMenuItem("Undo");
		mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		mnEdit.add(mntmUndo);
		
		mntmRedo = new JMenuItem("Redo");
		mntmRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
		mnEdit.add(mntmRedo);
		
		JSeparator separator1 = new JSeparator();
		mnEdit.add(separator1);
		
		JMenuItem mntmCut = new JMenuItem("Cut");
		mntmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		mnEdit.add(mntmCut);
		
		JMenuItem mntmCopy = new JMenuItem("Copy");
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		mnEdit.add(mntmCopy);
		
		JMenuItem mntmPaste = new JMenuItem("Paste");
		mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		mnEdit.add(mntmPaste);
		
		JSeparator separator2 = new JSeparator();
		mnEdit.add(separator2);
		
		JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mnEdit.add(mntmPreferences);
		
		JMenu mnTransform = new JMenu("Transform");
		menuBar.add(mnTransform);
		
		JMenuItem mntmDebug = new JMenuItem("Debug");
		mntmDebug.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
		mnTransform.add(mntmDebug);
		
		JMenuItem mntmRun = new JMenuItem("Run");
		mntmRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		mnTransform.add(mntmRun);
		
		final JMenu mnSource = new JMenu("Source");
		menuBar.add(mnSource);
		
		JCheckBoxMenuItem chckbxmntmWrapSource = new JCheckBoxMenuItem("Wrap");
		chckbxmntmWrapSource.setSelected(getPreferredInitialWrapSourceData());
		mnSource.add(chckbxmntmWrapSource);
		
		final JCheckBoxMenuItem chckbxmntmEditSource = new JCheckBoxMenuItem("Edit");
		chckbxmntmEditSource.setSelected(getPreferredInitialSourceEditMode());
		mnSource.add(chckbxmntmEditSource);

		final JMenuItem mntmSaveSource = new JMenuItem("Save");
		mntmSaveSource.setEnabled(false);
		mnSource.add(mntmSaveSource);
		
		JMenuItem mntmRefreshSource = new JMenuItem("Refresh");
		mnSource.add(mntmRefreshSource);
		
		JMenu mnTarget = new JMenu("Target");
		menuBar.add(mnTarget);
		
		JCheckBoxMenuItem chckbxmntmWrapTarget = new JCheckBoxMenuItem("Wrap");
		chckbxmntmWrapTarget.setSelected(getPreferredInitialWrapTargetData());
		mnTarget.add(chckbxmntmWrapTarget);
		
		JMenuItem mntmBlankTarget = new JMenuItem("Blank");
		mntmBlankTarget.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK));
		mnTarget.add(mntmBlankTarget);
		
		JMenu mnConsole = new JMenu("Console");
		menuBar.add(mnConsole);
		
		JCheckBoxMenuItem chckbxmntmWrapConsole = new JCheckBoxMenuItem("Wrap");
		chckbxmntmWrapConsole.setSelected(getPreferredInitialWrapConsoleData());
		mnConsole.add(chckbxmntmWrapConsole);
		
		JMenuItem mntmBlankConsole = new JMenuItem("Blank");
		mntmBlankConsole.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK));
		mnConsole.add(mntmBlankConsole);
		
		JMenuItem mntmListEngines = new JMenuItem("Engines");
		mntmListEngines.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		mnConsole.add(mntmListEngines);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmOnline = new JMenuItem("Online");
		mnHelp.add(mntmOnline);
		
		JMenuItem mntmDonate = new JMenuItem("Donate");
		mnHelp.add(mntmDonate);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		
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
							setCurrentBatchHash(batchTextArea.getText().hashCode());
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
							setCurrentBatchFile(openBatch(batchTextArea, getCurrentBatchFile()));
							setCurrentBatchHash(batchTextArea.getText().hashCode());
							if ( getCurrentBatchFile() != null )
							{
								Batch batch = unmarshalBatch(batchTextArea.getText());
								if ( batch != null )
								{
									openURL(sourceTextArea, batch.getSource().getLocator());
									setCurrentSourceHash(sourceTextArea.getText().hashCode());
									transformBlank(targetTextArea);
									// Add Undo/Redo Listener
									addUndoableEditListener(batchTextArea);
								}
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
					try
					{
						if (getCurrentBatchFile() != null)
							save(batchTextArea, getCurrentBatchFile());
						else
							setCurrentBatchFile(saveAsBatch(batchTextArea, getCurrentBatchFile()));
						setCurrentBatchHash(batchTextArea.getText().hashCode());
					}
					catch (Exception ex)
					{
						notification(ex);
					}
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
						setCurrentBatchFile(saveAsBatch(batchTextArea, getCurrentBatchFile()));
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
				public void actionPerformed(ActionEvent e)
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
			}
		);
		
		frame.addWindowListener
		(
		    new WindowAdapter()
		    {
		        public void windowClosing(WindowEvent e)
		        {
		            stop();
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
					batchTextArea.cut();
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
					batchTextArea.copy();
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
					batchTextArea.paste();
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
					getPreferencesManager().setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					getPreferencesManager().setVisible(true);
					// updateLookAndFeel();
					// Use preferred values.
					batchTextArea.setFont(getPreferredFont());
					sourceTextArea.setFont(getPreferredFont());
					targetTextArea.setFont(getPreferredFont());
					consoleTextArea.setFont(getPreferredFont());
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
					try
					{
						transformDebug(batchTextArea, sourceTextArea, targetTextArea);
					}
					catch (Exception ex)
					{
						notification(ex);
					}
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
					try
					{
						transformRun(batchTextArea, sourceTextArea, targetTextArea);
					}
					catch (Exception ex)
					{
						notification(ex);
					}
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
					try
					{
						transformBlank(targetTextArea);
					}
					catch (Exception ex)
					{
						notification(ex);
					}
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
					try
					{
						transformBlank(consoleTextArea);
					}
					catch (Exception ex)
					{
						notification(ex);
					}
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
						Desktop.getDesktop().browse(new URI(LINK_HOME));
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
						Desktop.getDesktop().browse(new URI(LINK_DONATE));
					}
					catch (Exception ex)
					{
						notification(ex);
					}
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
		
		// Add Undo/Redo Listener
		addUndoableEditListener(batchTextArea);
		// Start the console.
		new ConsoleThread(consoleTextArea).start();
		// Set preferred size and position.
		frame.pack();
		frame.setBounds(getPreferredBounds());
		frame.setExtendedState(getPreferredExtendedState());
	}

	private boolean confirmClose(final JTextArea txtrBatch, final JTextArea txtrSource, final JTextArea txtrTarget,
		final JCheckBoxMenuItem chckbxmntmEdit, final JMenuItem mntmSaveSource) throws Exception
	{
		boolean confirmClose = false;
		boolean batchChanged = (txtrBatch.getText().hashCode() != getCurrentBatchHash());
		boolean sourceChanged = (txtrSource.getText().hashCode() != getCurrentSourceHash());
		if ( batchChanged || sourceChanged )
		{
			int response = JOptionPane.showConfirmDialog(frame, "Save Changes?");
			switch ( response )
			{
				case JOptionPane.YES_OPTION:
					if (getCurrentBatchFile() == null)
						setCurrentBatchFile(saveAsBatch(txtrBatch, getCurrentBatchFile()));
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
		// Add Undo/Redo Listener
		addUndoableEditListener(txtrBatch);
		setCurrentBatchHash(txtrBatch.getText().hashCode());
		setCurrentSourceHash(txtrSource.getText().hashCode());
	}
	
	private void transformBlank(JTextArea txtrArea)
	{
		txtrArea.setText("");
	}
	
	private void transformDebug(JTextArea txtrBatch, JTextArea txtrSource, JTextArea txtrTarget)
		throws Exception
	{
		Batch batch = unmarshalBatch(txtrBatch.getText());
		if ( batch != null )
		{
			// Use current edits? refreshSource(txtrSource, batch);
			txtrTarget.setText(transform(batch, txtrSource.getText()));
		}
	}

	private void transformRun(JTextArea txtrBatch, JTextArea txtrSource, JTextArea txtrTarget)
		throws Exception
	{
		Batch batch = unmarshalBatch(txtrBatch.getText());
		if ( batch != null )
		{
			refreshSource(txtrSource, batch);
			transformBlank(txtrTarget);
			execute(batch, false);
			txtrTarget.setText("Target: "+resolve(batch.getTarget().getLocator()));
		}
	}

	private void refreshSource(JTextArea txtrSource, Batch batch)
		throws IOException
	{
		if ( (batch.getSource() == null) || batch.getSource().getLocator() == null )
			transformBlank(txtrSource);
		else
			openURL(txtrSource, batch.getSource().getLocator());
	}

	private void lineWrap(JTextArea textArea, boolean wrap)
	{
		textArea.setLineWrap(wrap);
		textArea.validate();
	}

	/**
	 * GUI message dialog.
	 *
	 * @param type the type to identify how information will be annotated or appended
	 * @param message the message for notification
	 */
	public static void guiMessageDialog(MessageType type, String message)
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

	private FileFilter fileFilter;
	private FileFilter getBatchFileFilter()
	{
		if ( fileFilter == null )
			fileFilter = new FileNameExtensionFilter("TransformIO (*.tio)", "tio");
		return fileFilter;
	}

	private File openBatch(final JTextArea textArea, File currentFile) throws IOException
	{
		return open(textArea, currentFile, getBatchFileFilter());
	}

	private File open(final JTextArea textArea, File currentFile, FileFilter filter)
		throws IOException
	{
		File newFile = currentFile;
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(filter);
		if ( newFile != null )
			chooser.setCurrentDirectory(newFile);
		else
			chooser.setCurrentDirectory(getCurrentWorkingDirectory());
		int returnVal = chooser.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			if (file != null)
			{
				if ( file.exists())
				{
					if ( file.canRead() )
					{
						newFile = file;
						read(textArea, file);
					}
					else
						notification(MessageType.WARN, "File not readable: "+file);
				}
				else
					notification(MessageType.WARN, "File not found: "+file);
			}
			else
				notification(MessageType.WARN, "No file selected.");
		}
		else
			notification(MessageType.INFO, "Open canceled by user.");
		return newFile;
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
		textArea.setTabSize(DEFAULT_TAB_SIZE);
	}

	// Approved, returns non-null; otherwise, returns null.
	private File saveAsBatch(final JTextArea textArea, File currentFile) 
		throws IOException
	{
		return saveAs(textArea, currentFile, getBatchFileFilter());
	}

	// Approved, file != null; otherwise, file = null.
	private File saveAs(final JTextArea textArea, File currentFile, FileFilter filter)
		throws IOException
	{
		File file = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(filter);
		if ( currentFile != null )
			chooser.setCurrentDirectory(currentFile);
		else
			chooser.setCurrentDirectory(getCurrentWorkingDirectory());
		if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
		{
			if ((file = chooser.getSelectedFile()) != null)
				save(textArea, file);
			else
				file = currentFile;
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

	private void openURL(JTextArea textArea, Locator locator) throws IOException
	{
		textArea.setText(openURL(locator));
		textArea.setCaretPosition(0);
		textArea.setTabSize(DEFAULT_TAB_SIZE);
	}

	private void saveURL(JTextArea textArea, Locator locator) throws IOException
	{
		saveURL(locator, textArea.getText());
	}

	// Listen for undo and redo events
	@SuppressWarnings("serial")
	private void addUndoableEditListener(final JTextArea txtrBatch)
	{
		final UndoManager txtrBatchUndo = new UndoManager();
		if ( batchUndoableEditListener != null )
		{
			txtrBatch.getDocument().removeUndoableEditListener(batchUndoableEditListener);
			mntmUndo.removeActionListener(undoActionListener);
			mntmRedo.removeActionListener(redoActionListener);
		}
		batchUndoableEditListener = new UndoableEditListener() 
		{
			public void undoableEditHappened(UndoableEditEvent evt) 
			{
				txtrBatchUndo.addEdit(evt.getEdit());
			}
		};
		txtrBatch.getDocument().addUndoableEditListener(batchUndoableEditListener);
		// Create an undo action and add it to the text component
		txtrBatch.getActionMap().put
		(
			"Undo",
			new AbstractAction("Undo")
			{
				public void actionPerformed(ActionEvent evt)
				{
					try
					{
						if (txtrBatchUndo.canUndo())
							txtrBatchUndo.undo();
					}
					catch (CannotUndoException cue) 
					{
					}
				}
			}
		);
		// Bind the undo action to control-Z (or command-Z)
		txtrBatch.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 
			InputEvent.CTRL_MASK), "Undo" );
		// Add Undo listener to Undo menu item
		undoActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (txtrBatchUndo.canUndo())
						txtrBatchUndo.undo();
				}
				catch (CannotUndoException cue) 
				{
				}
			}
		};
		mntmUndo.addActionListener(undoActionListener);
		
		// Create a redo action and add it to the text component
		txtrBatch.getActionMap().put
		(
			"Redo",
			new AbstractAction("Redo")
			{
				public void actionPerformed(ActionEvent evt)
				{
					try
					{
						if (txtrBatchUndo.canRedo())
							txtrBatchUndo.redo();
					}
					catch (CannotRedoException cre) 
					{
					}
				}
			}
		);
		// Bind the redo action to control-Y
		txtrBatch.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, 
			InputEvent.CTRL_MASK), "Redo" );
		// Add Redo listener to Redo menu item
		redoActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (txtrBatchUndo.canRedo())
						txtrBatchUndo.redo();
				}
				catch (CannotRedoException cre) 
				{
				}
			}
		};
		mntmRedo.addActionListener(redoActionListener);
	}

	private class ConsoleThread extends Thread
	{
		protected JTextArea textArea;

		private ConsoleThread(JTextArea textArea)
		{
			super(ConsoleThread.class.getSimpleName());
			this.textArea = textArea;
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
					if ( textArea.getText().isEmpty() )
						lineHistory.clear();
					String line = readLine+"\n";
					textArea.append(line);
					lineHistory.add(line.length());
					if ( lineHistory.size() > getPreferredConsoleHistorySize() )
					{
						textArea.replaceRange("", 0, lineHistory.get(0));
						lineHistory.remove(0);
					}
					textArea.setCaretPosition(textArea.getText().length());
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
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
