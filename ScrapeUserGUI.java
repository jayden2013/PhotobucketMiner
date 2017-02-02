import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * A GUI for scraping user profiles.
 * @author Jayden Weaver
 *
 */
public class ScrapeUserGUI {

	final static double VERSION = 1.0;

	public static void main(String[] args) throws IOException{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			System.err.println(e1);
		}
		JFrame frame = new JFrame("PhotobucketMiner " + VERSION);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTextField textField = new JTextField(20);
		JTextArea statusArea = new JTextArea(20,20);
		JButton enterButton = new JButton("Scrape User");
		JLabel pageLabel = new JLabel("page");
		JTextField startPageField = new JTextField(3);
		JLabel throughLabel = new JLabel("through");
		JTextField endPageField = new JTextField(3);
		///////////////////////////////////////////////////////////////////
		// http://stackoverflow.com/questions/19834155/jtextarea-as-console
		PrintStream out = new PrintStream(new TextAreaOutputStream(statusArea));
		System.setOut(out);
		System.setErr(out);
		///////////////////////////////////////////////////////////////////

		ActionListener EnterMonitor = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//do user
				enterButton.setText("SCRAPING..."); //button text while scraping
				enterButton.setForeground(Color.RED); //button text color while scraping				
				String username = textField.getText();

				//check for empty username field
				if (username.equals("")){
					System.err.println("Cannot leave field blank!");
					enterButton.setText("Scrape User"); //reset button text after scraping
					enterButton.setForeground(Color.BLACK); //reset text color after scraping 
					return;
				}

				System.out.println("WILL ATTEMPT TO PARSE USER: " + username);
				String url = "photobucket.com/user/" + username + "/library/";
				User user = new User(url);

				boolean pageSet = false;
				//check to see if user set a start page
				if (!startPageField.getText().equals("")){
					user.setCurrentPage(Integer.parseInt(startPageField.getText()));
					pageSet = true;
				}

				//check to see if user set an end page
				if (!endPageField.getText().equals("") && pageSet){
					if (Integer.parseInt(startPageField.getText()) > Integer.parseInt(endPageField.getText())){ //check size of pages
						System.out.println("Start page cannot be greater than the end page!"); //let the user know
						enterButton.setText("Scrape User"); //reset button text after scraping
						enterButton.setForeground(Color.BLACK); //reset text color after scraping 
						return;
					}
					user.setNumberOfPages(Integer.parseInt(endPageField.getText()));

				}

				user.setUsername(username);
				user.parseUser();
				enterButton.setText("Scrape User"); //reset button text after scraping
				enterButton.setForeground(Color.BLACK); //reset text color after scraping 
			}
		};

		JTabbedPane tabbedPane = new JTabbedPane();

		//Main tab
		JPanel panel1 = new JPanel();
		JLabel userLabel = new JLabel("User: ");
		panel1.add(userLabel);
		panel1.add(textField);
		panel1.add(pageLabel);
		panel1.add(startPageField);
		panel1.add(throughLabel);
		panel1.add(endPageField);
		panel1.add(enterButton);

		tabbedPane.addTab("Z-750 Binary Rifle ", panel1); //for accuracy

		//Recent Uploads and Search terms
		JPanel scatterPanel = new JPanel();
		JTextField searchTermText = new JTextField(20);
		JButton searchButton = new JButton("Scrape Search Term");
		searchButton.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

		//when the button is pressed, being the parsing.
		searchButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//change button
				searchButton.setText("SCRAPING...");
				searchButton.setForeground(Color.RED);
				
				String args[];
				if (searchTermText.getText().equals("")){
					args = new String[1];
					args[0] = "1";
					RecentUploads.main(args);
				}
				else{
					args = new String[2];
					args[0] = "1";
					args[1] = searchTermText.getText();
					RecentUploads.main(args);
				}
				
				//reset button
				searchButton.setText("Scrape Search Term");
				searchButton.setForeground(Color.BLACK);
			}
		});
		
		JLabel searchLabel = new JLabel("Search: ");
		scatterPanel.add(searchLabel);
		scatterPanel.add(searchTermText);
		scatterPanel.add(searchButton);
		tabbedPane.addTab("Z-180 Scattershot", scatterPanel); //sometimes you'll hit something

		//Logging tab
		JPanel panel2 = new JPanel();
		Font statusFont = new Font("Serif", Font.BOLD, 12);
		statusArea.setFont(statusFont);
		panel2.add(statusArea);
		tabbedPane.addTab("Console", panel2);

		//about tab
		JLabel author = new JLabel("@author: Jayden Weaver");
		JLabel github = new JLabel("github.com/jayden2013");
		JLabel space = new JLabel(" ");
		JLabel year = new JLabel("2017");
		JPanel panel3 = new JPanel();
		panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
		panel3.add(author);
		panel3.add(github);
		panel3.add(space);
		panel3.add(year);
		tabbedPane.addTab("About", panel3);

		//Add hyperlink to author label
		author.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				try{
					Desktop.getDesktop().browse(new URI("http://www.twitter.com/weaverfever69")); //twitter account
				}
				catch(Exception ex){
					System.err.println(ex);
				}
			}			
		});
		author.setCursor(new Cursor(Cursor.HAND_CURSOR));

		//Add hyperlink to github label
		github.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				try{
					Desktop.getDesktop().browse(new URI("http://www.github.com/jayden2013")); //github account
				}
				catch(Exception ex){
					System.err.println(ex);
				}
			}			
		});
		github.setCursor(new Cursor(Cursor.HAND_CURSOR));	

		//add enter button listener
		enterButton.addActionListener(EnterMonitor);
		enterButton.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR)); //lel

		//initialize frame
		frame.setLayout(new FlowLayout());
		frame.add(tabbedPane);
		frame.pack();
		frame.setVisible(true);
	}
}

/**
 * For displaying console output inside the GUI
 * @author Jayden Weaver 
 * http://stackoverflow.com/questions/19834155/jtextarea-as-console
 */
class TextAreaOutputStream extends OutputStream {
	private JTextArea textControl;

	public TextAreaOutputStream(JTextArea control){
		this.textControl = control;
	}

	public void write(int b) throws IOException {
		this.textControl.append(String.valueOf((char) b));
	}
}