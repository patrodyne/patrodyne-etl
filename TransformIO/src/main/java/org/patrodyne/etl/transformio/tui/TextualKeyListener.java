// PatroDyne: Patron Supported Dynamic Executables
// Released under LGPL license. See terms at http://www.gnu.org.
package org.patrodyne.etl.transformio.tui;

import org.patrodyne.etl.transformio.MessageType;
import org.patrodyne.etl.transformio.Transformer;

import charva.awt.event.KeyEvent;
import charva.awt.event.KeyListener;
import charvax.swing.JTextArea;

/**
 * Implement a clip board for the Textual User Interface.
 *
 * @see TextualKeyEvent
 * 
 * @author Rick O'Sullivan
 */
public class TextualKeyListener
	implements KeyListener
{
	private static final int CTRL_C=3, CTRL_K=11, CTRL_V=22, CTRL_X=24;

	private JTextArea textArea;
	
	/**
	 * Gets the text area.
	 *
	 * @return the text area
	 */
	protected JTextArea getTextArea()	{ return textArea; 	}
	private void setTextArea(JTextArea textArea)	{ this.textArea = textArea;	}

	private TextualClipboard clipboard;
	/**
	 * Gets the clipboard.
	 *
	 * @return the clipboard
	 */
	protected TextualClipboard getClipboard() { return clipboard; }
	private void setClipboard(TextualClipboard clipboard) { this.clipboard = clipboard; }

	private int mark = -1;
	/**
	 * Gets the mark.
	 *
	 * @return the mark
	 */
	protected int getMark()	{ return mark;	}
	/**
	 * Sets the mark.
	 *
	 * @param mark the new mark
	 */
	protected void setMark(int mark) { this.mark = mark; }
	
	/**
	 * Gets the clip.
	 *
	 * @return the clip
	 */
	protected String getClip() { return getClipboard().getClip(); }
	/**
	 * Sets the clip.
	 *
	 * @param clip the new clip
	 */
	protected void setClip(String clip) { getClipboard().setClip(clip); }
	
	/**
	 * Gets the caret.
	 *
	 * @return the caret
	 */
	protected int getCaret() { return getTextArea().getCaretPosition(); }
	
	/**
	 * Key pressed.
	 *
	 * @param keyEvent the key event
	 * @see charva.awt.event.KeyListener#keyPressed(charva.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent keyEvent)
	{
		if ( getTextArea().isEditable() )
		{
			switch ( keyEvent.getKeyCode() )
			{
				case CTRL_K: mark(); break;
				case CTRL_X: cut();	break;
				case CTRL_C: copy(); break;
				case CTRL_V: paste(); break;
				default: break;
			}
		}
	}
	
	/**
	 * Mark the caret position.
	 */
	public void mark()
	{
		if ( getTextArea().isEditable() )
			setMark(getTextArea().getCaretPosition());
			
	}

	/**
	 * Cut from the caret position.
	 */
	public void cut()
	{
		if ( getTextArea().isEditable() )
			setClip(cut(sort(getMark(), getCaret())));
	}
	
	/**
	 * Copy from the caret position.
	 */
	public void copy()
	{
		if ( getTextArea().isEditable() )
			setClip(copy(sort(getMark(), getCaret())));
	}
	
	/**
	 * Paste at the caret position.
	 */
	public void paste()
	{
		if ( getTextArea().isEditable() )
			getTextArea().insert(getClip(), getCaret());
	}
	
	/**
	 * Key typed.
	 *
	 * @param keyEvent the key event
	 * @see charva.awt.event.KeyListener#keyTyped(charva.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent keyEvent) { }

	/**
	 * Key released.
	 *
	 * @param keyEvent the key event
	 * @see charva.awt.event.KeyListener#keyReleased(charva.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent keyEvent) { }
	
	private String cut(int[] pos)
	{
		String cut = copy(pos);
		getTextArea().replaceRange("", pos[0], pos[1]);
		return cut;
	}
	
	private String copy(int[] pos)
	{
		String copy = "";
		if ( pos[0] >= 0 )
			copy = getTextArea().getText().substring(pos[0], pos[1]);
		else
			notification(MessageType.WARN, "Mark not set.");
		return copy;
	}
	
	private int[] sort(int n1, int n2)
	{
		return n1 <  n2 ? new int[] {n1,n2} : new int[] {n2,n1};
	}
	
	/**
	 * Notification.
	 *
	 * @param type the type of information.
	 * @param message the message to notify.
	 */
	protected static void notification(MessageType type, String message)
	{
		Transformer.notification(type, message);
	}
	
	/**
	 * Instantiates a new textual key listener.
	 *
	 * @param textArea the text area
	 * @param clipboard the clipboard
	 */
	public TextualKeyListener(JTextArea textArea, TextualClipboard clipboard)
	{
		super();
		setTextArea(textArea);
		setClipboard(clipboard);
	}
}

// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
