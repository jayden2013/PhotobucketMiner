/**
 *  
 * @author Jayden Weaver
 *
 */

/*
 * Scrapes the profile of a specified user.
 */
public class ScrapeUser {

	public static void main(String args[]){
		if (args.length < 1 || args.length > 1){
			System.err.println(usage());
			return;
		}
		String username = args[0];
		System.out.println("WILL ATTEMPT TO PARSE USER: " + username);
		String url = "photobucket.com/user/" + username + "/library/";
		User user = new User(url);
		user.setUsername(username);
		user.parseUser();		
	}

	private static String usage(){
		return "Windows: \n"
				+ "java -classpath .;jsoup-1.8.3.jar ScrapeUser <username>\n" + 
				"Linux / Mac OS: \n" + "java -classpath .:jsoup-1.8.3.jar ScrapeUser <username>";
	}

}
