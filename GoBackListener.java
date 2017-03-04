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
 * This class provides a bunch of services when the user click the add tag button
 * on the window.
 * 
 * @author Zhi Lin
 * @author Zhiyu Liang
 */
public class GoBackListener implements ActionListener {
	/** The area to use to display the nested directory contents. */
	private JFrame previousFrame;
	private JFrame photoFrame;

	private static final Logger logger = Logger.getLogger(FileChooserButtonListener.class.getName());

	/**
	 * Constructor for this button.
	 * 
	 * @param previousFrame
	 *            the photo renamer window
	 * @param photoFrame
	 *            the select photo window
	 */
	public GoBackListener(JFrame previousFrame, JFrame photoFrame) {
		this.previousFrame = previousFrame;
		this.photoFrame = photoFrame;
	}

	/**
	 * Provides services when the user click this button. It will lead user
	 * to enter valid tag input to be added to the photo and show the result.
	 * 
	 * @param e
	 *            the event object
	 */
	public void actionPerformed(ActionEvent e) {
		this.photoFrame.setVisible(false);
		this.previousFrame.setVisible(true);
	}
}
