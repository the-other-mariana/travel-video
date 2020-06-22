package UP.edu.demo;

import java.io.File;
import java.util.Vector;

public class Trip {
	/**
	 * Trip class. Stores gps, date and other info about a location. It stores also a vector of files for each location
	 */
	private String gps;
	float lat;
	float lon;
	private String time;
	private File containsGeneralMap;
	Vector<File> files = new Vector<File>();
	
	//class constructor
	public Trip(String gps, String time) {
		setGps(gps);
		setTime(time);
		setContainsGeneralMap(null);
		String[] coords = gps.split(",");
		lat = Float.parseFloat(coords[0]);
		lon = Float.parseFloat(coords[1]);
	}
	
	/**
	 * getGps function
	 * @return gps location in string
	 */
	public String getGps() {
		return gps;
	}

	public void setGps(String gps) {
		this.gps = gps;
	}
	
	/**
	 * getTime function
	 * @return time in string
	 */
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	/**
	 * getContainsGeneralMap
	 * @return constainsGeneralMap in a file with overview map
	 */
	public File getContainsGeneralMap() {
		return containsGeneralMap;
	}

	public void setContainsGeneralMap(File containsGeneralMap) {
		this.containsGeneralMap = containsGeneralMap;
	}
}
