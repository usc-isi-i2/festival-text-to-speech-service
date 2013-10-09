festival-text-to-speech-service
===============================
###Overview
This sample web service calls upon a Festival server to do synthesize text into audio. Communication between the browser, web service, and Festival service is as follows:

1) Browser sends HTTP request to the Java-based web service

2) Web service acts as a client and calls the Festival speech server

3) Festival server returns audio as a byte array

4) Web service handles the byte array audio and returns it to the browser

The Festival TTS server returns all audio as some form of uncompressed format such as WAV. In order for the web service to return smaller audio formats such as MP3, 
edits would have to be made to the web service so that it converts the byte array into the desired format. This might be done using libraries such as LAME, Tritonus, or possibly even the built-in Java Sound API.
Just to note, the byte array returned by Festival is a full audio file, headers included, so keep that in mind, if you wish to conver formats.

###Installation Instructions for Festival
####Requirements
+ Unix-based environment, preferably Linux (Do **NOT** use Windows with Cygwin)
+ A C++ compiler
+ GNU Make any recent version
####Deciding which to version to use
+ Default voices for Festival are generally low quality, so additional voices must be added.
+ Among the most popular additional voices for Festival are the Nitech HTS voices and the enhanced CMU Arctic voices
+ Nitech HTS voices only work on version 1.96 of Festival, which has a harder installation process
+ CMU Arctic voices work on both the 1.96 and newest 2.1 versions of Festival
####Instructions for installing v1.96
1) Download "festival.tar.gz" and "speech-tools.tar.gz" from the "Festival-1.96-revisions" folder

2) Move these archives into whatever folder you wish Festival to rest in
```
tar xzf festival.tar.gz
tar xzf speech-tools.tar.gz
sudo apt-get install libncurses5-dev
cd speech-tools
./configure
make
make test
make install
cd ../festival
./configure
make
make test (optional)
make install
```

####Instructions for installing v2.1
1) Easiest method is to run "sudo apt-get install festival" to get the Debian version of festival. Otherwise, continue reading for how to install festival from source

2) Go to [http://festvox.org/packed/festival/2.1/] (http://festvox.org/packed/festival/2.1/) and download 
	festival-2.1-release.tar.gz, festlex_CMU.tar.gz, festlex_OALD.tar.gz, festlex_POSLEX.tar.gz, and speech_tools-2.1-release.tar.gz. 
	The other archives are optional, and merely offer a set of working voices. If you wish to test the festival installation using the 
	given test suite, download 	the other all the remaining files as well.

3) Unzip all the archives using a "tar xzf <filename>" command and move the "festival" and "speech-tools" folders into whatever directory you wish Festival to rest in

4) Change directories to wherever festival is
```
sudo apt-get install libncurses5-dev
cd speech-tools
./configure
make
make test
make install
cd ../festival
./configure
make
make test
make install
```

####Installing Additional Voices
+ Just follow the instructions [here:](http://ubuntuforums.org/showthread.php?t=751169)
+ Highly recommended are the Enhanced Arctic CMU voices and/or the Nitech HTS voices. CMU voices are far bigger, but work on v2.1, while Nitech only works on v1.96
+ After installing the voices, be sure to edit the voices.scm file (located in festival>lib) to include the names of whatever voices you've added. This edit should be done in the "(defvar default-voice-priority-list " function, located around line 325.
+ To make a voice the default voice, either edit the voice list voices.scm so that the desired voice is first on the list, or edit the festival.scm file (located in festival>lib) so that "(set! voice_default 'voice_{NAME OF VOICE}')" is added at the end.
+ **NOTE** The Enhanced Arctic CMU voices CANNOT be set as defaults for some reason. Therefore, to make sure festival is able to run, make sure to have an installed and useable voice (the default diphone voices, or even the Nitech voices) at the top of the voice list. Do not edit the festival.scm file to change the default voice.

####Running the Festival server
+ To run the festival server, either export the PATH to point to the bin inside the festival folder (`export PATH={path to main festival folder}/festival/bin:$PATH`), 
	or manually type out the path and do a `festival --server` from the command line. Of course, include the "./" prefix if done without exporting the PATH.
+ By default, Festival runs on port 1314. To change the port at runtime, add "'(set! server_port PORT)'" to the command, where PORT is the desired port number. Ex: `festival --server '(set! server_port 1515)'`
+ To edit any defaults, including the port numbers, number of concurrent clients, security settings, etc...Edit the festival.scm file (located at festival>lib). Information about the server settings begins at around line 408.

####Additional information
+ The Festival manual is located at [http://www.cstr.ed.ac.uk/projects/festival/manual/festival_toc.html] (http://www.cstr.ed.ac.uk/projects/festival/manual/festival_toc.html). Reference it for more details and client/server API.

###Creating the web service
+ An example web service is included in the "festival_service" folder and uses the Jersey framework. To run this example web service using Eclipse:
	- Make a new Dynamic Web Project, and call it "festival_service." Make sure to check the box that says "Generate web.xml deployment descriptor"
	![alt text] (festival_service/Project-creation.png "")
	- Replace the "WEB-INF" folder under "WebContent" with the one from the example.
	- Create a festival_service.client package and add the Client.java file to it
	- Create a festival_service.rest package and add the Festival_service.java file to it.
	- Set up the server and add the project to it.
	- Run
	- To test Festival client and server, direct your browser to "localhost:{PORT}/festival_service/rest/audiotest/get/hello" which should return a wav file of the word "hello"
+ Details about the source code:
	- Client.java is the actual Festival client. It communicates with the Festival server using Scheme commands and returns a byte array of the audio file in some uncompressed format (wav, nist, ulaw, etc.). 
		This class handles customization of server location, voice selection, text mode (not demonstrated...see manual), and file type. 
		As it is, all options except the file type have been hard-coded into the client class, though of course they can easily be changed to parameters.
	- Festival_service.java demonstrates how to call the Festival client. The first function, returnSound() gets the desired text from the URL and returns the audio file in raw, binary format. The second function, returnSound2() receives a JSON and returns a JSON as well. 
		This second function offers an example of how to parse the wav-format header for information about the audio (sampling rate, number of samples). The audio file itself is returned as a base64 string inside the JSON.
		If any audio conversions are to be done, this should be the class to do either it or call upon the class that does.





