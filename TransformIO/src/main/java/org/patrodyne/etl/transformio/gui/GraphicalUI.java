// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

import javax.script.ScriptException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextArea;
import org.patrodyne.etl.transformio.MessageType;
import org.patrodyne.etl.transformio.Transformer;
import org.patrodyne.etl.transformio.xml.Batch;
import org.patrodyne.etl.transformio.xml.Locator;
import org.patrodyne.etl.transformio.xml.Record;
import org.patrodyne.etl.transformio.xml.Record.Field;
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
	 * Set current batch file and set title.
	 * @see {@link #setCurrentBatchFile(File)}
	 */
	protected void setCurrentBatchFile(File currentBatchFile)
	{
		super.setCurrentBatchFile(currentBatchFile);
		if ( currentBatchFile != null )
			frame.setTitle(currentBatchFile.getPath());
		else
			frame.setTitle("");
	}
	
	@SuppressWarnings("serial")
	private class FieldListModel 
		extends DelegatingListModel<Record.Field>
	{
		public FieldListModel(Record record)
		{
			super(record.getFields());
		}
	}

    private void initializeIcon()
    {
        URL iconURL = this.getClass().getResource("/TransformIO.png");
        if( iconURL != null )
        {
        	ImageIcon imageIcon = new ImageIcon(iconURL);
        	frame.setIconImage(imageIcon.getImage());
        }
    }
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(Properties options)
	{
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		initializeIcon();
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

		// Batch batch = unmarshalBatch(batchTextArea.getText());
		Batch newBatch = newBatch();

		SourceTableModel sourceTableModel = new SourceTableModel(newBatch.getSource());
		JTable sourceProperties = new SourceTable(sourceTableModel);
		sourceProperties.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JButton btnSourceFieldsAdd = new JButton("+");
		btnSourceFieldsAdd.setFont(new Font("DialogInput", Font.BOLD, 12));
		JButton btnSourceFieldsRemove = new JButton("-");
		btnSourceFieldsRemove.setFont(new Font("DialogInput", Font.BOLD, 12));
		JButton btnSourceFieldsUp = new JButton("↑");
		btnSourceFieldsUp.setFont(new Font("DialogInput", Font.BOLD, 12));
		JButton btnSourceFieldsDown = new JButton("↓");
		btnSourceFieldsDown.setFont(new Font("DialogInput", Font.BOLD, 12));
		
		JPanel sourceFieldsButtonsPane = new JPanel();
		sourceFieldsButtonsPane.setLayout(new BoxLayout(sourceFieldsButtonsPane, BoxLayout.LINE_AXIS));
		sourceFieldsButtonsPane.add(btnSourceFieldsAdd);
		sourceFieldsButtonsPane.add(btnSourceFieldsRemove);
		sourceFieldsButtonsPane.add(btnSourceFieldsUp);
		sourceFieldsButtonsPane.add(btnSourceFieldsDown);
		
		// Bind the batch (instance) source record field(s) to a Swing list model.
		FieldListModel sourceFieldModel = new FieldListModel(newBatch.getSource().getRecord());
		
		final JList<Record.Field> sourceFieldList = new JList<Record.Field>(sourceFieldModel);
		sourceFieldList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sourceFieldList.setVisibleRowCount(10);
		
		JScrollPane sourceFieldListScrollPane = new JScrollPane(sourceFieldList);

		JPanel sourceFieldsForm = new JPanel();
		sourceFieldsForm.setBorder(new TitledBorder(null, "Fields", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sourceFieldsForm.setLayout(new BoxLayout(sourceFieldsForm, BoxLayout.PAGE_AXIS));
		
		sourceFieldsForm.add(sourceFieldListScrollPane);
		sourceFieldsForm.add(sourceFieldsButtonsPane);
		
		JPanel sourceRecordPane = new JPanel();
		sourceRecordPane.setBorder(new TitledBorder(null, "Source", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sourceRecordPane.setLayout(new BoxLayout(sourceRecordPane, BoxLayout.PAGE_AXIS));
		sourceRecordPane.add(sourceProperties);
		sourceRecordPane.add(Box.createVerticalStrut(10));
		sourceRecordPane.add(sourceFieldsForm);
		
		TargetTableModel targetTableModel = new TargetTableModel(newBatch.getTarget());
		JTable targetProperties = new TargetTable(targetTableModel);
		targetProperties.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JButton btnTargetFieldsAdd = new JButton("+");
		btnTargetFieldsAdd.setFont(new Font("DialogInput", Font.BOLD, 12));
		JButton btnTargetFieldsRemove = new JButton("-");
		btnTargetFieldsRemove.setFont(new Font("DialogInput", Font.BOLD, 12));
		JButton btnTargetFieldsUp = new JButton("↑");
		btnTargetFieldsUp.setFont(new Font("DialogInput", Font.BOLD, 12));
		JButton btnTargetFieldsDown = new JButton("↓");
		btnTargetFieldsDown.setFont(new Font("DialogInput", Font.BOLD, 12));
		
		JPanel targetFieldsButtonsPane = new JPanel();
		targetFieldsButtonsPane.setLayout(new BoxLayout(targetFieldsButtonsPane, BoxLayout.LINE_AXIS));
		targetFieldsButtonsPane.add(btnTargetFieldsAdd);
		targetFieldsButtonsPane.add(btnTargetFieldsRemove);
		targetFieldsButtonsPane.add(btnTargetFieldsUp);
		targetFieldsButtonsPane.add(btnTargetFieldsDown);

		// Bind the batch (instance) target record field(s) to a Swing list model.
		FieldListModel targetFieldModel = new FieldListModel(newBatch.getTarget().getRecord());
		
		final JList<Record.Field> targetFieldList = new JList<Record.Field>(targetFieldModel);
		targetFieldList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		targetFieldList.setVisibleRowCount(10);

		JScrollPane targetFieldListScrollPane = new JScrollPane(targetFieldList);

		JPanel targetFieldsForm = new JPanel();
		targetFieldsForm.setBorder(new TitledBorder(null, "Fields", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		targetFieldsForm.setLayout(new BoxLayout(targetFieldsForm, BoxLayout.PAGE_AXIS));

		targetFieldsForm.add(targetFieldListScrollPane);
		targetFieldsForm.add(targetFieldsButtonsPane);

		JPanel targetRecordPane = new JPanel();
		targetRecordPane.setBorder(new TitledBorder(null, "Target", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		targetRecordPane.setLayout(new BoxLayout(targetRecordPane, BoxLayout.PAGE_AXIS));
		targetRecordPane.add(targetProperties);
		targetRecordPane.add(Box.createVerticalStrut(10));
		targetRecordPane.add(targetFieldsForm);

		JPanel batchRecordsForm = new JPanel();
		batchRecordsForm.setLayout(new BoxLayout(batchRecordsForm, BoxLayout.LINE_AXIS));
		batchRecordsForm.add(sourceRecordPane);
		batchRecordsForm.add(Box.createHorizontalStrut(10));
		batchRecordsForm.add(targetRecordPane);

		JLabel lblEditorFieldName = new JLabel("Name");
		JLabel lblEditorFieldGet = new JLabel("Get");
		JLabel lblEditorFieldSet = new JLabel("Set");

		final JTextField fldEditorFieldName = new JTextField();
		fldEditorFieldName.setMinimumSize(new Dimension(320, 19));
		fldEditorFieldName.setColumns(40);

		final JTextField fldEditorFieldGet = new JTextField();
		fldEditorFieldGet.setMinimumSize(new Dimension(320, 19));
		fldEditorFieldGet.setColumns(40);

		final JTextField fldEditorFieldSet = new JTextField();
		fldEditorFieldSet.setMinimumSize(new Dimension(320, 19));
		fldEditorFieldSet.setColumns(40);

		JPanel recordEditorPane = new JPanel();
		recordEditorPane.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Field Editor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		recordEditorPane.setLayout(new GridBagLayout());

		GridBagConstraints recordEditor00 = new GridBagConstraints();
		recordEditor00.anchor = GridBagConstraints.EAST;
		recordEditor00.ipadx = 5;
		recordEditor00.gridx = 0;
		recordEditor00.gridy = 0;
		recordEditorPane.add(lblEditorFieldName, recordEditor00);
		GridBagConstraints recordEditor10 = new GridBagConstraints();
		recordEditor10.gridx = 1;
		recordEditor10.gridy = 0;
		recordEditorPane.add(fldEditorFieldName, recordEditor10);

		GridBagConstraints recordEditor01 = new GridBagConstraints();
		recordEditor01.anchor = GridBagConstraints.EAST;
		recordEditor01.ipadx = 5;
		recordEditor01.gridx = 0;
		recordEditor01.gridy = 1;
		recordEditorPane.add(lblEditorFieldGet, recordEditor01);
		GridBagConstraints recordEditor11 = new GridBagConstraints();
		recordEditor11.gridx = 1;
		recordEditor11.gridy = 1;
		recordEditorPane.add(fldEditorFieldGet, recordEditor11);
		
		GridBagConstraints recordEditor02 = new GridBagConstraints();
		recordEditor02.anchor = GridBagConstraints.EAST;
		recordEditor02.ipadx = 5;
		recordEditor02.gridx = 0;
		recordEditor02.gridy = 2;
		recordEditorPane.add(lblEditorFieldSet, recordEditor02);
		GridBagConstraints recordEditor12 = new GridBagConstraints();
		recordEditor12.gridx = 1;
		recordEditor12.gridy = 2;
		recordEditorPane.add(fldEditorFieldSet, recordEditor12);

		final FieldTableModel fieldTableModel = new FieldTableModel(newBatch);
		JTable tblFieldEditor = new FieldTable(fieldTableModel);
		tblFieldEditor.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		tblFieldEditor.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
		
		JPanel recordEditorPane2 = new JPanel();
		recordEditorPane2.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Field Editor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		recordEditorPane2.setLayout(new BoxLayout(recordEditorPane2, BoxLayout.PAGE_AXIS));
		recordEditorPane2.add(tblFieldEditor.getTableHeader());
		recordEditorPane2.add(tblFieldEditor);
		
		JPanel batchRecordsPane = new JPanel();
		batchRecordsPane.setLayout(new MigLayout("", "[grow]", "[grow][][]"));
		batchRecordsPane.add(batchRecordsForm, "cell 0 0, grow");
		batchRecordsPane.add(recordEditorPane, "cell 0 1, grow");
		batchRecordsPane.add(recordEditorPane2, "cell 0 2, grow");
		
		JSplitPane batchScriptPane = new JSplitPane();
		batchScriptPane.setOneTouchExpandable(true);
		batchScriptPane.setResizeWeight(DEFAULT_RESIZE_WEIGHT);
		batchScriptPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		final RSyntaxTextArea batchTextArea = new RSyntaxTextArea();
		batchTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		batchTextArea.setTabSize(DEFAULT_TAB_SIZE);
		batchTextArea.setFont(getPreferredFont());
		batchTextArea.setRows(20);
		batchTextArea.setColumns(40);
		JScrollPane batchTextPane = new JScrollPane(batchTextArea);

		JTabbedPane batchTabbedPane = new JTabbedPane();
		batchTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		
		batchTabbedPane.addTab("Records", null, batchRecordsPane, null);
		batchTabbedPane.addTab("Script", null, batchScriptPane, null);
		batchTabbedPane.addTab("Batch", null, batchTextPane, null);
		
		splitPaneBatch.setLeftComponent(batchTabbedPane);
		
		final RTextArea sourceTextArea = new RTextArea();
		sourceTextArea.setTabSize(DEFAULT_TAB_SIZE);
		sourceTextArea.setEditable(false);
		sourceTextArea.setFont(getPreferredFont());
		sourceTextArea.setLineWrap(getPreferredInitialWrapSourceData());
		sourceTextArea.setText("");
		sourceTextArea.setColumns(40);
		sourceTextArea.setRows(10);
		JScrollPane sourcePane = new JScrollPane(sourceTextArea);
		splitPaneRight.setLeftComponent(sourcePane);
		
		final RTextArea targetTextArea = new RTextArea();
		targetTextArea.setTabSize(DEFAULT_TAB_SIZE);
		targetTextArea.setEditable(false);
		targetTextArea.setFont(getPreferredFont());
		targetTextArea.setLineWrap(getPreferredInitialWrapTargetData());
		targetTextArea.setText("");
		targetTextArea.setRows(10);
		targetTextArea.setColumns(40);
		JScrollPane targetPane = new JScrollPane(targetTextArea);
		splitPaneRight.setRightComponent(targetPane);
		
		final RTextArea consoleTextArea = new RTextArea();
		consoleTextArea.setTabSize(DEFAULT_TAB_SIZE);
		consoleTextArea.setEditable(false);
		consoleTextArea.setFont(getPreferredFont());
		consoleTextArea.setLineWrap(getPreferredInitialWrapConsoleData());
		consoleTextArea.setRows(5);
		
		final Stack<RTextArea> textAreaHistory = new Stack<RTextArea>();
		textAreaHistory.push(batchTextArea);
		
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
		
		JMenuItem mntmUndo = new JMenuItem("Undo");
		mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		mnEdit.add(mntmUndo);
		
		JMenuItem mntmRedo = new JMenuItem("Redo");
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
		
		JMenuItem mntmKeys = new JMenuItem("Keys");
		mnHelp.add(mntmKeys);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		
		batchTextArea.addFocusListener
		(
			new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)	{ }
				
				@Override
				public void focusGained(FocusEvent e)
				{
					textAreaHistory.clear();
					textAreaHistory.push(batchTextArea);
				}
			}
		);
		
		sourceTextArea.addFocusListener
		(
			new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)	{ }
				
				@Override
				public void focusGained(FocusEvent e)
				{
					textAreaHistory.clear();
					textAreaHistory.push(sourceTextArea);
				}
			}
		);
		
		targetTextArea.addFocusListener
		(
			new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)	{ }
				
				@Override
				public void focusGained(FocusEvent e)
				{
					textAreaHistory.clear();
					textAreaHistory.push(targetTextArea);
				}
			}
		);
		
		consoleTextArea.addFocusListener
		(
			new FocusListener()
			{
				@Override
				public void focusLost(FocusEvent e)	{ }
				
				@Override
				public void focusGained(FocusEvent e)
				{
					textAreaHistory.clear();
					textAreaHistory.push(consoleTextArea);
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
									blank(targetTextArea);
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
		
		mntmUndo.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					textAreaHistory.peek().undoLastAction();
				}
			}
		);
		
		mntmRedo.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					textAreaHistory.peek().redoLastAction();
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
					textAreaHistory.peek().cut();
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
					textAreaHistory.peek().copy();
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
					textAreaHistory.peek().paste();
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
					catch (ScriptException se)
					{
						notification(MessageType.WARN, message(se));
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
						blank(targetTextArea);
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
						blank(consoleTextArea);
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
		
		btnSourceFieldsAdd.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					addField(sourceFieldList);
				}
			}
		);
		
		btnSourceFieldsRemove.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					removeField(sourceFieldList);
				}
			}
		);
		
		btnSourceFieldsUp.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					moveFieldsUp(sourceFieldList);
				}
			}
		);
		
		btnSourceFieldsDown.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					moveFieldsDown(sourceFieldList);
				}
			}
		);
		
		btnTargetFieldsAdd.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					addField(targetFieldList);
				}
			}
		);
		
		btnTargetFieldsRemove.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					removeField(targetFieldList);
				}
			}
		);
		
		btnTargetFieldsUp.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					moveFieldsUp(targetFieldList);
				}
			}
		);
		
		btnTargetFieldsDown.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					moveFieldsDown(targetFieldList);
				}
			}
		);
		
		sourceFieldList.addListSelectionListener
		(
			new ListSelectionListener()
			{
				@Override
				public void valueChanged(ListSelectionEvent e)
				{
					if ( !e.getValueIsAdjusting() )
					{
						@SuppressWarnings("unchecked")
						JList<Field> source = (JList<Field>) e.getSource();
						Field field = source.getSelectedValue();
						fieldTableModel.setFieldName(field.getName());
						fieldTableModel.setValueAt(field.getName(), 0, 1);
						fieldTableModel.setValueAt(field.getGet(), 1, 1);
						fieldTableModel.setValueAt(field.getSet(), 2, 1);
					}
				}
			}
		);
		
		targetFieldList.addListSelectionListener
		(
			new ListSelectionListener()
			{
				@Override
				public void valueChanged(ListSelectionEvent e)
				{
					if ( !e.getValueIsAdjusting() )
					{
						@SuppressWarnings("unchecked")
						JList<Field> source = (JList<Field>) e.getSource();
						Field field = source.getSelectedValue();
						fieldTableModel.setFieldName(field.getName());
						fieldTableModel.setValueAt(field.getName(), 0, 2);
						fieldTableModel.setValueAt(field.getGet(), 1, 2);
						fieldTableModel.setValueAt(field.getSet(), 2, 2);
					}
				}
			}
		);
		
		// Start the console.
		new ConsoleThread(consoleTextArea).start();
		// Set preferred size and position.
		frame.pack();
		frame.setBounds(getPreferredBounds());
		frame.setExtendedState(getPreferredExtendedState());
	}

	private void addField(final JList<Record.Field> fieldList)
	{
		Field newField = new Field();
		FieldListModel model = (FieldListModel) fieldList.getModel();

		// Generate the next field name of the form 'F01' 
		int newFieldNo = model.getSize();
		do
		{
			newFieldNo += 1;
			newField.setName(newFieldNo < 10 ? "F0"+newFieldNo : "F"+newFieldNo);
		} while ( model.contains(newField) );
		
		// When known, use the selected getter/setter;
		// otherwise, use the default getter/setter.
		int selectedIndex = fieldList.getSelectedIndex();
		if ( selectedIndex >= 0 )
		{
			Field selectedField = model.get(selectedIndex);
			newField.setGet(selectedField.getGet());
			newField.setSet(selectedField.getSet());
			model.add(++selectedIndex, newField);
		}
		else
		{
			newField.setGet("(.*)\n");
			newField.setSet("$1");
			model.add(newField);
			selectedIndex = model.getSize()-1;
		}
		
		// Select the new field.
		fieldList.setSelectedIndex(selectedIndex);
		fieldList.ensureIndexIsVisible(selectedIndex);
	}

	private void removeField(final JList<Record.Field> fieldList)
	{
		FieldListModel model = (FieldListModel) fieldList.getModel();
		int[] indices = fieldList.getSelectedIndices();
		if ( indices.length > 0 )
		{
			for (int index=indices.length-1; index >= 0; --index)
				model.remove(indices[index]);
			fieldList.clearSelection();
			if ( !model.isEmpty() )
			{
				int selectedIndex = indices[0];
				if (selectedIndex >= model.getSize())
					selectedIndex = model.getSize()-1;
				fieldList.setSelectedIndex(selectedIndex);
				fieldList.ensureIndexIsVisible(selectedIndex);
			}
		}
	}

	private void moveFieldsUp(final JList<Record.Field> fieldList)
	{
		FieldListModel model = (FieldListModel) fieldList.getModel();
		int[] indices = fieldList.getSelectedIndices();
		if ( indices.length > 0 )
		{
			int[] selected = new int[indices.length];
			for (int index=0; index < indices.length; ++index)
			{
				int oldIndex = indices[index];
				if ( oldIndex > 0 )
				{
					int newIndex = oldIndex - 1;
					Field tmp = model.get(newIndex);
					model.set(newIndex, model.get(oldIndex));
					model.set(oldIndex, tmp);
					selected[index] = newIndex;
				}
				else
					selected[index] = oldIndex;
			}
			fieldList.clearSelection();
			fieldList.setSelectedIndices(selected);
			fieldList.ensureIndexIsVisible(selected[0]);
		}
	}

	private void moveFieldsDown(final JList<Record.Field> fieldList)
	{
		FieldListModel model = (FieldListModel) fieldList.getModel();
		int[] indices = fieldList.getSelectedIndices();
		if ( indices.length > 0)
		{
			int[] selected = new int[indices.length];
			for (int index=indices.length-1; index >= 0; --index)
			{
				int oldIndex = indices[index];
				if ( oldIndex < model.getSize()-1 )
				{
					int newIndex = oldIndex + 1;
					Field tmp = model.get(newIndex);
					model.set(newIndex, model.get(oldIndex));
					model.set(oldIndex, tmp);
					selected[index] = newIndex;
				}
				else
					selected[index] = oldIndex;
			}
			fieldList.clearSelection();
			fieldList.setSelectedIndices(selected);
			fieldList.ensureIndexIsVisible(selected[indices.length-1]);
		}
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
		// Reset batch and source.
		setCurrentBatchHash(txtrBatch.getText().hashCode());
		setCurrentSourceHash(txtrSource.getText().hashCode());
	}
	
	private void blank(JTextArea txtrArea)
	{
		txtrArea.setText("");
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

	/**
	 * Add Cut/Copy/Paste context menu to all editable components of the given container.
	 * 
	 * @param container The component containing editable components.
	 */
	public static void addTextPopupMenu(Container container)
	{
		if (container instanceof JTextComponent)
			addTextMouseListener(container);
		for (Component comp : container.getComponents())
		{
			if (comp instanceof JTextComponent)
				addTextMouseListener(comp);
			else if (comp instanceof Container)
				addTextPopupMenu((Container) comp);
		}
	}

	private static void addTextMouseListener(Component comp)
	{
		comp.addMouseListener
		(
			new MouseAdapter()
			{
				// Linux & OSX
				public void mousePressed(final MouseEvent e)
				{
					addTextPopupMenuItems(e);
				}
				// Other
				public void mouseReleased(final MouseEvent e)
				{
					addTextPopupMenuItems(e);
				}
			}
		);
	}

	private static void addTextPopupMenuItems(final MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			final JTextComponent component = (JTextComponent) e.getComponent();
			final JPopupMenu menu = new JPopupMenu();
			JMenuItem item;
			// Cut
			item = new JMenuItem(new DefaultEditorKit.CutAction());
			item.setText("Cut");
			item.setEnabled(component.isEditable()
				&& component.getSelectionStart() != component.getSelectionEnd());
			menu.add(item);
			// Copy
			item = new JMenuItem(new DefaultEditorKit.CopyAction());
			item.setText("Copy");
			item.setEnabled(component.getSelectionStart() != component.getSelectionEnd());
			menu.add(item);
			// Paste
			item = new JMenuItem(new DefaultEditorKit.PasteAction());
			item.setText("Paste");
			item.setEnabled(component.isEditable());
			menu.add(item);
			// Show
			menu.show(e.getComponent(), e.getX(), e.getY());
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
