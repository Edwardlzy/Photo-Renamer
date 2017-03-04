package photo_renamer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

/*
* This class allows the user to revert the photo to a specific time back in the history. This process includes
* renaming this photo to the its name back to the specific time and reconstructing its tags to that time.
* Meanwhile, the tag manager will also change accordingly.
* 
* @author Zhiyu Liang
* @author Zhi Lin
*/
public class RevertListener implements ActionListener {
	private PhotoNode photo;
	private String date;
	private JMenu revertOptions;
	private DefaultListModel<String> thisListModel;
	private DefaultListModel<String> allListModel;
	private JList<String> thisList;
	private JList<String> allList;
	private JFrame photoFrame;
	private JTextArea imageNameIndicator;
	private static final String TEXT_SEPARATOR = "            ";

	/**
	 * Constructor for this button.
	 * 
	 * @param photo
	 *            the currently editing photo
	 * @param date
	 *            the date to be reverted back
	 * @param revertOptions
	 *            the JMenu corresponding to the date
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
	 * @param imageNameIndicator
	 *            the text area to display this photo's current name
	 */
	public RevertListener(PhotoNode photo, String date, JMenu revertOptions, DefaultListModel thisListModel,
			DefaultListModel allListModel, JList thisList, JList allList, JFrame photoFrame,
			JTextArea imageNameIndicator) {
		this.date = date;
		this.photo = photo;
		this.revertOptions = revertOptions;
		this.thisListModel = thisListModel;
		this.allListModel = allListModel;
		this.thisList = thisList;
		this.allList = allList;
		this.photoFrame = photoFrame;
		this.imageNameIndicator = imageNameIndicator;
	}

	/**
	 * Enables the user to revert the photo to the one at the designated date
	 * and update the renaming history when this button is clicked.
	 * 
	 * @param e
	 *            the event object
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			this.photo.revert(this.date);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Clears all tags with no photo attached to them from the TagManager.
		try {
			TagManager.tagCleaner();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Reconstructs the ListModel of all used tags.
		allListModel.clear();
		for (String tags : TagManager.getTags().keySet()) {
			allListModel.addElement(tags);
		}

		// Reconstructs the ListModel of this photo's tags.
		thisListModel.clear();
		for (String thisTags : photo.getTags().keySet()) {
			thisListModel.addElement(thisTags);
		}

		// Removes later dates from the menu option
		this.revertOptions.removeAll();

		// Updates the renaming history.
		for (String date : this.photo.getRenamingHistory().keySet()) {
			JMenuItem newMenuItem = new JMenuItem(date + "--> " + this.photo.getRenamingHistory().get(date).getName());
			this.revertOptions.add(newMenuItem);
			ActionListener revertBackToDate = new RevertListener(photo, date, revertOptions, thisListModel,
					allListModel, thisList, allList, photoFrame, imageNameIndicator);
			newMenuItem.addActionListener(revertBackToDate);
		}

		// Repaints the window and updates the photo's name to show the effect
		// of this change to the user.
		photoFrame.getContentPane().validate();
		photoFrame.getContentPane().repaint();
		imageNameIndicator.setText(TEXT_SEPARATOR + this.photo.getName() + TEXT_SEPARATOR);
	}

}