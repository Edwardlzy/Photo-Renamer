package photo_renamer;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class adds the typed tag(s) split by "," to this photo and updates the history of
 * add operations on this photo for future reversions.
 * 
 * @author Zhi Lin
 * @author Zhiyu Liang
 */
public class AddTagButtonListener implements ActionListener {
	/** The area to use to display the nested directory contents. */
	private JTextField textArea;
	private PhotoNode photo;
	private DefaultListModel<String> thisListModel;
	private DefaultListModel<String> allListModel;
	private JList<String> thisList;
	private JList<String> allList;
	private JFrame photoFrame;
	private JMenu revertOptions;
	private JTextArea imageNameIndicator;
	private static final String TEXT_SEPARATOR = "            ";

	private static final Logger logger = Logger.getLogger(FileChooserButtonListener.class.getName());

	/**
	 * Constructor for this button.
	 * 
	 * @param textArea
	 * 			  the text field for the user to type a new tag
	 * @param photo
	 *            the selected PhotoNode
	 * @param thisListModel
	 * 			  the listModel containing all the tags of this photo
	 * @param thisList
	 * 			  the JList displaying this photo's tags
	 * @param allListModel
	 * 			  the listModel containing all used tags
	 * @param allList
	 * 			  the JList displaying all used tags
	 * @param photoFrame
	 *            the whole window opened by select button
	 * @param revertOptions
	 *            the history of this photo's operations
	 * @param imageNameIndicator
	 *            the JTextArea displaying this photo's name
	 */
	public AddTagButtonListener(JTextField textArea, PhotoNode photo, DefaultListModel<String> thisListModel,
			JList<String> thisList, DefaultListModel<String> allListModel, JList<String> allList, JFrame photoFrame, 
			JMenu revertOptions, JTextArea imageNameIndicator) {
		this.textArea = textArea;
		this.photo = photo;
		this.thisListModel = thisListModel;
		this.thisList = thisList;
		this.allListModel = allListModel;
		this.allList = allList;
		this.photoFrame = photoFrame;
		this.revertOptions = revertOptions;
		this.imageNameIndicator = imageNameIndicator;
	}

	/**
	 * Provides services when the user click this button. It will lead user
	 * to enter valid tag input to be added to the photo and show the result.
	 * Meanwhile the history of this photo's operations is updated.
	 * 
	 * @param e
	 *            the event object
	 */
	public void actionPerformed(ActionEvent e) {
		String allTags = this.textArea.getText().trim();
		
		if (allTags.equals("")) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(null, "Please enter a non-empty tag.");
			textArea.requestFocusInWindow();
			textArea.selectAll();
			return;
		} else if (allTags.indexOf("@") != -1) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(null, "Tag should not contain '@' character, please re-enter.");
			textArea.requestFocusInWindow();
			textArea.selectAll();
			textArea.setText("");
			return;
		} 
		
		// The user provided a valid tag(s). Adds the tag(s) to this photo.
		else {
			List<String> tagsList = Arrays.asList(allTags.split(","));
			for (String tagEntered: tagsList) {
				if (!tagEntered.trim().equals("") && !thisListModel.contains(tagEntered.trim())) {
					Tag newTag = TagManager.findTag(tagEntered.trim());
					if (newTag == null) {
						newTag = new Tag(tagEntered.trim());
					}
					try {
						String tagName = tagEntered.trim();
						this.photo.addTag(newTag);
						thisListModel.addElement(tagName);
						if (!allListModel.contains(tagName)) {
							allListModel.addElement(tagName);
						}
					} catch (IOException a) {
						a.printStackTrace();
					}
				}
			}
			JOptionPane.showMessageDialog(null, "Done adding tags to " + photo.getName());
			JOptionPane.showMessageDialog(null, this.photo.printTags());
		}
		
		// Resets the text field.
		int index = thisList.getSelectedIndex();
        textArea.requestFocusInWindow();
		textArea.setText("");
        
		// Updates the revert options only if this tag is new.
        String date = this.photo.getLastModifiedDate();
        System.out.println(this.photo.getName());
        boolean alreadyAdded = false;
        for (int i=0; i<revertOptions.getItemCount(); i++) {
            String menuText = date + "--> (Added Tag) " + this.photo.getRenamingHistory().get(this.photo.getLastModifiedDate()).getName();
        	if (revertOptions.getItem(i).getText().equals(menuText)) {
        		logger.log(Level.FINE, "menuItem: " + revertOptions.getItem(i).getName());
        		alreadyAdded = true;
        	}
        }
        if (!alreadyAdded) {
        	JMenuItem newMenuItem = new JMenuItem(date + "--> (Added Tag) " + this.photo.getRenamingHistory().get(date).getName());
    		revertOptions.add(newMenuItem);
    		ActionListener revertBackToDate = new RevertListener(photo, date, revertOptions, thisListModel,
    				allListModel, thisList, allList, photoFrame, imageNameIndicator);
    		newMenuItem.addActionListener(revertBackToDate);
    		
    		// Updates JFrame to add the new revert options.
            photoFrame.getContentPane().validate();
    		photoFrame.getContentPane().repaint();
        }
        //updates the photo's name on Photo Editing window.
        imageNameIndicator.setText(TEXT_SEPARATOR + this.photo.getName() + TEXT_SEPARATOR);
	}
}
