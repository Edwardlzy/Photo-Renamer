package photo_renamer;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
 * This class provides unit tests for the TagManager class.
 * 
 * @author Zhi Lin
 */
public class TagManagerTest {
	/** The directory in which the test directory structures will be created. */
	private static final String TEST_PATH = "/Users/HL/Desktop/photoRenamerTester";

	/** The File for the first test directory. */
	private File testFile1;

	/** The File for the second test directory. */
	private File testFile2;
	
	private Tag testTag1;
	private Tag testTag2;
	private Tag testTag3;
	
	private PhotoNode testPhoto1;
	private PhotoNode testPhoto2;
	
	Map<String, Tag> testMap;
	
	private PhotoManager photoManager;
	private TagManager tagManager;
	
	/**
	 * Create the test files, initialize variables and reset TagManager class.
	 * @throws IOException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	@Before
	public void setUp() throws IOException, ClassNotFoundException {
		testFile1 = new File(TEST_PATH + "/tags.ser");
		testFile2 = new File(TEST_PATH + "/photos.ser");
		testTag1 = new Tag("tag1");
		testTag2 = new Tag("tag2");
		testTag3 = new Tag("tag3");
		testPhoto1 = new PhotoNode("photo1", null, null);
		testPhoto2 = new PhotoNode("photo1", null, null);
		photoManager = new PhotoManager();
		tagManager = new TagManager();
		testMap = new HashMap<String, Tag>();
		resetTagManager();
	}
	
	/**
	 * Test adding one tag.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAppendTag() throws IOException {
		Map<String, Tag> resultTags = new HashMap<String, Tag>();
		resultTags.put(testTag1.getName(), testTag1);
		TagManager.appendTag(testTag1);
		assertEquals(resultTags, TagManager.getTags());
	}
	
	/**
	 * Test appending two tags and finding one tag.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFindTag() throws IOException {
		TagManager.appendTag(testTag1);
		TagManager.appendTag(testTag2);
		assertEquals(testTag2, TagManager.findTag(testTag2.getName()));
	}
	
	/**
	 * Test tagCleaner method with two unused tags.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testTagCleaner1() throws IOException {
		TagManager.appendTag(testTag1);
		TagManager.appendTag(testTag2);
		TagManager.tagCleaner();
		assertTrue(TagManager.getTags().isEmpty());
	}
	
	/**
	 * Test addPhoto method with two photos and tagCleaner method
	 * with one unused tag out of three tags.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testTagCleaner2() throws IOException {
		TagManager.appendTag(testTag1);
		TagManager.appendTag(testTag2);
		TagManager.appendTag(testTag3);
		testTag1.addPhoto(testPhoto1);
		testTag3.addPhoto(testPhoto2);
		TagManager.tagCleaner();
		assertEquals(2, TagManager.getTags().size());
	}
	
	/**
	 * Test appendTag with three same tags.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAppendRepetition() throws IOException {
		TagManager.appendTag(testTag1);
		TagManager.appendTag(testTag1);
		TagManager.appendTag(testTag1);
		assertEquals(1, TagManager.getTags().size());
	}
	
	/**
	 * Test getTags with three tags added to TagManager.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetTags() throws IOException {
		TagManager.appendTag(testTag1);
		TagManager.appendTag(testTag2);
		TagManager.appendTag(testTag3);
		testMap.put(testTag1.getName(), testTag1);
		testMap.put(testTag2.getName(), testTag2);
		testMap.put(testTag3.getName(), testTag3);
		assertEquals(testMap, TagManager.getTags());
	}
	
	/**
	 * Test getFilepath with a TagManager with non-empty path.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFilePathNotEmpty() throws IOException {
		assertTrue(!TagManager.getFilepath().equals(""));
	}
	
	/**
	 * Test saveToFile and readFromFile.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testSaveAndReadFromFile() throws IOException, ClassNotFoundException{
		TagManager.appendTag(testTag3);
		testMap.put(testTag3.getName(), testTag3);
		TagManager.appendTag(testTag2);
		testMap.put(testTag2.getName(), testTag2);
		TagManager.appendTag(testTag1);
		testMap.put(testTag1.getName(), testTag1);
		TagManager.saveToFile(TagManager.getFilepath());
		TagManager.readFromFile(TagManager.getFilepath());
		assertTrue(testMap.keySet().equals(TagManager.getTags().keySet()));
	}
	
	/**
	 * Clean up TagManager class and delete the test directories.
	 * 
	 * @throws IOException
	 */
	@After
	public void tearDown() throws IOException {
		TagManager.getTags().clear();
		resetTagManager();
	}
	
	/**
	 * Delete file and its contents.
	 * 
	 * @param file
	 *            the file to delete.
	 */
	private static void delete(File file) {
		if (file.isDirectory()) {
			for (File c : file.listFiles())
				delete(c);
		}
	}
	
	/**
	 * Cleans everything in TagManager class.
	 * 
	 * @throws FileNotFoundException
	 */
	private static void resetTagManager() throws FileNotFoundException{
		PrintWriter printWriter = new PrintWriter(TagManager.getFilepath());
		printWriter.close();
		delete(new File(TEST_PATH));
	}
}
