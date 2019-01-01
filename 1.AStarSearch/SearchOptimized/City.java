import java.util.ArrayList;
import java.util.List;

/*
 *Javabean city
 *Created by Zeya Kong
 *Sep.20
 */
public class City {
	private String name;
	private double latitude;
	private double longitude;
	private List<Integer> adjacent = new ArrayList<Integer>();
	
	public City(String name,double latitude , double longitude) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public List<Integer> getAdjacents() {
		return adjacent;
	}
	
	public void setAdjacent(int index) {
		if(!adjacent.contains(index))
		adjacent.add(index);
	}
}
