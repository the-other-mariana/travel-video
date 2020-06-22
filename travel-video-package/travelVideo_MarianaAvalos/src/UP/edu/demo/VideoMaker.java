package UP.edu.demo;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;


public class VideoMaker {
	
	//Global variables
	static Map<String, Trip> trips = new HashMap<String, Trip>();
	static String inputPath = "";
	static String desktopDir = "";
	static String size = "800x600";
	static String scale = "800:600";
	static int numFiles = 0;
	static String INITIAL_POINT_GRAPHIC = "ol-marker-green";
	static String DESTINATION_POINT_GRAPHIC = "ol-marker";
	static String DEFAULT_GRAPHIC = "bullseye";
	static String destDir = "";
	
	//main class
	public static void main(String[] args) throws IOException, ParseException{
		
		//validate that user gives folder argument to the jar file
		if(args.length != 1) {
			System.out.println("Please insert the name of your input folder at Desktop.");
			System.exit(0);
		}
        
		//getting current Desktop
		FileSystemView filesys = FileSystemView.getFileSystemView();
		desktopDir = filesys.getHomeDirectory().getAbsolutePath() + "\\";
		System.out.println("desktop: " + desktopDir);
		
		inputPath = desktopDir + args[0];
		System.out.println("input: " + inputPath);
		
		//creating output folder
		new File(desktopDir + "YourVideoOutput").mkdirs();
		destDir = desktopDir + "YourVideoOutput" + "\\";
		
		//going through input folder
		try(Stream<Path> paths = Files.walk(Paths.get(inputPath))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                	numFiles++;
                    System.out.println(filePath.toString());
                    File file = new File(filePath.toString());
                    System.out.println(file.getAbsolutePath());
                    
                    try {
						execExiftool(file.getAbsolutePath(), filePath.toString());
					} catch (IOException e) {
						System.out.println("Problem executing exif.");
						System.exit(0);
					}
                }
            });
        } catch (IOException e) {
            System.out.println("Problems finding folder. Make sure the folder is in Desktop.");
            System.exit(0);
        }
		
		//validation for usable input folder
		if(numFiles == 0) {
			System.out.println("Empty folder. No media to include.");
			System.exit(0);
		}
		if(trips.size() == 0) {
			System.out.println("Missing data on files. No media to include.");
			System.exit(0);
		}
		
		Vector <String> keys = new Vector<String>(trips.keySet());
        for(String key : keys) {
        	System.out.println("-------------------------");
        	System.out.println("GPS: " + key);
        	System.out.println("Time: " + trips.get(key).getTime());
        }
        
        //ordering array of trips by date
        Collection<Trip> values = trips.values(); 
        ArrayList<Trip> tripList = new ArrayList<Trip>(values);
        tripList.sort(Comparator.comparing(o -> o.getTime()));
        
        //calculating zoom and center of general image
        int zoomGralImage = 18;
        if(tripList.size() > 1) zoomGralImage = ImageTools.calculateZoom(tripList.get(0), tripList.get(tripList.size() - 1));
        float centerX = ImageTools.averageCenter(tripList.get(0).lat, tripList.get(tripList.size() - 1).lat);
        float centerY = ImageTools.averageCenter(tripList.get(0).lon, tripList.get(tripList.size() - 1).lon);
        
        //marking origin place and destination in general image
        String markersGralImage = "center=" + centerX + "," + centerY + "&zoom=" + zoomGralImage + "&size=" + size + "&maptype=mapnik&markers=";
        for(int i = 0; i < tripList.size(); i++) {
        	String graphic = DEFAULT_GRAPHIC;
        	if(i == 0) {
        		graphic = INITIAL_POINT_GRAPHIC;
        		markersGralImage += tripList.get(i).getGps() + "," + graphic;
        		if(i < tripList.size() - 1 && tripList.size() > 1) markersGralImage += "|";
        	}
        	if(i == tripList.size() - 1) {
        		graphic = DESTINATION_POINT_GRAPHIC;
        		markersGralImage += tripList.get(i).getGps() + "," + graphic;
        	}
        }
        
        //saving general image
        Image overview = null;
		String link = "http://localhost/staticmaplite-master/staticmap.php?" + markersGralImage;
		URL url = new URL(link);
		overview = ImageIO.read(url);
		BufferedImage bi = (BufferedImage)overview;
		File overviewFile = new File(destDir + "overview.png");
		ImageIO.write(bi, "png", overviewFile);
		tripList.get(0).setContainsGeneralMap(overviewFile);
        
        int counter = 0;
        boolean videoExists = false;
        boolean gralImage = false;
        
        //going through all trips and each of its files, determining whether video or image and sending them to ffmpeg
        for(int i = 0; i < tripList.size() + 1; i++) {
        	System.out.println("-------------------------");
        	System.out.println(tripList.get(i % tripList.size()).getGps());
        	System.out.println(tripList.get(i % tripList.size()).getTime());
        	
        	if(tripList.get(i % tripList.size()).getContainsGeneralMap() != null && !gralImage) {
        		gralImage = true;
        		String path = tripList.get(i % tripList.size()).getContainsGeneralMap().getAbsolutePath();
    			String vidName = "absVid" + counter + ".mkv";
    			String command = "cmd /c start /wait ffmpeg -loop 1 -i " + path + " -vcodec mpeg4 -r 30 -t 3 -an -vf scale=" + scale + ",setsar=1:1 -video_track_timescale 90000 " + destDir + vidName;
    			videoExists = true;
    			File dir = new File(desktopDir + "ffmpeg\\bin");
        		Process process = Runtime.getRuntime().exec(command, null, dir);
        		try {
					process.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        		counter++;
        	}
        	
        	//going though current trip files, and making a video for each of the files
        	for(int j = 0; j < tripList.get(i % tripList.size()).files.size(); j++) {
        		System.out.println("file" + j + ": " + tripList.get(i % tripList.size()).files.elementAt(j).getAbsolutePath());
        		System.out.println("type: "+ ImageTools.getType(tripList.get(i % tripList.size()).files.elementAt(j).getAbsolutePath()));
        		String type = ImageTools.getType(tripList.get(i % tripList.size()).files.elementAt(j).getAbsolutePath());
        		
        		try {
        			String path = tripList.get(i % tripList.size()).files.elementAt(j).getAbsolutePath();
        			String vidName = "absVid" + counter + ".mkv";
        			String command = "";
            		if(type == "image") {
            			command = "cmd /c start /wait ffmpeg -loop 1 -i " + path + " -vcodec mpeg4 -r 30 -t 3 -an -vf scale=" + scale + ",setsar=1:1 -video_track_timescale 90000 " + destDir + vidName;
            			videoExists = true;
            		}
            		if(type == "video") {
            			command = "cmd /c start /wait ffmpeg -i " + path +" -vf scale=" + scale + ",setsar=1:1 -video_track_timescale 90000 -r 30 " + destDir + vidName + " -hide_banner";
            			videoExists = true;
            			System.out.println("video command: " + command);
            		}
            		if(type == "unknown") continue;
            		
            		File dir = new File(desktopDir + "ffmpeg\\bin");
            		Process process = Runtime.getRuntime().exec(command, null, dir);
					process.waitFor();
				} catch (Exception e) {
					e.printStackTrace();
				}
        		counter++;
        	}
        }
        
        //collecting each video piece and making them one full video in ffmpeg
        if(videoExists) {
	        File dir = new File(desktopDir + "ffmpeg\\bin");
	        String command = "cmd /c start /wait ffmpeg";
	        for (int i = 0; i < counter; i++) {
	        	command += " -i " + destDir + "absVid" + i +".mkv"; 
	        }
	        command += " -filter_complex \"";
	        for(int i = 0; i < counter; i++) {
	        	command += "[" + i + ":v:0] ";
	        }
	        command += "concat=n=" + (counter) + ":v=1 [v]\" -map \"[v]\" " + destDir + "RESULT.mkv";
	        System.out.println("command" + command);
	        
	        Process process = Runtime.getRuntime().exec(command, null, dir);
	        try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        
	}
	
	/**
	 * function that executes exiftool in cmd and looks for gps and date info
	 * @param absPath
	 * @param localPath
	 * @throws IOException
	 */
	public static void execExiftool(String absPath, String localPath) throws IOException {
		String s = null;
		boolean hasGPSInfo = false;
		boolean hasDateInfo = false;
		Process p = Runtime.getRuntime().exec(desktopDir + "exiftool -c \"%d deg %d' %.2f\"\\\" "+ absPath);
		
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String[] gpsParts = null;
		String[] timeParts = null;
		
        while ((s = stdInput.readLine()) != null) {
        	if(s.contains("GPS Position") && s.contains("deg") && s.contains("'") && s.contains('"' + "")) {
        		hasGPSInfo = true;
        		gpsParts = s.split(" : ");
        	}
        	if(s.contains("Create Date")) {
        		hasDateInfo = true;
        		timeParts = s.split(" : ");
        	}
        }
        if(!hasGPSInfo) {
        	System.out.println("Missing GPS info. Cannot include file.");
        }
        if(!hasDateInfo) {
        	System.out.println("Missing Date info. Cannot include file.");
        }
        if(hasGPSInfo && hasDateInfo) processLine(gpsParts[1], timeParts[1], localPath);
	}
	
	/**
	 * function that creates a Trip object with the gps and date info,
	 * then fills a trip map with the gps location as the key
	 * @param gps
	 * @param timeStamp
	 * @param localPath
	 */
	public static void processLine(String gps, String timeStamp, String localPath) {
		if(!timeStamp.contains(".")) timeStamp += ".00";
		
		String[] pos = gps.split(", ");
		String[] latitude = pos[0].split(" ");
		String[] longitude = pos[1].split(" ");
		
		int[] indexes = {0, 2, 3};
		float[] latInfo = new float[3];
		float[] lonInfo = new float[3];
		
		//getting and parsing gps location to decimal degrees
		for(int i = 0; i < 3; i++) {
			latitude[indexes[i]] = latitude[indexes[i]].replace("'", "");
			latitude[indexes[i]] = latitude[indexes[i]].replace('"' +"", "");
			longitude[indexes[i]] = longitude[indexes[i]].replace("'", "");
			longitude[indexes[i]] = longitude[indexes[i]].replace('"' +"", "");
			
			latInfo[i] = Float.parseFloat(latitude[indexes[i]]);
			lonInfo[i] = Float.parseFloat(longitude[indexes[i]]);
		}
		
		//creating DecCoordinate object to get the degrees conversion
		DecCoordinate xCoord = new DecCoordinate(latInfo[0], latInfo[1], latInfo[2], latitude[4]);
		DecCoordinate yCoord = new DecCoordinate(lonInfo[0], lonInfo[1], lonInfo[2], longitude[4]);
		
		String keyString = xCoord.getValue() + "," + yCoord.getValue();
		System.out.println(keyString);
		
		//checking if map value already exists to add a new trip or just add a file to its trip
		if(trips.get(keyString) == null) {
			Trip temp = new Trip(keyString, timeStamp);
			Image image = null;
			try {
				String link = "http://localhost/staticmaplite-master/staticmap.php?center=" + keyString + "&zoom=13&size=" + size + "&maptype=mapnik&markers="+keyString+",bullseye";
			    URL url = new URL(link);
			    image = ImageIO.read(url);
			    BufferedImage bi = (BufferedImage)image;
			    File f = new File(destDir + "map"+ keyString.replace(".", "#") +".png");
			    temp.files.add(f);
			    ImageIO.write(bi, "png", f);
			} catch (IOException e) {
			}
			File tempFile = new File(localPath);
			temp.files.add(tempFile);
			trips.put(keyString, temp);
		}else {
			File tempFile = new File(localPath);
			trips.get(keyString).files.add(tempFile);
		}

	}
	
	
	
}
