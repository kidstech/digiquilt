package umm.digiquilt.view.filebrowser;

import java.awt.BorderLayout;

import javax.swing.*;
/**
 * The panel on the bottom of the save and load boxes.
 *
 */
@SuppressWarnings("serial")
public class NotePanel extends JPanel{
	
	/**
	 * Text area to display notes.
	 */
	private JTextArea text = new JTextArea(5, 25);
	/**
	 * ScrollPane to contain the text area.
	 */
	private JScrollPane textscroll = new JScrollPane(text);
	
	/**
	 * A panel to display the notes found in quilt xml files
	 */
	public NotePanel() {
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		textscroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		text.setText("");
		text.setName("Notes");
		this.add(BorderLayout.WEST, new JLabel("Notes:"));
		this.add(BorderLayout.EAST, textscroll);
	}
	
	/**
     * Set the text box to display this.
     * 
	 * @param text the text to display
	 */
	public void setText(String text) {
		this.text.setText(text);
	}
	
	/**
     * Make the text box editable, or not.
     * 
	 * @param isEdit true for edit enabled, false for disabled
	 */
	public void setEditable(boolean isEdit) {
		text.setEditable(isEdit);
	}
	
	/**
     * Get the string in the text box
	 * @return the string
	 */
	public String getNotes() {
		return text.getText();
	}
}
