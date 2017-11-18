import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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

	public String execute(String sql){
		try {
			this.result = this.sqlStatement.executeQuery(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}
	
	public void insert(String sql){
		try{
			this.sqlStatement.execute(sql);
		}
		catch(SQLException e){
			e.printStackTrace();
			System.out.println("blows up here");
		}
	}

	public ArrayList<String> getResults(){
		ArrayList<String> stringList = new ArrayList<String>();
		try {
			while(this.result.next() == true){
				stringList.add(this.result.getString("ID"));
				stringList.add(this.result.getString("UNAME"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stringList;


	}

}
