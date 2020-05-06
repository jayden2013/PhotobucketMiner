# PhotobucketMiner
Parses and downloads Photobucket's recent uploads and the profiles of the recent uploaders. Specific users can be targeted, and images with certain properties can be targeted.

# Limitations

##### May 2020: Photobucket removed their recent uploads page. Running RecentUploads will pull the accounts of users who uploaded with the text "recent".

Currently, only photos that aren't inside of a directory can be pulled and saved. This will be addressed in future versions.  

# Usage
For running the main program, which fetches the RecentUploaders and downloads their images:

### Windows:

javac -classpath .;jsoup-1.8.3.jar *.java  
java -classpath .;jsoup-1.8.3.jar RecentUploads \<number of pages to parse\>

### Linux / Mac OS:
javac -classpath .:jsoup-1.8.3.jar *.java  
java -classpath .:jsoup-1.8.3.jar RecentUploads \<number of pages to parse\>  

For downloading the images of specified accounts:  

### Windows:  
javac -classpath .;jsoup-1.8.3.jar *.java  
java -classpath .;jsoup-1.8.3.jar ScrapeUser \<username\> [starting page] [ending page]  

GUI: java -classpath .;jsoup-1.8.3.jar ScrapeUserGUI  

### Linux / Mac OS:
javac -classpath .:jsoup-1.8.3.jar *.java  
java -classpath .:jsoup-1.8.3.jar ScrapeUser \<username\> [starting page] [ending page]  

GUI: java -classpath .:jsoup-1.8.3.jar ScrapeUserGUI
