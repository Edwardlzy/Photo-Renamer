package photo_renamer;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class is a listener of Add Existing Tag button which adds a group of selected 
 * tags from the currently existing tags to the current photo. This includes renaming 
 * this photo, deleting the tags from the tag manager if there is no longer any photo 
 * has the tags and recording the operations history for future reversions.
 * 
 * @author Zhi Lin
 * @author Zhiyu Liang
 */
public class AddExistingButtonListener implements ActionListener {
	private PhotoNode photo;
	private DefaultListModel<String> thisListModel;
	private DefaultListModel<String> allListModel;
	private JList<String> thisList;
	private JList<String> allList;
	private JFrame photoFrame;
	private JMenu revertOptions;
	private JTextArea imageNameIndicator;
	private static final String TEXT_SEPARATOR = "            ";

	/**
	 * Constructor for this button.
	 * 
	 * @param photo
	 *            the selected PhotoNode
	 * @param thisListModel
	 *            the listModel for this photo's tags
	 * @param allListModel
	 *            the listModel for currently all tags
	 * @param thisList
	 *            this photo's JList of tags
	 * @param allList
	 *            JList of all tags
	 * @param photoFrame
	 *            the whole window opened by select button
	 * @param revertOptions
	 *            the options to be reverted back including the date of
	 *            modification and the corresponding photo name
	 * @param imageNameIndicator
	 *            the text area to display this photo's current name.
	 */
	public AddExistingButtonListener(PhotoNode photo, DefaultListModel thisListModel, DefaultListModel allListModel,
			JList thisList, JList allList, JFrame photoFrame, JMenu revertOptions, JTextArea imageNameIndicator) {
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
	 * Provides services when the user click this button. It will add what the
	 * user selected from the current existing tags to this photo and update the
	 * photo's name on the window.
	 * 
	 * @param e
	 *            the event object
	 */
	public void actionPerformed(ActionEvent e) {
		// Gets selected indices.
		int[] index = allList.getSelectedIndices();
		if (index.length == 0) {
			JOptionPane.showMessageDialog(null, "Please select one or more tags.");
			return;
		}
		allList.setSelectedIndices(index);

		// Updates this photo's tags.
		for (int i : index) {
			if (!thisListModel.contains(allListModel.getElementAt(i))) {
				thisListModel.addElement(allListModel.getElementAt(i));
				try {
					photo.addTag(TagManager.findTag((String) allListModel.getElementAt(i)));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "This photo already has " + allListModel.getElementAt(i));
			}
		}

		// Updates the revert options.
		String date = this.photo.getLastModifiedDate();
		JMenuItem newMenuItem = new JMenuItem(
				date + "--> (Added Tag) " + this.photo.getRenamingHistory().get(date).getName());
		revertOptions.add(newMenuItem);
		ActionListener revertBackToDate = new RevertListener(photo, date, revertOptions, thisListModel, allListModel,
				thisList, allList, photoFrame, imageNameIndicator);
		newMenuItem.addActionListener(revertBackToDate);

		// Updates JFrame to add the new revert options and shows the changes of
		// the photo's name.
		imageNameIndicator.setText(TEXT_SEPARATOR + this.photo.getName() + TEXT_SEPARATOR);
		photoFrame.getContentPane().validate();
		photoFrame.getContentPane().repaint();
	}
}
