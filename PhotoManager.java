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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/*
 * A photo manager that keeps track of all the selected photo so far. It keeps updating the
 * serializable file every time a new photo is viewed and operated.
 * 
 * @author Zhi Lin
 * @author Zhiyu Liang
 */
public class PhotoManager {
	private static final Logger logger = Logger.getLogger(PhotoManager.class.getName());
    private static final Handler consoleHandler = new ConsoleHandler();
    private static final String filePath = "./photos.bin";
	private static Map<String, PhotoNode> photos;
	
	/**
	 * Constructor for the PhotoManager object. It checks if the serializable file has already
	 * been created. If so, reads the stored photos HashMap. Else, creates a new serializable
	 * file to store the photos HashMap.
	 * 
	 * @param photos 					the HashMap to store all viewed photos so far
	 * @param file						the serializable file storing the history of this program
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
    public PhotoManager() throws ClassNotFoundException, IOException {
    	photos = new HashMap<String, PhotoNode>();
        
        // Associate the handler with the logger.
        logger.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        
        // Reads serializable objects from file.
        // Populates the record list using stored data, if it exists.
        File file = new File(filePath);
        if (file.exists() && file.length() != 0) {
            readFromFile(filePath);
        } else if (!file.exists()){
            file.createNewFile();
        }
    }
    
    /**
     * This is a helper function to read from the serializable file at the 
     * designated path.
     * 
     * @param path						the path of the serializable file
     * @throws ClassNotFoundException
     */
    private static void readFromFile(String path) throws ClassNotFoundException {
        try {
            InputStream file = new FileInputStream(path);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);

            //deserialize the Map
            photos = (Map<String,PhotoNode>) input.readObject();
            input.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot read from input.", ex);
        }    
    }
    
    /**
     * Saves the current data to the serializable file. 
     * This function also can be used to update file content .
     * 
     * @throws IOException
     */
    public static void saveToFile() throws IOException {
        OutputStream file = new FileOutputStream(filePath);
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);

        // serialize the Map
        output.writeObject(photos);
        output.close();
    }
    
    /**
     * Adds a new photo to this photos HashMap and updates the serializable file.
     * 
     * @param photo			the PhotoNode to be added
     * @throws IOException
     */
	public static void appendPhoto(PhotoNode photo) throws IOException {
		photos.put(photo.getInitialName(), photo);
		saveToFile();
        logger.log(Level.FINE, "Added a new photo in TagManager" + photo.getName());
        logger.log(Level.FINE, "All photos: " + printAllPhotos());
	}
	
	/**
     * Deletes the photo from this photos HashMap and updates the serializable file.
     * 
     * @param photo			the PhotoNode to be removed
     * @throws IOException
     */
	public static void removePhoto(PhotoNode photo) throws IOException {
		photos.remove(photo.getInitialName());
		saveToFile();
        logger.log(Level.FINE, "Deleted photo " + photo.getName());
	}
	
	/**
	 * Returns the PhotoNode value with the designated key name.
	 * 
	 * @param	name	the name of the PhotoNode being searched
	 * @return 			the corresponding PhotoNode
	 */
	public static PhotoNode findPhoto(String name) {
		return photos.get(name);
	}
	
	/**
	 * Returns a reader-friendly string representation of all the PhotoNode
	 * stored in this PhotoManager.
	 * 
	 * @return 	the string representation
	 */
	@Override
    public String toString() {
        String result = "";
        for (PhotoNode photo : photos.values()) {
            result += photo.getName() + "\n";
        }
        return result;
    }
	
	/**
	 * Returns a String containing the name of all operated photos stored in this manager.
	 * 
	 * @return	the string containing all photos
	 */
	public static String printAllPhotos() {
        String result = "";
        for (String photo : photos.keySet()) {
            result += photo + "\n";
        }
        return result;
    }
}




