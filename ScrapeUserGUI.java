import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * A GUI for scraping user profiles.
 * @author Jayden Weaver
 *
 */
public class ScrapeUserGUI {
	//TODO: Fix console output and make GUI better
	
	final static double VERSION = 1.0;

	public static void main(String[] args) throws IOException{
		JFrame frame = new JFrame("Scrape User GUI " + VERSION);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTextField textField = new JTextField(20);
		JTextArea statusArea = new JTextArea(20,20);
		
		///////////////////////////////////////////////////////////////////
		// http://stackoverflow.com/questions/19834155/jtextarea-as-console
		PrintStream out = new PrintStream(new TextAreaOutputStream(statusArea));
		System.setOut(out);
		System.setErr(out);
		///////////////////////////////////////////////////////////////////
		
		ActionListener EnterMonitor = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//do user
				String username = textField.getText();
				System.out.println("WILL ATTEMPT TO PARSE USER: " + username);
				String url = "photobucket.com/user/" + username + "/library/";
				User user = new User(url);
				user.setUsername(username);
				user.parseUser();
			}
		};

		JButton enterButton = new JButton("Scrape User");
		enterButton.addActionListener(EnterMonitor);
		frame.setLayout(new FlowLayout());
		frame.add(textField);
		frame.add(enterButton);
		frame.add(statusArea);
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
		textControl = control;
	}
	
	public void write(int b) throws IOException {
		textControl.append(String.valueOf((char) b));
	}
}





