import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class PBisDown {

	private String url = "http://isitdownorjust.me/photobucket-com/"; //Website to use to check if Photobucket is down.
	private boolean isDown = false;

	public PBisDown() {
		try {
			Document photobucket = Jsoup.connect(url).get();
			Elements elements = photobucket.getElementsContainingText("Photobucket.com seems to be down.");
			
			if (elements.isEmpty()){
				this.isDown = false;
			}
			else{
				this.isDown = true;
			}

		} catch (IOException e) {
			System.err.println("Failed to connect to remote server.");
			return;
		}
	}

	/**
	 * Returns true if Photobucket is down, false if Photobucket is up and running.
	 * @return
	 */
	public boolean isDown(){
		return isDown;
	}
}