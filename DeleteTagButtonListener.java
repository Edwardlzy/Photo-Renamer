package photo_renamer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * This class allows the user to delete the selected tags from this photo's tags as well as
 * updating the history of operations on this photo for future reversions.
 * 
 * @author Zhi Lin
 * @author Zhiyu Liang
 */
public class DeleteTagButtonListener implements ActionListener {
	/** The area to use to display the nested directory contents. */
	private PhotoNode photo;
	private DefaultListModel thisListModel;
	private DefaultListModel allListModel;
	private JList thisList;
	private JList allList;
	private JFrame photoFrame;
	private JMenu revertOptions;
	private JTextArea imageNameIndicator;
	private static final String TEXT_SEPARATOR = "            ";

	/**
	 * Constructor for this delete tag button.
	 * 
	 * @param photo
	 *            the selected PhotoNode
	 * @param thisListModel
	 *            this photo's listModel of tags
	 * @param allListModel
	 *            the listModel of all existing tags
	 * @param thisList
	 *            this photo's JList of tags
	 * @param allList
	 *            JList of all tags
	 * @param photoFrame
	 * 			  the whole window opened by select button.
	 * @param revertOptions
	 *            the history of this photo's operations
	 * @param imageNameIndicator
	 *            the JTextArea displaying this photo's name
	 */
	public DeleteTagButtonListener(PhotoNode photo, DefaultListModel thisListModel, DefaultListModel allListModel, JList thisList,
			JList allList, JFrame photoFrame, JMenu revertOptions, JTextArea imageNameIndicator) {
		this.photo = photo;
		this.thisListModel = thisListModel;
		this.allListModel = allListModel;
		this.thisList = thisList;
		this.allList = allList;
		this.photoFrame = photoFrame;
		this.revertOptions = revertOptions;
		this.imageNameIndicator = imageNameIndicator;
	}

	/**
	 * Provides services when the user click this button. It deletes the tags
	 * selected by the user from the current photo and updates the renaming
	 * history.
	 * 
	 * @param e
	 *            the event object
	 */
	public void actionPerformed(ActionEvent ae) {
		// Checks if there are tags for this photo.
		if (thisListModel.size() == 0) {
			JOptionPane.showMessageDialog(null, "This image does not have any tags yet!");
			return;
		}

		// Gets the selection(s) from user.
		int[] index = thisList.getSelectedIndices();
		thisList.setSelectedIndices(index);
		if (index.length == 0) {
			JOptionPane.showMessageDialog(null, "No selection made!");
			return;
		}
		
		// Rebuilds this photo's listModel which consists all of its tags.
		List<String> removeList = new ArrayList<String>();
		for (int h = index.length - 1; h >= 0; h--) {
			String removedTagName = (String) thisListModel.get(index[h]);
			removeList.add(removedTagName);
		}
		for (String removed : removeList) {
			thisListModel.removeElement(removed);
			Tag removedTag = TagManager.findTag(removed);
			// Deletes the valid tag from the photo.
			if (removedTag != null) {
				try {
					photo.deleteTag(removedTag);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// Clear all tags with no photo attached to them from the TagManager.
		try {
			TagManager.tagCleaner();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Rebuilds the tag manager listModel.
		allListModel.clear();
		for (String tags : TagManager.getTags().keySet()) {
			allListModel.addElement(tags);
		}
		JOptionPane.showMessageDialog(null, "Done deleting tags for " + photo.getName());
		JOptionPane.showMessageDialog(null, this.photo.printTags());

		// Updates the renaming history.
		String date = this.photo.getLastModifiedDate();
		JMenuItem newMenuItem = new JMenuItem(
				date + "--> (Deleted Tag) " + this.photo.getRenamingHistory().get(date).getName());
		revertOptions.add(newMenuItem);
		ActionListener revertBackToDate = new RevertListener(photo, date, revertOptions, thisListModel,
								allListModel, thisList, allList, photoFrame, imageNameIndicator);
		newMenuItem.addActionListener(revertBackToDate);
		
		// Updates JFrame to add the new revert options and the photo's name on Photo Editing window.
		photoFrame.getContentPane().validate();
		photoFrame.getContentPane().repaint();
		imageNameIndicator.setText(TEXT_SEPARATOR + this.photo.getName() + TEXT_SEPARATOR);
	}
}
