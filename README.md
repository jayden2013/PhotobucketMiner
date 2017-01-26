# PhotobucketMiner
Parses and downloads Photobucket's recent uploads and the profiles of the recent uploaders. 
 This utilizes JSoup for grabbing the recent uploads page, and the urls for the pages the images are on.
 However, due to the way Photobucket handles their photos JSoup would not work for getting the direct links to the images. 
 Because of this, a URL, input stream, and buffered reader were used for the second half of this program...in case you were wondering why JSoup wasn't used. 

#Limitations
<hr>
Currently, only photos that aren't inside of a directory can be pulled and saved. This will be addressed in future versions.
<br>
<hr>

#Usage
<hr>
For running the main program, which fetches the RecentUploaders and downloads their images:
<hr>
###On Windows:
<br>
javac -classpath .;jsoup-1.8.3.jar *.java
<br>
java -classpath .;jsoup-1.8.3.jar RecentUploads \<number of pages to parse\>

###Linux / Mac OS:
<br>
javac -classpath .:jsoup-1.8.3.jar *.java
<br>
java -classpath .:jsoup-1.8.3.jar RecentUploads \<number of pages to parse\>
<hr>
For downloading the images of specified accounts:
<hr>
###On Windows:
<br>
javac -classpath .;jsoup-1.8.3.jar *.java
<br>
java -classpath .;jsoup-1.8.3.jar ScrapeUser \<username\> [starting page] [ending page]
<br>

GUI: java -classpath .;jsoup-1.8.3.jar ScrapeUserGUI

###Linux / Mac OS:
<br>
javac -classpath .:jsoup-1.8.3.jar *.java
<br>
java -classpath .:jsoup-1.8.3.jar ScrapeUser \<username\> [starting page [ending page]
<br>

GUI: java -classpath .:jsoup-1.8.3.jar ScrapeUserGUI
