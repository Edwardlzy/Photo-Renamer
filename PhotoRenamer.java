package photo_renamer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * This class consists the front-end design of Photo Renamer program. Mainly
 * building a window for the user to be able to select a photo and add/delete
 * tags to it.
 * @author Zhi Lin
 * @author Zhiyu Liang
 */
public class PhotoRenamer {
	/**
	 * The prefix to use when displaying nested files and directories; each
	 * nested level has one more copy of this.
	 */
	public final static String PREFIX = "--";

	/**
	 * Creates and returns the window for the Photo Renamer so that user can
	 * view and operates on this program.
	 *
	 * @return the window for the Photo Renamer
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @see    JFrame
	 */
	public static JFrame buildWindow() throws ClassNotFoundException, IOException {
		JFrame directoryFrame = new JFrame("File Renamer");
		directoryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		// JFileChooser that only selects photos.
		JFileChooser photoChooser = new JFileChooser();
		FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
		photoChooser.setFileFilter(imageFilter);

		JLabel directoryLabel = new JLabel("Select a destination directory to view photos");

		// Set up the area for the directory contents.
		JTextArea textArea = new JTextArea(15, 50);
		textArea.setEditable(false);

		// Put it in a scroll pane in case the output is long.
		JScrollPane scrollPane = new JScrollPane(textArea);
		
		// The search photo button.
		JButton selectPhotoButton = new JButton("Select A Photo");
		selectPhotoButton.setVerticalTextPosition(AbstractButton.CENTER);
		selectPhotoButton.setHorizontalTextPosition(AbstractButton.LEADING);
		selectPhotoButton.setMnemonic(KeyEvent.VK_D);
		selectPhotoButton.setActionCommand("disable");
		
		// The directory choosing button.
		JButton openButton = new JButton("Choose Directory To View Photos");
		openButton.setVerticalTextPosition(AbstractButton.CENTER);
		openButton.setHorizontalTextPosition(AbstractButton.LEADING);
		openButton.setMnemonic(KeyEvent.VK_D);
		openButton.setActionCommand("disable");
		 
		// The listener for selectPhotoButton
		ActionListener selectPhotoButtonListener = new SelectButtonListener(directoryFrame, directoryLabel, photoChooser);
		selectPhotoButton.addActionListener(selectPhotoButtonListener);

		// The listener for openFileChosserButton.
		ActionListener choosePhotoButtonListener = new FileChooserButtonListener(directoryFrame, directoryLabel, textArea, fileChooser);
		openButton.addActionListener(choosePhotoButtonListener);

		// Put them all together.
		Container c = directoryFrame.getContentPane();
		c.add(directoryLabel, BorderLayout.PAGE_END);
		c.add(scrollPane, BorderLayout.PAGE_START);
		c.add(selectPhotoButton, BorderLayout.CENTER);
		c.add(openButton, BorderLayout.LINE_END);
		c.setBackground(Color.lightGray);

		directoryFrame.pack();
		return directoryFrame;
	}

	/**
	 * Create and show a directory explorer, which displays the contents of a
	 * directory.
	 *
	 * @param argsv
	 *            the command-line arguments.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		PhotoRenamer.buildWindow().setVisible(true);
	}

}
