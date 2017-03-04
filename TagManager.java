package photo_renamer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * A manager that keeps track of all tags that is ever been created. It also provides access
 * to fetch a Tag given the tag's name, the filepath of the serializable file storing the data
 * of all tags and clearance of all unused tags.
 * 
 * @author Zhiyu Liang
 * @author Zhi Lin
 */
public class TagManager {
    private static final String filePath = "./tag manager.bin";
	private static Map<String, Tag> tags;

	/**
	 * Constructor for this TagManager object. Updates from the serializable
	 * file if such file has been created. Else creates a new serializable file
	 * to store all tags.
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public TagManager() throws ClassNotFoundException, IOException {
		tags = new HashMap<String, Tag>();
		File file = new File(filePath);
		if (file.exists() && file.length() != 0) {
			readFromFile(filePath);
		} else if (!file.exists()) {
			file.createNewFile();
		}
	}

	/**
	 * Deserializes the map from given path.
	 * 
	 * @param path
	 *            the path of the serializable file
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	protected static void readFromFile(String path) throws ClassNotFoundException {
		try {
			InputStream file = new FileInputStream(path);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input;
			input = new ObjectInputStream(buffer);
			tags = (HashMap<String, Tag>) input.readObject();
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the current data to the serializable file. This function also can
	 * be used to update file content .
	 * 
	 * @param path
	 * 			  the path of the serializable file
	 * @throws IOException
	 */
	protected static void saveToFile(String path) throws IOException {

		OutputStream file = new FileOutputStream(path);
		OutputStream buffer = new BufferedOutputStream(file);
		ObjectOutput output = new ObjectOutputStream(buffer);

		// serialize the Map
		output.writeObject(tags);
		output.close();
	}

	/**
	 * Adds a new tag to this tags HashMap and updates the serializable file.
	 * 
	 * @param tag
	 *            the Tag to be added
	 * @throws IOException
	 */
	public static void appendTag(Tag tag) throws IOException {
		tags.put(tag.getName(), tag);
		saveToFile(filePath);
	}

	/**
	 * Returns the current map of all tags used.
	 * 
	 * @return all used non-empty tags
	 */
	public static Map<String, Tag> getTags() {
		return tags;
	}

	/**
	 * Deletes all unused tags from this HashMap of tags and updates the serializable file.
	 * 
	 * @throws IOException
	 */
	public static void tagCleaner() throws IOException {
		List<String> tagsToBeRemoved = new ArrayList<String>();
		for (Tag tag : tags.values()) {
			if (tag.getPhotos().size() == 0)
				tagsToBeRemoved.add(tag.getName());
		}
		for (String tagName : tagsToBeRemoved) {
			tags.remove(tagName);
		}
		saveToFile(filePath);
	}

	/**
	 * Returns the Tag tag according to the designated name of the tag.
	 * 
	 * @param tagName
	 *            the designated tag to be searched and returned
	 * @return the corresponding tag from tags
	 */
	public static Tag findTag(String tagName) {
		return tags.get(tagName);
	}

	/**
	 * @return the filepath of the serializable file storing all tags
	 */
	public static String getFilepath() {
		return filePath;
	}
}
