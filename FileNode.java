/**
 * 
 */
package photo_renamer;

import java.util.Map;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Design Pattern: Composite
 * It has composite object of file and directory objects into tree structures. It stores the file hierarchy as tree structures.
 * The pros of this design pattern include that it is easy to access files within directory by recursively calls. In addition, 
 * it is also easy to access and recognize different files within the same directory.
 * 
 * @author Zhiyu Liang
 * @author Zhi Lin
 */
public class FileNode implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(FileChooserButtonListener.class.getName());

	/** The name of the file or directory this node represents. */
	protected String name;
	/** Whether this node represents a file or a directory. */
	protected FileType type;
	/** This node's parent. */
	protected FileNode parent;
	/**
	 * This node's children, mapped from the file names to the nodes. If type is
	 * FileType.FILE, this is null.
	 */
	protected Map<String, FileNode> children;

	/**
	 * A node in this tree.
	 *
	 * @param name
	 *            the file
	 * @param parent
	 *            the parent node.
	 * @param type
	 *            file or directory
	 * @see buildFileTree
	 */
	public FileNode(String name, FileNode parent, FileType type) {
		this.name = name;
		this.parent = parent;
		this.type = type;
		this.children = new HashMap<String, FileNode>();
	}

	/**
	 * Finds and returns a child node named name in this directory tree, or null
	 * if there is no such child node.
	 *
	 * @param name
	 *            the file name to search for
	 * @return the node named name
	 */
	public FileNode findChild(String name) {
		Queue<FileNode> queue = new LinkedList<FileNode>();
		for (String key : this.children.keySet()) {
			queue.add(this.children.get(key));
		}
		while (!queue.isEmpty()) {
			FileNode node = (FileNode) queue.remove();
			if (node.type == FileType.DIRECTORY) {
				if (node.children != null) {
					for (String k1 : node.children.keySet()) {
						queue.add(node.children.get(k1));
					}
				}
			} else {
				if (node.getName().equals(name)) {
					return node;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the name of the file or directory represented by this node.
	 *
	 * @return name of this Node
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the current node
	 *
	 * @param name
	 *            of the file/directory
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the child nodes of this node.
	 *
	 * @return the child nodes directly underneath this node.
	 */
	public Collection<FileNode> getChildren() {
		return this.children.values();
	}

	/**
	 * Returns this node's parent.
	 * 
	 * @return the parent
	 */
	public FileNode getParent() {
		return parent;
	}

	/**
	 * Sets this node's parent to p.
	 * 
	 * @param p
	 *            the parent to set
	 */
	public void setParent(FileNode p) {
		this.parent = p;
	}

	/**
	 * Adds childNode, representing a file or directory named name, as a child
	 * of this node.
	 * 
	 * @param name
	 *            the name of the file or directory
	 * @param childNode
	 *            the node to add as a child
	 */
	public void addChild(String name, FileNode childNode) {
		this.children.put(name, childNode);
	}

	/**
	 * Returns whether this node represents a directory.
	 * 
	 * @return whether this node represents a directory.
	 */
	public boolean isDirectory() {
		return this.type == FileType.DIRECTORY;
	}
}