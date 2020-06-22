package UP.edu.demo;

public class DecCoordinate {
	/**
	 * DecCoordinate class. Stores latitude and longitude info and also calculates its decimal degree equivalent
	 */
	private String coord = "";
	private float value = 0;
	
	/**
	 * class constructor
	 * @param deg
	 * @param min
	 * @param sec
	 * @param ref
	 */
	public DecCoordinate(float deg, float min, float sec, String ref) {
		this.value += (int)deg;
		this.value += getDecPart(min, sec);
		if(!getSign(ref)) this.value = -1 * this.value;
		
		this.value = Math.round(this.value*10000.0)/(float)10000.0;
	}
	
	/**
	 * returns the decimal part for the degrees
	 * @param min
	 * @param sec
	 * @return
	 */
	public float getDecPart(float min, float sec) {
		float result = sec / (float)60.0;
		float dec = (min + result) / (float)60.0;
		return dec;
	}
	
	/**
	 * returns whether a coordinate is positive or negative depending on its reference
	 * false if negative, true if positive
	 * @param ref
	 * @return
	 */
	public boolean getSign(String ref) {
		if(ref.contentEquals("W") || ref.contentEquals("S")) {
			return false;
		}
		return true;
	}

	/**
	 * get a coordinates float value
	 * @return value
	 */
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	/**
	 * get a coordinates string value
	 * @return
	 */
	public String getCoord() {
		return coord;
	}

	public void setCoord(String coord) {
		this.coord = coord;
	}
	
}
