# PhotobucketMiner
Fetches, parses, and displays Photobucket's recent uploads. Very basic, very messy, somewhat slow, and thrown together very quickly. However, it can still be very useful. This class provides a basic framework for what will eventually be a nice program. This utilizes JSoup for grabbing the recent uploads page, and the urls for the pages the images are on. However, due to the way Photobucket handles their photos JSoup would not work for getting the direct links to the images. Because of this, a URL, input stream, and buffered reader were used for the second half of this program...in case you were wondering. The program does have a few faults, which will be addressed in future updates.



Usage
<hr>
On Windows:
<br>
javac -classpath .;jsoup-1.8.3.jar RecentUploads.java
<br>
java -classpath .;jsoup-1.8.3.jar RecentUploads

