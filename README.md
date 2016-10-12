# PhotobucketMiner
Fetches, parses, and displays Photobucket's recent uploads.
 This utilizes JSoup for grabbing the recent uploads page, and the urls for the pages the images are on.
 However, due to the way Photobucket handles their photos JSoup would not work for getting the direct links to the images. 
 Because of this, a URL, input stream, and buffered reader were used for the second half of this program...in case you were wondering why JSoup wasn't used. 
 The program does have a few faults, which will be addressed in future updates.

Limitations
<hr>
Currently, only photos that aren't inside of a directory can be pulled and saved. This will be addressed in future versions. Because this is going to be fixed soon, links to images within directories are still parsed, but not working. If all of the fetched images are inside of directories, a connection error can occur and no images will be saved.
<br>
<hr>

Usage
<hr>
On Windows:
<br>
javac -classpath .;jsoup-1.8.3.jar RecentUploads.java
<br>
java -classpath .;jsoup-1.8.3.jar RecentUploads <number of pages to parse>

Linux / Mac OS:
<br>
javac -classpath .:jsoup-1.8.3.jar RecentUploads.java
<br>
java -classpath .:jsoup-1.8.3.jar RecentUploads <number of pages to parse>
