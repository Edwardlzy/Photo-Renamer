package photo_renamer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
 * This class consists all the necessary data of a single tag object including
 * its name, current set of photos that has this tag. It also provides adding/
 * deleting itself to a photo.
 * 
 * @author Zhiyu Liang
 * @author Zhi Lin
 */
public class Tag implements Serializable {
	private Map<String, PhotoNode> photos;
	private String name;

	/**
	 * Constructor for the Tag object.
	 * 
	 * @param name
	 *            the name of this tag
	 */
	public Tag(String name) {
		this.name = name;
		this.photos = new HashMap<String, PhotoNode>();
	}

	/**
	 * Returns the current set of photos that have this tag.
	 * 
	 * @return the photos having this tag
	 */
	public Map<String, PhotoNode> getPhotos() {
		return this.photos;
	}

	/**
	 * Returns the name of this tag.
	 * 
	 * @return this tag's name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of this tag.
	 * 
	 * @param name
	 *            the name to set to this tag
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Adds the photo to this tag if it is not currently in this tag's photo
	 * set.
	 * 
	 * @param photo
	 *            the photo to be added to this tag
	 */
	public void addPhoto(PhotoNode photo) {
		this.photos.put(photo.getInitialName(), photo);
	}

	/**
	 * Deletes the photo from this tag if it exists.
	 */
	public void deletePhoto(PhotoNode photo) {
		this.photos.remove(photo.getInitialName());
	}
}
