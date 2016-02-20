import java.util.ArrayList;

public class Node {

	public static int nodeGIN = 0;
	
	public ArrayList<Integer> cons = new ArrayList<>();
	public double val;
	public boolean used;
	public int gin;
	
	public Node(boolean stahpid) {
		if(stahpid) {
			gin = nodeGIN;
			nodeGIN++;
		}
	}
	
	public double get() {
		return 1.0/(1.0+Math.exp(-5.0*val));
	}
	public Node getNode() {
		Node n = new Node(false);
		ArrayList<Integer> nt = new ArrayList<>();
		for(int i = 0; i < cons.size(); i++) {
			nt.add(0+cons.get(i));
		}
		n.cons = nt;
		n.val = val;
		n.gin = gin;
		return n;
	}
	
}
