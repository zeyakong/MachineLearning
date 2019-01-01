import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AStarSearch {
	private int nodeNumber = 1;
	private Distance ds = new Distance();
	private List<City> citydata = new ArrayList<City>();
	private Map<String, Integer> table = new HashMap<String, Integer>();// Give every city a index to find
	private int index = 0;// The index of the city

	private int flag = 1;// to control the tpye of file.

	private void loadData(File file) {
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("The file path can not be found!");
			System.exit(0);
		}
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		try {
			System.out.print("Reading city data...");
			line = br.readLine();
			while (line != null) {
				saveLine(line);
				line = br.readLine();
			}
			System.out.println("Done.\n");
		} catch (IOException e) {
			System.out.println("The file can not be recognized!");
			return;
		} finally {
			// Close all the stream resources
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					System.out.println("Stream close error!");
				}
			if (isr != null)
				try {
					isr.close();
				} catch (IOException e) {
					System.out.println("Stream close error!");
				}
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("Stream close error!");
				}
		}
	}

	/*
	 * The citys' file has two parts One is city's information Two is distance
	 * between cities This function divided the file into two parts accroding to the
	 * value of flag.
	 */
	private void saveLine(String str) {
		if (str.startsWith("# distances"))
			flag = 2;

		if (!str.startsWith("#") && flag == 1) {
			String[] temp = str.split(" ");
			double latitude = Double.valueOf(temp[temp.length - 2]);
			double longitude = Double.valueOf(temp[temp.length - 1]);
			String cityName = "";
			for (int i = 0; i < temp.length - 2; i++) {
				if (i < temp.length - 3)
					cityName += temp[i] + " ";
				else
					cityName += temp[i];
			}
			citydata.add(new City(cityName, latitude, longitude));
			table.put(cityName, index);
			index++;
		}

		if (!str.startsWith("#") && flag == 2) {
			String[] temp2 = str.split(": ");
			double distance = Double.valueOf(temp2[temp2.length - 1]);
			String[] temp3 = temp2[0].split(", ");
			ds.setDistance(temp3[0], temp3[1], distance);
			// Use the index to set the adjacent city
			citydata.get(table.get(temp3[0])).setAdjacent(table.get(temp3[1]));
			citydata.get(table.get(temp3[1])).setAdjacent(table.get(temp3[0]));
		}
	}

	/*
	 * A* alglothm 1. Put start into closed set(parent node is start) 2. Put the
	 * adjacent node of parent into open set 3. Choose the node of minnum f(n) and
	 * put it into close (as parent) 4. Choose the minnum G(n), put it into close
	 * (as parent) 5. Loop step.2 4 6. If we found the destination in the closed
	 * set, it means finding successfully
	 */

	private void AStar(int start, int end, Scanner scanner) {
		
		System.out.println("\nSearch for path from " + citydata.get(start).getName() + " to "
				+ citydata.get(end).getName() + "...\n" + "Target found: " + citydata.get(end).getName() + " "
				+ citydata.get(end).getLatitude() + " " + citydata.get(end).getLongitude());

		// map < <cityIndex , parent >, value of g(n) > not work
//		Map<Integer[], Double> openSet = new HashMap<Integer[], Double>();
		
		/*I create a tree node to remember each node's parent*/
		Map<Node , Double>openSet = new HashMap<Node, Double>();
		Map<Integer, Double> closedSet = new HashMap<Integer, Double>();
		Map<Integer, Integer> path = new HashMap<Integer, Integer>();

		double endLatitude  = citydata.get(end).getLatitude();
		double endLongitude = citydata.get(end).getLongitude();

		int parentNode = start;
		closedSet.put(start, (double) 0);
		Node minNode=null;
		while (!closedSet.containsKey(end) ) {

			// Than put the adjacent nodes of choosed one into open set
			for (Iterator<Integer> i = citydata.get(parentNode).getAdjacents().iterator(); i.hasNext();) {
				int tempIndex = i.next();
				/*Optimized to fix the loop*/
				if(closedSet.containsKey(tempIndex)) {
					continue;
				}
				double disParentToTemp = ds.getDistance(citydata.get(tempIndex).getName(), citydata.get(parentNode).getName());
				double g = disParentToTemp + closedSet.get(parentNode);
				/*Very Optimize*/
				double disParentToEnd = ds.getDistance(citydata.get(parentNode).getName(), citydata.get(end).getName());
				if(disParentToEnd>0&&disParentToTemp>disParentToEnd) {
					continue;
				}
//				System.out.println(
//						"Now find adjacent node is :" + tempIndex + ",its g(n)= " + g + "...and put it into open");
				nodeNumber++;
				openSet.put(new Node(tempIndex,parentNode), g);
			}

			// Find the minmun value of open set.
			int minCity = -1;
			double minF = 100000000;
			
			// Collection<Double> c = openSet.values();
			// Object[] obj = c.toArray();
			// Arrays.sort(obj);
			for (Iterator<Node> i = openSet.keySet().iterator(); i.hasNext();) {
				Node key = i.next();
				/* Find the minmum f(n), f(n)=g(n)+h(n) */
				double f = openSet.get(key) + getHaversineDistance(citydata.get(key.getCityIndex()).getLatitude(),
						citydata.get(key.getCityIndex()).getLongitude(), endLatitude, endLongitude);
				if (f < minF) {
					minCity = key.getCityIndex();
					minF = f;
					minNode = key;
				}
			}
//			System.out.println("Now we found the minmun f(n) in open is:" + minCity + ",f(n)=" + minF);
//			System.out.println("Now choose " + minCity + " and put it into closed set,g(n) is :" + openSet.get(minNode));
			closedSet.put(minCity, openSet.get(minNode));// Put the choosed one into the closed set

			openSet.remove(minNode);
//			System.out.println("Remove " + citydata.get(minNode.getCityIndex()).getName() + " from openSet and put " + citydata.get(minNode.getParent()).getName()
//							   + "--->" + minCity + citydata.get(minNode.getCityIndex()).getName() + " in path map...\n\n");
			if(!path.containsKey(minNode.getCityIndex()))path.put(minNode.getCityIndex(), minNode.getParent());
			parentNode = minCity;
		}
		
		if (closedSet.containsKey(end)) {
//			System.out.println("We find the index " + end + " called " + citydata.get(end).getName());
			List<Integer> pathList = new ArrayList<Integer>();
			pathList.add(end);
//
//			for (Iterator<Integer> i = path.keySet().iterator(); i.hasNext();) {
//				int index = (int) i.next();
//				System.out.println("Index " + index + " is :" + path.get(index));
//			}
			
            int newEnd = end;
			while (path.containsKey(newEnd)) {
				newEnd = path.get(newEnd);
				pathList.add(newEnd);
			}
			Collections.reverse(pathList);

			System.out.println("\nRount found: ");
			for (int i = 0; i < pathList.size(); i++) {
				System.out.print(citydata.get(pathList.get(i)).getName());
				if(i<pathList.size()-1)System.out.print(" -> ");
			}
			System.out.println("\nDistance: "+closedSet.get(end)+" miles");
			
		}else {
			System.out.println("Route can not be found.");
		}
		System.out.println("\nTotal nodes generated  : "+nodeNumber);
		System.out.println("Nodes left in open list: " + openSet.size());
		System.out.print("\n-----------------------------\n"
				+ "Enter 0 to quit, or any other keys to search again: ");
		String flag2="1";
		flag2 = scanner.nextLine();
		if(!flag2.equals("0")) {
			System.out.println("-----------------------------\n");
			nodeNumber = 1;
			int[] index = getInput(scanner);
			AStar(index[0], index[1],scanner);
		}else {
			System.out.println("\nGoodbye.");
			System.exit(0);
		}
	}

	private static double getHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
		double latd = Math.toRadians(lat1 - lat2);
		double lngd = Math.toRadians(lon1 - lon2);

		double a = Math.sin(latd / 2) * Math.sin(latd / 2) + Math.sin(lngd / 2) * Math.sin(lngd / 2)
				* Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return Math.round(3961 * c);
	}

	private int[] getInput(Scanner scanner) {
		System.out.print("Please enter name of start city (0 to quit):");
		int[] index = new int[2];
		String start = scanner.nextLine();
		
		while (!table.containsKey(start)) {
			if (start.equals("0")) {
				System.out.println("\nGoodbye.");
				System.exit(0);
			}
			System.out.println("The " + start + " not in data-set.");
			System.out.print("Please enter name of start city (0 to quit):");
			start = scanner.nextLine();
		}

		index[0] = table.get(start);

		System.out.print("Please enter name of end city (0 to quit)  :");
		String end = scanner.nextLine();

		while (!table.containsKey(end)) {
			if (end.equals("0")) {
				System.out.println("\nGoodbye.");
				System.exit(0);
			}
			System.out.println("The " + end + " not in data-set.");
			System.out.print("Please enter name of end city (0 to quit)  :");
			end = scanner.nextLine();
		}
		
		index[1] = table.get(end);
		return index;
	}

	public void run(String path) {
		Scanner scanner = new Scanner(System.in);
		loadData(new File(path));
		System.out.println("Number of cities: " + index);
		int[] index = getInput(scanner);
		AStar(index[0], index[1],scanner);
		scanner.close();
	}
}
