// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.gui;

import static org.patrodyne.etl.transformio.PreferenceKey.BoundHeight;
import static org.patrodyne.etl.transformio.PreferenceKey.BoundWidth;
import static org.patrodyne.etl.transformio.PreferenceKey.BoundX;
import static org.patrodyne.etl.transformio.PreferenceKey.BoundY;
import static org.patrodyne.etl.transformio.PreferenceKey.ConsoleHistorySize;
import static org.patrodyne.etl.transformio.PreferenceKey.ExtendedState;
import static org.patrodyne.etl.transformio.PreferenceKey.FontFamily;
import static org.patrodyne.etl.transformio.PreferenceKey.FontSize;
import static org.patrodyne.etl.transformio.PreferenceKey.FontStyle;
import static org.patrodyne.etl.transformio.PreferenceKey.InitialSourceEditMode;
import static org.patrodyne.etl.transformio.PreferenceKey.InitialWrapSourceData;
import static org.patrodyne.etl.transformio.PreferenceKey.InitialWrapTargetData;
import static org.patrodyne.etl.transformio.PreferenceKey.InitialWrapConsoleData;
import static org.patrodyne.etl.transformio.PreferenceKey.LookAndFeel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The Class PreferencesManager is a JDialog to configure preferences.
 * 
 * @author Rick O'Sullivan
 */
public class PreferencesManager
	extends JDialog
{
	/** Represents serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private static final String FONT_STYLE_PLAIN = "Plain";
	private static final String FONT_STYLE_BOLD = "Bold";
	private static final String FONT_STYLE_ITALIC = "Italic";
	private static final String[] FONT_STYLE_LIST = new String[] { FONT_STYLE_PLAIN, FONT_STYLE_BOLD, FONT_STYLE_ITALIC };
	private static final String[] FONT_SIZE_LIST = new String[] 
	{
	 	"08", "09", "10", "11", "12", 
	 	"13", "14", "15", "16", "18",
	 	"20", "22", "24", "28", "30"
	};
	private static final String FONT_SAMPLE_TEXT = "The quick brown fox jumped over the lazy dog.";
	private static final Font DEFAULT_FONT = new Font("Courier New", Font.PLAIN, 15);
	private static final Rectangle DEFAULT_BOUNDS = new Rectangle(50, 50, 800, 600);
	private static final int DEFAULT_CONSOLE_HISTORY_SIZE = 100;

	private static String preferencesPath;
	/**
	 * Gets the preferences path.
	 *
	 * @return the preferences path
	 */
	protected static String getPreferencesPath()
	{
		return preferencesPath;
	}
	/**
	 * Sets the preferences path.
	 *
	 * @param preferencesPath the new preferences path
	 */
	protected static void setPreferencesPath(String preferencesPath)
	{
		PreferencesManager.preferencesPath = preferencesPath;
	}

	private static Preferences preferences;
	/**
	 * Gets the preferences.
	 *
	 * @return the preferences
	 */
	protected static Preferences getPreferences()
	{
		if ( preferences == null )
			setPreferences(Preferences.userRoot().node(getPreferencesPath()));
		return preferences;
	}
	/**
	 * Sets the preferences.
	 *
	 * @param preferences the new preferences
	 */
	protected static void setPreferences(Preferences preferences)
	{
		PreferencesManager.preferences = preferences;
	}
	
	private static Integer preferredConsoleHistorySize;
	/**
	 * Gets the preferred console history size.
	 *
	 * @return the preferred console history size
	 */
	protected static Integer getPreferredConsoleHistorySize()
	{
		if ( preferredConsoleHistorySize == null )
			preferredConsoleHistorySize = getPreferences().getInt(ConsoleHistorySize.name(), 
				DEFAULT_CONSOLE_HISTORY_SIZE);
		return preferredConsoleHistorySize;
	}
	/**
	 * Sets the preferred console history size.
	 *
	 * @param preferredConsoleHistorySize the new preferred console history size
	 */
	protected static void setPreferredConsoleHistorySize(Integer preferredConsoleHistorySize)
	{
		getPreferences().putInt(ConsoleHistorySize.name(), preferredConsoleHistorySize);
		PreferencesManager.preferredConsoleHistorySize = preferredConsoleHistorySize;
	}
	/**
	 * Sets the preferred console history size.
	 *
	 * @param preferredConsoleHistorySize the new preferred console history size
	 */
	protected static void setPreferredConsoleHistorySize(String preferredConsoleHistorySize)
	{
		try
		{
			setPreferredConsoleHistorySize(Integer.parseInt(preferredConsoleHistorySize));
		}
		catch ( NumberFormatException nfe)
		{
			// No update.
		}
	}

	private static Rectangle preferredBounds = null;
	/**
	 * Gets the preferred bounds.
	 *
	 * @return the preferred bounds
	 */
	protected static Rectangle getPreferredBounds()
	{
		if ( preferredBounds == null )
		{
			int boundX = getPreferences().getInt(BoundX.name(), DEFAULT_BOUNDS.x);
			int boundY = getPreferences().getInt(BoundY.name(), DEFAULT_BOUNDS.y);
			int boundW = getPreferences().getInt(BoundWidth.name(), DEFAULT_BOUNDS.width);
			int boundH = getPreferences().getInt(BoundHeight.name(), DEFAULT_BOUNDS.height);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if (boundW > screenSize.width) boundW = screenSize.width;
			if (boundH > screenSize.height) boundH = screenSize.height;
			if (boundX + boundW > screenSize.width) boundX = screenSize.width - boundW;
			if (boundY + boundH > screenSize.height) boundY = screenSize.height - boundH;
			preferredBounds = new Rectangle(boundX, boundY, boundW, boundH);
		}
		return preferredBounds;
	}
	/**
	 * Sets the preferred bounds.
	 *
	 * @param preferredBounds the new preferred bounds
	 */
	protected static void setPreferredBounds(Rectangle preferredBounds)
	{
		getPreferences().putInt(BoundX.name(), preferredBounds.x);
		getPreferences().putInt(BoundY.name(), preferredBounds.y);
		getPreferences().putInt(BoundWidth.name(), preferredBounds.width);
		getPreferences().putInt(BoundHeight.name(), preferredBounds.height);
		PreferencesManager.preferredBounds = preferredBounds;
	}

	private static Integer preferredExtendedState;
	/**
	 * Gets the preferred extended state.
	 *
	 * @return the preferred extended state
	 */
	protected static Integer getPreferredExtendedState()
	{
		if ( preferredExtendedState == null )
			preferredExtendedState = getPreferences().getInt(ExtendedState.name(), JFrame.NORMAL);
		return preferredExtendedState;
	}
	/**
	 * Sets the preferred extended state.
	 *
	 * @param preferredExtendedState the new preferred extended state
	 */
	protected static void setPreferredExtendedState(Integer preferredExtendedState)
	{
		getPreferences().putInt(ExtendedState.name(), preferredExtendedState);
		PreferencesManager.preferredExtendedState = preferredExtendedState;
	}

	private static Boolean preferredInitialSourceEditMode;
	/**
	 * Gets the preferred initial source edit mode.
	 *
	 * @return the preferred initial source edit mode
	 */
	protected static Boolean getPreferredInitialSourceEditMode()
	{
		if ( preferredInitialSourceEditMode == null )
		{
			preferredInitialSourceEditMode =
				getPreferences().getBoolean(InitialSourceEditMode.name(), false);
		}
		return preferredInitialSourceEditMode;
	}
	/**
	 * Sets the preferred initial source edit mode.
	 *
	 * @param preferredInitialSourceEditMode the new preferred initial source edit mode
	 */
	protected static void setPreferredInitialSourceEditMode(Boolean preferredInitialSourceEditMode)
	{
		getPreferences().putBoolean(InitialSourceEditMode.name(), preferredInitialSourceEditMode);
		PreferencesManager.preferredInitialSourceEditMode = preferredInitialSourceEditMode;
	}
	
	private static Boolean preferredInitialWrapSourceData;
	/**
	 * Gets the preferred initial wrap source data.
	 *
	 * @return the preferred initial wrap source data
	 */
	protected static Boolean getPreferredInitialWrapSourceData()
	{
		if ( preferredInitialWrapSourceData == null )
		{
			preferredInitialWrapSourceData =
				getPreferences().getBoolean(InitialWrapSourceData.name(), false);
		}
		return preferredInitialWrapSourceData;
	}
	/**
	 * Sets the preferred initial wrap source data.
	 *
	 * @param preferredInitialWrapSourceData the new preferred initial wrap source data
	 */
	protected static void setPreferredInitialWrapSourceData(Boolean preferredInitialWrapSourceData)
	{
		getPreferences().putBoolean(InitialWrapSourceData.name(), preferredInitialWrapSourceData);
		PreferencesManager.preferredInitialWrapSourceData = preferredInitialWrapSourceData;
	}
	
	private static Boolean preferredInitialWrapTargetData;
	/**
	 * Gets the preferred initial wrap target data.
	 *
	 * @return the preferred initial wrap target data
	 */
	protected static Boolean getPreferredInitialWrapTargetData()
	{
		if ( preferredInitialWrapTargetData == null )
		{
			preferredInitialWrapTargetData =
				getPreferences().getBoolean(InitialWrapTargetData.name(), false);
		}
		return preferredInitialWrapTargetData;
	}
	/**
	 * Sets the preferred initial wrap target data.
	 *
	 * @param preferredInitialWrapTargetData the new preferred initial wrap target data
	 */
	protected static void setPreferredInitialWrapTargetData(Boolean preferredInitialWrapTargetData)
	{
		getPreferences().putBoolean(InitialWrapTargetData.name(), preferredInitialWrapTargetData);
		PreferencesManager.preferredInitialWrapTargetData = preferredInitialWrapTargetData;
	}
	
	private static Boolean preferredInitialWrapConsoleData;
	/**
	 * Gets the preferred initial wrap console data.
	 *
	 * @return the preferred initial wrap console data
	 */
	protected static Boolean getPreferredInitialWrapConsoleData()
	{
		if ( preferredInitialWrapConsoleData == null )
		{
			preferredInitialWrapConsoleData =
				getPreferences().getBoolean(InitialWrapConsoleData.name(), false);
		}
		return preferredInitialWrapConsoleData;
	}
	/**
	 * Sets the preferred initial wrap console data.
	 *
	 * @param preferredInitialWrapConsoleData the new preferred initial wrap console data
	 */
	protected static void setPreferredInitialWrapConsoleData(Boolean preferredInitialWrapConsoleData)
	{
		getPreferences().putBoolean(InitialWrapConsoleData.name(), preferredInitialWrapConsoleData);
		PreferencesManager.preferredInitialWrapConsoleData = preferredInitialWrapConsoleData;
	}
	
	private static Font preferredFont;
	/**
	 * Gets the preferred font.
	 *
	 * @return the preferred font
	 */
	protected static Font getPreferredFont()
	{
		if ( preferredFont == null)
		{
			String fontFamily = getPreferences().get(FontFamily.name(), DEFAULT_FONT.getFamily());
			int fontSize = getPreferences().getInt(FontSize.name(), DEFAULT_FONT.getSize());
			int fontStyle = getPreferences().getInt(FontStyle.name(), DEFAULT_FONT.getStyle());
			preferredFont = new Font(fontFamily, fontStyle, fontSize);
		}
		return preferredFont;
	}
	/**
	 * Sets the preferred font.
	 *
	 * @param preferredFont the new preferred font
	 */
	protected static void setPreferredFont(Font preferredFont)
	{
		getPreferences().put(FontFamily.name(), preferredFont.getFamily());
		getPreferences().putInt(FontSize.name(), preferredFont.getSize());
		getPreferences().putInt(FontStyle.name(), preferredFont.getStyle());
		PreferencesManager.preferredFont = preferredFont;
	}

	private static String preferredLookAndFeel;
	/**
	 * Gets the preferred look and feel.
	 *
	 * @return the preferred look and feel
	 */
	protected static String getPreferredLookAndFeel()
	{
		if ( preferredLookAndFeel == null )
			preferredLookAndFeel = getPreferences().get(LookAndFeel.name(), UIManager.getSystemLookAndFeelClassName());
		return preferredLookAndFeel;
	}
	/**
	 * Sets the preferred look and feel.
	 *
	 * @param preferredLookAndFeel the new preferred look and feel
	 */
	protected static void setPreferredLookAndFeel(String preferredLookAndFeel)
	{
		getPreferences().put(LookAndFeel.name(), preferredLookAndFeel);
		PreferencesManager.preferredLookAndFeel = preferredLookAndFeel;
	}

	/**
	 * Create the dialog.
	 *
	 * @param parent the parent
	 * @param preferencesPath the preferences path
	 */
	public PreferencesManager(Frame parent, String preferencesPath)
	{
		super(parent, true);
		setPreferencesPath(preferencesPath);
		setResizable(false);
		setTitle("PreferencesManager");
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 15));
		getContentPane().add(contentPanel);
		final JTextField txtfldConsoleHistoryLines = new JTextField();
		final JCheckBox chckbxInitialSourceEditMode = new JCheckBox("initially allow editing of source data");
		final JCheckBox chckbxInitialWrapSourceData = new JCheckBox("initially wrap data in the source pane");
		final JCheckBox chckbxInitialWrapTargetData = new JCheckBox("initially wrap data in the target pane");
		final JCheckBox chckbxInitialWrapConsoleData = new JCheckBox("initially wrap data in the console pane");
		final JTextArea txtrFontSample = new JTextArea();
		txtrFontSample.setWrapStyleWord(true);
		txtrFontSample.setLineWrap(true);
		txtrFontSample.setEditable(false);
		txtrFontSample.setText(FONT_SAMPLE_TEXT);
		final JComboBox<LookAndFeelItem> listLookAndFeel = new JComboBox<LookAndFeelItem>(getLookAndFeelList());
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.LINE_AXIS));
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane);
			{
				JPanel generalPanel = new JPanel();
				tabbedPane.addTab("General", null, generalPanel, null);
				GridBagLayout gbl_generalPanel = new GridBagLayout();
				gbl_generalPanel.columnWidths = new int[]{175, 205, 0};
				gbl_generalPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
				gbl_generalPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
				gbl_generalPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
				generalPanel.setLayout(gbl_generalPanel);
				{
					JLabel lblConsoleHistory = new JLabel("Console History");
					GridBagConstraints gbc_lblConsoleHistory = new GridBagConstraints();
					gbc_lblConsoleHistory.anchor = GridBagConstraints.WEST;
					gbc_lblConsoleHistory.insets = new Insets(0, 0, 5, 5);
					gbc_lblConsoleHistory.gridx = 0;
					gbc_lblConsoleHistory.gridy = 0;
					generalPanel.add(lblConsoleHistory, gbc_lblConsoleHistory);
				}
				{
					JPanel consoleHistoryPanel = new JPanel();
					GridBagConstraints gbc_consoleHistoryPanel = new GridBagConstraints();
					gbc_consoleHistoryPanel.anchor = GridBagConstraints.WEST;
					gbc_consoleHistoryPanel.insets = new Insets(0, 0, 5, 0);
					gbc_consoleHistoryPanel.gridx = 1;
					gbc_consoleHistoryPanel.gridy = 0;
					generalPanel.add(consoleHistoryPanel, gbc_consoleHistoryPanel);
					consoleHistoryPanel.setLayout(new BoxLayout(consoleHistoryPanel, BoxLayout.LINE_AXIS));
					{
						txtfldConsoleHistoryLines.setHorizontalAlignment(SwingConstants.TRAILING);
						txtfldConsoleHistoryLines.setText(""+getPreferredConsoleHistorySize());
						consoleHistoryPanel.add(txtfldConsoleHistoryLines);
						txtfldConsoleHistoryLines.setColumns(5);
					}
					{
						JLabel lblConsoleHistoryLines = new JLabel(" lines");
						consoleHistoryPanel.add(lblConsoleHistoryLines);
					}
				}
				{
					JLabel lblSourceEditMode = new JLabel("Source Edit Mode");
					GridBagConstraints gbc_lblSourceEditMode = new GridBagConstraints();
					gbc_lblSourceEditMode.anchor = GridBagConstraints.WEST;
					gbc_lblSourceEditMode.insets = new Insets(0, 0, 5, 5);
					gbc_lblSourceEditMode.gridx = 0;
					gbc_lblSourceEditMode.gridy = 1;
					generalPanel.add(lblSourceEditMode, gbc_lblSourceEditMode);
				}
				{
					chckbxInitialSourceEditMode.setSelected(getPreferredInitialSourceEditMode());
					GridBagConstraints gbc_chckbxInitialSourceEditMode = new GridBagConstraints();
					gbc_chckbxInitialSourceEditMode.anchor = GridBagConstraints.WEST;
					gbc_chckbxInitialSourceEditMode.insets = new Insets(0, 0, 5, 0);
					gbc_chckbxInitialSourceEditMode.gridx = 1;
					gbc_chckbxInitialSourceEditMode.gridy = 1;
					generalPanel.add(chckbxInitialSourceEditMode, gbc_chckbxInitialSourceEditMode);
				}
				{
					JLabel lblSourceWrapMode = new JLabel("Source Wrap Mode");
					GridBagConstraints gbc_lblSourceWrapMode = new GridBagConstraints();
					gbc_lblSourceWrapMode.insets = new Insets(0, 0, 5, 5);
					gbc_lblSourceWrapMode.anchor = GridBagConstraints.WEST;
					gbc_lblSourceWrapMode.gridx = 0;
					gbc_lblSourceWrapMode.gridy = 2;
					generalPanel.add(lblSourceWrapMode, gbc_lblSourceWrapMode);
				}
				{
					chckbxInitialWrapSourceData.setSelected(getPreferredInitialWrapSourceData());
					GridBagConstraints gbc_chckbxInitialWrapSourceData = new GridBagConstraints();
					gbc_chckbxInitialWrapSourceData.anchor = GridBagConstraints.WEST;
					gbc_chckbxInitialWrapSourceData.insets = new Insets(0, 0, 5, 0);
					gbc_chckbxInitialWrapSourceData.gridx = 1;
					gbc_chckbxInitialWrapSourceData.gridy = 2;
					generalPanel.add(chckbxInitialWrapSourceData, gbc_chckbxInitialWrapSourceData);
				}
				{
					JLabel lblTargetWrapMode = new JLabel("Target Wrap Mode");
					GridBagConstraints gbc_lblTargetWrapMode = new GridBagConstraints();
					gbc_lblTargetWrapMode.anchor = GridBagConstraints.WEST;
					gbc_lblTargetWrapMode.insets = new Insets(0, 0, 5, 5);
					gbc_lblTargetWrapMode.gridx = 0;
					gbc_lblTargetWrapMode.gridy = 3;
					generalPanel.add(lblTargetWrapMode, gbc_lblTargetWrapMode);
				}
				{
					chckbxInitialWrapTargetData.setSelected(getPreferredInitialWrapTargetData());
					GridBagConstraints gbc_chckbxInitialWrapTargetData = new GridBagConstraints();
					gbc_chckbxInitialWrapTargetData.anchor = GridBagConstraints.WEST;
					gbc_chckbxInitialWrapTargetData.insets = new Insets(0, 0, 5, 0);
					gbc_chckbxInitialWrapTargetData.gridx = 1;
					gbc_chckbxInitialWrapTargetData.gridy = 3;
					generalPanel.add(chckbxInitialWrapTargetData, gbc_chckbxInitialWrapTargetData);
				}
				{
					JLabel lblConsoleWrapMode = new JLabel("Console Wrap Mode");
					GridBagConstraints gbc_lblConsoleWrapMode = new GridBagConstraints();
					gbc_lblConsoleWrapMode.anchor = GridBagConstraints.WEST;
					gbc_lblConsoleWrapMode.insets = new Insets(0, 0, 5, 5);
					gbc_lblConsoleWrapMode.gridx = 0;
					gbc_lblConsoleWrapMode.gridy = 4;
					generalPanel.add(lblConsoleWrapMode, gbc_lblConsoleWrapMode);
				}
				{
					chckbxInitialWrapConsoleData.setSelected(getPreferredInitialWrapConsoleData());
					GridBagConstraints gbc_chckbxInitialWrapConsoleData = new GridBagConstraints();
					gbc_chckbxInitialWrapConsoleData.anchor = GridBagConstraints.WEST;
					gbc_chckbxInitialWrapConsoleData.insets = new Insets(0, 0, 5, 0);
					gbc_chckbxInitialWrapConsoleData.gridx = 1;
					gbc_chckbxInitialWrapConsoleData.gridy = 4;
					generalPanel.add(chckbxInitialWrapConsoleData, gbc_chckbxInitialWrapConsoleData);
				}
				{
					JLabel lblLookAndFeel = new JLabel("Look and Feel");
					GridBagConstraints gbc_lblLookAndFeel = new GridBagConstraints();
					gbc_lblLookAndFeel.anchor = GridBagConstraints.WEST;
					gbc_lblLookAndFeel.insets = new Insets(0, 0, 0, 5);
					gbc_lblLookAndFeel.gridx = 0;
					gbc_lblLookAndFeel.gridy = 5;
					generalPanel.add(lblLookAndFeel, gbc_lblLookAndFeel);
				}
				{
					JPanel lookedAndFeelPanel = new JPanel();
					GridBagConstraints gbc_lookedAndFeelPanel = new GridBagConstraints();
					gbc_lookedAndFeelPanel.anchor = GridBagConstraints.WEST;
					gbc_lookedAndFeelPanel.fill = GridBagConstraints.VERTICAL;
					gbc_lookedAndFeelPanel.gridx = 1;
					gbc_lookedAndFeelPanel.gridy = 5;
					generalPanel.add(lookedAndFeelPanel, gbc_lookedAndFeelPanel);
					{
						listLookAndFeel.setSelectedItem(getPreferredLookAndFeelItem());
						listLookAndFeel.setMaximumRowCount(4);
						GridBagConstraints gbc_listLookAndFeel = new GridBagConstraints();
						gbc_listLookAndFeel.anchor = GridBagConstraints.WEST;
						gbc_listLookAndFeel.gridx = 1;
						gbc_listLookAndFeel.gridy = 5;
						lookedAndFeelPanel.add(listLookAndFeel, gbc_listLookAndFeel);
					}
					{
						JLabel lblInitialTheme = new JLabel("restart recommended");
						GridBagConstraints gbc_lblInitialTheme = new GridBagConstraints();
						gbc_lblInitialTheme.anchor = GridBagConstraints.WEST;
						gbc_lblInitialTheme.insets = new Insets(0, 0, 0, 5);
						gbc_lblInitialTheme.gridx = 2;
						gbc_lblInitialTheme.gridy = 5;
						lookedAndFeelPanel.add(lblInitialTheme, gbc_lblInitialTheme);
					}
				}
			}
			{
				JPanel fontPanel = new JPanel();
				tabbedPane.addTab("Font", null, fontPanel, null);
				fontPanel.setLayout(new BoxLayout(fontPanel, BoxLayout.PAGE_AXIS));
				{
					JPanel fontListsPanel = new JPanel();
					fontPanel.add(fontListsPanel);
					final JTextField txtfldFontFamily = new JTextField(getPreferredFont().getFamily());
					txtfldFontFamily.setFont(new Font("Dialog", Font.PLAIN, 12));
					txtfldFontFamily.setEditable(false);
					txtfldFontFamily.setOpaque(true);
					txtfldFontFamily.setForeground(Color.BLACK);
					txtfldFontFamily.setBackground(Color.WHITE);
					final JTextField txtfldFontSize = new JTextField(""+getPreferredFont().getSize());
					txtfldFontSize.setColumns(2);
					txtfldFontSize.setOpaque(true);
					txtfldFontSize.setForeground(Color.BLACK);
					txtfldFontSize.setFont(new Font("Dialog", Font.PLAIN, 12));
					txtfldFontSize.setEditable(false);
					txtfldFontSize.setBackground(Color.WHITE);
					final JTextField txtfldFontStyle = new JTextField(FONT_STYLE_LIST[getPreferredFont().getStyle()]);
					txtfldFontStyle.setOpaque(true);
					txtfldFontStyle.setForeground(Color.BLACK);
					txtfldFontStyle.setFont(new Font("Dialog", Font.PLAIN, 12));
					txtfldFontStyle.setEditable(false);
					txtfldFontStyle.setBackground(Color.WHITE);
					fontListsPanel.setLayout(new BoxLayout(fontListsPanel, BoxLayout.LINE_AXIS));
					{
						JPanel fontFamilySelectorPanel = new JPanel();
						fontFamilySelectorPanel.setBorder(new EmptyBorder(20, 20, 20, 0));
						fontListsPanel.add(fontFamilySelectorPanel);
						fontFamilySelectorPanel.setLayout(new BoxLayout(fontFamilySelectorPanel, BoxLayout.PAGE_AXIS));
						fontFamilySelectorPanel.add(txtfldFontFamily);
						{
							JSeparator fontFamilySeparator = new JSeparator();
							fontFamilySelectorPanel.add(fontFamilySeparator);
						}
						{
							JScrollPane fontFamilyScrollPane = new JScrollPane();
							fontFamilySelectorPanel.add(fontFamilyScrollPane);
							{
								final JList<String> fontFamilyList = new JList<String>(getFontFamilyNames());
								fontFamilyScrollPane.setViewportView(fontFamilyList);
								fontFamilyList.setSelectedValue(txtfldFontFamily.getText(), true);
								fontFamilyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
								fontFamilyList.addListSelectionListener
								(
									new ListSelectionListener()
									{
										@Override
										public void valueChanged(ListSelectionEvent e)
										{
											txtfldFontFamily.setText((String) fontFamilyList.getSelectedValue());
											displayFontSample(txtrFontSample, txtfldFontFamily, txtfldFontSize, txtfldFontStyle);
										}
									}
								);
							}
						}
					}
					{
						JPanel fontSizeSelectorPanel = new JPanel();
						fontSizeSelectorPanel.setBorder(new EmptyBorder(20, 20, 20, 0));
						fontListsPanel.add(fontSizeSelectorPanel);
						fontSizeSelectorPanel.setLayout(new BoxLayout(fontSizeSelectorPanel, BoxLayout.PAGE_AXIS));
						fontSizeSelectorPanel.add(txtfldFontSize);
						{
							JSeparator fontSizeSeparator = new JSeparator();
							fontSizeSelectorPanel.add(fontSizeSeparator);
						}
						{
							JScrollPane fontSizeScrollPane = new JScrollPane();
							fontSizeSelectorPanel.add(fontSizeScrollPane);
							{
								final JList<String> fontSizeList = new JList<String>(FONT_SIZE_LIST);
								fontSizeScrollPane.setViewportView(fontSizeList);
								fontSizeList.setSelectedValue(txtfldFontSize.getText(), true);
								fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
								fontSizeList.addListSelectionListener
								(
									new ListSelectionListener()
									{
										@Override
										public void valueChanged(ListSelectionEvent e)
										{
											txtfldFontSize.setText((String) fontSizeList.getSelectedValue());
											displayFontSample(txtrFontSample, txtfldFontFamily, txtfldFontSize, txtfldFontStyle);
										}
									}
								);
							}
						}
					}
					{
						JPanel fontStyleSelectorPanel = new JPanel();
						fontStyleSelectorPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
						fontListsPanel.add(fontStyleSelectorPanel);
						fontStyleSelectorPanel.setLayout(new BoxLayout(fontStyleSelectorPanel, BoxLayout.PAGE_AXIS));
						fontStyleSelectorPanel.add(txtfldFontStyle);
						{
							JSeparator fontStyleSeparator = new JSeparator();
							fontStyleSelectorPanel.add(fontStyleSeparator);
						}
						{
							JScrollPane fontStyleScrollPane = new JScrollPane();
							fontStyleSelectorPanel.add(fontStyleScrollPane);
							{
								final JList<String> fontStyleList = new JList<String>(FONT_STYLE_LIST);
								fontStyleScrollPane.setViewportView(fontStyleList);
								fontStyleList.setSelectedValue(txtfldFontStyle.getText(), true);
								fontStyleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
								fontStyleList.addListSelectionListener
								(
									new ListSelectionListener()
									{
										@Override
										public void valueChanged(ListSelectionEvent e)
										{
											txtfldFontStyle.setText((String) fontStyleList.getSelectedValue());
											displayFontSample(txtrFontSample, txtfldFontFamily, txtfldFontSize, txtfldFontStyle);
										}
									}
								);
							}
						}
					}
					displayFontSample(txtrFontSample, txtfldFontFamily, txtfldFontSize, txtfldFontStyle);
				}
				fontPanel.add(txtrFontSample);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
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
							setPreferredFont(txtrFontSample.getFont());
							setPreferredConsoleHistorySize(txtfldConsoleHistoryLines.getText());
							setPreferredInitialSourceEditMode(chckbxInitialSourceEditMode.isSelected());
							setPreferredInitialWrapSourceData(chckbxInitialWrapSourceData.isSelected());
							setPreferredInitialWrapTargetData(chckbxInitialWrapTargetData.isSelected());
							setPreferredInitialWrapConsoleData(chckbxInitialWrapConsoleData.isSelected());
							LookAndFeelItem lookAndFeelItem = (LookAndFeelItem) listLookAndFeel.getSelectedItem();
							setPreferredLookAndFeel(lookAndFeelItem.getLookAndFeelInfo().getClassName());
							setVisible(false);
							dispose();
						}
					}
				);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener
				(
					new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							setVisible(false);
							dispose();
						}
					}
				);
			}
		}
		pack();
		setLocationRelativeTo(parent);
	}

	private static LookAndFeelItem[] lookAndFeelInfoList;
	private static LookAndFeelItem[] getLookAndFeelList()
	{
		if ( lookAndFeelInfoList == null )
		{
			int listSize = UIManager.getInstalledLookAndFeels().length;
			lookAndFeelInfoList = new LookAndFeelItem[listSize];
			for ( int i=0; i < listSize; ++i)
			{
				LookAndFeelInfo lookAndFeelInfo = UIManager.getInstalledLookAndFeels()[i];
				lookAndFeelInfoList[i] = new LookAndFeelItem(lookAndFeelInfo);
			}
		}
		return lookAndFeelInfoList;
	}

	private static LookAndFeelItem getPreferredLookAndFeelItem()
	{
		LookAndFeelItem preferredLookAndFeelItem = getLookAndFeelList()[0];
		for ( LookAndFeelItem lookAndFeelItem : getLookAndFeelList() )
		{
			LookAndFeelInfo lookAndFeelInfo = lookAndFeelItem.getLookAndFeelInfo();
			if ( getPreferredLookAndFeel().equals(lookAndFeelInfo.getClassName()) )
			{
				preferredLookAndFeelItem = lookAndFeelItem;
				break;
			}
		}
		return preferredLookAndFeelItem;
	}

	private static String[] fontFamilyNames;
	private static String[] getFontFamilyNames()
	{
		if ( fontFamilyNames == null )
		{
			List<String> fontFamilyNameList = new ArrayList<String>();
			for ( String fontFamilyName : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames() )
			{
				Font font = new Font(fontFamilyName, 10, Font.PLAIN);
				if ( font.canDisplay('A') )
					fontFamilyNameList.add(fontFamilyName);
			}
			fontFamilyNames = fontFamilyNameList.toArray(new String[fontFamilyNameList.size()]);
		}
		return fontFamilyNames;
	}

	/**
	 * Display font sample.
	 *
	 * @param txtrFontSample the text area font sample
	 * @param txtfldFontFamily the test field font family
	 * @param txtfldFontSize the test field font size
	 * @param txtfldFontStyle the test field font style
	 */
	protected void displayFontSample(JTextArea txtrFontSample, JTextField txtfldFontFamily, JTextField txtfldFontSize, JTextField txtfldFontStyle)
	{
		String fontFamily = txtfldFontFamily.getText();
		int fontSize = Integer.parseInt(txtfldFontSize.getText());
		int fontStyle = FONT_STYLE_BOLD.equals(txtfldFontStyle.getText()) ? Font.BOLD :
			FONT_STYLE_ITALIC.equals(txtfldFontStyle.getText()) ? Font.ITALIC :Font.PLAIN;	
		txtrFontSample.setFont(new Font(fontFamily, fontStyle, fontSize));
		txtrFontSample.setText(FONT_SAMPLE_TEXT);
		pack();
	}
}

