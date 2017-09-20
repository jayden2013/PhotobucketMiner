import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * A class that stores information about Photobucket.com
 * @author Jayden Weaver
 *
 */
public class PBisDown {

	private String url = "http://downforeveryoneorjustme.com/photobucket.com"; //Website to use to check if Photobucket is down.
	private boolean isDown = false;

	public PBisDown() {
		try {
			Document photobucket = Jsoup.connect(this.url).timeout(10000).get();
			Elements elements = photobucket.getElementsContainingText("It's not just you!");
			this.isDown = !elements.isEmpty();
		} catch (IOException e) {
			System.err.println("Unable to verify status of Photobucket. Failed to connect to remote server.");
			return;
		}
	}

	/**
	 * Returns true if Photobucket is down, false if Photobucket is up and running.
	 * @return
	 */
	public boolean isDown(){
		return this.isDown;
	}
}