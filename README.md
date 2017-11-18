# PhotobucketMiner
Parses and downloads Photobucket's recent uploads and the profiles of the recent uploaders. Specific users can be targeted, and images with certain properties can be targeted.

# Limitations
Currently, only photos that aren't inside of a directory can be pulled and saved. This will be addressed in future versions.  

# Usage
For running the main program, which fetches the RecentUploaders and downloads their images:

### Windows:

javac -classpath .;* *.java  
java -classpath .;* RecentUploads \<number of pages to parse\>

### Linux / Mac OS:
javac -classpath .:* *.java  
java -classpath .:* RecentUploads \<number of pages to parse\>  

For downloading the images of specified accounts:  

### Windows:  
javac -classpath .;* *.java  
java -classpath .;* ScrapeUser \<username\> [starting page] [ending page]  

GUI: java -classpath .;* ScrapeUserGUI  

### Linux / Mac OS:
javac -classpath .:* *.java  
java -classpath .:* ScrapeUser \<username\> [starting page] [ending page]  

GUI: java -classpath .:* ScrapeUserGUI
