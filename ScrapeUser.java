
/**
 *  
 * @author Jayden Weaver
 * Scrapes the profile of a specified user.
 * 
 */
public class ScrapeUser {

	public static void main(String args[]){
		if (args.length < 1 || args.length > 3){
			System.err.println(usage());
			return;
		}
		String username = args[0];
		System.out.println("WILL ATTEMPT TO PARSE USER: " + username);
		String url = "photobucket.com/user/" + username + "/library/";
		User user = new User(url);
		user.setUsername(username);
		if (args.length == 2){ //if the user wants a start page
			user.setCurrentPage(Integer.parseInt(args[1]));
		}
		if (args.length == 3){ //if the user wants a start page and an end page
			user.setCurrentPage(Integer.parseInt(args[1]));
			user.setNumberOfPages(Integer.parseInt(args[2]));
		}
		user.parseUser();		
	}

	/**
	 * Returns a usage statement.
	 * @return usage
	 */
	private static String usage(){
		return "Windows: \n"
				+ "java -classpath .;jsoup-1.8.3.jar ScrapeUser <username>\n" + 
				"Linux / Mac OS: \n" + "java -classpath .:jsoup-1.8.3.jar ScrapeUser <username>";
	}
}