import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.util.ArrayList;

/**
 * Class to manage a database.
 * @author Jayden Weaver
 *
 */
public class Database {
	String databaseFile;
	String url;
	ResultSet result;
	Statement sqlStatement;
	Connection connection;
	public Database(String databaseFile) {

		try{
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			this.databaseFile = databaseFile;
			this.connection = DriverManager.getConnection("jdbc:ucanaccess://" + databaseFile);
			this.sqlStatement = this.connection.createStatement();

		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	/**
	 * Executes an SQL Query.
	 * @param sql
	 * @return
	 */
	public String execute(String sql){
		try {
			this.result = this.sqlStatement.executeQuery(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * Executes an SQL Query.
	 * @param sql
	 */
	public void insert(String sql){
		try{
			this.sqlStatement.execute(sql);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}

//	public ArrayList<String> getResults(){
//		ArrayList<String> stringList = new ArrayList<String>();
//		try {
//			while(this.result.next() == true){
//				stringList.add(this.result.getString("ID"));
//				stringList.add(this.result.getString("UNAME"));
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return stringList;
//
//
//	}

	/**
	 * Returns true if database contains duplicate users.
	 * @param username
	 * @return
	 */
	public boolean hasDuplicate(String username){
		try {
			this.result = this.sqlStatement.executeQuery("SELECT COUNT(*) FROM ACCOUNTS WHERE UNAME = '" + username + "';");
			this.result.next();	
			return Integer.valueOf(this.result.getString(1)) > 1;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Deletes duplicate database entries.
	 * @param username
	 * @return
	 */
	public boolean purgeDuplicate(String username){
		try{
			while(hasDuplicate(username)){
				this.result = this.sqlStatement.executeQuery("SELECT ID FROM ACCOUNTS WHERE UNAME = '" + username + "';");
				this.result.next();
				this.sqlStatement.execute("DELETE FROM ACCOUNTS WHERE ID = '" + this.result.getString(1) + "';");
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}

}
