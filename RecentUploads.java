
/**
 * @author Jayden Weaver
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Parses Photobucket's recent uploads page and the profiles of the recent uploaders.
 * 
 */
public class RecentUploads {

	public static void main(String[] args) {
		if (args.length < 1 || args.length > 1){
			System.err.println(usage());
			return;
		}
		int pageNumber = 1; //initial page number.
		int numberOfPages = 0;
		try{
			numberOfPages = Integer.parseInt(args[0]); //number of pages to parse.
		}
		catch(NumberFormatException e){
			System.err.println("Enter a number, dumbass.\n");
			System.err.println(usage());
			return;
		}
		while(pageNumber <= numberOfPages){
			// URL to get
			String URL = "http://www.photobucket.com/recentuploads?page=" + pageNumber;
			try {
				// Get entire document
				org.jsoup.nodes.Document photobucket = Jsoup.connect(URL).get();

				// Put document into an element
				Elements photo = photobucket.getElementsByTag("script");

				// Get only the section we want, put it into an element of its own.
				Element script = photo.get(photo.size() - 12); //Changed from 11 to 12, because photobucket added advertisements, which upset the offset. Fixed 10/2016.
				//Changed from 12 to 13, because photobucket sucks. Offset was upset. Fixed 11/2016.
				//Changed from 13 to 12, because photobucket sucks. Offset was upset. Fixed 12/2016.

				// Put the section into a string we can manipulate.
				String selection = script.toString();

				// now the hard part....parsing through...by hand......RIP me.

				// we'll need a string builder
				StringBuilder losLinks = new StringBuilder();
				boolean isLink = false;
				ArrayList<String> linkArray = new ArrayList<String>();

				// loop.
				char prev = 'a', current = 'a';
				for (char ch : selection.toCharArray()) {
					current = ch;
					if (current == 'U' && prev == '[') {
						isLink = true;
					}

					if (isLink) {
						losLinks.append(current);

						if (prev == 'l' && current == ']') {
							isLink = false;
							linkArray.add(losLinks.toString());
							losLinks.delete(0, losLinks.length());
						}

					}

					prev = ch;

				}

				// This string will make parsing easier.
				String parseEasy = "http://media.photobucket.com/user/";
				String temp, finalString;
				ArrayList<String> usernameArray = new ArrayList<String>();

				for (int i = 0; i < linkArray.size(); i++) {
					temp = linkArray.get(i);
					temp = temp.substring(42, temp.length());
					// We'll use this offset to cut the string again, to get the username only.
					int offSet = 0; // no magic numbers here, no sir.
					// reset our current and previous.
					current = 'a';
					prev = 'a';

					for (char ch : temp.toCharArray()) {
						current = ch;
						if (current == '/' && prev == '\\') {
							break;
						}
						prev = current;
						offSet++;
					}

					// ok, now substring again.
					String username = temp.substring(0, offSet - 1);
					//add to username arraylist
					usernameArray.add(username);
					//concat
					finalString = parseEasy + username + "/media";

					// last thing. and i really should make a method for this. oh well. maybe later.
					// reset current and previous
					current = 'a';
					prev = 'a';
					// let's substring again.
					temp = temp.substring(offSet + 7); // magic number, being the number of characters in /\media\ minus 1.
					offSet = 0;
					for (char ch : temp.toCharArray()) {
						current = ch;
						if (prev == '\\' && current == '/') {
							break;
						}
						prev = current;
						offSet++;
					}
					String albumAndOrPhoto = temp.substring(0, offSet - 1);
					finalString += albumAndOrPhoto;
					// Remember to add the final string to the string array for use later.
					linkArray.set(i, finalString);

				}

				// New array list for now, until the connection bug is fixed.
				ArrayList<String> losLinksArray = new ArrayList<String>();
				int linkCount = 0; //we'll use a link count in case no photos are shown, we can warn the user.
				ArrayList<String> profileLinkArray = new ArrayList<String>(); //for profile links
				ArrayList<String> newUserArray = new ArrayList<String>(); //a new arraylist for usernames
				for (int urls = 0; urls < linkArray.size(); urls++) {

					if (linkArray.get(urls).substring(linkArray.get(urls).length() - 4, linkArray.get(urls).length()).equals("html")) {
						linkCount++;
						// create url
						URL url = new URL(linkArray.get(urls));
						InputStream inStream = url.openStream();
						BufferedReader bufRead = new BufferedReader(new InputStreamReader(inStream));
						String directLink;
						int lineCount = 0;
						while (true) {
							directLink = bufRead.readLine();
							if (lineCount == 19) { // 19 is the magic number, the line the direct link to the image is on.
								break;
							}
							lineCount++;
						}

						// now, parse..
						directLink = directLink.substring(40, directLink.length() - 4);
						losLinksArray.add(directLink);
						//System.out.println(directLink);
						String profileLink = directLink.substring(7, 13);
						if (profileLink.endsWith("p")){
							profileLink = profileLink.substring(0, profileLink.length() - 1);
						}
						else if (profileLink.endsWith("h")){ //This is to fix the problem where links with a shorter string of characters before the domain name would become corrupted. 
							profileLink = profileLink.substring(0, profileLink.length() - 2);
						}

						profileLink += "photobucket.com/" + "user/" + usernameArray.get(urls) + "/library/"; // parse the url to the user's profile. this can be used later on to download all of the user's photos.
						//until then, we will display the link after we're done.
						//add to an arraylist
						profileLinkArray.add(profileLink);
						newUserArray.add(usernameArray.get(urls));
					}

				}
				
				if (linkCount == 0){
					System.err.println("No links were parsed!"); //Limitation: can't get images inside of directories. Requires more parsing.
					return; //Don't bother loading a frame if no links were parsed.
				}

				//Print all of the profile links.
				System.out.println("FETCHED USER PROFILES...");
				System.out.println("WILL ATTEMPT TO PARSE: ");
				for (int k = 0; k < profileLinkArray.size(); k++){
					System.out.println(profileLinkArray.get(k));				
				}

				int i = 0;
				ArrayList<String> oldProfileLinkArray = new ArrayList<String>();
				boolean purge = false;
				for (String profileLink : profileLinkArray){
					purge = false;
					User user = new User(profileLink);
					user.setUsername(newUserArray.get(i));
					for (String oldProfileLink : oldProfileLinkArray){
						if (oldProfileLink.equals(profileLink)){
							purge = true;
							//set purge variable so we will do nothing.
						}
					}
					if (!purge){
						user.parseUser();
					}
					//keep track of profiles we've already parsed.
					oldProfileLinkArray.add(profileLink);
					i++;
				}

			} catch (IOException e) {
				System.err.println("Connection Failure.");
			}

			pageNumber++;
		}

	}

	private static String usage(){
		return "Windows: \n"
				+ "java -classpath .;jsoup-1.8.3.jar RecentUploads <number of pages to parse>\n" + 
				"Linux / Mac OS: \n" + "java -classpath .:jsoup-1.8.3.jar RecentUploads <number of pages to parse>";
	}
}