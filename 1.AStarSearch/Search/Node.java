
public class Node {
	private int parentIndex;
	private int cityIndex;
	
	public Node(int cityIndex, int parentIndex) {
		this.cityIndex = cityIndex;
		this.parentIndex = parentIndex;
	}
	
	public int getParent() {
		return parentIndex;
	}
	
	public int getCityIndex() {
		return cityIndex;
	}
}
