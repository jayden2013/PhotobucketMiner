import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jsoup.Connection.Response;
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

	private ArrayList<String> imageURLs = new ArrayList<String>();
	private String userURL = "http://";
	private String username = "";
	private int numeral = 0;
	private int numPages = 1;
	private int currentPage = 1;

	public User(String url) {
		this.userURL += url + "?sort=3&page=1"; //the last part is needed to increment the URL.
		try{
			numberOfPages();
			System.out.println("NUMBER OF PAGES: " + this.numPages);
		}
		catch(Exception e){
			System.err.println("User created, but failed to fetch number of pages.");
		}
	}

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
		userURL = userURL.substring(0, userURL.length() - 1) + currentPage;
	}

	/**
	 * Parses and returns the number of pages.
	 * @return number of pages.
	 * @throws IOException
	 */
	public int numberOfPages() throws IOException{
		System.out.println(this.userURL);
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
			}
		}
		return this.numPages;
	}


	/**
	 * Parses the user profile.
	 */
	public void parseUser(){

		System.out.println("made it this far, parse user...");
		while(this.currentPage <= this.numPages){

			try {
				//Get entire photobucket page.
				//Need a user agent in order to get the redirect from photobucket.
				Document photobucketDocument = Jsoup.connect(this.userURL).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2").get();
				//get all the script tags and put them in the element.
				Elements photo = photobucketDocument.getElementsByTag("script");
				//System.out.println(photo);
				Element script = photo.get(photo.size() - 25); //Magic number. Photobucket loves to change this, but as of 01/2017 this is the offset. 

				//System.out.println(script.toString());
				String httpParse = "http://";
				String selection = script.toString();
				ArrayList<String> photoLinkList = new ArrayList<String>();
				StringBuilder losLinks = new StringBuilder(); //TODO: use this to create the links, for efficiency.

				//parse links

				StringTokenizer tokenizer = new StringTokenizer(selection, "\\/"); //use \/ as the delimiter. 
				String currentToken = "";
				String previousToken = "";
				String imageURL = "";
				while (tokenizer.hasMoreTokens()){
					previousToken = currentToken;
					currentToken = tokenizer.nextToken();

					if (currentToken.equals("albums")){
						imageURL = httpParse + previousToken + "/" + currentToken + "/";
						imageURL += tokenizer.nextToken() + "/" + tokenizer.nextToken() + "/" + tokenizer.nextToken();

						//clean up
						char prev = ' ';
						char cur = ' ';
						int counter = 0;
						for (char a : imageURL.toCharArray()){
							cur = a;
							if (cur == 'g' && prev == 'e' || cur == 'g' && prev == 'p' || cur == 'f' && prev == 'i' || cur == 'g' && prev == 'n' || cur == '4' && prev == 'p'){ //for jpeg, jpg, gif, png, and mp4.
								//there's a bug in this. if a username is **eg, pg, if, etc... the link is cut off right there. TODO: Fix this.							
								imageURL = imageURL.substring(0, counter + 1);
								if (photoLinkList.isEmpty()){ //prevent an index out of bounds exception below by checking before trying to access.
									photoLinkList.add(imageURL);
								}
								else if (!imageURL.equals(photoLinkList.get(photoLinkList.size() - 1))){
									//TODO: add the differentiation upon saving the file, that way it's easier. 
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
				for (String s : photoLinkList){
					URL photoURL = new URL(s);
					try{
						BufferedImage photoJoto = ImageIO.read(photoURL);
						directory = new File("Saved_Users\\");
						directory.mkdir();
						directory = new File("Saved_Users\\" + this.username + "\\");
						directory.mkdir();
						outputFile = new File("Saved_Users\\" + this.username + "\\" + this.numeral + ".jpg"); //TODO: Add ability to save png, gif, mp4 with correct file extension.
						ImageIO.write(photoJoto,"jpg", outputFile);
						this.numeral++; //prevent a bug that overwrites file by making numeral a global variable.
						System.out.println(s);
					} catch(Exception e){
						System.out.println("BAD URL..! SKIPPING."); //catch the exception that is thrown by some bad duplicate URLs that redirect you.
					}
				}
				
			//Separate links to the same pictures are being produced. This is because the HTML has multiple links to the same picture, so they're all being parsed.
			//TODO: Use a string builder because it would be faster.

		} catch (IOException e) {
			System.err.println("User Connection Error.");
		}
			System.out.println("FINISHED PAGE " + currentPage + " of " + numPages);
			currentPage++;
			incrementURL();
		}

	}
}
