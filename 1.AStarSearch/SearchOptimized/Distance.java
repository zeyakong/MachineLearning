import java.util.HashMap;
import java.util.Map;

public class Distance {
	private Map<String , Double> distanceMap;
	
	public Distance() {
		distanceMap = new HashMap< String , Double >();
	}
	
	public void setDistance(String city1,String city2,double distance) {
		String coupleCity = city1+city2;
		distanceMap.put(coupleCity, distance);
	}
	
	public double getDistance(String city1,String city2) {
		if(city1.equals(city2))return 0;
		if(distanceMap.containsKey(city1+city2)) {
			return (Double) distanceMap.get(city1+city2);
		}else if(distanceMap.containsKey(city2+city1)) {
			return (Double) distanceMap.get(city2+city1);
		}else {
			return (double) -1;
		}
	}
}
