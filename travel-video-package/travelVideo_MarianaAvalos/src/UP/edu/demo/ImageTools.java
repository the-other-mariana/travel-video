package UP.edu.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class ImageTools {
	
	/**
	 * ImageTools class. Abstract class that has calculations for a general image
	 */
	
	public ImageTools() {
		
	}
	
	/**
	 * returns if a file is an image or a video
	 * @param path
	 * @return type
	 * @throws IOException
	 */
	public static String getType(String path) throws IOException {
		String mt = Files.probeContentType(Paths.get(path));
		if(mt != null) {
			if(mt.startsWith("image")) return "image";
			if(mt.startsWith("video")) return "video";
			return "unknown";
		}
		return "unknown";
	}
	
	/**
	 * calculates zoom value between two given trips.
	 * zoom value depends on circular distance, so it needs the Earth radius and
	 * the delta of latitude and longitude (distance) so that the length of the
	 * chord between them is returned
	 * @param t1
	 * @param t2
	 * @return zoom value
	 */
	public static int calculateZoom(Trip t1, Trip t2) {
		int tolerance = 1;
		float earthRadius = (float)6373.0;
		float dlat = (float)Math.toRadians(t2.lat) - (float)Math.toRadians(t1.lat);
		float dlon = (float)Math.toRadians(t2.lon) - (float)Math.toRadians(t1.lon);
		
		double a = (Math.sin(dlat/2.0)) * (Math.sin(dlat/2.0)) + Math.cos(Math.toRadians(t1.lat)) * Math.cos(Math.toRadians(t2.lat)) * (Math.sin(dlon/2.0))*(Math.sin(dlon/2.0));
		float af = (float)a;
		float c = 2 * (float)Math.atan2( Math.sqrt(af), Math.sqrt(1-af));
		float distance = earthRadius * c;
		System.out.println("distance: " + distance);
		
		if(distance >= 0 && distance < 2.0) return 15 - tolerance;
		if(distance >= 2.0 && distance < 20.0) return 12 - tolerance;
		if(distance >= 20.0 && distance < 200.0) return 9 - tolerance;
		if(distance >= 200.0 && distance < 2000.0) return 6 - tolerance;
		if(distance >= 2000.0) return 3 - tolerance;
		
		return 18;
	}
	
	public static float averageCenter(float t1, float t2) {
		float value = (float)((t1 + t2) / 2.0);
		value = Math.round(value*10000.0)/(float)10000.0;
		return value;
	}
}
