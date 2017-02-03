import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
	
	//TODO: Add ability to store number of pages a user has, and read it.

	private ArrayList<String> imageURLs = new ArrayList<String>();
	private String userURL = "http://";
	private String username = "";
	private int numeral = 0;
	private int numPages = 1;
	private int currentPage = 1;
	private final int TOLERANCE = 4; //4 seems like a good tolerance, because the duplicates tend to be 3 to 5...

	public User(String url) {
		this.userURL += url + "?sort=3&page=1"; //the last part is needed to increment the URL.
		try{
			numberOfPages();
		}
		catch(Exception e){
			System.out.println(e);
			System.err.println("User created, but failed to fetch number of pages.");
		}
	}

	/**
	 * Sets the username. 
	 * @param un
	 */
	public void setUsername(String un){
		this.username = un;
	}

	/**
	 * Returns the entire list of image urls.
	 * @return an ArrayList containing the URLs to the images on the user profile.
	 */
	public ArrayList<String> getImageURLs(){
		return this.imageURLs;
	}

	/**
	 * Returns the image URL at a given index.
	 * @param i
	 * @return the image URL at a given index.
	 */
	public String getImage(int i){
		return this.imageURLs.get(i);
	}

	/**
	 * Sets the user URL to a given string.
	 * @param url
	 */
	public void setUserURL(String url){
		this.userURL = url;
	}

	/**
	 * Returns the user URL.
	 * @return the user URL
	 */
	public String getUserURL(){
		return this.userURL;
	}

	/**
	 * Increments the user URL.
	 */
	public void incrementURL(){
		this.userURL = this.userURL.substring(0, this.userURL.length() - 1) + this.currentPage;
	}

	/**
	 * Sets the current page variable.
	 * @param i
	 */
	public void setCurrentPage(int i){
		while(this.currentPage < i){
			this.currentPage++;
			incrementURL();
		}
	}

	/**
	 * Manually set the number of pages.
	 * @param i
	 */
	public void setNumberOfPages(int i){
		this.numPages = i;
	}

	/**
	 * Parses and returns the number of pages.
	 * @return number of pages.
	 * @throws IOException
	 */
	public int numberOfPages() throws IOException{
		//Get entire photobucket page.
		//Need a user agent in order to get the redirect from photobucket.
		Document photobucketDocument = Jsoup.connect(this.userURL).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2").get();
		//get all the script tags and put them in the element.
		Elements photo = photobucketDocument.getElementsByTag("script");
		//GET NUMBER OF PAGES. FOR PARSING ENTIRE PROFILES.
		Element scriptPages = photo.get(photo.size() - 26); //Magic number. Photobucket loves to change this, but as of 01/2017 this is the offset. 
		String scriptPagesString = scriptPages.toString();
		StringTokenizer pageTokenizer = new StringTokenizer(scriptPagesString, ","); //use , as a delimter.
		String currToken = "", tokenBackup = "";
		while (pageTokenizer.hasMoreTokens()){
			currToken = pageTokenizer.nextToken();
			tokenBackup = currToken;
			currToken = currToken.substring(0,currToken.length() - 1);
			if (currToken.equals("\"numPages\":")){
				this.numPages = Integer.parseInt(tokenBackup.substring(tokenBackup.length() - 1)); //supports one digit 
			}
			else{
				currToken = currToken.substring(0,currToken.length() -1);
				if (currToken.equals("\"numPages\":")){
					this.numPages = Integer.parseInt(tokenBackup.substring(tokenBackup.length() - 2)); //supports two digit
				}
				else{
					currToken = currToken.substring(0,currToken.length() - 1);
					if (currToken.equals("\"numPages\":")){
						this.numPages = Integer.parseInt(tokenBackup.substring(tokenBackup.length() -3)); //should support three digit, but haven't come across a 3 digit account
					}
				}
			}
		}
		return this.numPages;
	}

	/**
	 * Parses the user profile.
	 */
	public void parseUser(){

		while(this.currentPage <= this.numPages){
			System.out.println("BEGINING PAGE " + currentPage + " OF " + numPages);
			try {
				//Get entire photobucket page.
				//Need a user agent in order to get the redirect from photobucket.
				Document photobucketDocument = Jsoup.connect(this.userURL).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2").get();
				//get all the script tags and put them in the element.
				Elements photo = photobucketDocument.getElementsByTag("script");
				Element script = photo.get(photo.size() - 25); //Magic number. Photobucket loves to change this, but as of 01/2017 this is the offset. 

				String httpParse = "http://";
				String selection = script.toString();
				ArrayList<String> photoLinkList = new ArrayList<String>();
				StringBuilder losLinks = new StringBuilder(); //kachow

				//parse links

				StringTokenizer tokenizer = new StringTokenizer(selection, "\\/"); //use \/ as the delimiter. 
				String currentToken = "";
				String previousToken = "";
				String imageURL = "";
				while (tokenizer.hasMoreTokens()){
					previousToken = currentToken;
					currentToken = tokenizer.nextToken();
					losLinks.setLength(0); //clear string builder

					if (currentToken.equals("albums")){
						losLinks.append(httpParse + previousToken + "/" + currentToken + "/");
						losLinks.append(tokenizer.nextToken() + "/" + tokenizer.nextToken() + "/" + tokenizer.nextToken());

						//clean up
						char prev = ' ';
						char cur = ' ';
						int counter = 0;
						for (char a : losLinks.toString().toCharArray()){
							cur = a;
							if (cur == 'g' && prev == 'e' || cur == 'g' && prev == 'p' || cur == 'f' && prev == 'i' || cur == 'g' && prev == 'n' || cur == '4' && prev == 'p'){ //for jpeg, jpg, gif, png, and mp4.
								//there's a bug in this. if a username is **eg, pg, if, etc... the link is cut off right there. TODO: Fix this.							
								imageURL = losLinks.toString().substring(0, counter + 1);
								if (photoLinkList.isEmpty()){ //prevent an index out of bounds exception below by checking before trying to access.
									photoLinkList.add(imageURL);
								}
								else if (!imageURL.equals(photoLinkList.get(photoLinkList.size() - 1))){
									photoLinkList.add(imageURL);
								}
								break;
							}
							counter++;
							prev = a;

						}

					}

				}

				if (!photoLinkList.isEmpty()){
					//Remove last link of the of the photoLinkList because it is garbage.
					photoLinkList.remove(photoLinkList.size() - 1);
				}

				//Begin Saving Files.
				File outputFile, directory;
				float modCount = 1; //use modcount in an attempt to reduce duplicate photos, will use a float in case there are lots of images.
				for (String s : photoLinkList){
					URL photoURL = new URL(s);
					if (modCount % this.TOLERANCE != 0){
						//Do nothing.
					}
					else{
						try{
							BufferedImage photoJoto = ImageIO.read(photoURL);
							directory = new File("Saved_Users\\");
							directory.mkdir();
							directory = new File("Saved_Users\\" + this.username + "\\");
							directory.mkdir();
							//Differentiate between file types. Could download pngs and static gifs as jpg, but some are discolored when you don't differentiate.
							if (photoURL.toString().substring(photoURL.toString().length() - 3, photoURL.toString().length()).equals("png")){
								outputFile = new File("Saved_Users\\" + this.username + "\\" + this.numeral + ".png");					
								ImageIO.write(photoJoto,"png", outputFile);
							}
							else if (photoURL.toString().substring(photoURL.toString().length() - 3, photoURL.toString().length()).equals("gif")){
								outputFile = new File("Saved_Users\\" + this.username + "\\" + this.numeral + ".gif");					
								ImageIO.write(photoJoto,"gif", outputFile);
							}
							else{ //if not a png or gif, must be a jpg or jpeg.
								outputFile = new File("Saved_Users\\" + this.username + "\\" + this.numeral + ".jpg");					
								ImageIO.write(photoJoto,"jpg", outputFile);
							}
							this.numeral++; //prevent a bug that overwrites file by making numeral a global variable.
							System.out.println("SAVED: " + s);
						} catch(Exception e){
							//catch the exception that is thrown by some bad duplicate URLs that redirect you.
						}
					}
					modCount++;
				}

				//Separate links to the same pictures are being produced. This is because the HTML has multiple links to the same picture, so they're all being parsed.
				//Because seperate links to the same pictures are being produced, it makes it hard to compare and determine which are duplicates.

			} catch (IOException e) {
				System.err.println("User Connection Error.");
				break;
			}
			System.out.println("FINISHED PAGE " + currentPage + " OF " + numPages);
			currentPage++;
			incrementURL();
		}
	}
}
