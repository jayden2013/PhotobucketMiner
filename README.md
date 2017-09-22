# PhotobucketMiner
Parses and downloads Photobucket's recent uploads and the profiles of the recent uploaders. Specific users can be targeted, and images with certain properties can be targeted.

# Limitations
<hr>
Currently, only photos that aren't inside of a directory can be pulled and saved. This will be addressed in future versions.
<br>
Tested 100% working on Windows. Mac OS sort of works, but not quite. Linux was tested and 100% working months ago, but has not been tested with recent updates.
<hr>

# Usage
<hr>
For running the main program, which fetches the RecentUploaders and downloads their images:
<hr>
### On Windows:
<br>
javac -classpath .;jsoup-1.8.3.jar *.java
<br>
java -classpath .;jsoup-1.8.3.jar RecentUploads \<number of pages to parse\>

### Linux / Mac OS:
<br>
javac -classpath .:jsoup-1.8.3.jar *.java
<br>
java -classpath .:jsoup-1.8.3.jar RecentUploads \<number of pages to parse\>
<hr>
For downloading the images of specified accounts:
<hr>
### On Windows:
<br>
javac -classpath .;jsoup-1.8.3.jar *.java
<br>
java -classpath .;jsoup-1.8.3.jar ScrapeUser \<username\> [starting page] [ending page]
<br>

GUI: java -classpath .;jsoup-1.8.3.jar ScrapeUserGUI

### Linux / Mac OS:
<br>
javac -classpath .:jsoup-1.8.3.jar *.java
<br>
java -classpath .:jsoup-1.8.3.jar ScrapeUser \<username\> [starting page] [ending page]
<br>

GUI: java -classpath .:jsoup-1.8.3.jar ScrapeUserGUI
