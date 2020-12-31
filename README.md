# Travel Video Generator

Program in Java that concatenates all your travel media (pics and vids) and, by using its metadata, the program separates everything by locations in the output video. The video result is divided by these locations, where each of them starts with a map of the place and it's followed by all the media created when in that place.<br />

## Specifications

Java version (JRE or JDK): `jre1.8.0_241`<br />
Execution environment: `JavaSE-1.8`<br />
Xampp Apache <br />

### External tools used

The code executes other existing programs, all of which are included in the main folder, but which I do not own. It uses:<br />

ffmpeg (found online)<br />
Exif Tool (found online)<br />
static map lite (found online, by another git user)<br />

## Usage

### Before Executing

Download `travel-video-package` folder and store it in your computer.<br />
Drag `ffmpeg` folder to your Desktop.<br />
Drag `exiftool.exe` file to your Desktop.<br />
Drag `sampleInputFolder` folder to your Desktop.<br />
Drag `YourTrip.jar` file to your Desktop.<br />
Drag `staticmaplite-master` decompressed folder to htdocs.<br />
Start xampp apache<br />

### While executing

For running the jar file, please type `java -jar YourTrip.jar sampleInputFolder` in your command prompt.<br />

### After executing

Video file `RESULT.mkv` as your final output video in `YourVideoOutput` folder.<br />

## Sample output
The gif version of the output is the following:<br />

![alt text](https://github.com/the-other-mariana/travel-video/blob/master/result-gifv.gif)<br />

## Source Code

The folder `travelVideo_MarianaAvalos` has the java project, where the `VideoMaker` file contains the main function.<br />
