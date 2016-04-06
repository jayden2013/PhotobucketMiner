
/**
 * @author Jayden Weaver
 */

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/*
 * Fetches, parses, and displays Photobucket's recent uploads. Very basic, very messy, somewhat slow, and thrown together very quickly. 
 * However, it can still be very useful. This class provides a basic framework for what will eventually be a nice program.
 * This utilizes JSoup for grabbing the recent uploads page, and the urls for the pages the images are on.
 * However, due to the way Photobucket handles their photos JSoup would not work for getting the direct links to the images.
 * Because of this, a URL, input stream, and buffered reader were used for the second half of this program...in case you were wondering.
 * The program does have a few faults, which will be addressed in future updates.
 */
public class RecentUploads {

	public static void main(String[] args) {
		// URL to get
		String URL = "http://www.photobucket.com/recentuploads?page=1";
		try {
			// Get entire document
			org.jsoup.nodes.Document photobucket = Jsoup.connect(URL).get();

			// Put document into an element
			Elements photo = photobucket.getElementsByTag("script");

			// Get only the section we want, put it into an element of its own.
			Element script = photo.get(photo.size() - 11);

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

			// TODO: Make this work with all photos. Currently only gets the links for photos that aren't several folders deep. Mostly working though.
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
			//	System.out.println("TEMP: " + temp);
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
			//	System.out.println(albumAndOrPhoto);
				finalString += albumAndOrPhoto;
				// Remember to add the final string to the string array for use later.
				linkArray.set(i, finalString);
				//System.out.println(finalString);

			}

			// New array list for now, until the connection bug is fixed.
			ArrayList<String> losLinksArray = new ArrayList<String>();
			int linkCount = 0; //we'll use a link count in case no photos are shown, we can warn the user.
			DecimalFormat df = new DecimalFormat("0.00"); //for use showing percentage...
			ArrayList<String> profileLinkArray = new ArrayList<String>(); //for profile links
			for (int urls = 0; urls < linkArray.size(); urls++) {

				if (linkArray.get(urls).substring(linkArray.get(urls).length() - 4, linkArray.get(urls).length()).equals("html")) {
					linkCount++;
					// before we can do a frame, we need to get the direct link to images. otherwise we're fucked.
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
					
					profileLink += "photobucket.com/" + "user/" + usernameArray.get(urls) + "/library/"; // parse the url to the user's profile. this can be used later on to download all of the user's photos.
					//until then, we will display the link after we're done.
					//add to an arraylist
					profileLinkArray.add(profileLink);
					
				}
				else{ //if the photo is in a folder, reparse.s
					
				}				
				
				System.out.println(df.format((double)(urls / (double) linkArray.size()) * 100) + "% Complete.");
			}
			System.out.println("100.00% Complete.");
			if (linkCount == 0){
				System.err.println("No links were parsed! This is due to a parsing limitation in this version of code."); //Limitation: can't get images inside of directories. Requires more parsing.
				System.err.println("No GUI will be loaded.");
				return; //Don't bother loading a frame if no links were parsed.
			}
			
			//Print all of the profile links.
			System.out.println("Profile Links: ");
			for (int k = 0; k < profileLinkArray.size(); k++){
				System.out.println(profileLinkArray.get(k));				
			}

			// ok frame time.?
			JFrame frame = new JFrame("Recent Uploads");
			frame.setLayout(new GridLayout(6, 6));
			int height = 0;
			int width = 0;
			//For saving the photos.
			int numeral = 0;
			File outputFile, directory;
			
			
			// time to loop through to display images...
			for (int linkArrayLoop = 0; linkArrayLoop < losLinksArray.size(); linkArrayLoop++) {
				URL photoURL = new URL(losLinksArray.get(linkArrayLoop));
				BufferedImage photoJoto = ImageIO.read(photoURL);
				ImageIcon photoPendejo = new ImageIcon(photoJoto);
				width = photoPendejo.getIconWidth();
				height = photoPendejo.getIconHeight();
				JLabel label = new JLabel(photoPendejo);
				label.setSize(width, height);
				// frame.getContentPane().add(label);
				frame.add(label);
				frame.pack();
				
				//save the photo - this assumes all photos are jpgs.
				//assuming they are all jpgs is fine for jpgs and pngs.
				//gifs too...they just won't animate.
				//differentiation could come in an update.
				directory = new File("saved\\");
				directory.mkdir();
				directory = new File("saved\\" + usernameArray.get(numeral) + "\\");
				directory.mkdir();
				outputFile = new File("saved\\" + usernameArray.get(numeral)+"\\" +"saved_" + numeral + ".jpg"); //TODO: Add ability to save photos in directories to same directory inside of username locally.
				ImageIO.write(photoJoto, "jpg", outputFile);
				numeral++;
			}

			frame.setSize(800, 800);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setVisible(true);

		} catch (IOException e) {
			System.out.println("Connection Failure.");
		}

	}

}