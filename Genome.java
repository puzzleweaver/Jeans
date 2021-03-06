import java.util.ArrayList;
import java.util.StringTokenizer;

public class Genome {

	public static double range = 1.0, range1 = 0.1;
	
	public ArrayList<Connection> cons = new ArrayList<>();
	public ArrayList<Node> nodes = new ArrayList<>();
	public int inNum, outNum;
	
	public int species;

	public Genome(int in, int out) {

		this.inNum = in;
		this.outNum = out;
		for(int i = 0; i < inNum; i++) {
			nodes.add(new Node(true));
		}
		for(int i = 0; i < outNum; i++) {
			nodes.add(new Node(true));
		}
		
	}

	// g reproduces asexually
	public Genome(Genome g) {

		cons = new ArrayList<>();
		for(int i = 0; i < g.cons.size(); i++) {
			cons.add(g.cons.get(i).get());
		}
		nodes = new ArrayList<>();
		for(int i = 0; i < g.nodes.size(); i++) {
			nodes.add(g.nodes.get(i).getNode());
		}
		inNum = g.inNum;
		outNum = g.outNum;

		mutate();
	
	}
	
	// a and b crossover
	public Genome(Genome a, Genome b) {

		// switch a and b sometimes so that order doesn't matter
		if(Population.r.nextBoolean()) {
			Genome tmp = a;
			a = b;
			b = tmp;
		}

		// copy a's topology
		cons = new ArrayList<>();
		for(int i = 0; i < a.cons.size(); i++) {
			cons.add(a.cons.get(i).get());
		}
		nodes = new ArrayList<>();
		boolean aHas[] = new boolean[Node.nodeGIN];
		for(int i = 0; i < a.nodes.size(); i++) {
			nodes.add(a.nodes.get(i).getNode());
			aHas[nodes.get(nodes.size()-1).gin] = true;
		}
		inNum = a.inNum;
		outNum = a.outNum;

		int shared[] = new int[b.nodes.size()];
		// copy b's nodes that aren't part of a's
		for(int i = 0; i < b.nodes.size(); i++) {
			if(!aHas[b.nodes.get(i).gin]) {
				nodes.add(b.nodes.get(i).getNode());
				shared[i] = nodes.size()-1;
			}
		}

		// crossover
		for(int i = 0; i < b.cons.size(); i++) {
			boolean added = false;
			if(shared[b.cons.get(i).a] != 0 || shared[b.cons.get(i).b] != 0) {
				// when connections involve nodes unique to b, make new connections for a
				int conA = b.cons.get(i).a, conB = b.cons.get(i).b;
				if(shared[conA] != 0)
					conA = shared[conA];
				if(shared[conB] != 0)
					conB = shared[conB];
				cons.add(new Connection(conA, conB, b.cons.get(i).weight, b.cons.get(i).gin));
			}else {
				for(int j = 0; j < cons.size(); j++) {
					if(cons.get(j).gin == b.cons.get(i).gin) {
						if(Population.r.nextBoolean()) {
							cons.get(j).weight = b.cons.get(i).weight;
						}
						added = true;
					}
				}
				if(!added)
					cons.add(b.cons.get(i).get());
			}
		}
		
		// reset node connections
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).cons = new ArrayList<>();
		}
		// set up node connections
		for(int i = 0; i < cons.size(); i++) {
			nodes.get(cons.get(i).a).cons.add(i);
		}

		mutate();
	}
	
	// get text to save
	public String toString() {
		String out = cons.size() + " " + nodes.size() + " " + inNum + " " + outNum + "\n";
		for(int i = 0; i < cons.size(); i++) {
			out += cons.get(i).a + " " + cons.get(i).b + " " + cons.get(i).weight + " " + cons.get(i).gin + "\n";
		}
		for(int i = 0; i < nodes.size(); i++) {
			out += nodes.get(i).gin + " " + nodes.get(i).cons.size() + " ";
			for(int j = 0; j < nodes.get(i).cons.size(); j++) {
				out += nodes.get(i).cons.get(j) + " ";
			}
		}
		return out;
	}
	// load from text
	public Genome(String s) {
		StringTokenizer st = new StringTokenizer(s);
		int conNum = Integer.parseInt(st.nextToken()), nodeNum = Integer.parseInt(st.nextToken());
		inNum = Integer.parseInt(st.nextToken());
		outNum = Integer.parseInt(st.nextToken());
		for(int i = 0; i < conNum; i++) {
			cons.add(new Connection(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
					Double.parseDouble(st.nextToken()), Integer.parseInt(st.nextToken())));
		}
		Node n;
		for(int i = 0; i < nodeNum; i++) {
			n = new Node(false);
			n.gin = Integer.parseInt(st.nextToken());
			conNum = Integer.parseInt(st.nextToken());
			for(int j = 0; j < conNum; j++) {
				n.cons.add(Integer.parseInt(st.nextToken()));
			}
			nodes.add(n);
		}
	}
	
	public void mutate() {
		// add node
		if(Math.random() < 0.03) {
			if(cons.size() != 0) {
				int id = Population.r.nextInt(cons.size());
				nodes.add(new Node(true));
				cons.get(id).enabled = false;
				cons.add(new Connection(cons.get(id).a, nodes.size()-1, Math.random()*2*range-range));
				nodes.get(cons.get(id).a).cons.add(cons.size()-1);
				cons.add(new Connection(nodes.size()-1, cons.get(id).b, Math.random()*2*range-range));
				nodes.get(cons.get(cons.size()-1).a).cons.add(cons.size()-1);
			}
		}
		// add connection
		if(Math.random() < 0.05) {
			int a = Population.r.nextInt(nodes.size()), b = Population.r.nextInt(nodes.size());
			boolean unique = true;
			for(int i = 0; i < cons.size(); i++) {
				if(a == cons.get(i).a && b == cons.get(i).b) {
					unique = false;
					break;
				}
			}
			if(unique) {
				cons.add(new Connection(a, b, Math.random()*2*range-range));
				nodes.get(a).cons.add(cons.size()-1);
			}
		}
		// mutate weights
		if(Math.random() < 0.9) {
			// change all weights slightly
			for(int i = 0; i < cons.size(); i++) {
				cons.get(i).weight *= Math.random()*range1+1.0-range1*0.5;
			}
		} else {
			// randomize all weights
			for(int i = 0; i < cons.size(); i++) {
				cons.get(i).weight = Math.random()*2*range-range;
			}
		}
	}

	public ArrayList<Integer> toUse;
	public boolean used[];
	
	public void propagate(int id) {
		Node n = nodes.get(id);
		for(int i = 0; i < n.cons.size(); i++) {
			if(cons.get(n.cons.get(i)).enabled) {
				if(!used[cons.get(n.cons.get(i)).b]) {
					toUse.add(cons.get(n.cons.get(i)).b);
					used[cons.get(n.cons.get(i)).b] = true;
				}
				nodes.get(cons.get(n.cons.get(i)).b).val += n.get()*cons.get(n.cons.get(i)).weight;
			}
		}
	}
	
	public void iterate(double[] in, int iters) {
		
		toUse = new ArrayList<>();
		used = new boolean[nodes.size()];
		
		// add inputs to 
		for(int i = 0; i < inNum; i++) {
			nodes.get(i).val = in[i];
			toUse.add(i);
			used[i] = true;
		}
		
		for(int iter = 0; iter < iters; iter++) {
			
			int size = toUse.size();
			for(int i = 0; i < size; i++) {
				propagate(toUse.get(0));
				toUse.remove(0);
			}
			used = new boolean[nodes.size()];
			
		}

	}

	public double[] getOut() {
		double[] out = new double[outNum];
		for(int i = 0; i < outNum; i++) {
			out[i] = nodes.get(i+inNum).get();
		}
		return out;
	}

	public void clear() {
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).val = 0;
		}
	}

}
