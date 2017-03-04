package photo_renamer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/*
 * The button listener for the button to select a photo and add/delete
 * 
 * @ Zhi Lin
 * @ Zhiyu Liang
 */
public class SelectButtonListener implements ActionListener{
	/** The window the button is in. */
	private JFrame directoryFrame;
	/** The label for the full path to the chosen directory. */
	private JLabel directoryLabel;
	/** The file chooser to use when the user clicks. */
	private JFileChooser fileChooser;
	/** The field used to input the tags. */
	/** The name of the file created by this button which contains photo. */
	private JList<String> thisList;
	private JList<String> allList;
	private JButton deleteTagButton;
	private JTextField tagName;
	private PhotoNode photo;
	private PhotoManager pm;
	private TagManager tm;
	private static final String ADD_STRING = "Add Tag";
    private static final String DELETE_STRING = "Delete Tag";
    private static final String ADD_FROM_EXISTING = "Add Existing Tag";
    private static final String TEXT_SEPARATOR = "            ";

	/**
	 * An action listener for window dirFrame, displaying a file path on
	 * dirLabel, using fileChooser to choose a file.
	 *
	 * @param dirFrame
	 *            the main window
	 * @param dirLabel
	 *            the label for the directory path
	 * @param fileChooser
	 *            the file chooser to use
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public SelectButtonListener(JFrame dirFrame, JLabel dirLabel, JFileChooser fileChooser) throws ClassNotFoundException, IOException {
		this.directoryFrame = dirFrame;
		this.directoryLabel = dirLabel;
		this.fileChooser = fileChooser;
		this.pm = new PhotoManager();
		this.tm = new TagManager();
	}
	
	/**
	 * Handles the user clicking on the open button. When the button is clicked, it will first let 
	 * the user to choose a photo and then create a new window to allows the user to do further 
	 * operations such as add/delete tags and revert back to a specific time.
	 *
	 * @param e
	 *            the event object
	 */
	public void actionPerformed(ActionEvent e) {
		// Chooses a photo to edit.
		int returnVal = fileChooser.showOpenDialog(directoryFrame.getContentPane());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (file.exists()) {
				directoryLabel.setText("Selected Photo: " + file.getAbsolutePath());
				// parent file node.
				FileNode parent = new FileNode(file.getParent(), null, FileType.DIRECTORY);
				// find the photoNode in photoManager, if not, build a new fileNode.
				String fileName = file.getName();
				String initName = fileName;
				int startIndex = fileName.indexOf("@");
				// check if the photo has any tags.
				if (startIndex != -1) {
					int endIndex = fileName.lastIndexOf(".");
					String taggedPart = fileName.substring(startIndex, endIndex);
					initName = fileName.replace(taggedPart, "");
				}
				try {
					// All methods in photoManager and TagManager are static,
					// So creating another manager instance won't effect the saved file.
					pm = new PhotoManager();
					tm = new TagManager();
				} catch (ClassNotFoundException | IOException e1) {
					e1.printStackTrace();
				} 
				// Assigns this photo to the selected photo.
				if (PhotoManager.findPhoto(initName) == null) {
					this.photo = new PhotoNode(initName, parent, file);
				} else {
					this.photo = PhotoManager.findPhoto(initName);	
				}
				
				// another window for editing tags.
				this.directoryFrame.setVisible(false);
				JFrame editingPhoto = new JFrame("Photo Editing Mode");
				editingPhoto.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				// the panel to show the name of the photo and meaning of the JLists.
				JPanel indicatorPanel = new JPanel(new BorderLayout());
				JTextArea tagManagerIndicator = new JTextArea("This list shows all currently used tags");
				tagManagerIndicator.setEditable(false);
				JTextArea thisTagsIndicator = new JTextArea("This list shows all tags of this photo");
				thisTagsIndicator.setEditable(false);
				JTextArea imageNameIndicator = new JTextArea(TEXT_SEPARATOR + this.photo.getName() + TEXT_SEPARATOR);
				imageNameIndicator.setEditable(false);
				
				// Add the indicators together.
				indicatorPanel.add(tagManagerIndicator, BorderLayout.WEST);
				indicatorPanel.add(thisTagsIndicator, BorderLayout.EAST);
				indicatorPanel.add(imageNameIndicator, BorderLayout.CENTER);
				
				// the panel to display the photo.
				JPanel imagePanel = new JPanel();
				BufferedImage img = null;
				try {
					img = ImageIO.read(file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				// Resizes the image to fit this window.
				BufferedImage resizedImage = resize(img);
				ImageIcon icon = new ImageIcon(resizedImage);
				JLabel imageLabel = new JLabel(icon, JLabel.CENTER);
				imagePanel.add(imageLabel);
				
				// Builds the text field to enter a tag.
				tagName = new JTextField(10);
				
				// Sets up the JList for this photo's tags.
				DefaultListModel<String> thisListModel = new DefaultListModel<String>();
				for (String tag : this.photo.getTags().keySet()) {
					thisListModel.addElement(tag);
				}
				
				// Creates a JList for displaying this photo's tags and put it into a scroll pane.
				thisList = new JList<String>(thisListModel);
				thisList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				thisList.setVisibleRowCount(10);
				JScrollPane thisListScrollPane = new JScrollPane(thisList);
				
				// Sets up the JList for all currently available tags.
				DefaultListModel<String> allListModel = new DefaultListModel<String>();
				for (String tag : TagManager.getTags().keySet()) {
					allListModel.addElement(tag);
				}
				
				// Creates a JList and puts it into a scroll pane.
				allList = new JList<String>(allListModel);
				allList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				allList.setVisibleRowCount(10);
				JScrollPane allListScrollPane = new JScrollPane(allList);
				
				// Sets up the menu for reversion.
				JMenuBar menuBar = new JMenuBar();
				JMenu revertOptions = new JMenu("Revert Change To");
				menuBar.add(revertOptions);
				JMenuItem newMenuItem = new JMenuItem(this.photo.getFirstModificationDate() + "--> " + this.photo.getInitialName());
				revertOptions.add(newMenuItem);
				ActionListener revertBackToDate = new RevertListener(this.photo, this.photo.getFirstModificationDate(), revertOptions,
						thisListModel, allListModel, thisList, allList, editingPhoto, imageNameIndicator);
				newMenuItem.addActionListener(revertBackToDate);
				
				// Sets up the menu for going back to choose another photo or directory.
				JMenu goBack = new JMenu("Go Back..");
				menuBar.add(goBack);
				JMenuItem selectAnotherPhoto = new JMenuItem("Select Another Photo");
				JMenuItem viewPhotos = new JMenuItem("View All Photos Under A Directory");
				goBack.add(selectAnotherPhoto);
				goBack.add(viewPhotos);
				ActionListener goBackToPreviousPage = new GoBackListener(directoryFrame, editingPhoto);
				selectAnotherPhoto.addActionListener(goBackToPreviousPage);
				viewPhotos.addActionListener(goBackToPreviousPage);
				
				// Sets up the add tag button.
				JButton addTagButton = new JButton(ADD_STRING);
				addTagButton.setActionCommand(ADD_STRING);
				addTagButton.addActionListener(new AddTagButtonListener(tagName, photo, thisListModel, thisList, allListModel, 
						allList, editingPhoto, revertOptions, imageNameIndicator));
				
				// Sets up the delete tag button.
				deleteTagButton = new JButton(DELETE_STRING);
				deleteTagButton.setActionCommand(DELETE_STRING);
				deleteTagButton.addActionListener(new DeleteTagButtonListener(photo, thisListModel, allListModel, thisList, 
						allList, editingPhoto, revertOptions, imageNameIndicator));
				
				// Sets up the "add from existing tags" button.
				JButton addSelected = new JButton(ADD_FROM_EXISTING);
				addSelected.setActionCommand(ADD_FROM_EXISTING);
				addSelected.addActionListener(new AddExistingButtonListener(photo, thisListModel, allListModel, 
						thisList, allList, editingPhoto, revertOptions, imageNameIndicator));
				
				//Creates a panel that uses BoxLayout.
		        JPanel buttonPane = new JPanel();
		        buttonPane.setLayout(new BoxLayout(buttonPane,
                        BoxLayout.LINE_AXIS));
		        buttonPane.add(addSelected);
		        buttonPane.add(Box.createHorizontalStrut(5));
		        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		        buttonPane.add(Box.createHorizontalStrut(5));
		        buttonPane.add(tagName);
		        buttonPane.add(addTagButton);
		        buttonPane.add(deleteTagButton);
		        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		           
		        // Enables the user to hit enter to add tag(s).
				tagName.addActionListener(new AddTagButtonListener(tagName, photo, thisListModel, 
						thisList, allListModel, allList, editingPhoto, revertOptions, imageNameIndicator));
		        
				// Put the above JCompenents all together to this editing photo frame.
				editingPhoto.setJMenuBar(menuBar);
		        Container c = editingPhoto.getContentPane();
		        c.add(imagePanel, BorderLayout.CENTER);
		        c.add(buttonPane, BorderLayout.SOUTH);
		        c.add(thisListScrollPane, BorderLayout.EAST);
		        c.add(allListScrollPane, BorderLayout.WEST);
		        c.add(indicatorPanel, BorderLayout.NORTH);
		        c.setBackground(Color.lightGray);
		        editingPhoto.pack();
				editingPhoto.setVisible(true);
			}
		}
		else {
			directoryLabel.setText("No Photo Selected");
		}
	}
	
	/**
	 * Returns a resized picture with resolution 1136 * 639 which can be properly displayed in this window.
	 * 
	 * @param image
	 * 		the picture to be resized
	 * @param width
	 * 		the desired width
	 * @param height
	 * 		the desired height
	 * @return
	 * 		a resized BufferImage
	 */
	public static BufferedImage resize(BufferedImage image) {
		double ratio = (image.getWidth() > image.getHeight()) ? 1136 / image.getWidth() : 639 / image.getHeight();
		double newWidth = image.getWidth();
		double newHeight = image.getHeight();
		
		// Shrinks the photo to the maximum length 1136 or maximum height 639 depending on the photo
		// is landscape or portrait.
		if (image.getHeight() > 639) {
			if (image.getWidth() < 1136) {
				ratio = 639.0 / image.getHeight();
				newHeight = image.getHeight() * ratio;
				newWidth = image.getWidth() * ratio;
			} else {
				if (image.getHeight() > image.getWidth()) {
					ratio = 639.0 / image.getHeight();
					newHeight = image.getHeight() * ratio;
					newWidth = image.getWidth() * ratio;
				} else {
					ratio = 1136.0 / image.getWidth();
					newWidth = image.getWidth() * ratio;
					newHeight = image.getHeight() * ratio;
				}
			}
		} else {
			if (image.getWidth() > 1136) {
				ratio = 1136.0 / image.getWidth();
				newWidth = image.getWidth() * ratio;
				newHeight = image.getHeight() * ratio;
			}
		}
		
		// Redraws the image.
	    BufferedImage bi = new BufferedImage((int)newWidth, (int)newHeight, BufferedImage.TRANSLUCENT);
	    Graphics2D g2d = (Graphics2D) bi.createGraphics();
	    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
	    g2d.drawImage(image, 0, 0, (int)newWidth, (int)newHeight, null);
	    g2d.dispose();
	    return bi;
	}
	
}
