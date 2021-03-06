import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author Jayden Weaver
 *
 */
public class User {

	// TODO: Add ability to store number of pages a user has, and read it.

	private ArrayList<String> imageURLs = new ArrayList<String>();
	private String userURL = "http://";
	private String username = "";
	private int numeral = 0;
	private int numPages = 1;
	private int currentPage = 1;
	private final int TOLERANCE = 4; // 4 seems like a good tolerance, because the duplicates tend to be 3 to 5...
	private File log;
	private ArrayList<String> imagesOfInterest = new ArrayList<String>();
	private boolean SSCFlag = false;

	public User(String url) {
		this.userURL += url + "?sort=3&page=1"; // the last part is needed to increment the URL.
		try {
			numberOfPages();
		} catch (Exception e) {
			System.out.println(e);
			System.err.println("User created, but failed to fetch number of pages.");
		}
	}

	/**
	 * Returns the username.
	 * 
	 * @return
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Returns the images who match desired properties.
	 * 
	 * @return
	 */
	public ArrayList<String> getImagesOfInterest() {
		return this.imagesOfInterest;
	}

	/**
	 * Creates a log file to track details about the account.
	 */
	public void createLog() {

		try {
			this.log = new File(this.username + "_log.pblog");
			this.log.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (this.log.exists()) {
			try {
				PrintWriter writer = new PrintWriter(this.log, "UTF-8");
				writer.println(this.numPages);
				writer.println(new Date());
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Reads a log file.
	 */
	public void readLog() {
		if (this.log.exists()) {
			try {
				Scanner scan = new Scanner(this.log);
				int savedPageNumber = scan.nextInt();
				scan.close();
				System.out.println(savedPageNumber);
			} catch (FileNotFoundException e) {
				System.err.println("Failed to read log file.");
				e.printStackTrace();
			}
		} else {
			System.out.println("No user log saved.");
		}
	}

	/**
	 * Sets the username.
	 * 
	 * @param un
	 */
	public void setUsername(String un) {
		this.username = un;
	}

	/**
	 * Returns the entire list of image urls.
	 * 
	 * @return an ArrayList containing the URLs to the images on the user profile.
	 */
	public ArrayList<String> getImageURLs() {
		return this.imageURLs;
	}

	/**
	 * Returns the image URL at a given index.
	 * 
	 * @param i
	 * @return the image URL at a given index.
	 */
	public String getImage(int i) {
		return this.imageURLs.get(i);
	}

	/**
	 * Sets the user URL to a given string.
	 * 
	 * @param url
	 */
	public void setUserURL(String url) {
		this.userURL = url;
	}

	/**
	 * Returns the user URL.
	 * 
	 * @return the user URL
	 */
	public String getUserURL() {
		return this.userURL;
	}

	/**
	 * Increments the user URL.
	 */
	public void incrementURL() {
		this.userURL = this.userURL.substring(0, this.userURL.length() - 1) + this.currentPage;
	}

	/**
	 * Sets the current page variable.
	 * 
	 * @param i
	 */
	public void setCurrentPage(int i) {
		while (this.currentPage < i) {
			this.currentPage++;
			incrementURL();
		}
	}

	/**
	 * Manually set the number of pages.
	 * 
	 * @param i
	 */
	public void setNumberOfPages(int i) {
		this.numPages = i;
	}

	/**
	 * Set the SSCFlag to analyze images for SSCs.
	 * 
	 * @param flag
	 */
	public void setSSCFlag(boolean flag) {
		this.SSCFlag = flag;
	}

	/**
	 * Parses and returns the number of pages.
	 * 
	 * @return number of pages.
	 * @throws IOException
	 */
	public int numberOfPages() throws IOException {
		// Get entire photobucket page.
		// Need a user agent in order to get the redirect from photobucket.
		Document photobucketDocument = Jsoup.connect(this.userURL).userAgent(
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2")
				.timeout(10000).get();
		// get all the script tags and put them in the element.
		Elements photo = photobucketDocument.getElementsByTag("script");
		// GET NUMBER OF PAGES. FOR PARSING ENTIRE PROFILES.
		// Element scriptPages = photo.get(photo.size() - 24); //Magic number.
		// Photobucket loves to change this, but as of 01/2017 this is the offset.
		int pageOffset = -1;
		System.out.print("\nCalculating Page Offset...");
		pageOffset += 2;
		Element scriptPages = photo.get(photo.size() - pageOffset);
		while (!scriptPages.html().contains("\"page\":1")) {
			pageOffset++;
			scriptPages = photo.get(photo.size() - pageOffset);
		}
		System.out.println(pageOffset);

		String scriptPagesString = scriptPages.toString();
		StringTokenizer pageTokenizer = new StringTokenizer(scriptPagesString, ","); // use , as a delimter.
		String currToken = "", tokenBackup = "";
		while (pageTokenizer.hasMoreTokens()) {
			currToken = pageTokenizer.nextToken();
			tokenBackup = currToken;
			currToken = currToken.substring(0, currToken.length() - 1);
			if (currToken.equals("\"numPages\":")) {
				this.numPages = Integer.parseInt(tokenBackup.substring(tokenBackup.length() - 1)); // supports one digit
			} else {
				currToken = currToken.substring(0, currToken.length() - 1);
				if (currToken.equals("\"numPages\":")) {
					this.numPages = Integer.parseInt(tokenBackup.substring(tokenBackup.length() - 2)); // supports two
																										// digit
				} else {
					currToken = currToken.substring(0, currToken.length() - 1);
					if (currToken.equals("\"numPages\":")) {
						this.numPages = Integer.parseInt(tokenBackup.substring(tokenBackup.length() - 3)); // should
																											// support
																											// three
																											// digit,
																											// but
																											// haven't
																											// come
																											// across a
																											// 3 digit
																											// account
					}
				}
			}
		}
		return this.numPages;
	}

	/**
	 * Parses the user profile.
	 */
	public void parseUser() {

		int userOffset = -1;
		while (this.currentPage <= this.numPages) {

			try {
				// Get entire photobucket page.
				// Need a user agent in order to get the redirect from photobucket.
				Document photobucketDocument = Jsoup.connect(this.userURL).userAgent(
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2")
						.timeout(10000).get();
				// get all the script tags and put them in the element.
				Elements photo = photobucketDocument.getElementsByTag("script");
				// Element script = photo.get(photo.size() - 23); //Magic number. Photobucket
				// loves to change this, but as of 07/2018 this is the offset.

				// Automatically calculate the offset to always fetch the correct data.
				Element script;
				if (userOffset == -1) {
					System.out.print("Calculating Parse Offset...");
					userOffset += 2;
					script = photo.get(photo.size() - userOffset);
					while (!script.html().contains("libraryAlbumsPageCollectionData")) {
						userOffset++;
						script = photo.get(photo.size() - userOffset);
					}
					System.out.println(userOffset);
				}

				System.out.println("BEGINING PAGE " + currentPage + " OF " + numPages);

				script = photo.get(photo.size() - userOffset);

				String httpParse = "http://";
				String selection = script.toString();
				ArrayList<String> photoLinkList = new ArrayList<String>();
				StringBuilder losLinks = new StringBuilder(); // kachow

				// parse links

				StringTokenizer tokenizer = new StringTokenizer(selection, "\\/"); // use \/ as the delimiter.
				String currentToken = "";
				String previousToken = "";
				String imageURL = "";
				while (tokenizer.hasMoreTokens()) {
					previousToken = currentToken;
					currentToken = tokenizer.nextToken();
					losLinks.setLength(0); // clear string builder

					if (currentToken.equals("albums")) {
						losLinks.append(httpParse + previousToken + "/" + currentToken + "/");
						losLinks.append(
								tokenizer.nextToken() + "/" + tokenizer.nextToken() + "/" + tokenizer.nextToken());

						// clean up
						char prev = ' ';
						char cur = ' ';
						int counter = 0;
						for (char a : losLinks.toString().toCharArray()) {
							cur = a;
							if (cur == 'g' && prev == 'e' || cur == 'g' && prev == 'p' || cur == 'f' && prev == 'i'
									|| cur == 'g' && prev == 'n' || cur == '4' && prev == 'p') { // for jpeg, jpg, gif,
																									// png, and mp4.
								// there's a bug in this. if a username is **eg, pg, if, etc... the link is cut
								// off right there. TODO: Fix this.
								imageURL = losLinks.toString().substring(0, counter + 1);
								if (photoLinkList.isEmpty()) { // prevent an index out of bounds exception below by
																// checking before trying to access.
									photoLinkList.add(imageURL);
								} else if (!imageURL.equals(photoLinkList.get(photoLinkList.size() - 1))) {
									photoLinkList.add(imageURL);
								}
								break;
							}
							counter++;
							prev = a;

						}

					}

				}

				if (!photoLinkList.isEmpty()) {
					// Remove last link of the of the photoLinkList because it is garbage.
					photoLinkList.remove(photoLinkList.size() - 1);
				}

				// Begin Saving Files.
				File outputFile, directory;
				float modCount = 1; // use modcount in an attempt to reduce duplicate photos, will use a float in
									// case there are lots of images.
				for (String s : photoLinkList) {
					URL photoURL = new URL(s);
					if (modCount % this.TOLERANCE != 0) {
						// Do nothing.
					} else {
						try {

							HttpURLConnection connection = (HttpURLConnection) photoURL.openConnection();
							/*
							 * Set Request Properties to receive a compatible image, rather than a WebP.
							 * Photobucket sends WebP now instead of other image formats, unless explicitly
							 * told to do so. WebP is incompatible with the way we save images. Set User
							 * Agent to IE 5.5, which does not support WebP format. Set Accept Property to
							 * compatible image formats. Set Referer Property to photobucket.com, so that
							 * images can be downloaded without a watermark.
							 */
							connection.setRequestProperty("User-Agent",
									"Mozilla/4.0 (compatible; MSIE 5.5; AOL 5.0; Windows 95)");
							connection.setRequestProperty("Accept",
									"image/jpg, image/jpeg, image/gif, image/png, image/bmp");
							connection.setRequestProperty("Referer", "http://s.photobucket.com/");

							InputStream is = connection.getInputStream();
							BufferedImage photoJoto = ImageIO.read(is);

							if (photoJoto == null) {
								if (photoURL.toString().contains("http")) {
									System.out.println("Photo issue. The issue may be caused by Photobucket.");
									System.out.println("URL obtained appears to be valid:");
									System.out.println(photoURL.toString());
									System.out.println("PhotobucketMiner may need an update.");
								} else {
									System.err.println("Photo issue.");
									System.err.println("URL obtained appears to be invalid:");
									System.err.println(photoURL.toString());
									System.err.println("PhotobucketMiner may need an update.");
								}
								System.exit(0);
							}

							directory = new File("Saved_Users\\");
							directory.mkdir();
							directory = new File("Saved_Users\\" + this.username + "\\");
							directory.mkdir();
							// Differentiate between file types. Could download pngs and static gifs as jpg,
							// but some are discolored when you don't differentiate.
							if (photoURL.toString()
									.substring(photoURL.toString().length() - 3, photoURL.toString().length())
									.equals("png")) {
								outputFile = new File("Saved_Users\\" + this.username + "\\" + this.numeral + ".png");
								ImageIO.write(photoJoto, "png", outputFile);
							} else if (photoURL.toString()
									.substring(photoURL.toString().length() - 3, photoURL.toString().length())
									.equals("gif")) {
								outputFile = new File("Saved_Users\\" + this.username + "\\" + this.numeral + ".gif");
								ImageIO.write(photoJoto, "gif", outputFile);
							} else { // if not a png or gif, must be a jpg or jpeg.
								outputFile = new File("Saved_Users\\" + this.username + "\\" + this.numeral + ".jpg");
								ImageIO.write(photoJoto, "jpg", outputFile);
							}

							// Check if flag is set and analyze image if so...
							if (SSCFlag) {
								ImageAnalyzer ia = new ImageAnalyzer(outputFile);
								if (ia.isSSN()) {
									imagesOfInterest.add("POSSIBLE MATCH: " + outputFile.toString());
								} else if (ia.isBetwixt) {
									imagesOfInterest.add("UNSURE: " + outputFile.toString());
								}
							}

							this.numeral++; // prevent a bug that overwrites file by making numeral a global variable.
							System.out.println("SAVED: " + s);
						} catch (Exception e) {
							System.out.println(e);
							// catch the exception that is thrown by some bad duplicate URLs that redirect
							// you.
						}
					}
					modCount++;
				}

				// Separate links to the same pictures are being produced. This is because the
				// HTML has multiple links to the same picture, so they're all being parsed.
				// Because separate links to the same pictures are being produced, it makes it
				// hard to compare and determine which are duplicates.

			} catch (IOException e) {
				System.err.println("User Connection Error.");
				System.out.println(e);
				break;
			}
			System.out.println("FINISHED PAGE " + currentPage + " OF " + numPages);
			currentPage++;
			incrementURL();
		}
	}
}
