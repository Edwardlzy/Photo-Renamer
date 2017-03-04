package photo_renamer;

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Design Pattern: Memento. 
 * Specifically, this class utilizes hashmap to store the all the renaming history, which externalized the photonode's internal
 * state so that the photonode can be reverted to this state later.
 * 
 * PhotoNode class consists all data of a single photo file including its initial name, current 
 * name, current set of tags, all history of renaming operations and many methods to change the 
 * data and update the necessary serializable file.
 * 
 * @author Zhiyu Liang
 * @author Zhi Lin
 */
public class PhotoNode extends FileNode implements Serializable {

	private static final long serialVersionUID = -1380943893375531698L;
	private static final Logger logger = Logger.getLogger(PhotoNode.class.getName());
	private static final String renameLogPath = "./renamed_history.txt";
	private static final FileHandler fileHanderler = addHandler(renameLogPath);
	private static final DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private String lastModifiedDate;
	private final String firstModificationDate;
	private final String initialName;
	public final static String PREFIX = "@";
	public final static String ADD_MODE = "ADD";
	public final static String DELETE_MODE = "DELETE";

	private Map<String, Tag> tags;
	private Map<String, PhotoNode> renamingHistory;
	public File file;
	public static final FileType filetype = FileType.PHOTO;

	/**
	 * Constructor for the PhotoNode object. It inherits constructor for name,
	 * parent and filetype from its super class FileNode.
	 * 
	 * @param originalName
	 *            the current name when declaring a new PhotoNode
	 * @param parent
	 *            the parent node of this photoNode
	 * @param file
	 *            the actual photo file in the system corresponding to this
	 *            PhotoNode
	 */
	public PhotoNode(String originalName, FileNode parent, File file) {
		super(originalName, parent, filetype);
		this.initialName = originalName;
		this.tags = new HashMap<String, Tag>();
		this.renamingHistory = new HashMap<String, PhotoNode>();
		this.file = file;
		this.lastModifiedDate = "";
		// Set the firstModificationDate to the time the PhotoNode is created.
		Date date = new Date();
		String currentTime = dateFormatter.format(date);
		this.firstModificationDate = currentTime;
		// Associate the handler with the logger.
		logger.setLevel(Level.ALL);
		fileHanderler.setLevel(Level.ALL);
		logger.addHandler(fileHanderler);
	}

	/**
	 * Selects a tag from the existing tags and add to the Photo. If the tag
	 * does not exist, creates a new Tag object and adds to the TagManager then
	 * adds the tag to the Photo.
	 * <p>
	 * When adding/deleting a new tag to a photo, also adds/deletes photo to the
	 * tag and rename the actual photo in the system with the tag.
	 * 
	 * @param t
	 *            the tag to be added to the photo.
	 * @throws IOException
	 *             if failed updating file.
	 */
	public void addTag(Tag t) throws IOException {
		if (this.getLastModifiedDate().equals("")) {
			initializeRenamingHistory();
		}
		this.tags.put(t.getName(), t);
		t.addPhoto(this);
		TagManager.appendTag(t);
		this.renamePhoto(t, ADD_MODE);
	}

	/**
	 * Deletes the tag from the photo. If the tag only appears in this photo,
	 * deletes the tag from the TagManager.
	 * 
	 * @param t
	 *            the tag to be deleted
	 * @throws IOException
	 */
	public void deleteTag(Tag t) throws IOException {
		if (this.getLastModifiedDate().equals("")) {
			initializeRenamingHistory();
		}
		tags.remove(t.getName());
		t.deletePhoto(this);
		TagManager.tagCleaner();
		this.renamePhoto(t, DELETE_MODE);
	}

	/**
	 * Initialize RenamingHistory to the date of creation corresponding to a
	 * PhotoNode with this PhotoNode's initial name if no change has been made
	 * to the PhotoNode.
	 * <p>
	 * When initializing RenamingHistory, also set LastModifiedDate to
	 * firstModificationDate, which is the date of creation.
	 * 
	 * @throws IOException
	 *             if failed updating file.
	 */
	private void initializeRenamingHistory() throws IOException {
		PhotoNode originalCopy = new PhotoNode(this.initialName, this.parent, this.file);
		Map<String, Tag> emptyMap = new HashMap<String, Tag>();
		originalCopy.setTags(emptyMap);
		this.renamingHistory.put(this.firstModificationDate, originalCopy);
		this.setLastModifiedDate(firstModificationDate);
	}

	/**
	 * Extends the tag with a '@' prefix to the original Photo name or deletes
	 * the tag from the original photo name based on the designated mode. After
	 * renaming process it writes the change to the renaming history of this
	 * photo for further possible reversion.
	 * 
	 * @param t
	 *            the tag to be added/deleted from the actual photo name.
	 * @param mode
	 *            whether to add or delete the tag from the photo name.
	 * @throws IOException
	 */
	private void renamePhoto(Tag t, String mode) throws IOException {
		String rootPath = file.getParent();
		String newName;
		String suffix = file.getName().substring(file.getName().lastIndexOf("."));
		if (mode == ADD_MODE) {
			newName = addTagToName(t, suffix);
		} else if (mode == DELETE_MODE) {
			newName = deleteTagFromName(t);
		} else {
			newName = this.getName();
		}
		Path base = Paths.get(rootPath);
		Path filePath = base.resolve(this.getName());
		this.name = newName;
		PhotoNode changedPhoto = new PhotoNode(newName, this.parent, this.file);
		this.recordToHistory(changedPhoto);
		PhotoManager.appendPhoto(this);
		Files.move(filePath, filePath.resolveSibling(newName));
		logger.log(Level.FINE, "Renamed Photo To: " + newName);
	}

	/**
	 * Add tag name with prefix to the PhotoNode name and return it as a String.
	 * 
	 * @param t
	 *            Tag object to add tag name from.
	 * @param suffix
	 *            suffix of the PhotoNode.
	 * @return returns updated name.
	 */
	private String addTagToName(Tag t, String suffix) {
		String newName;
		int suffixIndex = this.getName().lastIndexOf(".");
		String tagNameWithPrefix = PREFIX + t.getName();
		if (this.getName().contains(tagNameWithPrefix)) {
			newName = this.getName();
		} else {
			newName = this.getName().substring(0, suffixIndex) + PREFIX + t.getName() + suffix;
		}
		return newName;
	}

	/**
	 * Delete tag name from the PhotoNode name and return it as a String.
	 * 
	 * @param t
	 *            The tag object, it's name will be deleted from PhotoNode name.
	 * @return returns updated name.
	 */
	private String deleteTagFromName(Tag t) {
		String newName;
		if (this.getName().indexOf(t.getName()) != -1) {
			int suffixStartIndex = this.getName().lastIndexOf(".") + 1;
			int tagIndex = this.getName().indexOf("@");
			if (tagIndex != -1) {
				suffixStartIndex = this.getName().indexOf(t.getName(), tagIndex);
			}
			int endIndex = this.getName().indexOf("@", suffixStartIndex);
			if (endIndex != -1) {
				newName = this.getName().substring(0, suffixStartIndex - 1) + this.getName().substring(endIndex);
			} else {
				endIndex = this.getName().lastIndexOf(".");
			}
			newName = this.getName().substring(0, suffixStartIndex - 1) + this.getName().substring(endIndex);
			return newName;
		}
		return this.getName();
	}

	/**
	 * Records current operation on the photo to its renaming history.
	 * Information includes the time of this operation and the PhotoNode after
	 * change.
	 * 
	 * @param changedPhoto
	 *            the PhotoNode recorded to the history
	 * @throws IOException
	 */
	public void recordToHistory(PhotoNode changedPhoto) throws IOException {
		Date date = new Date();
		String currentTime = dateFormatter.format(date);
		Map<String, Tag> currentMap = new HashMap<String, Tag>();
		for (Map.Entry<String, Tag> entry : this.getTags().entrySet()) {
			currentMap.put(entry.getKey(), entry.getValue());
		}
		changedPhoto.setTags(currentMap);
		this.renamingHistory.put(currentTime, changedPhoto);
		this.setLastModifiedDate(currentTime);
	}

	/**
	 * Revert back to given date of change. Every attribute of this PhotoNode
	 * will be reverted back to the designated date and the actual photo file
	 * will be renamed accordingly.
	 * 
	 * @param date
	 *            the designated date to revert to
	 * @throws IOException
	 */
	public void revert(String date) throws IOException {
		PhotoNode destinationNode = this.renamingHistory.get(date);
		if (destinationNode != null) {
			renameWithNameGiven(destinationNode.getName());
			this.setName(destinationNode.getName());
			for (Map.Entry<String, Tag> entry : destinationNode.getTags().entrySet()) {
				if (!tags.containsKey(entry.getKey())) {
					this.addTag(entry.getValue());
				}
			}
			List<Tag> tagsToBeRemoved = new ArrayList<Tag>();
			for (Map.Entry<String, Tag> entry : this.tags.entrySet()) {
				if (!(destinationNode.getTags().containsKey(entry.getKey()))) {
					TagManager.findTag(entry.getKey()).deletePhoto(this);
					tagsToBeRemoved.add(entry.getValue());
				}
			}
			for (Tag tag : tagsToBeRemoved) {
				tag.deletePhoto(this);
				tags.remove(tag.getName());
			}
			stashLaterChanges(date);
			TagManager.tagCleaner();
			PhotoManager.saveToFile();
			TagManager.saveToFile(TagManager.getFilepath());
			logger.log(Level.FINE, "Reverted Back To Date: " + date + " with name " + this.getName());
		}
	}

	/**
	 * Stashes all changes after the reverted date.
	 * 
	 * @param date
	 *            time of revert.
	 */
	private void stashLaterChanges(String revertedDate) {
		try {
			Date thatDay = dateFormatter.parse(revertedDate);
			List<String> datesToBeRemoved = new ArrayList<String>();
			for (String historyDate : this.renamingHistory.keySet()) {
				Date historyDateDate = dateFormatter.parse(historyDate);
				if (thatDay.before(historyDateDate)) {
					datesToBeRemoved.add(historyDate);
				}
			}
			for (String changedDate : datesToBeRemoved) {
				this.renamingHistory.remove(changedDate);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.setLastModifiedDate(revertedDate);
	}

	/**
	 * Helper function to rename the actual file in the file system to the
	 * designated new name.
	 * 
	 * @param name
	 *            the designated new name of this file.
	 * @throws IOException
	 */
	public void renameWithNameGiven(String name) throws IOException {
		Path base = Paths.get(this.file.getParent());
		Path filePath = base.resolve(this.getName());
		Files.move(filePath, filePath.resolveSibling(name));
		PhotoManager.appendPhoto(this);
	}

	/**
	 * @return the lastModifiedDate
	 */
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate
	 *            the lastModifiedDate to set
	 */
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * Returns the initial name of this PhotoNode. Note that the initial name
	 * remains unchanged regardless of any operations.
	 * 
	 * @return the initialName
	 */
	public String getInitialName() {
		return this.initialName;
	}

	/**
	 * Returns a map of this photo's tag set.
	 * 
	 * @return the tag set of this PhotoNode
	 */
	public Map<String, Tag> getTags() {
		return this.tags;
	}

	/**
	 * Sets this photo's tag set to the designated new tag set and saves the
	 * change to the serialized file maintained by photoManager class.
	 * 
	 * @param tags
	 *            the tags to set
	 * @throws IOException
	 */
	private void setTags(Map<String, Tag> tags) throws IOException {
		this.tags = tags;
		PhotoManager.saveToFile();
	}

	/**
	 * Returns a map of all renaming history of this PhotoNode. Keys are the
	 * time of changing and values are the complete PhotoNode at the
	 * corresponding time.
	 * 
	 * @return the renamingHistory map
	 */
	public Map<String, PhotoNode> getRenamingHistory() {
		return renamingHistory;
	}

	/**
	 * @return the firstModificationDate
	 */
	public String getFirstModificationDate() {
		return firstModificationDate;
	}

	/**
	 * Returns the fileHandler of the logger and updates the logging file.
	 * 
	 * @param filePath
	 *            the path of the logger file
	 * @return the file handler
	 */
	public static FileHandler addHandler(String filePath) {
		try {
			// Create a file handler that append log to filePath.
			FileHandler fileHandler = new FileHandler(filePath, true);
			return fileHandler;
		} catch (IOException e) {
			logger.log(Level.WARNING, "IOException!");
		}
		return null;
	}

	/**
	 * Returns all tags of this PhotoNode in a reader-friendly format.
	 * 
	 * @return the set of tags of this PhotoNode
	 */
	public String printTags() {
		String tags = "Here are all tags for this photo:\n";
		for (Tag t : this.getTags().values()) {
			tags += t.getName();
			tags += "\n";
		}
		return tags;
	}
}
